import { BodyPart } from 'solarxr-protocol';
import { createUniqueId, For } from 'solid-js';
import FullResetIcon from '~icons/slime/full-reset';
import MountingResetIcon from '~icons/slime/mounting-reset';
import YawResetIcon from '~icons/slime/yaw-reset';
import {
  settingField,
  settingRows,
  settingsReset,
} from '@/domain/settings/setting-field';
import { bodyPartLabels } from '@/domain/trackers';
import { cx } from '@/lib/cx';
import { formats, type NumberSpec } from '@/lib/format';
import { t, type MessageId } from '@/lib/l10n';
import type { SettingsIcon } from '@/lib/settings-def';
import { NumberField } from '@/ui/number-field';
import { type SelectOption, Select } from '@/ui/select';
import { ResetButton, SettingRow, useSettingHighlight } from '@/ui/setting-row';
import { SettingsGroup } from '@/ui/settings-group';
import { Toggle } from '@/ui/toggle';
import { resetsSettingsPage } from './resets-def';
import { tapDetectionDefaults, tapDetectionSettings } from './resets-settings';

const TAP_BODY_PARTS = [
  BodyPart.UPPER_CHEST,
  BodyPart.LOWER_CHEST,
  BodyPart.HIP,
  BodyPart.LEFT_UPPER_ARM,
  BodyPart.RIGHT_UPPER_ARM,
  BodyPart.LEFT_UPPER_LEG,
  BodyPart.RIGHT_UPPER_LEG,
  BodyPart.LEFT_LOWER_LEG,
  BodyPart.RIGHT_LOWER_LEG,
  BodyPart.LEFT_FOOT,
  BodyPart.RIGHT_FOOT,
];

const trackerOptions: SelectOption<BodyPart>[] = TAP_BODY_PARTS.map((part) => ({
  value: part,
  label: bodyPartLabels[part],
}));

const { tapResets, tapThreshold } = resetsSettingsPage.groups;
const tapRow = settingRows(tapDetectionSettings, tapDetectionDefaults);

const COUNT: NumberSpec = { maximumFractionDigits: 0 };

const WIDE_COLUMNS =
  '@3xl:grid-cols-[auto_auto_minmax(0,12rem)_minmax(0,1fr)_minmax(0,10rem)_minmax(0,10rem)]';

interface ResetKind {
  row: 'yawReset' | 'fullReset' | 'mountingReset';
  name: MessageId;
  icon: SettingsIcon;
  enabled: 'yawResetEnabled' | 'fullResetEnabled' | 'mountingResetEnabled';
  tracker: 'yawResetTracker' | 'fullResetTracker' | 'mountingResetTracker';
  taps: 'yawResetTaps' | 'fullResetTaps' | 'mountingResetTaps';
  delay: 'yawResetDelay' | 'fullResetDelay' | 'mountingResetDelay';
}

const kinds: ResetKind[] = [
  {
    row: 'yawReset',
    name: 'settings-general-gesture_control-yawReset-name',
    icon: YawResetIcon,
    enabled: 'yawResetEnabled',
    tracker: 'yawResetTracker',
    taps: 'yawResetTaps',
    delay: 'yawResetDelay',
  },
  {
    row: 'fullReset',
    name: 'settings-general-gesture_control-fullReset-name',
    icon: FullResetIcon,
    enabled: 'fullResetEnabled',
    tracker: 'fullResetTracker',
    taps: 'fullResetTaps',
    delay: 'fullResetDelay',
  },
  {
    row: 'mountingReset',
    name: 'settings-general-gesture_control-mountingReset-name',
    icon: MountingResetIcon,
    enabled: 'mountingResetEnabled',
    tracker: 'mountingResetTracker',
    taps: 'mountingResetTaps',
    delay: 'mountingResetDelay',
  },
];

function bindKind(kind: ResetKind) {
  return {
    kind,
    setting: tapResets.rows[kind.row],
    enabled: settingField(tapDetectionSettings, kind.enabled),
    tracker: settingField(tapDetectionSettings, kind.tracker),
    taps: settingField(tapDetectionSettings, kind.taps),
    delay: settingField(tapDetectionSettings, kind.delay),
    reset: settingsReset(tapDetectionSettings, tapDetectionDefaults, [
      kind.enabled,
      kind.tracker,
      kind.taps,
      kind.delay,
    ]),
  };
}

type KindBinding = ReturnType<typeof bindKind>;

const kindRows = kinds.map(bindKind);
const trackers = tapRow(tapThreshold.rows.numberTrackersOverThreshold);

function Caption(props: { id: string; label: MessageId }) {
  return (
    <span id={props.id} class="font-bold @3xl:sr-only">
      {t(props.label)}
    </span>
  );
}

