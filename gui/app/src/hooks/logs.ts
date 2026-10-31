import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { zipSync } from 'fflate';
import {
  LogEventT,
  LogFileChunkRequestT,
  LogFileChunkResponseT,
  LogLevel,
  LogSource,
  LogSourceInfoT,
  LogStreamRequestT,
  LogStreamResponseT,
  LogsRequestT,
  LogsResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { GuiLogEvent } from '@slimevr/gui-shared';
import { SERIES_COLORS } from '@/components/commons/charts/series-colors';
import { useWebsocketAPI } from './websocket-api';
import { useElectron } from './electron';

export interface LogRow {
  id: number;
  source: LogSource;
  timeMs: number;
  level: LogLevel;
  logger: string;
  message: string;
  stackTrace: string | null;
}

export interface LogFileRow {
  source: LogSource;
  name: string;
  size: number;
  modifiedMs: number;
  current: boolean;
}

/** Live rows kept in memory; older ones drop off and stay in the log files. */
export const LIVE_LOG_LIMIT = 5000;

const CHUNK_TIMEOUT_MS = 10_000;

let nextRowId = 0;

/** Fluent id suffix and zip folder of a source, e.g. `slimevr_server`. */
export function sourceKey(source: LogSource) {
  return LogSource[source].toLowerCase();
}

/** Colour of the dot that marks where a log line or file comes from. */
/** Colour marking a source, from the same palette as the telemetry graphs. */
export function sourceColor(source: LogSource) {
  return SERIES_COLORS[source % SERIES_COLORS.length];
}

function serverRow(event: LogEventT): LogRow {
  return {
    id: nextRowId++,
    source: event.source,
    timeMs: Number(event.timeMs),
    level: event.level,
    logger: event.logger?.toString() ?? '',
    message: event.message?.toString() ?? '',
    stackTrace: event.stackTrace?.toString() ?? null,
  };
}

function guiRow(event: GuiLogEvent): LogRow {
  return { ...event, id: nextRowId++ };
}

function isSortedFrom(rows: LogRow[], from: number) {
  for (let i = Math.max(1, from); i < rows.length; i++) {
    if (rows[i].timeMs < rows[i - 1].timeMs) return false;
  }
  return true;
}

/** `current` plus `incoming`, ordered by time, keeping the newest LIVE_LOG_LIMIT. */
function appendRows(current: LogRow[], incoming: LogRow[]) {
  let next = current.concat(incoming);
  if (!isSortedFrom(next, current.length)) next.sort((a, b) => a.timeMs - b.timeMs);
  if (next.length > LIVE_LOG_LIMIT) next = next.slice(next.length - LIVE_LOG_LIMIT);
  return next;
}

/** Two time ordered lists as one, keeping the newest LIVE_LOG_LIMIT. */
function mergeByTime(a: LogRow[], b: LogRow[]) {
  if (a.length === 0) return b;
  if (b.length === 0) return a;
  const merged: LogRow[] = [];
  let i = 0;
  let j = 0;
  while (i < a.length || j < b.length) {
    if (j >= b.length || (i < a.length && a[i].timeMs <= b[j].timeMs))
      merged.push(a[i++]);
    else merged.push(b[j++]);
  }
  return merged.length > LIVE_LOG_LIMIT
    ? merged.slice(merged.length - LIVE_LOG_LIMIT)
    : merged;
}

const GUI_SOURCE = new LogSourceInfoT(LogSource.GUI, true);

/**
 * Every log source available here and their files: the server's, plus the GUI's own on
 * desktop. `refresh` asks again, e.g. when the file list is shown.
 */
export function useLogsInfo() {
  const { sendRPCPacket, useRPCPacket, isConnected } = useWebsocketAPI();
  const electron = useElectron();
  const [server, setServer] = useState<{
    sources: LogSourceInfoT[];
    files: LogFileRow[];
  }>({ sources: [], files: [] });
  const [guiFiles, setGuiFiles] = useState<LogFileRow[]>([]);

  const refresh = useCallback(() => {
    if (isConnected) sendRPCPacket(RpcMessage.LogsRequest, new LogsRequestT());
    if (electron.isElectron) electron.api.guiLogFiles().then(setGuiFiles);
  }, [isConnected]);

  useEffect(() => refresh(), [refresh]);

  useRPCPacket(RpcMessage.LogsResponse, (res: LogsResponseT) =>
    setServer({
      sources: res.sources,
      files: res.files.map((file) => ({
        source: file.source,
        name: file.name?.toString() ?? '',
        size: Number(file.size),
        modifiedMs: Number(file.modifiedMs),
        current: file.current,
      })),
    })
  );

  const sources = useMemo(
    () => (electron.isElectron ? [...server.sources, GUI_SOURCE] : server.sources),
    [server.sources, electron.isElectron]
  );
  const files = useMemo(() => [...server.files, ...guiFiles], [server.files, guiFiles]);

  return { sources, files, refresh };
}

export function useLiveLogs(sources: LogSource[]) {
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const electron = useElectron();
  const [serverRows, setServerRows] = useState<LogRow[]>([]);
  const [guiRows, setGuiRows] = useState<LogRow[]>([]);

  const serverKey = sources
    .filter((source) => source !== LogSource.GUI)
    .sort()
    .join(',');
  const withGui = sources.includes(LogSource.GUI);

  useConnectedEffect(() => {
    if (serverKey === '') return;
    setServerRows([]);
    sendRPCPacket(
      RpcMessage.LogStreamRequest,
      new LogStreamRequestT(serverKey.split(',').map(Number))
    );
    // An empty source list stops the stream
    return () => {
      sendRPCPacket(RpcMessage.LogStreamRequest, new LogStreamRequestT([]));
    };
  }, [serverKey]);

  useRPCPacket(RpcMessage.LogStreamResponse, (res: LogStreamResponseT) =>
    setServerRows((current) => appendRows(current, res.events.map(serverRow)))
  );

  useEffect(() => {
    if (!withGui || !electron.isElectron) return;
    // A backlog replaces the list: a quick remount can deliver the previous subscription's too
    return electron.api.onGuiLogEvents((events, backlog) =>
      setGuiRows((current) => appendRows(backlog ? [] : current, events.map(guiRow)))
    );
  }, [withGui]);

  const rows = useMemo(() => mergeByTime(serverRows, guiRows), [serverRows, guiRows]);

  const clear = useCallback(() => {
    setServerRows([]);
    setGuiRows([]);
  }, []);

  return { rows, clear };
}

const chunkKey = (source: LogSource, name: string, offset: number) =>
  `${source}/${name}/${offset}`;

function concat(parts: Uint8Array[]) {
  const out = new Uint8Array(parts.reduce((size, part) => size + part.length, 0));
  let offset = 0;
  for (const part of parts) {
    out.set(part, offset);
    offset += part.length;
  }
  return out;
}

/** Reads a whole log file, from the server in chunks or from the desktop app. */
export function useLogFileReader() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const electron = useElectron();
  const pending = useRef(new Map<string, (res: LogFileChunkResponseT) => void>());

  useRPCPacket(RpcMessage.LogFileChunkResponse, (res: LogFileChunkResponseT) => {
    const key = chunkKey(res.source, res.name?.toString() ?? '', Number(res.offset));
    const resolve = pending.current.get(key);
    if (!resolve) return;
    pending.current.delete(key);
    resolve(res);
  });

  const requestChunk = (source: LogSource, name: string, offset: number) =>
    new Promise<LogFileChunkResponseT>((resolve, reject) => {
      const key = chunkKey(source, name, offset);
      const timer = setTimeout(() => {
        pending.current.delete(key);
        reject(new Error(`Timed out reading log file ${name}`));
      }, CHUNK_TIMEOUT_MS);
      pending.current.set(key, (res) => {
        clearTimeout(timer);
        resolve(res);
      });

      const req = new LogFileChunkRequestT();
      req.source = source;
      req.name = name;
      req.offset = BigInt(offset);
      sendRPCPacket(RpcMessage.LogFileChunkRequest, req);
    });

  return useCallback(
    async (file: LogFileRow): Promise<Uint8Array<ArrayBuffer>> => {
      if (file.source === LogSource.GUI) {
        const data = electron.isElectron
          ? await electron.api.readGuiLogFile(file.name)
          : null;
        if (!data) throw new Error(`Log file ${file.name} is gone`);
        return new Uint8Array(data);
      }

      const parts: Uint8Array[] = [];
      let offset = 0;
      for (;;) {
        const res = await requestChunk(file.source, file.name, offset);
        const data = Uint8Array.from(res.data);
        parts.push(data);
        offset += data.length;
        if (res.eof) break;
      }
      return concat(parts);
    },
    [electron.isElectron]
  );
}

