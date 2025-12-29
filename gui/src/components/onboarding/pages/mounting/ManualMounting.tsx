import { ReactNode, useCallback, useMemo, useState } from 'react';
import { AssignTrackerRequestT, BodyPart, RpcMessage } from 'solarxr-protocol';
import { useOnboarding } from '@/hooks/onboarding';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import {
  MountingOrientationDegreesToQuatT,
  QuaternionFromQuatT,
  similarQuaternions,
} from '@/maths/quaternion';
import { Button } from '@/components/commons/Button';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { BodyAssignment } from '@/components/onboarding/BodyAssignment';
import { MountingSelectionMenu } from './MountingSelectionMenu';
import { Localized } from '@fluent/react';
import { useBreakpoint } from '@/hooks/breakpoint';
import { Quaternion } from 'three';
import { AssignMode, defaultConfig, useConfig } from '@/hooks/config';
import { assignedTrackersAtom, FlatDeviceTracker } from '@/store/app-store';
import { useAtomValue } from 'jotai';
import * as Sentry from '@sentry/react';

export function ManualMountingPage() {
  const { isMobile } = useBreakpoint('mobile');
  const { applyProgress, state } = useOnboarding();
  const { sendRPCPacket } = useWebsocketAPI();
  const { config } = useConfig();

  const [selectedRole, setSelectRole] = useState<BodyPart>(BodyPart.NONE);

  applyProgress(0.7);

  const assignedTrackers = useAtomValue(assignedTrackersAtom);

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

  const onDirectionSelected = (mountingOrientationDegrees: Quaternion) => {
    (trackerPartGrouped[selectedRole] || []).forEach((td) => {
      const assignreq = new AssignTrackerRequestT();

      assignreq.bodyPosition = td.tracker.info?.bodyPart || BodyPart.NONE;
      assignreq.mountingOrientation = MountingOrientationDegreesToQuatT(
        mountingOrientationDegrees
      );
      assignreq.trackerId = td.tracker.trackerId;
      assignreq.allowDriftCompensation = false;

      sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
      Sentry.metrics.count('manual_mounting_set', 1, {
        attributes: {
          part: BodyPart[assignreq.bodyPosition],
          direction: assignreq.mountingOrientation,
        },
      });
    });

    setSelectRole(BodyPart.NONE);
  };

  const getCurrRotation = useCallback(
    (role: BodyPart) => {
      if (role === BodyPart.NONE) return undefined;

      const trackers = trackerPartGrouped[role] || [];
      const [mountingOrientation, ...orientation] = trackers
        .map((td) => td.tracker.info?.mountingOrientation)
        .filter((orientation) => !!orientation)
        .map((orientation) => QuaternionFromQuatT(orientation));

      const identicalOrientations =
        mountingOrientation !== undefined &&
        orientation.every((quat) =>
          similarQuaternions(quat, mountingOrientation)
        );
      return identicalOrientations ? mountingOrientation : undefined;
    },
    [trackerPartGrouped]
  );

  return (
    <>
      <MountingSelectionMenu
        bodyPart={selectedRole}
        currRotation={getCurrRotation(selectedRole)}
        isOpen={selectedRole !== BodyPart.NONE}
        onClose={() => setSelectRole(BodyPart.NONE)}
        onDirectionSelected={onDirectionSelected}
      />
      <div className="flex flex-col gap-5 h-full items-center w-full xs:justify-center relative overflow-y-auto">
        <div className="flex xs:flex-row mobile:flex-col h-full px-8 xs:w-full xs:justify-center mobile:px-4 items-center">
          <div className="flex flex-col w-full xs:max-w-sm gap-3">
            <Typography variant="main-title" id="onboarding-manual_mounting" />
            <Typography id="onboarding-manual_mounting-description" />
            <Typography id="tips-find_tracker" />
            <Localized id="tips-find_tracker">
              <TipBox />
            </Localized>

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
          </div>
          <div className="flex flex-row justify-center">
            <BodyAssignment
              width={isMobile ? 160 : undefined}
              mirror={config?.mirrorView ?? defaultConfig.mirrorView}
              onlyAssigned={true}
              assignMode={AssignMode.All}
              onRoleSelected={setSelectRole}
            />
          </div>
        </div>
      </div>
    </>
  );
}

export function ManualMountingPageStayAligned({
  children,
}: {
  children: ReactNode;
}) {
  const { isMobile } = useBreakpoint('mobile');
  const { sendRPCPacket } = useWebsocketAPI();
  const { config } = useConfig();

  const [selectedRole, setSelectRole] = useState<BodyPart>(BodyPart.NONE);

  const assignedTrackers = useAtomValue(assignedTrackersAtom);

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

  const onDirectionSelected = (mountingOrientationDegrees: Quaternion) => {
    (trackerPartGrouped[selectedRole] || []).forEach((td) => {
      const assignreq = new AssignTrackerRequestT();

      assignreq.bodyPosition = td.tracker.info?.bodyPart || BodyPart.NONE;
      assignreq.mountingOrientation = MountingOrientationDegreesToQuatT(
        mountingOrientationDegrees
      );
      assignreq.trackerId = td.tracker.trackerId;
      assignreq.allowDriftCompensation = false;

      sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
      Sentry.metrics.count('manual_mounting_set', 1, {
        attributes: {
          part: BodyPart[assignreq.bodyPosition],
          direction: assignreq.mountingOrientation,
        },
      });
    });

    setSelectRole(BodyPart.NONE);
  };

  const getCurrRotation = useCallback(
    (role: BodyPart) => {
      if (role === BodyPart.NONE) return undefined;

      const trackers = trackerPartGrouped[role] || [];
      const [mountingOrientation, ...orientation] = trackers
        .map((td) => td.tracker.info?.mountingOrientation)
        .filter((orientation) => !!orientation)
        .map((orientation) => QuaternionFromQuatT(orientation));

      const identicalOrientations =
        mountingOrientation !== undefined &&
        orientation.every((quat) =>
          similarQuaternions(quat, mountingOrientation)
        );
      return identicalOrientations ? mountingOrientation : undefined;
    },
    [trackerPartGrouped]
  );

  return (
    <>
      <MountingSelectionMenu
        bodyPart={selectedRole}
        currRotation={getCurrRotation(selectedRole)}
        isOpen={selectedRole !== BodyPart.NONE}
        onClose={() => setSelectRole(BodyPart.NONE)}
        onDirectionSelected={onDirectionSelected}
      />
      <div className="flex flex-col gap-5 h-full items-center w-full xs:justify-center relative overflow-y-auto">
        <div className="flex xs:flex-row mobile:flex-col h-full px-8 xs:w-full xs:justify-center mobile:px-4 items-center">
          <div className="flex flex-col w-full xs:max-w-sm gap-3">
            <Typography variant="main-title" id="onboarding-manual_mounting" />
            <Typography id="onboarding-manual_mounting-description" />
            <Typography id="tips-find_tracker" />
            <Localized id="tips-find_tracker">
              <TipBox />
            </Localized>
            {children}
          </div>
          <div className="flex flex-row justify-center">
            <BodyAssignment
              width={isMobile ? 160 : undefined}
              mirror={config?.mirrorView ?? defaultConfig.mirrorView}
              onlyAssigned={true}
              assignMode={AssignMode.All}
              onRoleSelected={setSelectRole}
            />
          </div>
        </div>
      </div>
    </>
  );
}
