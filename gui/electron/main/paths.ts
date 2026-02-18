import { app } from 'electron';
import path, { join } from 'node:path';
import { getPlatform } from './utils';
import { glob } from 'glob';
import { spawn } from 'node:child_process';
import javaVersionJar from '../resources/java-version/JavaVersion.jar?asset&asarUnpack';
import { existsSync } from 'node:fs';
import { options } from './cli'

const javaBin = getPlatform() === 'windows' ? 'java.exe' : 'java';
export const CONFIG_IDENTIFIER = 'dev.slimevr.SlimeVR';


export const getGuiDataFolder = () => {
  const platform = getPlatform();

  switch (platform) {
    case 'linux':
      if (process.env['XDG_DATA_HOME'])
        return join(process.env['XDG_DATA_HOME'], CONFIG_IDENTIFIER);
      return join(app.getPath('home'), '.local/share', CONFIG_IDENTIFIER);
    case 'windows':
      return join(app.getPath('appData'), CONFIG_IDENTIFIER);
    case 'macos':
      return join(
        app.getPath('home'),
        'Library/Application Support',
        CONFIG_IDENTIFIER
      );
    case 'unknown':
      throw 'error';
  }
};

export const getServerDataFolder = () => {
  const platform = getPlatform();

  switch (platform) {
    case 'linux':
    case 'windows':
    case 'macos':
      return join(app.getPath('appData'), CONFIG_IDENTIFIER);
    case 'unknown':
      throw 'error';
  }
};

export const getLogsFolder = () => {
  return join(getGuiDataFolder(), 'logs');
};

export const getWindowStateFile = () => join(getServerDataFolder(), '.window-state.json');

const localJavaBin = (sharedDir: string) => {
  const jre = join(sharedDir, 'jre/bin', javaBin);
  return jre;
};

const javaHomeBin = () => {
  const javaHome = process.env['JAVA_HOME'];
  if (!javaHome) return null;
  const javaHomeJre = join(javaHome, 'bin', javaBin);
  return javaHomeJre;
};


export const findSystemJRE = async (sharedDir: string) => {
  const paths = [
    localJavaBin(sharedDir),
    javaHomeBin(),
    ...(await glob('/usr/lib/jvm/*/bin/' + javaBin)),
    ...(await glob('/Library/Java/JavaVirtualMachines/*/Contents/Home/bin/' + javaBin)),
  ]
  console.log(paths);
  for (const path of paths) {
    if (!path) continue;

    const version = await new Promise<number | null>((resolve) => {
      console.log(`${path} -jar ${javaVersionJar}`)
      const process = spawn(path, ['-jar', javaVersionJar]);

      let version: number | null = null;

      process.stdout?.once('data', (data) => {
        try {
          version = parseFloat(data.toString());
        } catch {
          version = null;
        }
      });

      process.on('exit', () => {
        resolve(version);
      });
    });

    if (version && version >= 17) return path;
  }
  return null;
};

export const findServerJar = () => {

  const paths = [
    options.path ? path.resolve(options.path) : undefined,
    // AppImage passes the fakeroot in `APPDIR` env var.
    process.env['APPDIR']
      ? path.resolve(join(process.env['APPDIR'], 'usr/share/slimevr/'))
      : undefined,
    path.dirname(app.getPath('exe')),

    // For flatpack container
    path.resolve('/app/share/slimevr/'),
    path.resolve('/usr/share/slimevr/'),
  ];
  console.log(paths)
  return paths
    .filter((p) => !!p)
    .map((p) => join(p!, 'slimevr.jar'))
    .find((p) => existsSync(p));
};

export const findUpdaterJar = () => {
  const paths = [
    options.path ? path.resolve(options.path) : undefined,
    // AppImage passes the fakeroot in `APPDIR` env var.
    process.env['APPDIR']
      ? path.resolve(join(process.env['APPDIR'], 'usr/share/slimevr/'))
      : undefined,
    path.dirname(app.getPath('exe')),

    // For flatpack container
    path.resolve('/app/share/slimevr/'),
    path.resolve('/usr/share/slimevr/'),
  ];
  return paths
    .filter((p) => !!p)
    .map((p) => join(p!, 'updater.jar'))
    .find((p) => existsSync(p));
};
