import { useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { FieldPath, useForm } from 'react-hook-form';
import {
  ChangeSkeletonSettingsRequestT,
  FilteringType,
  RpcMessage,
  SkeletonFilteringT,
  SkeletonRatiosT,
  SkeletonSettingsRequestT,
  SkeletonSettingsResponseT,
  SkeletonTogglesT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useSettingsDefaults } from '@/hooks/settings-defaults';
import { useLocaleConfig } from '@/i18n/config';
import { CheckBox } from '@/components/commons/Checkbox';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { Radio } from '@/components/commons/Radio';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { settingReset } from '@/components/settings/SettingReset';
import {
  SettingRow,
  SettingsAdvanced,
  SettingsGroup,
} from '@/components/settings/SettingRow';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';
import { MartialArtsIcon } from '@/components/commons/icon/MartialArtsIcon';
import { defineSettingsPage } from '@/components/settings/settings-definitions';

type SkeletonForm = {
  toggles: {
    floorClip: boolean;
    skatingCorrection: boolean;
    toeSnap: boolean;
    footPlant: boolean;
    mocapMode: boolean;
    useTrackerPositions: boolean;
    enforceConstraints: boolean;
  };
  ratios: {
    imputeSpineFromUpperToLower: number;
    spineCurvature: number;
    interpolateUpperLegsTwistWithLowerLegs: number;
    skatingCorrectionStrength: number;
  };
  filtering: {
    type: number;
    amount: number;
  };
};

// Exported so other components (e.g. StayAlignedSettings' debug copy) can
// read the current skeleton toggles/filtering without re-requesting them.
export const skeletonSettingsAtom = atom<SkeletonSettingsResponseT | null>(
  null
);
const skeletonSettingsValueAtom = selectAtom(
  skeletonSettingsAtom,
  (settings) => settings,
  isEqual
);

function toForm(settings: SkeletonSettingsResponseT): SkeletonForm {
  const { toggles, ratios, filtering } = settings;
  return {
    toggles: {
      floorClip: toggles?.floorClip ?? false,
      skatingCorrection: toggles?.skatingCorrection ?? false,
      toeSnap: toggles?.toeSnap ?? false,
      footPlant: toggles?.footPlant ?? false,
      mocapMode: toggles?.mocapMode ?? false,
      useTrackerPositions: toggles?.useTrackerPositions ?? false,
      enforceConstraints: toggles?.enforceConstraints ?? false,
    },
    ratios: {
      imputeSpineFromUpperToLower: ratios?.imputeSpineFromUpperToLower ?? 0,
      spineCurvature: ratios?.spineCurvature ?? 0,
      interpolateUpperLegsTwistWithLowerLegs:
        ratios?.interpolateUpperLegsTwistWithLowerLegs ?? 0,
      skatingCorrectionStrength: ratios?.skatingCorrectionStrength ?? 0,
    },
    filtering: {
      type: filtering?.type ?? FilteringType.NONE,
      amount: filtering?.amount ?? 0,
    },
  };
}

export const trackingSettingsPage = defineSettingsPage({
  path: 'tracking',
  title: 'settings-sidebar-tracking',
  groups: {
    filtering: {
      title: 'settings-general-tracker_mechanics-filtering',
      rows: {
        type: {
          title: 'settings-general-tracker_mechanics-filtering-type',
          description:
            'settings-general-tracker_mechanics-filtering-description',
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
        skatingCorrectionAmount: {
          title:
            'settings-general-fk_settings-leg_tweak-skating_correction-amount',
          description:
            'settings-general-fk_settings-leg_tweak-skating_correction-amount-description',
        },
        floorClip: {
          title: 'settings-general-fk_settings-leg_tweak-floor_clip',
          description:
            'settings-general-fk_settings-leg_tweak-floor_clip-description',
        },
        footPlant: {
          title: 'settings-general-fk_settings-leg_tweak-foot_plant',
          description:
            'settings-general-fk_settings-leg_tweak-foot_plant-description',
        },
        toeSnap: {
          title: 'settings-general-fk_settings-leg_tweak-toe_snap',
          description:
            'settings-general-fk_settings-leg_tweak-toe_snap-description',
        },
      },
    },
    position: {
      title: 'settings-general-fk_settings-position_group',
      rows: {
        ik: {
          title: 'settings-general-fk_settings-ik',
          description:
            'settings-general-fk_settings-ik-use_position-description',
        },
        selfLocalization: {
          title: 'settings-general-fk_settings-self_localization-title',
          description:
            'settings-general-fk_settings-self_localization-description',
        },
      },
    },
    constraints: {
      title: 'settings-general-fk_settings-constraints_group',
      rows: {
        enforceJointConstraints: {
          title: 'settings-general-fk_settings-enforce_joint_constraints',
          description:
            'settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description',
        },
      },
    },
    ratios: {
      title: 'settings-general-fk_settings-skeleton_settings-ratios',
      description:
        'settings-general-fk_settings-skeleton_settings-ratios-description',
      rows: {
        imputeSpineFromUpperToLower: {
          title:
            'settings-general-fk_settings-skeleton_settings-impute_spine_from_upper_to_lower',
          description:
            'settings-general-fk_settings-skeleton_settings-impute_spine_from_upper_to_lower-description',
        },
        spineCurvature: {
          title:
            'settings-general-fk_settings-skeleton_settings-spine_curvature',
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

const { groups } = trackingSettingsPage;

export function TrackingSettings() {
  const setSettings = useSetAtom(skeletonSettingsAtom);
  const settings = useAtomValue(skeletonSettingsValueAtom);
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const defaults = useSettingsDefaults().skeleton;

  const percentageFormat = new Intl.NumberFormat(currentLocales, {
    style: 'percent',
    maximumFractionDigits: 0,
  });

  const { control, watch, handleSubmit, reset, setValue } =
    useForm<SkeletonForm>({
      defaultValues: toForm(defaults),
      mode: 'onChange',
      reValidateMode: 'onChange',
    });

  const onSubmit = (values: SkeletonForm) => {
    const settingsReq = new ChangeSkeletonSettingsRequestT();

    const toggles = new SkeletonTogglesT();
    toggles.floorClip = values.toggles.floorClip;
    toggles.skatingCorrection = values.toggles.skatingCorrection;
    toggles.toeSnap = values.toggles.toeSnap;
    toggles.footPlant = values.toggles.footPlant;
    toggles.mocapMode = values.toggles.mocapMode;
    toggles.useTrackerPositions = values.toggles.useTrackerPositions;
    toggles.enforceConstraints = values.toggles.enforceConstraints;
    settingsReq.toggles = toggles;

    const ratios = new SkeletonRatiosT();
    ratios.imputeSpineFromUpperToLower =
      values.ratios.imputeSpineFromUpperToLower;
    ratios.spineCurvature = values.ratios.spineCurvature;
    ratios.interpolateUpperLegsTwistWithLowerLegs =
      values.ratios.interpolateUpperLegsTwistWithLowerLegs;
    ratios.skatingCorrectionStrength = values.ratios.skatingCorrectionStrength;
    settingsReq.ratios = ratios;

    const filtering = new SkeletonFilteringT();
    filtering.type = values.filtering.type;
    filtering.amount = values.filtering.amount;
    settingsReq.filtering = filtering;

    sendRPCPacket(RpcMessage.ChangeSkeletonSettingsRequest, settingsReq);
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useConnectedEffect(() => {
    sendRPCPacket(
      RpcMessage.SkeletonSettingsRequest,
      new SkeletonSettingsRequestT()
    );
  }, []);

  useEffect(() => {
    if (!settings) return;
    reset(toForm(settings));
  }, [settings]);

  useRPCPacket(
    RpcMessage.SkeletonSettingsResponse,
    (settings: SkeletonSettingsResponseT) => {
      setSettings(settings);
    }
  );

  const filteringEnabled = watch('filtering.type') !== FilteringType.NONE;
  const skatingCorrection = watch('toggles.skatingCorrection');

  const resetTo = (name: FieldPath<SkeletonForm>) =>
    settingReset<SkeletonForm, FieldPath<SkeletonForm>>(
      name.split('.').reduce<any>((value, key) => value?.[key], defaults) ??
        undefined,
      watch(name),
      (next) => {
        setValue(name, next);
        handleSubmit(onSubmit)();
      }
    );

  const stepper = (name: FieldPath<SkeletonForm>, min = 0.0) => ({
    ...resetTo(name),
    controlWidth: 'wide' as const,
    control: (
      <NumberSelector
        control={control}
        name={name}
        valueLabelFormat={(value) => percentageFormat.format(value)}
        min={min}
        max={1.0}
        step={0.05}
      />
    ),
  });

  const toggle = (name: FieldPath<SkeletonForm>) => ({
    ...resetTo(name),
    controlId: `tracking-${name}`,
    control: (
      <CheckBox
        variant="toggle"
        control={control}
        name={name}
        id={`tracking-${name}`}
      />
    ),
  });

  return (
    <SettingsPagePaneLayout
      icon={<MartialArtsIcon />}
      title={trackingSettingsPage.title}
    >
      <div className="flex flex-col">
        <SettingsGroup group={groups.filtering}>
          <SettingRow
            setting={groups.filtering.rows.type}
            {...resetTo('filtering.type')}
            childrenDisabled={!filteringEnabled}
            below={
              <div className="flex md:flex-row flex-col gap-3">
                <Radio
                  control={control}
                  name="filtering.type"
                  label={l10n.getString(
                    'settings-general-tracker_mechanics-filtering-type-none'
                  )}
                  description={l10n.getString(
                    'settings-general-tracker_mechanics-filtering-type-none-description'
                  )}
                  value={FilteringType.NONE}
                />
                <Radio
                  control={control}
                  name="filtering.type"
                  label={l10n.getString(
                    'settings-general-tracker_mechanics-filtering-type-smoothing'
                  )}
                  description={l10n.getString(
                    'settings-general-tracker_mechanics-filtering-type-smoothing-description'
                  )}
                  value={FilteringType.SMOOTHING}
                />
                <Radio
                  control={control}
                  name="filtering.type"
                  label={l10n.getString(
                    'settings-general-tracker_mechanics-filtering-type-prediction'
                  )}
                  description={l10n.getString(
                    'settings-general-tracker_mechanics-filtering-type-prediction-description'
                  )}
                  value={FilteringType.PREDICTION}
                />
              </div>
            }
          >
            <SettingRow
              setting={groups.filtering.rows.amount}
              {...stepper('filtering.amount')}
            />
          </SettingRow>
        </SettingsGroup>

        <SettingsGroup group={groups.legFk}>
          <SettingRow
            setting={groups.legFk.rows.skatingCorrection}
            childrenDisabled={!skatingCorrection}
            {...toggle('toggles.skatingCorrection')}
          >
            <SettingRow
              setting={groups.legFk.rows.skatingCorrectionAmount}
              {...stepper('ratios.skatingCorrectionStrength', 0.05)}
            />
          </SettingRow>
          <SettingRow
            setting={groups.legFk.rows.floorClip}
            {...toggle('toggles.floorClip')}
          />
          <SettingRow
            setting={groups.legFk.rows.footPlant}
            {...toggle('toggles.footPlant')}
          />
          <SettingRow
            setting={groups.legFk.rows.toeSnap}
            {...toggle('toggles.toeSnap')}
          />
        </SettingsGroup>

        <SettingsGroup group={groups.position}>
          <SettingRow
            setting={groups.position.rows.ik}
            {...toggle('toggles.useTrackerPositions')}
          />
          <SettingRow
            setting={groups.position.rows.selfLocalization}
            {...toggle('toggles.mocapMode')}
          />
        </SettingsGroup>

        <SettingsAdvanced>
          <SettingsGroup group={groups.constraints}>
            <SettingRow
              setting={groups.constraints.rows.enforceJointConstraints}
              {...toggle('toggles.enforceConstraints')}
            />
          </SettingsGroup>

          <SettingsGroup group={groups.ratios}>
            <SettingRow
              setting={groups.ratios.rows.imputeSpineFromUpperToLower}
              {...stepper('ratios.imputeSpineFromUpperToLower')}
            />
            <SettingRow
              setting={groups.ratios.rows.spineCurvature}
              {...stepper('ratios.spineCurvature')}
            />
            <SettingRow
              setting={
                groups.ratios.rows.interpolateUpperLegsTwistWithLowerLegs
              }
              {...stepper('ratios.interpolateUpperLegsTwistWithLowerLegs')}
            />
          </SettingsGroup>
        </SettingsAdvanced>
      </div>
    </SettingsPagePaneLayout>
  );
}
