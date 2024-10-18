import { Button } from '@/components/commons/Button';
import { WarningBox } from '@/components/commons/TipBox';
import { Localized, useLocalization } from '@fluent/react';
import { BaseModal } from '@/components/commons/BaseModal';
import ReactModal from 'react-modal';

export function NeckWarningModal({
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
   * Function to trigger when the neck warning hasn't been accepted
   */
  onClose: () => void;
  /**
   * Function when you press `i understand`
   */
  accept: () => void;
} & ReactModal.Props) {
  const { l10n } = useLocalization();

  // isOpen is checked by checking if the parent modal is opened + our bodyPart is the
  // neck and we haven't showed this warning yet
  return (
    <BaseModal
      isOpen={isOpen}
      shouldCloseOnOverlayClick
      shouldCloseOnEsc
      onRequestClose={onClose}
      className={props.className}
      overlayClassName={props.overlayClassName}
    >
      <div className="flex w-full h-full flex-col ">
        <div className="flex w-full flex-col flex-grow items-center gap-3">
          <Localized
            id="tracker_selection_menu-neck_warning"
            elems={{ b: <b></b> }}
          >
            <WarningBox>
              <b>Warning:</b> A neck tracker can be deadly if adjusted too
              tightly, the strap could cut the circulation to your head!
            </WarningBox>
          </Localized>

          <div className="flex flex-row gap-3 pt-5 place-content-center">
            <Button variant="secondary" onClick={onClose}>
              {l10n.getString('tracker_selection_menu-neck_warning-cancel')}
            </Button>
            <Button
              variant="primary"
              onClick={() => {
                accept();
                sessionStorage.setItem('neckWarning', 'true');
              }}
            >
              {l10n.getString('tracker_selection_menu-neck_warning-done')}
            </Button>
          </div>
        </div>
      </div>
    </BaseModal>
  );
}
