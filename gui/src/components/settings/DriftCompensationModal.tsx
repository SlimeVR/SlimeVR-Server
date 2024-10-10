import { Button } from '@/components/commons/Button';
import { WarningBox } from '@/components/commons/TipBox';
import { Localized, useLocalization } from '@fluent/react';
import { BaseModal } from '@/components/commons/BaseModal';
import ReactModal from 'react-modal';

export function DriftCompensationModal({
  isOpen = true,
  onClose,
  accept,
  ...props
}: {
  /**
   * Is the parent/sibling component opened?
   */
  isOpen: boolean;
  /**
   * Function to trigger when the warning hasn't been accepted
   */
  onClose: () => void;
  /**
   * Function when you press `I understand`
   */
  accept: () => void;
} & ReactModal.Props) {
  const { l10n } = useLocalization();

  return (
    <BaseModal
      isOpen={isOpen}
      shouldCloseOnOverlayClick
      onRequestClose={onClose}
      className={props.className}
      overlayClassName={props.overlayClassName}
    >
      <div className="flex w-full h-full flex-col ">
        <div className="flex flex-col flex-grow items-center gap-3">
          <Localized
            id="settings-general-tracker_mechanics-drift_compensation_warning"
            elems={{ b: <b></b> }}
          >
            <WarningBox>
              <b>Warning:</b> Drift compensation should only be used if you find
              you need to reset very often (~5-10 minutes).
              <br />
              Some IMUs prone to frequent resets include: Joy-Cons, owoTrack,
              and MPUs (without recent firmware).
            </WarningBox>
          </Localized>

          <div className="flex flex-row gap-3 pt-5 place-content-center">
            <Button variant="primary" onClick={onClose}>
              {l10n.getString(
                'settings-general-tracker_mechanics-drift_compensation_warning-cancel'
              )}
            </Button>
            <Button
              variant="tertiary"
              onClick={() => {
                accept();
              }}
            >
              {l10n.getString(
                'settings-general-tracker_mechanics-drift_compensation_warning-done'
              )}
            </Button>
          </div>
        </div>
      </div>
    </BaseModal>
  );
}
