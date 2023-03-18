import { useMemo, useState } from 'react';
import { AssignTrackerRequestT, BodyPart, RpcMessage } from 'solarxr-protocol';
import { FlatDeviceTracker } from '../../../../hooks/app';
import { useOnboarding } from '../../../../hooks/onboarding';
import { useTrackers } from '../../../../hooks/tracker';
import { useWebsocketAPI } from '../../../../hooks/websocket-api';
import { MountingOrientationDegreesToQuatT } from '../../../../maths/quaternion';
import { Button } from '../../../commons/Button';
import { TipBox } from '../../../commons/TipBox';
import { Typography } from '../../../commons/Typography';
import { BodyAssignment } from '../../BodyAssignment';
import { MountingSelectionMenu } from './MountingSelectionMenu';
import { useLocalization } from '@fluent/react';

export function ManualMountingPage() {
  const { l10n } = useLocalization();
  const { applyProgress, skipSetup, state } = useOnboarding();
  const { sendRPCPacket } = useWebsocketAPI();

  const [selectedRole, setSelectRole] = useState<BodyPart>(BodyPart.NONE);

  applyProgress(0.7);

  const { useAssignedTrackers } = useTrackers();
  const assignedTrackers = useAssignedTrackers();

  const trackerPartGrouped = useMemo(
    () =>
      assignedTrackers.reduce<{ [key: number]: FlatDeviceTracker[] }>(
        (curr, td) => {
          const key = td.tracker.info?.bodyPart || BodyPart.NONE;
          return {
            ...curr,
            [key]: [...(curr[key] || []), td],
          };
        },
        {}
      ),
    [assignedTrackers]
  );

  const onDirectionSelected = (mountingOrientationDegrees: number) => {
    (trackerPartGrouped[selectedRole] || []).forEach((td) => {
      const assignreq = new AssignTrackerRequestT();

      assignreq.bodyPosition = td.tracker.info?.bodyPart || BodyPart.NONE;
      assignreq.mountingOrientation = MountingOrientationDegreesToQuatT(
        mountingOrientationDegrees
      );
      assignreq.trackerId = td.tracker.trackerId;
      sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
    });

    setSelectRole(BodyPart.NONE);
  };

  return (
    <>
      <MountingSelectionMenu
        isOpen={selectedRole !== BodyPart.NONE}
        onClose={() => setSelectRole(BodyPart.NONE)}
        onDirectionSelected={onDirectionSelected}
      ></MountingSelectionMenu>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
        <div className="flex flex-col w-full h-full justify-center items-center">
          <div className="flex md:gap-8">
            <div className="flex flex-col w-full max-w-md gap-3">
              <Typography variant="main-title">
                {l10n.getString('onboarding-manual_mounting')}
              </Typography>
              <Typography color="secondary">
                {l10n.getString('onboarding-manual_mounting-description')}
              </Typography>
              <TipBox>{l10n.getString('tips-find_tracker')}</TipBox>
            </div>
            <div className="flex flex-col flex-grow gap-3 rounded-xl fill-background-50">
              <BodyAssignment
                onlyAssigned={true}
                advanced={true}
                onRoleSelected={setSelectRole}
              ></BodyAssignment>
            </div>
          </div>
        </div>
        <div className="w-full pb-4 flex flex-row">
          <div className="flex flex-grow gap-3">
            {!state.alonePage && (
              <>
                <Button variant="secondary" to="/onboarding/trackers-assign">
                  {l10n.getString('onboarding-enter_vr-back')}
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
              to="/onboarding/mounting/auto"
            >
              {l10n.getString('onboarding-manual_mounting-auto_mounting')}
            </Button>
            {!state.alonePage && (
              <Button variant="primary" to="/onboarding/reset-tutorial">
                {l10n.getString('onboarding-manual_mounting-next')}
              </Button>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
