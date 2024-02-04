import { ReactNode, useEffect, useState } from 'react';
import { ProcessStatus, useAutobone } from '@/hooks/autobone';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { P, match } from 'ts-pattern';
import { AutoboneErrorModal } from './AutoboneErrorModal';

export function Recording({
  nextStep,
  resetSteps,
}: {
  nextStep: () => void;
  resetSteps: () => void;
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

  return (
    <div className="flex flex-col items-center w-full justify-between">
      <AutoboneErrorModal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          resetSteps();
        }}
      ></AutoboneErrorModal>
      <div className="flex gap-1 flex-col justify-center items-center">
        <div className="flex text-status-critical justify-center items-center gap-1">
          <div className="w-2 h-2 rounded-lg bg-status-critical"></div>
          <Typography color="text-status-critical">
            {l10n.getString('onboarding-automatic_proportions-recording-title')}
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
      <ol className="list-decimal mobile:px-4">
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
  );
}
