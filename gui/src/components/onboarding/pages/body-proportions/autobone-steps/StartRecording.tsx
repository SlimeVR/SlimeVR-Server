import { useAutobone } from '../../../../../hooks/autobone';
import { Button } from '../../../../commons/Button';
import { TipBox } from '../../../../commons/TipBox';
import { Typography } from '../../../../commons/Typography';
import { useLocalization } from '@fluent/react';

export function StartRecording({
  nextStep,
  prevStep,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { l10n } = useLocalization();
  const { startRecording } = useAutobone();

  const start = () => {
    nextStep();
    startRecording();
  };

  return (
    <>
      <div className="flex flex-col flex-grow">
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
          <ol className="list-decimal">
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
        </div>

        <div className="flex gap-3">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tiertiary'}
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
