import {
  app,
  BrowserWindow,
  dialog,
  Menu,
  nativeImage,
  net,
  protocol,
  screen,
  shell,
  Tray,
} from 'electron';
import { IPC_CHANNELS } from '../shared';
import path, { dirname, join } from 'path';
import open from 'open';
import trayIcon from '../resources/icons/icon.png?asset';
import appleTrayIcon from '../resources/icons/appleTrayIcon.png?asset';
import { readFile, stat } from 'fs/promises';
import { getPlatform, handleIpc, isPortAvailable } from './utils';
import {
  findServerJar,
  findSystemJRE,
  findUpdaterJar,
  getGuiDataFolder,
  getLogsFolder,
  getServerDataFolder,
  getWindowStateFile,
} from './paths';
import { stores } from './store';
import { logger } from './logger';
import { writeFileSync } from 'node:fs';

import { spawn } from 'node:child_process';
import { discordPresence } from './presence';
import { options } from './cli';
import { ServerStatusEvent } from 'electron/preload/interface';

// Register custom protocol to handle asset paths with leading slashes
protocol.registerSchemesAsPrivileged([
  {
    scheme: 'app',
    privileges: {
      standard: true,
      secure: true,
      supportFetchAPI: true,
      corsEnabled: true,
    },
  },
]);

let mainWindow: BrowserWindow | null = null;

handleIpc(IPC_CHANNELS.GH_FETCH, async (e, options) => {
  if (options.type === 'fw-releases') {
    return fetch(
      'https://api.github.com/repos/SlimeVR/SlimeVR-Tracker-ESP/releases'
    ).then((res) => res.json());
  }
  if (options.type === 'asset') {
    if (
      !options.url.startsWith(
        'https://github.com/SlimeVR/SlimeVR-Tracker-ESP/releases/download'
      )
    )
      return null;
    return fetch(options.url).then((res) => res.json());
  }
});

handleIpc(IPC_CHANNELS.OS_STATS, async () => {
  return {
    type: getPlatform(),
  };
});

handleIpc(IPC_CHANNELS.I18N_OVERRIDE, async () => {
  const overridefile = join(getServerDataFolder(), 'override.ftl');
  const exists = await stat(overridefile)
    .then(() => true)
    .catch(() => false);

  if (!exists) return false;
  return readFile(overridefile, { encoding: 'utf-8' });
});

handleIpc(IPC_CHANNELS.LOG, (e, type, ...args) => {
  let payload: Record<string, unknown> = {};
  const messageParts: unknown[] = [];

  args.forEach((arg) => {
    if (arg instanceof Error) {
      payload.err = arg;
    } else if (typeof arg === 'object' && arg !== null) {
      payload = { ...payload, ...arg };
    } else {
      messageParts.push(arg);
    }
  });

  const msg = messageParts.join(' ');

  switch (type) {
    case 'error':
      logger.error(payload, msg);
      break;
    case 'warn':
      logger.warn(payload, msg);
      break;
    default:
      logger.info(payload, msg);
  }
});

handleIpc(IPC_CHANNELS.OPEN_URL, (e, url) => {
  const allowsd_urls = [
    /steam:\/\/.*/,
    /ms-settings:network$/,
    /https:\/\/.*\.slimevr\.dev.*/,
    /https:\/\/github\.com\/.*/,
    /https:\/\/discord\.gg\/slimevr$/,
  ];
  if (allowsd_urls.find((a) => url.match(a))) open(url);
  else logger.error({ url }, 'trying to open non allowed url');
});

handleIpc(IPC_CHANNELS.STORAGE, async (e, { type, method, key, value }) => {
  const store = stores[type];
  if (!store) throw new Error(`Storage type ${type} not found`);

  switch (method) {
    case 'get':
      return store.get(key!);
    case 'set':
      return store.set(key!, value);
    case 'delete':
      return store.delete(key!);
    case 'save':
      return store.save();
  }
});

handleIpc(IPC_CHANNELS.DISCORD_PRESENCE, async (e, options) => {
  if (options.enable && !discordPresence.state.ready) {
    await discordPresence.connect();
    discordPresence.updateActivity(options.activity);
  } else if (!options.enable && discordPresence.state.ready) {
    discordPresence.destroy();
  }
});

