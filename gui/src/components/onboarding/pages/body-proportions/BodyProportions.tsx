import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import {
  MouseEventHandler,
  ReactNode,
  useEffect,
  useMemo,
  useState,
} from 'react';
import {
  ChangeSkeletonConfigRequestT,
  RpcMessage,
  SkeletonBone,
  SkeletonConfigRequestT,
  SkeletonConfigResponseT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '../../../../hooks/websocket-api';
import { Typography } from '../../../commons/Typography';

function IncrementButton({
  children,
  onClick,
}: {
  children: ReactNode;
  onClick?: MouseEventHandler<HTMLDivElement>;
}) {
  return (
    <div
      onClick={onClick}
      className={classNames(
        'p-3  rounded-lg w-16 h-16 flex flex-col justify-center items-center bg-background-60 hover:bg-opacity-50'
      )}
    >
      <Typography variant="main-title" bold>
        {children}
      </Typography>
    </div>
  );
}

export function BodyProportions({
  precise,
  type,
  variant = 'onboarding',
}: {
  precise: boolean;
  type: 'linear' | 'ratio';
  variant: 'onboarding' | 'alone';
}) {
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [config, setConfig] = useState<Omit<
    SkeletonConfigResponseT,
    'pack'
  > | null>(null);
  const [selectedBone, setSelectedBone] = useState(SkeletonBone.HEAD);

  useRPCPacket(
    RpcMessage.SkeletonConfigResponse,
    (data: SkeletonConfigResponseT) => {
      setConfig(data);
    }
  );

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.SkeletonConfigRequest,
      new SkeletonConfigRequestT()
    );
  }, []);

  const updateConfigValue = (configChange: ChangeSkeletonConfigRequestT) => {
    sendRPCPacket(RpcMessage.ChangeSkeletonConfigRequest, configChange);
    const conf = { ...config } as Omit<SkeletonConfigResponseT, 'pack'> | null;
    const b = conf?.skeletonParts?.find(({ bone }) => bone == selectedBone);
    if (!b || !conf) return;
    b.value = configChange.value;
    setConfig(conf);
  };

  return (
    <div className="relative w-full">
      <div
        className={classNames(
          'flex flex-col overflow-y-scroll overflow-x-hidden max-h-[450px] w-full px-1',
          'gap-3 pb-16',
          variant === 'onboarding' && 'gradient-mask-b-90',
          variant === 'alone' && 'gradient-mask-b-80'
        )}
      >
        {type === 'linear' && (
          <LinearBoneList
            precise={precise}
            config={config}
            selectedBone={selectedBone}
            setSelectedBone={setSelectedBone}
            updateConfigValue={updateConfigValue}
          ></LinearBoneList>
        )}
        {type === 'ratio' && (
          <RatioBoneList
            precise={precise}
            config={config}
            selectedBone={selectedBone}
            setSelectedBone={setSelectedBone}
            updateConfigValue={updateConfigValue}
          ></RatioBoneList>
        )}
      </div>
    </div>
  );
}

interface BoneListCall {
  precise: boolean;
  config: Omit<SkeletonConfigResponseT, 'pack'> | null;
  selectedBone: SkeletonBone;
  setSelectedBone: (skeletonBone: SkeletonBone) => void;
  updateConfigValue: (configChange: ChangeSkeletonConfigRequestT) => void;
}

interface BoneLabel {
  bone: SkeletonBone;
  value: number;
  label: string;
}

export class SkeletonGroup {
  static BONE_MAPPING: Map<
    SkeletonBone[],
    (...bones: BoneLabel[]) => SkeletonGroup
  > = new Map([
    [
      [SkeletonBone.CHEST, SkeletonBone.HIP, SkeletonBone.WAIST],
      (...bones) =>
        SkeletonGroup.fromBones('skeleton_bone-torso_group', ...bones),
    ],
    [
      [SkeletonBone.UPPER_LEG, SkeletonBone.LOWER_LEG],
      (...bones) =>
        SkeletonGroup.fromBones('skeleton_bone-leg_group', ...bones),
    ],
    [
      [SkeletonBone.UPPER_ARM, SkeletonBone.LOWER_ARM],
      (...bones) =>
        SkeletonGroup.fromBones('skeleton_bone-arm_group', ...bones),
    ],
  ]);

  constructor(
    public label: string,
    public value: number,
    public children: { bone: SkeletonBone; ratio: number; label: string }[]
  ) {}

  static fromBones(label: string, ...bones: BoneLabel[]): SkeletonGroup {
    const total = bones.reduce((acc, cur) => cur.value + acc, 0);
    return new SkeletonGroup(
      label,
      total,
      bones.map((x) => ({
        ratio: x.value / total,
        bone: x.bone,
        label: x.label,
      }))
    );
  }

