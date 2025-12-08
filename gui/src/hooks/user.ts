import { locale, hostname, platform, version  } from '@tauri-apps/plugin-os';
import { hash } from './crypto';

export async function getUserID() {
  // FIXME: This does not support android. It currently return the same id for all android users

  return hash(`${await hostname()}-${await locale()}-${platform()}-${version()}`);
}
