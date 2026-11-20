import { spawn } from 'node:child_process';
import { existsSync } from 'node:fs';
import { join, delimiter } from 'node:path';

const HOST = '127.0.0.1';
const PORT = 5173;
const RENDERER_URL = `http://${HOST}:${PORT}`;

function resolveElectronExecPath() {
  if (process.env.ELECTRON_EXEC_PATH) return process.env.ELECTRON_EXEC_PATH;
  const exe = process.platform === 'win32' ? 'electron.exe' : 'electron';
  for (const dir of (process.env.PATH || '').split(delimiter)) {
    if (!dir || dir.includes('node_modules')) continue;
    const candidate = join(dir, exe);
    if (existsSync(candidate)) return candidate;
  }
  return '';
}
const ELECTRON_EXEC_PATH = resolveElectronExecPath();

const children = [];
let shuttingDown = false;

function shutdown(code = 0) {
  if (shuttingDown) return;
  shuttingDown = true;
  for (const child of children) {
    child.removeAllListeners('exit');
    child.kill();
  }
  process.exit(code);
}

process.on('SIGINT', () => shutdown(0));
process.on('SIGTERM', () => shutdown(0));

function run(command, args, extraEnv = {}) {
  const child = spawn(command, args, {
    stdio: 'inherit',
    shell: process.platform === 'win32',
    env: { ...process.env, ...extraEnv },
  });
  child.on('exit', (code) => {
    if (!shuttingDown) shutdown(code ?? 0);
  });
  children.push(child);
  return child;
}

async function waitForServer(url, timeoutMs = 30000) {
  const start = Date.now();
  while (Date.now() - start < timeoutMs) {
    try {
      await fetch(url);
      return;
    } catch {
      await new Promise((r) => setTimeout(r, 250));
    }
  }
  throw new Error(
    `Renderer dev server did not start at ${url} within ${timeoutMs}ms`
  );
}

// 1. Start the renderer dev server (fixed port so the URL is deterministic).
run('pnpm', [
  '--filter',
  '@slimevr/gui-app',
  'exec',
  'vite',
  '--host',
  HOST,
  '--port',
  String(PORT),
  '--strictPort',
]);

// 2. Wait for it, then launch electron-vite (main + preload watch) at the dev URL.
await waitForServer(RENDERER_URL);
run(
  'pnpm',
  [
    'exec',
    'electron-vite',
    'dev',
    '--watch',
    '--config',
    'electron.vite.config.ts',
  ],
  { ELECTRON_RENDERER_URL: RENDERER_URL, ELECTRON_EXEC_PATH }
);
