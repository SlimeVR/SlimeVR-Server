import * as v from 'valibot';
import { defineSession } from '@/lib/storage';

const CROWDIN_PROJECT = '566b32df1e96d3d76a6ddfd6a17e4cbe';
const CROWDIN_DOMAIN = 'slimevr';
const CROWDIN_ORIGIN = `https://${CROWDIN_DOMAIN}.crowdin.com`;
const ACCOUNTS_ORIGIN = 'https://accounts.crowdin.com';
const JIPT_CDN = 'https://cdn.crowdin.com/jipt';
const STARTUP_TIMEOUT_MS = 20_000;

export type InContextMode = 'off' | 'active' | 'failed';

const mode = defineSession<InContextMode>(
  'crowdin-in-context',
  v.picklist(['off', 'active', 'failed']),
  'off'
);

// Fixed for the whole page load, every switch reloads
export const inContextMode: InContextMode = mode();
let started = false;

export function reloadInContext(next: InContextMode) {
  mode.set(next);
  location.reload();
}

export function setInContext(enabled: boolean) {
  reloadInContext(enabled ? 'active' : 'off');
}

interface CrowdinMessage {
  msg_type?: string;
  has_access?: boolean;
  response?: string | { errorCode?: string };
}

function parseCrowdinMessage(data: unknown): CrowdinMessage | null {
  try {
    return typeof data === 'string' ? JSON.parse(data) : (data as CrowdinMessage);
  } catch {
    return null;
  }
}

function onLogoutMessage(event: MessageEvent) {
  if (event.origin !== ACCOUNTS_ORIGIN && event.origin !== CROWDIN_ORIGIN) return;
  const isLogout =
    event.data === 'logout' ||
    parseCrowdinMessage(event.data)?.msg_type === 'logged_out';
  if (isLogout) mode.set('off');
}

function startupOutcome(data: unknown): 'ready' | 'failed' | null {
  const msg = parseCrowdinMessage(data);
  switch (msg?.msg_type) {
    case 'init_project_data':
      return 'ready';
    case 'request_jipt_error':
      return typeof msg.response === 'string' || msg.response?.errorCode === 'timeout'
        ? 'failed'
        : 'ready';
    case 'init_access_storage':
      return msg.has_access ? null : 'ready';
    default:
      return null;
  }
}

function watchStartup() {
  const fail = () => reloadInContext('failed');
  const onMessage = (event: MessageEvent) => {
    if (event.origin !== CROWDIN_ORIGIN) return;
    const outcome = startupOutcome(event.data);
    if (outcome === 'failed') fail();
    else if (outcome === 'ready') {
      clearTimeout(timeout);
      window.removeEventListener('message', onMessage);
    }
  };
  const timeout = setTimeout(fail, STARTUP_TIMEOUT_MS);
  window.addEventListener('message', onMessage);
  return fail;
}

export function startCrowdinInContext() {
  if (started) return;
  started = true;

  window._jipt = [
    ['project', CROWDIN_PROJECT],
    ['domain', CROWDIN_DOMAIN],
    ['start_type', 'manual'],
    ['escape', () => reloadInContext('off')],
  ];

  const fail = watchStartup();
  window.addEventListener('message', onLogoutMessage);

  const script = document.createElement('script');
  script.src = `${JIPT_CDN}/jipt.js`;
  script.async = true;
  script.onload = () => window.jipt?.start();
  script.onerror = fail;
  document.body.appendChild(script);
}
