import {
  AutoBoneSettingsT,
  ChangeSettingsRequestT,
  HeightRequestT,
  HeightResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '../../../../../hooks/websocket-api';
import { Button } from '../../../../commons/Button';
import { Typography } from '../../../../commons/Typography';
import { useLocalization } from '@fluent/react';
import { useForm } from 'react-hook-form';
import { useEffect, useMemo, useState } from 'react';
import { NumberSelector } from '../../../../commons/NumberSelector';
import { DEFAULT_HEIGHT, MIN_HEIGHT } from '../ProportionsChoose';
import { useLocaleConfig } from '../../../../../i18n/config';

interface HeightForm {
  height: number;
  hmdHeight: number;
}

export function CheckHeight({
  nextStep,
  prevStep,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { l10n } = useLocalization();
  const [hmdDiff, setHmdDiff] = useState(0);
  const { control, handleSubmit, setValue, watch } = useForm<HeightForm>({
    defaultValues: { hmdHeight: 0, height: DEFAULT_HEIGHT },
  });
  const watchHeight = watch('height', DEFAULT_HEIGHT);
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { currentLocales } = useLocaleConfig();

  const mFormat = useMemo(
    () =>
      Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'meter',
        maximumFractionDigits: 2,
      }),
    [currentLocales]
  );

  useEffect(() => setValue('hmdHeight', watchHeight - hmdDiff), [watchHeight]);

  useRPCPacket(
    RpcMessage.HeightResponse,
    ({ hmdHeight, estimatedFullHeight }: HeightResponseT) => {
      setValue('height', estimatedFullHeight || DEFAULT_HEIGHT);
      setHmdDiff(estimatedFullHeight - hmdHeight);
    }
  );

  useEffect(() => {
    sendRPCPacket(RpcMessage.HeightRequest, new HeightRequestT());
  }, []);

  const onSubmit = (values: HeightForm) => {
    const changeSettings = new ChangeSettingsRequestT();
    const autobone = new AutoBoneSettingsT();
    autobone.targetFullHeight = values.height;
    autobone.targetHmdHeight = values.height - hmdDiff;
    changeSettings.autoBoneSettings = autobone;

    sendRPCPacket(RpcMessage.ChangeSettingsRequest, changeSettings);
    nextStep();
  };

  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4">
          <Typography variant="main-title" bold>
            {l10n.getString(
              'onboarding-automatic_proportions-check_height-title'
            )}
          </Typography>
          <div>
            <Typography color="secondary">
              {l10n.getString(
                'onboarding-automatic_proportions-check_height-description'
              )}
            </Typography>
          </div>
          <form className="flex flex-col self-center items-center justify-center">
            <NumberSelector
              control={control}
              name="height"
              label={l10n.getString(
                'onboarding-automatic_proportions-check_height-height'
              )}
              valueLabelFormat={(value) => mFormat.format(value)}
              min={MIN_HEIGHT}
              max={4}
              step={0.01}
            />
            <NumberSelector
              control={control}
              name="hmdHeight"
              label={l10n.getString(
                'onboarding-automatic_proportions-check_height-hmd_height'
              )}
              valueLabelFormat={(value) => mFormat.format(value)}
              min={MIN_HEIGHT}
              max={4}
              step={0.01}
              disabled={true}
            />
          </form>
        </div>

        <div className="flex gap-3 mobile:justify-between">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_proportions-prev_step')}
          </Button>
          <Button variant="primary" onClick={handleSubmit(onSubmit)}>
            {l10n.getString(
              'onboarding-automatic_proportions-check_height-next_step'
            )}
          </Button>
        </div>
      </div>
    </>
  );
}
