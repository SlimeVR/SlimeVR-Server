import { useOnboarding } from '../../../../hooks/onboarding';
import { ArrowLink } from '../../../commons/ArrowLink';
import { Button } from '../../../commons/Button';
import { Typography } from '../../../commons/Typography';
import { MountingStepper } from './MountingStepper';

export function AutomaticMountingPage() {
  const { applyProgress, skipSetup, state } = useOnboarding();

  applyProgress(0.7);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
        <div className="flex flex-col w-full h-full justify-center max-w-3xl gap-5">
          <div className="flex flex-col max-w-lg gap-3">
            {!state.alonePage && (
              <ArrowLink to="/onboarding/enter-vr" direction="left">
                Go Back to Enter VR
              </ArrowLink>
            )}
            <Typography variant="main-title">
              Mounting calibration!{' '}
              <span className="p-1 bg-accent-background-30 text-standard rounded-md">
                Work in progress
              </span>
            </Typography>
            <Typography color="secondary">
              For SlimeVR trackers to work, we need to assign a mounting
              rotation to your trackers to align them with your physical tracker
              mounting.
            </Typography>
          </div>
          <div className="flex">
            <MountingStepper
              variant={state.alonePage ? 'alone' : 'onboarding'}
            ></MountingStepper>
          </div>
        </div>
        <div className="w-full pb-4 flex flex-row">
          <div className="flex flex-grow gap-3">
            {!state.alonePage && (
              <Button variant="secondary" to="/" onClick={skipSetup}>
                Skip setup
              </Button>
            )}
          </div>
          <div className="flex gap-3">
            <Button
              variant="secondary"
              state={{ alonePage: state.alonePage }}
              to="/onboarding/mounting/manual"
            >
              Manually set mounting
            </Button>
            {!state.alonePage && (
              <Button variant="primary" to="/onboarding/reset-tutorial">
                Next step
              </Button>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
