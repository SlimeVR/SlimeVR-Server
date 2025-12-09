import { Localized, useLocalization } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { useWifiForm } from '@/hooks/wifi-form';
import { Button } from '@/components/commons/Button';
import { Input } from '@/components/commons/Input';
import { Typography } from '@/components/commons/Typography';
import classNames from 'classnames';
import { USBIcon } from '@/components/commons/icon/UsbIcon';
import { WifiIcon } from '@/components/commons/icon/WifiIcon';
import { WarningBox } from '@/components/commons/TipBox';

export function WifiCredsPage() {
  const { l10n } = useLocalization();
  const { applyProgress, state } = useOnboarding();
  const { control, handleSubmit, submitWifiCreds, formState } = useWifiForm();

  applyProgress(0.2);

  return (
    <div className="flex flex-col w-full h-full xs:justify-center items-center">
      <div className="grid xs:grid-cols-2 gap-4 max-w-6xl p-4">
        <div className="flex flex-col gap-2">
          <div className="flex gap-2 items-center">
            <div className="bg-accent-background-30 rounded-full p-2 fill-background-10">
              <USBIcon size={24} />
            </div>
            <Typography
              variant="main-title"
              id="onboarding-wifi_creds-dongle-title"
            />
          </div>
          <div className={classNames('flex flex-col gap-2 flex-grow p-2')}>
            <Typography
              whitespace="whitespace-pre-wrap"
              id="onboarding-wifi_creds-dongle-description"
            />
            <Localized id="onboarding-wifi_creds-dongle-wip">
              <WarningBox whitespace>WARNING</WarningBox>
            </Localized>
          </div>
          <div className="flex px-2 p-6">
            <Button
              variant="primary"
              to={state.alonePage ? '/' : '/onboarding/trackers-assign'}
              id="onboarding-wifi_creds-dongle-continue"
            />
          </div>
        </div>
        <form
          className="flex flex-col gap-2"
          onSubmit={handleSubmit(submitWifiCreds)}
        >
          <div className="flex gap-2 items-center">
            <div className="bg-accent-background-30 rounded-full p-2 fill-background-10">
              <WifiIcon variant="navbar" value={1} size={24} />
            </div>
            <Typography variant="main-title" id="onboarding-wifi_creds-v2" />
          </div>

          <div className="flex flex-col gap-2 w-full h-full p-2">
            <Typography
              id="onboarding-wifi_creds-description-v2"
              whitespace="whitespace-pre-wrap"
            />
            <div
              className={classNames(
                'flex flex-col gap-3 p-5 rounded-xl sentry-mask',
                !state.alonePage && 'bg-background-70',
                state.alonePage && 'bg-background-60'
              )}
            >
              <Localized
                id="onboarding-wifi_creds-ssid"
                attrs={{ placeholder: true, label: true }}
              >
                <Input
                  control={control}
                  rules={{ required: true }}
                  name="ssid"
                  type="text"
                  label="SSID"
                  placeholder="ssid"
                  variant="secondary"
                />
              </Localized>
              <Localized
                id="onboarding-wifi_creds-password"
                attrs={{ placeholder: true, label: true }}
              >
                <Input
                  control={control}
                  rules={{
                    validate: {
                      validPassword: (v: string | undefined) =>
                        v === undefined ||
                        v.length === 0 ||
                        new Blob([v]).size >= 8,
                    },
                  }}
                  name="password"
                  type="password"
                  label="Password"
                  placeholder="password"
                  variant="secondary"
                />
              </Localized>
              <div className="flex flex-row gap-3 justify-between">
                <Button
                  variant="secondary"
                  className={state.alonePage ? 'opacity-0' : ''}
                  state={{ alonePage: state.alonePage }}
                  to={'/onboarding/trackers-assign'}
                >
                  {l10n.getString('onboarding-wifi_creds-skip')}
                </Button>
                <Button
                  type="submit"
                  variant="primary"
                  disabled={!formState.isValid}
                >
                  {l10n.getString('onboarding-wifi_creds-submit')}
                </Button>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
