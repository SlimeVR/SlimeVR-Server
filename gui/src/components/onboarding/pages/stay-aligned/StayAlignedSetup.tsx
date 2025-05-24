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
import {
  EnableStayAlignedRequestT,
  ResetType,
  RpcMessage,
} from 'solarxr-protocol';
import { RPCPacketType, useWebsocketAPI } from '@/hooks/websocket-api';
import { useEffect } from 'react';
import VerticalStepper, {
  VerticalStepComponentProps,
} from '@/components/commons/VerticalStepper';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useAtomValue } from 'jotai';
import { flatTrackersAtom } from '@/store/app-store';
import { TipBox } from '@/components/commons/TipBox';
import { BodyDisplay } from '@/components/commons/BodyDisplay';
import { Button } from '@/components/commons/Button';
import { SkeletonVisualizerWidget } from '@/components/widgets/SkeletonVisualizerWidget';
import { ResetButton } from '@/components/home/ResetButton';

export function enableStayAligned(
  enable: boolean,
  sendRPCPacket: (type: RpcMessage, data: RPCPacketType) => void
) {
  const req = new EnableStayAlignedRequestT();
  req.enable = enable;
  sendRPCPacket(RpcMessage.EnableStayAlignedRequest, req);
}

// const steps: Step[] = [
//   ...autoMountingSteps,
//   { type: 'numbered', component: VerifyMountingStep },
//   { type: 'numbered', component: StandingRelaxedPoseStep },
//   { type: 'numbered', component: SittingRelaxedPoseStep },
//   { type: 'numbered', component: FlatRelaxedPoseStep },
//   { type: 'fullsize', component: DoneStep },
// ];

function PutTrackersOnStep({ isActive, nextStep }: VerticalStepComponentProps) {
  const { isMobile } = useBreakpoint('mobile');
  const trackers = useAtomValue(flatTrackersAtom);
  const { l10n } = useLocalization();

  return (
    <div className="flex flex-col items-center w-full">
      <div className="flex flex-col flex-grow gap-2">
        <div className="flex flex-grow flex-col gap-4">
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
        <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-16">
          <BodyDisplay
            trackers={trackers}
            width={150}
            dotsSize={15}
            variant="dots"
            hideUnassigned={true}
          />
        </div>
        <div className="flex flex-col gap-3">
          <div className="flex gap-3 justify-end">
            <Button variant="primary" onClick={nextStep}>
              {l10n.getString(
                'onboarding-automatic_mounting-put_trackers_on-next'
              )}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

export function PreparationStep({
  nextStep,
  prevStep,
}: VerticalStepComponentProps) {
  const { isMobile } = useBreakpoint('mobile');
  const { l10n } = useLocalization();

  return (
    <div className="flex flex-col flex-grow justify-between">
      <div className="flex flex-col gap-4">
        <div>
          <Typography color="secondary">
            {l10n.getString('onboarding-automatic_mounting-preparation-step-0')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('onboarding-automatic_mounting-preparation-step-1')}
          </Typography>
        </div>
      </div>
      <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-12">
        <img src="/images/reset-pose.webp" width={100} alt="Reset position" />
      </div>
      <div className="flex gap-3 justify-between">
        <Button variant={'secondary'} onClick={prevStep}>
          {l10n.getString('onboarding-automatic_mounting-prev_step')}
        </Button>
        <ResetButton
          size="small"
          type={ResetType.Full}
          onReseted={nextStep}
        ></ResetButton>
      </div>
    </div>
  );
}

export function VerifyMountingStep({
  nextStep,
  prevStep,
}: VerticalStepComponentProps) {
  const { l10n } = useLocalization();

  return (
    <div className="flex flex-grow flex-col gap-4">
      <div className="flex flex-col gap-2">
        <Typography color="secondary">
          {l10n.getString('onboarding-stay_aligned-verify_mounting-step-0')}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('onboarding-stay_aligned-verify_mounting-step-1')}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('onboarding-stay_aligned-verify_mounting-step-2')}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('onboarding-stay_aligned-verify_mounting-step-3')}
        </Typography>
      </div>
      <div className="flex gap-3 justify-between">
        <Button variant={'secondary'} to="/onboarding/mounting/choose">
          Redo Mounting calibration
        </Button>
        <Button variant="primary" onClick={nextStep}>
          {l10n.getString('onboarding-stay_aligned-next_step')}
        </Button>
      </div>
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
    <div className="h-full w-full flex gap-2 mobile:flex-col bg-background-80">
      <div className="bg-background-70 rounded-md flex-grow p-4 overflow-y-auto">
        <div className="flex flex-col xs:max-w-lg gap-3">
          <Typography variant="main-title">
            {l10n.getString('onboarding-stay_aligned-title')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('onboarding-stay_aligned-description')}
          </Typography>
        </div>
        <div className="flex pl-4 pt-4">
          <VerticalStepper
            steps={[
              {
                title: 'Put on trackers',
                component: PutTrackersOnStep,
                id: 'start',
              },
              {
                title: 'Preparation',
                component: PreparationStep,
              },
              {
                title: 'Verify Mounting',
                component: VerifyMountingStep,
              },
              {
                title: l10n.getString(
                  'onboarding-stay_aligned-relaxed_poses-standing-title'
                ),
                component: StandingRelaxedPoseStep,
              },
              {
                title: l10n.getString(
                  'onboarding-stay_aligned-relaxed_poses-sitting-title'
                ),
                component: SittingRelaxedPoseStep,
              },
              {
                title: l10n.getString(
                  'onboarding-stay_aligned-relaxed_poses-flat-title'
                ),
                component: FlatRelaxedPoseStep,
              },
              {
                title: l10n.getString('onboarding-stay_aligned-done-title'),
                component: DoneStep,
              },
            ]}
          ></VerticalStepper>
        </div>
      </div>
      <div className="bg-background-70 rounded-md w-[45%] mobile:w-full mobile:h-[30%]">
        <SkeletonVisualizerWidget></SkeletonVisualizerWidget>
      </div>
    </div>
  );
}
