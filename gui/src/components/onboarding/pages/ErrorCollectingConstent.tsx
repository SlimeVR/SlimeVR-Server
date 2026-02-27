import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { useConfig } from '@/hooks/config';

export function ErrorCollectingConsentPage() {
  const { setConfig } = useConfig();

  const accept = () => {
    setConfig({ errorTracking: true });
  };

  const cancel = () => {
    setConfig({ errorTracking: false });
  };

  const { l10n } = useLocalization();
  return (
    <div className="flex items-center justify-center h-full flex-col gap-3 p-4">
      <div className="max-w-2xl flex flex-col gap-4">
        <div className="flex flex-col w-full gap-4">
          <Typography variant="main-title" id="error_collection_modal-title" />
          <Localized
            id={'error_collection_modal-description_v2'}
            elems={{
              b: <b />,
              h1: <span className="text-md font-bold" />,
            }}
          >
            <Typography variant="standard" whitespace="whitespace-pre-line" />
          </Localized>
        </div>
        <div className={'flex flex-row gap-2 justify-between'}>
          <Button
            variant="tertiary"
            to="/onboarding/wifi-creds"
            onClick={cancel}
            id="error_collection_modal-cancel"
          />
          <Button
            variant="primary"
            to="/onboarding/wifi-creds"
            onClick={accept}
          >
            {l10n.getString('error_collection_modal-confirm')}
          </Button>
        </div>
      </div>
    </div>
  );
}
