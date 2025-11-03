import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { VerticalStepComponentProps } from '@/components/commons/VerticalStepper';
import { Localized } from '@fluent/react';

export function DoneStep({ goTo }: VerticalStepComponentProps) {
  return (
    <div className="flex flex-col items-center w-full justify-center gap-5 py-2">
      <div className="flex gap-1 flex-col justify-center items-center pt-10">
        <Localized id="onboarding-stay_aligned-done-description">
          <Typography variant="main-title" />
        </Localized>
        <Localized id="onboarding-stay_aligned-done-description-2">
          <Typography />
        </Localized>
      </div>
      <div className="flex gap-3 justify-between">
        <Localized id="onboarding-stay_aligned-restart">
          <Button variant={'secondary'} onClick={() => goTo('start')} />
        </Localized>
        <Localized id="onboarding-stay_aligned-done">
          <Button
            variant="primary"
            to="/settings/trackers"
            state={{ scrollTo: 'stayaligned' }}
          />
        </Localized>
      </div>
    </div>
  );
}
