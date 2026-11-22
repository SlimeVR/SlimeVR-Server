import classNames from 'classnames';
import { Localized, useLocalization } from '@fluent/react';
import { ReactNode, useEffect, useMemo } from 'react';
import { useAtomValue } from 'jotai';
import {
  MountingMethod,
  ResetLifecycle,
  ResetType,
  StayAlignedPart,
  StayAlignedPoseCountdownT,
  StayAlignedPoseRecordingDetail,
  StayAlignedPoseRecordingLifecycle,
  StayAlignedPoseRecordingStatusResponseT,
  StayAlignedStatusResponseT,
} from 'solarxr-protocol';
import { BaseModal } from '@/components/commons/BaseModal';
import { Button } from '@/components/commons/Button';
import { FullResetExamples } from '@/components/commons/FullResetExamples';
import { IconButton } from '@/components/commons/IconButton';
import { Timeline, TimelineItem } from '@/components/commons/Timeline';
import { Typography } from '@/components/commons/Typography';
import { CloseIcon } from '@/components/commons/icon/CloseIcon';
import { ResetButton } from '@/components/home/ResetButton';
import {
  PoseMountingInstructions,
  PoseMountingVideo,
} from '@/components/mounting/PoseMountingVideo';
import { StepMedia } from '@/components/mounting/StepMountingStatus';
import { useCancelReset } from '@/hooks/reset';
import { useLocaleConfig } from '@/i18n/config';
import {
  PART_LABEL,
  POSE_DESCRIPTION,
  POSE_IMAGE,
  POSE_TITLE,
  RecordablePose,
  RecorderStep,
  useRecorderSteps,
  useStayAlignedRecording,
} from '@/hooks/stay-aligned';
import { resetStatusAtom } from '@/store/app-store';
import { RecordingLive } from './RecordingLive';

const MODAL_CLASSES = classNames(
  'focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent outline-none',
  'relative flex flex-col gap-4 bg-background-70 border border-background-60 p-6 rounded-lg m-2',
  'text-background-10 text-standard w-[min(920px,100%)]',
  'mobile:w-full mobile:p-4 mobile:h-[min(40rem,calc(100dvh-1rem))]'
);

const STEP_TITLE: Record<RecorderStep, string> = {
  trackers: 'settings-stay_aligned-recorder-step-trackers',
  'full-reset': 'settings-stay_aligned-recorder-step-full_reset',
  mounting: 'settings-stay_aligned-recorder-step-mounting',
  'feet-mounting': 'settings-stay_aligned-recorder-step-feet_mounting',
  record: 'settings-stay_aligned-recorder-step-record',
};

interface RecorderFrame {
  pose: RecordablePose;
  timeline: RecorderStep[];
  current: RecorderStep;
  onClose: () => void;
}

function RecorderTimeline({
  timeline,
  current,
  body,
}: {
  timeline: RecorderStep[];
  current: RecorderStep;
  body: ReactNode;
}) {
  if (timeline.length === 1) return <>{body}</>;

  const currentIndex = timeline.indexOf(current);
  const stateOf = (index: number) => {
    if (index < currentIndex) return 'done';
    if (index === currentIndex) return 'current';
    return 'todo';
  };

  return (
    <Timeline>
      {timeline.map((step, index) => (
        <TimelineItem
          key={step}
          state={stateOf(index)}
          number={index + 1}
          last={index === timeline.length - 1}
          title={<Localized id={STEP_TITLE[step]} />}
        >
          {step === current && (
            <div className="flex flex-col gap-2 pt-3">{body}</div>
          )}
        </TimelineItem>
      ))}
    </Timeline>
  );
}

function RecorderLayout({
  frame,
  body,
  media,
  actions,
}: {
  frame: RecorderFrame;
  body: ReactNode;
  media: ReactNode;
  actions: ReactNode;
}) {
  return (
    <>
      <Typography
        variant="main-title"
        id={POSE_TITLE[frame.pose]}
        textAlign="text-left"
      />
      <IconButton
        labelId="settings-stay_aligned-recorder-close"
        tooltip={false}
        onClick={frame.onClose}
        className="absolute right-3 top-3 flex h-9 w-9 items-center justify-center rounded-full bg-background-50 xs:hidden"
      >
        <CloseIcon size={28} className="stroke-background-10" />
      </IconButton>
      <div className="grid h-[500px] grid-cols-[396px_minmax(0,1fr)] gap-6 mobile:flex mobile:h-auto mobile:min-h-0 mobile:flex-1 mobile:flex-col mobile:gap-4">
        <div className="flex min-w-0 flex-col gap-2 mobile:min-h-0 mobile:flex-initial mobile:overflow-y-auto">
          <RecorderTimeline
            timeline={frame.timeline}
            current={frame.current}
            body={body}
          />
        </div>
        <div
          className={classNames(
            'relative flex min-h-0 items-center justify-center overflow-hidden rounded-xl bg-background-80 fill-background-50 p-4',
            'mobile:order-1 mobile:min-h-24 mobile:flex-1 mobile:p-2'
          )}
        >
          {media}
        </div>
      </div>
      <div className="flex items-center justify-between gap-3">
        <Button
          variant="tertiary"
          className="mobile:hidden"
          onClick={frame.onClose}
          id="settings-stay_aligned-recorder-close"
        />
        <div className="ml-auto flex gap-3">{actions}</div>
      </div>
    </>
  );
}

