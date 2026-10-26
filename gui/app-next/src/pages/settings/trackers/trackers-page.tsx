import { settingRows } from '@/domain/settings/setting-field';
import { formats } from '@/lib/format';
import { Localized } from '@/ui/localized';
import { NumberField } from '@/ui/number-field';
import { SettingRow } from '@/ui/setting-row';
import { SettingsAdvanced, SettingsGroup } from '@/ui/settings-group';
import { Toggle } from '@/ui/toggle';
import { SettingsPane } from '../settings-pane';
import { trackersSettingsPage } from './trackers-def';
import {
  globalMagDefaults,
  globalMagSettings,
  hidDefaults,
  hidSettings,
  timeoutDefaults,
  timeoutSettings,
} from './trackers-settings';

const { magnetometer, connection } = trackersSettingsPage.groups;

const magRow = settingRows(globalMagSettings, globalMagDefaults);
const timeoutRow = settingRows(timeoutSettings, timeoutDefaults);
const hidRow = settingRows(hidSettings, hidDefaults);

const bold = { b: (text: string) => <b>{text}</b> };

export function TrackersPage() {
  const mag = magRow(magnetometer.rows.enable);
  const delay = timeoutRow(connection.rows.delay);
  const overHid = hidRow(connection.rows.trackersOverHid);

  return (
    <SettingsPane page={trackersSettingsPage}>
      <SettingsGroup group={magnetometer}>
        <SettingRow
          bind={mag}
          pressable
          details={
            <p class="whitespace-pre-line">
              <Localized
                id="settings-general-tracker_mechanics-use_mag_on_all_trackers-description"
                elems={bold}
              />
            </p>
          }
          control={(f) => <Toggle {...f} />}
        />
      </SettingsGroup>

      <SettingsAdvanced>
        <SettingsGroup group={connection}>
          <SettingRow
            bind={delay}
            details={
              <p>
                <Localized
                  id="settings-general-tracker_mechanics-timeout_delay-description"
                  elems={bold}
                />
              </p>
            }
            control={(f) => (
              <NumberField
                {...f}
                min={0}
                max={3600}
                step={1}
                format={formats.seconds}
              />
            )}
          />
          <SettingRow
            bind={overHid}
            pressable
            details={
              <p>
                <Localized
                  id="settings-general-tracker_mechanics-trackers_over_usb-description"
                  elems={bold}
                />
              </p>
            }
            control={(f) => <Toggle {...f} />}
          />
        </SettingsGroup>
      </SettingsAdvanced>
    </SettingsPane>
  );
}