const LEVEL_NAMES: Record<string, LogLevel> = {
  TRACE: LogLevel.TRACE,
  DEBUG: LogLevel.DEBUG,
  INFO: LogLevel.INFO,
  NONE: LogLevel.INFO,
  WARN: LogLevel.WARN,
  ERROR: LogLevel.ERROR,
  FATAL: LogLevel.FATAL,
};

const SERVER_LINE =
  /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})\.(\d{3})\d* (\w+) \[.*?\] (.*?) : (.*)$/;

function parseServerLine(line: string, source: LogSource): LogRow | null {
  const match = SERVER_LINE.exec(line);
  if (!match) return null;
  const [, year, month, day, hours, minutes, seconds, millis, level, logger, message] =
    match;
  return {
    id: nextRowId++,
    source,
    timeMs: new Date(
      Number(year),
      Number(month) - 1,
      Number(day),
      Number(hours),
      Number(minutes),
      Number(seconds),
      Number(millis)
    ).getTime(),
    level: LEVEL_NAMES[level] ?? LogLevel.INFO,
    logger,
    message,
    stackTrace: null,
  };
}

// Driver: `[2026-10-15 23:09:49.011753034+0000] [INFO] message`
// Bindings provider: `[2026-10-07 03:56:11] [INFO] message`
const DRIVER_LINE =
  /^\[(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})(?:\.(\d+))?(?:([+-])(\d{2})(\d{2}))?\] \[([A-Z]+)\] (.*)$/;

