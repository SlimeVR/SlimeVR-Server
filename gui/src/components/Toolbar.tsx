import { Typography } from './commons/Typography';
import classNames from 'classnames';
import {
  ClearMountingResetRequestT,
  ResetType,
  RpcMessage,
} from 'solarxr-protocol';
import { ResetBtnStatus, useReset } from '@/hooks/reset';
import { Tooltip } from './commons/Tooltip';
import { useAtomValue } from 'jotai';
import { assignedTrackersAtom, connectedTrackersAtom } from '@/store/app-store';
import { ClearIcon } from './commons/icon/ClearIcon';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useMemo, useState } from 'react';
import { HomeSettingsModal } from './home/HomeSettingsModal';
import { SkiIcon } from './commons/icon/SkiIcon';
import { FullResetIcon, YawResetIcon } from './commons/icon/ResetIcon';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { QuaternionFromQuatT, similarQuaternions } from '@/maths/quaternion';
import { Quaternion } from 'three';
import { LayoutIcon } from './commons/icon/LayoutIcon';
import { FootIcon } from './commons/icon/FootIcon';
import { FingersIcon } from './commons/icon/FingersIcon';

const MAINBUTTON_CLASSES = ({ disabled }: { disabled: boolean }) =>
  classNames(
    'p-2 flex justify-center px-4 gap-4 h-full items-center bg-background-60 relative overflow-clip aspect-square md:aspect-auto',
    {
      'cursor-pointer hover:bg-background-50 bg-background-60': !disabled,
      'cursor-not-allowed bg-background-70 brightness-75': disabled,
    }
  );

function ButtonProgress({
  progress,
  status,
}: {
  progress: number;
  status: ResetBtnStatus;
}) {
  return (
    <div
      className={classNames(
        'absolute top-0 left-0 w-0 h-full bg-accent-background-20 opacity-50 transition-all duration-1000 ease-linear',
        { 'duration-150': status === 'finished' }
      )}
      style={{ width: `${progress * 100}%` }}
    ></div>
  );
}

function BasicResetButton({ type }: { type: ResetType }) {
  const { isMd } = useBreakpoint('md');
  const { triggerReset, status, name, timer, disabled, duration } =
    useReset(type);

  const icon = useMemo(() => {
    switch (type) {
      case ResetType.Yaw:
        return <YawResetIcon width={24} />;
    }
    return <FullResetIcon width={24} />;
  }, [type]);

  const progress = status === 'counting' ? 1 - (timer - 1) / duration : 0;

  return (
    <Tooltip
      disabled={isMd}
      content={<Typography textAlign="text-center" id={name}></Typography>}
      preferedDirection="top"
    >
      <div
        className={classNames(MAINBUTTON_CLASSES({ disabled }), 'rounded-lg')}
        style={{
          animationIterationCount: 1,
        }}
        onClick={() => !disabled && triggerReset()}
      >
        <div
          className={classNames({
            'animate-spin-ccw': status === 'finished',
          })}
          style={{
            animationIterationCount: 1,
          }}
        >
          {icon}
        </div>

        <div className="hidden md:block">
          <Typography
            variant="section-title"
            textAlign="text-center"
            id={name}
          ></Typography>
        </div>
        <ButtonProgress progress={progress} status={status}></ButtonProgress>
      </div>
    </Tooltip>
  );
}

