import { invoke } from '@tauri-apps/api/core';

export function log(...msgs: any[]) {
  console.log(...msgs);
  invoke('logging', { msg: msgs.join() });
}

export function error(...msgs: any[]) {
  console.error(...msgs);
  invoke('erroring', { msg: msgs.join() });
}

export function warn(...msgs: any[]) {
  console.warn(...msgs);
  invoke('warning', { msg: msgs.join() });
}
