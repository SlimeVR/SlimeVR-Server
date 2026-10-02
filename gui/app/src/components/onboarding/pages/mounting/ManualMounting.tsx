import { ReactNode } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { Localized } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { useMountingSelection } from '@/hooks/tracker-mounting';
import { PickerContext } from '@/hooks/tracker-picker';
import { Button } from '@/components/commons/Button';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { PickerPanel } from '@/components/onboarding/pages/trackers-assign/BodyAssignmentPanel';
import { ExtremityGroupRenderer } from '@/components/onboarding/ExtremityAssignment';
import {
  ExtremityGroupCard,
  PartCardRenderer,
} from '@/components/onboarding/parts/PartCard';
import { MountingPartCard } from './MountingPartCard';
import { MountingSelectionMenu } from './MountingSelectionMenu';

const renderCard: PartCardRenderer = (props) => (
  <MountingPartCard key={props.role} {...props} />
);

const renderGroup: ExtremityGroupRenderer = ({
  id,
  labelId,
  direction,
  rows,
  edge,
  flow,
}) => {
  const mounted = rows.filter(({ td }) => !!td);
  if (mounted.length === 0) return null;

  return (
    <ExtremityGroupCard
      key={id}
      edge={edge}
      flow={flow}
      labelId={labelId}
      direction={direction}
      rows={mounted}
      renderRow={renderCard}
    />
  );
};

export function ManualMounting({ footer }: { footer?: ReactNode }) {
  const mounting = useMountingSelection();

  return (
    <>
      <MountingSelectionMenu
        bodyPart={mounting.target}
        currRotation={mounting.currRotation}
        isOpen={mounting.target !== BodyPart.NONE}
        onClose={mounting.clearTarget}
        onDirectionSelected={mounting.setDirection}
      />
      <div className="w-full h-full flex mobile:flex-col xs:flex-row min-h-0 overflow-hidden gap-3 px-4 xs:px-8 py-4">
        <div className="flex flex-col w-full xs:max-w-sm gap-3 shrink-0">
          <Typography variant="main-title" id="onboarding-manual_mounting" />
          <Typography id="onboarding-manual_mounting-description" />
          <Localized id="tips-find_tracker">
            <TipBox />
          </Localized>
          {footer}
        </div>
        <PickerContext.Provider value={mounting}>
          <PickerPanel
            dots="tap"
            renderCard={renderCard}
            renderGroup={renderGroup}
          />
        </PickerContext.Provider>
      </div>
    </>
  );
}

export function ManualMountingPage() {
  const { applyProgress, state } = useOnboarding();

  applyProgress(0.6);

  return (
    <ManualMounting
      footer={
        <div className="flex flex-row gap-3 mt-auto">
          <Button
            variant="secondary"
            to="/onboarding/mounting/choose"
            state={state}
            id="onboarding-previous_step"
          />
          {!state.alonePage && (
            <Button
              variant="primary"
              to="/onboarding/body-proportions/scaled"
              id="onboarding-manual_mounting-next"
            />
          )}
        </div>
      }
    />
  );
}
