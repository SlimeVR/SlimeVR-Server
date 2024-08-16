import { useLocalization } from '@fluent/react';
import { RpcMessage, SkeletonResetAllRequestT } from 'solarxr-protocol';
import { useOnboarding } from '@/hooks/onboarding';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { StepperSlider } from '@/components/onboarding/StepperSlider';
import { PutTrackersOnStep } from './autobone-steps/PutTrackersOn';
import { useCountdown } from '@/hooks/countdown';
import { CheckHeight } from './autobone-steps/CheckHeight';
import { PreparationStep } from './autobone-steps/Preparation';
import { HeightContextC, useProvideHeightContext } from '@/hooks/height';
import { CheckFloorHeight } from './autobone-steps/CheckFloorHeight';

export function ScaledProportionsPage() {
  const { l10n } = useLocalization();
  const { applyProgress, state } = useOnboarding();
  const { sendRPCPacket } = useWebsocketAPI();
  const heightContext = useProvideHeightContext();
  const { isCounting, startCountdown, timer } = useCountdown({
    onCountdownEnd: () => {
      sendRPCPacket(
        RpcMessage.SkeletonResetAllRequest,
        new SkeletonResetAllRequestT()
      );
    },
  });

  applyProgress(0.9);

  return (
    <HeightContextC.Provider value={heightContext}>
      <div className="flex flex-col gap-5 h-full items-center w-full xs:justify-center overflow-y-auto overflow-x-hidden relative px-4 pb-4">
        <div className="flex flex-col w-full xs:h-full xs:justify-center max-w-3xl gap-5">
          <div className="flex flex-col max-w-lg gap-3">
            <Typography variant="main-title">
              {l10n.getString('onboarding-scaled_proportions-title')}
            </Typography>
            <div>
              <Typography color="secondary">
                {l10n.getString('onboarding-scaled_proportions-description')}
              </Typography>
            </div>
          </div>
          <div className="flex">
            <StepperSlider
              variant={state.alonePage ? 'alone' : 'onboarding'}
              steps={[
                { type: 'numbered', component: PutTrackersOnStep },
                { type: 'numbered', component: PreparationStep },
                { type: 'numbered', component: CheckHeight },
                { type: 'numbered', component: CheckFloorHeight },
              ]}
            ></StepperSlider>
          </div>
        </div>
        <div className="w-full pb-4 flex flex-row mobile:justify-center">
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
    </HeightContextC.Provider>
  );
}
