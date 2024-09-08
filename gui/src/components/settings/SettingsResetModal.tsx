import { Button } from '@/components/commons/Button';
import { WarningBox } from '@/components/commons/TipBox';
import { Localized, useLocalization } from '@fluent/react';
import { BaseModal } from '@/components/commons/BaseModal';
import ReactModal from 'react-modal';

export function SettingsResetModal({
  isOpen = true,
  onClose,
  accept,
  variant,
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
   * Function when you press `Reset settings`
   */
  accept: () => void;
  /**
   * Type of reset
   */
  variant: 'gui' | 'server' | 'all';
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
            id="settings-utils-advanced-reset_warning"
            elems={{ b: <b></b> }}
            vars={{ type: variant }}
          >
            <WarningBox>
              <b>Warning:</b> This will reset your {variant} settings to the
              defaults.
              <br />
              Are you sure you want to do this?
            </WarningBox>
          </Localized>

          <div className="flex flex-row gap-3 pt-5 place-content-center">
            <Button variant="primary" onClick={onClose}>
              {l10n.getString('settings-utils-advanced-reset_warning-cancel')}
            </Button>
            <Button
              variant="tertiary"
              onClick={() => {
                accept();
              }}
            >
              {l10n.getString('settings-utils-advanced-reset_warning-reset')}
            </Button>
          </div>
        </div>
      </div>
    </BaseModal>
  );
}
