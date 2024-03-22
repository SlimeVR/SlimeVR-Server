import { BaseModal } from '@/components/commons/BaseModal';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { Localized, useLocalization } from '@fluent/react';

export function TryManualFirstModal({
  isOpen = true,
  cancel,
  accept,
}: {
  /**
   * Is the parent/sibling component opened?
   */
  isOpen: boolean;
  /**
   * Function to trigger when they want to do automatic anyways
   */
  accept: () => void;
  /**
   * Function to trigger when they agree to do manual
   */
  cancel?: () => void;
}) {
  const { l10n } = useLocalization();

  return (
    <BaseModal isOpen={isOpen} onRequestClose={cancel}>
      <div className="flex flex-col gap-3">
        <>
          <div className="flex flex-col items-center gap-3 fill-accent-background-20">
            <div className="flex flex-col items-center gap-2">
              <Typography
                variant="main-title"
                whitespace="whitespace-pre"
                textAlign="text-center"
              >
                {l10n.getString(
                  'onboarding-choose_mounting-manual_modal-title'
                )}
              </Typography>
              <div className="w-[30vw] min-w-fit">
                <Localized
                  id="onboarding-choose_mounting-manual_modal-description"
                  elems={{ b: <b></b> }}
                >
                  <Typography variant="standard">
                    You should do manual mounting first!
                  </Typography>
                </Localized>
              </div>
            </div>
          </div>

          <Button variant="primary" onClick={accept}>
            {l10n.getString('onboarding-choose_mounting-manual_modal-confirm')}
          </Button>
          <Button variant="tertiary" onClick={cancel}>
            {l10n.getString('onboarding-choose_mounting-manual_modal-cancel')}
          </Button>
        </>
      </div>
    </BaseModal>
  );
}
