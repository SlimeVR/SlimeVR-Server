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
import { assignedTrackersAtom } from '@/store/app-store';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useMemo } from 'react';
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
        'absolute top-0 left-0 w-0 h-full bg-accent-background-20 opacity-50'
      )}
      style={{
        width: `${progress * 100}%`,
        transition:
          status === 'counting'
            ? 'width 0.3s cubic-bezier(0.68, -0.8, 0.32, 1.8)'
            : 'width 1s linear',
      }}
    />
  );
}

function BasicResetButton(options: UseResetOptions & { customName?: string }) {
  const { isMd } = useBreakpoint('md');
  const {
    triggerReset,
    status,
    name: resetName,
    timer,
    progress: resetProress,
    disabled,
    duration,
    error,
  } = useReset(options);

  const progress = status === 'counting' ? resetProress / duration : 0;

  const name = options.customName || resetName;

  const skiReset =
    options.type === ResetType.Mounting && options.group === 'default';

  return (
    <Tooltip
      disabled={!error && isMd}
      content={
        error ? (
          <Typography
            id={error}
            textAlign="text-center"
            color="text-status-critical"
          />
        ) : (
          <Typography textAlign="text-center" id={name} />
        )
      }
      spacing={5}
      preferedDirection={error ? 'bottom' : 'top'}
    >
      <button
        type="button"
        disabled={disabled}
        className={classNames(
          MAINBUTTON_CLASSES({ disabled }),
          'rounded-lg',
          'absolute'
        )}
        style={{
          animationIterationCount: 1,
        }}
        onClick={() => !disabled && triggerReset()}
      >
        <div
          className={classNames({
            'animate-spin-ccw': !skiReset && status === 'finished',
            'animate-skiing': skiReset && status === 'finished',
            'opacity-0': status === 'counting',
          })}
          style={{
            animationIterationCount: 1,
          }}
        >
          <ResetButtonIcon {...options} />
        </div>

        <div
          className={classNames('hidden md:block relative', {
            'opacity-0': status === 'counting',
          })}
        >
          <Typography
            variant="section-title"
            textAlign="text-center"
            id={name}
          />
        </div>

        <ButtonProgress progress={progress} status={status} />
        <div
          className={classNames(
            {
              'opacity-0': status !== 'counting',
              'animate-timer-tick': status === 'counting',
            },
            'absolute top-0 h-full flex items-center justify-center'
          )}
        >
          <Typography variant="main-title" textAlign="text-center">
            {timer}
          </Typography>
        </div>
      </button>
    </Tooltip>
  );
}

export function Toolbar() {
  const assignedTrackers = useAtomValue(assignedTrackersAtom);

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
      <div className="flex mobile:py-2 flex-col items-center bg-background-70 rounded-t-lg h-[var(--toolbar-h)] mr-2 xs:mt-2 mobile:mr-0">
        <div className="px-3 py-3 w-full flex gap-4 justify-center md:justify-start">
          <div className="flex-col flex gap-1 md:w-[60%]">
            <Typography variant="section-title" id="toolbar-drift_reset" />
            <div className="gap-2 md:h-[72px] h-[62px] w-full grid-cols-2 grid">
              <BasicResetButton type={ResetType.Full} />
              <BasicResetButton type={ResetType.Yaw} />
            </div>
          </div>
          <div className="flex-col flex gap-1 md:flex-grow">
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
                customName="toolbar-mounting_calibration-default"
              />
              <BasicResetButton
                type={ResetType.Mounting}
                group={'feet'}
                customName="toolbar-mounting_calibration-feet"
              />
              {groupVisibility['fingers'] && (
                <BasicResetButton
                  type={ResetType.Mounting}
                  group={'fingers'}
                  customName="toolbar-mounting_calibration-fingers"
                />
              )}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
