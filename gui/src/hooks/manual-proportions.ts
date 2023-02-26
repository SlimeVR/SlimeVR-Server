import {
  SkeletonConfigResponseT,
  SkeletonBone,
  RpcMessage,
  SkeletonConfigRequestT,
  ChangeSkeletonConfigRequestT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useReducer, useEffect, useMemo, useState } from 'react';

export type ProportionChange = LinearChange | RatioChange | BoneChange | GroupChange;

export enum ProportionChangeType {
  Linear,
  Ratio,
  Bone,
  Group,
}

export interface LinearChange {
  type: ProportionChangeType.Linear;
  value: number;
}

export interface RatioChange {
  type: ProportionChangeType.Ratio;
  /**
   * This is a number between -1 and 1 [-1; 1]
   */
  value: number;
}

export interface BoneChange {
  type: ProportionChangeType.Bone;
  bone: SkeletonBone;
  value: number;
  label: string;
}

export interface GroupChange {
  type: ProportionChangeType.Group;
  bones: {
    bone: SkeletonBone;
    /**
     * This is a number between 0 and 1 [0; 1]
     */
    value: number;
    label: string;
  }[];
  value: number;
  label: string;
  index?: number;
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
    /**
     * This is a number between 0 and 1 [0; 1]
     */
    value: number;
  }[];
  value: number;
  label: string;
  index?: number;
}

function reducer(state: ProportionState, action: ProportionChange): ProportionState {
  switch (action.type) {
    case ProportionChangeType.Bone: {
      return {
        ...action,
        type: BoneType.Single,
      };
    }

    case ProportionChangeType.Group: {
      return {
        ...action,
        type: BoneType.Group,
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

    case ProportionChangeType.Ratio: {
      if (state.type === BoneType.Single || state.index === undefined) {
        throw new Error(`Unexpected increase of bone ${state}`);
      }

      const newState = { ...state };
      if (newState.index === undefined) throw 'unreachable';
      newState.bones[newState.index].value += action.value;
      const filtered = newState.bones.filter((_it, index) => newState.index !== index);
      const total = filtered.reduce((acc, cur) => acc + cur.value, 0);

      for (const part of filtered) {
        part.value += (part.value / total) * action.value;
      }

      return newState;
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
    /**
     * This is a number between 0 and 1 [0; 1]
     */
    value: number;
    label: string;
  }[];
  value: number;
  label: string;
}

export interface GroupPartLabel {
  type: LabelType.GroupPart;
  bone: SkeletonBone;
  /**
   * This is a number between 0 and 1 [0; 1]
   */
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
    if (!config) return [];
    if (ratio) {
      // TODO: Please do this Uriel.
      return [];
    }

    return config.skeletonParts.map(({ bone, value }) => ({
      type: LabelType.Bone,
      bone,
      label: 'skeleton_bone-' + SkeletonBone[bone],
      value,
    }));
  }, [config, ratio]);

  useRPCPacket(RpcMessage.SkeletonConfigResponse, (data: SkeletonConfigResponseT) => {
    setConfig(data);
  });

  useEffect(() => {
    sendRPCPacket(RpcMessage.SkeletonConfigRequest, new SkeletonConfigRequestT());
  }, []);

  useEffect(() => {
    const conf = { ...config } as Omit<SkeletonConfigResponseT, 'pack'> | null;

    if (state.type === BoneType.Single) {
      // Just ignore if bone is none (because initial state value)
      // and check if we actually changed of value
      if (
        state.bone === SkeletonBone.NONE ||
        bodyParts.find((it) => it.type === LabelType.Bone && it.bone === state.bone)
          ?.value === state.value
      ) {
        return;
      }

      sendRPCPacket(
        RpcMessage.ChangeSkeletonConfigRequest,
        new ChangeSkeletonConfigRequestT(state.bone, state.value)
      );
      const b = conf?.skeletonParts?.find(({ bone }) => bone === state.bone);
      if (!b || !conf) return;
      b.value = state.value;
    } else {
      const part = bodyParts.find(
        (it) => it.type === LabelType.Group && it.label === state.label
      ) as GroupLabel | undefined;

      // Check if we found the group we were looking for
      // and check if it even changed of value
      // we only need to check one child because changing one
      // value propagates to the other children
      if (
        !part ||
        part.value === state.value ||
        part.bones[0].value === state.bones[0].value
      ) {
        return;
      }

      for (const child of state.bones) {
        sendRPCPacket(
          RpcMessage.ChangeSkeletonConfigRequest,
          new ChangeSkeletonConfigRequestT(child.bone, state.value * child.value)
        );

        const b = conf?.skeletonParts?.find(({ bone }) => bone === child.bone);
        if (!b || !conf) return;
        b.value = state.value;
      }
    }

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
