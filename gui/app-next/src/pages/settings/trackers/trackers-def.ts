import WrenchIcon from '~icons/material-symbols/build-rounded';
import { defineSettingsPage } from '@/lib/settings-def';

export const trackersSettingsPage = defineSettingsPage({
  path: 'trackers',
  title: 'settings-sidebar-trackers',
  icon: WrenchIcon,
  groups: {
    magnetometer: {
      title: 'settings-general-tracker_mechanics-magnetometer_group',
      rows: {
        enable: {
          title: 'settings-general-tracker_mechanics-use_mag_on_all_trackers',
        },
      },
    },
    connection: {
      title: 'settings-general-tracker_mechanics-connection_group',
      rows: {
        delay: { title: 'settings-general-tracker_mechanics-timeout_delay' },
        trackersOverHid: {
          title: 'settings-general-tracker_mechanics-trackers_over_usb',
        },
      },
    },
  },
});
