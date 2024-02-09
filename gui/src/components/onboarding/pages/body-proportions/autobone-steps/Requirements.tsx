import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { PlayCircleIcon } from '@/components/commons/icon/PlayIcon';
import { useLocalization } from '@fluent/react';
import { useRef, useState } from 'react';

export function RequirementsStep({
  nextStep,
  prevStep,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { l10n } = useLocalization();
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const [paused, setPaused] = useState(true);

  function toggleVideo() {
    if (!videoRef.current) return;
    if (videoRef.current.paused) {
      videoRef.current.play();
    } else {
      videoRef.current.pause();
      videoRef.current.fastSeek(0);
    }
    setPaused(videoRef.current.paused);
  }

  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4">
          <Typography variant="main-title" bold>
            {l10n.getString(
              'onboarding-automatic_proportions-requirements-title'
            )}
          </Typography>
          <div className="flex flex-grow flex-row gap-4">
            <ul className="list-disc mobile:px-4">
              <>
                {l10n
                  .getString(
                    'onboarding-automatic_proportions-requirements-descriptionv2'
                  )
                  .split('\n')
                  .map((line, i) => (
                    <li key={i}>
                      <Typography color="secondary">{line}</Typography>
                    </li>
                  ))}
              </>
            </ul>
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
                preload=""
                ref={videoRef}
                src="/images/autobone.webm"
                className="min-w-[12rem]"
                muted
                loop
                playsInline
                controls={false}
                poster="/images/autobone-poster.webp"
              ></video>
            </button>
          </div>
        </div>

        <div className="flex gap-3 mobile:justify-between">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_proportions-prev_step')}
          </Button>
          <Button variant="primary" onClick={nextStep}>
            {l10n.getString(
              'onboarding-automatic_proportions-requirements-next'
            )}
          </Button>
        </div>
      </div>
    </>
  );
}