const _q = new Quaternion();
function MountingCalibrationButton() {
  const { isNmd } = useBreakpoint('nmd');
  const { triggerReset, status, name, timer, disabled, duration } = useReset(
    ResetType.Mounting
  );
  const assignedTrackers = useAtomValue(assignedTrackersAtom);

  const { sendRPCPacket } = useWebsocketAPI();
  const trackerWithMounting = useMemo(
    () =>
      assignedTrackers.some(
        (d) =>
          !similarQuaternions(
            QuaternionFromQuatT(d?.tracker.info?.mountingResetOrientation),
            _q
          )
      ),
    [assignedTrackers]
  );

  const clearMounting = () => {
    const record = new ClearMountingResetRequestT();
    sendRPCPacket(RpcMessage.ClearMountingResetRequest, record);
  };

  const progress = status === 'counting' ? 1 - (timer - 1) / duration : 0;

  const [open, setOpen] = useState(false);

  return (
    <>
      <div
        className={classNames('flex relative')}
        style={{
          animationIterationCount: 1,
        }}
      >
        <div className="rounded-lg overflow-clip flex w-full">
          <Tooltip
            disabled={!isNmd}
            content={
              <Typography textAlign="text-center" id={name}></Typography>
            }
            preferedDirection="top"
          >
            <div
              className={classNames(
                MAINBUTTON_CLASSES({ disabled }),
                'flex-grow'
              )}
              onClick={() => !disabled && setOpen(true)}
            >
              <div
                className={classNames(
                  {
                    'animate-skiing': status === 'finished',
                  },
                  'fill-background-10'
                )}
                style={{
                  animationIterationCount: 1,
                }}
              >
                <SkiIcon size={24} />
              </div>
              <div className="hidden md:block">
                <Typography
                  variant="section-title"
                  textAlign="text-center"
                  id={name}
                ></Typography>
              </div>
            </div>
          </Tooltip>
          <Tooltip
            content={
              <Typography variant="vr-accessible">
                Clear Mounting Calibration
              </Typography>
            }
            preferedDirection="top"
          >
            <div
              className={classNames(
                MAINBUTTON_CLASSES({
                  disabled: !trackerWithMounting || disabled,
                }),
                'fill-background-10 rounded-r-lg border-l-[2px] border-background-50 md:w-16 aspect-square md:aspect-auto'
              )}
              onClick={() => trackerWithMounting && !disabled && clearMounting}
            >
              <div className="rotate-12">
                <ClearIcon></ClearIcon>
              </div>
            </div>
          </Tooltip>
          <ButtonProgress progress={progress} status={status}></ButtonProgress>
        </div>

        <div
          className={classNames(
            'absolute bottom-0 left-0 translate-y-full w-full z-[60]',
            {
              hidden: !open,
            }
          )}
        >
          <div className="h-20 flex relative rounded-lg overflow-clip mt-2">
            <Tooltip
              disabled={!isNmd}
              content={
                <Typography textAlign="text-center" id={name}></Typography>
              }
              preferedDirection="top"
            >
              <div
                className={classNames(
                  MAINBUTTON_CLASSES({ disabled }),
                  'flex-grow'
                )}
                onClick={() => !disabled && setOpen(false) && triggerReset()}
              >
                <div
                  className={classNames(
                    {
                      'animate-skiing': status === 'finished',
                    },
                    'fill-background-10'
                  )}
                  style={{
                    animationIterationCount: 1,
                  }}
                >
                  <SkiIcon size={22} />
                </div>
                <div className="hidden md:block">
                  <Typography variant="section-title" textAlign="text-center">
                    Body mounting calibration
                  </Typography>
                </div>
              </div>
            </Tooltip>
          </div>
          <div className="h-20 flex relative rounded-lg overflow-clip mt-2">
            <Tooltip
              disabled={!isNmd}
              content={
                <Typography textAlign="text-center" id={name}></Typography>
              }
              preferedDirection="top"
            >
              <div
                className={classNames(
                  MAINBUTTON_CLASSES({ disabled }),
                  'flex-grow'
                )}
                onClick={() => !disabled && setOpen(true)}
              >
                <div
                  className={classNames(
                    {
                      'animate-skiing': status === 'finished',
                    },
                    'fill-background-10'
                  )}
                  style={{
                    animationIterationCount: 1,
                  }}
                >
                  <FootIcon />
                </div>
                <div className="hidden md:block">
                  <Typography variant="section-title" textAlign="text-center">
                    Feet mounting calibration
                  </Typography>
                </div>
              </div>
            </Tooltip>
          </div>
          <div className="h-20 flex relative rounded-lg overflow-clip mt-2">
            <Tooltip
              disabled={!isNmd}
              content={
                <Typography textAlign="text-center" id={name}></Typography>
              }
              preferedDirection="top"
            >
              <div
                className={classNames(
                  MAINBUTTON_CLASSES({ disabled }),
                  'bg-background-70',
                  'flex-grow'
                )}
                onClick={() => !disabled && setOpen(true)}
              >
                <div
                  className={classNames(
                    {
                      'animate-skiing': status === 'finished',
                    },
                    'fill-background-50'
                  )}
                  style={{
                    animationIterationCount: 1,
                  }}
                >
                  <FingersIcon width={20} />
                </div>
                <div className="hidden md:block">
                  <Typography
                    variant="section-title"
                    textAlign="text-center"
                    color="text-background-50"
                  >
                    Fingers Mounting calibration
                  </Typography>
                </div>
              </div>
            </Tooltip>
          </div>
        </div>
      </div>
      <div
        className={classNames(
          'fixed top-0 left-0 w-screen h-screen bg-background-90 z-50 bg-opacity-50',
          { hidden: !open }
        )}
        onClick={() => setOpen(false)}
      ></div>
    </>
  );
}

