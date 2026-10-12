import { defineSettingsPage } from '@/components/settings/settings-definitions';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import {
  SettingsAdvanced,
  SettingsGroup,
} from '@/components/settings/SettingRow';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcon';
import { MagnetometerToggleSetting } from './components/MagnetometerToggleSetting';
import { TimeoutSettings } from './components/TimeoutSettings';
import { HIDSettings } from './components/HIDSettings';

export const trackersSettingsPage = defineSettingsPage({
  path: 'trackers',
  title: 'settings-sidebar-trackers',
  groups: {
    magnetometer: {
      title: 'settings-general-tracker_mechanics-magnetometer_group',
      rows: {
        useMagOnAllTrackers: {
          title: 'settings-general-tracker_mechanics-use_mag_on_all_trackers',
          description:
            'settings-general-tracker_mechanics-use_mag_on_all_trackers-description',
        },
      },
    },
    connection: {
      title: 'settings-general-tracker_mechanics-connection_group',
      rows: {
        timeoutDelay: {
          title: 'settings-general-tracker_mechanics-timeout_delay',
          description:
            'settings-general-tracker_mechanics-timeout_delay-description',
        },
        trackersOverUsb: {
          title: 'settings-general-tracker_mechanics-trackers_over_usb',
          description:
            'settings-general-tracker_mechanics-trackers_over_usb-description',
        },
      },
    },
  },
});

const { groups } = trackersSettingsPage;

export function TrackersSettings() {
  return (
    <SettingsPagePaneLayout
      icon={<WrenchIcon />}
      title={trackersSettingsPage.title}
    >
      <div className="flex flex-col">
        <SettingsGroup group={groups.magnetometer}>
          <MagnetometerToggleSetting
            settingType="general"
            setting={groups.magnetometer.rows.useMagOnAllTrackers}
          />
        </SettingsGroup>

        <SettingsAdvanced>
          <SettingsGroup group={groups.connection}>
            <TimeoutSettings setting={groups.connection.rows.timeoutDelay} />
            <HIDSettings setting={groups.connection.rows.trackersOverUsb} />
          </SettingsGroup>
        </SettingsAdvanced>
      </div>
    </SettingsPagePaneLayout>
  );
}
