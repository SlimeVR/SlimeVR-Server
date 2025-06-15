import { Localized, useLocalization } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import { StepperSlider } from '@/components/onboarding/StepperSlider';
import { HeightContextC, useProvideHeightContext } from '@/hooks/height';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/commons/Button';
import { WarningBox } from '@/components/commons/TipBox';
import { useMemo } from 'react';
import { useAtomValue } from 'jotai';
import { flatTrackersAtom } from '@/store/app-store';
import { BodyPart } from 'solarxr-protocol';
import { ManualHeightStep } from './scaled-steps/ManualHeightStep';
import { DoneStep } from './scaled-steps/Done';
import { ResetProportionsStep } from './scaled-steps/ResetProportions';
import { CheckFloorHeightStep } from './autobone-steps/CheckFloorHeight';
import { CheckHeightStep } from './autobone-steps/CheckHeight';

export function ScaledProportionsPage() {
  const { l10n } = useLocalization();
  const { applyProgress, state } = useOnboarding();
  const heightContext = useProvideHeightContext();
  const navigate = useNavigate();
  const trackers = useAtomValue(flatTrackersAtom);

  const { hasHmd, hasHandControllers } = useMemo(() => {
    const hasHmd = trackers.some(
      (tracker) =>
        tracker.tracker.info?.bodyPart === BodyPart.HEAD &&
        (tracker.tracker.info.isHmd || tracker.tracker.position?.y)
    );
    const hasHandControllers =
      trackers.filter(
        (tracker) =>
          tracker.tracker.info?.bodyPart === BodyPart.LEFT_HAND ||
          tracker.tracker.info?.bodyPart === BodyPart.RIGHT_HAND
      ).length >= 2;

    return { hasHmd, hasHandControllers };
  }, [trackers]);

  const canDoAuto = hasHmd && hasHandControllers;

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

          {!canDoAuto && (
            <WarningBox>
              <Localized
                id="onboarding-scaled_proportions-manual_height-warning"
                elems={{ b: <b /> }}
              >
                <Typography
                  whitespace="whitespace-pre"
                  color="text-background-60"
                />
              </Localized>
              <ul className="list-disc ml-8">
                {!hasHmd && (
                  <Localized id="onboarding-scaled_proportions-manual_height-warning-no_hmd">
                    <li />
                  </Localized>
                )}
                {!hasHandControllers && (
                  <Localized id="onboarding-scaled_proportions-manual_height-warning-no_controllers">
                    <li />
                  </Localized>
                )}
              </ul>
            </WarningBox>
          )}
          <div className="flex">
            <StepperSlider
              variant={state.alonePage ? 'alone' : 'onboarding'}
              steps={
                !canDoAuto
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
            />
          </div>
          {state.alonePage && (
            <div className="flex justify-end">
              <Localized id="onboarding-manual_proportions-title">
                <Button
                  to="/onboarding/body-proportions/manual"
                  variant="secondary"
                  state={{ alonePage: state.alonePage }}
                />
              </Localized>
            </div>
          )}
        </div>
      </div>
    </HeightContextC.Provider>
  );
}