export function Toolbar({ showSettings }: { showSettings: boolean }) {
  const trackers = useAtomValue(connectedTrackersAtom);
  const settingsOpenState = useState(false);
  const [, setSettingsOpen] = settingsOpenState;

  return (
    <>
      <HomeSettingsModal open={settingsOpenState}></HomeSettingsModal>
      <div className="flex mobile:py-2 flex-col items-center bg-background-70 rounded-t-lg h-[var(--toolbar-h)] mr-2 mt-2 mobile:mr-0">
        <div className="px-1 py-3 w-full divide-x-2 divide-background-50 flex justify-center md:justify-start">
          <div className="flex-col flex gap-1 px-2 md:w-[60%]">
            <Typography variant="section-title">Drift Resets</Typography>
            <div className="gap-2 md:h-[72px] h-[62px] w-full grid-cols-2 grid">
              <BasicResetButton type={ResetType.Full}></BasicResetButton>
              <BasicResetButton type={ResetType.Yaw}></BasicResetButton>
            </div>
          </div>
          <div className="flex-col flex gap-1 px-2 md:flex-grow">
            <div className="">
              <Typography variant="section-title">
                Mounting Calibration
              </Typography>
            </div>
            <div className="gap-2 md:h-[72px] h-[62px] w-full grid-cols-3 grid">
              <div className="flex h-full items-center justify-center gap-2 px-4 bg-background-60 rounded-lg fill-background-10 aspect-square md:aspect-auto">
                <SkiIcon></SkiIcon>
                <div className="hidden md:inline-block">
                  <Typography>Body</Typography>
                </div>
              </div>
              <div className="flex h-full items-center justify-center gap-2 px-4 bg-background-60 rounded-lg fill-background-10 aspect-square md:aspect-auto">
                <FootIcon></FootIcon>
                <div className="hidden md:inline-block">
                  <Typography>Foot</Typography>
                </div>
              </div>
              <div className="flex h-full items-center justify-center gap-2 px-4 bg-background-60 rounded-lg fill-background-10 aspect-square md:aspect-auto">
                <FingersIcon width={16}></FingersIcon>
                <div className="hidden md:inline-block">
                  <Typography>Fingers</Typography>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className="flex w-full gap-2 items-center px-4 h-5">
          <Typography color="secondary">
            {trackers.length} trackers connected
          </Typography>
          <div className="bg-background-50 h-[2px] rounded-lg flex-grow"></div>
          {showSettings && (
            <div
              className="fill-background-30 hover:fill-background-20 cursor-pointer"
              onClick={() => setSettingsOpen(true)}
            >
              <LayoutIcon size={18}></LayoutIcon>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
