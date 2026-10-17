import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { SkeletonVisualizerWidget } from '@/components/widgets/SkeletonVisualizerWidget';
import {
  MountingFlowLayout,
  MountingPhaseProps,
} from '@/components/onboarding/pages/mounting/MountingFlow';

export function DonePhase({
  flow,
  restart,
  alonePage,
}: MountingPhaseProps & { alonePage: boolean }) {
  return (
    <MountingFlowLayout
      flow={flow}
      body={<Typography id="onboarding-automatic_mounting-done-description" />}
      media={<SkeletonVisualizerWidget />}
      actions={
        <>
          <Button
            variant={alonePage ? 'tertiary' : 'secondary'}
            onClick={restart}
            id="onboarding-automatic_mounting-done-restart"
          />
          {alonePage ? (
            <Button
              variant="primary"
              to="/"
              id="onboarding-automatic_mounting-return-home"
            />
          ) : (
            <Button
              variant="primary"
              to="/onboarding/body-proportions/scaled"
              id="onboarding-automatic_mounting-next"
            />
          )}
        </>
      }
    />
  );
}
