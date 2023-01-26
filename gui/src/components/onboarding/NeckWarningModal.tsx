import { BodyPart } from 'solarxr-protocol';
import { Button } from '../commons/Button';
import { WarningBox } from '../commons/TipBox';
import { Localized, useLocalization } from '@fluent/react';
import { BaseModal } from '../commons/BaseModal';
import ReactModal from 'react-modal';

export function NeckWarningModal({
  isOpen = true,
  hasShowed = false,
  onClose,
  setShowed,
  bodyPart,
  ...props
}: {
  isOpen: boolean;
  hasShowed: boolean;
  bodyPart: BodyPart | null;
  onClose: () => void;
  setShowed: (arg0: boolean) => void;
} & ReactModal.Props) {
  const { l10n } = useLocalization();
  // Skip popup if bodyPart isn't neck
  if (isOpen && !hasShowed && bodyPart !== BodyPart.NECK) {
    setShowed(true);
  }
  // Reset when no longer opened
  if (!isOpen && hasShowed) {
    setShowed(false);
  }

  return (
    <BaseModal
      isOpen={isOpen && bodyPart === BodyPart.NECK && !hasShowed}
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
            <WarningBox>Warning!</WarningBox>
          </Localized>

          <div className="flex flex-row gap-3 pt-5 place-content-center">
            <Button variant="secondary" onClick={onClose}>
              {l10n.getString('tracker_selection_menu-neck_warning-cancel')}
            </Button>
            <Button variant="primary" onClick={() => setShowed(true)}>
              {l10n.getString('tracker_selection_menu-neck_warning-done')}
            </Button>
          </div>
        </div>
      </div>
    </BaseModal>
  );
}
