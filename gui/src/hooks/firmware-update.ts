import { BoardType, DeviceDataT } from 'solarxr-protocol';
import { cacheWrap } from './cache';
import semver from 'semver';
import { hostname, locale, platform, version } from '@tauri-apps/plugin-os';

type DeployDataJson = Map<string, string>;
type DeployData = Map<number, Date>;

export interface FirmwareRelease {
  name: string;
  version: string;
  changelog: string;
  firmwareFile: string;
  todayUpdateRange: number;
  userUniqueRange: number;
}

// implemetation of https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function
const hash = (str: string) => {
  let hash = 2166136261;
  for (let i = 0; i < str.length; i++) {
    hash ^= str.charCodeAt(i);
    hash = Math.imul(hash, 16777619); // FNV prime
  }

  // Convert to unsigned 32-bit integer and normalize (0, 1)
  return (hash >>> 0) / 2 ** 32;
};

const uniqueUserKey = `${await hostname()}-${await locale()}-${platform()}-${version()}`;

const firstAsset = (assets: any[], name: string) =>
  assets.find((asset: any) => asset.name === name && asset.browser_download_url);

export async function fetchCurrentFirmwareRelease(): Promise<FirmwareRelease | null> {
  const releases: any[] | null = JSON.parse(
    (await cacheWrap(
      'firmware-releases',
      () =>
        fetch('https://api.github.com/repos/SlimeVR/SlimeVR-Tracker-ESP/releases')
          .then((res) => res.text())
          .catch(() => null),
      60 * 60 * 1000
    )) || 'null'
  );
  if (!releases) return null;

  const firstRelease = releases.find(
    (release) =>
      // release.prerelease === false && // GONE FOR NOW / We will bring it back next release
      release.assets &&
      firstAsset(release.assets, 'BOARD_SLIMEVR-firmware.bin') &&
      firstAsset(release.assets, 'deploy.json')
  );
  if (!firstRelease) return null;

  const deployDataJson: DeployData | null = await fetch(
    firstAsset(firstRelease.assets, 'deploy.json').browser_download_url
  )
    .then((res) => res.json())
    .catch(() => null);
  if (!deployDataJson) return null;

  const deployDataMap = new Map(
    Object.entries(deployDataJson)
  ) as unknown as DeployDataJson;
  const sortedMap = [...deployDataMap].sort(
    ([a], [b]) => parseFloat(b) - parseFloat(a)
  );

  if (sortedMap.keys().find((key) => key > 1 || key <= 0)) return null; // values outside boundaries / cancel

  const deployData: DeployData = new Map();
  let minTime = 0;
  for (const [percent, date] of sortedMap) {
    const d = new Date(date);
    if (d.getTime() <= minTime) return null; // Dates in the wrong order / cancel
    minTime = d.getTime();
    deployData.set(parseFloat(percent), new Date(date));
  }

  const todayUpdateRange = deployData
    .entries()
    .find(([, date]) => Date.now() >= date.getTime())?.[0];

  if (!todayUpdateRange) return null;

  let version = firstRelease.tag_name;
  if (version.charAt(0) === 'v') {
    version = version.substring(1);
  }
  return {
    name: firstRelease.name,
    version,
    changelog: firstRelease.body,
    firmwareFile: firstAsset(firstRelease.assets, 'BOARD_SLIMEVR-firmware.bin')
      .browser_download_url,
    todayUpdateRange,
    userUniqueRange: hash(`${uniqueUserKey}-${version}`), // Make it so the hash change every version. Prevent the same user from getting the same delay
  };
}

export function checkForUpdate(
  currentFirmwareRelease: FirmwareRelease,
  device: DeviceDataT
): 'can-update' | 'low-battery' | 'updated' | 'unavailable' {
  if (
    device.hardwareInfo?.officialBoardType !== BoardType.SLIMEVR ||
    !semver.valid(currentFirmwareRelease.version) ||
    !semver.valid(device.hardwareInfo.firmwareVersion?.toString() ?? 'none') ||
    currentFirmwareRelease.userUniqueRange > currentFirmwareRelease.todayUpdateRange
  ) {
    return 'unavailable';
  }

  const canUpdate = semver.lt(
    device.hardwareInfo.firmwareVersion?.toString() ?? 'none',
    currentFirmwareRelease.version
  );

  if (
    canUpdate &&
    device.hardwareStatus?.batteryPctEstimate != null &&
    device.hardwareStatus.batteryPctEstimate < 50
  ) {
    return 'low-battery';
  }

  return canUpdate ? 'can-update' : 'updated';
}
