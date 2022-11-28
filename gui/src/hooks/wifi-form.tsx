import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { Input } from '../components/commons/Input';
import { useOnboarding } from './onboarding';

export interface WifiFormData {
  ssid: string;
  password: string;
}

export function useWifiForm() {
  const navigate = useNavigate();
  const { state, setWifiCredentials } = useOnboarding();
  const { register, reset, handleSubmit, formState } = useForm<WifiFormData>({
    defaultValues: {},
    mode: 'onChange',
  });

  useEffect(() => {
    if (state.wifi) {
      reset({
        ssid: state.wifi.ssid,
        password: state.wifi.password,
      });
    }
  }, []);

  const submitWifiCreds = (value: WifiFormData) => {
    setWifiCredentials(value.ssid, value.password);
    navigate('/onboarding/connect-trackers', {
      state: { alonePage: state.alonePage },
    });
  };

  return {
    submitWifiCreds,
    handleSubmit,
    register,
    formState,
    hasWifiCreds: !!state.wifi,
    WifiForm: () => (
      <>
        <Input
          {...register('ssid', { required: true })}
          type="text"
          label="SSID"
          placeholder="Enter SSID"
          variant="secondary"
        />
        <Input
          {...register('password')}
          type="password"
          label="Password"
          placeholder="Enter password"
          variant="secondary"
        />
      </>
    ),
  };
}
