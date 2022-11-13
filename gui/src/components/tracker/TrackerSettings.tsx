import { IPv4 } from 'ip-num/IPNumber';
import { Quaternion } from 'math3d';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useParams } from 'react-router-dom';
import { AssignTrackerRequestT, BodyPart, RpcMessage } from 'solarxr-protocol';
import { useDebouncedEffect } from '../../hooks/timeout';
import { useTrackerFromId } from '../../hooks/tracker';
import { useWebsocketAPI } from '../../hooks/websocket-api';
import {
  FixEuler,
  QuaternionFromQuatT,
  QuaternionToQuatT,
} from '../../maths/quaternion';
import { ArrowLink } from '../commons/ArrowLink';
import { Button } from '../commons/Button';
import { FootIcon } from '../commons/icon/FootIcon';
import { Input } from '../commons/Input';
import { Typography } from '../commons/Typography';
import { MountingSelectionMenu } from '../onboarding/pages/mounting/MountingSelectionMenu';
import { SingleTrackerBodyAssignmentMenu } from './SingleTrackerBodyAssignmentMenu';
import { TrackerCard } from './TrackerCard';

const rotationToQuatMap = {
  FRONT: 180,
  LEFT: 90,
  RIGHT: -90,
  BACK: 0,
};

const rotationsLabels = {
  [rotationToQuatMap.BACK]: 'Back',
  [rotationToQuatMap.FRONT]: 'Front',
  [rotationToQuatMap.LEFT]: 'Left',
  [rotationToQuatMap.RIGHT]: 'Right',
};

