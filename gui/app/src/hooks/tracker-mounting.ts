import { useState } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { Quaternion } from 'three';
import * as Sentry from '@sentry/react';
import {
  MountingOrientationDegreesToQuatT,
  QuaternionFromQuatT,
} from '@/maths/quaternion';
import { FlatDeviceTracker } from '@/store/app-store';
import { useAssignTracker } from './tracker-assignment';
import { usePickerShell } from './tracker-picker';

export function useMountingOrientation(td: FlatDeviceTracker | undefined) {
  const assignTracker = useAssignTracker();

  const mountingOrientation = td?.tracker.info?.mountingOrientation;
  const currRotation = mountingOrientation
    ? QuaternionFromQuatT(mountingOrientation)
    : undefined;

  const setDirection = (mountingOrientationDegrees: Quaternion) => {
    if (!td) return;

    const bodyPart = td.tracker.info?.bodyPart || BodyPart.NONE;
    const orientation = MountingOrientationDegreesToQuatT(mountingOrientationDegrees);

    assignTracker(td.tracker.trackerId, bodyPart, orientation);
    Sentry.metrics.count('manual_mounting_set', 1, {
      attributes: {
        part: BodyPart[bodyPart],
        direction: orientation,
      },
    });
  };

  return { currRotation, setDirection };
}

export function useMountingSelection() {
  const shell = usePickerShell();
  const [target, setTarget] = useState<BodyPart>(BodyPart.NONE);

  const td = shell.trackerByPart[target];
  const { currRotation, setDirection: applyDirection } = useMountingOrientation(td);

  const clearTarget = () => setTarget(BodyPart.NONE);

  return {
    ...shell,
    target,
    activePart: target,
    clearTarget,
    currRotation,
    selectPart: (role: BodyPart) => {
      if (shell.trackerByPart[role]) setTarget(role);
    },
    setDirection: (direction: Quaternion) => {
      applyDirection(direction);
      clearTarget();
    },
  };
}
