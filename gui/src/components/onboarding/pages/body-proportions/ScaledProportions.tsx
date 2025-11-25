import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import { HeightContextC, useProvideHeightContext } from '@/hooks/height';
import { Button } from '@/components/commons/Button';
import { Localized } from '@fluent/react';
import { TipBox } from '@/components/commons/TipBox';
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
import { ResetButton } from '@/components/home/ResetButton';
import { Vector3 } from 'three';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';

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

export function ScaledProportionsPage() {
  const [hmdHeight, setHmdHeight] = useState(0);
  const { applyProgress, state } = useOnboarding();
  const heightContext = useProvideHeightContext();

  const serverGuards = useAtomValue(serverGuardsAtom);

  const [status, setState] = useState<UserHeightRecordingStatusResponseT>();
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
      setState(res);
      setHmdHeight(res.hmdHeight);
      if (res.status === UserHeightCalibrationStatus.DONE) {
        applyHeight(res.hmdHeight);
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

  const auto = status && status.status !== UserHeightCalibrationStatus.NONE;

  return (
    <HeightContextC.Provider value={heightContext}>
      <div
        className={classNames('flex bg-background-80 gap-2 w-full h-full', {
          'p-4': !state.alonePage,
        })}
      >
        <div
          className={classNames('flex rounded-lg p-4 flex-grow', {
            'bg-background-70': state.alonePage,
          })}
        >
          <div className="justify-center items-center flex flex-grow flex-col">
            <div className="flex flex-col gap-3 max-w-xl flex-grow justify-center">
              <Typography
                variant="main-title"
                id="onboarding-user_height-title"
              />
              <div>
                <Typography id="onboarding-user_height-description" />
              </div>
              <HeightSelectionInput
                hmdHeight={hmdHeight}
                setHmdHeight={applyHeight}
              />
              <Tooltip
                disabled={serverGuards?.canDoUserHeightCalibration}
                preferedDirection="bottom"
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
            </div>
            <div className="flex w-full gap-2 justify-between">
              <div>
                {state.alonePage && (
                  <Button
                    variant="secondary"
                    id="onboarding-user_height-manual-proportions"
                    to="/onboarding/body-proportions/manual"
                    state={{ alonePage: state.alonePage }}
                  />
                )}
              </div>
              {!state.alonePage && (
                <Button
                  variant="primary"
                  id="onboarding-user_height-next_step"
                />
              )}
            </div>
          </div>
        </div>
        <div className="flex w-1/3 bg-background-70 rounded-lg relative flex-col h-full">
          <div
            className={classNames(
              'absolute h-full w-full transition-opacity duration-300 p-4',
              { 'opacity-100': auto, 'opacity-0': !auto }
            )}
          >
            {status && (
              <div className="flex flex-col h-full">
                <div className="flex flex-col">
                  {!errorSteps.includes(status.status) ? (
                    <div className="rounded-lg py-4 bg-background-60">
                      {status.canDoFloorHeight ? (
                        <>
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
                                UserHeightCalibrationStatus.WAITING_FOR_RISE
                              )
                            }
                            text="onboarding-user_height-calibration-WAITING_FOR_RISE"
                          />
                        </>
                      ) : (
                        <UserHeightStep
                          done={
                            status.status >
                            statusSteps.indexOf(
                              UserHeightCalibrationStatus.WAITING_FOR_RISE
                            )
                          }
                          text="onboarding-user_height-calibration-WAITING_FOR_RISE-no_floor"
                        />
                      )}
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
                    <div className="rounded-lg bg-background-60 p-4 outline outline-status-critical">
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

                <div className="flex flex-grow justify-center min-h-0">
                  <img
                    className="object-contain h-full"
                    src={
                      errorSteps.includes(status.status)
                        ? '/images/nighty-height-error.webp'
                        : '/images/nighty-height-question.webp'
                    }
                  />
                </div>
              </div>
            )}
          </div>
          <div
            className={classNames(
              'w-full flex flex-grow transition-opacity duration-300 overflow-clip',
              {
                'opacity-100': !auto,
                'opacity-0': auto,
              }
            )}
          >
            <SkeletonVisualizerWidget
              onInit={(context) => {
                context.addView({
                  left: 0,
                  bottom: 0,
                  width: 1,
                  height: 1,
                  position: new Vector3(3, 2.5, -3),
                  onHeightChange(v, newHeight) {
                    v.controls.target.set(0, newHeight / 2, 0);
                    const scale = Math.max(1, newHeight) / 1.2;
                    v.camera.zoom = 1 / scale;
                  },
                });
              }}
            />
            <div className="absolute w-full p-4 flex h-20">
              <ResetButton
                type={ResetType.Full}
                className="w-full h-full bg-background-50 hover:bg-background-40 text-background-10"
              />
            </div>
            <div className="absolute bottom-0 p-4 w-full">
              <Localized id="onboarding-user_height-manual-tip">
                <TipBox className="p-4">PRO TIP</TipBox>
              </Localized>
            </div>
          </div>
        </div>
      </div>
    </HeightContextC.Provider>
  );
}
