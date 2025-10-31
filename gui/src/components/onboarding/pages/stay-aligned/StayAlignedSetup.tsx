import { Typography } from '@/components/commons/Typography';
import { DoneStep } from './stay-aligned-steps/Done';
import { useLocalization } from '@fluent/react';
import {
  FlatRelaxedPoseStep,
  SittingRelaxedPoseStep,
  StandingRelaxedPoseStep,
} from './stay-aligned-steps/RelaxedPoseSteps';
import { EnableStayAlignedRequestT, RpcMessage } from 'solarxr-protocol';
import { RPCPacketType, useWebsocketAPI } from '@/hooks/websocket-api';
import { useEffect, useRef } from 'react';
import VerticalStepper from '@/components/commons/VerticalStepper';
import { useBreakpoint } from '@/hooks/breakpoint';
import {
  SkeletonPreviewView,
  SkeletonVisualizerWidget,
} from '@/components/widgets/SkeletonVisualizerWidget';
import { Vector3 } from 'three';
import { Easing } from '@tweenjs/tween.js';
import { VerifyMountingStep } from './stay-aligned-steps/VerifyMounting';
import { PutTrackersOnStep } from './stay-aligned-steps/PutTrackersOnStep';
import { PreparationStep } from './stay-aligned-steps/PreparationStep';

export function enableStayAligned(
  enable: boolean,
  sendRPCPacket: (type: RpcMessage, data: RPCPacketType) => void
) {
  const req = new EnableStayAlignedRequestT();
  req.enable = enable;
  sendRPCPacket(RpcMessage.EnableStayAlignedRequest, req);
}

export function StayAlignedSetup() {
  const { l10n } = useLocalization();
  const { isMobile } = useBreakpoint('mobile');
  const { sendRPCPacket } = useWebsocketAPI();

  const viewsRef = useRef<{
    cam1?: SkeletonPreviewView;
    cam2?: SkeletonPreviewView;
  }>({});

  useEffect(() => {
    // Disable Stay Aligned as soon as we enter the setup flow so that we don't
    // adjust the trackers while trying to set up the feature
    enableStayAligned(false, sendRPCPacket);
  }, []);

  const updateCamSizes = () => {
    const views = viewsRef.current;
    if (!views.cam1 || !views.cam2) return;
    if (!views.cam2.hidden) {
      if (isMobile) {
        views.cam1.height = 1;
        views.cam2.height = 1;
        views.cam2.width = 0.5;
        views.cam1.width = 0.5;
        views.cam1.bottom = 0;
        views.cam2.bottom = 0;
        views.cam2.left = 0.5;
      } else {
        views.cam1.height = 0.5;
        views.cam2.height = 0.5;
        views.cam2.width = 1;
        views.cam1.width = 1;
        views.cam1.bottom = 0;
        views.cam2.bottom = 0.5;
        views.cam2.left = 0;
      }
    } else {
      views.cam1.height = 1;
      views.cam2.height = 1;
      views.cam2.width = 1;
      views.cam1.width = 1;
      views.cam1.bottom = 0;
      views.cam2.bottom = 0;
      views.cam2.left = 0;
    }
  };

  const onStepChange = (index: number, id?: string) => {
    if (id === 'start') {
      enableStayAligned(false, sendRPCPacket);
    }

    const views = viewsRef.current;
    if (!views.cam1 || !views.cam2) return;
    switch (id) {
      case 'standing': {
        views.cam2.hidden = true;
        views.cam1.tween
          .stop()
          .to(new Vector3(0, 1, -6), 500)
          .easing(Easing.Quadratic.InOut)
          .startFromCurrentValues();
        break;
      }
      case 'flat':
      case 'sitting': {
        views.cam2.hidden = false;
        views.cam1.tween
          .stop()
          .to(new Vector3(-5, 1, -0), 500)
          .easing(Easing.Quadratic.InOut)
          .startFromCurrentValues();

        views.cam2.tween
          .stop()
          .to(new Vector3(0, 4, -0.2), 500)
          .easing(Easing.Quadratic.InOut)
          .startFromCurrentValues();
        break;
      }
      default: {
        views.cam2.hidden = true;
        views.cam1.tween
          .stop()
          .to(new Vector3(3, 2.5, -3), 1000)
          .easing(Easing.Quadratic.InOut)
          .startFromCurrentValues();
        break;
      }
    }

    updateCamSizes();
  };

  useEffect(() => {
    updateCamSizes();
  }, [isMobile]);

  return (
    <div className="h-full w-full flex gap-2 mobile:flex-col bg-background-80">
      <div className="bg-background-70 rounded-md flex-grow p-4 overflow-y-auto">
        <div className="flex flex-col xs:max-w-lg gap-3">
          <Typography variant="main-title">
            {l10n.getString('onboarding-stay_aligned-title')}
          </Typography>
          <Typography>
            {l10n.getString('onboarding-stay_aligned-description')}
          </Typography>
        </div>
        <div className="flex pl-4 pt-4">
          <VerticalStepper
            onStepChange={onStepChange}
            steps={[
              {
                title: l10n.getString(
                  'onboarding-stay_aligned-put_trackers_on-title'
                ),
                component: PutTrackersOnStep,
                id: 'start',
              },
              {
                title: l10n.getString(
                  'onboarding-stay_aligned-preparation-title'
                ),
                component: PreparationStep,
              },
              {
                title: l10n.getString(
                  'onboarding-stay_aligned-verify_mounting-title'
                ),
                component: VerifyMountingStep,
              },
              {
                title: l10n.getString(
                  'onboarding-stay_aligned-relaxed_poses-standing-title'
                ),
                component: StandingRelaxedPoseStep,
                id: 'standing',
              },
              {
                title: l10n.getString(
                  'onboarding-stay_aligned-relaxed_poses-sitting-title'
                ),
                component: SittingRelaxedPoseStep,
                id: 'sitting',
              },
              {
                title: l10n.getString(
                  'onboarding-stay_aligned-relaxed_poses-flat-title'
                ),
                component: FlatRelaxedPoseStep,
                id: 'flat',
              },
              {
                title: l10n.getString('onboarding-stay_aligned-done-title'),
                component: DoneStep,
              },
            ]}
          />
        </div>
      </div>
      <div className="bg-background-70 rounded-md xs:max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg w-full mobile:h-[30%]">
        <SkeletonVisualizerWidget
          onInit={(context) => {
            viewsRef.current.cam1 = context.addView({
              left: 0,
              bottom: 0,
              width: 1,
              height: 1,
              position: new Vector3(3, 2.5, -3),
              onHeightChange(v, newHeight) {
                v.controls.target.set(0, newHeight / 2, 0);
                const scale = Math.max(1, newHeight) / 1.5;
                v.camera.zoom = 1 / scale;
              },
            });

            viewsRef.current.cam2 = context.addView({
              left: 0,
              bottom: 0.5,
              width: 1,
              height: 0.5,
              hidden: true,
              position: new Vector3(3, 2.5, -3),
              onHeightChange(v, newHeight) {
                v.controls.target.set(0, newHeight / 2, 0);
                const scale = Math.max(1, newHeight) / 1.5;
                v.camera.zoom = 1 / scale;
              },
            });
          }}
        />
      </div>
    </div>
  );
}
