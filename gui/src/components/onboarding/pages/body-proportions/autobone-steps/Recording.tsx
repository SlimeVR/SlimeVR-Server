import { ReactNode, useEffect, useRef, useState } from 'react';
import { ProcessStatus, useAutobone } from '@/hooks/autobone';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { P, match } from 'ts-pattern';
import { AutoboneErrorModal } from './AutoboneErrorModal';
import { PlayCircleIcon } from '@/components/commons/icon/PlayIcon';
import { useDebouncedEffect } from '@/hooks/timeout';
import { AUTOBONE_VIDEO } from '@/utils/tauri';

export function Recording({
  nextStep,
  resetSteps,
  active,
}: {
  nextStep: () => void;
  resetSteps: () => void;
  active: boolean;
}) {
  const { l10n } = useLocalization();
  const { progress, hasCalibration, hasRecording, eta } = useAutobone();
  const [modalOpen, setModalOpen] = useState(false);

  useEffect(() => {
    if (
      hasRecording === ProcessStatus.REJECTED ||
      hasCalibration === ProcessStatus.REJECTED
    ) {
      setModalOpen(true);
    }
    if (progress !== 1) return;

    if (
      hasRecording === ProcessStatus.FULFILLED &&
      hasCalibration === ProcessStatus.FULFILLED
    ) {
      nextStep();
    }
  }, [progress, hasCalibration, hasRecording]);

  const videoRef = useRef<HTMLVideoElement | null>(null);
  const [paused, setPaused] = useState(true);

  function toggleVideo() {
    if (!videoRef.current) return;
    if (videoRef.current.paused) {
      videoRef.current.play();
    } else {
      videoRef.current.pause();
      videoRef.current.currentTime = 0;
    }
    setPaused(videoRef.current.paused);
  }

  useDebouncedEffect(
    () => {
      if (paused) videoRef.current?.pause();
    },
    [paused],
    250
  );

  useEffect(() => {
    if (!active && !paused) {
      toggleVideo();
      return;
    }
    if (active && paused) {
      toggleVideo();
      return;
    }
  }, [active]);

  return (
    <div className="flex flex-row flex-grow">
      <AutoboneErrorModal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          resetSteps();
        }}
      ></AutoboneErrorModal>
      <div className="flex flex-col items-center w-full justify-between">
        <div className="flex gap-1 flex-col justify-center items-center">
          <div className="flex text-status-critical justify-center items-center gap-1">
            <div className="w-2 h-2 rounded-lg bg-status-critical"></div>
            <Typography color="text-status-critical">
              {l10n.getString(
                'onboarding-automatic_proportions-recording-title'
              )}
            </Typography>
          </div>
          <Typography variant="section-title">
            {l10n.getString(
              'onboarding-automatic_proportions-recording-description-p0'
            )}
          </Typography>
          <Typography color="secondary">
            {l10n.getString(
              'onboarding-automatic_proportions-recording-description-p1'
            )}
          </Typography>
        </div>
        <ol className="list-decimal mobile:px-4 nsmol:hidden">
          <>
            {l10n
              .getString('onboarding-automatic_proportions-recording-steps')
              .split('\n')
              .map((line, i) => (
                <li key={i}>
                  <Typography color="secondary">{line}</Typography>
                </li>
              ))}
          </>
        </ol>
        <div className="flex">
          <TipBox>{l10n.getString('tips-do_not_move_heels')}</TipBox>
        </div>
        <div className="flex flex-col gap-2 items-center w-full max-w-[150px]">
          <ProgressBar
            progress={progress}
            height={2}
            colorClass={match([hasCalibration, hasRecording])
              .returnType<string | undefined>()
              .with(
                P.union(
                  [ProcessStatus.REJECTED, P._],
                  [P._, ProcessStatus.REJECTED]
                ),
                () => 'bg-status-critical'
              )
              .with(
                [ProcessStatus.FULFILLED, ProcessStatus.FULFILLED],
                () => 'bg-status-success'
              )
              .otherwise(() => undefined)}
          ></ProgressBar>
          <Typography color="secondary">
            {match([hasCalibration, hasRecording])
              .returnType<ReactNode>()
              .with([ProcessStatus.PENDING, ProcessStatus.FULFILLED], () =>
                l10n.getString(
                  'onboarding-automatic_proportions-recording-processing'
                )
              )
              .with([ProcessStatus.PENDING, ProcessStatus.PENDING], () =>
                l10n.getString(
                  'onboarding-automatic_proportions-recording-timer',
                  { time: Math.round(eta) }
                )
              )
              .otherwise(() => '')}
          </Typography>
        </div>
      </div>
      <button className="relative appearance-none h-fit" onClick={toggleVideo}>
        <div
          className="absolute w-[100px] h-[100px] top-0 bottom-0 left-0 right-0 m-auto fill-background-20"
          hidden={!paused}
        >
          <PlayCircleIcon width={100}></PlayCircleIcon>
        </div>

        <video
          preload="auto"
          ref={videoRef}
          src={AUTOBONE_VIDEO}
          className="min-w-[12rem] w-[12rem]"
          muted
          loop
          playsInline
          controls={false}
          poster="/images/autobone-poster.webp"
        ></video>
      </button>
    </div>
  );
}