function TrackersStep({
  frame,
  missingParts,
}: {
  frame: RecorderFrame;
  missingParts: StayAlignedPart[];
}) {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const partList = useMemo(
    () => new Intl.ListFormat(currentLocales, { type: 'conjunction' }),
    [currentLocales]
  );

  return (
    <RecorderLayout
      frame={frame}
      body={
        <>
          <Typography id="settings-stay_aligned-recorder-trackers-intro" />
          <Typography
            id="settings-stay_aligned-trackers-missing"
            vars={{
              parts: partList.format(
                missingParts.map((part) => l10n.getString(PART_LABEL[part]))
              ),
            }}
          />
        </>
      }
      media={
        <img
          src={POSE_IMAGE[frame.pose]}
          className="max-h-full max-w-full object-contain"
        />
      }
      actions={
        <Button
          variant="tertiary"
          to="/onboarding/trackers-assign"
          state={{ alonePage: true }}
          id="settings-stay_aligned-recorder-trackers-assign"
        />
      }
    />
  );
}

function FullResetStep({
  frame,
  onDone,
}: {
  frame: RecorderFrame;
  onDone: () => void;
}) {
  return (
    <RecorderLayout
      frame={frame}
      body={
        <>
          <Typography id="settings-stay_aligned-recorder-full_reset-intro" />
          <div className="flex flex-col gap-1">
            <Typography id="onboarding-automatic_mounting-preparation-v2-step-0" />
            <Typography id="onboarding-automatic_mounting-preparation-v2-step-1" />
            <Typography id="onboarding-automatic_mounting-preparation-v2-step-2" />
          </div>
        </>
      }
      media={
        <FullResetExamples
          className="h-full w-full"
          tileClassName="bg-background-70"
        />
      }
      actions={<ResetButton type={ResetType.FULL} onReseted={onDone} />}
    />
  );
}

function MountingStep({
  frame,
  mountingMethod,
  onDone,
}: {
  frame: RecorderFrame;
  mountingMethod: MountingMethod | undefined;
  onDone: () => void;
}) {
  const stepMounting = mountingMethod === MountingMethod.STEP;

  return (
    <RecorderLayout
      frame={frame}
      body={
        <>
          <Typography id="settings-stay_aligned-recorder-mounting-intro" />
          {stepMounting ? (
            <div className="flex flex-col gap-1">
              <Typography id="onboarding-step_mounting-step-0" />
              <Typography id="onboarding-step_mounting-step-1" />
            </div>
          ) : (
            <PoseMountingInstructions />
          )}
        </>
      }
      media={
        stepMounting ? (
          <StepMedia status={null} className="h-full w-full object-contain" />
        ) : (
          <PoseMountingVideo className="h-full" />
        )
      }
      actions={
        <ResetButton
          type={ResetType.MOUNTING}
          group="default"
          onReseted={onDone}
        />
      }
    />
  );
}

function FeetMountingStep({
  frame,
  onDone,
}: {
  frame: RecorderFrame;
  onDone: () => void;
}) {
  return (
    <RecorderLayout
      frame={frame}
      body={
        <>
          <Typography id="settings-stay_aligned-recorder-feet_mounting-intro" />
          <div className="flex flex-col gap-1">
            <Typography id="onboarding-automatic_mounting-mounting_reset-feet-step-0" />
            <Typography id="onboarding-automatic_mounting-mounting_reset-feet-step-1" />
          </div>
        </>
      }
      media={
        <div className="flex h-full w-full gap-2">
          <img
            src="/images/mounting/MountingFeets.webp"
            alt=""
            className="min-w-0 flex-1 object-contain"
          />
          <img
            src="/images/mounting/MountingFeetsSide.webp"
            alt=""
            className="min-w-0 flex-1 object-contain"
          />
        </div>
      }
      actions={
        <ResetButton
          type={ResetType.MOUNTING}
          group="feet"
          onReseted={onDone}
        />
      }
    />
  );
}

type RecordPhase = 'idle' | 'countdown' | 'waiting' | 'failed';

function recordPhase(
  recording: StayAlignedPoseRecordingStatusResponseT | null
): RecordPhase {
  switch (recording?.lifecycle) {
    case StayAlignedPoseRecordingLifecycle.RUNNING:
      return recording.detailType ===
        StayAlignedPoseRecordingDetail.StayAlignedPoseCountdown
        ? 'countdown'
        : 'waiting';
    case StayAlignedPoseRecordingLifecycle.FAILED:
      return 'failed';
    default:
      return 'idle';
  }
}

