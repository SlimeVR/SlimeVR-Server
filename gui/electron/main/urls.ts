import open from 'open';
import { logger } from './logger';

export const CROWDIN_URL = /^https:\/\/(?:[\w-]+\.)?crowdin\.com(?:\/.*)?$/;

const EXTERNAL_URL_ALLOWLIST = [
  /^steam:\/\//,
  /^ms-settings:network$/,
  /^https:\/\/(?:.+\.)?slimevr\.dev(?:\/.+)?$/,
  /^https:\/\/github\.com\/SlimeVR(?:\/.+)?$/,
  /^https:\/\/discord\.gg\/slimevr$/,
  CROWDIN_URL,
];

export function openExternalUrl(url: string) {
  if (EXTERNAL_URL_ALLOWLIST.some((a) => url.match(a))) open(url);
  else logger.error({ url }, 'attempted to open non-whitelisted URL');
}

export function getAppUrl() {
  return process.env.ELECTRON_RENDERER_URL || 'app://./index.html';
}

export function isAppUrl(url: string) {
  const appUrl = getAppUrl();
  if (appUrl.startsWith('app://')) return url.startsWith('app://');
  try {
    return new URL(url).origin === new URL(appUrl).origin;
  } catch {
    return false;
  }
}
