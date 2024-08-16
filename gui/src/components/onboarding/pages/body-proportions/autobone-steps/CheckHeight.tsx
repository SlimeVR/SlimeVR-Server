import { HeightRequestT, HeightResponseT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { Localized, useLocalization } from '@fluent/react';
import { useMemo, useState } from 'react';
import { useLocaleConfig } from '@/i18n/config';
import { TipBox } from '@/components/commons/TipBox';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useHeightContext } from '@/hooks/height';
import { useInterval } from '@/hooks/timeout';

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
  const { hmdHeight, setHmdHeight } = useHeightContext();
  const [fetchedHeight, setFetchedHeight] = useState(false);
  const [fetchHeight, setFetchHeight] = useState(false);
  const { isMobile } = useBreakpoint('mobile');
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { currentLocales } = useLocaleConfig();

  useInterval(() => {
    if (fetchHeight) {
      sendRPCPacket(RpcMessage.HeightRequest, new HeightRequestT());
    }
  }, 100);

  const mFormat = useMemo(
    () =>
      new Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'meter',
        maximumFractionDigits: 2,
      }),
    [currentLocales]
  );

  useRPCPacket(RpcMessage.HeightResponse, ({ maxHeight }: HeightResponseT) => {
    if (fetchHeight) {
      setHmdHeight((val) =>
        val === null ? maxHeight : Math.max(maxHeight, val)
      );
    }
  });
  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex gap-1 flex-grow">
          <div className="flex flex-grow flex-col gap-4">
            <Typography variant="main-title" bold>
              {l10n.getString(
                'onboarding-automatic_proportions-check_height-title-v2'
              )}
            </Typography>
            <div>
              <Typography color="secondary">
                {l10n.getString(
                  'onboarding-automatic_proportions-check_height-description'
                )}
              </Typography>
              <Localized
                id="onboarding-automatic_proportions-check_height-calculation_warning-v2"
                elems={{ u: <span className="underline"></span> }}
              >
                <Typography color="secondary" bold>
                  Press the button to get your height!
                </Typography>
              </Localized>

              <div className="flex flex-row items-center mt-2 gap-2 mobile:flex-col">
                <TipBox className="break-words">
                  {l10n.getString(
                    'onboarding-automatic_proportions-check_height-guardian_tip'
                  )}
                </TipBox>
              </div>
            </div>
            <div className="flex flex-grow items-center justify-center">
              <div className="flex flex-col gap-3 items-center">
                {!fetchHeight && (
                  <Button
                    variant="primary"
                    onClick={() => {
                      setHmdHeight(null);
                      setFetchedHeight(false);
                      setFetchHeight(true);
                    }}
                  >
                    <Typography textAlign="text-center">
                      {l10n.getString(
                        fetchedHeight
                          ? 'onboarding-automatic_proportions-check_height-measure-reset'
                          : 'onboarding-automatic_proportions-check_height-measure-start'
                      )}
                    </Typography>
                  </Button>
                )}
                {fetchHeight && (
                  <Button
                    variant="primary"
                    onClick={() => {
                      setFetchHeight(false);
                      setFetchedHeight(true);
                    }}
                  >
                    <Typography textAlign="text-center">
                      {l10n.getString(
                        'onboarding-automatic_proportions-check_height-measure-stop'
                      )}
                    </Typography>
                  </Button>
                )}
                <Typography>
                  {l10n.getString(
                    'onboarding-automatic_proportions-check_height-hmd_height2'
                  )}
                </Typography>
                <Typography
                  color={fetchHeight ? 'text-status-success' : undefined}
                >
                  {hmdHeight === null
                    ? l10n.getString(
                        'onboarding-automatic_proportions-check_height-unknown'
                      )
                    : mFormat.format(hmdHeight)}
                </Typography>
              </div>
            </div>
          </div>
          <div className="self-center">
            <img
              src="/images/front-standing-pose.webp"
              width={isMobile ? 400 : 300}
              alt="Reset position"
            />
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
            onClick={nextStep}
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
