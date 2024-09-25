import { isTauri } from '@tauri-apps/api/core';
import { type } from '@tauri-apps/plugin-os';

/**
 * Fetches the resource as a blob if necessary because of https://github.com/tauri-apps/tauri/issues/3725
 * @param url static asset to fetch
 * @returns URL
 */
export async function fetchResourceUrl(url: string) {
  if (!isTauri() || type() !== 'linux') return url;
  return URL.createObjectURL(await fetch(url).then((res) => res.blob()));
}
