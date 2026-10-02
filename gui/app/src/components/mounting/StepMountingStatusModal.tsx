import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { useAtomValue } from 'jotai';
import { ResetLifecycle } from 'solarxr-protocol';
import { BaseModal } from '@/components/commons/BaseModal';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useCancelReset } from '@/hooks/reset';
import { useStepMountingProgress } from '@/hooks/step-mounting';
import { STEP_MOUNTING_PATH } from '@/hooks/mounting-method';
import { resetStatusAtom } from '@/store/app-store';
import { StepMountingStatusContent } from './StepMountingStatus';

/** Follows the step mounting reset wherever it was started from */
export function StepMountingStatusModal() {
  const progress = useStepMountingProgress();
  const resetStatus = useAtomValue(resetStatusAtom);
  const cancel = useCancelReset();
  const [dismissed, setDismissed] = useState(resetStatus);

  // make sure the modal close if we leave the page
  const onStepPage = useLocation().pathname === STEP_MOUNTING_PATH;
  useEffect(() => {
    if (onStepPage) setDismissed(resetStatus);
  }, [onStepPage, resetStatus]);

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
      <div className="flex flex-col gap-4 p-2 w-[min(90vw,420px)]">
        <Typography variant="main-title" bold id="step_mounting-title" />
        <StepMountingStatusContent {...progress} />
        <div className="flex gap-3 justify-end pt-2">
          {progress.lifecycle === ResetLifecycle.RUNNING ? (
            <Button
              variant="secondary"
              onClick={cancel}
              id="step_mounting-cancel"
            />
          ) : (
            <Button
              variant="primary"
              onClick={() => setDismissed(resetStatus)}
              id="step_mounting-close"
            />
          )}
        </div>
      </div>
    </BaseModal>
  );
}
