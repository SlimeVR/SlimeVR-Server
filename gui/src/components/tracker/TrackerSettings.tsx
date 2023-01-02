import { IPv4 } from 'ip-num/IPNumber';
import Quaternion from 'quaternion';
import { useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { useParams } from 'react-router-dom';
import { AssignTrackerRequestT, BodyPart, RpcMessage } from 'solarxr-protocol';
import { useDebouncedEffect } from '../../hooks/timeout';
import { useTrackerFromId } from '../../hooks/tracker';
import { useWebsocketAPI } from '../../hooks/websocket-api';
import { DEG_TO_RAD, RAD_TO_DEG } from '../../maths/angle';
import { FixEuler, GetYaw, QuaternionToQuatT } from '../../maths/quaternion';
import { ArrowLink } from '../commons/ArrowLink';
import { Button } from '../commons/Button';
import { FootIcon } from '../commons/icon/FootIcon';
import { Input } from '../commons/Input';
import { Typography } from '../commons/Typography';
import { MountingSelectionMenu } from '../onboarding/pages/mounting/MountingSelectionMenu';
import { SingleTrackerBodyAssignmentMenu } from './SingleTrackerBodyAssignmentMenu';
import { TrackerCard } from './TrackerCard';
import { CheckBox } from '../commons/Checkbox';

export const rotationToQuatMap = {
  FRONT: 180,
  LEFT: 90,
  RIGHT: -90,
  BACK: 0,
};

const rotationsLabels = {
  [rotationToQuatMap.BACK]: 'tracker-rotation-back',
  [rotationToQuatMap.FRONT]: 'tracker-rotation-front',
  [rotationToQuatMap.LEFT]: 'tracker-rotation-left',
  [rotationToQuatMap.RIGHT]: 'tracker-rotation-right',
};

export function TrackerSettingsPage() {
  const { t } = useTranslation();

  const { sendRPCPacket } = useWebsocketAPI();
  const [firstLoad, setFirstLoad] = useState(false);
  const [selectRotation, setSelectRotation] = useState<boolean>(false);
  const [selectBodypart, setSelectBodypart] = useState<boolean>(false);
  const { trackernum, deviceid } = useParams<{
    trackernum: string;
    deviceid: string;
  }>();
  const { control, register, watch, reset, handleSubmit } = useForm<{
    trackerName: string | null;
    allowDriftCompensation: boolean | null;
  }>({
    defaultValues: {
      trackerName: null,
      allowDriftCompensation: null,
    },
    reValidateMode: 'onSubmit',
  });
  const { trackerName, allowDriftCompensation } = watch();

  const tracker = useTrackerFromId(trackernum, deviceid);

  const onDirectionSelected = (mountingOrientation: number) => {
    if (!tracker) return;

    const assignreq = new AssignTrackerRequestT();

    assignreq.mountingOrientation = QuaternionToQuatT(
      Quaternion.fromEuler(
        0,
        0,
        FixEuler(+mountingOrientation) * DEG_TO_RAD,
        'XZY'
      )
    );
    assignreq.bodyPosition = tracker?.tracker.info?.bodyPart || BodyPart.NONE;
    assignreq.trackerId = tracker?.tracker.trackerId;
    if (allowDriftCompensation != null)
      assignreq.allowDriftCompensation = allowDriftCompensation;
    sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
    setSelectRotation(false);
  };

  const onRoleSelected = (role: BodyPart) => {
    if (!tracker) return;

    const assignreq = new AssignTrackerRequestT();
    assignreq.bodyPosition = role;
    assignreq.trackerId = tracker?.tracker.trackerId;
    if (allowDriftCompensation != null)
      assignreq.allowDriftCompensation = allowDriftCompensation;
    sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
    setSelectBodypart(false);
  };

  const currRotation = useMemo(() => {
    return tracker?.tracker.info?.mountingOrientation
      ? FixEuler(GetYaw(tracker.tracker.info?.mountingOrientation) * RAD_TO_DEG)
      : rotationToQuatMap.FRONT;
  }, [tracker?.tracker.info?.mountingOrientation]);

  const updateTrackerSettings = () => {
    if (!tracker) return;
    if (allowDriftCompensation == null) return;
    if (
      trackerName == tracker.tracker.info?.customName &&
      allowDriftCompensation == tracker.tracker.info?.allowDriftCompensation
    )
      return;
    const assignreq = new AssignTrackerRequestT();
    assignreq.bodyPosition = tracker?.tracker.info?.bodyPart || BodyPart.NONE;
    assignreq.mountingOrientation = assignreq.mountingOrientation =
      QuaternionToQuatT(
        Quaternion.fromEuler(0, 0, FixEuler(+currRotation) * DEG_TO_RAD, 'XZY')
      );
    assignreq.displayName = trackerName;
    assignreq.trackerId = tracker?.tracker.trackerId;
    assignreq.allowDriftCompensation = allowDriftCompensation;
    sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
  };

  const onSettingsSubmit = () => {
    updateTrackerSettings();
  };

  useDebouncedEffect(
    () => {
      updateTrackerSettings();
    },
    [trackerName],
    1000
  );

  useEffect(() => {
    updateTrackerSettings();
  }, [allowDriftCompensation]);

  useEffect(() => {
    if (tracker && !firstLoad) setFirstLoad(true);
  }, [tracker, firstLoad]);

  useEffect(() => {
    if (firstLoad) {
      reset({
        trackerName: tracker?.tracker.info?.customName as string | null,
        allowDriftCompensation: tracker?.tracker.info?.allowDriftCompensation,
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
              <Typography color="secondary">
                {t('tracker-infos-manufacturer')}
              </Typography>
              <Typography>
                {tracker?.device?.hardwareInfo?.manufacturer}
              </Typography>
            </div>
            <div className="flex justify-between">
              <Typography color="secondary">
                {t('tracker-infos-display_name')}
              </Typography>
              <Typography>{tracker?.tracker.info?.displayName}</Typography>
            </div>
            <div className="flex justify-between">
              <Typography color="secondary">
                {t('tracker-infos-custom_name')}
              </Typography>
              <Typography>
                {tracker?.tracker.info?.customName || '--'}
              </Typography>
            </div>
            <div className="flex justify-between">
              <Typography color="secondary">
                {t('tracker-infos-url')}
              </Typography>
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
          <ArrowLink to="/">{t('tracker-settings-back')}</ArrowLink>
          <Typography variant="main-title">
            {t('tracker-settings-title')}
          </Typography>
          <div className="flex flex-col gap-2 w-full mt-3">
            <Typography variant="section-title">
              {t('tracker-settings-assignment_section-title')}
            </Typography>
            <Typography color="secondary">
              {t('tracker-settings-assignment_section-description')}
            </Typography>
            <div className="flex justify-between bg-background-80 w-full p-3 rounded-lg">
              <div className="flex gap-3 items-center">
                <FootIcon></FootIcon>
                <Typography>
                  {t(
                    'body_part-' +
                      BodyPart[tracker?.tracker.info?.bodyPart || BodyPart.NONE]
                  )}
                </Typography>
              </div>
              <div className="flex">
                <Button
                  variant="secondary"
                  onClick={() => setSelectBodypart(true)}
                >
                  {t('tracker-settings-assignment_section-edit')}
                </Button>
              </div>
            </div>
          </div>
          <div className="flex flex-col gap-2 w-full mt-3">
            <Typography variant="section-title">
              {t('tracker-settings-mounting_section-title')}
            </Typography>
            <Typography color="secondary">
              {t('tracker-settings-mounting_section-description')}
            </Typography>
            <div className="flex justify-between bg-background-80 w-full p-3 rounded-lg">
              <div className="flex gap-3 items-center">
                <FootIcon></FootIcon>
                <Typography>{t(rotationsLabels[currRotation])}</Typography>
              </div>
              <div className="flex">
                <Button
                  variant="secondary"
                  onClick={() => setSelectRotation(true)}
                >
                  {t('tracker-settings-mounting_section-edit')}
                </Button>
              </div>
            </div>
          </div>
          {tracker?.tracker.info?.isImu && (
            <>
              <div className="flex flex-col gap-2 w-full mt-3">
                <Typography variant="section-title">
                  {t('tracker-settings-drift_compensation_section-title')}
                </Typography>
                <Typography color="secondary">
                  {t('tracker-settings-drift_compensation_section-description')}
                </Typography>
                <div className="flex">
                  <CheckBox
                    variant="toggle"
                    outlined
                    name="allowDriftCompensation"
                    control={control}
                    label={t(
                      'tracker-settings-drift_compensation_section-edit'
                    )}
                  />
                </div>
              </div>
            </>
          )}
          <div className="flex flex-col gap-2 w-full mt-3">
            <Typography variant="section-title">
              {t('tracker-settings-name_section-title')}
            </Typography>
            <Typography color="secondary">
              {t('tracker-settings-name_section-description')}
            </Typography>
            <Input
              placeholder={t('tracker-settings-name_section-input_placeholder')}
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
