import {
  ChannelName,
  UpdateManifest,
  UpdateManifestChannel,
  UpdateManifestChannelVersion,
  UpdateManifestChannelVersionBuild,
  Version,
} from '@slimevr/update-manifest';

type System = {
  platform: string;
  architecture: string;
};

export function checkVersionCompatibility(
  manifest: UpdateManifest,
  channelName: ChannelName,
  version: Version,
  currentChannelName: ChannelName,
  system: System
): {
  alreadyInstalled: boolean;
  isInstallable: boolean;
  channel: UpdateManifestChannel | null;
  version: UpdateManifestChannelVersion | null;
  build: UpdateManifestChannelVersionBuild | null;
} {
  const alreadyInstalled = isAlreadyInstalled(channelName, version, currentChannelName);

  const channel = manifest.channels[channelName];

  const versionInfo = channel.versions[version];
  if (!versionInfo)
    return {
      alreadyInstalled,
      channel,
      version: null,
      build: null,
      isInstallable: false,
    };

  const build = findBuildForPlatformAndArchitecture(versionInfo, system);

  return {
    alreadyInstalled,
    channel,
    version: versionInfo,
    build,
    isInstallable: !alreadyInstalled && build !== null,
  };
}

function isAlreadyInstalled(
  channelName: ChannelName,
  version: Version,
  currentChannelName: ChannelName
) {
  return currentChannelName === channelName && __VERSION_TAG__ === version;
}

export function checkVersionCompatibility2(
  channelName: ChannelName,
  version: Version,
  versionInfo: UpdateManifestChannelVersion,
  currentChannelName: ChannelName,
  system: System
) {
  const alreadyInstalled = isAlreadyInstalled(channelName, version, currentChannelName);

  const build = findBuildForPlatformAndArchitecture(versionInfo, system);

  return {
    alreadyInstalled,
    build,
    isInstallable: !alreadyInstalled && build !== null,
  };
}

function findBuildForPlatformAndArchitecture(
  versionInfo: UpdateManifestChannelVersion,
  system: System
) {
  const buildsForPlatform = versionInfo.builds[system.platform];
  if (!buildsForPlatform) return null;

  const buildForPlatformAndArchitecture = buildsForPlatform[system.architecture];
  if (!buildForPlatformAndArchitecture) return null;

  return buildForPlatformAndArchitecture;
}
