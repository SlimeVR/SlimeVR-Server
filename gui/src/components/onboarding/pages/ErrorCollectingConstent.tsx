import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { useConfig } from '@/hooks/config';

export function ErrorCollectingConsentPage() {
  const { config } = useConfig();


  const setConfig = (consent:boolean) => {
  if (!config) throw 'invalid state!'
    config.errorTracking = consent
  }


  const accept = () => {
    setConfig(true)
  }

  const cancel = () => {
    setConfig(false)
  }

  const { l10n } = useLocalization();
  return (
    <div className="flex items-center flex-col gap-3">
      <>
        <div className="flex flex-col w-full gap-3 justify-center">
          <div className="flex flex-col items-center gap-2">
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
                  textAlign='text-center'
                />
              </Localized>
            </div>
            <div className={'flex flex-row gap-2 justify-center'}>
              <Button variant="tertiary" to={'/onboarding/wifi-creds'} onClick={cancel}>
                {l10n.getString('error_collection_modal-cancel')}
              </Button>
              <Button variant="primary" to={'/onboarding/wifi-creds'} onClick={accept}>
                {l10n.getString('error_collection_modal-confirm')}
              </Button>
            </div>
          </div>
      </>
    </div>
  )
}