  has(bone: SkeletonBone): boolean {
    return this.children.some((x) => x.bone === bone);
  }

  static mixArray(
    bones: (BoneLabel | SkeletonGroup)[]
  ): (BoneLabel | SkeletonGroup)[] {
    if (!bones.length) return bones;
    for (const [expectedBones, build] of SkeletonGroup.BONE_MAPPING) {
      const group: BoneLabel[] = [];

      const filtered = bones.filter((part) => {
        if ('bone' in part && expectedBones.includes(part.bone)) {
          group.push(part);
          return false;
        }
        return true;
      });

      if (group.length !== expectedBones.length) {
        throw `SkeletonGroup mapping has invalid expected bones: ${expectedBones}`;
      }

      filtered.push(build(...group));

      bones = filtered;
    }
    return bones;
  }
}

export function RatioBoneList({
  precise,
  config,
  selectedBone,
  setSelectedBone,
  updateConfigValue,
}: BoneListCall) {
  const { l10n } = useLocalization();

  const bodyParts = useMemo(() => {
    return SkeletonGroup.mixArray(
      config?.skeletonParts.map(({ bone, value }) => ({
        bone,
        label: l10n.getString('skeleton_bone-' + SkeletonBone[bone]),
        value,
      })) || []
    );
  }, [config]);

  const roundedStep = (value: number, step: number, add: boolean) => {
    if (!add) {
      return (Math.round(value * 200) - step * 2) / 200;
    } else {
      return (Math.round(value * 200) + step * 2) / 200;
    }
  };

  const increment = async (value: number, v: number) => {
    const configChange = new ChangeSkeletonConfigRequestT();

    configChange.bone = selectedBone;
    configChange.value = roundedStep(value, v, true);

    updateConfigValue(configChange);
  };

  const decrement = (value: number, v: number) => {
    const configChange = new ChangeSkeletonConfigRequestT();

    configChange.bone = selectedBone;
    configChange.value = value - v / 100;

    updateConfigValue(configChange);
  };

  return (
    <>
      {bodyParts.map(({ label, value, ...props }) =>
        'bone' in props ? (
          <div className="flex" key={props.bone}>
            <div
              className={classNames(
                'flex gap-2 transition-opacity duration-300',
                selectedBone != props.bone && 'opacity-0 pointer-events-none'
              )}
            >
              {!precise && (
                <IncrementButton onClick={() => decrement(value, 5)}>
                  -5
                </IncrementButton>
              )}
              <IncrementButton onClick={() => decrement(value, 1)}>
                -1
              </IncrementButton>
              {precise && (
                <IncrementButton onClick={() => decrement(value, 0.5)}>
                  -0.5
                </IncrementButton>
              )}
            </div>
            <div
              className="flex flex-grow flex-col px-2"
              onClick={() => setSelectedBone(props.bone)}
            >
              <div
                key={props.bone}
                className={classNames(
                  'p-3  rounded-lg h-16 flex w-full items-center justify-between px-6 transition-colors duration-300 bg-background-60',
                  (selectedBone == props.bone && 'opacity-100') || 'opacity-50'
                )}
              >
                <Typography variant="section-title" bold>
                  {label}
                </Typography>
                <Typography variant="main-title" bold>
                  {Number(value * 100)
                    .toFixed(1)
                    .replace(/[.,]0$/, '')}{' '}
                  CM
                </Typography>
              </div>
            </div>
            <div
              className={classNames(
                'flex gap-2 transition-opacity duration-300',
                selectedBone != props.bone && 'opacity-0 pointer-events-none'
              )}
            >
              {precise && (
                <IncrementButton onClick={() => increment(value, 0.5)}>
                  +0.5
                </IncrementButton>
              )}
              <IncrementButton onClick={() => increment(value, 1)}>
                +1
              </IncrementButton>
              {!precise && (
                <IncrementButton onClick={() => increment(value, 5)}>
                  +5
                </IncrementButton>
              )}
            </div>
          </div>
        ) : (
          <>
            <div className="flex" key={label}>
              <div
                className={classNames(
                  'flex gap-2 transition-opacity duration-300',
                  selectedBone != props.bone && 'opacity-0 pointer-events-none'
                )}
              >
                {!precise && (
                  <IncrementButton onClick={() => decrement(value, 5)}>
                    -5
                  </IncrementButton>
                )}
                <IncrementButton onClick={() => decrement(value, 1)}>
                  -1
                </IncrementButton>
                {precise && (
                  <IncrementButton onClick={() => decrement(value, 0.5)}>
                    -0.5
                  </IncrementButton>
                )}
              </div>
              <div
                className="flex flex-grow flex-col px-2"
                onClick={() => setSelectedBone(props.bone)}
              >
                <div
                  key={props.bone}
                  className={classNames(
                    'p-3  rounded-lg h-16 flex w-full items-center justify-between px-6 transition-colors duration-300 bg-background-60',
                    (selectedBone == props.bone && 'opacity-100') ||
                      'opacity-50'
                  )}
                >
                  <Typography variant="section-title" bold>
                    {label}
                  </Typography>
                  <Typography variant="main-title" bold>
                    {Number(value * 100)
                      .toFixed(1)
                      .replace(/[.,]0$/, '')}{' '}
                    CM
                  </Typography>
                </div>
              </div>
              <div
                className={classNames(
                  'flex gap-2 transition-opacity duration-300',
                  selectedBone != props.bone && 'opacity-0 pointer-events-none'
                )}
              >
                {precise && (
                  <IncrementButton onClick={() => increment(value, 0.5)}>
                    +0.5
                  </IncrementButton>
                )}
                <IncrementButton onClick={() => increment(value, 1)}>
                  +1
                </IncrementButton>
                {!precise && (
                  <IncrementButton onClick={() => increment(value, 5)}>
                    +5
                  </IncrementButton>
                )}
              </div>
            </div>
          </>
        )
      )}
    </>
  );
}

