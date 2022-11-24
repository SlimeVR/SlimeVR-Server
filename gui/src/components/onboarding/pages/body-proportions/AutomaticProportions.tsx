import { useState } from 'react';
import { RpcMessage, SkeletonResetAllRequestT } from 'solarxr-protocol';
import {
  AutoboneContextC,
  useProvideAutobone,
} from '../../../../hooks/autobone';
import { useOnboarding } from '../../../../hooks/onboarding';
import { useWebsocketAPI } from '../../../../hooks/websocket-api';
import { ArrowLink } from '../../../commons/ArrowLink';
import { Button } from '../../../commons/Button';
import { Typography } from '../../../commons/Typography';
import { AutoboneStepper } from './AutoboneStepper';

export function AutomaticProportionsPage() {
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
                Go Back to Reset tutorial
              </ArrowLink>
            )}
            <Typography variant="main-title">Measure your body</Typography>
            <div>
              <Typography color="secondary">
                For SlimeVR trackers to work, we need to know the length of your
                bones.
              </Typography>
              <Typography color="secondary">
                This short calibration will measure it for you.
              </Typography>
            </div>
          </div>
          <div className="flex">
            <AutoboneStepper
              variant={state.alonePage ? 'alone' : 'onboarding'}
            ></AutoboneStepper>
          </div>
        </div>
        <div className="w-full pb-4 flex flex-row">
          <div className="flex flex-grow">
            {!state.alonePage && (
              <Button variant="secondary" to="/" onClick={skipSetup}>
                Skip setup
              </Button>
            )}
            <Button
              variant="secondary"
              onClick={resetAll}
              disabled={resetDisabled}
            >
              Reset all proportions
            </Button>
          </div>
          <div className="flex gap-3">
            <Button
              variant="secondary"
              state={{ alonePage: state.alonePage }}
              to="/onboarding/body-proportions/manual"
            >
              Manual calibration
            </Button>
            {!state.alonePage && (
              <Button variant="primary" to="/onboarding/done">
                Continue
              </Button>
            )}
          </div>
        </div>
      </div>
    </AutoboneContextC.Provider>
  );
}
