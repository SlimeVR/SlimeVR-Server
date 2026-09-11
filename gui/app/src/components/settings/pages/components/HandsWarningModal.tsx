import { Button } from '@/components/commons/Button';
import { WarningBox } from '@/components/commons/TipBox';
import { Localized } from '@fluent/react';
import { BaseModal } from '@/components/commons/BaseModal';
import ReactModal from 'react-modal';

export function HandsWarningModal({
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
   * Function when you press `Yes`
   */
  accept: () => void;
} & ReactModal.Props) {
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
          <Localized id="settings-routing-hands-warning" elems={{ b: <b /> }}>
            <WarningBox>
              <b>Warning:</b> Routing hands to SteamVR/Monado replaces your
              controllers!
            </WarningBox>
          </Localized>

          <div className="flex flex-row gap-3 pt-5 place-content-center">
            <Button
              variant="primary"
              onClick={onClose}
              id="settings-routing-hands-warning-cancel"
            />
            <Button
              variant="tertiary"
              onClick={accept}
              id="settings-routing-hands-warning-done"
            />
          </div>
        </div>
      </div>
    </BaseModal>
  );
}
