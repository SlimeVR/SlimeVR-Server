import { useLocalization } from '@fluent/react';
import { BaseModal } from './commons/BaseModal';
import { Button } from './commons/Button';
import { Typography } from './commons/Typography';

export function TrackersStillOnModal({
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
    <BaseModal isOpen={isOpen} onRequestClose={cancel} important>
      <div className="flex flex-col gap-3">
        <>
          <div className="flex flex-col items-center gap-3 fill-accent-background-20">
            <div className="flex flex-col items-center gap-2">
              <Typography variant="main-title">
                {l10n.getString('trackers_still_on-modal-title')}
              </Typography>
              <Typography variant="standard">
                {l10n.getString('trackers_still_on-modal-description')}
              </Typography>
            </div>
          </div>

          <Button variant="primary" onClick={accept}>
            {l10n.getString('trackers_still_on-modal-confirm')}
          </Button>
          <Button variant="tertiary" onClick={cancel}>
            {l10n.getString('trackers_still_on-modal-cancel')}
          </Button>
        </>
      </div>
    </BaseModal>
  );
}
