import {
  DataFeedUpdateT,
  StartDataFeedT,
  type BodyPart,
  type BoneT,
  type ServerGuardsT,
} from 'solarxr-protocol';
import { createEffect, createRoot, createSignal, onCleanup, untrack } from 'solid-js';
import { devSetting } from '@/domain/developer';
import { ingestTrackers } from '@/domain/trackers';
import { client } from '@/solarxr/client';
import { toPlain, type Plain } from '@/solarxr/plain';
import {
  BONES_FEED,
  bonesFeedConfig,
  TRACKERS_FEED,
  trackersFeedConfig,
} from './config';

export type Bone = Plain<BoneT>;
export type ServerGuards = Plain<ServerGuardsT>;

function sameGuards(a: ServerGuards | null, b: ServerGuards | null) {
  if (a === null || b === null) return a === b;
  return (
    a.yawReset === b.yawReset &&
    a.mountingReset === b.mountingReset &&
    a.canDoUserHeightCalibration === b.canDoUserHeightCalibration
  );
}

// Hot data, replaced every tick: read it in leaves or three.js frame loops
const [bones, setBones] = createSignal<readonly Bone[]>([], { equals: false });
const [serverGuards, setServerGuards] = createSignal<ServerGuards | null>(null, {
  equals: sameGuards,
});

export { bones, serverGuards };

export function boneByPart(part: BodyPart): Bone | undefined {
  return bones().find((bone) => bone.bodyPart === part);
}

function sendStart(fast: boolean) {
  const start = new StartDataFeedT();
  start.dataFeeds = [trackersFeedConfig(fast), bonesFeedConfig(fast)];
  client.send(start);
}

// Boot service. Starts the feeds on every connect and when the rate pref
// changes, the server replaces the previous feeds on each StartDataFeed.
export function startDatafeed(): () => void {
  return createRoot((dispose) => {
    createEffect(() => {
      const fast = devSetting('fastDataFeed');
      if (client.status() === 'connected') untrack(() => sendStart(fast));
    });
    onCleanup(
      client.on(DataFeedUpdateT, (update) => {
        if (update.index === TRACKERS_FEED) {
          ingestTrackers(update, performance.now());
          setServerGuards(update.serverGuards && toPlain(update.serverGuards));
        } else if (update.index === BONES_FEED) {
          setBones(toPlain(update.bones));
        }
      })
    );
    return dispose;
  });
}

// Tests and HMR
export function resetDatafeed() {
  setBones([]);
  setServerGuards(null);
}