export function TrackerSettingsPage() {
  const { sendRPCPacket } = useWebsocketAPI();
  const [firstLoad, setFirstLoad] = useState(false);
  const [selectRotation, setSelectRotation] = useState<boolean>(false);
  const [selectBodypart, setSelectBodypart] = useState<boolean>(false);
  const { trackernum, deviceid } = useParams<{
    trackernum: string;
    deviceid: string;
  }>();
  const { register, watch, reset, handleSubmit } = useForm<{
    trackerName: string | null;
  }>({
    defaultValues: { trackerName: null },
    reValidateMode: 'onSubmit',
  });
  const { trackerName } = watch();

  const tracker = useTrackerFromId(trackernum, deviceid);

  const onDirectionSelected = (mountingOrientation: number) => {
    if (!tracker) return;

    const assignreq = new AssignTrackerRequestT();
    assignreq.mountingRotation = QuaternionToQuatT(
      Quaternion.Euler(0, +mountingOrientation, 0)
    );
    assignreq.bodyPosition = tracker?.tracker.info?.bodyPart || BodyPart.NONE;
    assignreq.trackerId = tracker?.tracker.trackerId;
    sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
    setSelectRotation(false);
  };

  const onRoleSelected = (role: BodyPart) => {
    if (!tracker) return;

    const assignreq = new AssignTrackerRequestT();
    assignreq.bodyPosition = role;
    assignreq.trackerId = tracker?.tracker.trackerId;
    sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
    setSelectBodypart(false);
  };

  const currRotation = useMemo(
    () =>
      tracker?.tracker.info?.mountingOrientation
        ? FixEuler(
            QuaternionFromQuatT(tracker.tracker.info?.mountingOrientation)
              .eulerAngles.y
          )
        : rotationToQuatMap.BACK,
    [tracker?.tracker.info?.mountingOrientation]
  );

  const updateTrackerName = () => {
    if (!tracker) return;
    if (trackerName == tracker.tracker.info?.customName) return;
    const assignreq = new AssignTrackerRequestT();
    assignreq.bodyPosition = tracker?.tracker.info?.bodyPart || BodyPart.NONE;
    assignreq.displayName = trackerName;
    assignreq.trackerId = tracker?.tracker.trackerId;
    sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
  };

  const onSettingsSubmit = () => {
    updateTrackerName();
  };

  useDebouncedEffect(
    () => {
      updateTrackerName();
    },
    [trackerName],
    1000
  );

  useEffect(() => {
    if (tracker && !firstLoad) setFirstLoad(true);
  }, [tracker, firstLoad]);

  useEffect(() => {
    if (firstLoad) {
      reset({
        trackerName: tracker?.tracker.info?.customName as string | null,
      });
    }
  }, [firstLoad]);

  return (
    <form
      className="h-full overflow-y-auto"
      onSubmit={handleSubmit(onSettingsSubmit)}
    >
      <SingleTrackerBodyAssignmentMenu
        isOpen={selectBodypart}
        onClose={() => setSelectBodypart(false)}
        onRoleSelected={onRoleSelected}
      ></SingleTrackerBodyAssignmentMenu>
      <MountingSelectionMenu
        isOpen={selectRotation}
        onClose={() => setSelectRotation(false)}
        onDirectionSelected={onDirectionSelected}
      ></MountingSelectionMenu>
      <div className="flex gap-2 md:h-full flex-wrap md:flex-row ">
        <div className="flex flex-col w-full md:max-w-xs gap-2">
          {tracker && (
            <TrackerCard
              bg={'bg-background-70'}
              device={tracker?.device}
              tracker={tracker?.tracker}
              shakeHighlight={false}
            ></TrackerCard>
          )}
          {/* <div className="flex flex-col bg-background-70 p-3 rounded-lg gap-2">
            <Typography bold>Firmware version</Typography>
            <div className="flex gap-2">
              <Typography color="secondary">
                {tracker?.device?.hardwareInfo?.firmwareVersion}
              </Typography>
              <Typography color="secondary">-</Typography>
              <Typography color="text-accent-background-10">
                Up to date
              </Typography>
            </div>
            <Button variant="primary" disabled>
              Update now
            </Button>
          </div> */}
          <div className="flex flex-col bg-background-70 p-3 rounded-lg gap-2">
            <div className="flex justify-between">
              <Typography color="secondary">Manufacturer</Typography>
              <Typography>
                {tracker?.device?.hardwareInfo?.manufacturer}
              </Typography>
            </div>
            <div className="flex justify-between">
              <Typography color="secondary">Display name</Typography>
              <Typography>{tracker?.tracker.info?.displayName}</Typography>
            </div>
            <div className="flex justify-between">
              <Typography color="secondary">Custom name</Typography>
              <Typography>
                {tracker?.tracker.info?.customName || '--'}
              </Typography>
            </div>
            <div className="flex justify-between">
              <Typography color="secondary">Tracker URL</Typography>
              <Typography>
                udp://
                {IPv4.fromNumber(
                  tracker?.device?.hardwareInfo?.ipAddress?.addr || 0
                ).toString()}
              </Typography>
            </div>
          </div>
        </div>
        <div className="flex flex-col flex-grow  bg-background-70 rounded-lg p-5 gap-3">
          <ArrowLink to="/">Go back to trackers list</ArrowLink>
          <Typography variant="main-title">Tracker settings</Typography>
          <div className="flex flex-col gap-2 w-full mt-3">
            <Typography variant="section-title">Assignment</Typography>
            <Typography color="secondary">
              What part of the body the tracker is assigned to.
            </Typography>
            <div className="flex justify-between bg-background-80 w-full p-3 rounded-lg">
              <div className="flex gap-3 items-center">
                <FootIcon></FootIcon>
                <Typography>
                  {BodyPart[tracker?.tracker.info?.bodyPart || BodyPart.NONE]}
                </Typography>
              </div>
              <div className="flex">
                <Button
                  variant="secondary"
                  onClick={() => setSelectBodypart(true)}
                >
                  Edit assignment
                </Button>
              </div>
            </div>
          </div>
          <div className="flex flex-col gap-2 w-full mt-3">
            <Typography variant="section-title">Mounting position</Typography>
            <Typography color="secondary">
              Where is the tracker mounted?
            </Typography>
            <div className="flex justify-between bg-background-80 w-full p-3 rounded-lg">
              <div className="flex gap-3 items-center">
                <FootIcon></FootIcon>
                <Typography>{rotationsLabels[currRotation]}</Typography>
              </div>
              <div className="flex">
                <Button
                  variant="secondary"
                  onClick={() => setSelectRotation(true)}
                >
                  Edit mounting
                </Button>
              </div>
            </div>
          </div>
          <div className="flex flex-col gap-2 w-full mt-3">
            <Typography variant="section-title">Tracker name</Typography>
            <Typography color="secondary">
              {'Give it a cute nickname :)'}
            </Typography>
            <Input
              placeholder="NightyBeast's left leg"
              type="text"
              autocomplete={false}
              {...register('trackerName')}
            ></Input>
          </div>
        </div>
      </div>
    </form>
  );
}
