export function log(...msgs: any[]) {
  console.log(...msgs);
  if (window.electronAPI) window.electronAPI.log('info', ...msgs);
}

export function error(...msgs: any[]) {
  console.error(...msgs);
  if (window.electronAPI) window.electronAPI.log('error', ...msgs);
}

export function warn(...msgs: any[]) {
  console.warn(...msgs);
  if (window.electronAPI) window.electronAPI.log('warn', ...msgs);
}
