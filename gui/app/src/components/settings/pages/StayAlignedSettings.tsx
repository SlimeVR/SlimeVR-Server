import { Localized, useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import {
  ChangeStayAlignedSettingsRequestT,
  RpcMessage,
  StayAlignedSettingsRequestT,
  StayAlignedSettingsResponseT,
} from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import {
  SettingRow,
  SettingsAdvanced,
  SettingsGroup,
} from '@/components/settings/SettingRow';
import { HorizontalAlignIcon } from '@/components/commons/icon/HorizontalAlignIcon';
import { useAtomValue, atom, useSetAtom } from 'jotai';
import { selectAtom } from 'jotai/utils';
import { isEqual } from '@react-hookz/deep-equal';
import { connectedIMUTrackersAtom } from '@/store/app-store';
import { bodypartToString } from '@/utils/formatting';
import { useLocaleConfig } from '@/i18n/config';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useSettingsDefaults } from '@/hooks/settings-defaults';
import { settingReset } from '@/components/settings/SettingReset';
import {
  FlatRelaxedPoseModal,
  SittingRelaxedPoseModal,
  StandingRelaxedPoseModal,
} from './components/StayAlignedPoseModal';

export type StayAlignedSettingsForm = {
  setupComplete: boolean;
  enabled: boolean;
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
};

function toForm(
  settings: StayAlignedSettingsResponseT
): StayAlignedSettingsForm {
  return {
    setupComplete: settings.setupComplete,
    enabled: settings.enabled,
    standingEnabled: settings.standingEnabled,
    standingUpperLegAngle: settings.standingUpperLegAngle,
    standingLowerLegAngle: settings.standingLowerLegAngle,
    standingFootAngle: settings.standingFootAngle,
    sittingEnabled: settings.sittingEnabled,
    sittingUpperLegAngle: settings.sittingUpperLegAngle,
    sittingLowerLegAngle: settings.sittingLowerLegAngle,
    sittingFootAngle: settings.sittingFootAngle,
    flatEnabled: settings.flatEnabled,
    flatUpperLegAngle: settings.flatUpperLegAngle,
    flatLowerLegAngle: settings.flatLowerLegAngle,
    flatFootAngle: settings.flatFootAngle,
  };
}

const stayAlignedSettingsAtom = atom<StayAlignedSettingsResponseT | null>(null);
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

  const copySettings = () => {
    const config = values;

    const debug = `
Stay Aligned debug info:

GENERAL
=======
Enabled: ${config.enabled}
Setup complete: ${config.setupComplete}

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
      return `${bodypartToString(info.bodyPart)}: correction=${numberFormat.format(stayAligned.yawCorrectionInDeg)} locked=${stayAligned.locked}`;
    }
  })
  .join('\n')}
`;

    navigator.clipboard.writeText(debug);
  };

  return (
    <Button
      variant="secondary"
      className="whitespace-nowrap"
      onClick={copySettings}
    >
      {l10n.getString('settings-stay_aligned-debug-copy-short')}
    </Button>
  );
}

export function StayAlignedSettings() {
  const setSettings = useSetAtom(stayAlignedSettingsAtom);
  const settings = useAtomValue(stayAlignedSettingsValueAtom);
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const defaults = useSettingsDefaults().stayAligned;

  const { control, watch, handleSubmit, reset, setValue } =
    useForm<StayAlignedSettingsForm>({
      defaultValues: toForm(defaults),
      mode: 'onChange',
      reValidateMode: 'onChange',
    });

  const values = watch();

  const onSubmit = (values: StayAlignedSettingsForm) => {
    const settingsReq = new ChangeStayAlignedSettingsRequestT();
    settingsReq.enabled = values.enabled;
    settingsReq.standingEnabled = values.standingEnabled;
    settingsReq.sittingEnabled = values.sittingEnabled;
    settingsReq.flatEnabled = values.flatEnabled;

    sendRPCPacket(RpcMessage.ChangeStayAlignedSettingsRequest, settingsReq);
  };

  const resetTo = (
    name: 'enabled' | 'standingEnabled' | 'sittingEnabled' | 'flatEnabled'
  ) =>
    settingReset<StayAlignedSettingsForm, typeof name>(
      defaults[name],
      watch(name),
      (next) => {
        setValue(name, next);
        handleSubmit(onSubmit)();
      }
    );

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
    if (!settings) return;
    reset(toForm(settings));
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

  const poses = [
    {
      id: 'standing',
      name: 'standingEnabled' as const,
      hasPose: hasStandingPose,
      open: openStanding,
      modal: <StandingRelaxedPoseModal open={openStanding} />,
    },
    {
      id: 'sitting',
      name: 'sittingEnabled' as const,
      hasPose: hasSittingPose,
      open: openSitting,
      modal: <SittingRelaxedPoseModal open={openSitting} />,
    },
    {
      id: 'flat',
      name: 'flatEnabled' as const,
      hasPose: hasFlatPose,
      open: openFlat,
      modal: <FlatRelaxedPoseModal open={openFlat} />,
    },
  ];

  return (
    <SettingsPagePaneLayout
      icon={<HorizontalAlignIcon />}
      title="settings-sidebar-stay_aligned"
    >
      <div className="flex flex-col">
        <div className="flex flex-col gap-1 pb-4 max-w-[75ch]">
          <Typography id="settings-stay_aligned-description" />
          <Typography id="settings-stay_aligned-setup-description" />
        </div>

        <SettingsGroup title="settings-stay_aligned-setup_group">
          <SettingRow
            title="settings-stay_aligned-setup-label"
            control={
              <Button
                variant="primary"
                to="/onboarding/stay-aligned"
                state={{ alonePage: true }}
                id="settings-stay_aligned-setup-open"
              />
            }
          />
          <SettingRow
            title="settings-stay_aligned-enabled-label"
            disabled={!config.setupComplete}
            disabledReason="settings-stay_aligned-setup-description"
            controlId="stay-aligned-enabled"
            {...resetTo('enabled')}
            control={
              <CheckBox
                variant="toggle"
                control={control}
                name="enabled"
                id="stay-aligned-enabled"
              />
            }
          />
        </SettingsGroup>

        <SettingsGroup
          title="settings-stay_aligned-relaxed_poses-label"
          description="settings-stay_aligned-relaxed_poses-description"
        >
          {poses.map(({ id, name, hasPose, open, modal }) => (
            <SettingRow
              key={id}
              title={`settings-stay_aligned-relaxed_poses-${id}`}
              disabled={!config.setupComplete}
              disabledReason="settings-stay_aligned-setup-description"
              controlId={`stay-aligned-${id}`}
              {...resetTo(name)}
              control={
                <div className="flex gap-2 items-center">
                  {modal}
                  <Localized id="settings-stay_aligned-relaxed_poses-save_pose">
                    <Button
                      variant="secondary"
                      className="whitespace-nowrap"
                      onClick={() => open[1](true)}
                    />
                  </Localized>
                  <CheckBox
                    variant="toggle"
                    control={control}
                    name={name}
                    id={`stay-aligned-${id}`}
                    disabled={!config.setupComplete || !hasPose}
                  />
                </div>
              }
            />
          ))}
        </SettingsGroup>

        <SettingsAdvanced>
          <SettingsGroup title="settings-stay_aligned-debug-label">
            <SettingRow
              title="settings-stay_aligned-debug-copy-label"
              description="settings-stay_aligned-debug-description"
              control={<CopySettingsButton values={values} />}
            />
          </SettingsGroup>
        </SettingsAdvanced>
      </div>
    </SettingsPagePaneLayout>
  );
}
