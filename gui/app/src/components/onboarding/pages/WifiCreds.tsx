import { Localized } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { useWifiForm } from '@/hooks/wifi-form';
import { Button } from '@/components/commons/Button';
import { Input } from '@/components/commons/Input';
import { Typography } from '@/components/commons/Typography';
import classNames from 'classnames';
import { WifiIcon } from '@/components/commons/icon/WifiIcon';
import { DongleSectionContent } from './Dongle';

export function WifiCredsPage() {
  const { applyProgress, state } = useOnboarding();
  const { control, handleSubmit, submitWifiCreds, formState } = useWifiForm();

  applyProgress(0.2);

  return (
    <div className="flex flex-col w-full h-full xs:justify-center items-center">
      <div
        className={classNames('grid gap-4 max-w-6xl p-4', {
          'xs:grid-cols-2': state.alonePage,
        })}
      >
        {state.alonePage && <DongleSectionContent />}
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
                {!state.alonePage ? (
                  <Button
                    variant="secondary"
                    state={{ alonePage: state.alonePage }}
                    to={'/onboarding/quiz/slime-set'}
                    id="onboarding-wifi_creds-back-v2"
                  />
                ) : (
                  <div />
                )}
                <Button
                  type="submit"
                  variant="primary"
                  disabled={!formState.isValid}
                  id="onboarding-wifi_creds-submit"
                />
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
