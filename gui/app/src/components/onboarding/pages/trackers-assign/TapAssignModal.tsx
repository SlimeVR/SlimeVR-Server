import { BodyPart } from 'solarxr-protocol';
import { BaseModal } from '@/components/commons/BaseModal';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';

export function TapAssignModal({
  role,
  onClose,
}: {
  role: BodyPart;
  onClose: () => void;
}) {
  const isOpen = role !== BodyPart.NONE;

  return (
    <BaseModal
      isOpen={isOpen}
      appendClasses="max-w-md w-full"
      closeable
      onRequestClose={onClose}
    >
      <div className="flex flex-col gap-3 items-center text-center">
        <Typography
          variant="main-title"
          id="onboarding-assign_trackers-tap_modal-title"
        />
        {isOpen && (
          <Typography
            bold
            variant="section-title"
            color="text-accent-background-10"
            id={'body_part-' + BodyPart[role]}
          />
        )}
        <Typography id="onboarding-assign_trackers-tap_modal-description" />
        <Button
          variant="secondary"
          onClick={onClose}
          id="onboarding-assign_trackers-tap_modal-cancel"
        />
      </div>
    </BaseModal>
  );
}
