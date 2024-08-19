import { RpcMessage, SkeletonResetAllRequestT } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { useCountdown } from '@/hooks/countdown';
import { useWebsocketAPI } from '@/hooks/websocket-api';

export function ResetProportionsStep({
  nextStep,
  prevStep,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();
  const { isCounting, startCountdown, timer } = useCountdown({
    onCountdownEnd: () => {
      sendRPCPacket(
        RpcMessage.SkeletonResetAllRequest,
        new SkeletonResetAllRequestT()
      );
      nextStep();
    },
  });

  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            {l10n.getString(
              'onboarding-scaled_proportions-reset_proportion-title'
            )}
          </Typography>
          <div>
            <Typography color="secondary">
              {l10n.getString(
                'onboarding-scaled_proportions-reset_proportion-description'
              )}
            </Typography>
          </div>
        </div>

        <div className="flex flex-col gap-3">
          <div className="flex gap-3 mobile:justify-between">
            <Button
              variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
              onClick={prevStep}
            >
              {l10n.getString('onboarding-automatic_proportions-prev_step')}
            </Button>
            <Button
              variant="secondary"
              onClick={startCountdown}
              disabled={isCounting}
            >
              <div className="relative">
                <div className="opacity-0 h-0">
                  {l10n.getString('reset-reset_all')}
                </div>
                {!isCounting ? l10n.getString('reset-reset_all') : timer}
              </div>
            </Button>
          </div>
        </div>
      </div>
    </>
  );
}
