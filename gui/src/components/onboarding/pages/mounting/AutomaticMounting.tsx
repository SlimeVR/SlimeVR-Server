import { useOnboarding } from '../../../../hooks/onboarding';
import { Button } from '../../../commons/Button';
import { Typography } from '../../../commons/Typography';
import { StepperSlider } from '../../StepperSlider';
import { DoneStep } from './mounting-steps/Done';
import { MountingResetStep } from './mounting-steps/MountingReset';
import { PreparationStep } from './mounting-steps/Preparation';
import { PutTrackersOnStep } from './mounting-steps/PutTrackersOn';
import { useLocalization } from '@fluent/react';

export function AutomaticMountingPage() {
  const { l10n } = useLocalization();
  const { applyProgress, skipSetup, state } = useOnboarding();

  applyProgress(0.7);

  return (
    <>
      <div className="flex flex-col gap-2 h-full items-center w-full justify-center">
        <div className="flex flex-col w-full h-full justify-center max-w-3xl gap-5">
          <div className="flex flex-col max-w-lg gap-3">
            <Typography variant="main-title">
              {l10n.getString('onboarding-automatic_mounting-title')}
            </Typography>
            <Typography color="secondary">
              {l10n.getString('onboarding-automatic_mounting-description')}
            </Typography>
          </div>
          <div className="flex">
            <StepperSlider
              variant={state.alonePage ? 'alone' : 'onboarding'}
              steps={[
                { type: 'numbered', component: PutTrackersOnStep },
                { type: 'numbered', component: PreparationStep },
                { type: 'numbered', component: MountingResetStep },
                { type: 'fullsize', component: DoneStep },
              ]}
            ></StepperSlider>
          </div>
        </div>
        <div className="w-full pb-4 flex flex-row">
          <div className="flex flex-grow gap-3">
            {!state.alonePage && (
              <>
                <Button variant="secondary" to="/onboarding/trackers-assign">
                  {l10n.getString('onboarding-previous_step')}
                </Button>
                <Button variant="secondary" to="/" onClick={skipSetup}>
                  {l10n.getString('onboarding-skip')}
                </Button>
              </>
            )}
          </div>
          <div className="flex gap-3">
            <Button
              variant="secondary"
              state={{ alonePage: state.alonePage }}
              to="/onboarding/mounting/manual"
            >
              {l10n.getString('onboarding-automatic_mounting-manual_mounting')}
            </Button>
            {!state.alonePage && (
              <Button variant="primary" to="/onboarding/reset-tutorial">
                {l10n.getString('onboarding-automatic_mounting-next')}
              </Button>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
