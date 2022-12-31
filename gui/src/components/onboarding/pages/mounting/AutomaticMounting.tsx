import { useTranslation } from 'react-i18next';
import { useOnboarding } from '../../../../hooks/onboarding';
import { ArrowLink } from '../../../commons/ArrowLink';
import { Button } from '../../../commons/Button';
import { Typography } from '../../../commons/Typography';
import { StepperSlider } from '../../StepperSlider';
import { DoneStep } from './mounting-steps/Done';
import { MountingResetStep } from './mounting-steps/MountingReset';
import { PreparationStep } from './mounting-steps/Preparation';
import { PutTrackersOnStep } from './mounting-steps/PutTrackersOn';

export function AutomaticMountingPage() {
  const { t } = useTranslation();
  const { applyProgress, skipSetup, state } = useOnboarding();

  applyProgress(0.7);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
        <div className="flex flex-col w-full h-full justify-center max-w-3xl gap-5">
          <div className="flex flex-col max-w-lg gap-3">
            {!state.alonePage && (
              <ArrowLink to="/onboarding/enter-vr" direction="left">
                {t('onboarding-automatic_mounting-back')}
              </ArrowLink>
            )}
            <Typography variant="main-title">
              {t('onboarding-automatic_mounting-title')}
            </Typography>
            <Typography color="secondary">
              {t('onboarding-automatic_mounting-description')}
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
              <Button variant="secondary" to="/" onClick={skipSetup}>
                {t('onboarding-skip')}
              </Button>
            )}
          </div>
          <div className="flex gap-3">
            <Button
              variant="secondary"
              state={{ alonePage: state.alonePage }}
              to="/onboarding/mounting/manual"
            >
              {t('onboarding-automatic_mounting-manual_mounting')}
            </Button>
            {!state.alonePage && (
              <Button variant="primary" to="/onboarding/reset-tutorial">
                {t('onboarding-automatic_mounting-next')}
              </Button>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
