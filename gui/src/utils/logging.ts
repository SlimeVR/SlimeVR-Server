import { invoke, isTauri } from '@tauri-apps/api/core';

export function log(...msgs: any[]) {
  console.log(...msgs);
  if (isTauri()) invoke('logging', { msg: msgs.join() });
}

export function error(...msgs: any[]) {
  console.error(...msgs);
  if (isTauri()) invoke('erroring', { msg: msgs.join() });
}

export function warn(...msgs: any[]) {
  console.warn(...msgs);
  if (isTauri()) invoke('warning', { msg: msgs.join() });
}
