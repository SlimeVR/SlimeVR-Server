import { Localized, useLocalization } from '@fluent/react';
import { useOnboarding } from '../../../hooks/onboarding';
import { Button } from '../../commons/Button';
import { Typography } from '../../commons/Typography';
import { useMemo, useState } from 'react';
import { SkipSetupWarningModal } from '../SkipSetupWarningModal';
import { SkipSetupButton } from '../SkipSetupButton';
import { ProgressBar } from '../../commons/ProgressBar';
import { LoaderIcon, SlimeState } from '../../commons/icon/LoaderIcon';
import { useCountdown } from '../../../hooks/countdown';
import classNames from 'classnames';
import { TaybolIcon } from '../../commons/icon/TaybolIcon';

export enum CalibrationStatus {
  SUCCESS,
  CALIBRATING,
  WAITING,
  ERROR,
}

export const IMU_CALIBRATION_TIME = 4;

export function CalibrationTutorialPage() {
  const { l10n } = useLocalization();
  const { applyProgress, skipSetup } = useOnboarding();
  const [skipWarning, setSkipWarning] = useState(false);
  const [calibrationStatus, setCalibrationStatus] = useState(
    CalibrationStatus.WAITING
  );
  const { timer, isCounting, startCountdown } = useCountdown({
    duration: IMU_CALIBRATION_TIME,
    onCountdownEnd: () => setCalibrationStatus(CalibrationStatus.SUCCESS),
  });

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
        <SkipSetupButton
          visible={true}
          modalVisible={skipWarning}
          onClick={() => setSkipWarning(true)}
        ></SkipSetupButton>
        <div className="flex w-full h-full justify-center px-20 gap-14">
          <div className="flex gap-8 self-center">
            <div className="flex flex-col max-w-md gap-3">
              <div>
                <Typography variant="main-title">
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
                <div className="flex justify-center">
                  <LoaderIcon slimeState={slimeStatus}></LoaderIcon>
                </div>
                <ProgressBar
                  progress={
                    isCounting
                      ? (IMU_CALIBRATION_TIME - timer) / IMU_CALIBRATION_TIME
                      : calibrationStatus === CalibrationStatus.SUCCESS
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
              <div className="flex gap-3">
                <Button
                  variant="secondary"
                  to="/onboarding/wifi-creds"
                  className="mr-auto"
                >
                  {l10n.getString('onboarding-previous_step')}
                </Button>
                <Button
                  variant="primary"
                  onClick={() => {
                    setCalibrationStatus(CalibrationStatus.CALIBRATING);
                    startCountdown();
                  }}
                  disabled={isCounting}
                  hidden={CalibrationStatus.SUCCESS === calibrationStatus}
                  className="ml-auto"
                >
                  {l10n.getString('onboarding-calibration_tutorial-calibrate')}
                </Button>
                <Button
                  variant="primary"
                  to="/onboarding/assign-tutorial"
                  className={classNames(
                    'ml-auto',
                    CalibrationStatus.SUCCESS !== calibrationStatus && 'hidden'
                  )}
                >
                  {l10n.getString('onboarding-continue')}
                </Button>
              </div>
            </div>
          </div>
          <div className="flex self-center w-[32rem]">
            <div className="stroke-none fill-background-10">
              <TaybolIcon width="500"></TaybolIcon>
              {/* <img src="/images/taybol.png"></img> */}
            </div>
          </div>
        </div>
        <SkipSetupWarningModal
          accept={skipSetup}
          onClose={() => setSkipWarning(false)}
          isOpen={skipWarning}
        ></SkipSetupWarningModal>
      </div>
    </>
  );
}
