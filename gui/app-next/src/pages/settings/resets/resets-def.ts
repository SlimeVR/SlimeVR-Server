import FullResetIcon from '~icons/slime/full-reset';
import { defineSettingsPage } from '@/lib/settings-def';

export const resetsSettingsPage = defineSettingsPage({
  path: 'resets',
  title: 'settings-sidebar-resets',
  icon: FullResetIcon,
  groups: {
    main: {
      rows: {
        mountingMethod: {
          title: 'settings-general-mounting_method',
          description: 'settings-general-mounting_method-description',
        },
        armsMountingResetMode: {
          title: 'settings-general-fk_settings-arms_mounting_reset_mode',
          description:
            'settings-general-fk_settings-arms_mounting_reset_mode-description',
        },
        resetMountingFeet: {
          title: 'settings-general-fk_settings-leg_fk-reset_mounting_feet-v1',
          description:
            'settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1',
        },
      },
    },
    tapResets: {
      title: 'settings-general-gesture_control-subtitle',
      description: 'settings-general-gesture_control-description',
      rows: {
        yawReset: { title: 'settings-general-gesture_control-yawResetEnabled' },
        fullReset: {
          title: 'settings-general-gesture_control-fullResetEnabled',
        },
        mountingReset: {
          title: 'settings-general-gesture_control-mountingResetEnabled',
        },
      },
    },
    tapThreshold: {
      rows: {
        numberTrackersOverThreshold: {
          title: 'settings-general-gesture_control-numberTrackersOverThreshold',
          description:
            'settings-general-gesture_control-numberTrackersOverThreshold-description',
        },
      },
    },
    resetBehaviour: {
      title: 'settings-general-fk_settings-reset_behaviour_group',
      rows: {
        yawResetSmoothTime: {
          title: 'settings-general-tracker_mechanics-yaw-reset-smooth-time',
          description:
            'settings-general-tracker_mechanics-yaw-reset-smooth-time-description',
        },
        resetHmdAttitude: {
          title:
            'settings-general-fk_settings-reset_settings-reset_reliable_reference_attitude',
          description:
            'settings-general-fk_settings-reset_settings-reset_reliable_reference_attitude-description',
        },
        saveMountingReset: {
          title: 'settings-general-tracker_mechanics-save_mounting_reset',
        },
      },
    },
  },
});
