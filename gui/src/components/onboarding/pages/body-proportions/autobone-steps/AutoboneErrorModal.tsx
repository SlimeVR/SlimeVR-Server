import { Localized, useLocalization } from '@fluent/react';
import ReactModal from 'react-modal';
import { BaseModal } from '@/components/commons/BaseModal';
import { WarningBox } from '@/components/commons/TipBox';
import { Button } from '@/components/commons/Button';
import { A } from '@/components/commons/A';
import { DOCS_SITE, SLIMEVR_DISCORD } from '@/App';

export function AutoboneErrorModal({
  isOpen = true,
  onClose,
  ...props
}: {
  /**
   * Is the parent/sibling component opened?
   */
  isOpen: boolean;
  /**
   * Function to trigger when closed or accepted
   */
  onClose: () => void;
} & ReactModal.Props) {
  const { l10n } = useLocalization();

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
            id="onboarding-automatic_proportions-error_modal"
            elems={{
              b: <b></b>,
              docs: (
                <A
                  href={`${DOCS_SITE}/server/body-config.html#common-issues--debugging`}
                ></A>
              ),
              discord: <A href={SLIMEVR_DISCORD}></A>,
            }}
          >
            <WarningBox>
              <b>Warning:</b> An autobone error happened!
            </WarningBox>
          </Localized>

          <div className="flex flex-row gap-3 pt-5 place-content-center">
            <Button variant="primary" onClick={onClose}>
              {l10n.getString(
                'onboarding-automatic_proportions-error_modal-confirm'
              )}
            </Button>
          </div>
        </div>
      </div>
    </BaseModal>
  );
}
