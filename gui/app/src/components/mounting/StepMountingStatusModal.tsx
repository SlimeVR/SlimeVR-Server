import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { useAtomValue } from 'jotai';
import { useLocalization } from '@fluent/react';
import { ResetLifecycle } from 'solarxr-protocol';
import { BaseModal } from '@/components/commons/BaseModal';
import { Typography } from '@/components/commons/Typography';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';
import { useCancelReset } from '@/hooks/reset';
import { useStepMountingProgress } from '@/hooks/step-mounting';
import { STEP_MOUNTING_PATH } from '@/hooks/mounting-method';
import { resetStatusAtom } from '@/store/app-store';
import { StepMountingRetryButton } from './StepMountingRetryButton';
import {
  StepMedia,
  StepMountingMessage,
  StepMountingSegments,
} from './StepMountingStatus';

/** Follows the step mounting reset wherever it was started from */
export function StepMountingStatusModal() {
  const { l10n } = useLocalization();
  const progress = useStepMountingProgress();
  const resetStatus = useAtomValue(resetStatusAtom);
  const cancel = useCancelReset();
  const [dismissed, setDismissed] = useState(resetStatus);

  // make sure the modal close if we leave the page
  const onStepPage = useLocation().pathname === STEP_MOUNTING_PATH;
  useEffect(() => {
    if (onStepPage) setDismissed(resetStatus);
  }, [onStepPage, resetStatus]);

  const isDone = progress?.lifecycle === ResetLifecycle.DONE;
  useEffect(() => {
    if (isDone) setDismissed(resetStatus);
  }, [isDone, resetStatus]);

  const isRunning = progress?.lifecycle === ResetLifecycle.RUNNING;
  const isFailed = progress?.lifecycle === ResetLifecycle.FAILED;

  // Closing a running calibration cancels it
  const close = () => {
    if (isRunning) {
      cancel();
      return;
    }
    setDismissed(resetStatus);
  };

  const isOpen =
    progress !== null &&
    !onStepPage &&
    resetStatus !== dismissed &&
    progress.lifecycle !== ResetLifecycle.CANCELED;
  if (!progress) return null;

  return (
    <BaseModal
      isOpen={isOpen}
      onRequestClose={() => setDismissed(resetStatus)}
      closeable={false}
    >
      <div className="flex flex-col gap-5 p-2 w-[min(90vw,560px)]">
        <div className="flex items-center justify-between gap-3">
          <Typography variant="main-title" bold id="step_mounting-title" />
          <button
            type="button"
            aria-label={l10n.getString('step_mounting-close')}
            onClick={close}
            className="flex items-center justify-center fill-background-10 bg-background-50 hover:bg-background-40 rounded-full w-9 h-9 shrink-0 cursor-pointer transition-colors"
          >
            <CrossIcon size={16} />
          </button>
        </div>
        <div className="flex h-[min(55vh,480px)] items-center justify-center rounded-lg bg-background-60 p-4 fill-background-50">
          <StepMedia
            status={progress.status}
            done={isDone}
            className="h-full object-contain"
          />
        </div>
        {!isFailed && (
          <div className="text-center">
            <StepMountingMessage {...progress} />
          </div>
        )}
        {isFailed && <StepMountingRetryButton large />}
        <StepMountingSegments progress={progress} />
      </div>
    </BaseModal>
  );
}
