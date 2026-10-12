import { useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { DefaultValues, FieldPath, useForm } from 'react-hook-form';
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
import { useLocaleConfig } from '@/i18n/config';
import { CheckBox } from '@/components/commons/Checkbox';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import {
  SettingRow,
  SettingsAdvanced,
  SettingsGroup,
} from '@/components/settings/SettingRow';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';
import { MartialArtsIcon } from '@/components/commons/icon/MartialArtsIcon';

type SkeletonForm = {
  toggles: {
    floorClip: boolean;
    skatingCorrection: boolean;
    toeSnap: boolean;
    footPlant: boolean;
    mocapMode: boolean;
    useTrackerPositions: boolean;
    enforceConstraints: boolean;
    correctConstraints: boolean;
  };
  ratios: {
    imputeSpineFromUpperToLower: number;
    imputeSpineCurvature: number;
    interpolateHipWithUpperLegs: number;
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
export const skeletonSettingsAtom = atom(new SkeletonSettingsResponseT());
const skeletonSettingsValueAtom = selectAtom(
  skeletonSettingsAtom,
  (settings) => settings,
  isEqual
);

const defaultValues: SkeletonForm = {
  toggles: {
    floorClip: false,
    skatingCorrection: false,
    toeSnap: false,
    footPlant: false,
    mocapMode: false,
    useTrackerPositions: false,
    enforceConstraints: false,
    correctConstraints: false,
  },
  ratios: {
    imputeSpineFromUpperToLower: 0.0,
    imputeSpineCurvature: 0.0,
    interpolateHipWithUpperLegs: 0.0,
    interpolateUpperLegsTwistWithLowerLegs: 0.0,
    skatingCorrectionStrength: 0.0,
  },
  filtering: { amount: 0.1, type: FilteringType.NONE },
};

export function TrackingSettings() {
  const setSettings = useSetAtom(skeletonSettingsAtom);
  const settings = useAtomValue(skeletonSettingsValueAtom);
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const percentageFormat = new Intl.NumberFormat(currentLocales, {
    style: 'percent',
    maximumFractionDigits: 0,
  });

  const { control, watch, handleSubmit, getValues, reset } =
    useForm<SkeletonForm>({
      defaultValues,
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
    ratios.imputeSpineCurvature = values.ratios.imputeSpineCurvature;
    ratios.interpolateHipWithUpperLegs =
      values.ratios.interpolateHipWithUpperLegs;
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

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.SkeletonSettingsRequest,
      new SkeletonSettingsRequestT()
    );
  }, []);

  useEffect(() => {
    const formData: DefaultValues<SkeletonForm> = {};

    if (settings.toggles) {
      formData.toggles = Object.keys(settings.toggles).reduce(
        (curr, key: string) => ({
          ...curr,
          [key]: (settings.toggles && (settings.toggles as any)[key]) ?? false,
        }),
        {}
      );
    }

    if (settings.ratios) {
      formData.ratios = Object.keys(settings.ratios).reduce(
        (curr, key: string) => ({
          ...curr,
          [key]: (settings.ratios && (settings.ratios as any)[key]) ?? 0.0,
        }),
        {}
      );
    }

    if (settings.filtering) {
      formData.filtering = {
        type: settings.filtering.type,
        amount: settings.filtering.amount,
      };
    }

    reset({ ...getValues(), ...formData });
  }, [settings]);

  useRPCPacket(
    RpcMessage.SkeletonSettingsResponse,
    (settings: SkeletonSettingsResponseT) => {
      setSettings(settings);
    }
  );

  const filteringEnabled = watch('filtering.type') !== FilteringType.NONE;
  const skatingCorrection = watch('toggles.skatingCorrection');

  const stepper = (name: FieldPath<SkeletonForm>, min = 0.0) => ({
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
      title="settings-sidebar-tracking"
    >
      <div className="flex flex-col">
        <SettingsGroup
          title="settings-general-tracker_mechanics-filtering"
          details={l10n
            .getString(
              'settings-general-tracker_mechanics-filtering-description'
            )
            .split('\n')
            .map((line, i) => (
              <Typography key={i}>{line}</Typography>
            ))}
        >
          <div className="flex md:flex-row flex-col gap-3 p-4">
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
          {filteringEnabled && (
            <SettingRow
              title="settings-general-tracker_mechanics-filtering-amount"
              {...stepper('filtering.amount')}
            />
          )}
        </SettingsGroup>

        <SettingsGroup title="settings-general-fk_settings-leg_fk">
          <SettingRow
            title="settings-general-fk_settings-leg_tweak-floor_clip"
            description="settings-general-fk_settings-leg_tweak-floor_clip-description"
            {...toggle('toggles.floorClip')}
          />
          <SettingRow
            title="settings-general-fk_settings-leg_tweak-foot_plant"
            description="settings-general-fk_settings-leg_tweak-foot_plant-description"
            {...toggle('toggles.footPlant')}
          />
          <SettingRow
            title="settings-general-fk_settings-leg_tweak-toe_snap"
            description="settings-general-fk_settings-leg_tweak-toe_snap-description"
            {...toggle('toggles.toeSnap')}
          />
          <SettingRow
            title="settings-general-fk_settings-leg_tweak-skating_correction"
            description="settings-general-fk_settings-leg_tweak-skating_correction-description"
            childrenDisabled={!skatingCorrection}
            {...toggle('toggles.skatingCorrection')}
          >
            <SettingRow
              title="settings-general-fk_settings-leg_tweak-skating_correction-amount"
              {...stepper('ratios.skatingCorrectionStrength', 0.1)}
            />
          </SettingRow>
        </SettingsGroup>

        <SettingsGroup title="settings-general-fk_settings-position_group">
          <SettingRow
            title="settings-general-fk_settings-ik"
            description="settings-general-fk_settings-ik-use_position-description"
            {...toggle('toggles.useTrackerPositions')}
          />
          <SettingRow
            title="settings-general-fk_settings-self_localization-title"
            description="settings-general-fk_settings-self_localization-description"
            {...toggle('toggles.mocapMode')}
          />
        </SettingsGroup>

        <SettingsAdvanced>
          <SettingsGroup title="settings-general-fk_settings-constraints_group">
            <SettingRow
              title="settings-general-fk_settings-enforce_joint_constraints"
              description="settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description"
              {...toggle('toggles.enforceConstraints')}
            />
          </SettingsGroup>

          <SettingsGroup
            title="settings-general-fk_settings-skeleton_settings-ratios"
            description="settings-general-fk_settings-skeleton_settings-ratios-description"
          >
            <SettingRow
              title="settings-general-fk_settings-skeleton_settings-impute_spine_from_upper_to_lower"
              {...stepper('ratios.imputeSpineFromUpperToLower')}
            />
            <SettingRow
              title="settings-general-fk_settings-skeleton_settings-impute_spine_curvature"
              {...stepper('ratios.imputeSpineCurvature')}
            />
            <SettingRow
              title="settings-general-fk_settings-skeleton_settings-interpolate_hip_with_upper_legs"
              {...stepper('ratios.interpolateHipWithUpperLegs')}
            />
            <SettingRow
              title="settings-general-fk_settings-skeleton_settings-interpolate_upper_legs_twist_with_lower_legs"
              {...stepper('ratios.interpolateUpperLegsTwistWithLowerLegs')}
            />
          </SettingsGroup>
        </SettingsAdvanced>
      </div>
    </SettingsPagePaneLayout>
  );
}
