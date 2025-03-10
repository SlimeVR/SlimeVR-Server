import {
  SkeletonConfigResponseT,
  SkeletonBone,
  RpcMessage,
  SkeletonConfigRequestT,
  ChangeSkeletonConfigRequestT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useReducer, useEffect, useMemo, useState, useLayoutEffect } from 'react';

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
  parentLabel: string;
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
  currentLabel: string;
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
  currentLabel: string;
  index?: number;
  parentLabel: string;
}

function reducer(state: ProportionState, action: ProportionChange): ProportionState {
  switch (action.type) {
    case ProportionChangeType.Bone: {
      return {
        ...action,
        currentLabel: action.label,
        type: BoneType.Single,
      };
    }

    case ProportionChangeType.Group: {
      return {
        ...action,
        currentLabel: action.label,
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
        value: state.value + action.value / 100,
      };
    }

    case ProportionChangeType.Ratio: {
      if (state.type === BoneType.Single || state.index === undefined) {
        throw new Error(`Unexpected increase of bone ${state.currentLabel}`);
      }

      const newState: GroupState = JSON.parse(JSON.stringify(state));
      if (newState.index === undefined) throw 'unreachable';
      newState.bones[newState.index].value += action.value;
      if (newState.bones[newState.index].value <= 0) return state;
      const filtered = newState.bones.filter((_it, index) => newState.index !== index);
      const total = filtered.reduce((acc, cur) => acc + cur.value, 0);

      for (const part of filtered) {
        part.value += (part.value / total) * action.value * -1;
        if (part.value <= 0) return state;
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
  parentLabel: string;
  index: number;
}

const BONE_MAPPING: Map<string, SkeletonBone[]> = new Map([
  [
    'skeleton_bone-torso_group',
    [
      SkeletonBone.UPPER_CHEST,
      SkeletonBone.CHEST,
      SkeletonBone.HIP,
      SkeletonBone.WAIST,
    ],
  ],
  ['skeleton_bone-leg_group', [SkeletonBone.UPPER_LEG, SkeletonBone.LOWER_LEG]],
  ['skeleton_bone-arm_group', [SkeletonBone.UPPER_ARM, SkeletonBone.LOWER_ARM]],
]);

export const INVALID_BONE: BoneState = {
  type: BoneType.Single,
  bone: SkeletonBone.NONE,
  value: 0,
  currentLabel: 'invalid-bone',
};

export function useManualProportions(): {
  bodyParts: Label[];
  ratioMode: boolean;
  state: ProportionState;
  dispatch: (change: ProportionChange) => void;
  setRatioMode: (ratio: boolean) => void;
} {
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [config, setConfig] = useState<Omit<SkeletonConfigResponseT, 'pack'> | null>(
    null
  );
  const [ratio, setRatio] = useState(false);
  const [state, dispatch] = useReducer(reducer, INVALID_BONE);

  const bodyParts: Label[] = useMemo(() => {
    if (!config) return [];
    if (ratio) {
      const groups: GroupPartLabel[] = [];
      for (const [label, related] of BONE_MAPPING) {
        const children = config.skeletonParts.filter((it) => related.includes(it.bone));
        const total = children.reduce((acc, cur) => cur.value + acc, 0);

        const group: GroupPartLabel = {
          parentLabel: label,
          label,
          type: LabelType.GroupPart,
          value: total,
          bones: children.map((it) => ({
            label: 'skeleton_bone-' + SkeletonBone[it.bone],
            value: it.value / total,
            bone: it.bone,
          })),
          index: 0,
        };
        groups.push(
          ...children.map((_it, index) => ({
            ...group,
            index,
            label: group.bones[index].label,
          }))
        );
      }

      return config.skeletonParts.flatMap(({ bone, value }) => {
        const part = groups.find((it) => it.bones[it.index].bone === bone);
        if (part === undefined) {
          return {
            type: LabelType.Bone,
            bone,
            label: 'skeleton_bone-' + SkeletonBone[bone],
            value,
          };
        }

        if (part.index === 0) {
          return [
            // For some reason, Typescript can't handle this being a GroupPart
            // when specifically inside an array. If I directly return it without part,
            // it will work. Surely some typing in flatMap's definition is wrong
            {
              ...part,
              type: LabelType.Group,
              label: part.parentLabel,
              index: undefined,
            } as unknown as GroupPartLabel,
            part,
          ];
        }
        return part;
      });
    }

    return config.skeletonParts.map(({ bone, value }) => ({
      type: LabelType.Bone,
      bone,
      label: 'skeleton_bone-' + SkeletonBone[bone],
      value,
    }));
  }, [config, ratio]);

  useLayoutEffect(() => {
    dispatch({
      ...INVALID_BONE,
      label: INVALID_BONE.currentLabel,
      type: ProportionChangeType.Bone,
    });
  }, [ratio]);

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
        (it) =>
          it.type === LabelType.Group &&
          (it.label === state.currentLabel || it.label === state.parentLabel)
      ) as GroupLabel | undefined;

      // Check if we found the group we were looking for
      // and check if it even changed of value
      // we only need to check one child because changing one
      // value propagates to the other children

      if (
        !part ||
        (part.value === state.value && part.bones[0].value === state.bones[0].value)
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
        b.value = state.value * child.value;
      }
    }

    setConfig(conf);
  }, [state]);

  return { bodyParts, ratioMode: ratio, state, dispatch, setRatioMode: setRatio };
}

function roundedStep(value: number, step: number, add: boolean): number {
  if (!add) {
    return (Math.round(value * 200) - step * 2) / 200;
  } else {
    return (Math.round(value * 200) + step * 2) / 200;
  }
}

export const MIN_HEIGHT = 0.4;
export const CURRENT_EXPORT_VERSION = 1;
