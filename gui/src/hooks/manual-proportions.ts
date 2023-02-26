import {
  SkeletonConfigResponseT,
  SkeletonBone,
  RpcMessage,
  SkeletonConfigRequestT,
  ChangeSkeletonConfigRequestT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useReducer, useEffect, useMemo, useState } from 'react';

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
  label: string;
}

export type ProportionState = BoneState | GroupState;

export enum BoneType {
  Single,
  Group,
}

export interface BoneState {
  type: BoneType.Single;
  bone: SkeletonBone;
  value: number;
  label: string;
}

export interface GroupState {
  type: BoneType.Group;
  bones: {
    bone: SkeletonBone;
    value: number;
  }[];
  value: number;
  label: string;
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

export type Label = BoneLabel | GroupLabel | GroupPartLabel;

export enum LabelType {
  Bone,
  Group,
  GroupPart,
}

export interface BoneLabel {
  type: LabelType.Bone;
  bone: SkeletonBone;
  value: number;
  label: string;
}

export interface GroupLabel {
  type: LabelType.Group;
  bones: {
    bone: SkeletonBone;
    value: number;
    label: string;
  }[];
  value: number;
  label: string;
}

export interface GroupPartLabel {
  type: LabelType.GroupPart;
  bone: SkeletonBone;
  value: number;
  label: string;
  index: number;
}

export function useManualProportions(): [
  Label[],
  boolean,
  ProportionState,
  (change: ProportionChange) => void,
  (ratio: boolean) => void
] {
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [config, setConfig] = useState<Omit<SkeletonConfigResponseT, 'pack'> | null>(
    null
  );
  const [ratio, setRatio] = useState(false);
  const [state, dispatch] = useReducer(reducer, {
    type: BoneType.Single,
    bone: SkeletonBone.NONE,
    value: 0,
    label: 'invalid-bone',
  });

  const bodyParts: Label[] = useMemo(() => {
    return (
      config?.skeletonParts.map(({ bone, value }) => ({
        bone,
        label: 'skeleton_bone-' + SkeletonBone[bone],
        value,
      })) || []
    );
  }, [config]);

  useRPCPacket(RpcMessage.SkeletonConfigResponse, (data: SkeletonConfigResponseT) => {
    setConfig(data);
  });

  useEffect(() => {
    sendRPCPacket(RpcMessage.SkeletonConfigRequest, new SkeletonConfigRequestT());
  }, []);

  useEffect(() => {
    if (
      state.bone === SkeletonBone.NONE ||
      bodyParts.find((it) => it.bone === state.bone)?.value === state.value
    ) {
      return;
    }

    sendRPCPacket(
      RpcMessage.ChangeSkeletonConfigRequest,
      new ChangeSkeletonConfigRequestT(state.bone, state.value)
    );
    const conf = { ...config } as Omit<SkeletonConfigResponseT, 'pack'> | null;
    const b = conf?.skeletonParts?.find(({ bone }) => bone == state.bone);
    if (!b || !conf) return;
    b.value = state.value;

    setConfig(conf);
  }, [state]);

  return [bodyParts, ratio, state, dispatch, setRatio];
}

function roundedStep(value: number, step: number, add: boolean): number {
  if (!add) {
    return (Math.round(value * 200) - step * 2) / 200;
  } else {
    return (Math.round(value * 200) + step * 2) / 200;
  }
}
