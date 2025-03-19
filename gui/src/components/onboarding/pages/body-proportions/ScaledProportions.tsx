import { Localized, useLocalization } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import { StepperSlider } from '@/components/onboarding/StepperSlider';
import { CheckHeightStep } from './autobone-steps/CheckHeight';
import { HeightContextC, useProvideHeightContext } from '@/hooks/height';
import { CheckFloorHeightStep } from './autobone-steps/CheckFloorHeight';
import { ResetProportionsStep } from './scaled-steps/ResetProportions';
import { DoneStep } from './scaled-steps/Done';
import { useNavigate } from 'react-router-dom';
import { ManualHeightStep } from './scaled-steps/ManualHeightStep';
import { Button } from '@/components/commons/Button';
import { useAtomValue } from 'jotai';
import { hasHMDTrackerAtom } from '@/store/app-store';

export function ScaledProportionsPage() {
  const { l10n } = useLocalization();
  const { applyProgress, state } = useOnboarding();
  const heightContext = useProvideHeightContext();
  const navigate = useNavigate();

  const hmdTracker = useAtomValue(hasHMDTrackerAtom);

  applyProgress(0.9);

  return (
    <HeightContextC.Provider value={heightContext}>
      <div className="flex flex-col gap-5 h-full items-center w-full xs:justify-center overflow-y-auto overflow-x-hidden relative px-4 pb-4">
        <div className="flex flex-col w-full xs:h-full xs:justify-center max-w-3xl gap-5">
          <div className="flex flex-col max-w-lg gap-3">
            <Typography variant="main-title">
              {l10n.getString('onboarding-scaled_proportions-title')}
            </Typography>
            <div>
              <Typography color="secondary">
                {l10n.getString('onboarding-scaled_proportions-description')}
              </Typography>
            </div>
          </div>
          <div className="flex">
            <StepperSlider
              variant={state.alonePage ? 'alone' : 'onboarding'}
              steps={
                !hmdTracker
                  ? [
                      { type: 'numbered', component: ManualHeightStep },
                      { type: 'numbered', component: ResetProportionsStep },
                      { type: 'fullsize', component: DoneStep },
                    ]
                  : [
                      { type: 'numbered', component: CheckHeightStep },
                      { type: 'numbered', component: CheckFloorHeightStep },
                      { type: 'numbered', component: ResetProportionsStep },
                      { type: 'fullsize', component: DoneStep },
                    ]
              }
              back={() => navigate('/onboarding/reset-tutorial', { state })}
            ></StepperSlider>
          </div>
          {state.alonePage && (
            <div className="flex justify-end">
              <Localized id="onboarding-manual_proportions-title">
                <Button
                  to="/onboarding/body-proportions/manual"
                  variant="secondary"
                  state={{ alonePage: state.alonePage }}
                ></Button>
              </Localized>
            </div>
          )}
        </div>
      </div>
    </HeightContextC.Provider>
  );
}
