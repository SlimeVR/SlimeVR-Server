import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import {
  SettingsAdvanced,
  SettingsGroup,
} from '@/components/settings/SettingRow';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcon';
import { MagnetometerToggleSetting } from './components/MagnetometerToggleSetting';
import { TimeoutSettings } from './components/TimeoutSettings';
import { HIDSettings } from './components/HIDSettings';

export function TrackersSettings() {
  return (
    <SettingsPagePaneLayout
      icon={<WrenchIcon />}
      title="settings-sidebar-trackers"
    >
      <div className="flex flex-col">
        <SettingsGroup title="settings-general-tracker_mechanics-magnetometer_group">
          <MagnetometerToggleSetting settingType="general" />
        </SettingsGroup>

        <SettingsAdvanced>
          <SettingsGroup title="settings-general-tracker_mechanics-connection_group">
            <TimeoutSettings />
            <HIDSettings />
          </SettingsGroup>
        </SettingsAdvanced>
      </div>
    </SettingsPagePaneLayout>
  );
}
