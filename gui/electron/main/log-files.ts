import {
  closeSync,
  fstatSync,
  mkdirSync,
  openSync,
  readdirSync,
  renameSync,
  statSync,
  unlinkSync,
  writeSync,
} from 'node:fs';
import { join } from 'node:path';

const LOG_TAG = 'slimevr-gui';
const LOG_SUFFIX = '.log';
const SEPARATOR = '_';
const FILE_SIZE_LIMIT = 12 * 1000 * 1000;
const MAX_FILE_COUNT = 8;

/** Files written by older versions of the GUI, moved out of the way once on startup. */
const LEGACY_LOG =
  /^(slimevr-gui\.\d+\.log|slimevr\.log|slimevr_\d{4}-\d{2}-\d{2}_\d{2}-\d{2}-\d{2}\.log)$/;
const LEGACY_FOLDER = 'legacy';

export interface LogFileInfo {
  name: string;
  path: string;
  size: number;
  modifiedMs: number;
}

const pad = (n: number) => String(n).padStart(2, '0');

function fileNameDate(date: Date) {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}_${pad(date.getHours())}-${pad(date.getMinutes())}-${pad(date.getSeconds())}`;
}

export function listLogFiles(directory: string): LogFileInfo[] {
  let names: string[];
  try {
    names = readdirSync(directory);
  } catch {
    return [];
  }

  return names
    .filter((name) => name.startsWith(LOG_TAG + SEPARATOR) && name.endsWith(LOG_SUFFIX))
    .flatMap((name) => {
      const path = join(directory, name);
      try {
        const stats = statSync(path);
        if (!stats.isFile()) return [];
        return [{ name, path, size: stats.size, modifiedMs: stats.mtimeMs }];
      } catch {
        return [];
      }
    })
    .sort((a, b) => b.modifiedMs - a.modifiedMs);
}

function tryDelete(path: string) {
  try {
    unlinkSync(path);
  } catch {
    // Still held open by another process on Windows, the next run tries again
  }
}

function moveLegacyLogs(directory: string) {
  let names: string[];
  try {
    names = readdirSync(directory).filter((name) => LEGACY_LOG.test(name));
  } catch {
    return;
  }
  if (names.length === 0) return;

  const archive = join(directory, LEGACY_FOLDER);
  mkdirSync(archive, { recursive: true });
  for (const name of names) {
    try {
      renameSync(join(directory, name), join(archive, name));
    } catch {
      // Windows refuses to move a file another program has open, the next start tries again
    }
  }
}

export function createLogFileWriter(directory: string) {
  const namePrefix = `${LOG_TAG}${SEPARATOR}${fileNameDate(new Date())}${SEPARATOR}`;
  let index = 0;
  let size = 0;

  const openNextFile = () => {
    mkdirSync(directory, { recursive: true });
    listLogFiles(directory)
      .slice(MAX_FILE_COUNT - 1)
      .forEach((file) => tryDelete(file.path));

    const fd = openSync(join(directory, `${namePrefix}${index++}${LOG_SUFFIX}`), 'a');
    size = fstatSync(fd).size;
    return fd;
  };

  mkdirSync(directory, { recursive: true });
  moveLegacyLogs(directory);
  let fd = openNextFile();

  return {
    write(line: string) {
      if (size >= FILE_SIZE_LIMIT) {
        closeSync(fd);
        fd = openNextFile();
      }
      size += writeSync(fd, line);
    },
    close() {
      closeSync(fd);
    },
  };
}
