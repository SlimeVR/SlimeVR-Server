import { HeightRequestT, HeightResponseT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from '../../../../../hooks/websocket-api';
import { Button } from '../../../../commons/Button';
import { Typography } from '../../../../commons/Typography';
import { useLocalization } from '@fluent/react';
import { useForm } from 'react-hook-form';
import { useEffect, useMemo } from 'react';
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
  const { control, watch, handleSubmit, setValue } = useForm<HeightForm>();
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

  useRPCPacket(
    RpcMessage.HeightResponse,
    ({ hmdHeight, estimatedFullHeight }: HeightResponseT) => {
      setValue('height', estimatedFullHeight || DEFAULT_HEIGHT);
      setValue('hmdHeight', hmdHeight);
    }
  );

  useEffect(() => {
    sendRPCPacket(RpcMessage.HeightRequest, new HeightRequestT());
  }, []);

  const onSubmit = (values: HeightForm) => {
    // FIXME: set height
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
          <form className="flex self-center items-center justify-center">
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
