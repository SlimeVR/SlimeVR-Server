import { useLocalization } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useState, useMemo, useEffect } from 'react';
import {
  BodyPart,
  ResetResponseT,
  ResetStatus,
  ResetType,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
} from 'solarxr-protocol';
import { useTrackers } from '@/hooks/tracker';
import { BodyDisplay } from '@/components/commons/BodyDisplay';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import classNames from 'classnames';
import { useBreakpoint } from '@/hooks/breakpoint';
import { log } from '@/utils/logging';

export function ResetTutorialPage() {
  const { isMobile } = useBreakpoint('mobile');
  const { l10n } = useLocalization();
  const { applyProgress } = useOnboarding();
  const { useAssignedTrackers } = useTrackers();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [curIndex, setCurIndex] = useState(0);
  const [tapSettings, setTapSettings] = useState<number[]>([]);
  applyProgress(0.8);

  const assignedTrackers = useAssignedTrackers();

  const highestTorsoTracker = useMemo(
    () =>
      assignedTrackers
        .filter((x) =>
          TORSO_PARTS.includes(x.tracker.info?.bodyPart ?? BodyPart.NONE)
        )
        .sort(
          (a, b) =>
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            TORSO_PARTS.indexOf(a.tracker.info!.bodyPart)! -
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            TORSO_PARTS.indexOf(b.tracker.info!.bodyPart)!
        ),
    [assignedTrackers]
  );

  const highestRightLegTracker = useMemo(
    () =>
      assignedTrackers
        .filter((x) =>
          RIGHT_LEG_PARTS.includes(x.tracker.info?.bodyPart ?? BodyPart.NONE)
        )
        .sort(
          (a, b) =>
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            RIGHT_LEG_PARTS.indexOf(a.tracker.info!.bodyPart)! -
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            RIGHT_LEG_PARTS.indexOf(b.tracker.info!.bodyPart)!
        ),
    [assignedTrackers]
  );

  const highestLeftLegTracker = useMemo(
    () =>
      assignedTrackers
        .filter((x) =>
          LEFT_LEG_PARTS.includes(x.tracker.info?.bodyPart ?? BodyPart.NONE)
        )
        .sort(
          (a, b) =>
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            LEFT_LEG_PARTS.indexOf(a.tracker.info!.bodyPart)! -
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            LEFT_LEG_PARTS.indexOf(b.tracker.info!.bodyPart)!
        ),
    [assignedTrackers]
  );

  useRPCPacket(
    RpcMessage.ResetResponse,
    ({ status, resetType }: ResetResponseT) => {
      if (status !== ResetStatus.STARTED) return;
      log(status);
      if (resetType === RESET_TYPE_ORDER[curIndex]) {
        setCurIndex(curIndex + 1);
      }
    }
  );

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  useRPCPacket(
    RpcMessage.SettingsResponse,
    ({ tapDetectionSettings }: SettingsResponseT) => {
      if (
        !tapDetectionSettings ||
        !tapDetectionSettings.yawResetTaps ||
        !tapDetectionSettings.fullResetTaps ||
        !tapDetectionSettings.mountingResetTaps
      )
        return;
      setTapSettings([
        tapDetectionSettings.yawResetTaps,
        tapDetectionSettings.fullResetTaps,
        tapDetectionSettings.mountingResetTaps,
      ]);
    }
  );

  const order = [
    highestTorsoTracker[0],
    highestLeftLegTracker[0],
    highestRightLegTracker[0],
  ];

  return (
    <div className="flex flex-col gap-5 h-full items-center w-full xs:justify-center relative overflow-y-auto">
      <div className="flex xs:flex-row mobile:flex-col w-full h-full xs:justify-center xs:px-20 mobile:px-4 gap-8 self-center">
        <div className="flex flex-col gap-3 xs:w-96 self-center">
          <Typography variant="main-title">
            {l10n.getString('onboarding-reset_tutorial')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('onboarding-reset_tutorial-explanation')}
          </Typography>
          <div className="flex">
            <Button variant="secondary" to="/onboarding/mounting/choose">
              {l10n.getString('onboarding-previous_step')}
            </Button>

            <Button
              variant="secondary"
              className={classNames(
                'ml-auto',
                curIndex + 1 >= order.length && 'hidden'
              )}
              onClick={() => {
                setCurIndex(curIndex + 1);
              }}
            >
              {l10n.getString('onboarding-reset_tutorial-skip')}
            </Button>

            <Button
              variant="primary"
              to="/onboarding/body-proportions/scaled"
              className={classNames(
                'ml-auto',
                order.length > curIndex + 1 && 'hidden'
              )}
            >
              {l10n.getString('onboarding-continue')}
            </Button>
          </div>
          <div
            className={classNames(
              'self-center xs:w-72 md:hidden xs:mt-10 mobile:mt-5 xs:ml-auto border-background-10',
              'border-l-4 pl-4',
              curIndex < order.length && 'visible',
              curIndex >= order.length && 'hidden'
            )}
          >
            <Typography whitespace="whitespace-pre-line" color="secondary">
              {l10n.getString(`onboarding-reset_tutorial-${curIndex}`, {
                taps: tapSettings[curIndex],
              })}
            </Typography>
          </div>
        </div>
        <div className="flex flex-row justify-center">
          <BodyDisplay
            width={isMobile ? 160 : undefined}
            trackers={[order[curIndex]]}
            hideUnassigned={true}
          ></BodyDisplay>
          <div
            className={classNames(
              'self-center w-72 md-max:hidden',
              curIndex >= order.length && 'hidden'
            )}
          >
            <Typography whitespace="whitespace-pre-line" color="secondary">
              {l10n.getString(`onboarding-reset_tutorial-${curIndex}`, {
                taps: tapSettings[curIndex],
              })}
            </Typography>
          </div>
        </div>
      </div>
    </div>
  );
}

export const TORSO_PARTS = [
  BodyPart.UPPER_CHEST,
  BodyPart.CHEST,
  BodyPart.WAIST,
  BodyPart.HIP,
];
export const LEFT_LEG_PARTS = [
  BodyPart.LEFT_UPPER_LEG,
  BodyPart.LEFT_LOWER_LEG,
];
export const RIGHT_LEG_PARTS = [
  BodyPart.RIGHT_UPPER_LEG,
  BodyPart.RIGHT_LOWER_LEG,
];

export const RESET_TYPE_ORDER = [
  ResetType.Yaw,
  ResetType.Full,
  ResetType.Mounting,
];
