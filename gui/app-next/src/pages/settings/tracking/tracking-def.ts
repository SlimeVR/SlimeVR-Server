import MartialArtsIcon from '~icons/material-symbols/sports-martial-arts-rounded';
import { defineSettingsPage } from '@/lib/settings-def';

export const trackingSettingsPage = defineSettingsPage({
  path: 'tracking',
  title: 'settings-sidebar-tracking',
  icon: MartialArtsIcon,
  groups: {
    filtering: {
      title: 'settings-general-tracker_mechanics-filtering',
      rows: {
        type: {
          title: 'settings-general-tracker_mechanics-filtering-type',
          description: 'settings-general-tracker_mechanics-filtering-description',
        },
        amount: {
          title: 'settings-general-tracker_mechanics-filtering-amount',
          description:
            'settings-general-tracker_mechanics-filtering-amount-description',
        },
      },
    },
    legFk: {
      title: 'settings-general-fk_settings-leg_fk',
      rows: {
        skatingCorrection: {
          title: 'settings-general-fk_settings-leg_tweak-skating_correction',
          description:
            'settings-general-fk_settings-leg_tweak-skating_correction-description',
        },
        skatingCorrectionStrength: {
          title: 'settings-general-fk_settings-leg_tweak-skating_correction-amount',
          description:
            'settings-general-fk_settings-leg_tweak-skating_correction-amount-description',
        },
        floorClip: {
          title: 'settings-general-fk_settings-leg_tweak-floor_clip',
          description: 'settings-general-fk_settings-leg_tweak-floor_clip-description',
        },
        footPlant: {
          title: 'settings-general-fk_settings-leg_tweak-foot_plant',
          description: 'settings-general-fk_settings-leg_tweak-foot_plant-description',
        },
        toeSnap: {
          title: 'settings-general-fk_settings-leg_tweak-toe_snap',
          description: 'settings-general-fk_settings-leg_tweak-toe_snap-description',
        },
      },
    },
    position: {
      title: 'settings-general-fk_settings-position_group',
      rows: {
        useTrackerPositions: {
          title: 'settings-general-fk_settings-ik',
          description: 'settings-general-fk_settings-ik-use_position-description',
        },
        mocapMode: {
          title: 'settings-general-fk_settings-self_localization-title',
          description: 'settings-general-fk_settings-self_localization-description',
        },
      },
    },
    constraints: {
      title: 'settings-general-fk_settings-constraints_group',
      rows: {
        enforceConstraints: {
          title: 'settings-general-fk_settings-enforce_joint_constraints',
          description:
            'settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description',
        },
      },
    },
    ratios: {
      title: 'settings-general-fk_settings-skeleton_settings-ratios',
      description: 'settings-general-fk_settings-skeleton_settings-ratios-description',
      rows: {
        imputeSpineFromUpperToLower: {
          title:
            'settings-general-fk_settings-skeleton_settings-impute_spine_from_upper_to_lower',
          description:
            'settings-general-fk_settings-skeleton_settings-impute_spine_from_upper_to_lower-description',
        },
        spineCurvature: {
          title: 'settings-general-fk_settings-skeleton_settings-spine_curvature',
          description:
            'settings-general-fk_settings-skeleton_settings-spine_curvature-description',
        },
        interpolateUpperLegsTwistWithLowerLegs: {
          title:
            'settings-general-fk_settings-skeleton_settings-interpolate_upper_legs_twist_with_lower_legs',
          description:
            'settings-general-fk_settings-skeleton_settings-interpolate_upper_legs_twist_with_lower_legs-description',
        },
      },
    },
  },
});
