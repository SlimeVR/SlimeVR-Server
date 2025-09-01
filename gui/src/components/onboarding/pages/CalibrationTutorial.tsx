import { Localized, useLocalization } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useEffect, useMemo, useRef, useState } from 'react';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { useCountdown } from '@/hooks/countdown';
import classNames from 'classnames';
import { TaybolIcon } from '@/components/commons/icon/TaybolIcon';
import { useRestCalibrationTrackers } from '@/hooks/imu-logic';
import { Vector3FromVec3fT } from '@/maths/vector3';
import { Vector3 } from 'three';
import { useTimeout } from '@/hooks/timeout';
import { useAtomValue } from 'jotai';
import { connectedIMUTrackersAtom } from '@/store/app-store';

export enum CalibrationStatus {
  SUCCESS,
  CALIBRATING,
  WAITING,
  ERROR,
}

export const IMU_CALIBRATION_TIME = 4;
export const IMU_SETTLE_TIME = 1;
const ACCEL_TOLERANCE = 0.5; // m/s^2
const ACCEL_HYSTERESIS = 0.1; // m/s^2

export function CalibrationTutorialPage() {
  const { l10n } = useLocalization();
  const { applyProgress } = useOnboarding();
  const [calibrationStatus, setCalibrationStatus] = useState(
    CalibrationStatus.WAITING
  );
  const [skipButton, setSkipButton] = useState(false);
  const [settled, setSettled] = useState(false);
  const { timer, isCounting, startCountdown, abortCountdown } = useCountdown({
    duration: settled ? IMU_CALIBRATION_TIME : IMU_SETTLE_TIME,
    onCountdownEnd: () =>
      settled
        ? setCalibrationStatus(CalibrationStatus.SUCCESS)
        : setSettled(true),
  });
  useTimeout(() => setSkipButton(true), 10000);
  const connectedIMUTrackers = useAtomValue(connectedIMUTrackersAtom);
  const restCalibrationTrackers =
    useRestCalibrationTrackers(connectedIMUTrackers);
  const [rested, setRested] = useState(false);
  const lastValueMap = useRef(new Map<number, Vector3>());
  useEffect(() => {
    const accelLength = restCalibrationTrackers.every((x) => {
      if (
        x.device?.id?.id === undefined ||
        x.tracker.trackerId?.trackerNum === undefined ||
        !x.tracker.linearAcceleration
      )
        return false;

      const trackerId = x.tracker.trackerId.trackerNum + (x.device.id.id << 8);
      const lastValue = lastValueMap.current.get(trackerId) ?? new Vector3();
      lastValueMap.current.set(trackerId, lastValue);

      const vec3 = Vector3FromVec3fT(x.tracker.linearAcceleration);

      if (vec3.lengthSq() > ACCEL_TOLERANCE ** 2) {
        return false;
      }

      const delta = new Vector3();
      delta.subVectors(lastValue, vec3);

      if (delta.lengthSq() > ACCEL_HYSTERESIS ** 2) {
        lastValue.copy(vec3);
        return false;
      }

      return true;
    });

    if (accelLength && !settled && !isCounting) {
      abortCountdown();
      startCountdown();
    } else if (!accelLength && !settled && isCounting) {
      abortCountdown();
    } else if (!accelLength && settled) {
      setSettled(false);
    }

    setRested(settled || restCalibrationTrackers.length === 0);
  }, [restCalibrationTrackers, settled, isCounting]);

  useEffect(() => {
    if (calibrationStatus === CalibrationStatus.CALIBRATING && !rested) {
      setCalibrationStatus(CalibrationStatus.ERROR);
      abortCountdown();
    }
  }, [calibrationStatus, rested]);

  const progressBarClass = useMemo(() => {
    switch (calibrationStatus) {
      case CalibrationStatus.ERROR:
        return 'bg-status-critical';
      case CalibrationStatus.SUCCESS:
        return 'bg-status-success';
    }
  }, [calibrationStatus]);

  const slimeStatus = useMemo(() => {
    switch (calibrationStatus) {
      case CalibrationStatus.CALIBRATING:
        return SlimeState.JUMPY;
      case CalibrationStatus.ERROR:
        return SlimeState.SAD;
      default:
        return SlimeState.HAPPY;
    }
  }, [calibrationStatus]);

  const progressText = useMemo(() => {
    switch (calibrationStatus) {
      case CalibrationStatus.CALIBRATING:
        return l10n.getString(
          'onboarding-calibration_tutorial-status-calibrating'
        );
      case CalibrationStatus.ERROR:
        return l10n.getString('onboarding-calibration_tutorial-status-error');
      case CalibrationStatus.SUCCESS:
        return l10n.getString('onboarding-calibration_tutorial-status-success');
      case CalibrationStatus.WAITING:
        return l10n.getString('onboarding-calibration_tutorial-status-waiting');
    }
  }, [calibrationStatus, l10n]);

  applyProgress(0.43);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center relative">
        <div className="flex w-full h-full justify-center xs:px-20 mobile:px-5 pb-5 gap-14">
          <div className="flex gap-4 self-center mobile:z-10">
            <div className="flex flex-col max-w-md gap-3">
              <div>
                <Typography variant="mobile-title">
                  {l10n.getString('onboarding-calibration_tutorial')}
                </Typography>
                <Typography variant="vr-accessible" italic>
                  {l10n.getString('onboarding-calibration_tutorial-subtitle')}
                </Typography>
              </div>
              <Localized
                id="onboarding-calibration_tutorial-description-v1"
                elems={{ b: <b></b> }}
              >
                <Typography color="secondary">
                  Description on calibration of IMU
                </Typography>
              </Localized>
              <div>
                <div className="xs:hidden flex flex-row justify-center">
                  <div className="stroke-none fill-background-10 ">
                    <TaybolIcon width="220"></TaybolIcon>
                  </div>
                </div>
                <div className="flex justify-center">
                  <LoaderIcon slimeState={slimeStatus}></LoaderIcon>
                </div>
                <ProgressBar
                  progress={
                    isCounting && settled
                      ? (IMU_CALIBRATION_TIME - timer) / IMU_CALIBRATION_TIME
                      : calibrationStatus === CalibrationStatus.SUCCESS ||
                          calibrationStatus === CalibrationStatus.ERROR
                        ? 1
                        : 0
                  }
                  height={14}
                  animated={true}
                  colorClass={progressBarClass}
                ></ProgressBar>
              </div>
              <div className="flex justify-center">
                <Typography variant="section-title">{progressText}</Typography>
              </div>
              <div className="flex gap-3 mobile:flex-col">
                <Button
                  variant="secondary"
                  to="/onboarding/wifi-creds"
                  className="xs:mr-auto"
                >
                  {l10n.getString('onboarding-previous_step')}
                </Button>
                <Button
                  variant="primary"
                  onClick={() => {
                    setCalibrationStatus(CalibrationStatus.CALIBRATING);
                    startCountdown();
                  }}
                  disabled={isCounting || !rested}
                  className={classNames(
                    'xs:ml-auto',
                    CalibrationStatus.SUCCESS === calibrationStatus && 'hidden'
                  )}
                >
                  {l10n.getString('onboarding-calibration_tutorial-calibrate')}
                </Button>
                <Button
                  variant="primary"
                  to="/onboarding/assign-tutorial"
                  className={classNames(
                    'xs:ml-auto',
                    CalibrationStatus.SUCCESS !== calibrationStatus && 'hidden'
                  )}
                >
                  {l10n.getString('onboarding-continue')}
                </Button>
              </div>
              <Button
                variant="secondary"
                to="/onboarding/assign-tutorial"
                className={classNames('xs:ml-auto', !skipButton && 'hidden')}
              >
                {l10n.getString('onboarding-calibration_tutorial-skip')}
              </Button>
            </div>
          </div>
          <div className="mobile:hidden flex self-center w-[32rem] mobile:absolute">
            <div className="stroke-none xs:fill-background-10 mobile:fill-background-50 mobile:blur-sm">
              <TaybolIcon width="450"></TaybolIcon>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
