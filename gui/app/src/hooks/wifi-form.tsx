import { useEffect, useState } from 'react';
import { useForm, useWatch, type Control } from 'react-hook-form';
import {
  RpcMessage,
  StartWifiProvisioningRequestT,
  StartWifiScanRequestT,
  StopWifiProvisioningRequestT,
  StopWifiScanRequestT,
  TrackerProvisioningStateT,
  WifiNetworkT,
  WifiProvisioningStatusResponseT,
  WifiScanStatus,
  WifiScanStatusResponseT,
} from 'solarxr-protocol';
import { useOnboarding } from './onboarding';
import { useWebsocketAPI } from './websocket-api';

export interface WifiFormData {
  ssid: string;
  password?: string;
}

export function useWifiCredsForm() {
  const { state, setWifiCredentials } = useOnboarding();
  const { register, reset, handleSubmit, formState, control } =
    useForm<WifiFormData>({
      defaultValues: {},
      reValidateMode: 'onSubmit',
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
    setWifiCredentials(value.ssid, value.password ?? '');
  };

  return {
    submitWifiCreds,
    handleSubmit,
    register,
    formState,
    hasWifiCreds: !!state.wifi,
    control,
  };
}

export function useWifiScan() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const [networks, setNetworks] = useState<WifiNetworkT[]>([]);
  const [scanStatus, setScanStatus] = useState<WifiScanStatus>(
    WifiScanStatus.NONE
  );

  const retryScan = () => {
    sendRPCPacket(RpcMessage.StartWifiScanRequest, new StartWifiScanRequestT());
  };

  useEffect(() => {
    retryScan();
    return () => {
      sendRPCPacket(RpcMessage.StopWifiScanRequest, new StopWifiScanRequestT());
    };
  }, []);

  useRPCPacket(
    RpcMessage.WifiScanStatusResponse,
    ({ status, networks: newNetworks }: WifiScanStatusResponseT) => {
      setScanStatus(status);
      if (status === WifiScanStatus.RESULTS) {
        setNetworks(newNetworks ?? []);
      }
    }
  );

  return {
    networks,
    scanStatus,
    retryScan,
  };
}

function sameCreds(a: Partial<WifiFormData>, b: Partial<WifiFormData>) {
  return a.ssid === b.ssid && (a.password ?? '') === (b.password ?? '');
}

export function useWifiProvisioningSession(control: Control<WifiFormData>) {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const [trackers, setTrackers] = useState<TrackerProvisioningStateT[]>([]);
  const [submittedCreds, setSubmittedCreds] = useState<WifiFormData | null>(
    null
  );

  useRPCPacket(
    RpcMessage.WifiProvisioningStatusResponse,
    ({ trackers: newTrackers }: WifiProvisioningStatusResponseT) => {
      setTrackers(newTrackers ?? []);
    }
  );

  useEffect(() => {
    return () => {
      sendRPCPacket(
        RpcMessage.StopWifiProvisioningRequest,
        new StopWifiProvisioningRequestT()
      );
    };
  }, []);

  const watchedCreds = useWatch({ control });
  const credentialsChanged =
    submittedCreds !== null && !sameCreds(watchedCreds, submittedCreds);

  const submit = (creds: WifiFormData) => {
    setSubmittedCreds(creds);
    const req = new StartWifiProvisioningRequestT();
    req.ssid = creds.ssid;
    req.password = creds.password ?? '';
    sendRPCPacket(RpcMessage.StartWifiProvisioningRequest, req);
  };

  return {
    trackers,
    hasSubmitted: submittedCreds !== null,
    credentialsChanged,
    submit,
  };
}
