import { useLocalization } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import { StepperSlider } from '@/components/onboarding/StepperSlider';
import { CheckHeightStep } from './autobone-steps/CheckHeight';
import { HeightContextC, useProvideHeightContext } from '@/hooks/height';
import { CheckFloorHeightStep } from './autobone-steps/CheckFloorHeight';
import { ResetProportionsStep } from './scaled-steps/ResetProportions';
import { DoneStep } from './scaled-steps/Done';

export function ScaledProportionsPage() {
  const { l10n } = useLocalization();
  const { applyProgress, state } = useOnboarding();
  const heightContext = useProvideHeightContext();

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
              steps={[
                { type: 'numbered', component: CheckHeightStep },
                { type: 'numbered', component: CheckFloorHeightStep },
                { type: 'numbered', component: ResetProportionsStep },
                { type: 'fullsize', component: DoneStep },
              ]}
            ></StepperSlider>
          </div>
        </div>
      </div>
    </HeightContextC.Provider>
  );
}
