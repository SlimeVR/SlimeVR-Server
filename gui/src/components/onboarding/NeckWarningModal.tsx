import { BodyPart } from 'solarxr-protocol';
import { Button } from '../commons/Button';
import { WarningBox } from '../commons/TipBox';
import { Localized, useLocalization } from '@fluent/react';
import { BaseModal } from '../commons/BaseModal';
import ReactModal from 'react-modal';

export function NeckWarningModal({
  isOpen = true,
  hasShown = false,
  onClose,
  setShown,
  bodyPart,
  ...props
}: {
  /**
   * Is the parent/sibling component opened?
   */
  isOpen: boolean;
  /**
   * Has this warning be shown
   * We want the parent/sibling component to tell us this because they also should
   * know about the state
   */
  hasShown: boolean;
  /**
   * The current body part chosen
   */
  bodyPart: BodyPart | null;
  /**
   * Function to trigger when the neck warning hasn't been accepted
   */
  onClose: () => void;
  /**
   * Function to change the hasShown value
   */
  setShown: (arg0: boolean) => void;
} & ReactModal.Props) {
  const { l10n } = useLocalization();
  // Skip popup if bodyPart isn't neck or we already showed the warning in this session
  if (
    isOpen &&
    !hasShown &&
    (bodyPart !== BodyPart.NECK || sessionStorage.getItem('neckWarning'))
  ) {
    setShown(true);
  }
  // Reset shown to false when no longer opened
  if (!isOpen && hasShown) {
    setShown(false);
  }

  // isOpen is checked by checking if the parent modal is opened + our bodyPart is the
  // neck and we havent showed this warning yet
  return (
    <BaseModal
      isOpen={isOpen && bodyPart === BodyPart.NECK && !hasShown}
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
                setShown(true);
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
