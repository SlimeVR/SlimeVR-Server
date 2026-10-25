import { TrackerStatus } from 'solarxr-protocol';
import { createUniqueId, Show } from 'solid-js';
import ExclamationTriangleIcon from '~icons/heroicons/exclamation-triangle-20-solid';
import { cx } from '@/lib/cx';
import { t, type MessageId } from '@/lib/l10n';
import { Tooltip } from '@/ui/tooltip';
import { BodyPartIcon } from './body-part-icon';
import { trackerName } from './display';
import { trackerByKey, trackerMovement, type TrackerKey } from './store';
import { TrackerBattery } from './tracker-battery';
import { TrackerStatusLabel } from './tracker-status';
import { TrackerWifi } from './tracker-wifi';

export interface TrackerCardProps {
  trackerKey: TrackerKey;
  // small: one line (Home grid). large: centered column (tracker page).
  size?: 'small' | 'large';
  warning?: MessageId;
  // Glow with the tracker's movement
  glow?: boolean;
}

function SmallCard(props: { trackerKey: TrackerKey; warning?: MessageId }) {
  const tracker = () => trackerByKey(props.trackerKey);

  return (
    <div class="flex h-17.5 w-full items-center gap-lg px-md py-1.5">
      <div class="relative flex">
        <span
          class={cx(
            'flex overflow-clip rounded-md border-3 icon-xl',
            props.warning ? 'border-warning/80' : 'border-transparent'
          )}
        >
          <BodyPartIcon trackerKey={props.trackerKey} />
        </span>
        <Show when={props.warning}>
          <span class="absolute -right-2 -bottom-3 flex text-warning icon-md">
            <ExclamationTriangleIcon />
          </span>
        </Show>
      </div>
      <div class="flex min-w-0 grow flex-col justify-center gap-0.5">
        <span class="truncate text-section">
          {trackerName(tracker()?.info)}
        </span>
        <TrackerStatusLabel status={tracker()?.status ?? TrackerStatus.NONE} />
      </div>
      <TrackerBattery trackerKey={props.trackerKey} />
      <TrackerWifi trackerKey={props.trackerKey} />
    </div>
  );
}

function LargeCard(props: { trackerKey: TrackerKey }) {
  const tracker = () => trackerByKey(props.trackerKey);

  return (
    <div class="flex w-full flex-col items-center justify-center gap-sm px-lg py-2xl">
      <span class="flex icon-lg">
        <BodyPartIcon trackerKey={props.trackerKey} />
      </span>
      <span class="max-w-full truncate font-bold">
        {trackerName(tracker()?.info)}
      </span>
      <TrackerStatusLabel status={tracker()?.status ?? TrackerStatus.NONE} />
      <div class="flex min-h-9 flex-wrap items-center justify-center gap-5">
        <TrackerBattery trackerKey={props.trackerKey} />
        <TrackerWifi trackerKey={props.trackerKey} numeric />
      </div>
    </div>
  );
}

// Tracker summary on a card surface. Make it pressable by wrapping it in
// Pressable or a link with `group/press`, the card then shows hover.
export function TrackerCard(props: TrackerCardProps) {
  const tipId = createUniqueId();
  let el: HTMLDivElement | undefined;
  const movement = () =>
    props.glow === false ? 0 : trackerMovement(props.trackerKey);

  return (
    <>
      <div
        ref={el}
        class="
          surface-card movement-glow overflow-hidden rounded-lg group-hover/press:bg-surface-control
        "
        style={{ '--movement': movement() }}
      >
        <Show
          when={props.size === 'small'}
          fallback={<LargeCard trackerKey={props.trackerKey} />}
        >
          <SmallCard trackerKey={props.trackerKey} warning={props.warning} />
          <Show when={props.warning}>
            {(warning) => <span class="sr-only">{t(warning())}</span>}
          </Show>
        </Show>
      </div>
      <Tooltip id={tipId} anchor={() => el} placement="top">
        {props.size === 'small' && props.warning ? (
          <span class="flex items-center gap-xs text-warning icon-md">
            <ExclamationTriangleIcon />
            {t(props.warning)}
          </span>
        ) : undefined}
      </Tooltip>
    </>
  );
}
