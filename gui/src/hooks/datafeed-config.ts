import { DataFeedConfigT, DeviceDataMaskT, TrackerDataMaskT } from 'solarxr-protocol';
import { useConfig } from './config';

export function useDataFeedConfig() {
  const { config } = useConfig();

  const fastDataFeed = config?.debug && config?.devSettings?.fastDataFeed;
  const feedMaxTps = fastDataFeed ? 90 : 10;

  const trackerData = new TrackerDataMaskT();
  trackerData.position = true;
  trackerData.rotation = true;
  trackerData.info = true;
  trackerData.status = true;
  trackerData.temp = true;
  trackerData.linearAcceleration = true;
  trackerData.rotationReferenceAdjusted = true;
  trackerData.rotationIdentityAdjusted = true;
  trackerData.tps = true;
  trackerData.rawMagneticVector = true;
  trackerData.stayAligned = true;

  const dataMask = new DeviceDataMaskT();
  dataMask.deviceData = true;
  dataMask.trackerData = trackerData;

  const dataFeedConfig = new DataFeedConfigT();
  dataFeedConfig.dataMask = dataMask;
  dataFeedConfig.boneMask = false;
  dataFeedConfig.minimumTimeSinceLast = 1000 / feedMaxTps;
  dataFeedConfig.syntheticTrackersMask = trackerData;
  dataFeedConfig.stayAlignedPoseMask = true;
  dataFeedConfig.serverGuardsMask = true;

  return {
    dataFeedConfig,
    feedMaxTps,
  };
}

export function useBonesDataFeedConfig() {
  const { config } = useConfig();

  const fastDataFeed = config?.debug && config?.devSettings?.fastDataFeed;
  const feedMaxTps = fastDataFeed ? 90 : 40;

  const dataFeedConfig = new DataFeedConfigT();
  dataFeedConfig.boneMask = true;
  dataFeedConfig.minimumTimeSinceLast = 1000 / feedMaxTps;
  return dataFeedConfig;
}
