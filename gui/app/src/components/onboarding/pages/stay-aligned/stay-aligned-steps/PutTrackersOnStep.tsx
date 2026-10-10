import { BodyDisplay } from '@/components/commons/BodyDisplay';
import { Button } from '@/components/commons/Button';
import { TipBox, WarningBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { VerticalStepComponentProps } from '@/components/commons/VerticalStepper';
import { assignedTrackersAtom } from '@/store/app-store';
import { Localized } from '@fluent/react';
import { useAtomValue } from 'jotai';

export function PutTrackersOnStep({ nextStep }: VerticalStepComponentProps) {
  const assignedTrackers = useAtomValue(assignedTrackersAtom);

  // Keep the button while in dev
  const canContinue = assignedTrackers.length >= 5 || import.meta.env.DEV;

  return (
    <div className="flex flex-col w-full py-2">
      <div className="flex flex-col flex-grow gap-2">
        <div className="flex flex-grow flex-col gap-4">
          <div>
            <Localized id="onboarding-stay_aligned-put_trackers_on-description">
              <Typography />
            </Localized>
          </div>
          <div className="flex">
            <Localized id="tips-find_tracker">
              <TipBox>Tip</TipBox>
            </Localized>
          </div>
          {assignedTrackers.length < 5 && (
            <div className="flex">
              <Localized id="onboarding-stay_aligned-put_trackers_on-trackers_warning">
                <WarningBox>Warning</WarningBox>
              </Localized>
            </div>
          )}
        </div>
        <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-16">
          <div className="h-[500px]">
            <BodyDisplay
              trackers={assignedTrackers}
              dotsSize={15}
              hideUnassigned={true}
            />
          </div>
        </div>
        <div className="flex flex-col gap-3">
          <div className="flex gap-3 justify-end">
            <Localized id="onboarding-stay_aligned-put_trackers_on-next">
              <Button
                variant="primary"
                onClick={nextStep}
                disabled={!canContinue}
              />
            </Localized>
          </div>
        </div>
      </div>
    </div>
  );
}
