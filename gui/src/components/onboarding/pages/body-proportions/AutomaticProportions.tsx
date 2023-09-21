import { useLocalization } from '@fluent/react';
import { RpcMessage, SkeletonResetAllRequestT } from 'solarxr-protocol';
import { AutoboneContextC, useProvideAutobone } from '@/hooks/autobone';
import { useOnboarding } from '@/hooks/onboarding';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { StepperSlider } from '@/components/onboarding/StepperSlider';
import { DoneStep } from './autobone-steps/Done';
import { RequirementsStep } from './autobone-steps/Requirements';
import { PutTrackersOnStep } from './autobone-steps/PutTrackersOn';
import { Recording } from './autobone-steps/Recording';
import { StartRecording } from './autobone-steps/StartRecording';
import { VerifyResultsStep } from './autobone-steps/VerifyResults';
import { useCountdown } from '@/hooks/countdown';
import { CheckHeight } from './autobone-steps/СheckHeight';

export function AutomaticProportionsPage() {
  const { l10n } = useLocalization();
  const { applyProgress, state } = useOnboarding();
  const { sendRPCPacket } = useWebsocketAPI();
  const context = useProvideAutobone();
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
    <AutoboneContextC.Provider value={context}>
      <div className="flex flex-col gap-5 h-full items-center w-full xs:justify-center overflow-y-auto overflow-x-hidden relative px-4 pb-4">
        <div className="flex flex-col w-full xs:h-full xs:justify-center max-w-3xl gap-5">
          <div className="flex flex-col max-w-lg gap-3">
            <Typography variant="main-title">
              {l10n.getString('onboarding-automatic_proportions-title')}
            </Typography>
            <div>
              <Typography color="secondary">
                {l10n.getString('onboarding-automatic_proportions-description')}
              </Typography>
            </div>
          </div>
          <div className="flex">
            <StepperSlider
              variant={state.alonePage ? 'alone' : 'onboarding'}
              steps={[
                { type: 'numbered', component: PutTrackersOnStep },
                { type: 'numbered', component: RequirementsStep },
                { type: 'numbered', component: CheckHeight },
                { type: 'numbered', component: StartRecording },
                { type: 'fullsize', component: Recording },
                { type: 'numbered', component: VerifyResultsStep },
                { type: 'fullsize', component: DoneStep },
              ]}
            ></StepperSlider>
          </div>
        </div>
        <div className="w-full pb-4 flex flex-grow flex-row mobile:justify-center">
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
    </AutoboneContextC.Provider>
  );
}
