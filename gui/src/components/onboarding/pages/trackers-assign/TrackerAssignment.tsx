import { useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import {
  AssignTrackerRequestT,
  BodyPart,
  QuatT,
  RpcMessage,
  TrackerIdT,
} from 'solarxr-protocol';
import { FlatDeviceTracker } from '../../../../hooks/app';
import { useOnboarding } from '../../../../hooks/onboarding';
import { useTrackers } from '../../../../hooks/tracker';
import { useWebsocketAPI } from '../../../../hooks/websocket-api';
import { ArrowLink } from '../../../commons/ArrowLink';
import { Button } from '../../../commons/Button';
import { CheckBox } from '../../../commons/Checkbox';
import { TipBox } from '../../../commons/TipBox';
import { Typography } from '../../../commons/Typography';
import { BodyAssignment } from '../../BodyAssignment';
import { TrackerSelectionMenu } from './TrackerSelectionMenu';

export function TrackersAssignPage() {
  const { t } = useTranslation();
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

  const onTrackerSelected = (tracker: FlatDeviceTracker | null) => {
    const assign = (
      role: BodyPart,
      rotation: QuatT | null,
      trackerId: TrackerIdT | null
    ) => {
      const assignreq = new AssignTrackerRequestT();

      assignreq.bodyPosition = role;
      assignreq.mountingRotation = rotation;
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

  return (
    <>
      <TrackerSelectionMenu
        bodyPart={selectedRole}
        isOpen={selectedRole !== BodyPart.NONE}
        onClose={() => setSelectRole(BodyPart.NONE)}
        onTrackerSelected={onTrackerSelected}
      ></TrackerSelectionMenu>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
        <div className="flex flex-col w-full h-full justify-center items-center">
          <div className="flex md:gap-8">
            <div className="flex flex-col max-w-sm gap-3">
              {!state.alonePage && (
                <ArrowLink to="/onboarding/wifi-creds" direction="left">
                  {t('onboarding-assign_trackers-back')}
                </ArrowLink>
              )}
              <Typography variant="main-title">
                {t('onboarding-assign_trackers-title')}
              </Typography>
              <Typography color="secondary">
                {t('onboarding-assign_trackers-description')}
              </Typography>
              <div className="flex gap-1">
                <Typography color="secondary">
                  {t('onboarding-assign_trackers-assigned', {
                    assigned: assignedTrackers.length,
                    trackers: trackers.length,
                  })}
                </Typography>
              </div>
              <TipBox>{t('tips-find_tracker')}</TipBox>
              <CheckBox
                control={control}
                label={t('onboarding-assign_trackers-advanced')}
                name="advanced"
                variant="toggle"
              ></CheckBox>
            </div>
            <div className="flex flex-col flex-grow gap-3 rounded-xl fill-background-50">
              <BodyAssignment
                onlyAssigned={false}
                advanced={advanced}
                onRoleSelected={setSelectRole}
              ></BodyAssignment>
            </div>
          </div>
        </div>
        <div className="w-full pb-4 flex flex-row">
          <div className="flex flex-grow">
            {!state.alonePage && (
              <Button variant="secondary" to="/" onClick={skipSetup}>
                {t('onboarding-skip')}
              </Button>
            )}
          </div>
          <div className="flex gap-3">
            {!state.alonePage && (
              <Button variant="primary" to="/onboarding/enter-vr">
                {t('onboarding-assign_trackers-next')}
              </Button>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
