import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { useEffect, useMemo } from 'react';
import { useLocaleConfig } from '@/i18n/config';
import {
  DEFAULT_FULL_HEIGHT,
  EYE_HEIGHT_TO_HEIGHT_RATIO,
  useHeightContext,
} from '@/hooks/height';
import { useForm } from 'react-hook-form';
import {
  ChangeSettingsRequestT,
  ModelSettingsT,
  RpcMessage,
  SkeletonHeightT,
} from 'solarxr-protocol';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { useOnboarding } from '@/hooks/onboarding';
import { MIN_HEIGHT } from '@/hooks/manual-proportions';

interface HeightForm {
  height: number;
}

export function ManualHeightStep({
  nextStep,
  prevStep,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { state } = useOnboarding();
  const { l10n } = useLocalization();
  const { setHmdHeight, currentHeight } = useHeightContext();
  const { control, handleSubmit, formState, watch, reset } =
    useForm<HeightForm>({
      defaultValues: { height: DEFAULT_FULL_HEIGHT },
    });
  const { sendRPCPacket } = useWebsocketAPI();
  const { currentLocales } = useLocaleConfig();
  const height = watch('height');

  // Load the last configured height
  useEffect(() => {
    const height = currentHeight();
    reset({
      height:
        (currentHeight && currentHeight / EYE_HEIGHT_TO_HEIGHT_RATIO) || DEFAULT_FULL_HEIGHT,
    });
  }, [currentHeight]);

  const mFormat = useMemo(
    () =>
      new Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'meter',
        maximumFractionDigits: 2,
      }),
    [currentLocales]
  );

  const submitFullHeight = (values: HeightForm) => {
    const newHeight = values.height * EYE_HEIGHT_TO_HEIGHT_RATIO;
    setHmdHeight(newHeight);
    const settingsRequest = new ChangeSettingsRequestT();
    settingsRequest.modelSettings = new ModelSettingsT(
      null,
      null,
      null,
      new SkeletonHeightT(newHeight, 0)
    );
    sendRPCPacket(RpcMessage.ChangeSettingsRequest, settingsRequest);
    nextStep();
  };

  return (
    <form
      className="flex flex-col flex-grow"
      onSubmit={handleSubmit(submitFullHeight)}
    >
      <div className="flex gap-2 flex-grow">
        <div className="flex flex-grow flex-col gap-4">
          <Typography variant="main-title" bold>
            {l10n.getString(
              'onboarding-scaled_proportions-manual_height-title'
            )}
          </Typography>
          <div>
            <Typography color="secondary">
              {l10n.getString(
                'onboarding-scaled_proportions-manual_height-description-v2'
              )}
            </Typography>
          </div>
          <div className="flex flex-col self-center items-center justify-center">
            <NumberSelector
              control={control}
              name="height"
              label={l10n.getString(
                'onboarding-scaled_proportions-manual_height-height-v2'
              )}
              valueLabelFormat={(value) =>
                isNaN(value)
                  ? l10n.getString(
                      'onboarding-scaled_proportions-manual_height-unknown'
                    )
                  : mFormat.format(value)
              }
              min={MIN_HEIGHT}
              max={4}
              step={0.01}
              showButtonWithNumber
              doubleStep={0.1}
            />
          </div>
          <div className="flex flex-col self-center items-center justify-center">
            <Typography>
              {l10n.getString(
                'onboarding-scaled_proportions-manual_height-estimated_height'
              )}
            </Typography>
            <Typography>
              {mFormat.format(height * EYE_HEIGHT_TO_HEIGHT_RATIO)}
            </Typography>
          </div>
        </div>
      </div>

      <div className="flex gap-3 mobile:justify-between">
        {!state.alonePage && (
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_proportions-prev_step')}
          </Button>
        )}
        <Button type="submit" variant="primary" disabled={!formState.isValid}>
          {l10n.getString(
            'onboarding-scaled_proportions-manual_height-next_step'
          )}
        </Button>
      </div>
    </form>
  );
}