handleIpc(IPC_CHANNELS.OPEN_FILE, (e, folder) => {
  const requestedPath = path.resolve(folder);

  const isAllowed = [getServerDataFolder(), getGuiDataFolder(), getLogsFolder()].some(
    (parent) => {
      const absoluteParent = path.resolve(parent);
      const relative = path.relative(absoluteParent, requestedPath);
      return !relative.includes('..') && !path.isAbsolute(relative);
    }
  );

  if (isAllowed) {
    shell.openPath(requestedPath);
  } else {
    logger.error({ path: requestedPath }, 'Blocked unauthorized path');
  }
});

handleIpc(IPC_CHANNELS.GET_FOLDER, (e, folder) => {
  switch (folder) {
    case 'config':
      return getGuiDataFolder();
    case 'logs':
      return getLogsFolder();
  }
});

const windowStateFile = await readFile(getWindowStateFile(), {
  encoding: 'utf-8',
}).catch(() => null);

const defaultWindowState: {
  width: number;
  height: number;
  x?: number;
  y?: number;
  minimized: boolean;
} = {
  width: 1289.0,
  height: 709.0,
  x: undefined,
  y: undefined,
  minimized: false,
};
const windowState = windowStateFile ? JSON.parse(windowStateFile) : defaultWindowState;

const MIN_WIDTH = 393;
const MIN_HEIGHT = 667;

function validateWindowState(state: typeof defaultWindowState) {
  if (state.x === undefined || state.y === undefined) {
    return state;
  }

  const displays = screen.getAllDisplays();

  const isVisible = displays.some((display) => {
    return (
      state.x! >= display.bounds.x &&
      state.y! >= display.bounds.y &&
      state.x! + state.width <= display.bounds.x + display.bounds.width &&
      state.y! + state.height <= display.bounds.y + display.bounds.height
    );
  });

  const minWidth = MIN_WIDTH;
  const minHeight = MIN_HEIGHT;

  if (!isVisible || state.width < minWidth || state.height < minHeight) {
    return defaultWindowState;
  }

  return state;
}

function createWindow() {
  const validatedState = validateWindowState(windowState);

  mainWindow = new BrowserWindow({
    width: validatedState.width,
    height: validatedState.height,
    x: validatedState.x,
    y: validatedState.y,
    minHeight: MIN_HEIGHT,
    minWidth: MIN_WIDTH,
    movable: true,
    frame: false,
    roundedCorners: true,
    webPreferences: {
      preload: join(__dirname, '../preload/index.js'),
      nodeIntegration: false,
      contextIsolation: true,
    },
  });

  if (windowState.minimized) {
    mainWindow.minimize();
  }

  if (process.env.ELECTRON_RENDERER_URL) {
    mainWindow.loadURL(process.env.ELECTRON_RENDERER_URL);
    mainWindow.webContents.openDevTools();
  } else {
    mainWindow.loadURL('app://./index.html');
  }

  mainWindow.on('closed', () => {
    mainWindow = null;
  });

  handleIpc('window-actions', (e, action) => {
    switch (action) {
      case 'close':
        mainWindow?.close();
        break;
      case 'minimize':
        mainWindow?.minimize();
        break;
      case 'maximize':
        mainWindow?.maximize();
        break;
    }
  });

  handleIpc('open-dialog', (e, options) => dialog.showOpenDialog(options));
  handleIpc('save-dialog', (e, options) => dialog.showSaveDialog(options));

  const icon = nativeImage.createFromPath(
    getPlatform() === 'macos' ? appleTrayIcon : trayIcon
  );
  const tray = new Tray(icon);
  tray.setToolTip('SlimeVR');
  tray.on('click', () => {
    mainWindow?.show();
  });
  const contextMenu = Menu.buildFromTemplate([
    {
      label: 'Show',
      click: () => {
        mainWindow?.show();
      },
    },
    {
      label: 'Hide',
      click: () => {
        mainWindow?.hide();
      },
    },
    { role: 'quit' },
  ]);
  tray.setContextMenu(contextMenu);

  const updateWindowState = () => {
    if (!mainWindow) return;

    windowState.minimized = mainWindow.isMinimized();
    if (!mainWindow.isMinimized() && !mainWindow.isMaximized()) {
      const bounds = mainWindow.getBounds();
      windowState.width = bounds.width;
      windowState.height = bounds.height;
      windowState.x = bounds.x;
      windowState.y = bounds.y;
    }
  };

  mainWindow.on('move', updateWindowState);
  mainWindow.on('resize', updateWindowState);
  mainWindow.on('minimize', updateWindowState);
  mainWindow.on('maximize', updateWindowState);
}