export function LinearBoneList({
  precise,
  config,
  selectedBone,
  setSelectedBone,
  updateConfigValue,
}: BoneListCall) {
  const { l10n } = useLocalization();

  const bodyParts: BoneLabel[] = useMemo(() => {
    return (
      config?.skeletonParts.map(({ bone, value }) => ({
        bone,
        label: l10n.getString('skeleton_bone-' + SkeletonBone[bone]),
        value,
      })) || []
    );
  }, [config]);

  const roundedStep = (value: number, step: number, add: boolean) => {
    if (!add) {
      return (Math.round(value * 200) - step * 2) / 200;
    } else {
      return (Math.round(value * 200) + step * 2) / 200;
    }
  };

  const increment = async (value: number, v: number) => {
    const configChange = new ChangeSkeletonConfigRequestT();

    configChange.bone = selectedBone;
    configChange.value = roundedStep(value, v, true);

    updateConfigValue(configChange);
  };

  const decrement = (value: number, v: number) => {
    const configChange = new ChangeSkeletonConfigRequestT();

    configChange.bone = selectedBone;
    configChange.value = value - v / 100;

    updateConfigValue(configChange);
  };

  return (
    <>
      {bodyParts.map(({ label, bone, value }) => (
        <div className="flex" key={bone}>
          <div
            className={classNames(
              'flex gap-2 transition-opacity duration-300',
              selectedBone != bone && 'opacity-0 pointer-events-none'
            )}
          >
            {!precise && (
              <IncrementButton onClick={() => decrement(value, 5)}>
                -5
              </IncrementButton>
            )}
            <IncrementButton onClick={() => decrement(value, 1)}>
              -1
            </IncrementButton>
            {precise && (
              <IncrementButton onClick={() => decrement(value, 0.5)}>
                -0.5
              </IncrementButton>
            )}
          </div>
          <div
            className="flex flex-grow flex-col px-2"
            onClick={() => setSelectedBone(bone)}
          >
            <div
              key={bone}
              className={classNames(
                'p-3  rounded-lg h-16 flex w-full items-center justify-between px-6 transition-colors duration-300 bg-background-60',
                (selectedBone == bone && 'opacity-100') || 'opacity-50'
              )}
            >
              <Typography variant="section-title" bold>
                {label}
              </Typography>
              <Typography variant="main-title" bold>
                {Number(value * 100)
                  .toFixed(1)
                  .replace(/[.,]0$/, '')}{' '}
                CM
              </Typography>
            </div>
          </div>
          <div
            className={classNames(
              'flex gap-2 transition-opacity duration-300',
              selectedBone != bone && 'opacity-0 pointer-events-none'
            )}
          >
            {precise && (
              <IncrementButton onClick={() => increment(value, 0.5)}>
                +0.5
              </IncrementButton>
            )}
            <IncrementButton onClick={() => increment(value, 1)}>
              +1
            </IncrementButton>
            {!precise && (
              <IncrementButton onClick={() => increment(value, 5)}>
                +5
              </IncrementButton>
            )}
          </div>
        </div>
      ))}
    </>
  );
}
