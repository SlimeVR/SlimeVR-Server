import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import { Step, StepperSlider } from '@/components/onboarding/StepperSlider';
import { DoneStep } from './stay-aligned-steps/Done';
import { useLocalization } from '@fluent/react';
import { autoMountingSteps } from '@/components/onboarding/pages/mounting/AutomaticMounting';
import {
  FlatRelaxedPoseStep,
  SittingRelaxedPoseStep,
  StandingRelaxedPoseStep,
} from './stay-aligned-steps/RelaxedPoseSteps';
import { EnableStayAlignedRequestT, RpcMessage } from 'solarxr-protocol';
import { RPCPacketType, useWebsocketAPI } from '@/hooks/websocket-api';
import { useEffect } from 'react';
import { VerifyMountingStep } from './stay-aligned-steps/VerifyMounting';
import VerticalStepper, {
  VerticalStepComponentProps,
} from '@/components/commons/VerticalStepper';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useAtomValue } from 'jotai';
import { flatTrackersAtom } from '@/store/app-store';
import { TipBox } from '@/components/commons/TipBox';
import { BodyDisplay } from '@/components/commons/BodyDisplay';
import { Button } from '@/components/commons/Button';

export function enableStayAligned(
  enable: boolean,
  sendRPCPacket: (type: RpcMessage, data: RPCPacketType) => void
) {
  const req = new EnableStayAlignedRequestT();
  req.enable = enable;
  sendRPCPacket(RpcMessage.EnableStayAlignedRequest, req);
}

const steps: Step[] = [
  ...autoMountingSteps,
  { type: 'numbered', component: VerifyMountingStep },
  { type: 'numbered', component: StandingRelaxedPoseStep },
  { type: 'numbered', component: SittingRelaxedPoseStep },
  { type: 'numbered', component: FlatRelaxedPoseStep },
  { type: 'fullsize', component: DoneStep },
];

function PutTrackersOnStep({ isActive, nextStep }: VerticalStepComponentProps) {
  const { isMobile } = useBreakpoint('mobile');
  const trackers = useAtomValue(flatTrackersAtom);
  const { l10n } = useLocalization();

  return (
    <div className="flex mobile:flex-col items-center w-full">
      <div className="flex flex-col flex-grow gap-2">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <div>
            <Typography color="secondary">
              {l10n.getString(
                'onboarding-automatic_mounting-put_trackers_on-description'
              )}
            </Typography>
          </div>
          <div className="flex">
            <TipBox>{l10n.getString('tips-find_tracker')}</TipBox>
          </div>
        </div>

        {isMobile && (
          <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-16">
            <BodyDisplay
              trackers={trackers}
              width={150}
              dotsSize={15}
              variant="dots"
              hideUnassigned={true}
            />
          </div>
        )}

        <div className="flex flex-col gap-3">
          <div className="flex gap-3 mobile:justify-between">
            <Button variant="primary" onClick={nextStep}>
              {l10n.getString(
                'onboarding-automatic_mounting-put_trackers_on-next'
              )}
            </Button>
          </div>
        </div>
      </div>
      {!isMobile && (
        <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-16">
          <BodyDisplay
            trackers={trackers}
            width={150}
            dotsSize={15}
            variant="dots"
            hideUnassigned={true}
          />
        </div>
      )}
    </div>
  );
}

export function StayAlignedSetup() {
  const { l10n } = useLocalization();
  const { state } = useOnboarding();
  const { sendRPCPacket } = useWebsocketAPI();

  useEffect(() => {
    // Disable Stay Aligned as soon as we enter the setup flow so that we don't
    // adjust the trackers while trying to set up the feature
    enableStayAligned(false, sendRPCPacket);
  }, []);

  return (
    <div className="flex flex-col gap-2 h-full w-full relative overflow-y-auto overflow-x-hidden px-4 pb-4">
      <div className="flex flex-col xs:max-w-lg gap-3">
        <Typography variant="main-title">
          {l10n.getString('onboarding-stay_aligned-title')}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('onboarding-stay_aligned-description')}
        </Typography>
      </div>
      <div className="flex pb-4">
        {/* <StepperSlider
            variant={state.alonePage ? 'alone' : 'onboarding'}
            steps={steps}
          /> */}
        <VerticalStepper
          steps={[
            {
              title: 'Put on trackers',
              component: PutTrackersOnStep,
            },
          ]}
        ></VerticalStepper>
      </div>
    </div>
  );
}
