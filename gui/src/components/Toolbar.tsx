import { Typography } from './commons/Typography';
import classNames from 'classnames';
import { ResetType } from 'solarxr-protocol';
import { ResetBtnStatus, useReset } from '@/hooks/reset';
import { Tooltip } from './commons/Tooltip';
import { GearIcon } from './commons/icon/GearIcon';
import { useAtomValue } from 'jotai';
import { connectedTrackersAtom } from '@/store/app-store';
import { ClearIcon } from './commons/icon/ClearIcon';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useMemo, useState } from 'react';
import { HomeSettingsModal } from './home/HomeSettingsModal';
import { SkiIcon } from './commons/icon/SkiIcon';
import { FullResetIcon, YawResetIcon } from './commons/icon/ResetIcon';

const MAINBUTTON_CLASSES = ({ disabled }: { disabled: boolean }) =>
  classNames(
    'p-2 flex justify-center px-4 gap-4 h-full items-center hover:bg-background-50 bg-background-60 relative overflow-clip aspect-square md:aspect-auto',
    {
      'cursor-pointer': !disabled,
      'cursor-not-allowed': disabled,
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
        'absolute top-0 left-0 w-0 h-full bg-background-70 opacity-50 transition-all duration-1000 ease-linear',
        { 'duration-150': status === 'finished' }
      )}
      style={{ width: `${progress * 100}%` }}
    ></div>
  );
}

function BasicResetButton({ type }: { type: ResetType }) {
  const { isNmd } = useBreakpoint('nmd');
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
      disabled={!isNmd}
      content={<Typography textAlign="text-center" id={name}></Typography>}
      preferedDirection="top"
    >
      <div
        className={classNames(
          MAINBUTTON_CLASSES({ disabled }),
          {
            'animate-bounce': status === 'finished',
          },
          'rounded-lg'
        )}
        style={{
          animationIterationCount: 1,
        }}
        onClick={() => !disabled && triggerReset()}
      >
        {icon}

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

function MountingCalibrationButton() {
  const { isNmd } = useBreakpoint('nmd');
  const { triggerReset, status, name, timer, disabled, duration } = useReset(
    ResetType.Mounting
  );

  const progress = status === 'counting' ? 1 - (timer - 1) / duration : 0;

  return (
    <div
      className={classNames('flex rounded-lg overflow-clip relative', {
        'animate-bounce': status === 'finished',
      })}
      style={{
        animationIterationCount: 1,
      }}
    >
      <Tooltip
        disabled={!isNmd}
        content={<Typography textAlign="text-center" id={name}></Typography>}
        preferedDirection="top"
      >
        <div
          className={classNames(MAINBUTTON_CLASSES({ disabled }), 'flex-grow')}
          onClick={() => !disabled && triggerReset()}
        >
          <div className="fill-background-10">
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
            MAINBUTTON_CLASSES({ disabled }),
            'fill-background-10 rounded-r-lg border-l-[2px] border-background-50 md:w-16 aspect-square md:aspect-auto'
          )}
        >
          <div className="rotate-12">
            <ClearIcon></ClearIcon>
          </div>
        </div>
      </Tooltip>
      <ButtonProgress progress={progress} status={status}></ButtonProgress>
    </div>
  );
}

export function Toolbar({ showSettings }: { showSettings: boolean }) {
  const trackers = useAtomValue(connectedTrackersAtom);
  const settingsOpenState = useState(false);
  const [, setSettingsOpen] = settingsOpenState;

  return (
    <>
      <HomeSettingsModal open={settingsOpenState}></HomeSettingsModal>
      <div className="flex pb-3 py-1 mobile:py-2 flex-col items-center bg-background-70 rounded-t-lg h-[var(--toolbar-h)] mr-2 mt-2 mobile:mr-0">
        <div className="gap-4 px-4 py-4 flex-grow flex md:grid md:grid-cols-3 max-w-[1200px] md:w-full grid-cols-2">
          <BasicResetButton type={ResetType.Yaw}></BasicResetButton>
          <BasicResetButton type={ResetType.Full}></BasicResetButton>
          <MountingCalibrationButton></MountingCalibrationButton>
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
              <GearIcon size={22}></GearIcon>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
