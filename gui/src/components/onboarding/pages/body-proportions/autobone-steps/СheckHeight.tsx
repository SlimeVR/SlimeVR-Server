import {
  AutoBoneSettingsT,
  ChangeSettingsRequestT,
  HeightRequestT,
  HeightResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { Localized, useLocalization } from '@fluent/react';
import { useForm } from 'react-hook-form';
import { useMemo, useState } from 'react';
import { NumberSelector } from '@/components/commons/NumberSelector';
import {
  DEFAULT_HEIGHT,
  MIN_HEIGHT,
} from '@/components/onboarding/pages/body-proportions/ProportionsChoose';
import { useLocaleConfig } from '@/i18n/config';
import { useCountdown } from '@/hooks/countdown';

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
  const { control, handleSubmit, setValue } = useForm<HeightForm>();
  const [fetchedHeight, setFetchedHeight] = useState(false);
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { timer, isCounting, startCountdown } = useCountdown({
    duration: 3,
    onCountdownEnd: () => {
      setFetchedHeight(true);
      sendRPCPacket(RpcMessage.HeightRequest, new HeightRequestT());
    },
  });
  const { currentLocales } = useLocaleConfig();

  const mFormat = useMemo(
    () =>
      new Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'meter',
        maximumFractionDigits: 2,
      }),
    [currentLocales]
  );

  const sFormat = useMemo(
    () => new Intl.RelativeTimeFormat(currentLocales, { style: 'short' }),
    [currentLocales]
  );

  useRPCPacket(
    RpcMessage.HeightResponse,
    ({ hmdHeight, estimatedFullHeight }: HeightResponseT) => {
      setValue('height', estimatedFullHeight || DEFAULT_HEIGHT);
      setValue('hmdHeight', hmdHeight);
    }
  );

  const onSubmit = (values: HeightForm) => {
    const changeSettings = new ChangeSettingsRequestT();
    const autobone = new AutoBoneSettingsT();
    autobone.targetFullHeight = values.height;
    autobone.targetHmdHeight = values.hmdHeight;
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
            <Localized
              id="onboarding-automatic_proportions-check_height-calculation_warning"
              elems={{ u: <span className="underline"></span> }}
            >
              <Typography color="secondary" bold>
                Press the button to get your height!
              </Typography>
            </Localized>

            <Button
              variant="primary"
              className="mt-2"
              onClick={startCountdown}
              disabled={isCounting}
            >
              {isCounting
                ? sFormat.format(timer, 'second')
                : l10n.getString(
                    'onboarding-automatic_proportions-check_height-fetch_height'
                  )}
            </Button>
          </div>
          <form className="flex flex-col self-center items-center justify-center">
            <NumberSelector
              control={control}
              name="hmdHeight"
              label={l10n.getString(
                'onboarding-automatic_proportions-check_height-hmd_height1'
              )}
              valueLabelFormat={(value) =>
                isNaN(value)
                  ? l10n.getString(
                      'onboarding-automatic_proportions-check_height-unknown'
                    )
                  : mFormat.format(value)
              }
              min={MIN_HEIGHT}
              max={4}
              step={0.01}
              disabled={true}
            />
            <NumberSelector
              control={control}
              name="height"
              label={l10n.getString(
                'onboarding-automatic_proportions-check_height-height1'
              )}
              valueLabelFormat={(value) =>
                isNaN(value)
                  ? l10n.getString(
                      'onboarding-automatic_proportions-check_height-unknown'
                    )
                  : mFormat.format(value)
              }
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
          <Button
            variant="primary"
            onClick={handleSubmit(onSubmit)}
            disabled={!fetchedHeight}
          >
            {l10n.getString(
              'onboarding-automatic_proportions-check_height-next_step'
            )}
          </Button>
        </div>
      </div>
    </>
  );
}
