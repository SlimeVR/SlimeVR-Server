import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { useOnboarding } from '../../../hooks/onboarding';
import { ArrowLink } from '../../commons/ArrowLink';
import { Button } from '../../commons/Button';
import { Input } from '../../commons/Input';
import { Typography } from '../../commons/Typography';

export interface WifiForm {
  ssid: string;
  password: string;
}

export function WifiCredsPage() {
  const navigate = useNavigate();
  const { applyProgress, state, setWifiCredentials, skipSetup } =
    useOnboarding();
  const { register, reset, handleSubmit, formState } = useForm<WifiForm>({
    defaultValues: {},
    mode: 'onChange',
  });

  applyProgress(0.2);

  useEffect(() => {
    if (state.wifi) {
      reset({
        ssid: state.wifi.ssid,
        password: state.wifi.password,
      });
    }
  }, []);

  const submitWifiCreds = (value: WifiForm) => {
    setWifiCredentials(value.ssid, value.password);
    navigate('/onboarding/connect-trackers');
  };

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
            <Input
              {...register('ssid', { required: true })}
              type="text"
              label="SSID"
              placeholder="Enter SSID"
            />
            <Input
              {...register('password')}
              type="password"
              label="Password"
              placeholder="Enter password"
            />
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
