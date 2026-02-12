
export async function openUrl(url: string) {
  if (window.electronAPI) {
    window.electronAPI.openUrl(url);
  } else {
    window.open(url, '_blank')
  }
}
