import { BoardType, DeviceDataT } from 'solarxr-protocol';
import { fetch as tauriFetch } from '@tauri-apps/plugin-http';
import { cacheWrap } from './cache';
import semver from 'semver';
import { normalizedHash } from './crypto';

export interface FirmwareRelease {
  name: string;
  version: string;
  changelog: string;
  firmwareFiles: Partial<Record<BoardType, { url: string; digest: string }>>;
  userCanUpdate: boolean;
}

const firstAsset = (assets: any[], name: string) =>
  assets.find((asset: any) => asset.name === name && asset.browser_download_url);

const todaysRange = (deployData: [number, Date][]): number => {
  let maxRange = 0;
  for (const [range, date] of deployData) {
    if (Date.now() >= date.getTime()) maxRange = range;
  }
  return maxRange;
};

const checkUserCanUpdate = async (uuid: string, url: string, fwVersion: string) => {
  const deployDataJson = JSON.parse(
    (await cacheWrap(
      `firmware-${fwVersion}-deploy`,
      () =>
        tauriFetch(url)
          .then((res) => res.text())
          .catch(() => null),
      60 * 60 * 1000
    )) || 'null'
  );
  if (!deployDataJson) return false;

  const deployData = (
    Object.entries(deployDataJson).map(([key, val]) => {
      return [parseFloat(key), new Date(val as string)];
    }) as [number, Date][]
  ).sort(([a], [b]) => a - b);

  if (deployData.find(([key]) => key > 1 || key <= 0)) return false; // values outside boundaries / cancel

  if (
    deployData.find(
      ([, date], index) =>
        index > 0 && date.getTime() < deployData[index - 1][1].getTime()
    )
  )
    return false; // Dates in the wrong order / cancel

  const todayUpdateRange = todaysRange(deployData);
  if (!todayUpdateRange) return false;

  // Make it so the hash change every version. Prevent the same user from getting the same delay
  return normalizedHash(`${uuid}-${fwVersion}`) <= todayUpdateRange;
};

export async function fetchCurrentFirmwareRelease(
  uuid: string
): Promise<FirmwareRelease | null> {
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

  const processedReleses = [];
  for (const release of releases) {
    const fwAsset = firstAsset(release.assets, 'BOARD_SLIMEVR-firmware.bin');
    const fw12Asset = firstAsset(release.assets, 'BOARD_SLIMEVR_V1_2-firmware.bin');
    const deployAsset = firstAsset(release.assets, 'deploy.json');
    if (
      !release.assets ||
      !deployAsset ||
      (!fwAsset && !fw12Asset) ||
      release.prerelease
    )
      continue;

    let version = release.tag_name;
    if (version.charAt(0) === 'v') {
      version = version.substring(1);
    }

    const userCanUpdate = await checkUserCanUpdate(
      uuid,
      deployAsset.browser_download_url,
      version
    );
    processedReleses.push({
      name: release.name,
      version,
      changelog: release.body,
      firmwareFiles: {
        [BoardType.SLIMEVR]: {
          url: fwAsset.browser_download_url,
          digest: fwAsset.digest,
        },
        [BoardType.SLIMEVR_V1_2]: {
          url: fw12Asset.browser_download_url,
          digest: fw12Asset.digest,
        },
      },
      userCanUpdate,
    });

    if (userCanUpdate) break; // Stop early if we found one valid update. No need to download more
  }
  return (
    processedReleses.find(({ userCanUpdate }) => userCanUpdate) ?? processedReleses[0]
  );
}

export function checkForUpdate(
  currentFirmwareRelease: FirmwareRelease,
  device: DeviceDataT
): 'can-update' | 'low-battery' | 'updated' | 'unavailable' | 'blocked' {
  if (!currentFirmwareRelease.userCanUpdate) return 'blocked';

  if (
    !device.hardwareInfo?.officialBoardType ||
    ![BoardType.SLIMEVR, BoardType.SLIMEVR_V1_2].includes(
      device.hardwareInfo.officialBoardType
    ) ||
    !semver.valid(currentFirmwareRelease.version) ||
    !semver.valid(device.hardwareInfo.firmwareVersion?.toString() ?? 'none')
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
