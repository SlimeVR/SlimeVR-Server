import {
  UpdateManifestChannel,
  UpdateManifestChannelVersion,
  UpdateManifestChannelVersionBuild,
  type ChannelName,
  type UpdateManifest,
  type Version,
} from '@slimevr/update-manifest';
import { createContext, useContext } from 'react';

export interface UpdateContext {
  channel: ChannelName;
  notifyOnAvailableUpdates: boolean;

  isUpToDate: boolean;
  latestVersionOnChannel: Version | null;

  manifest: UpdateManifest | null;

  platform: string;
  architecture: string;
  changeSystem(platform: string, architecture: string): void;

  checkCompatibility(
    channelName: ChannelName,
    version: Version
  ): {
    alreadyInstalled: boolean;
    channel: UpdateManifestChannel | null;
    version: UpdateManifestChannelVersion | null;
    build: UpdateManifestChannelVersionBuild | null;
    isInstallable: boolean;
  };
  checkCompatibilityFromVersionInfo(
    channelName: ChannelName,
    version: Version,
    versionInfo: UpdateManifestChannelVersion
  ): {
    alreadyInstalled: boolean;
    build: UpdateManifestChannelVersionBuild | null;
    isInstallable: boolean;
  };
}

export const UpdateContextC = createContext<UpdateContext | null>(null);

export function useUpdateContext() {
  const context = useContext(UpdateContextC);
  if (!context) {
    throw new Error('useUpdateContext must be within a UpdateContext Provider');
  }

  return context;
}
