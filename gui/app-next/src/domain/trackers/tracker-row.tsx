import { TrackerStatus } from 'solarxr-protocol';
import { Show } from 'solid-js';
import { BodyPartIcon } from './body-part-icon';
import { trackerName } from './display';
import { trackerByKey, trackerMovement, type TrackerKey } from './store';
import { TrackerStatusLabel } from './tracker-status';

// One-line tracker: icon, name (+ display name under a custom name), status.
// Paints the parent's control colour. Wrap in Pressable for selection.
export function TrackerRow(props: { trackerKey: TrackerKey }) {
  const tracker = () => trackerByKey(props.trackerKey);
  const info = () => tracker()?.info;
  const renamed = () =>
    !!info()?.customName && info()?.displayName !== info()?.customName;

  return (
    <div
      class="
        movement-glow flex h-12 items-center gap-sm rounded-lg bg-surface-control p-sm
        group-hover/press:bg-surface-hover
      "
      style={{ '--movement': trackerMovement(props.trackerKey) }}
    >
      <span class="flex icon-xl">
        <BodyPartIcon trackerKey={props.trackerKey} />
      </span>
      <div class="flex min-w-0 grow flex-col">
        <span class="truncate font-bold">{trackerName(info())}</span>
        <Show when={renamed()}>
          <span class="truncate">{info()?.displayName}</span>
        </Show>
      </div>
      <span class="shrink-0">
        <TrackerStatusLabel status={tracker()?.status ?? TrackerStatus.NONE} />
      </span>
    </div>
  );
}
