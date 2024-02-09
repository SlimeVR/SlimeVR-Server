import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';

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
            <div className="relative h-fit">
              <video
                src="/images/autobone.webm"
                className="min-w-[12rem]"
                muted
                loop
                onClick={(ev) => {
                  if (!(ev.target instanceof HTMLVideoElement)) return;
                  if (ev.target.paused) {
                    ev.target.play();
                  } else {
                    ev.target.pause();
                    ev.target.fastSeek(0);
                  }
                }}
              ></video>
              <div className="absolute w-5 top-0 bottom-0 left-0 right-0 m-auto h-5 z-10"></div>
            </div>
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
