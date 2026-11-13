import { ReactNode } from 'react';
import { BodyPart, MountingMethod } from 'solarxr-protocol';
import { Localized } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { useMountingSelection } from '@/hooks/tracker-mounting';
import { PickerContext } from '@/hooks/tracker-picker';
import { ArrowLink } from '@/components/commons/ArrowLink';
import { Button } from '@/components/commons/Button';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { PickerPanel } from '@/components/onboarding/pages/trackers-assign/BodyAssignmentPanel';
import { SimpleTrackerRow } from '@/components/onboarding/pages/trackers-assign/TrackerAssignmentList';
import { ExtremityGroupRenderer } from '@/components/onboarding/ExtremityAssignment';
import {
  ExtremityGroupCard,
  PartCardRenderer,
} from '@/components/onboarding/parts/PartCard';
import { FlatDeviceTracker } from '@/store/app-store';
import { MountingPartCard } from './MountingPartCard';
import { MountingSelectionMenu } from './MountingSelectionMenu';

const isManuallyMounted = (td: FlatDeviceTracker | undefined) =>
  td?.tracker.info?.lastMountingMethod === MountingMethod.MANUAL;

const renderCard: PartCardRenderer = (props) =>
  isManuallyMounted(props.td) ? (
    <MountingPartCard key={props.role} {...props} />
  ) : null;

const renderGroup: ExtremityGroupRenderer = ({
  id,
  labelId,
  direction,
  rows,
  edge,
  flow,
}) => {
  const mounted = rows.filter(({ td }) => isManuallyMounted(td));
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

function AutoMountedList({
  trackers,
  selected,
  onSelect,
}: {
  trackers: FlatDeviceTracker[];
  selected: BodyPart;
  onSelect: (part: BodyPart) => void;
}) {
  return (
    <div className="flex flex-col gap-2 min-h-0">
      <div className="flex flex-col">
        <Typography bold id="onboarding-manual_mounting-automatic-title" />
        <Typography
          color="secondary"
          id="onboarding-manual_mounting-automatic-description"
        />
      </div>
      <div className="flex flex-col gap-2 min-h-0 overflow-y-auto -mx-2 px-2">
        {trackers.map((td) => {
          const part = td.tracker.info?.bodyPart ?? BodyPart.NONE;

          return (
            <SimpleTrackerRow
              key={td.tracker.trackerId}
              tracker={td.tracker}
              device={td.device}
              variant="tertiary"
              selected={selected === part}
              onClick={() => onSelect(part)}
            />
          );
        })}
      </div>
    </div>
  );
}

function AssignmentLink() {
  const { state } = useOnboarding();

  return (
    <ArrowLink
      to="/onboarding/trackers-assign"
      state={state}
      direction="right"
      variant="boxed-2"
    >
      <div className="flex flex-col text-left">
        <Typography bold id="onboarding-manual_mounting-assign_link-title" />
        <Typography
          color="secondary"
          id="onboarding-manual_mounting-assign_link-description"
        />
      </div>
    </ArrowLink>
  );
}

export function ManualMounting({ footer }: { footer?: ReactNode }) {
  const mounting = useMountingSelection();
  const autoMounted = mounting.assignedTrackers.filter(
    (td) => !isManuallyMounted(td)
  );

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
        <div className="flex flex-col w-full xs:max-w-sm gap-3 shrink-0 min-h-0">
          <Typography variant="main-title" id="onboarding-manual_mounting" />
          <Typography id="onboarding-manual_mounting-description" />
          <Localized id="tips-find_tracker">
            <TipBox />
          </Localized>
          {autoMounted.length > 0 && (
            <AutoMountedList
              trackers={autoMounted}
              selected={mounting.target}
              onSelect={mounting.selectPart}
            />
          )}
          <div className="mt-auto">
            <AssignmentLink />
          </div>
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
