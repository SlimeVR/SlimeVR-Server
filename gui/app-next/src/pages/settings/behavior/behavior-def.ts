import ArrowRightLeftIcon from '~icons/heroicons/arrows-right-left';
import { platform } from '@/lib/platform';
import { defineSettingsPage } from '@/lib/settings-def';

export const behaviorSettingsPage = defineSettingsPage({
  path: 'behavior',
  title: 'settings-sidebar-behavior',
  icon: ArrowRightLeftIcon,
  groups: {
    notifications: {
      title: 'settings-interface-notifications',
      rows: {
        feedbackSound: {
          title: 'settings-general-interface-feedback_sound',
          description: 'settings-general-interface-feedback_sound-description',
        },
        feedbackSoundVolume: {
          title: 'settings-general-interface-feedback_sound-volume',
          description: 'settings-general-interface-feedback_sound-volume-description',
        },
        connectedTrackersWarning: {
          title: 'settings-general-interface-connected_trackers_warning',
          description:
            'settings-general-interface-connected_trackers_warning-description',
        },
      },
    },
    desktop: {
      title: 'settings-interface-behavior-desktop_group',
      rows: {
        useTray: {
          title: 'settings-general-interface-use_tray',
          description: 'settings-general-interface-use_tray-description',
          available: () => !!platform.window,
        },
        discordPresence: {
          title: 'settings-general-interface-discord_presence',
          description: 'settings-general-interface-discord_presence-description',
          available: () => !!platform.discordPresence,
        },
        bvhDirectory: {
          title: 'settings-interface-behavior-bvh_directory',
          description: 'settings-interface-behavior-bvh_directory-description',
          available: () => !!platform.pickFile,
        },
      },
    },
    input: {
      title: 'settings-interface-behavior-input_group',
      rows: {
        controllerNav: {
          title: 'settings-interface-behavior-controller_nav',
          description: 'settings-interface-behavior-controller_nav-description',
        },
      },
    },
    privacy: {
      title: 'settings-interface-behavior-privacy_group',
      rows: {
        errorTracking: {
          title: 'settings-interface-behavior-error_tracking-label',
          description: 'settings-interface-behavior-error_tracking-summary',
        },
      },
    },
  },
});
