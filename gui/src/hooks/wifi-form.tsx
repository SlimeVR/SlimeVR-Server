import { Localized } from '@fluent/react';
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
  const { register, reset, handleSubmit, formState, control } =
    useForm<WifiFormData>({
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
        <Localized
          id="onboarding-wifi_creds-ssid"
          attrs={{ placeholder: true, label: true }}
        >
          <Input
            control={control}
            rules={{ required: true }}
            name="ssid"
            type="text"
            label="SSID"
            placeholder="ssid"
            variant="secondary"
          />
        </Localized>
        <Localized
          id="onboarding-wifi_creds-password"
          attrs={{ placeholder: true, label: true }}
        >
          <Input
            control={control}
            name="password"
            type="password"
            label="Password"
            placeholder="password"
            variant="secondary"
          />
        </Localized>
      </>
    ),
  };
}
