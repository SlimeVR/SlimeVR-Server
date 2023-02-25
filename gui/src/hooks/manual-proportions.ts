import {
  SkeletonConfigResponseT,
  SkeletonBone,
  RpcMessage,
  SkeletonConfigRequestT,
  ChangeSkeletonConfigRequestT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useReducer, useEffect, useMemo, useState } from 'react';

export interface BoneLabel {
  bone: SkeletonBone;
  value: number;
  label: string;
}

export type ProportionChange = LinearChange | BoneChange;

export enum ProportionChangeType {
  Linear,
  Bone,
}

export interface LinearChange {
  type: ProportionChangeType.Linear;
  value: number;
}

export interface BoneChange {
  type: ProportionChangeType.Bone;
  bone: SkeletonBone;
  value: number;
}

export type ProportionState = BoneState;

export enum BoneType {
  Single,
}

export interface BoneState {
  type: BoneType.Single;
  bone: SkeletonBone;
  value: number;
}

function reducer(state: ProportionState, action: ProportionChange): ProportionState {
  switch (action.type) {
    case ProportionChangeType.Bone: {
      return {
        ...action,
        type: BoneType.Single,
      };
    }

    case ProportionChangeType.Linear: {
      if (action.value > 0) {
        return {
          ...state,
          value: roundedStep(state.value, action.value, true),
        };
      }

      return {
        ...state,
        value: state.value - action.value / 100,
      };
    }
  }
}

export function useManualProportions() {
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [config, setConfig] = useState<Omit<SkeletonConfigResponseT, 'pack'> | null>(
    null
  );
  const [state, dispatch] = useReducer();

  useRPCPacket(RpcMessage.SkeletonConfigResponse, (data: SkeletonConfigResponseT) => {
    setConfig(data);
  });

  useEffect(() => {
    sendRPCPacket(RpcMessage.SkeletonConfigRequest, new SkeletonConfigRequestT());
  }, []);

  const updateConfigValue = (...configChanges: ChangeSkeletonConfigRequestT[]) => {
    const conf = { ...config } as Omit<SkeletonConfigResponseT, 'pack'> | null;
    for (const configChange of configChanges) {
      sendRPCPacket(RpcMessage.ChangeSkeletonConfigRequest, configChange);
      const b = conf?.skeletonParts?.find(({ bone }) => bone == selectedBone);
      if (!b || !conf) return;
      b.value = configChange.value;
    }

    setConfig(conf);
  };

  const bodyParts: BoneLabel[] = useMemo(() => {
    return (
      config?.skeletonParts.map(({ bone, value }) => ({
        bone,
        label: 'skeleton_bone-' + SkeletonBone[bone],
        value,
      })) || []
    );
  }, [config]);
}

function roundedStep(value: number, step: number, add: boolean): number {
  if (!add) {
    return (Math.round(value * 200) - step * 2) / 200;
  } else {
    return (Math.round(value * 200) + step * 2) / 200;
  }
}
