import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
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
import { restartAndPlay, scaledProportionsClick } from '@/sounds/sounds';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';
import { TipBox } from '@/components/commons/TipBox';
import { Localized } from '@fluent/react';
import { ResetButton } from '@/components/home/ResetButton';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useConfig } from '@/hooks/config';
import { ProportionsResetModal } from './ProportionsResetModal';
import * as Sentry from '@sentry/react';

const statusSteps = [
  // Order matters be carefull
  UserHeightCalibrationStatus.NONE,
  UserHeightCalibrationStatus.RECORDING_FLOOR,
  UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH,
  UserHeightCalibrationStatus.WAITING_FOR_RISE,
  UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK,
  UserHeightCalibrationStatus.RECORDING_HEIGHT,
  UserHeightCalibrationStatus.DONE,
];

const progressSteps: UserHeightCalibrationStatus[] = statusSteps.filter(
  (s) => s !== UserHeightCalibrationStatus.NONE
);

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

function Stepper({ status }: { status: UserHeightRecordingStatusResponseT }) {
  const stepIndex = progressSteps.indexOf(status.status);
  const isError = errorSteps.includes(status.status);
  const progress = isError ? 1 : (stepIndex + 1) / progressSteps.length;

  const { isXs } = useBreakpoint('xs');

  return (
    <div className="flex flex-col gap-2 px-2">
      <div className="flex gap-2 items-center">
        <div
          className={classNames(
            'w-8 aspect-square rounded-full fill-background-10 flex items-center justify-center',
            {
              'bg-background-70':
                status.status !== UserHeightCalibrationStatus.DONE && !isError,
              'bg-accent-background-10':
                status.status === UserHeightCalibrationStatus.DONE,
              'bg-status-critical': isError,
            }
          )}
        >
          {status.status !== UserHeightCalibrationStatus.DONE && !isError && (
            <Typography variant={isXs ? 'section-title' : 'standard'}>
              {stepIndex + 1}
            </Typography>
          )}
          {status.status === UserHeightCalibrationStatus.DONE && (
            <CheckIcon size={12} />
          )}
          {isError && <CrossIcon />}
        </div>
        <Typography
          id={`onboarding-user_height-calibration-${UserHeightCalibrationStatus[status.status]}`}
          variant={isXs ? 'section-title' : 'standard'}
        />
      </div>
      <ProgressBar
        progress={progress}
        animated
        colorClass={
          status.status === UserHeightCalibrationStatus.DONE
            ? 'bg-status-success'
            : isError
              ? 'bg-status-critical'
              : undefined
        }
      />
    </div>
  );
}