function parseDriverLine(line: string, source: LogSource): LogRow | null {
  const match = DRIVER_LINE.exec(line);
  if (!match) return null;
  const [
    ,
    year,
    month,
    day,
    hours,
    minutes,
    seconds,
    fraction = '',
    sign,
    offsetHours,
    offsetMinutes,
    level,
    message,
  ] = match;
  const parts = [
    Number(year),
    Number(month) - 1,
    Number(day),
    Number(hours),
    Number(minutes),
    Number(seconds),
    Number(fraction.padEnd(3, '0').slice(0, 3)),
  ] as const;
  const timeMs = sign
    ? Date.UTC(...parts) -
      (sign === '-' ? -1 : 1) *
        (Number(offsetHours) * 60 + Number(offsetMinutes)) *
        60_000
    : new Date(...parts).getTime();
  return {
    id: nextRowId++,
    source,
    timeMs,
    level: LEVEL_NAMES[level] ?? LogLevel.INFO,
    logger: '',
    message,
    stackTrace: null,
  };
}

const PINO_LEVELS: [number, LogLevel][] = [
  [60, LogLevel.FATAL],
  [50, LogLevel.ERROR],
  [40, LogLevel.WARN],
  [30, LogLevel.INFO],
  [20, LogLevel.DEBUG],
];

// pino JSON lines, written by the desktop app
function parsePinoLine(line: string, source: LogSource): LogRow | null {
  if (!line.startsWith('{')) return null;
  let entry: Record<string, unknown>;
  try {
    entry = JSON.parse(line);
  } catch {
    return null;
  }
  const level = typeof entry.level === 'number' ? entry.level : 30;
  const err = entry.err as { stack?: string } | undefined;
  return {
    id: nextRowId++,
    source,
    timeMs: typeof entry.time === 'number' ? entry.time : 0,
    level: PINO_LEVELS.find(([min]) => level >= min)?.[1] ?? LogLevel.TRACE,
    logger: typeof entry.source === 'string' ? entry.source : 'main',
    message: typeof entry.msg === 'string' ? entry.msg : '',
    stackTrace: err?.stack ?? null,
  };
}

/**
 * Turns a log file into rows. Lines that don't start an entry (stack traces,
 * multi-line messages) are attached to the entry before them; files in a
 * format we don't know show each line as is.
 */
export function parseLogFile(data: Uint8Array, file: LogFileRow): LogRow[] {
  const rows: LogRow[] = [];
  for (const raw of new TextDecoder().decode(data).split('\n')) {
    const line = raw.replace(/\r$/, '');
    if (!line) continue;

    const row =
      parsePinoLine(line, file.source) ??
      parseServerLine(line, file.source) ??
      parseDriverLine(line, file.source);
    if (row) {
      rows.push(row);
      continue;
    }

    const previous = rows[rows.length - 1];
    if (previous) {
      previous.stackTrace = previous.stackTrace
        ? `${previous.stackTrace}\n${line}`
        : line;
      continue;
    }

    rows.push({
      id: nextRowId++,
      source: file.source,
      timeMs: file.modifiedMs,
      level: LogLevel.INFO,
      logger: '',
      message: line,
      stackTrace: null,
    });
  }
  return rows;
}

// Server and GUI files: `<tag>_<yyyy-MM-dd>_<HH-mm-ss>_<index>.log`
const SESSION_FILE = /_(\d{4})-(\d{2})-(\d{2})_(\d{2})-(\d{2})-(\d{2})_(\d+)\.log$/;

/**
 * When the app run that wrote this file started, and which file of that run it
 * is (0 for the first). Null for names in another format.
 */
export function logFileSession(name: string) {
  const match = SESSION_FILE.exec(name);
  if (!match) return null;
  const [, year, month, day, hours, minutes, seconds, index] = match;
  return {
    startMs: new Date(
      Number(year),
      Number(month) - 1,
      Number(day),
      Number(hours),
      Number(minutes),
      Number(seconds)
    ).getTime(),
    index: Number(index),
  };
}

export async function zipLogFiles(
  files: LogFileRow[],
  read: (file: LogFileRow) => Promise<Uint8Array>
) {
  const entries: Record<string, Uint8Array> = {};
  for (const file of files) {
    entries[`${sourceKey(file.source)}/${file.name}`] = await read(file);
  }
  // fflate allocates a plain ArrayBuffer, never a shared one
  const zip = zipSync(entries) as Uint8Array<ArrayBuffer>;
  return new Blob([zip], { type: 'application/zip' });
}

const pad = (n: number, size = 2) => String(n).padStart(size, '0');

export function formatLogTime(timeMs: number) {
  const date = new Date(timeMs);
  return `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}.${pad(date.getMilliseconds(), 3)}`;
}

export function rowsToText(rows: LogRow[], sourceLabel: (source: LogSource) => string) {
  return rows
    .map((row) => {
      const line = `${formatLogTime(row.timeMs)} ${LogLevel[row.level]} [${sourceLabel(row.source)}] [${row.logger}] ${row.message}`;
      return row.stackTrace ? `${line}\n${row.stackTrace}` : line;
    })
    .join('\n');
}
