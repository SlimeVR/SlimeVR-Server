import { useOnboarding } from '../../../hooks/onboarding';
import { useWifiForm } from '../../../hooks/wifi-form';
import { ArrowLink } from '../../commons/ArrowLink';
import { Button } from '../../commons/Button';
import { Typography } from '../../commons/Typography';

export function WifiCredsPage() {
  const { applyProgress, skipSetup } = useOnboarding();
  const { WifiForm, handleSubmit, submitWifiCreds, formState } = useWifiForm();
  applyProgress(0.2);

  return (
    <form
      className="flex flex-col w-full h-full"
      onSubmit={handleSubmit(submitWifiCreds)}
    >
      <div className="flex flex-col w-full h-full justify-center items-center">
        <div className="flex gap-10">
          <div className="flex flex-col max-w-sm">
            <ArrowLink to="/onboarding/home" direction="left">
              Go Back to introduction
            </ArrowLink>
            <Typography variant="main-title">Input WiFi credentials</Typography>
            <Typography color="secondary">
              The Trackers will use these credentials to connect wirelessly
            </Typography>
            <Typography color="secondary">
              please use the credentials that you are currently connected to
            </Typography>
          </div>
          <div className="flex flex-col bg-background-70 gap-3 p-10 rounded-xl max-w-sm">
            <WifiForm></WifiForm>
          </div>
        </div>
      </div>
      <div className="w-full py-4 flex flex-row">
        <div className="flex flex-grow">
          <Button variant="secondary" to="/" onClick={skipSetup}>
            Skip setup
          </Button>
        </div>
        <div className="flex gap-3">
          <Button variant="secondary" to="/onboarding/trackers-assign">
            Skip wifi settings
          </Button>
          <Button type="submit" variant="primary" disabled={!formState.isValid}>
            Submit!
          </Button>
        </div>
      </div>
    </form>
  );
}