const checkEnvironmentVariables = () => {
  const to_check = ['_JAVA_OPTIONS', 'JAVA_TOOL_OPTIONS'];

  const set = to_check.filter((env) => !!process.env[env]);
  if (set.length > 0) {
    dialog.showErrorBox(
      'SlimeVR',
      `You have environment variables ${set.join(', ')} set, which may cause the SlimeVR Server to fail to launch properly.`
    );
    app.exit(0);
    return;
  }
};

const isServerRunning = async () => !await isPortAvailable(21110)

const spawnServer = async () => {
  if (options.skipServerIfRunning && await isServerRunning()) {
    logger.info({ skipServerIfRunning: options.skipServerIfRunning }, 'Server alredy running, skipping');
    return;
  }


  const updaterJar = findUpdaterJar();
  if (!updaterJar) {
    logger.info('updater jar not found, skippin');
    return;
  }
  
  const serverJar = findServerJar();
  if (!serverJar) {
    logger.info('server jar not found, skipping');
    return;
  }
  const sharedDir = dirname(serverJar);
  const javaBin = await findSystemJRE(sharedDir);
  if (!javaBin) {
    dialog.showErrorBox(
      'SlimeVR',
      `Couldn't find a compatible Java version, please download Java 17 or higher`
    );
    app.exit(0);
    return;
  }

  logger.info({ updaterJar }, 'found updater jar');
  logger.info({ serverJar }, 'found server jar');

  const updater = spawn(javaBin, ['-Xmx128M', '-jar', updaterJar, 'run']);

    updater.stdout?.on('data', (message) => {
    mainWindow?.webContents.send(IPC_CHANNELS.SERVER_STATUS, {
      message: message.toString(),
      type: 'stdout'
    } satisfies ServerStatusEvent)
  });

  updater.stderr?.on('data', (message) => {
    mainWindow?.webContents.send(IPC_CHANNELS.SERVER_STATUS, {
      message: message.toString(),
      type: 'stderr'
    } satisfies ServerStatusEvent)
  });

  const process = spawn(javaBin, ['-Xmx128M', '-jar', serverJar, 'run']);

  process.stdout?.on('data', (message) => {
    mainWindow?.webContents.send(IPC_CHANNELS.SERVER_STATUS, {
      message: message.toString(),
      type: 'stdout'
    } satisfies ServerStatusEvent)
  });

  process.stderr?.on('data', (message) => {
    mainWindow?.webContents.send(IPC_CHANNELS.SERVER_STATUS, {
      message: message.toString(),
      type: 'stderr'
    } satisfies ServerStatusEvent)
  });

  return {
    process: process,
    close: () => {
      process.kill('SIGTERM');
    },
  };
};

app.whenReady().then(async () => {
  // Register protocol handler for app:// scheme to handle assets with leading slashes
  protocol.handle('app', (request) => {
    const url = request.url.slice('app://'.length);
    const filePath = path.normalize(join(__dirname, '../renderer', url));
    return net.fetch('file://' + filePath);
  });

  checkEnvironmentVariables();
  const server = await spawnServer();

  createWindow();

  logger.info('SlimeVR started!');

  app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
      app.quit();
    }
  });

  process.on('exit', () => {
    server?.close();
  })

  app.on('before-quit', () => {
    logger.info('App quitting, saving...');
    server?.close();
    stores.settings.save();
    stores.cache.save();

    discordPresence.destroy();

    writeFileSync(getWindowStateFile(), JSON.stringify(windowState));
  });
});
