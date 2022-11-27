import { useOnboarding } from '../../../hooks/onboarding';
import { ArrowLink } from '../../commons/ArrowLink';
import { Button } from '../../commons/Button';
import { Typography } from '../../commons/Typography';

export function EnterVRPage() {
  const { applyProgress, skipSetup } = useOnboarding();

  applyProgress(0.6);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
        <div className="flex flex-col w-full h-full justify-center items-center">
          <div className="flex gap-8">
            <div className="flex flex-col max-w-md gap-3">
              <ArrowLink to="/onboarding/trackers-assign" direction="left">
                Go Back to Tracker assignent
              </ArrowLink>
              <Typography variant="main-title">Time to enter VR!</Typography>
              <Typography color="secondary">
                Put on all your trackers and then enter VR!
              </Typography>
            </div>
            {/* <div className="flex flex-col flex-grow gap-3 rounded-xl fill-background-50">
              <Typography variant="main-title">Illustration HERE</Typography>
            </div> */}
          </div>
        </div>
        <div className="w-full py-4 flex flex-row">
          <div className="flex flex-grow">
            <Button variant="secondary" to="/" onClick={skipSetup}>
              Skip setup
            </Button>
          </div>
          <div className="flex gap-3">
            <Button variant="primary" to="/onboarding/mounting/auto">
              I'm ready
            </Button>
          </div>
        </div>
      </div>
    </>
  );
}
