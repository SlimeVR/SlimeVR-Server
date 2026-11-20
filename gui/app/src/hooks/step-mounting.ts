import { useAtomValue } from 'jotai';
import {
  ResetDetail,
  ResetLifecycle,
  ResetType,
  StepMountingDetailT,
  StepMountingStatus,
} from 'solarxr-protocol';
import { resetStatusAtom } from '@/store/app-store';

export interface StepMountingProgress {
  status: StepMountingStatus;
  lifecycle: ResetLifecycle;
}

export function useStepMountingProgress(): StepMountingProgress | null {
  const resetStatus = useAtomValue(resetStatusAtom);
  if (
    resetStatus?.resetType !== ResetType.MOUNTING ||
    resetStatus.detailType !== ResetDetail.StepMountingDetail
  ) {
    return null;
  }
  return {
    status: (resetStatus.detail as StepMountingDetailT).status,
    lifecycle: resetStatus.lifecycle,
  };
}
