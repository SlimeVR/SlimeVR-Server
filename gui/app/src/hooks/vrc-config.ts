import { useLayoutEffect, useMemo, useState } from 'react';
import { useWebsocketAPI } from './websocket-api';
import {
  RpcMessage,
  VRCAvatarMeasurementType,
  VRCConfigSettingToggleMuteT,
  VRCConfigStateChangeResponseT,
  VRCConfigStateRequestT,
  VRCSpineMode,
  VRCTrackerModel,
} from 'solarxr-protocol';

type NonNull<T> = {
  [P in keyof T]: NonNullable<T[P]>;
};

export type VRCConfigStateSupported = { isSupported: true } & NonNull<
  Pick<VRCConfigStateChangeResponseT, 'recommended' | 'state' | 'validity' | 'muted'>
>;
export type VRCConfigState = { isSupported: false } | VRCConfigStateSupported;

export const spineModeTranslationMap: Record<VRCSpineMode, string> = {
  [VRCSpineMode.UNKNOWN]: 'vrc_config-spine_mode-UNKNOWN',
  [VRCSpineMode.LOCK_BOTH]: 'vrc_config-spine_mode-LOCK_BOTH',
  [VRCSpineMode.LOCK_HEAD]: 'vrc_config-spine_mode-LOCK_HEAD',
  [VRCSpineMode.LOCK_HIP]: 'vrc_config-spine_mode-LOCK_HIP',
};

export const trackerModelTranslationMap: Record<VRCTrackerModel, string> = {
  [VRCTrackerModel.UNKNOWN]: 'vrc_config-tracker_model-UNKNOWN',
  [VRCTrackerModel.AXIS]: 'vrc_config-tracker_model-AXIS',
  [VRCTrackerModel.BOX]: 'vrc_config-tracker_model-BOX',
  [VRCTrackerModel.SPHERE]: 'vrc_config-tracker_model-SPHERE',
  [VRCTrackerModel.SYSTEM]: 'vrc_config-tracker_model-SYSTEM',
};

export const avatarMeasurementTypeTranslationMap: Record<
  VRCAvatarMeasurementType,
  string
> = {
  [VRCAvatarMeasurementType.UNKNOWN]: 'vrc_config-avatar_measurement_type-UNKNOWN',
  [VRCAvatarMeasurementType.HEIGHT]: 'vrc_config-avatar_measurement_type-HEIGHT',
  [VRCAvatarMeasurementType.ARM_SPAN]: 'vrc_config-avatar_measurement_type-ARM_SPAN',
};

export function useVRCConfig() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [state, setState] = useState<VRCConfigState | null>(null);

  useLayoutEffect(() => {
    sendRPCPacket(RpcMessage.VRCConfigStateRequest, new VRCConfigStateRequestT());
  }, []);

  useRPCPacket(
    RpcMessage.VRCConfigStateChangeResponse,
    (data: VRCConfigStateChangeResponseT) => {
      setState(data as VRCConfigState);
    }
  );

  const invalidConfig = useMemo(() => {
    if (!state?.isSupported) return false;
    return Object.entries(state.validity)
      .filter(([k]) => !state.muted.includes(k))
      .some(([, v]) => !v);
  }, [state]);

  return {
    state,
    invalidConfig,
    toggleMutedSettings: async (key: keyof VRCConfigStateSupported['validity']) => {
      const req = new VRCConfigSettingToggleMuteT();
      req.key = key;
      sendRPCPacket(RpcMessage.VRCConfigSettingToggleMute, req);
    },
  };
}
