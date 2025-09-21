import { Typography } from './commons/Typography';
import classNames from 'classnames';
import { ResetType } from 'solarxr-protocol';
import {
  BODY_PARTS_GROUPS,
  MountingResetGroup,
  ResetBtnStatus,
  useReset,
  UseResetOptions,
} from '@/hooks/reset';
import { Tooltip } from './commons/Tooltip';
import { useAtomValue } from 'jotai';
import { assignedTrackersAtom, connectedTrackersAtom } from '@/store/app-store';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useMemo, useState } from 'react';
import { HomeSettingsModal } from './home/HomeSettingsModal';
import { LayoutIcon } from './commons/icon/LayoutIcon';
import { ResetButtonIcon } from './home/ResetButton';

const MAINBUTTON_CLASSES = ({ disabled }: { disabled: boolean }) =>
  classNames(
    'relative overflow-clip',
    'flex h-full items-center justify-center gap-2 px-4 bg-background-60 rounded-lg fill-background-10 aspect-square md:aspect-auto',
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

function BasicResetButton(options: UseResetOptions & { customName?: string }) {
  const { isMd } = useBreakpoint('md');
  const {
    triggerReset,
    status,
    name: resetName,
    timer,
    disabled,
    duration,
  } = useReset(options);

  const progress = status === 'counting' ? 1 - (timer - 1) / duration : 0;

  const name = options.customName || resetName;

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
            // 'animate-spin-ccw': status === 'finished',
            'animate-skiing': status === 'finished',
          })}
          style={{
            animationIterationCount: 1,
          }}
        >
          <ResetButtonIcon {...options} />
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

export function Toolbar({ showSettings }: { showSettings: boolean }) {
  const trackers = useAtomValue(connectedTrackersAtom);
  const assignedTrackers = useAtomValue(assignedTrackersAtom);

  const settingsOpenState = useState(false);
  const [, setSettingsOpen] = settingsOpenState;

  const { visibleGroups, groupVisibility } = useMemo(() => {
    const groupVisibility = Object.keys(BODY_PARTS_GROUPS)
      .filter((k) => ['fingers'].includes(k))
      .reduce(
        (curr, key) => {
          const group = key as MountingResetGroup;
          curr[group] = assignedTrackers.some(
            ({ tracker }) =>
              tracker.info?.bodyPart &&
              BODY_PARTS_GROUPS[group].includes(tracker.info?.bodyPart)
          );

          return curr;
        },
        {} as Record<MountingResetGroup, boolean>
      );

    return {
      groupVisibility,
      visibleGroups: Object.values(groupVisibility).filter((v) => v).length,
    };
  }, [assignedTrackers]);

  return (
    <>
      <HomeSettingsModal open={settingsOpenState}></HomeSettingsModal>
      <div className="flex mobile:py-2 flex-col items-center bg-background-70 rounded-t-lg h-[var(--toolbar-h)] mr-2 xs:mt-2 mobile:mr-0">
        <div className="px-1 py-3 w-full divide-x-2 divide-background-50 flex justify-center md:justify-start">
          <div className="flex-col flex gap-1 px-2 md:w-[60%]">
            <Typography variant="section-title">Drift Resets</Typography>
            <div className="gap-2 md:h-[72px] h-[62px] w-full grid-cols-2 grid">
              <BasicResetButton type={ResetType.Full}></BasicResetButton>
              <BasicResetButton type={ResetType.Yaw}></BasicResetButton>
            </div>
          </div>
          <div className="flex-col flex gap-1 px-2 md:flex-grow">
            <Typography
              variant="section-title"
              id="toolbar-mounting_calibration"
            />
            <div
              className="gap-2 md:h-[72px] h-[62px] w-full md:grid flex"
              style={{
                gridTemplateColumns: `repeat(calc(2 + ${visibleGroups}), 1fr)`,
              }}
            >
              <BasicResetButton
                type={ResetType.Mounting}
                group={'default'}
                customName="Body"
              ></BasicResetButton>
              <BasicResetButton
                type={ResetType.Mounting}
                group={'feet'}
                customName="Feet"
              ></BasicResetButton>
              {groupVisibility['fingers'] && (
                <BasicResetButton
                  type={ResetType.Mounting}
                  group={'fingers'}
                  customName="Fingers"
                ></BasicResetButton>
              )}
            </div>
          </div>
        </div>
        <div className="flex w-full gap-2 items-center px-4 h-5">
          <Typography
            color="secondary"
            id="toolbar-connected_trackers"
            vars={{ count: trackers.length }}
          />
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
