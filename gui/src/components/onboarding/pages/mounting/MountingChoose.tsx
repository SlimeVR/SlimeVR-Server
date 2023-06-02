import { useOnboarding } from '../../../../hooks/onboarding';
import { useLocalization } from '@fluent/react';
import { useState } from 'react';
import { SkipSetupWarningModal } from '../../SkipSetupWarningModal';
import { SkipSetupButton } from '../../SkipSetupButton';
import classNames from 'classnames';
import { Typography } from '../../../commons/Typography';
import { Button } from '../../../commons/Button';

export function MountingChoose() {
  const { l10n } = useLocalization();
  const { applyProgress, skipSetup, state } = useOnboarding();
  const [skipWarning, setSkipWarning] = useState(false);

  applyProgress(0.65);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center relative">
        <SkipSetupButton
          visible={!state.alonePage}
          modalVisible={skipWarning}
          onClick={() => setSkipWarning(true)}
        ></SkipSetupButton>
        <div className="flex flex-col gap-4 justify-center">
          <div className="w-[666px]">
            <Typography variant="main-title">
              {l10n.getString('onboarding-choose_mounting')}
            </Typography>
            <Typography
              variant="standard"
              color="secondary"
              whitespace="whitespace-pre-line"
            >
              {l10n.getString('onboarding-choose_mounting-description')}
            </Typography>
          </div>
          <div className={classNames('h-full w-[760px] min-w-[760px]')}>
            <div className="flex flex-row gap-4 [&>div]:grow">
              <div
                className={classNames(
                  'rounded-lg p-4 flex flex-row',
                  !state.alonePage && 'bg-background-70',
                  state.alonePage && 'bg-background-60'
                )}
              >
                <div className="flex flex-col gap-4">
                  <div className="flex flex-grow flex-col gap-4 max-w-sm">
                    <div>
                      <Typography variant="main-title" bold>
                        {l10n.getString(
                          'onboarding-choose_mounting-manual_mounting'
                        )}
                      </Typography>
                      <Typography variant="vr-accessible" italic>
                        {l10n.getString(
                          'onboarding-choose_mounting-manual_mounting-subtitle'
                        )}
                      </Typography>
                    </div>
                    <div>
                      <Typography color="secondary">
                        {l10n.getString(
                          'onboarding-choose_mounting-manual_mounting-description'
                        )}
                      </Typography>
                    </div>
                  </div>

                  <Button
                    variant={!state.alonePage ? 'secondary' : 'tertiary'}
                    to="/onboarding/mounting/manual"
                    className="self-start mt-auto"
                    state={{ alonePage: state.alonePage }}
                  >
                    {l10n.getString(
                      'onboarding-automatic_mounting-manual_mounting'
                    )}
                  </Button>
                </div>
              </div>
              <div
                className={classNames(
                  'rounded-lg p-4 flex flex-row relative',
                  !state.alonePage && 'bg-background-70',
                  state.alonePage && 'bg-background-60'
                )}
              >
                <div className="flex flex-col gap-4">
                  <div className="flex flex-grow flex-col gap-4 max-w-sm">
                    <div>
                      <img
                        src="/images/boxslime.png"
                        className="absolute w-1/3 -right-10 -top-16"
                      ></img>
                      <Typography variant="main-title" bold>
                        {l10n.getString(
                          'onboarding-choose_mounting-auto_mounting'
                        )}
                      </Typography>
                      <Typography variant="vr-accessible" italic>
                        {l10n.getString(
                          'onboarding-choose_mounting-auto_mounting-subtitle'
                        )}
                      </Typography>
                    </div>
                    <div>
                      <Typography color="secondary">
                        {l10n.getString(
                          'onboarding-choose_mounting-auto_mounting-description'
                        )}
                      </Typography>
                    </div>
                  </div>
                  <Button
                    variant="primary"
                    to="/onboarding/mounting/auto"
                    className="self-start mt-auto"
                    state={{ alonePage: state.alonePage }}
                  >
                    {l10n.getString('onboarding-manual_mounting-auto_mounting')}
                  </Button>
                </div>
              </div>
            </div>
          </div>
          {!state.alonePage && (
            <Button
              variant="secondary"
              className="self-start ml-4"
              to="/onboarding/trackers-assign"
            >
              {l10n.getString('onboarding-previous_step')}
            </Button>
          )}
        </div>
      </div>
      <SkipSetupWarningModal
        accept={skipSetup}
        onClose={() => setSkipWarning(false)}
        isOpen={skipWarning}
      ></SkipSetupWarningModal>
    </>
  );
}
