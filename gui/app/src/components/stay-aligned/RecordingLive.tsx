import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import {
  StayAlignedPart,
  StayAlignedPoseRecordingDetail,
  StayAlignedPoseRecordingFailure,
  StayAlignedPoseRecordingLifecycle,
  StayAlignedPoseRecordingStatusResponseT,
  StayAlignedPoseWaitingT,
  StayAlignedRecordingTrackerState,
  StayAlignedRelaxedPose,
} from 'solarxr-protocol';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { Typography } from '@/components/commons/Typography';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import { PART_LABEL, PART_ORDER, POSITION_LABEL } from '@/hooks/stay-aligned';

const STATE_DOT: Record<StayAlignedRecordingTrackerState, string> = {
  [StayAlignedRecordingTrackerState.STILL]: 'bg-status-success',
  [StayAlignedRecordingTrackerState.MOVING]: 'bg-status-warning',
};

const STATE_LABEL: Record<StayAlignedRecordingTrackerState, string> = {
  [StayAlignedRecordingTrackerState.STILL]:
    'settings-stay_aligned-recorder-state-still',
  [StayAlignedRecordingTrackerState.MOVING]:
    'settings-stay_aligned-recorder-state-moving',
};

const BADGE_CLASSES =
  'flex h-8 w-8 shrink-0 items-center justify-center rounded-full fill-background-10 text-standard-bold tabular-nums';

function RecordingBadge({
  failed,
  wrongPose,
  secondsLeft,
}: {
  failed: boolean;
  wrongPose: boolean;
  secondsLeft: number;
}) {
  if (failed) {
    return (
      <div className={classNames(BADGE_CLASSES, 'bg-status-critical')}>
        <CrossIcon size={12} />
      </div>
    );
  }
  if (wrongPose) {
    return (
      <div
        className={classNames(
          BADGE_CLASSES,
          'bg-status-warning text-background-60'
        )}
      >
        <WarningIcon width={16} />
      </div>
    );
  }
  return (
    <div className={classNames(BADGE_CLASSES, 'bg-background-50')}>
      {secondsLeft}
    </div>
  );
}

function RecordingStatus({
  recording,
  failed,
  wrongPose,
  detectedPose,
}: {
  recording: StayAlignedPoseRecordingStatusResponseT;
  failed: boolean;
  wrongPose: boolean;
  detectedPose: StayAlignedRelaxedPose;
}) {
  const { l10n } = useLocalization();

  if (wrongPose) {
    return (
      <span>
        <Typography
          tag="span"
          bold
          color={failed ? 'text-status-critical' : 'text-status-warning'}
          id="settings-stay_aligned-recorder-live-wrong_pose"
        />{' '}
        <Typography
          tag="span"
          id="settings-stay_aligned-recorder-live-detected"
          vars={{ position: l10n.getString(POSITION_LABEL[detectedPose]) }}
        />
      </span>
    );
  }
  if (!failed) {
    return <Typography id="settings-stay_aligned-recorder-live-hold" />;
  }
  if (recording.failure === StayAlignedPoseRecordingFailure.MISSING_TRACKERS) {
    return <Typography id="settings-stay_aligned-recorder-live-missing" />;
  }
  if (recording.failure === StayAlignedPoseRecordingFailure.NOT_CALIBRATED) {
    return (
      <Typography id="settings-stay_aligned-recorder-live-not_calibrated" />
    );
  }
  return <Typography id="settings-stay_aligned-recorder-live-not_still" />;
}

export function RecordingLive({
  recording,
}: {
  recording: StayAlignedPoseRecordingStatusResponseT;
}) {
  const failed =
    recording.lifecycle === StayAlignedPoseRecordingLifecycle.FAILED;

  const waiting =
    recording.detailType ===
    StayAlignedPoseRecordingDetail.StayAlignedPoseWaiting
      ? (recording.detail as StayAlignedPoseWaitingT)
      : null;

  const trackers = waiting?.trackers ?? [];

  const wrongPose = failed
    ? recording.failure === StayAlignedPoseRecordingFailure.WRONG_POSE ||
      recording.failure === StayAlignedPoseRecordingFailure.POSITION_UNKNOWN
    : waiting?.detectedPose !== recording.pose;

  const secondsLeft = waiting ? waiting.duration - waiting.progress : 0;
  const timeLeft = waiting ? secondsLeft / waiting.duration : 0;

  return (
    <div className="flex flex-col gap-3">
      <div className="flex h-9 items-center gap-2">
        <RecordingBadge
          failed={failed}
          wrongPose={wrongPose}
          secondsLeft={secondsLeft}
        />
        <div className="flex h-9 min-w-0 flex-1 items-center overflow-hidden">
          <RecordingStatus
            recording={recording}
            failed={failed}
            wrongPose={wrongPose}
            detectedPose={
              waiting?.detectedPose ?? StayAlignedRelaxedPose.UNKNOWN
            }
          />
        </div>
      </div>
      <ProgressBar
        progress={failed ? 0 : timeLeft}
        animated
        colorClass={failed ? 'bg-status-critical' : undefined}
      />
      <div className="grid grid-cols-2 grid-rows-[repeat(4,36px)] gap-2 mobile:grid-rows-[repeat(4,44px)]">
        {PART_ORDER.map((part) => {
          const state = trackers.find(
            (tracker) => tracker.part === part
          )?.state;
          if (state === undefined) return null;
          const moving = state === StayAlignedRecordingTrackerState.MOVING;
          return (
            <div
              key={part}
              className={classNames(
                'flex min-w-0 items-center justify-between gap-1 rounded-md bg-background-80 pl-2.5 pr-2 mobile:flex-col mobile:items-start mobile:justify-center mobile:gap-0.5',
                part === StayAlignedPart.UPPER_BODY && 'col-span-2'
              )}
            >
              <span className="min-w-0 truncate">
                <Typography tag="span" id={PART_LABEL[part]} />
              </span>
              <span className="inline-flex flex-none items-center gap-1.5">
                <span
                  className={classNames(
                    'h-2 w-2 flex-none rounded-full',
                    failed && moving ? 'bg-status-critical' : STATE_DOT[state]
                  )}
                />
                <Typography
                  bold
                  whitespace="whitespace-nowrap"
                  id={STATE_LABEL[state]}
                />
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}
