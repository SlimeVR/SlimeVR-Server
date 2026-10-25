import {
  BoneMaskT,
  DataFeedConfigT,
  DeviceDataMaskT,
  DongleDataMaskT,
  TrackerDataMaskT,
} from 'solarxr-protocol';

// Position in StartDataFeed.dataFeeds, echoed back as DataFeedUpdate.index
export const TRACKERS_FEED = 0;
export const BONES_FEED = 1;

export function trackersFeedConfig(fast: boolean): DataFeedConfigT {
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
  trackerData.origin = true;

  const dataMask = new DeviceDataMaskT();
  dataMask.deviceData = true;
  dataMask.trackerData = trackerData;

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

  const config = new DataFeedConfigT();
  config.dataMask = dataMask;
  config.boneMask = null;
  config.minimumTimeSinceLast = 1000 / (fast ? 90 : 10);
  config.serverGuardsMask = true;
  config.dongleMask = dongleMask;
  return config;
}

export function bonesFeedConfig(fast: boolean): DataFeedConfigT {
  const boneMask = new BoneMaskT();
  boneMask.bodyPart = true;
  boneMask.boneLength = true;
  boneMask.rotation = false;
  boneMask.orientation = true;
  boneMask.headPosition = true;
  boneMask.tailPosition = false;
  boneMask.angularVelocity = false;
  boneMask.linearVelocity = false;
  boneMask.trackerOffset = false;

  const config = new DataFeedConfigT();
  config.boneMask = boneMask;
  config.minimumTimeSinceLast = 1000 / (fast ? 90 : 40);
  return config;
}
