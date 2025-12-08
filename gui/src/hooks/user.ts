import { locale } from '@tauri-apps/plugin-os';
import { hostname, platform, version } from 'os';
import { hash } from './crypto';

export async function getUserID() {
  // FIXME: This does not support android. It currently return the same id for all android users

  return hash(`${hostname()}-${await locale()}-${platform()}-${version()}`);
}
