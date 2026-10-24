import { BrowserWindow } from 'electron';
import { isAppUrl, openExternalUrl } from './urls';
import { IPC_CHANNELS } from '@slimevr/gui-shared';

const SESSION_SET_URL = /^https:\/\/[\w-]+\.crowdin\.com\/backend\/jwt\/set\b/;

// This is to show a loader after completing login.
// Crowding take on avg 5s before completing all its redirections and give
// no user feedbacks. This at least show a loader while crowdin do its thing
const WAIT_CSS = `
.slimevr-crowdin-wait {
  position: fixed; inset: 0; z-index: 2147483647;
  display: flex; align-items: center; justify-content: center;
  background: rgba(255, 255, 255, 0.8); cursor: progress;
}
.slimevr-crowdin-wait::after {
  content: ''; width: 48px; height: 48px; border-radius: 50%;
  border: 4px solid #d0d0d0; border-top-color: #263238;
  animation: slimevr-crowdin-spin 0.8s linear infinite;
}
@keyframes slimevr-crowdin-spin { to { transform: rotate(360deg); } }
`;
const SHOW_WAIT_JS = `document.body?.appendChild(
  Object.assign(document.createElement('div'), { className: 'slimevr-crowdin-wait' })
);`;
const HIDE_WAIT_JS = `document.querySelectorAll('.slimevr-crowdin-wait').forEach((el) => el.remove());`;

let crowdinPopup: BrowserWindow | null = null;

export function openCrowdinPopup(parent: BrowserWindow, url: string) {
  if (crowdinPopup) {
    crowdinPopup.loadURL(url);
    crowdinPopup.focus();
    return;
  }

  const popup = new BrowserWindow({
    parent,
    width: 720,
    height: 800,
    webPreferences: { sandbox: true },
  });
  crowdinPopup = popup;

  const closeOnAppUrl = (event: Electron.Event, target: string) => {
    if (!isAppUrl(target)) return;
    event.preventDefault();
    popup.close();
  };

  popup.webContents.on('will-navigate', closeOnAppUrl);

  popup.webContents.on('did-start-navigation', (details) => {
    if (!details.isMainFrame || details.isSameDocument) return;
    popup.webContents.insertCSS(WAIT_CSS);
    popup.webContents.mainFrame.executeJavaScript(SHOW_WAIT_JS);
  });

  popup.webContents.on('did-stop-loading', () => {
    if (popup.isDestroyed()) return;
    popup.webContents.mainFrame.executeJavaScript(HIDE_WAIT_JS);
  });

  let sessionSet = false;
  popup.webContents.on('will-redirect', (event, target, _inPlace, isMainFrame) => {
    if (!isMainFrame) return;
    if (SESSION_SET_URL.test(target)) {
      sessionSet = true;
    } else if (sessionSet) {
      event.preventDefault();
      popup.close();
    } else {
      closeOnAppUrl(event, target);
    }
  });

  popup.webContents.setWindowOpenHandler(({ url: target }) => {
    openExternalUrl(target);
    return { action: 'deny' };
  });

  popup.on('closed', () => {
    crowdinPopup = null;
    if (!parent.isDestroyed())
      parent.webContents.send(IPC_CHANNELS.CROWDIN_POPUP_CLOSED);
  });

  popup.loadURL(url);
}
