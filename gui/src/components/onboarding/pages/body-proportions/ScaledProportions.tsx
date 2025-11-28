import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import { DEFAULT_FULL_HEIGHT } from '@/hooks/height';
import { Button } from '@/components/commons/Button';
import { useAtomValue } from 'jotai';
import { serverGuardsAtom } from '@/store/app-store';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useEffect, useState } from 'react';
import {
  CancelUserHeightCalibrationT,
  ChangeSettingsRequestT,
  ModelSettingsT,
  ResetType,
  RpcMessage,
  SkeletonConfigRequestT,
  SkeletonConfigResponseT,
  SkeletonHeightT,
  SkeletonResetAllRequestT,
  StartUserHeightCalibrationT,
  UserHeightCalibrationStatus,
  UserHeightRecordingStatusResponseT,
} from 'solarxr-protocol';
import { HeightSelectionInput } from './HeightInput';
import { Tooltip } from '@/components/commons/Tooltip';
import classNames from 'classnames';
import { SkeletonVisualizerWidget } from '@/components/widgets/SkeletonVisualizerWidget';
import { Vector3 } from 'three';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { useDebouncedEffect } from '@/hooks/timeout';
import { playTapSetupSound } from '@/sounds/sounds';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';
import { TipBox } from '@/components/commons/TipBox';
import { Localized } from '@fluent/react';
import { ResetButton } from '@/components/home/ResetButton';

function UserHeightStep({
  done = false,
  text,
}: {
  disabled?: boolean;
  done?: boolean;
  text: string;
}) {
  return (
    <div className="flex flex-col pr-2 ml-6 last:pb-0 pb-3 border-l-[2px] border-background-50">
      <div className="flex w-full gap-2">
        <div
          className={classNames(
            'p-1 rounded-full fill-background-10 flex items-center justify-center z-10 h-[25px] w-[25px] -ml-[13px]',
            { 'bg-background-50': !done, 'bg-accent-background-20': done }
          )}
        >
          {done && <CheckIcon size={10} />}
          {!done && (
            <div
              className={classNames('h-[12px] w-[12px] rounded-full', {
                'bg-accent-background-10 animate-pulse brightness-75': true,
              })}
            />
          )}
        </div>
        <Typography variant="section-title" id={text} />
      </div>
    </div>
  );
}

const statusSteps = [
  UserHeightCalibrationStatus.NONE,
  UserHeightCalibrationStatus.RECORDING_FLOOR,
  UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH,
  UserHeightCalibrationStatus.WAITING_FOR_RISE,
  UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK,
  UserHeightCalibrationStatus.RECORDING_HEIGHT,
  UserHeightCalibrationStatus.DONE,
];

const errorSteps = [
  UserHeightCalibrationStatus.ERROR_TIMEOUT,
  UserHeightCalibrationStatus.ERROR_TOO_HIGH,
  UserHeightCalibrationStatus.ERROR_TOO_SMALL,
];

const statusToImage: Record<UserHeightCalibrationStatus, string | null> = {
  [UserHeightCalibrationStatus.NONE]: null,
  [UserHeightCalibrationStatus.DONE]: '/images/user-height/done.webp',
  [UserHeightCalibrationStatus.RECORDING_FLOOR]:
    '/images/user-height/touch-floor.webp',
  [UserHeightCalibrationStatus.WAITING_FOR_RISE]:
    '/images/user-height/stand-still.webp',
  [UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH]: null,
  [UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK]: null,
  [UserHeightCalibrationStatus.RECORDING_HEIGHT]:
    '/images/user-height/stand-still.webp',
  [UserHeightCalibrationStatus.ERROR_TIMEOUT]:
    '/images/user-height/timeout.webp',
  [UserHeightCalibrationStatus.ERROR_TOO_HIGH]:
    '/images/user-height/wrong-height.webp',
  [UserHeightCalibrationStatus.ERROR_TOO_SMALL]:
    '/images/user-height/wrong-height.webp',
};

