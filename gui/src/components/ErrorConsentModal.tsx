import { Localized, useLocalization } from '@fluent/react';
import { BaseModal } from './commons/BaseModal';
import { Button } from './commons/Button';
import { Typography } from './commons/Typography';

export function ErrorConsentModal({
  isOpen = true,
  cancel,
  accept,
}: {
  /**
   * Is the parent/sibling component opened?
   */
  isOpen: boolean;
  /**
   * Function to trigger when you still want to close the app
   */
  accept: () => void;
  /**
   * Function to trigger when cancelling app close
   */
  cancel?: () => void;
}) {
  const { l10n } = useLocalization();

  return (
    <BaseModal isOpen={isOpen} onRequestClose={cancel} closeable={false}>
      <div className="flex flex-col gap-3">
        <>
          <div className="flex flex-col items-center gap-3 fill-accent-background-20">
            <div className="flex flex-col items-center gap-2 max-w-[512px]">
              <Typography variant="main-title">
                {l10n.getString('error_collection_modal-title')}
              </Typography>
              <Localized
                id={'error_collection_modal-description_v2'}
                elems={{
                  b: <b />,
                  h1: <span className="text-lg font-bold" />,
                }}
              >
                <Typography
                  variant="standard"
                  whitespace="whitespace-pre-line"
                />
              </Localized>
            </div>
          </div>

          <Button variant="primary" onClick={accept}>
            {l10n.getString('error_collection_modal-confirm')}
          </Button>
          <Button variant="tertiary" onClick={cancel}>
            {l10n.getString('error_collection_modal-cancel')}
          </Button>
        </>
      </div>
    </BaseModal>
  );
}
