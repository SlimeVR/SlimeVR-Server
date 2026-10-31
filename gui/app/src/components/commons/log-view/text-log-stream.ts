import { AnsiSpan, parseAnsi, stripAnsi } from '@/utils/ansi';
import { LogLineParser } from '@/utils/log-line-parsers';
import { LogViewLine, nameColor } from './log-line';

export interface TextLogLine extends LogViewLine {
  text: string;
}

export interface TextLogState {
  lines: TextLogLine[];
  // Raw tail after the last newline, still being written by the device
  pending: string;
  pendingTimeMs: number;
  nextId: number;
}

export const EMPTY_TEXT_LOG: TextLogState = {
  lines: [],
  pending: '',
  pendingTimeMs: 0,
  nextId: 0,
};

function dropChars(spans: AnsiSpan[], count: number) {
  let left = count;
  const kept: AnsiSpan[] = [];
  for (const span of spans) {
    if (left >= span.text.length) {
      left -= span.text.length;
      continue;
    }
    kept.push(left > 0 ? { ...span, text: span.text.slice(left) } : span);
    left = 0;
  }
  return kept;
}

function takeChars(spans: AnsiSpan[], count: number) {
  let left = count;
  const kept: AnsiSpan[] = [];
  for (const span of spans) {
    if (left <= 0) break;
    kept.push(
      span.text.length > left ? { ...span, text: span.text.slice(0, left) } : span
    );
    left -= span.text.length;
  }
  return kept;
}

// Dots or blanks before a line start are progress output, not a cut line
function lineStarts(text: string, parser: LogLineParser) {
  const starts = [0];
  const pattern = new RegExp(parser.lineStart.source, 'g');
  for (const match of text.matchAll(pattern)) {
    const at = match.index ?? 0;
    const before = text.slice(starts[starts.length - 1], at);
    if (at > 0 && !/^[.\s]*$/.test(before)) starts.push(at);
  }
  return starts;
}

function toTextLine(
  spans: AnsiSpan[],
  id: number,
  timeMs: number,
  parser: LogLineParser | null
): TextLogLine {
  const text = spans.map((span) => span.text).join('');
  const parsed = parser?.parse(text);
  const message = parsed?.message ?? text;
  const label = parsed?.label ?? null;
  return {
    id,
    timeMs,
    timeTitle: parsed?.timeTitle ?? null,
    level: parsed?.level ?? null,
    label,
    color: label ? nameColor(label) : null,
    message,
    spans: dropChars(spans, text.length - message.length),
    details: null,
    text,
  };
}

function parseTextLines(
  raw: string,
  firstId: number,
  timeMs: number,
  parser: LogLineParser | null
) {
  const spans = parseAnsi(raw);
  const text = spans.map((span) => span.text).join('');
  const starts = parser ? lineStarts(text, parser) : [0];
  return starts.map((start, i) => {
    const end = starts[i + 1] ?? text.length;
    const lineSpans = takeChars(dropChars(spans, start), end - start);
    return toTextLine(lineSpans, firstId + i, timeMs, parser);
  });
}

export function appendTextLog(
  state: TextLogState,
  chunk: string,
  nowMs: number,
  parser: LogLineParser | null,
  maxLines: number
): TextLogState {
  const parts = (state.pending + chunk).split('\n');
  const pending = parts.pop() ?? '';
  // The first line may have started in an earlier chunk
  const firstTimeMs = state.pending ? state.pendingTimeMs : nowMs;
  let { nextId } = state;
  const added = parts.flatMap((raw, i) => {
    const lines = parseTextLines(raw, nextId, i === 0 ? firstTimeMs : nowMs, parser);
    nextId += lines.length;
    return lines;
  });
  return {
    lines: [...state.lines, ...added].slice(-maxLines),
    pending,
    pendingTimeMs: parts.length === 0 ? firstTimeMs : nowMs,
    nextId,
  };
}

export function clearTextLog(state: TextLogState): TextLogState {
  return { ...EMPTY_TEXT_LOG, nextId: state.nextId };
}

export function textLogLines(
  state: TextLogState,
  parser: LogLineParser | null
): TextLogLine[] {
  if (!stripAnsi(state.pending)) return state.lines;
  return [
    ...state.lines,
    ...parseTextLines(state.pending, state.nextId, state.pendingTimeMs, parser),
  ];
}
