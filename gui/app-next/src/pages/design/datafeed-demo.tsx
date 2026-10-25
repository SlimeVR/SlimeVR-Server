import { BodyPart, ResetAvailability, TrackerStatus } from 'solarxr-protocol';
import { createSignal, For, Index, Show } from 'solid-js';
import { bones, serverGuards } from '@/domain/datafeed';
import {
  assignedTrackerKeys,
  connectedTrackerKeys,
  dongleById,
  dongleIds,
  toEulerDegrees,
  TrackerCard,
  trackerByKey,
  trackerDevice,
  trackerKeys,
  trackerMovement,
  trackerName,
  TrackerRow,
  type TrackerKey,
} from '@/domain/trackers';
import { fmt, formats } from '@/lib/format';
import { Pressable } from '@/ui/pressable';

//placholder
function TrackerTableRow(props: { trackerKey: TrackerKey }) {
  const tracker = () => trackerByKey(props.trackerKey);
  const device = () => trackerDevice(props.trackerKey);
  const euler = () => toEulerDegrees(tracker()?.rotation);
  const movement = () => trackerMovement(props.trackerKey);
  const battery = () => device()?.hardwareStatus?.batteryPctEstimate;

  return (
    <tr class="border-line border-t">
      <td class="py-xs pr-md font-mono">{props.trackerKey}</td>
      <td class="pr-md">{trackerName(tracker()?.info)}</td>
      <td class="pr-md">
        {TrackerStatus[tracker()?.status ?? TrackerStatus.NONE]}
      </td>
      <td class="pr-md font-mono">
        {fmt(formats.degrees, euler().x)} {fmt(formats.degrees, euler().y)}{' '}
        {fmt(formats.degrees, euler().z)}
      </td>
      <td class="pr-md">
        <div class="bg-surface-well h-xs w-24 rounded-full">
          <div
            class="bg-accent h-full rounded-full"
            style={{ width: `${movement() * 100}%` }}
          />
        </div>
      </td>
      <td class="pr-md">
        {fmt(formats.percent, battery() == null ? null : battery()! / 100)}
      </td>
      <td class="pr-md">
        {fmt(formats.volts, device()?.hardwareStatus?.batteryVoltage)}
      </td>
      <td class="pr-md">{fmt(formats.dbm, device()?.hardwareStatus?.rssi)}</td>
      <td class="pr-md font-mono">{tracker()?.tps ?? '--'}</td>
    </tr>
  );
}

function availability(value: ResetAvailability | undefined) {
  return value === undefined ? '--' : ResetAvailability[value];
}

function TrackerWidgets() {
  const [selected, setSelected] = createSignal<TrackerKey>();

  function toggle(key: TrackerKey) {
    setSelected(selected() === key ? undefined : key);
  }

  return (
    <>
      <span class="text-section">Tracker cards</span>
      <div class="grid gap-lg @3xl:grid-cols-2">
        <For each={trackerKeys()}>
          {(key, i) => (
            <Pressable onClick={() => toggle(key)} class="rounded-lg">
              <TrackerCard
                trackerKey={key}
                size="small"
                warning={i() === 0 ? 'tracker-status-error' : undefined}
              />
            </Pressable>
          )}
        </For>
      </div>
      <Show when={trackerKeys()[0]}>
        {(key) => (
          <div class="w-full max-w-xs">
            <TrackerCard trackerKey={key()} glow={false} />
          </div>
        )}
      </Show>
      <span class="text-section">Tracker rows (press to select)</span>
      <div class="flex max-w-md flex-col gap-sm">
        <For each={trackerKeys()}>
          {(key) => (
            <Pressable
              onClick={() => toggle(key)}
              pressed={selected() === key}
              class="rounded-lg aria-pressed:ring-2 aria-pressed:ring-accent"
            >
              <TrackerRow trackerKey={key} />
            </Pressable>
          )}
        </For>
      </div>
    </>
  );
}

// Dev check of the datafeed ingest and the trackers store
export function DatafeedDemo() {
  return (
    <div class="surface-page flex flex-col gap-lg rounded-lg p-lg">
      <span class="text-title">Datafeed</span>

      <div class="flex flex-wrap gap-lg">
        <span>trackers {trackerKeys().length}</span>
        <span>assigned {assignedTrackerKeys().length}</span>
        <span>connected {connectedTrackerKeys().length}</span>
        <span>bones {bones().length}</span>
      </div>

      <span class="text-section">Server guards</span>
      <Show
        when={serverGuards()}
        fallback={<span class="text-fg-muted">none yet</span>}
      >
        {(guards) => (
          <div class="flex flex-wrap gap-lg">
            <span>yaw reset {availability(guards().yawReset)}</span>
            <span>mounting reset {availability(guards().mountingReset)}</span>
            <span>
              height calibration{' '}
              {guards().canDoUserHeightCalibration ? 'yes' : 'no'}
            </span>
          </div>
        )}
      </Show>

      <span class="text-section">Trackers</span>
      <div class="overflow-x-auto">
        <table class="text-left">
          <thead class="text-fg-muted">
            <tr>
              <th class="pr-md font-normal">key</th>
              <th class="pr-md font-normal">name</th>
              <th class="pr-md font-normal">status</th>
              <th class="pr-md font-normal">euler x y z</th>
              <th class="pr-md font-normal">movement</th>
              <th class="pr-md font-normal">battery</th>
              <th class="pr-md font-normal">voltage</th>
              <th class="pr-md font-normal">rssi</th>
              <th class="pr-md font-normal">tps</th>
            </tr>
          </thead>
          <tbody>
            <For each={trackerKeys()}>
              {(key) => <TrackerTableRow trackerKey={key} />}
            </For>
          </tbody>
        </table>
      </div>

      <TrackerWidgets />

      <span class="text-section">Dongles</span>
      <Show
        when={dongleIds().length > 0}
        fallback={<span class="text-fg-muted">none</span>}
      >
        <For each={dongleIds()}>
          {(id) => (
            <span>
              {id}:{' '}
              {dongleById(id)?.customName ||
                dongleById(id)?.displayName ||
                '--'}{' '}
              ({dongleById(id)?.devicesIds.length ?? 0} devices)
            </span>
          )}
        </For>
      </Show>

      <span class="text-section">Bones</span>
      <div class="grid grid-cols-[auto_auto_1fr] gap-x-md font-mono">
        <Index each={bones()}>
          {(bone) => (
            <>
              <span>{BodyPart[bone().bodyPart]}</span>
              <span>{fmt(formats.meters, bone().boneLength)}</span>
              <span>
                {fmt(formats.offset, bone().headPosition?.x)}{' '}
                {fmt(formats.offset, bone().headPosition?.y)}{' '}
                {fmt(formats.offset, bone().headPosition?.z)}
              </span>
            </>
          )}
        </Index>
      </div>
    </div>
  );
}
