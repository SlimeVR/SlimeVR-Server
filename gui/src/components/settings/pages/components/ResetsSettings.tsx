import { useLocalization, Localized } from '@fluent/react';
import { useEffect } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
import {
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useLocaleConfig } from '@/i18n/config';
import { CheckBox } from '@/components/commons/Checkbox';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import {
  defaultResetSettings,
  loadResetSettings,
  ResetSettingsForm,
} from '@/hooks/reset-settings';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';

type ResetsForm = {
  resetsSettings: ResetSettingsForm;
};

const defaultValues: ResetsForm = {
  resetsSettings: defaultResetSettings,
};

const resetsSettingsAtom = atom(new ResetsSettingsResponseT());
const resetsSettingsValueAtom = selectAtom(
  resetsSettingsAtom,
  (settings) => settings,
  isEqual
);

export function ResetsSettings() {
  const setSettings = useSetAtom(resetsSettingsAtom);
  const settings = useAtomValue(resetsSettingsValueAtom);
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const secondsFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  });

  const { control, watch, handleSubmit, getValues, reset } =
    useForm<ResetsForm>({
      defaultValues,
      mode: 'onChange',
      reValidateMode: 'onChange',
    });

  const onSubmit = (values: ResetsForm) => {
    const settingsReq = loadResetSettings(values.resetsSettings);
    sendRPCPacket(RpcMessage.ChangeResetsSettingsRequest, settingsReq);
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.ResetsSettingsRequest,
      new ResetsSettingsRequestT()
    );
  }, []);

  useEffect(() => {
    const formData: DefaultValues<ResetsForm> = {};
    formData.resetsSettings = settings;
    reset({ ...getValues(), ...formData });
  }, [settings]);

  useRPCPacket(
    RpcMessage.ResetsSettingsResponse,
    (settings: ResetsSettingsResponseT) => {
      setSettings(settings);
    }
  );

  return (
    <>
      <div className="flex md:flex-row flex-col pt-5 pb-1">
        <Typography variant="section-title">
          {l10n.getString(
            'settings-general-tracker_mechanics-yaw-reset-smooth-time'
          )}
        </Typography>
      </div>

      <NumberSelector
        control={control}
        name="resetsSettings.yawResetSmoothTime"
        valueLabelFormat={(value) => secondsFormat.format(value)}
        min={0.0}
        max={0.5}
        step={0.05}
      />

      <div className="flex flex-col pt-5 pb-2">
        <Typography variant="section-title">
          {l10n.getString(
            'settings-general-tracker_mechanics-save_mounting_reset'
          )}
        </Typography>
        <Localized
          id="settings-general-tracker_mechanics-save_mounting_reset-description"
          elems={{ b: <b /> }}
        >
          <Typography />
        </Localized>
      </div>
      <CheckBox
        variant="toggle"
        outlined
        control={control}
        name="resetsSettings.saveMountingReset"
        label={l10n.getString(
          'settings-general-tracker_mechanics-save_mounting_reset-enabled-label'
        )}
      />

      <div className="flex flex-col pt-5">
        <div className="grid grid-cols-2 gap-2">
          <div className="flex flex-col gap-2">
            <Typography>
              {l10n.getString(
                'settings-general-fk_settings-reset_settings-reset_hmd_pitch-description'
              )}
            </Typography>
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="resetsSettings.resetHmdPitch"
              label={l10n.getString(
                'settings-general-fk_settings-reset_settings-reset_hmd_pitch'
              )}
            />
          </div>
          <div className="flex flex-col gap-2 justify-end">
            <Typography>
              {l10n.getString(
                'settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1'
              )}
            </Typography>
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="resetsSettings.resetMountingFeet"
              label={l10n.getString(
                'settings-general-fk_settings-leg_fk-reset_mounting_feet-v1'
              )}
            />
          </div>
        </div>
      </div>

      <div className="flex flex-col pt-5">
        <Typography>
          {l10n.getString(
            'settings-general-fk_settings-arm_fk-reset_mode-description'
          )}
        </Typography>
        <div className="grid md:grid-cols-2 flex-col gap-3 pt-2 pb-3">
          <Radio
            control={control}
            name="resetsSettings.armsResetMode"
            label={l10n.getString('settings-general-fk_settings-arm_fk-back')}
            description={l10n.getString(
              'settings-general-fk_settings-arm_fk-back-description'
            )}
            value={'0'}
          />
          <Radio
            control={control}
            name="resetsSettings.armsResetMode"
            label={l10n.getString(
              'settings-general-fk_settings-arm_fk-forward'
            )}
            description={l10n.getString(
              'settings-general-fk_settings-arm_fk-forward-description'
            )}
            value={'1'}
          />
          <Radio
            control={control}
            name="resetsSettings.armsResetMode"
            label={l10n.getString(
              'settings-general-fk_settings-arm_fk-tpose_up'
            )}
            description={l10n.getString(
              'settings-general-fk_settings-arm_fk-tpose_up-description'
            )}
            value={'2'}
          />
          <Radio
            control={control}
            name="resetsSettings.armsResetMode"
            label={l10n.getString(
              'settings-general-fk_settings-arm_fk-tpose_down'
            )}
            description={l10n.getString(
              'settings-general-fk_settings-arm_fk-tpose_down-description'
            )}
            value={'3'}
          />
        </div>
      </div>
    </>
  );
}
