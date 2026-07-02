import { Localized, useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
import {
  ChangeStayAlignedSettingsRequestT,
  RpcMessage,
  StayAlignedSettingsRequestT,
  StayAlignedSettingsResponseT,
} from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPageLayout';
import { HorizontalAlignIcon } from '@/components/commons/icon/HorizontalAlignIcon';
import { useAtomValue, atom, useSetAtom } from 'jotai';
import { selectAtom } from 'jotai/utils';
import { isEqual } from '@react-hookz/deep-equal';
import { connectedIMUTrackersAtom } from '@/store/app-store';
import { bodypartToString } from '@/utils/formatting';
import { useLocaleConfig } from '@/i18n/config';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import {
  FlatRelaxedPoseModal,
  SittingRelaxedPoseModal,
  StandingRelaxedPoseModal,
} from './StayAlignedPoseModal';

export type StayAlignedSettingsForm = {
  enabled: boolean;
  hideYawCorrection: boolean;
  standingEnabled: boolean;
  standingUpperLegAngle: number;
  standingLowerLegAngle: number;
  standingFootAngle: number;
  sittingEnabled: boolean;
  sittingUpperLegAngle: number;
  sittingLowerLegAngle: number;
  sittingFootAngle: number;
  flatEnabled: boolean;
  flatUpperLegAngle: number;
  flatLowerLegAngle: number;
  flatFootAngle: number;
  // Not part of StayAlignedSettingsResponse — tracked client-side/derived
  // from whether setup has produced non-zero relaxed pose angles.
  setupComplete: boolean;
};

export const defaultStayAlignedSettings: StayAlignedSettingsForm = {
  enabled: false,
  hideYawCorrection: false,
  standingEnabled: false,
  standingUpperLegAngle: 0.0,
  standingLowerLegAngle: 0.0,
  standingFootAngle: 0.0,
  sittingEnabled: false,
  sittingUpperLegAngle: 0.0,
  sittingLowerLegAngle: 0.0,
  sittingFootAngle: 0.0,
  flatEnabled: false,
  flatUpperLegAngle: 0.0,
  flatLowerLegAngle: 0.0,
  flatFootAngle: 0.0,
  setupComplete: false,
};

const stayAlignedSettingsAtom = atom(new StayAlignedSettingsResponseT());
const stayAlignedSettingsValueAtom = selectAtom(
  stayAlignedSettingsAtom,
  (settings) => settings,
  isEqual
);

function CopySettingsButton({ values }: { values: StayAlignedSettingsForm }) {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const numberFormat = new Intl.NumberFormat(currentLocales, {
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  });

  const trackers = useAtomValue(connectedIMUTrackersAtom);

  function boolify(value: boolean) {
    return value ? 'true' : 'false';
  }

  const copySettings = () => {
    const config = values;

    const debug = `
Stay Aligned

GENERAL
=======
Enabled: ${config.enabled ? 'true' : 'false'}
Setup complete: ${boolify(config.setupComplete)}

RELAXED POSES
=============
Standing: ${config.standingEnabled ? `Enabled (upper_leg=${numberFormat.format(config.standingUpperLegAngle)}, lower_leg=${numberFormat.format(config.standingLowerLegAngle)}, foot=${numberFormat.format(config.standingFootAngle)})` : 'Not enabled'}
Sitting: ${config.sittingEnabled ? `Enabled (upper_leg=${numberFormat.format(config.sittingUpperLegAngle)}, lower_leg=${numberFormat.format(config.sittingLowerLegAngle)}, foot=${numberFormat.format(config.sittingFootAngle)})` : 'Not enabled'}
Flat: ${config.flatEnabled ? `Enabled (upper_leg=${numberFormat.format(config.flatUpperLegAngle)}, lower_leg=${numberFormat.format(config.flatLowerLegAngle)}, foot=${numberFormat.format(config.flatFootAngle)})` : 'Not enabled'}

TRACKERS
========
${trackers
  .map((t) => {
    const info = t.tracker.info;
    const stayAligned = t.tracker.stayAligned;
    if (info && stayAligned) {
      return `${bodypartToString(info.bodyPart)}: correction=${numberFormat.format(stayAligned.yawCorrectionInDeg)} locked=${stayAligned.locked ? `true locked_error=${numberFormat.format(stayAligned.lockedErrorInDeg)}` : 'false'} center_error=${numberFormat.format(stayAligned.centerErrorInDeg)} neighbor_error=${numberFormat.format(stayAligned.neighborErrorInDeg)}`;
    }
  })
  .join('\n')}
`;

    navigator.clipboard.writeText(debug);
  };

  return (
    <Button variant="primary" onClick={copySettings}>
      {l10n.getString('settings-stay_aligned-debug-copy-label')}
    </Button>
  );
}

export function StayAlignedSettings() {
  const setSettings = useSetAtom(stayAlignedSettingsAtom);
  const settings = useAtomValue(stayAlignedSettingsValueAtom);
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const { control, watch, handleSubmit, getValues, reset } =
    useForm<StayAlignedSettingsForm>({
      defaultValues: defaultStayAlignedSettings,
      mode: 'onChange',
      reValidateMode: 'onChange',
    });

  const values = watch();

  const onSubmit = (values: StayAlignedSettingsForm) => {
    const settingsReq = new ChangeStayAlignedSettingsRequestT();
    settingsReq.enabled = values.enabled;
    settingsReq.standingEnabled = values.standingEnabled;
    settingsReq.standingUpperLegAngle = values.standingUpperLegAngle;
    settingsReq.standingLowerLegAngle = values.standingLowerLegAngle;
    settingsReq.standingFootAngle = values.standingFootAngle;
    settingsReq.sittingEnabled = values.sittingEnabled;
    settingsReq.sittingUpperLegAngle = values.sittingUpperLegAngle;
    settingsReq.sittingLowerLegAngle = values.sittingLowerLegAngle;
    settingsReq.sittingFootAngle = values.sittingFootAngle;
    settingsReq.flatEnabled = values.flatEnabled;
    settingsReq.flatUpperLegAngle = values.flatUpperLegAngle;
    settingsReq.flatLowerLegAngle = values.flatLowerLegAngle;
    settingsReq.flatFootAngle = values.flatFootAngle;

    sendRPCPacket(RpcMessage.ChangeStayAlignedSettingsRequest, settingsReq);
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.StayAlignedSettingsRequest,
      new StayAlignedSettingsRequestT()
    );
  }, []);

  useEffect(() => {
    const formData: DefaultValues<StayAlignedSettingsForm> = {};

    if (settings.enabled !== undefined) formData.enabled = settings.enabled;
    // TODO hideYawCorrection

    formData.standingEnabled = settings.standingEnabled ?? false;
    formData.standingUpperLegAngle = settings.standingUpperLegAngle ?? 0.0;
    formData.standingLowerLegAngle = settings.standingLowerLegAngle ?? 0.0;
    formData.standingFootAngle = settings.standingFootAngle ?? 0.0;

    formData.sittingEnabled = settings.sittingEnabled ?? false;
    formData.sittingUpperLegAngle = settings.sittingUpperLegAngle ?? 0.0;
    formData.sittingLowerLegAngle = settings.sittingLowerLegAngle ?? 0.0;
    formData.sittingFootAngle = settings.sittingFootAngle ?? 0.0;

    formData.flatEnabled = settings.flatEnabled ?? false;
    formData.flatUpperLegAngle = settings.flatUpperLegAngle ?? 0.0;
    formData.flatLowerLegAngle = settings.flatLowerLegAngle ?? 0.0;
    formData.flatFootAngle = settings.flatFootAngle ?? 0.0;

    formData.setupComplete =
      (formData.standingUpperLegAngle ?? 0) !== 0 ||
      (formData.sittingUpperLegAngle ?? 0) !== 0 ||
      (formData.flatUpperLegAngle ?? 0) !== 0;

    reset({ ...getValues(), ...formData });
  }, [settings]);

  useRPCPacket(
    RpcMessage.StayAlignedSettingsResponse,
    (settings: StayAlignedSettingsResponseT) => {
      setSettings(settings);
    }
  );

  const config = values;
  const hasStandingPose =
    config.standingEnabled ||
    config.standingUpperLegAngle !== 0.0 ||
    config.standingLowerLegAngle !== 0.0 ||
    config.standingFootAngle !== 0.0;
  const hasSittingPose =
    config.sittingEnabled ||
    config.sittingUpperLegAngle !== 0.0 ||
    config.sittingLowerLegAngle !== 0.0 ||
    config.sittingFootAngle !== 0.0;
  const hasFlatPose =
    config.flatEnabled ||
    config.flatUpperLegAngle !== 0.0 ||
    config.flatLowerLegAngle !== 0.0 ||
    config.flatFootAngle !== 0.0;

  const openStanding = useState(false);
  const openSitting = useState(false);
  const openFlat = useState(false);

  return (
    <SettingsPagePaneLayout icon={<HorizontalAlignIcon />} id="stayAligned">
      <Typography variant="main-title">
        {l10n.getString('settings-stay_aligned')}
      </Typography>
      <div className="pt-3">
        <Typography>
          {l10n.getString('settings-stay_aligned-description')}
        </Typography>
        <Typography>
          {l10n.getString('settings-stay_aligned-setup-description')}
        </Typography>
        <div className="flex pt-2">
          <Button
            variant="primary"
            to="/onboarding/stay-aligned"
            state={{ alonePage: true }}
          >
            {l10n.getString('settings-stay_aligned-setup-label')}
          </Button>
        </div>
      </div>
      <div className="pt-6">
        <Typography variant="section-title">
          {l10n.getString('settings-stay_aligned-general-label')}
        </Typography>
        <div className="grid sm:grid-cols-2 gap-3 pt-2">
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="enabled"
            label={l10n.getString('settings-stay_aligned-enabled-label')}
            disabled={!config.setupComplete}
          />
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="hideYawCorrection"
            label={l10n.getString(
              'settings-stay_aligned-hide_yaw_correction-label'
            )}
            disabled={!config.setupComplete}
          />
        </div>
      </div>
      <div className="pt-6">
        <Typography variant="section-title">
          {l10n.getString('settings-stay_aligned-relaxed_poses-label')}
        </Typography>
        <div className="pt-1">
          <Typography>
            {l10n.getString('settings-stay_aligned-relaxed_poses-description')}
          </Typography>
        </div>
        <div className="grid sm:grid-cols-1 gap-3 pt-2">
          <div className="flex gap-2">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="standingEnabled"
              label={l10n.getString(
                'settings-stay_aligned-relaxed_poses-standing'
              )}
              disabled={!config.setupComplete || !hasStandingPose}
            />
            <StandingRelaxedPoseModal open={openStanding} />
            <Localized id="settings-stay_aligned-relaxed_poses-save_pose">
              <Button
                variant="primary"
                className="w-full max-w-32"
                disabled={!config.setupComplete}
                onClick={() => openStanding[1](true)}
              />
            </Localized>
          </div>
          <div className="flex gap-2">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="sittingEnabled"
              label={l10n.getString(
                'settings-stay_aligned-relaxed_poses-sitting'
              )}
              disabled={!config.setupComplete || !hasSittingPose}
            />
            <SittingRelaxedPoseModal open={openSitting} />
            <Localized id="settings-stay_aligned-relaxed_poses-save_pose">
              <Button
                variant="primary"
                className="w-full max-w-32"
                disabled={!config.setupComplete}
                onClick={() => openSitting[1](true)}
              />
            </Localized>
          </div>
          <div className="flex gap-2">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="flatEnabled"
              label={l10n.getString('settings-stay_aligned-relaxed_poses-flat')}
              disabled={!config.setupComplete || !hasFlatPose}
            />
            <FlatRelaxedPoseModal open={openFlat} />
            <Localized id="settings-stay_aligned-relaxed_poses-save_pose">
              <Button
                variant="primary"
                className="w-full max-w-32"
                disabled={!config.setupComplete}
                onClick={() => openFlat[1](true)}
              />
            </Localized>
          </div>
        </div>
      </div>
      <div className="pt-6">
        <Typography variant="section-title">
          {l10n.getString('settings-stay_aligned-debug-label')}
        </Typography>
        <div className="pt-1">
          <Typography>
            {l10n.getString('settings-stay_aligned-debug-description')}
          </Typography>
        </div>
        <div className="pt-2">
          <CopySettingsButton values={values} />
        </div>
      </div>
    </SettingsPagePaneLayout>
  );
}