function UserHeightStatus({
  status,
}: {
  status: UserHeightRecordingStatusResponseT;
}) {
  const { isXs } = useBreakpoint('xs');

  return (
    <div className="flex flex-col h-full rounded-t-lg xs:rounded-b-lg bg-background-60 xs:py-2 px-2 pt-4 relative">
      <div className="flex flex-col bg-background-60 rounded-lg">
        <div className="px-4 hidden xs:block">
          <Typography
            variant="mobile-title"
            id="onboarding-user_height-calibration-title"
          />
        </div>
        <div className="flex flex-col py-2">
          <Stepper status={status} />
        </div>
      </div>

      <div className="flex flex-grow justify-center h-full min-h-0 items-center">
        {statusToImage[status.status] && (
          <div className="h-full w-full flex justify-center max-h-[950px]">
            <img
              className="object-contain h-full"
              src={statusToImage[status.status]!}
            />
          </div>
        )}
        {status.status === UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK && (
          <div className="h-full w-full flex p-2">
            <div className="grid grid-rows-3 h-full w-full gap-1 xs:gap-3 max-h-[950px]">
              <div className="bg-background-70 rounded-md flex gap-4 items-center px-4 relative">
                <CheckIcon className="sm:w-8 w-5 h-auto absolute xs:top-2 xs:left-2 -top-1 tall:w-8 tall:left-2 tall:top-2 -left-2 fill-status-success" />
                <img
                  className="object-cover h-full aspect-square"
                  src={'/images/user-height/look-forward-ok.webp'}
                />
                <Typography
                  variant={isXs ? 'section-title' : 'standard'}
                  whitespace="whitespace-pre-wrap"
                  id="onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-ok"
                />
              </div>
              <div className="bg-background-70 rounded-md flex gap-4 items-center px-4 relative">
                <CrossIcon className="sm:w-8 w-6 h-auto absolute xs:top-2 xs:left-2 -top-1 -left-2 tall:w-9 tall:left-1 tall:top-2 fill-status-critical" />

                <img
                  className="object-cover h-full aspect-square"
                  src={'/images/user-height/look-forward-low.webp'}
                />
                <Typography
                  variant={isXs ? 'section-title' : 'standard'}
                  whitespace="whitespace-pre-wrap"
                  id="onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-low"
                />
              </div>
              <div className="bg-background-70 rounded-md flex gap-4 items-center px-4 relative">
                <CrossIcon className="sm:w-8 w-6 h-auto absolute xs:top-2 xs:left-2 -top-1 -left-2 tall:w-9 tall:left-1 tall:top-2 fill-status-critical" />
                <img
                  className="object-cover h-full aspect-square"
                  src={'/images/user-height/look-forward-high.webp'}
                />
                <Typography
                  variant={isXs ? 'section-title' : 'standard'}
                  whitespace="whitespace-pre-wrap"
                  id="onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-high"
                />
              </div>
            </div>
          </div>
        )}
        {status.status ===
          UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH && (
          <div className="h-full w-full flex p-2">
            <div className="flex tall:grid grid-cols-2 grid-rows-2 h-full w-full gap-3 max-h-[950px]">
              <div className="bg-background-70 rounded-md flex gap-4 justify-between items-end px-4 relative  overflow-clip">
                <CheckIcon className="sm:w-9 w-5 h-auto absolute top-2 left-2 justify-center fill-status-success" />
                <img
                  className="object-bottom object-cover w-full"
                  src={'/images/user-height/controller-ok.webp'}
                />
              </div>
              <div className="bg-background-70 rounded-md flex gap-4 justify-between items-end px-4 relative  overflow-clip">
                <CrossIcon className="sm:w-10 w-6 h-auto absolute top-2 left-2 fill-status-critical" />
                <img
                  className="object-bottom object-cover w-full"
                  src={'/images/user-height/controller-wrong-1.webp'}
                />
              </div>
              <div className="bg-background-70 rounded-md flex gap-4 justify-between items-end px-4 relative  overflow-clip">
                <CrossIcon className="sm:w-10 w-6 h-auto absolute top-2 left-2 fill-status-critical" />
                <img
                  className="object-bottom object-cover w-full"
                  src={'/images/user-height/controller-wrong-2.webp'}
                />
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
  const [tmpHeight, setTmpHeight] = useState(0);
  const [lastUsed, setLastUsed] = useState<'manual' | 'auto' | null>(null);
  const { config, setConfig } = useConfig();
  const { applyProgress, state } = useOnboarding();

  const serverGuards = useAtomValue(serverGuardsAtom);

  const [status, setState] = useState<UserHeightRecordingStatusResponseT>();
  const [auto, setAuto] = useState(false);
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [resetModal, setResetModal] = useState<null | 'manual' | 'auto'>(null);

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
      if (!config || !config.feedbackSound) return;
      if (
        !status ||
        errorSteps.includes(status.status) ||
        status.status == UserHeightCalibrationStatus.NONE ||
        status.status == UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK ||
        status.status ==
          UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH
      )
        return;
      restartAndPlay(scaledProportionsClick, config.feedbackSoundVolume);
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
    setConfig({ lastUsedProportions: 'scaled' });
    setLastUsed('manual');
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
        setConfig({ lastUsedProportions: 'scaled' });
        setLastUsed('auto');
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
    if (lastUsed !== null) {
      Sentry.metrics.count('scaled_proportions', 1, {
        attributes: { calibration: lastUsed },
      });
    }
  }, [lastUsed]);

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
        status.status === UserHeightCalibrationStatus.DONE ? 2000 : 10_000
      );
      return () => {
        clearTimeout(id);
      };
    }
  }, [status]);

  const acceptHeight = () => {
    if (resetModal === 'manual') {
      applyHeight(tmpHeight);
    } else if (resetModal === 'auto') {
      start();
    }

    setResetModal(null);
  };

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
      <ProportionsResetModal
        isOpen={resetModal !== null}
        onClose={() => setResetModal(null)}
        accept={acceptHeight}
      />
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
              if (
                config?.lastUsedProportions != null &&
                config.lastUsedProportions !== 'scaled'
              ) {
                setTmpHeight(height);
                setResetModal('manual');
              } else {
                applyHeight(height);
              }
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
              onClick={() => {
                if (
                  config?.lastUsedProportions != null &&
                  config.lastUsedProportions !== 'scaled'
                ) {
                  setResetModal('auto');
                } else {
                  start();
                }
              }}
              id="onboarding-user_height-calculate"
            />
          </Tooltip>
          <div className="w-full flex gap-2 justify-between">
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
