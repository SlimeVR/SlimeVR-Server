import { useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
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
import { WrenchIcon } from '@/components/commons/icon/WrenchIcon';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPageLayout';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';

type SkeletonForm = {
  toggles: {
    forceArmsFromHmd: boolean;
    floorClip: boolean;
    skatingCorrection: boolean;
    toeSnap: boolean;
    footPlant: boolean;
    selfLocalization: boolean;
    usePosition: boolean;
    enforceConstraints: boolean;
    correctConstraints: boolean;
  };
  ratios: {
    imputeSpineFromUpperLower: number;
    imputeSpineCurvature: number;
    interpHipLegs: number;
    interpKneeTrackerAnkle: number;
    interpKneeAnkle: number;
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
    forceArmsFromHmd: false,
    floorClip: false,
    skatingCorrection: false,
    toeSnap: false,
    footPlant: true,
    selfLocalization: false,
    usePosition: true,
    enforceConstraints: true,
    correctConstraints: true,
  },
  ratios: {
    imputeSpineFromUpperLower: 0.5,
    imputeSpineCurvature: 0.5,
    interpHipLegs: 0.25,
    interpKneeTrackerAnkle: 0.85,
    interpKneeAnkle: 0.2,
    skatingCorrectionStrength: 0.3,
  },
  filtering: { amount: 0.1, type: FilteringType.NONE },
};

export function SkeletonSettings() {
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
    toggles.forceArmsFromHmd = values.toggles.forceArmsFromHmd;
    toggles.toeSnap = values.toggles.toeSnap;
    toggles.footPlant = values.toggles.footPlant;
    toggles.selfLocalization = values.toggles.selfLocalization;
    toggles.usePosition = values.toggles.usePosition;
    toggles.enforceConstraints = values.toggles.enforceConstraints;
    toggles.correctConstraints = values.toggles.correctConstraints;
    settingsReq.toggles = toggles;

    const ratios = new SkeletonRatiosT();
    ratios.imputeSpineFromUpperLower = values.ratios.imputeSpineFromUpperLower;
    ratios.imputeSpineCurvature = values.ratios.imputeSpineCurvature;
    ratios.interpHipLegs = values.ratios.interpHipLegs;
    ratios.interpKneeTrackerAnkle = values.ratios.interpKneeTrackerAnkle;
    ratios.interpKneeAnkle = values.ratios.interpKneeAnkle;
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
          [key]: (settings.toggles && (settings.toggles as any)[key]) || false,
        }),
        {}
      );
    }

    if (settings.ratios) {
      formData.ratios = Object.keys(settings.ratios).reduce(
        (curr, key: string) => ({
          ...curr,
          [key]: (settings.ratios && (settings.ratios as any)[key]) || 0.0,
        }),
        {}
      );
    }

    if (settings.filtering) {
      formData.filtering = {
        type: settings.filtering.type ?? defaultValues.filtering.type,
        amount: settings.filtering.amount ?? defaultValues.filtering.amount,
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

  return (
    <SettingsPagePaneLayout icon={<WrenchIcon />} id="fksettings">
      <>
        <Typography variant="main-title">
          {l10n.getString('settings-general-fk_settings')}
        </Typography>

        <Typography variant="section-title">
          {l10n.getString('settings-general-tracker_mechanics-filtering')}
        </Typography>
        <div className="flex flex-col pt-2 pb-4">
          {l10n
            .getString(
              'settings-general-tracker_mechanics-filtering-description'
            )
            .split('\n')
            .map((line, i) => (
              <Typography key={i}>{line}</Typography>
            ))}
        </div>
        <Typography>
          {l10n.getString('settings-general-tracker_mechanics-filtering-type')}
        </Typography>
        <div className="flex md:flex-row flex-col gap-3 pt-2">
          <Radio
            control={control}
            name="filtering.type"
            label={l10n.getString(
              'settings-general-tracker_mechanics-filtering-type-none'
            )}
            description={l10n.getString(
              'settings-general-tracker_mechanics-filtering-type-none-description'
            )}
            value={FilteringType.NONE.toString()}
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
            value={FilteringType.SMOOTHING.toString()}
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
            value={FilteringType.PREDICTION.toString()}
          />
        </div>
        <div className="flex gap-5 pt-5 md:flex-row flex-col pb-4">
          <NumberSelector
            control={control}
            name="filtering.amount"
            label={l10n.getString(
              'settings-general-tracker_mechanics-filtering-amount'
            )}
            valueLabelFormat={(value) => percentageFormat.format(value)}
            min={0.1}
            max={1.0}
            step={0.1}
          />
        </div>

        <div className="flex flex-col pt-2 pb-4 gap-2">
          <Typography variant="section-title">
            {l10n.getString(
              'settings-general-fk_settings-leg_tweak-skating_correction'
            )}
          </Typography>
          <Typography>
            {l10n.getString(
              'settings-general-fk_settings-leg_tweak-skating_correction-description'
            )}
          </Typography>
        </div>
        <div className="grid sm:grid-cols-1 gap-2 pb-4">
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="toggles.skatingCorrection"
            label={l10n.getString(
              'settings-general-fk_settings-leg_tweak-skating_correction'
            )}
          />
          <NumberSelector
            control={control}
            name="ratios.skatingCorrectionStrength"
            label={l10n.getString(
              'settings-general-fk_settings-leg_tweak-skating_correction-amount'
            )}
            valueLabelFormat={(value) => percentageFormat.format(value)}
            min={0.1}
            max={1.0}
            step={0.1}
          />
        </div>

        <div className="flex flex-col pt-2 pb-2">
          <Typography variant="section-title">
            {l10n.getString('settings-general-fk_settings-leg_fk')}
          </Typography>
        </div>
        <div className="grid sm:grid-cols-1 gap-3 pb-3">
          <Typography>
            {l10n.getString(
              'settings-general-fk_settings-leg_tweak-floor_clip-description'
            )}
          </Typography>
        </div>
        <div className="grid sm:grid-cols-1 gap-2 pb-3">
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="toggles.floorClip"
            label={l10n.getString(
              'settings-general-fk_settings-leg_tweak-floor_clip'
            )}
          />
        </div>
        <div className="flex flex-col pt-2 pb-3">
          <Typography>
            {l10n.getString(
              'settings-general-fk_settings-leg_tweak-foot_plant-description'
            )}
          </Typography>
        </div>
        <div className="grid sm:grid-cols-1 gap-3 pb-3">
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="toggles.footPlant"
            label={l10n.getString(
              'settings-general-fk_settings-leg_tweak-foot_plant'
            )}
          />
        </div>
        <div className="flex flex-col pt-2 pb-3">
          <Typography>
            {l10n.getString(
              'settings-general-fk_settings-leg_tweak-toe_snap-description'
            )}
          </Typography>
        </div>
        <div className="grid sm:grid-cols-1 gap-3 pb-3">
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="toggles.toeSnap"
            label={l10n.getString(
              'settings-general-fk_settings-leg_tweak-toe_snap'
            )}
          />
        </div>

        <div className="flex flex-col pt-2 pb-3">
          <Typography variant="section-title">
            {l10n.getString('settings-general-fk_settings-arm_fk')}
          </Typography>
          <Typography>
            {l10n.getString('settings-general-fk_settings-arm_fk-description')}
          </Typography>
        </div>
        <div className="grid sm:grid-cols-1 pb-3">
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="toggles.forceArmsFromHmd"
            label={l10n.getString(
              'settings-general-fk_settings-arm_fk-force_arms'
            )}
          />
        </div>

        <div className="flex flex-col pt-2 pb-1">
          <Typography variant="section-title">
            {l10n.getString(
              'settings-general-fk_settings-enforce_joint_constraints'
            )}
          </Typography>
          <div className="pt-2">
            <Typography>
              {l10n.getString(
                'settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description'
              )}
            </Typography>
          </div>
        </div>
        <div className="grid sm:grid-cols-1 pb-3">
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="toggles.enforceConstraints"
            label={l10n.getString(
              'settings-general-fk_settings-enforce_joint_constraints-enforce_constraints'
            )}
          />
        </div>

        <div className="flex flex-col pt-2 pb-3">
          <Typography bold>
            {l10n.getString('settings-general-fk_settings-ik')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString(
              'settings-general-fk_settings-ik-use_position-description'
            )}
          </Typography>
        </div>
        <div className="grid sm:grid-cols-1 pb-3">
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="toggles.usePosition"
            label={l10n.getString(
              'settings-general-fk_settings-ik-use_position'
            )}
          />
        </div>

        <div className="flex flex-col pt-2 pb-3">
          <Typography variant="section-title">
            {l10n.getString(
              'settings-general-fk_settings-skeleton_settings-ratios'
            )}
          </Typography>
          <Typography>
            {l10n.getString(
              'settings-general-fk_settings-skeleton_settings-ratios-description'
            )}
          </Typography>
        </div>
        <div className="grid sm:grid-cols-2 gap-3 pb-3">
          <NumberSelector
            control={control}
            name="ratios.imputeSpineFromUpperLower"
            label={l10n.getString(
              'settings-general-fk_settings-skeleton_settings-impute_spine_from_upper_lower'
            )}
            valueLabelFormat={(value) => percentageFormat.format(value)}
            min={0.0}
            max={1.0}
            step={0.05}
          />
          <NumberSelector
            control={control}
            name="ratios.imputeSpineCurvature"
            label={l10n.getString(
              'settings-general-fk_settings-skeleton_settings-impute_spine_curvature'
            )}
            valueLabelFormat={(value) => percentageFormat.format(value)}
            min={0.0}
            max={1.0}
            step={0.05}
          />
          <NumberSelector
            control={control}
            name="ratios.interpHipLegs"
            label={l10n.getString(
              'settings-general-fk_settings-skeleton_settings-interp_hip_legs'
            )}
            valueLabelFormat={(value) => percentageFormat.format(value)}
            min={0.0}
            max={1.0}
            step={0.05}
          />
          <NumberSelector
            control={control}
            name="ratios.interpKneeTrackerAnkle"
            label={l10n.getString(
              'settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle'
            )}
            valueLabelFormat={(value) => percentageFormat.format(value)}
            min={0.0}
            max={1.0}
            step={0.05}
          />
          <NumberSelector
            control={control}
            name="ratios.interpKneeAnkle"
            label={l10n.getString(
              'settings-general-fk_settings-skeleton_settings-interp_knee_ankle'
            )}
            valueLabelFormat={(value) => percentageFormat.format(value)}
            min={0.0}
            max={1.0}
            step={0.05}
          />
        </div>

        <div className="flex flex-col pt-2 pb-3">
          <Typography variant="section-title">
            {l10n.getString(
              'settings-general-fk_settings-self_localization-title'
            )}
          </Typography>
          <Typography>
            {l10n.getString(
              'settings-general-fk_settings-self_localization-description'
            )}
          </Typography>
        </div>
        <div className="grid sm:grid-cols-1 gap3 pb5">
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="toggles.selfLocalization"
            label={l10n.getString(
              'settings-general-fk_settings-self_localization-title'
            )}
          />
        </div>
      </>
    </SettingsPagePaneLayout>
  );
}
