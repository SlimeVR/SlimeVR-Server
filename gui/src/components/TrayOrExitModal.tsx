import { useLocalization } from '@fluent/react';
import { BaseModal } from './commons/BaseModal';
import { Button } from './commons/Button';
import { Typography } from './commons/Typography';
import { useForm } from 'react-hook-form';
import { Radio } from './commons/Radio';

interface TrayOrExitForm {
  exitType: string;
}

export function TrayOrExitModal({
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
   * @param tray If asked for close to tray
   */
  accept: (tray: boolean) => void;
  /**
   * Function to trigger when cancelling app close
   */
  cancel?: () => void;
}) {
  const { l10n } = useLocalization();
  const { control, handleSubmit } = useForm<TrayOrExitForm>({
    defaultValues: {
      exitType: '0',
    },
  });

  return (
    <BaseModal isOpen={isOpen} onRequestClose={cancel} important>
      <form
        className="flex flex-col gap-3 w-[27rem]"
        onSubmit={handleSubmit((form) => accept(form.exitType === '1'))}
      >
        <div className="flex flex-col items-center gap-3 fill-accent-background-20">
          <div className="flex flex-col items-center gap-2">
            <Typography variant="main-title">
              {l10n.getString('tray_or_exit_modal-title')}
            </Typography>
            <div>
              <Typography variant="standard" whitespace="whitespace-pre-line">
                {l10n.getString('tray_or_exit_modal-description')}
              </Typography>
            </div>
          </div>
          <Radio
            control={control}
            name="exitType"
            label={l10n.getString('tray_or_exit_modal-radio-exit')}
            value="0"
          ></Radio>
          <Radio
            control={control}
            name="exitType"
            label={l10n.getString('tray_or_exit_modal-radio-tray')}
            value="1"
          ></Radio>
        </div>

        <Button type="submit" variant="primary">
          {l10n.getString('tray_or_exit_modal-submit')}
        </Button>
        <Button variant="tertiary" onClick={cancel}>
          {l10n.getString('tray_or_exit_modal-cancel')}
        </Button>
      </form>
    </BaseModal>
  );
}