function UserHeightStatus({
  status,
}: {
  status: UserHeightRecordingStatusResponseT;
}) {
  return (
    <div className="flex flex-col h-full rounded-t-lg xs:rounded-b-lg bg-background-60 xs:py-2 px-2 pt-4 relative">
      <div className="flex flex-col bg-background-60 rounded-lg">
        <div className="px-4">
          <Typography variant="mobile-title">Calibration progress</Typography>
        </div>
        <div className="flex flex-col">
          {!errorSteps.includes(status.status) ? (
            <div className="py-2">
              <UserHeightStep
                done={
                  status.status >
                  statusSteps.indexOf(
                    UserHeightCalibrationStatus.RECORDING_FLOOR
                  )
                }
                text="onboarding-user_height-calibration-RECORDING_FLOOR"
              />
              <UserHeightStep
                done={
                  status.status >
                  statusSteps.indexOf(
                    UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH
                  )
                }
                text="onboarding-user_height-calibration-WAITING_FOR_CONTROLLER_PITCH"
              />
              <UserHeightStep
                done={
                  status.status >
                  statusSteps.indexOf(
                    UserHeightCalibrationStatus.WAITING_FOR_RISE
                  )
                }
                text="onboarding-user_height-calibration-WAITING_FOR_RISE"
              />
              <UserHeightStep
                done={
                  status.status >
                  statusSteps.indexOf(
                    UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK
                  )
                }
                text="onboarding-user_height-calibration-WAITING_FOR_FW_LOOK"
              />
              <UserHeightStep
                done={
                  status.status >
                  statusSteps.indexOf(
                    UserHeightCalibrationStatus.RECORDING_HEIGHT
                  )
                }
                text="onboarding-user_height-calibration-RECORDING_HEIGHT"
              />
            </div>
          ) : (
            <div className="m-4 p-2 outline outline-status-critical rounded-lg">
              <Typography
                variant="section-title"
                id="onboarding-user_height-calibration-error"
              />
              <Typography
                id={`onboarding-user_height-calibration-${UserHeightCalibrationStatus[status.status]}`}
              />
            </div>
          )}
        </div>
      </div>

      <div className="flex flex-grow justify-center h-full min-h-0 items-center">
        {statusToImage[status.status] && (
          <div className="h-full w-full flex justify-center">
            <img
              className="object-contain h-full"
              src={statusToImage[status.status]!}
            />
          </div>
        )}
        {status.status === UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK && (
          <div className="h-full w-full flex p-2">
            <div className="grid xs:grid-rows-3 h-full w-full gap-3">
              <div className="bg-background-70 rounded-md flex gap-4 justify-between items-center px-4 relative">
                <CheckIcon className="md:w-12 sm:w-8 w-6 h-auto absolute top-2 left-4 fill-status-success" />
                <img
                  className="object-cover h-full aspect-square"
                  src={'/images/user-height/look-forward-ok.webp'}
                />
                <Typography variant="main-title">
                  Make sure your head is leveled
                </Typography>
              </div>
              <div className="bg-background-70 rounded-md flex gap-4 justify-between items-center px-4 relative">
                <CrossIcon className="md:w-14 sm:w-8 w-6 h-auto absolute top-2 left-2 fill-status-critical" />

                <img
                  className="object-cover h-full aspect-square"
                  src={'/images/user-height/look-forward-low.webp'}
                />
                <Typography variant="main-title">
                  Do not look at the floor
                </Typography>
              </div>
              <div className="bg-background-70 rounded-md flex gap-4 justify-between items-center px-4 relative">
                <CrossIcon className="md:w-14 sm:w-8 w-6 h-auto absolute top-2 left-2 fill-status-critical" />
                <img
                  className="object-cover h-full aspect-square"
                  src={'/images/user-height/look-forward-high.webp'}
                />
                <Typography variant="main-title">
                  Do not look too high up
                </Typography>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export function ScaledProportionsPage() {
  const [hmdHeight, setHmdHeight] = useState(0);
  const { applyProgress, state } = useOnboarding();

  const serverGuards = useAtomValue(serverGuardsAtom);

  const [status, setState] = useState<UserHeightRecordingStatusResponseT>();
  const [auto, setAuto] = useState(false);
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  applyProgress(0.9);

  const start = () => {
    sendRPCPacket(
      RpcMessage.StartUserHeightCalibration,
      new StartUserHeightCalibrationT()
    );
  };

  const cancel = () => {
    sendRPCPacket(
      RpcMessage.CancelUserHeightCalibration,
      new CancelUserHeightCalibrationT()
    );
  };

  // Makes it so you dont get spammed by sounds if multiple status complete at once
  useDebouncedEffect(
    () => {
      if (
        !status ||
        errorSteps.includes(status.status) ||
        status.status == UserHeightCalibrationStatus.NONE ||
        status.status == UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK ||
        status.status == UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH
      )
        return;
      playTapSetupSound();
    },
    [status?.status],
    300
  );

  const applyHeight = (newHeight: number) => {
    setHmdHeight(newHeight);
    const settingsRequest = new ChangeSettingsRequestT();
    settingsRequest.modelSettings = new ModelSettingsT(
      null,
      null,
      null,
      new SkeletonHeightT(newHeight, 0)
    );
    sendRPCPacket(RpcMessage.ChangeSettingsRequest, settingsRequest);
    sendRPCPacket(
      RpcMessage.SkeletonResetAllRequest,
      new SkeletonResetAllRequestT()
    );
  };

  useRPCPacket(
    RpcMessage.UserHeightRecordingStatusResponse,
    (res: UserHeightRecordingStatusResponseT) => {
      if (res.status !== UserHeightCalibrationStatus.NONE) {
        setAuto(true);
      }

      setState(res);
      setHmdHeight(res.hmdHeight);
      if (res.status === UserHeightCalibrationStatus.DONE) {
        applyHeight(res.hmdHeight);
      }

      if (errorSteps.includes(res.status) && res.hmdHeight == 0) {
        applyHeight(DEFAULT_FULL_HEIGHT);
      }
    }
  );

  useRPCPacket(
    RpcMessage.SkeletonConfigResponse,
    (res: SkeletonConfigResponseT) => {
      setHmdHeight(res.userHeight);
    }
  );

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.SkeletonConfigRequest,
      new SkeletonConfigRequestT()
    );
    return () => {
      cancel();
    };
  }, []);

  useEffect(() => {
    const checkNotAuto = (status: UserHeightCalibrationStatus) =>
      status === UserHeightCalibrationStatus.DONE ||
      errorSteps.includes(status);

    if (status && checkNotAuto(status.status)) {
      const id = setTimeout(
        () => {
          if (status && checkNotAuto(status.status)) {
            setAuto(false);
            sendRPCPacket(
              RpcMessage.SkeletonConfigRequest,
              new SkeletonConfigRequestT()
            ); // Re ask the user height so it resets back to the correct value
          }
        },
        status.status === UserHeightCalibrationStatus.DONE ? 5000 : 10_000
      );
      return () => {
        clearTimeout(id);
      };
    }
  }, [status]);

  return (
    <div
      className={classNames(
        'flex gap-2 w-full h-full relative justify-center z-10',
        {
          'p-4': !state.alonePage,
          'bg-background-70': state.alonePage,
        }
      )}
    >
      <div className="h-full max-w-2xl w-full flex flex-col justify-end xs:py-2 z-10 xs:gap-2 pointer-events-none">
        {!auto && (
          <div className="p-0 xs:p-2">
            <Localized id="onboarding-user_height-manual-tip">
              <TipBox className="p-2 xs:p-4">PRO TIP</TipBox>
            </Localized>
          </div>
        )}
        <div
          className={classNames(
            'flex-grow transition-opacity duration-200 overflow-hidden',
            { 'opacity-0': !auto, 'opacity-100 pointer-events-auto': auto }
          )}
        >
          {status && <UserHeightStatus status={status} />}
        </div>

        <div
          className={classNames(
            'flex flex-col gap-3 p-4 bg-background-60 rounded-b-lg xs:rounded-t-lg pointer-events-auto',
            { 'rounded-t-lg': !auto }
          )}
        >
          <Typography
            variant="mobile-title"
            id="onboarding-user_height-title"
          />
          <HeightSelectionInput
            hmdHeight={hmdHeight}
            setHmdHeight={(height) => {
              applyHeight(height);
              setAuto(false);
            }}
          />
          <Tooltip
            disabled={serverGuards?.canDoUserHeightCalibration}
            preferedDirection="top"
            content={
              <Typography id="onboarding-user_height-need_head_tracker" />
            }
          >
            <Button
              variant="primary"
              disabled={!serverGuards?.canDoUserHeightCalibration}
              onClick={start}
              id="onboarding-user_height-calculate"
            />
          </Tooltip>
          <div className="w-full flex justify-between">
            {state.alonePage && (
              <>
                <Button
                  variant="tertiary"
                  id="onboarding-user_height-manual-proportions"
                  to="/onboarding/body-proportions/manual"
                  state={{ alonePage: state.alonePage }}
                />
                <ResetButton
                  type={ResetType.Full}
                  className="bg-background-50 hover:bg-background-40 text-background-10"
                />
              </>
            )}
            {!state.alonePage && (
              <Button
                variant="primary"
                id="onboarding-user_height-next_step"
                to="/"
              />
            )}
          </div>
        </div>
      </div>
      <div className="absolute top-0 left-0 w-full h-full">
        <SkeletonVisualizerWidget
          onInit={(context) => {
            context.addView({
              left: 0,
              bottom: 0,
              width: 1,
              height: 1,
              position: new Vector3(3, 2.5, -3),
              onHeightChange(v, newHeight) {
                v.controls.target.set(0, newHeight / 2.9, 0);
                const scale = Math.max(1, newHeight) / 1;
                v.camera.zoom = 1 / scale;
              },
            });
          }}
        />
      </div>
    </div>
  );
}
