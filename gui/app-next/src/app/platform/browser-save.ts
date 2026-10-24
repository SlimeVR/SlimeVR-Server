import { fileSave } from 'browser-fs-access';
import type { SaveOptions, SaveResult } from '@/lib/platform';

export async function saveWithBrowser({
  blob,
  fileName,
  extensions,
}: SaveOptions): Promise<SaveResult> {
  try {
    await fileSave(blob, { fileName, extensions });
    return 'saved';
  } catch (e) {
    if (e instanceof DOMException && e.name === 'AbortError') return 'canceled';
    throw e;
  }
}
