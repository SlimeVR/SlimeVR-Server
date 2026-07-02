import {
  ResetsSettingsResponseT,
  RpcMessage,
  SettingsResetRequestT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useEffect, useState } from 'react';

export interface ResetSettingsForm {
  resetMountingFeet: boolean;
  armsResetMode: number;
  yawResetSmoothTime: number;
  saveMountingReset: boolean;
  resetHmdPitch: boolean;
}

export const defaultResetSettings = {
  resetMountingFeet: false,
  armsResetMode: 0,
  yawResetSmoothTime: 0.0,
  saveMountingReset: false,
  resetHmdPitch: false,
};

export function loadResetSettings(resetSettingsForm: ResetSettingsForm) {
  const resetsSettings = new ResetsSettingsResponseT();
  resetsSettings.resetMountingFeet = resetSettingsForm.resetMountingFeet;
  resetsSettings.armsResetMode = resetSettingsForm.armsResetMode;
  resetsSettings.yawResetSmoothTime = resetSettingsForm.yawResetSmoothTime;
  resetsSettings.saveMountingReset = resetSettingsForm.saveMountingReset;
  resetsSettings.resetHmdPitch = resetSettingsForm.resetHmdPitch;

  return resetsSettings;
}

export function useResetSettings() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [settings, setSettings] = useState<ResetSettingsForm>(defaultResetSettings);

  useEffect(() =>
    sendRPCPacket(RpcMessage.ResetsSettingsRequest, new SettingsResetRequestT())
  );

  useRPCPacket(
    RpcMessage.ResetsSettingsResponse,
    (settings: ResetsSettingsResponseT) => {
      if (settings) setSettings(settings);
    }
  );

  return {
    update: (resetSettingsForm: Partial<ResetSettingsForm>) => {
      const req = loadResetSettings({ ...settings, ...resetSettingsForm });
      sendRPCPacket(RpcMessage.ChangeResetsSettingsRequest, req);
    },
  };
}
