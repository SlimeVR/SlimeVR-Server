import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { RpcMessage, SkeletonResetAllRequestT } from 'solarxr-protocol';
import {
  AutoboneContextC,
  useProvideAutobone
} from '../../../../hooks/autobone';
import { useOnboarding } from '../../../../hooks/onboarding';
import { useWebsocketAPI } from '../../../../hooks/websocket-api';
import { ArrowLink } from '../../../commons/ArrowLink';
import { Button } from '../../../commons/Button';
import { Typography } from '../../../commons/Typography';
import { StepperSlider } from '../../StepperSlider';
import { DoneStep } from './autobone-steps/Done';
import { PreparationStep } from './autobone-steps/Preparation';
import { PutTrackersOnStep } from './autobone-steps/PutTrackersOn';
import { Recording } from './autobone-steps/Recording';
import { StartRecording } from './autobone-steps/StartRecording';
import { VerifyResultsStep } from './autobone-steps/VerifyResults';

export function AutomaticProportionsPage() {
  const { t } = useTranslation();
  const { applyProgress, skipSetup, state } = useOnboarding();
  const { sendRPCPacket } = useWebsocketAPI();
  const context = useProvideAutobone();
  const [resetDisabled, setResetDisabled] = useState(false);

  applyProgress(0.9);

  const resetAll = () => {
    sendRPCPacket(
      RpcMessage.SkeletonResetAllRequest,
      new SkeletonResetAllRequestT()
    );
    setResetDisabled(true);

    setTimeout(() => {
      setResetDisabled(false);
    }, 3000);
  };

  return (
    <AutoboneContextC.Provider value={context}>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
        <div className="flex flex-col w-full h-full justify-center max-w-3xl gap-5">
          <div className="flex flex-col max-w-lg gap-3">
            {!state.alonePage && (
              <ArrowLink to="/onboarding/reset-tutorial" direction="left">
                {t('onboarding.automatic-proportions.back')}
              </ArrowLink>
            )}
            <Typography variant="main-title">
              {t('onboarding.automatic-proportions.title')}
            </Typography>
            <div>
              <Typography color="secondary">
                {t('onboarding.automatic-proportions.description')}
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
            {!state.alonePage && (
              <Button variant="secondary" to="/" onClick={skipSetup}>
                {t('onboarding.skip')}
              </Button>
            )}
            <Button
              variant="secondary"
              onClick={resetAll}
              disabled={resetDisabled}
            >
              {t('reset.reset-all')}
            </Button>
          </div>
          <div className="flex gap-3">
            <Button
              variant="secondary"
              state={{ alonePage: state.alonePage }}
              to="/onboarding/body-proportions/manual"
            >
              {t('onboarding.automatic-proportions.manual')}
            </Button>
            {!state.alonePage && (
              <Button variant="primary" to="/onboarding/done">
                {t('onboarding.continue')}
              </Button>
            )}
          </div>
        </div>
      </div>
    </AutoboneContextC.Provider>
  );
}
