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
import { spawn } from 'child_process';
import { getPlatform } from './utils';
import { findSystemJRE, findUpdaterJar } from './paths';
import path, { dirname } from 'path';
import { closeLogger, logger } from './logger';
import { ServerStatusEvent } from 'electron/preload/interface';
import { IPC_CHANNELS } from '../shared';

export const runUpdater = async (args: string[]) => {
  const platform = getPlatform();
  const updaterJar = findUpdaterJar();
  if (!updaterJar) {
    logger.info('updater jar not found');
    return;
  }
  const sharedDir = dirname(updaterJar);
  const javaBin = await findSystemJRE(sharedDir);
  if (!javaBin) {
    dialog.showErrorBox(
      'SlimeVR',
      `Couldn't find a compatible Java version, please download Java 17 or higher`
    );
    app.quit();
    return;
  }
  const updaterArgs = ['-Xmx128M', '-jar', updaterJar, ...args];
  console.log(updaterArgs)
  const updaterProcess = spawn(javaBin, updaterArgs, {
    detached: true,
    stdio: 'ignore'
  });

  updaterProcess.on('error', (err) => {
    logger.info({ err }, 'Error launching the updater')
  })
    updaterProcess.on('exit', () => {
      logger.info('Server process exiting');
    })
    app.exit();
};
