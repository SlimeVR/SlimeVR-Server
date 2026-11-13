import { fileSave } from 'browser-fs-access';

function toBase64(blob: Blob) {
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      const url = reader.result as string;
      resolve(url.slice(url.indexOf(',') + 1));
    };
    reader.onerror = () => reject(reader.error);
    reader.readAsDataURL(blob);
  });
}

/**
 * Saves `blob` as `fileName`. The Android WebView has no downloads, so there it goes
 * through the system save dialog.
 */
export async function saveFile(blob: Blob, fileName: string, extensions: string[]) {
  const android = window.__ANDROID__;
  if (android?.saveFile) {
    android.saveFile(
      fileName,
      blob.type || 'application/octet-stream',
      await toBase64(blob)
    );
    return;
  }

  await fileSave(blob, { fileName, extensions });
}
