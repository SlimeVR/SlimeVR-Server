import { ReactNode, useCallback, useState } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { useOnboarding } from '@/hooks/onboarding';
import {
  MountingOrientationDegreesToQuatT,
  QuaternionFromQuatT,
} from '@/maths/quaternion';
import { useAssignTracker } from '@/hooks/tracker-assignment';
import { Button } from '@/components/commons/Button';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { BodyAssignment } from '@/components/onboarding/BodyAssignment';
import { MountingSelectionMenu } from './MountingSelectionMenu';
import { Localized } from '@fluent/react';
import { Quaternion } from 'three';
import { defaultConfig, useConfig } from '@/hooks/config';
import { trackerByBodyPartAtom } from '@/store/app-store';
import { useAtomValue } from 'jotai';
import * as Sentry from '@sentry/react';

export function ManualMountingPage() {
  const { applyProgress, state } = useOnboarding();
  const assignTracker = useAssignTracker();
  const { config } = useConfig();

  const [selectedRole, setSelectRole] = useState<BodyPart>(BodyPart.NONE);

  applyProgress(0.6);

  const trackerByPart = useAtomValue(trackerByBodyPartAtom);

  const onDirectionSelected = (mountingOrientationDegrees: Quaternion) => {
    const td = trackerByPart[selectedRole];
    if (td) {
      const bodyPart = td.tracker.info?.bodyPart || BodyPart.NONE;
      const mountingOrientation = MountingOrientationDegreesToQuatT(
        mountingOrientationDegrees
      );

      assignTracker(td.tracker.trackerId, bodyPart, mountingOrientation);
      Sentry.metrics.count('manual_mounting_set', 1, {
        attributes: {
          part: BodyPart[bodyPart],
          direction: mountingOrientation,
        },
      });
    }

    setSelectRole(BodyPart.NONE);
  };

  const getCurrRotation = useCallback(
    (role: BodyPart) => {
      if (role === BodyPart.NONE) return undefined;

      const mountingOrientation =
        trackerByPart[role]?.tracker.info?.mountingOrientation;
      return mountingOrientation
        ? QuaternionFromQuatT(mountingOrientation)
        : undefined;
    },
    [trackerByPart]
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
              mirror={config?.mirrorView ?? defaultConfig.mirrorView}
              onlyAssigned={true}
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
  const assignTracker = useAssignTracker();
  const { config } = useConfig();

  const [selectedRole, setSelectRole] = useState<BodyPart>(BodyPart.NONE);

  const trackerByPart = useAtomValue(trackerByBodyPartAtom);

  const onDirectionSelected = (mountingOrientationDegrees: Quaternion) => {
    const td = trackerByPart[selectedRole];
    if (td) {
      const bodyPart = td.tracker.info?.bodyPart || BodyPart.NONE;
      const mountingOrientation = MountingOrientationDegreesToQuatT(
        mountingOrientationDegrees
      );

      assignTracker(td.tracker.trackerId, bodyPart, mountingOrientation);
      Sentry.metrics.count('manual_mounting_set', 1, {
        attributes: {
          part: BodyPart[bodyPart],
          direction: mountingOrientation,
        },
      });
    }

    setSelectRole(BodyPart.NONE);
  };

  const getCurrRotation = useCallback(
    (role: BodyPart) => {
      if (role === BodyPart.NONE) return undefined;

      const mountingOrientation =
        trackerByPart[role]?.tracker.info?.mountingOrientation;
      return mountingOrientation
        ? QuaternionFromQuatT(mountingOrientation)
        : undefined;
    },
    [trackerByPart]
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
              mirror={config?.mirrorView ?? defaultConfig.mirrorView}
              onlyAssigned={true}
              onRoleSelected={setSelectRole}
            />
          </div>
        </div>
      </div>
    </>
  );
}
