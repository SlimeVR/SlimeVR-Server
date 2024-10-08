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
import { useTrackers } from '@/hooks/tracker';
import { useRestCalibrationTrackers } from '@/hooks/imu-logic';
import { averageVector, Vector3FromVec3fT } from '@/maths/vector3';
import { Vector3 } from 'three';
import { useTimeout } from '@/hooks/timeout';

export enum CalibrationStatus {
  SUCCESS,
  CALIBRATING,
  WAITING,
  ERROR,
}

export const IMU_CALIBRATION_TIME = 4;
const ACCEL_TOLERANCE = 0.2; // m/s^2

export function CalibrationTutorialPage() {
  const { l10n } = useLocalization();
  const { applyProgress } = useOnboarding();
  const [calibrationStatus, setCalibrationStatus] = useState(
    CalibrationStatus.WAITING
  );
  const [skipButton, setSkipButton] = useState(false);
  const { timer, isCounting, startCountdown, abortCountdown } = useCountdown({
    duration: IMU_CALIBRATION_TIME,
    onCountdownEnd: () => setCalibrationStatus(CalibrationStatus.SUCCESS),
  });
  useTimeout(() => setSkipButton(true), 10000);
  const { useConnectedIMUTrackers } = useTrackers();
  const connectedIMUTrackers = useConnectedIMUTrackers();
  const restCalibrationTrackers =
    useRestCalibrationTrackers(connectedIMUTrackers);
  const [rested, setRested] = useState(false);
  const lastValueMap = useRef(new Map<number, Vector3[]>());
  useEffect(() => {
    const accelLength = restCalibrationTrackers.every((x) => {
      if (
        x.tracker.trackerId?.trackerNum === undefined ||
        x.tracker.trackerId.deviceId?.id === undefined ||
        !x.tracker.linearAcceleration
      )
        return false;

      const trackerId =
        x.tracker.trackerId.trackerNum + (x.tracker.trackerId.trackerNum << 8);
      const lastValues = lastValueMap.current.get(trackerId) ?? [];
      lastValueMap.current.set(trackerId, lastValues);

      const vec3 = Vector3FromVec3fT(x.tracker.linearAcceleration);
      if (lastValues.length > 5) {
        lastValues.shift();
        const avg = averageVector(lastValues).lengthSq();
        lastValues.push(vec3);
        return vec3.lengthSq() <= avg + ACCEL_TOLERANCE ** 2;
      }
      lastValues.push(vec3);
      return false;
    });

    setRested(accelLength || restCalibrationTrackers.length === 0);
  }, [restCalibrationTrackers]);

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
                id="onboarding-calibration_tutorial-description"
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
                    isCounting
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
