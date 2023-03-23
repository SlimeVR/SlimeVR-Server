import { useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { RpcMessage, SkeletonResetAllRequestT } from 'solarxr-protocol';
import {
  AutoboneContextC,
  useProvideAutobone,
} from '../../../../hooks/autobone';
import { useBodyProportions } from '../../../../hooks/body-proportions';
import { useOnboarding } from '../../../../hooks/onboarding';
import { useWebsocketAPI } from '../../../../hooks/websocket-api';
import { Button } from '../../../commons/Button';
import { Typography } from '../../../commons/Typography';
import { StepperSlider } from '../../StepperSlider';
import { DoneStep } from './autobone-steps/Done';
import { PreparationStep } from './autobone-steps/Preparation';
import { PutTrackersOnStep } from './autobone-steps/PutTrackersOn';
import { Recording } from './autobone-steps/Recording';
import { StartRecording } from './autobone-steps/StartRecording';
import { VerifyResultsStep } from './autobone-steps/VerifyResults';
import { SkipSetupWarningModal } from '../../SkipSetupWarningModal';
import { SkipSetupButton } from '../../SkipSetupButton';
import { useCountdown } from '../../../../hooks/countdown';

export function AutomaticProportionsPage() {
  const { l10n } = useLocalization();
  const { applyProgress, skipSetup, state } = useOnboarding();
  const { sendRPCPacket } = useWebsocketAPI();
  const context = useProvideAutobone();
  const { onPageOpened } = useBodyProportions();
  const [skipWarning, setSkipWarning] = useState(false);
  const { isCounting, startCountdown, timer } = useCountdown({
    onCountdownEnd: () => {
      sendRPCPacket(
        RpcMessage.SkeletonResetAllRequest,
        new SkeletonResetAllRequestT()
      );
    },
  });

  applyProgress(0.9);

  useEffect(() => {
    onPageOpened();
  }, []);

  return (
    <AutoboneContextC.Provider value={context}>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center relative">
        <SkipSetupButton
          visible={!state.alonePage}
          modalVisible={skipWarning}
          onClick={() => setSkipWarning(true)}
        ></SkipSetupButton>
        <div className="flex flex-col w-full h-full justify-center max-w-3xl gap-5">
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
                { type: 'numbered', component: PreparationStep },
                { type: 'numbered', component: StartRecording },
                { type: 'fullsize', component: Recording },
                { type: 'numbered', component: VerifyResultsStep },
                { type: 'fullsize', component: DoneStep },
              ]}
            ></StepperSlider>
          </div>
        </div>
        <div className="w-full pb-4 flex flex-row">
          <div className="flex flex-grow gap-3">
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
      <SkipSetupWarningModal
        accept={skipSetup}
        onClose={() => setSkipWarning(false)}
        isOpen={skipWarning}
      ></SkipSetupWarningModal>
    </AutoboneContextC.Provider>
  );
}
