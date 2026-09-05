import {
  BoneMaskT,
  DataFeedConfigT,
  DeviceDataMaskT,
  DongleDataMaskT,
  TrackerDataMaskT,
} from 'solarxr-protocol';
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
  dataFeedConfig.boneMask = null;
  dataFeedConfig.minimumTimeSinceLast = 1000 / feedMaxTps;
  dataFeedConfig.serverGuardsMask = true;

  const dongleMask = new DongleDataMaskT();
  dongleMask.boardType = true;
  dongleMask.customName = true;
  dongleMask.devicesIds = true;
  dongleMask.displayName = true;
  dongleMask.firmwareDate = true;
  dongleMask.firmwareVersion = true;
  dongleMask.hardwareAddress = true;
  dongleMask.hardwareRevision = true;
  dongleMask.manufacturer = true;
  dongleMask.model = true;
  dongleMask.status = true;
  dongleMask.protocolVersion = true;
  dataFeedConfig.dongleMask = dongleMask;

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
  const boneMask = new BoneMaskT();
  boneMask.bodyPart = true;
  boneMask.boneLength = true;
  boneMask.rotation = false;
  boneMask.orientation = true;
  boneMask.headPosition = true;
  boneMask.tailPosition = false;
  boneMask.angularVelocity = false;
  boneMask.linearVelocity = false;
  dataFeedConfig.boneMask = boneMask;
  dataFeedConfig.minimumTimeSinceLast = 1000 / feedMaxTps;
  return dataFeedConfig;
}