function RecordActions({
  phase,
  recording,
  onStart,
  onRedoPreparation,
}: {
  phase: RecordPhase;
  recording: StayAlignedPoseRecordingStatusResponseT | null;
  onStart: () => void;
  onRedoPreparation: () => void;
}) {
  if (phase === 'countdown' && recording) {
    // The countdown replaces the label, like the reset buttons
    const { progress, duration } =
      recording.detail as StayAlignedPoseCountdownT;
    return (
      <Button variant="primary" disabled>
        <div className="flex flex-col">
          <div className="h-0 opacity-0">
            <Localized id="settings-stay_aligned-recorder-start" />
          </div>
          {duration - progress}
        </div>
      </Button>
    );
  }
  if (phase === 'waiting') {
    return (
      <Button
        variant="tertiary"
        onClick={onStart}
        id="settings-stay_aligned-recorder-start_over"
      />
    );
  }
  if (phase === 'failed') {
    return (
      <>
        <Button
          variant="tertiary"
          onClick={onRedoPreparation}
          id="settings-stay_aligned-recorder-redo_preparation"
        />
        <Button
          variant="primary"
          onClick={onStart}
          id="settings-stay_aligned-recorder-try_again"
        />
      </>
    );
  }
  return (
    <Button
      variant="primary"
      onClick={onStart}
      id="settings-stay_aligned-recorder-start"
    />
  );
}

function RecordStep({
  frame,
  phase,
  recording,
  onStart,
  onRedoPreparation,
}: {
  frame: RecorderFrame;
  phase: RecordPhase;
  recording: StayAlignedPoseRecordingStatusResponseT | null;
  onStart: () => void;
  onRedoPreparation: () => void;
}) {
  const live = (phase === 'waiting' || phase === 'failed') && recording;

  return (
    <RecorderLayout
      frame={frame}
      body={
        <>
          <p>
            <Typography tag="span" id={POSE_DESCRIPTION[frame.pose]} />{' '}
            <Typography tag="span" id="settings-stay_aligned-recorder-arms" />
          </p>
          <div className="box-content h-[238px] pt-2 mobile:h-auto">
            {live ? (
              <RecordingLive recording={live} />
            ) : (
              <div className="flex flex-col">
                <Typography id="settings-stay_aligned-recorder-record-step-0" />
                <Typography id="settings-stay_aligned-recorder-record-step-1" />
              </div>
            )}
          </div>
        </>
      }
      media={
        <img
          src={POSE_IMAGE[frame.pose]}
          className="max-h-full max-w-full object-contain"
        />
      }
      actions={
        <RecordActions
          phase={phase}
          recording={recording}
          onStart={onStart}
          onRedoPreparation={onRedoPreparation}
        />
      }
    />
  );
}

function PoseRecorder({
  pose,
  status,
  onClose,
}: {
  pose: RecordablePose;
  status: StayAlignedStatusResponseT | null;
  onClose: () => void;
}) {
  const { recording, start, cancel, clear } = useStayAlignedRecording();
  const resetStatus = useAtomValue(resetStatusAtom);
  const cancelReset = useCancelReset();

  const phase = recordPhase(recording);
  const recordingRuns = phase === 'countdown' || phase === 'waiting';
  const steps = useRecorderSteps(recordingRuns, status);

  // A saved pose has nothing left to show
  useEffect(() => {
    if (recording?.lifecycle === StayAlignedPoseRecordingLifecycle.DONE)
      onClose();
  }, [recording]);

  const frame: RecorderFrame = {
    pose,
    timeline: steps.timeline,
    current: steps.current,
    onClose: () => {
      if (recordingRuns) cancel();
      if (resetStatus?.lifecycle === ResetLifecycle.RUNNING) cancelReset();
      onClose();
    },
  };

  switch (steps.current) {
    case 'trackers':
      return <TrackersStep frame={frame} missingParts={steps.missingParts} />;
    case 'full-reset':
      return (
        <FullResetStep frame={frame} onDone={() => steps.done('full-reset')} />
      );
    case 'mounting':
      return (
        <MountingStep
          frame={frame}
          mountingMethod={steps.mountingMethod}
          onDone={() => steps.done('mounting')}
        />
      );
    case 'feet-mounting':
      return (
        <FeetMountingStep
          frame={frame}
          onDone={() => steps.done('feet-mounting')}
        />
      );
    case 'record':
      return (
        <RecordStep
          frame={frame}
          phase={phase}
          recording={recording}
          onStart={() => start(pose)}
          onRedoPreparation={() => {
            clear();
            steps.redo();
          }}
        />
      );
  }
}

export function PoseRecorderModal({
  pose,
  status,
  onClose,
}: {
  pose: RecordablePose | null;
  status: StayAlignedStatusResponseT | null;
  onClose: () => void;
}) {
  return (
    <BaseModal
      isOpen={pose !== null}
      className={MODAL_CLASSES}
      onRequestClose={onClose}
    >
      {pose !== null && (
        <PoseRecorder pose={pose} status={status} onClose={onClose} />
      )}
    </BaseModal>
  );
}
