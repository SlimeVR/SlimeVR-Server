import { ErrorReportingConsent } from 'solarxr-protocol';
import { Show } from 'solid-js';
import { ErrorReportingDetails } from '@/domain/error-reporting/error-reporting-details';
import { errorReportingSettings } from '@/domain/error-reporting';
import { prefRow, settingField } from '@/domain/settings/setting-field';
import { formats } from '@/lib/format';
import { platform } from '@/lib/platform';
import { mapField } from '@/ui/field';
import { NumberField } from '@/ui/number-field';
import { TextPathField } from '@/ui/path-field';
import { controllerMotionPref } from '@/ui/motion';
import { type SettingBinding, SettingRow } from '@/ui/setting-row';
import { SettingsGroup } from '@/ui/settings-group';
import { Toggle } from '@/ui/toggle';
import { SettingsPane } from '../settings-pane';
import { behaviorSettingsPage } from './behavior-def';
import {
  bvhDirectoryPref,
  connectedTrackersWarningPref,
  discordPresencePref,
  feedbackSoundPref,
  feedbackSoundVolumePref,
  useTrayPref,
} from './behavior-prefs';

const { notifications, desktop, input, privacy } = behaviorSettingsPage.groups;

const feedbackSound = prefRow(
  notifications.rows.feedbackSound,
  feedbackSoundPref
);
const feedbackVolume = prefRow(
  notifications.rows.feedbackSoundVolume,
  feedbackSoundVolumePref
);
const trackersWarning = prefRow(
  notifications.rows.connectedTrackersWarning,
  connectedTrackersWarningPref
);
const discordPresence = prefRow(
  desktop.rows.discordPresence,
  discordPresencePref
);
const bvhDirectory = prefRow(desktop.rows.bvhDirectory, bvhDirectoryPref);
const controllerNav = prefRow(input.rows.controllerNav, controllerMotionPref);

const trayRow = prefRow(desktop.rows.useTray, useTrayPref);
const useTray: SettingBinding<boolean> = {
  ...trayRow,
  field: mapField(
    trayRow.field,
    (tray) => tray ?? false,
    (tray) => tray
  ),
};

const errorTracking: SettingBinding<boolean> = {
  setting: privacy.rows.errorTracking,
  field: mapField(
    settingField(errorReportingSettings, 'consent'),
    (consent) => consent === ErrorReportingConsent.ALLOWED,
    (allowed) =>
      allowed ? ErrorReportingConsent.ALLOWED : ErrorReportingConsent.DENIED
  ),
};

function ToggleRow(props: { bind: SettingBinding<boolean> }) {
  return (
    <SettingRow
      bind={props.bind}
      pressable
      control={(f) => <Toggle {...f} />}
    />
  );
}

export function BehaviorPage() {
  return (
    <SettingsPane page={behaviorSettingsPage}>
      <SettingsGroup group={notifications}>
        <SettingRow
          bind={feedbackSound}
          pressable
          childrenDisabled={!feedbackSound.field.value}
          control={(f) => <Toggle {...f} />}
        >
          <SettingRow
            bind={feedbackVolume}
            control={(f) => (
              <NumberField
                {...f}
                min={0.1}
                max={1}
                step={0.1}
                format={formats.percent}
              />
            )}
          />
        </SettingRow>
        <ToggleRow bind={trackersWarning} />
      </SettingsGroup>

      <SettingsGroup group={desktop}>
        <Show when={platform.window}>
          <ToggleRow bind={useTray} />
        </Show>
        <Show when={platform.discordPresence}>
          <ToggleRow bind={discordPresence} />
        </Show>
        <Show when={platform.pickFile}>
          <SettingRow
            bind={bvhDirectory}
            control={(f) => (
              <TextPathField
                {...f}
                mode="directory"
                placeholder="settings-interface-behavior-bvh_directory-choose"
                title="settings-interface-behavior-bvh_directory-label"
              />
            )}
          />
        </Show>
      </SettingsGroup>

      <SettingsGroup group={input}>
        <ToggleRow bind={controllerNav} />
      </SettingsGroup>

      <SettingsGroup group={privacy}>
        <SettingRow
          bind={errorTracking}
          pressable
          details={<ErrorReportingDetails />}
          control={(f) => <Toggle {...f} />}
        />
      </SettingsGroup>
    </SettingsPane>
  );
}