function KindRow(props: { row: KindBinding }) {
  const id = createUniqueId();
  const nameId = `${id}-name`;
  const off = () => !props.row.enabled.value;
  let el!: HTMLDivElement;

  const highlighted = useSettingHighlight(
    () => props.row.setting,
    () => el
  );

  const labelled = (column: string) => `${nameId} ${id}-${column}`;

  return (
    <div
      ref={el}
      class={cx(
        `
          grid grid-cols-[auto_minmax(0,1fr)_minmax(0,1fr)] items-center gap-x-sm gap-y-md py-md
          pr-md pl-xs transition-colors @lg:py-lg @lg:pr-lg @lg:pl-sm @3xl:gap-x-lg
        `,
        WIDE_COLUMNS,
        highlighted() ? 'bg-accent-subtle/30' : 'hover:bg-surface-hover/30'
      )}
    >
      <div class="col-start-1 row-start-1">
        <ResetButton
          reset={props.row.reset}
          disabled={!!props.row.enabled.disabled || !!props.row.enabled.loading}
        />
      </div>
      <div
        class="
          col-span-2 col-start-2 row-start-1 flex min-w-0 items-center justify-between gap-sm
          @3xl:contents
        "
      >
        <label
          for={id}
          id={nameId}
          class="
            flex min-w-0 cursor-pointer items-center gap-sm text-section @3xl:col-start-3
            @3xl:row-start-1
          "
        >
          <props.row.kind.icon class="icon shrink-0 icon-md" />
          <span class="truncate">{t(props.row.kind.name)}</span>
        </label>
        <div class="flex shrink-0 @3xl:col-start-2 @3xl:row-start-1">
          <Toggle {...props.row.enabled} id={id} labelledBy={nameId} />
        </div>
      </div>
      <div
        class="
          col-span-2 col-start-2 flex min-w-0 flex-col gap-xs @3xl:col-span-1 @3xl:col-start-4
          @3xl:row-start-1
        "
      >
        <Caption
          id={`${id}-tracker`}
          label="settings-general-gesture_control-column-tracker"
        />
        <Select
          {...props.row.tracker}
          disabled={props.row.tracker.disabled || off()}
          labelledBy={labelled('tracker')}
          options={trackerOptions}
        />
      </div>
      <div
        class="
          col-span-2 col-start-2 flex min-w-0 flex-col gap-xs @sm:col-span-1 @sm:col-start-2
          @3xl:col-start-5 @3xl:row-start-1
        "
      >
        <Caption
          id={`${id}-taps`}
          label="settings-general-gesture_control-column-taps"
        />
        <NumberField
          {...props.row.taps}
          disabled={props.row.taps.disabled || off()}
          labelledBy={labelled('taps')}
          min={2}
          max={10}
          step={1}
          format={COUNT}
        />
      </div>
      <div
        class="
          col-span-2 col-start-2 flex min-w-0 flex-col gap-xs @sm:col-span-1 @sm:col-start-3
          @3xl:col-start-6 @3xl:row-start-1
        "
      >
        <Caption
          id={`${id}-delay`}
          label="settings-general-gesture_control-column-delay"
        />
        <NumberField
          {...props.row.delay}
          disabled={props.row.delay.disabled || off()}
          labelledBy={labelled('delay')}
          min={0.2}
          max={3}
          step={0.2}
          format={formats.seconds}
        />
      </div>
    </div>
  );
}

function HeaderRow() {
  const column = (label: MessageId) => (
    <span class="text-section uppercase">{t(label)}</span>
  );
  return (
    <div
      aria-hidden="true"
      class={cx(
        'hidden items-center gap-x-lg py-md pr-lg pl-sm @3xl:grid',
        WIDE_COLUMNS
      )}
    >
      <div class="col-span-3 col-start-1">
        {column('settings-general-gesture_control-column-reset')}
      </div>
      <div class="col-start-4">
        {column('settings-general-gesture_control-column-tracker')}
      </div>
      <div class="col-start-5">
        {column('settings-general-gesture_control-column-taps')}
      </div>
      <div class="col-start-6">
        {column('settings-general-gesture_control-column-delay')}
      </div>
    </div>
  );
}

export function TapDetection() {
  return (
    <>
      <SettingsGroup group={tapResets}>
        <HeaderRow />
        <For each={kindRows}>{(row) => <KindRow row={row} />}</For>
      </SettingsGroup>
      <SettingsGroup group={tapThreshold}>
        <SettingRow
          bind={trackers}
          control={(f) => (
            <NumberField {...f} min={1} max={20} step={1} format={COUNT} />
          )}
        />
      </SettingsGroup>
    </>
  );
}
