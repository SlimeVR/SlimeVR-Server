import {
  SkeletonConfigResponseT,
  SkeletonBone,
  RpcMessage,
  SkeletonConfigRequestT,
  ChangeSkeletonConfigRequestT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useEffect, useMemo, useState } from 'react';
import { useConfig } from './config';
import * as Sentry from '@sentry/react';

type LabelBase = {
  value: number;
  label: string;
} & ({ unit: 'cm' } | { unit: 'percent'; ratio: number });

export type BoneLabel = LabelBase & {
  type: 'bone';
  bone: SkeletonBone;
};

export type GroupLabel = LabelBase & {
  type: 'group';
  bones: GroupPartLabel[];
};

export type GroupPartLabel = LabelBase & {
  type: 'group-part';
  bone: SkeletonBone;
  group: string;
};

export type Label = BoneLabel | GroupLabel | GroupPartLabel;

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

export type UpdateBoneParams = { newValue: number } & (
  | { type: 'bone'; bone: SkeletonBone }
  | { type: 'group'; group: string }
  | { type: 'group-part'; group: string; bone: SkeletonBone }
);

export function useManualProportions({ type }: { type: 'linear' | 'ratio' }): {
  bodyPartsGrouped: Label[];
  changeBoneValue: (params: UpdateBoneParams) => void;
} {
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const { setConfig } = useConfig();
  const [skeleton, setSkeleton] = useState<Omit<
    SkeletonConfigResponseT,
    'pack'
  > | null>(null);
  const bodyPartsGrouped: Label[] = useMemo(() => {
    if (!skeleton) return [];
    if (type === 'linear') {
      return skeleton.skeletonParts.map(
        ({ bone, value }) =>
          ({
            type: 'bone',
            unit: 'cm',
            bone,
            label: 'skeleton_bone-' + SkeletonBone[bone],
            value,
          }) satisfies BoneLabel
      );
    }

    return [
      ...[...BONE_MAPPING.keys()].map((groupName) => {
        const groupBones = BONE_MAPPING.get(groupName);
        if (!groupBones) throw 'invalid state - this value should always exits';
        const total = skeleton.skeletonParts
          .filter(({ bone }) => groupBones.includes(bone))
          .reduce((acc, cur) => cur.value + acc, 0);
        return {
          type: 'group',
          bones: skeleton.skeletonParts
            .filter(({ bone }) => groupBones.includes(bone))
            .map(({ bone, value }) => ({
              type: 'group-part',
              group: groupName,
              unit: 'percent',
              bone,
              label: 'skeleton_bone-' + SkeletonBone[bone],
              value: value,
              ratio: value / total,
            })),
          unit: 'cm',
          label: groupName,
          value: total,
        } satisfies GroupLabel;
      }),
      ...skeleton.skeletonParts
        .filter(
          ({ bone }) =>
            ![...BONE_MAPPING.values()].find((bones) => bones.includes(bone))
        )
        .map(
          ({ bone, value }) =>
            ({
              type: 'bone',
              unit: 'cm',
              bone,
              label: 'skeleton_bone-' + SkeletonBone[bone],
              value,
            }) satisfies BoneLabel
        ),
    ];
  }, [skeleton, type]);

  useRPCPacket(RpcMessage.SkeletonConfigResponse, (data: SkeletonConfigResponseT) => {
    setSkeleton(data);
  });

  useEffect(() => {
    sendRPCPacket(RpcMessage.SkeletonConfigRequest, new SkeletonConfigRequestT());
  }, []);

  return {
    bodyPartsGrouped,
    changeBoneValue: (params) => {
      if (!skeleton) return;
      if (params.type === 'group') {
        const group = BONE_MAPPING.get(params.group);
        if (!group) throw 'invalid state - group should exist';
        const oldGroupTotal = skeleton.skeletonParts
          .filter(({ bone }) => group.includes(bone))
          .reduce((acc, cur) => cur.value + acc, 0);
        for (const part of group) {
          const currentValue = skeleton.skeletonParts.find(({ bone }) => bone === part);
          if (!currentValue) throw 'invalid state - the bone should exists';
          const currentRatio = currentValue.value / oldGroupTotal;
          sendRPCPacket(
            RpcMessage.ChangeSkeletonConfigRequest,
            new ChangeSkeletonConfigRequestT(part, params.newValue * currentRatio)
          );
        }
      }

      if (params.type === 'group-part') {
        const group = BONE_MAPPING.get(params.group);
        if (!group) throw 'invalid state - group should exist';
        const part = skeleton.skeletonParts.find(({ bone }) => bone === params.bone);
        if (!part) throw 'invalid state - the part should exists';
        const oldGroupTotal = skeleton.skeletonParts
          .filter(({ bone }) => group.includes(bone))
          .reduce((acc, cur) => cur.value + acc, 0);
        let newValue = part.value + oldGroupTotal * params.newValue; // the new ratio is computed from the group size and not the bone
        if (newValue <= 0)
          // Prevent ratios from getting below zero
          newValue = 0;

        sendRPCPacket(
          RpcMessage.ChangeSkeletonConfigRequest,
          new ChangeSkeletonConfigRequestT(params.bone, newValue)
        );

        // Update percent from other bones ratios so the total stays 100%
        // it will remove or add to the other bones proportionally to their current value
        const diffValue = Math.abs(newValue - part.value);
        const signDiff = Math.sign(newValue - part.value);
        for (const part of group) {
          if (part === params.bone) continue;
          const currentValue = skeleton.skeletonParts.find(({ bone }) => bone === part);
          if (!currentValue) throw 'invalid state - the bone should exists';
          sendRPCPacket(
            RpcMessage.ChangeSkeletonConfigRequest,
            new ChangeSkeletonConfigRequestT(
              part,
              currentValue.value - (diffValue / (group.length - 1)) * signDiff
            )
          );
        }
      }

      if (params.type === 'bone') {
        sendRPCPacket(
          RpcMessage.ChangeSkeletonConfigRequest,
          new ChangeSkeletonConfigRequestT(params.bone, params.newValue)
        );
      }
      sendRPCPacket(RpcMessage.SkeletonConfigRequest, new SkeletonConfigRequestT());
      setConfig({ lastUsedProportions: 'manual' });
      Sentry.metrics.count('manual_proportions_change', 1, { attributes: params });
    },
  };
}

export const MIN_HEIGHT = 0.4;
export const CURRENT_EXPORT_VERSION = 1;
