import { isTauri } from '@tauri-apps/api/core';
import { warn as tauriWarn, error as tauriError, info } from '@tauri-apps/plugin-log';

export function log(...msgs: any[]) {
  console.log(...msgs);
  if (isTauri()) info(msgs.join());
}

export function error(...msgs: any[]) {
  console.error(...msgs);
  if (isTauri()) tauriError(msgs.join());
}

export function warn(...msgs: any[]) {
  console.warn(...msgs);
  if (isTauri()) tauriWarn(msgs.join());
}
