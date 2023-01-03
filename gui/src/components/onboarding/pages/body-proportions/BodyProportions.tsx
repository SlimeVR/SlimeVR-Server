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
  variant = 'onboarding',
}: {
  precise: boolean;
  variant: 'onboarding' | 'alone';
}) {
  const { l10n } = useLocalization();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [config, setConfig] = useState<Omit<
    SkeletonConfigResponseT,
    'pack'
  > | null>(null);
  const [selectedBone, setSelectedBone] = useState(SkeletonBone.HEAD);
  const bodyParts = useMemo(() => {
    return (
      config?.skeletonParts.map(({ bone, value }) => ({
        bone,
        label: l10n.getString('skeleton_bone-' + SkeletonBone[bone]),
        value,
      })) || []
    );
  }, [config]);

  useRPCPacket(
    RpcMessage.SkeletonConfigResponse,
    (data: SkeletonConfigResponseT) => {
      setConfig(data);
      console.log(data);
    }
  );

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.SkeletonConfigRequest,
      new SkeletonConfigRequestT()
    );
  }, []);

  const roundedStep = (value: number, step: number, add: boolean) => {
    if (!add) {
      return (Math.round(value * 200) - step * 2) / 200;
    } else {
      return (Math.round(value * 200) + step * 2) / 200;
    }
  };

  const updateConfigValue = (configChange: ChangeSkeletonConfigRequestT) => {
    sendRPCPacket(RpcMessage.ChangeSkeletonConfigRequest, configChange);
    const conf = { ...config } as Omit<SkeletonConfigResponseT, 'pack'> | null;
    const b = conf?.skeletonParts?.find(({ bone }) => bone == selectedBone);
    if (!b || !conf) return;
    b.value = configChange.value;
    setConfig(conf);
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
    <div className="relative w-full">
      <div className="flex flex-col overflow-y-scroll overflow-x-hidden max-h-[450px] w-full px-1  gap-3 pb-16">
        {bodyParts.map(({ label, bone, value }) => (
          <div className="flex" key={bone}>
            <div
              className={classNames(
                'flex gap-2 transition-opacity duration-300',
                selectedBone != bone && 'opacity-0 pointer-events-none'
              )}
            >
              {!precise && (
                <IncrementButton onClick={() => decrement(value, 10)}>
                  -10
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
                <IncrementButton onClick={() => increment(value, 10)}>
                  +10
                </IncrementButton>
              )}
            </div>
          </div>
        ))}
      </div>

      <div className="absolute bottom-0 h-20 w-full pointer-events-none">
        <div
          className={classNames(
            'w-full h-full bg-gradient-to-b from-transparent  opacity-100',
            variant === 'onboarding' && 'to-background-80',
            variant === 'alone' && 'to-background-70'
          )}
        ></div>
      </div>
    </div>
  );
}
