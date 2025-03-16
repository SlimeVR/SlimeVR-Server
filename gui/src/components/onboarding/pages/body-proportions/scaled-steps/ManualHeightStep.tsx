import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { Localized, useLocalization } from '@fluent/react';
import { useMemo } from 'react';
import { useLocaleConfig } from '@/i18n/config';
import { EYE_HEIGHT_TO_HEIGHT_RATIO, useHeightContext } from '@/hooks/height';
import { useForm } from 'react-hook-form';
import {
  ChangeSettingsRequestT,
  ModelSettingsT,
  RpcMessage,
  SkeletonHeightT,
  StatusData,
  StatusSteamVRDisconnectedT,
} from 'solarxr-protocol';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { WarningBox } from '@/components/commons/TipBox';
import { useStatusContext } from '@/hooks/status-system';
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
  const { setHmdHeight } = useHeightContext();
  const { control, handleSubmit, formState, watch } = useForm<HeightForm>({
    defaultValues: { height: 1.5 },
  });
  const { sendRPCPacket } = useWebsocketAPI();
  const { currentLocales } = useLocaleConfig();
  const { statuses } = useStatusContext();
  const height = watch('height');

  const missingSteamConnection = useMemo(
    () =>
      Object.values(statuses).some(
        (x) =>
          x.dataType === StatusData.StatusSteamVRDisconnected &&
          (x.data as StatusSteamVRDisconnectedT).bridgeSettingsName ===
            'steamvr'
      ),
    [statuses]
  );

  const mFormat = useMemo(
    () =>
      new Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'meter',
        maximumFractionDigits: 2,
      }),
    [currentLocales]
  );

  const submitHmdHeight = (values: HeightForm) => {
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
      onSubmit={handleSubmit(submitHmdHeight)}
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
            {missingSteamConnection && (
              <div className="flex flex-row items-center mt-2 gap-2 mobile:flex-col">
                <Localized
                  id="onboarding-scaled_proportions-manual_height-missing_steamvr"
                  elems={{ b: <b></b> }}
                  // TODO: Add link to docs!
                >
                  <WarningBox>You don't have SteamVR connected!</WarningBox>
                </Localized>
              </div>
            )}
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
