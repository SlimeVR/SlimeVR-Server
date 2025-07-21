import {
  ChangeSettingsRequestT,
  ResetsSettingsT,
  RpcMessage,
  SettingsResetRequestT,
  SettingsResponseT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useEffect, useState } from 'react';

export interface ResetSettingsForm {
  resetMountingFeet: boolean;
  armsMountingResetMode: number;
  yawResetSmoothTime: number;
  saveMountingReset: boolean;
  resetHmdPitch: boolean;
}

export const defaultResetSettings = {
  resetMountingFeet: false,
  armsMountingResetMode: 0,
  yawResetSmoothTime: 0.0,
  saveMountingReset: false,
  resetHmdPitch: false,
};

export function loadResetSettings(resetSettingsForm: ResetSettingsForm) {
  const resetsSettings = new ResetsSettingsT();
  resetsSettings.resetMountingFeet = resetSettingsForm.resetMountingFeet;
  resetsSettings.armsMountingResetMode = resetSettingsForm.armsMountingResetMode;
  resetsSettings.yawResetSmoothTime = resetSettingsForm.yawResetSmoothTime;
  resetsSettings.saveMountingReset = resetSettingsForm.saveMountingReset;
  resetsSettings.resetHmdPitch = resetSettingsForm.resetHmdPitch;

  return resetsSettings;
}

export function useResetSettings() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [settings, setSettings] = useState<ResetSettingsForm>(defaultResetSettings);

  useEffect(() =>
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsResetRequestT())
  );

  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    if (settings.resetsSettings) setSettings(settings.resetsSettings);
  });

  return {
    update: (resetSettingsForm: Partial<ResetSettingsForm>) => {
      const req = new ChangeSettingsRequestT();
      const res = loadResetSettings({ ...settings, ...resetSettingsForm });
      req.resetsSettings = res;
      sendRPCPacket(RpcMessage.ChangeSettingsRequest, req);
    },
  };
}
