import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import {
  AssignTrackerRequestT,
  BodyPart,
  QuatT,
  RpcMessage,
  TrackerIdT,
} from 'solarxr-protocol';
import { FlatDeviceTracker } from '../../../../hooks/app';
import { useChockerWarning } from '../../../../hooks/chocker-warning';
import { useOnboarding } from '../../../../hooks/onboarding';
import { useTrackers } from '../../../../hooks/tracker';
import { useWebsocketAPI } from '../../../../hooks/websocket-api';
import { ArrowLink } from '../../../commons/ArrowLink';
import { Button } from '../../../commons/Button';
import { CheckBox } from '../../../commons/Checkbox';
import { TipBox } from '../../../commons/TipBox';
import { Typography } from '../../../commons/Typography';
import { ASSIGNMENT_RULES, BodyAssignment } from '../../BodyAssignment';
import { NeckWarningModal } from '../../NeckWarningModal';
import { TrackerSelectionMenu } from './TrackerSelectionMenu';

export type BodyPartError = {
  label: string | undefined;
  affected_roles: BodyPart[];
};

export function TrackersAssignPage() {
  const { l10n } = useLocalization();
  const { useAssignedTrackers, trackers } = useTrackers();
  const { applyProgress, skipSetup, state } = useOnboarding();
  const { sendRPCPacket } = useWebsocketAPI();

  const { control, watch } = useForm<{ advanced: boolean }>({
    defaultValues: { advanced: false },
  });
  const { advanced } = watch();
  const [selectedRole, setSelectRole] = useState<BodyPart>(BodyPart.NONE);
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

  const rolesWithErrors = useMemo(() => {
    const trackerRoles = trackers.map(
      ({ tracker }) => tracker.info?.bodyPart || BodyPart.NONE
    );

    const message = (assignedRole: BodyPart) => {
      const unassignedRoles: [BodyPart | BodyPart[], boolean][] = (
        ASSIGNMENT_RULES[assignedRole] || []
      ).map((part) => [
        part,
        Array.isArray(part)
          ? trackerRoles.some((tr) => part.includes(tr))
          : trackerRoles.includes(part),
      ]);

      if (unassignedRoles.length === 0) return;

      return {
        affected_roles: unassignedRoles.flatMap(([part]) => part),
        label: l10n.getString(
          `onboarding-assign_trackers-warning-${BodyPart[assignedRole]}`,
          {
            unassigned: unassignedRoles
              .map(([, state]) => state)
              .reduce((acc, cur, i) => acc + (Number(cur) << i), 0),
          }
        ),
      };
    };

    return Object.keys(BodyPart)
      .filter((key) => typeof key === 'number' && !Number.isNaN(key))
      .map<BodyPart>((key) => +key)
      .reduce<Record<BodyPart, BodyPartError>>((curr, role) => {
        return {
          ...curr,
          [role]: trackerRoles.find((tr) => tr === role)
            ? message(role)
            : undefined,
        };
      }, {} as any);
  }, [trackers]);

  const onTrackerSelected = (tracker: FlatDeviceTracker | null) => {
    const assign = (
      role: BodyPart,
      rotation: QuatT | null,
      trackerId: TrackerIdT | null
    ) => {
      const assignreq = new AssignTrackerRequestT();

      assignreq.bodyPosition = role;
      assignreq.mountingOrientation = rotation;
      assignreq.trackerId = trackerId;
      sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
    };

    (trackerPartGrouped[selectedRole] || []).forEach((td) =>
      assign(
        BodyPart.NONE,
        td.tracker.info?.mountingOrientation || null,
        td.tracker.trackerId
      )
    );

    if (!tracker) {
      setSelectRole(BodyPart.NONE);
      return;
    }

    assign(
      selectedRole,
      tracker.tracker.info?.mountingOrientation || null,
      tracker.tracker.trackerId
    );
    setSelectRole(BodyPart.NONE);
  };

  applyProgress(0.5);

  const { closeChockerWarning, tryOpenChockerWarning, shouldShowChockerWarn } =
    useChockerWarning({
      next: setSelectRole,
    });

  const firstError = Object.values(rolesWithErrors).find((r) => !!r);

  return (
    <>
      <TrackerSelectionMenu
        bodyPart={selectedRole}
        isOpen={selectedRole !== BodyPart.NONE}
        onClose={() => setSelectRole(BodyPart.NONE)}
        onTrackerSelected={onTrackerSelected}
      ></TrackerSelectionMenu>
      <NeckWarningModal
        isOpen={shouldShowChockerWarn}
        overlayClassName={classNames(
          'fixed top-0 right-0 left-0 bottom-0 flex flex-col items-center w-full h-full justify-center bg-black bg-opacity-90 z-20'
        )}
        onClose={() => closeChockerWarning(true)}
        accept={() => closeChockerWarning(false)}
      ></NeckWarningModal>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
        <div className="flex flex-col w-full h-full justify-center items-center">
          <div className="flex md:gap-8">
            <div className="flex flex-col max-w-sm gap-3">
              {!state.alonePage && (
                <ArrowLink to="/onboarding/wifi-creds" direction="left">
                  {l10n.getString('onboarding-assign_trackers-back')}
                </ArrowLink>
              )}
              <Typography variant="main-title">
                {l10n.getString('onboarding-assign_trackers-title')}
              </Typography>
              <Typography color="secondary">
                {l10n.getString('onboarding-assign_trackers-description')}
              </Typography>
              <div className="flex gap-1">
                <Typography color="secondary">
                  {l10n.getString('onboarding-assign_trackers-assigned', {
                    assigned: assignedTrackers.length,
                    trackers: trackers.length,
                  })}
                </Typography>
              </div>
              <TipBox>{l10n.getString('tips-find_tracker')}</TipBox>
              <CheckBox
                control={control}
                label={l10n.getString('onboarding-assign_trackers-advanced')}
                name="advanced"
                variant="toggle"
              ></CheckBox>
              {!!firstError && (
                <div className="bg-status-warning text-background-60 px-3 py-2 text-justify rounded-md">
                  <div className="flex flex-col gap-1 whitespace-normal">
                    <span>{firstError.label}</span>
                  </div>
                </div>
              )}
            </div>
            <div className="flex flex-col flex-grow gap-3 rounded-xl fill-background-50">
              <BodyAssignment
                onlyAssigned={false}
                highlightedRoles={firstError?.affected_roles || []}
                rolesWithErrors={rolesWithErrors}
                advanced={advanced}
                onRoleSelected={tryOpenChockerWarning}
              ></BodyAssignment>
            </div>
          </div>
        </div>
        <div className="w-full pb-4 flex flex-row">
          <div className="flex flex-grow">
            {!state.alonePage && (
              <Button variant="secondary" to="/" onClick={skipSetup}>
                {l10n.getString('onboarding-skip')}
              </Button>
            )}
          </div>
          <div className="flex gap-3">
            {!state.alonePage && (
              <Button variant="primary" to="/onboarding/enter-vr">
                {l10n.getString('onboarding-assign_trackers-next')}
              </Button>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
