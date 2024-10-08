import { useAutobone } from '@/hooks/autobone';
import { Button } from '@/components/commons/Button';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { useEffect, useRef, useState } from 'react';
import { PlayCircleIcon } from '@/components/commons/icon/PlayIcon';
import { useDebouncedEffect } from '@/hooks/timeout';
import { AUTOBONE_VIDEO } from '@/utils/tauri';

export function StartRecording({
  nextStep,
  prevStep,
  variant,
  active,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
  active: boolean;
}) {
  const { l10n } = useLocalization();
  const { startRecording } = useAutobone();
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
    if (!active && !paused) toggleVideo();
  }, [active]);

  const start = () => {
    nextStep();
    startRecording();
  };

  return (
    <>
      <div className="flex flex-col flex-grow gap-3">
        <div className="flex flex-row flex-grow">
          <div className="flex flex-grow flex-col gap-4">
            <Typography variant="main-title" bold>
              {l10n.getString(
                'onboarding-automatic_proportions-start_recording-title'
              )}
            </Typography>
            <div>
              <Typography color="secondary">
                {l10n.getString(
                  'onboarding-automatic_proportions-start_recording-description'
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
            <div className="flex nsmol:hidden">
              <TipBox>{l10n.getString('tips-do_not_move_heels')}</TipBox>
            </div>
          </div>
          <button
            className="relative appearance-none h-fit"
            onClick={toggleVideo}
          >
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

        <div className="flex smol:hidden">
          <TipBox>{l10n.getString('tips-do_not_move_heels')}</TipBox>
        </div>
        <div className="flex gap-3 mobile:justify-between">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_proportions-prev_step')}
          </Button>
          <Button variant="primary" onClick={start}>
            {l10n.getString(
              'onboarding-automatic_proportions-start_recording-next'
            )}
          </Button>
        </div>
      </div>
    </>
  );
}
