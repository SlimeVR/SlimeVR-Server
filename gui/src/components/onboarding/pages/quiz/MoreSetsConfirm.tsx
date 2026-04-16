import { ButtonConfirmModal } from '@/components/commons/ButtonConfirmModal';
import { Typography } from '@/components/commons/Typography';
import { useNavigate } from 'react-router-dom';

export function MoreSetsConfirm() {
  const navigate = useNavigate();

  return (
    <ButtonConfirmModal
      variant="primary"
      className="ml-auto"
      id="onboarding-connect_tracker-next"
      cancelLabel="onboarding-quiz-more_sets_modal-cancel"
      confirmLabel="onboarding-quiz-more_sets_modal-confirm"
      onConfirm={() => navigate('/onboarding/trackers-assign')}
      onCancel={(reason) => {
        if (reason === 'cancel') navigate('/onboarding/quiz/slime-set');
      }}
    >
      <Typography
        variant="main-title"
        id="onboarding-quiz-more_sets_modal-title"
      />
      <Typography
        variant="vr-accessible"
        id="onboarding-quiz-more_sets_modal-desc"
      />
    </ButtonConfirmModal>
  );
}
