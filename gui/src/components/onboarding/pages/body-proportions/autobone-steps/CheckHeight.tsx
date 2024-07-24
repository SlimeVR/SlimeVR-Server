import {
  ChangeSettingsRequestT,
  HeightRequestT,
  HeightResponseT,
  ModelSettingsT,
  RpcMessage,
  SkeletonHeightT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { Localized, useLocalization } from '@fluent/react';
import { useCallback, useMemo, useRef, useState } from 'react';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { MIN_HEIGHT } from '@/components/onboarding/pages/body-proportions/ProportionsChoose';
import { useLocaleConfig } from '@/i18n/config';
import { useCountdown } from '@/hooks/countdown';
import { TipBox } from '@/components/commons/TipBox';
import { RulerIcon } from '@/components/commons/icon/RulerIcon';

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
  const [floorHeight, setFloorHeight] = useState(0);
  const [hmdHeight, setHmdHeight] = useState(NaN);
  const [fetchedHeight, setFetchedHeight] = useState(false);
  const putFloorHeight = useRef(false);
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
    ({ maxHeight, minHeight }: HeightResponseT) => {
      if (putFloorHeight.current) {
        putFloorHeight.current = false;
        setFloorHeight(minHeight);
      } else {
        setHmdHeight(maxHeight);
      }
    }
  );

  const onSubmit = useCallback(() => {
    const changeSettings = new ChangeSettingsRequestT();
    const skeletonHeight = new SkeletonHeightT(hmdHeight, floorHeight);
    const model = new ModelSettingsT(
      undefined,
      undefined,
      undefined,
      skeletonHeight
    );
    changeSettings.modelSettings = model;

    sendRPCPacket(RpcMessage.ChangeSettingsRequest, changeSettings);
    nextStep();
  }, [hmdHeight, floorHeight]);

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

            <div className="flex flex-row items-center mt-2 gap-2 mobile:flex-col">
              {/* <Button
                variant="primary"
                onClick={startCountdown}
                disabled={isCounting}
              >
                {isCounting
                  ? sFormat.format(timer, 'second')
                  : l10n.getString(
                      'onboarding-automatic_proportions-check_height-fetch_height'
                    )}
              </Button> */}
              <TipBox className="break-words">
                {l10n.getString(
                  'onboarding-automatic_proportions-check_height-guardian_tip'
                )}
              </TipBox>
            </div>
          </div>
          <div className="flex flex-col self-center items-center justify-center gap-1">
            <Typography>
              {l10n.getString(
                'onboarding-automatic_proportions-check_height-hmd_height2'
              )}
            </Typography>
            <Button variant="tertiary" className="pl-5 pr-1 py-3 min-w-20">
              <Typography textAlign="text-center">
                {isNaN(hmdHeight)
                  ? l10n.getString(
                      'onboarding-automatic_proportions-check_height-unknown'
                    )
                  : mFormat.format(hmdHeight)}
              </Typography>
            </Button>
            <Typography>
              {l10n.getString(
                'onboarding-automatic_proportions-check_height-height2'
              )}
            </Typography>
            <Button
              variant="tertiary"
              className="pl-5 pr-0 py-3 min-w-20 fill-background-10"
            >
              <Typography textAlign="text-center">
                {isNaN(floorHeight)
                  ? l10n.getString(
                      'onboarding-automatic_proportions-check_height-unknown'
                    )
                  : mFormat.format(floorHeight)}
              </Typography>
              <div className="ml-2 px-3 border-l border-background-30">
                <RulerIcon width={18} />
              </div>
            </Button>
          </div>
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
            onClick={onSubmit}
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
