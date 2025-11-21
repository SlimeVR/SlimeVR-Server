import { Typography } from '@/components/commons/Typography';
import { useBreakpoint } from '@/hooks/breakpoint';
import {
  DEFAULT_FULL_HEIGHT,
  EYE_HEIGHT_TO_HEIGHT_RATIO,
  useHeightContext,
} from '@/hooks/height';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useLocaleConfig } from '@/i18n/config';
import classNames from 'classnames';
import convert from 'convert';
import { useMemo, useState } from 'react';
import {
  ChangeSettingsRequestT,
  ModelSettingsT,
  RpcMessage,
  SkeletonHeightT,
  SkeletonResetAllRequestT,
} from 'solarxr-protocol';

function IncrementButton({
  value,
  unit,
  disabled = false,
  onClick,
}: {
  value: number;
  disabled?: boolean;
  unit: 'ft' | 'inch' | 'cm';
  onClick: () => void;
}) {
  const { isMd } = useBreakpoint('md');

  return (
    <div
      className={classNames(
        'flex md:aspect-square rounded-md items-center justify-center flex-row md:flex-col  w-fit gap-1 p-3 md:p-2 md:w-auto',
        {
          'cursor-not-allowed bg-background-80 opacity-50': disabled,
          'bg-background-60 hover:bg-background-50 cursor-pointer': !disabled,
        }
      )}
      onClick={() => !disabled && onClick()}
    >
      <Typography
        variant={isMd ? 'main-title' : 'section-title'}
        color={disabled ? 'text-background-40' : 'primary'}
      >
        {value > 0 ? `+${value}` : value}
      </Typography>
      <Typography
        id={`unit-${unit}`}
        color={disabled ? 'text-background-40' : 'primary'}
      />
    </div>
  );
}

function UnitSelector({
  name,
  active,
  onClick,
}: {
  name: string;
  active: boolean;
  onClick: () => void;
}) {
  const { isSm } = useBreakpoint('sm');

  return (
    <div
      className={classNames(
        {
          'bg-accent-background-30': active,
          'hover:bg-background-40 bg-background-50': !active,
        },
        'flex items-center justify-center rounded-md outline-background-10 cursor-pointer'
      )}
      onClick={onClick}
    >
      <Typography variant={isSm ? 'standard' : 'section-title'} id={name} />
    </div>
  );
}

function formatHeightWithIntl(meters: number, locale: string[]) {
  const totalInches = convert(meters, 'meter').to('inch');
  const feet = Math.trunc(totalInches / 12);
  const inches = Math.round(totalInches % 12);

  const feetFormatter = new Intl.NumberFormat(locale, {
    style: 'unit',
    unit: 'foot',
    unitDisplay: 'narrow',
    maximumFractionDigits: 0,
  });

  const inchFormatter = new Intl.NumberFormat(locale, {
    style: 'unit',
    unit: 'inch',
    unitDisplay: 'narrow',
    maximumFractionDigits: 0,
  });

  return `${feetFormatter.format(feet)} ${inchFormatter.format(inches)}`;
}

const roundHeight = (value: number): number => Math.round(value * 1000) / 1000;

export function HeightSelectionInput({ auto }: { auto: boolean }) {
  const { sendRPCPacket } = useWebsocketAPI();
  const [unit, setUnit] = useState<'meter' | 'foot'>('meter');
  const { currentLocales } = useLocaleConfig();
  const { currentHeight, setHmdHeight } = useHeightContext();

  const formatedHeight = useMemo(() => {
    const fullHeight = roundHeight(
      (currentHeight && currentHeight / EYE_HEIGHT_TO_HEIGHT_RATIO) ||
        DEFAULT_FULL_HEIGHT
    );
    if (unit === 'meter')
      return new Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'meter',
        maximumFractionDigits: 2,
        minimumFractionDigits: 2,
      }).format(fullHeight);
    else return formatHeightWithIntl(fullHeight, currentLocales);
  }, [currentHeight, unit]);

  const increment = (unit: 'inch' | 'cm', value: number) => {
    const headsetHeight = roundHeight(
      ((currentHeight && currentHeight / EYE_HEIGHT_TO_HEIGHT_RATIO) ||
        DEFAULT_FULL_HEIGHT) * EYE_HEIGHT_TO_HEIGHT_RATIO
    );

    const newValue = headsetHeight + convert(value, unit).to('cm') / 100;
    setHmdHeight(newValue);
    const settingsRequest = new ChangeSettingsRequestT();
    settingsRequest.modelSettings = new ModelSettingsT(
      null,
      null,
      null,
      new SkeletonHeightT(newValue, 0)
    );
    sendRPCPacket(RpcMessage.ChangeSettingsRequest, settingsRequest);

    sendRPCPacket(
      RpcMessage.SkeletonResetAllRequest,
      new SkeletonResetAllRequestT()
    );
  };

  const minimalHeight = (currentHeight ?? 0) <= 0.81;
  const minimalHeight10cm = (currentHeight ?? 0) <= 0.91;

  return (
    <div className="flex gap-2 md:h-[75px] w-full flex-col md:flex-row items-center">
      <div className="flex gap-2 h-full">
        {unit === 'foot' && (
          <>
            <div className="aspect-square bg-background-80 opacity-50 rounded-md items-center justify-center flex-col hidden md:flex" />
            <IncrementButton
              value={-1}
              unit={'inch'}
              onClick={() => increment('inch', -1)}
              disabled={auto || minimalHeight}
            />
          </>
        )}
        {unit === 'meter' && (
          <>
            <IncrementButton
              value={-10}
              unit={'cm'}
              onClick={() => increment('cm', -10)}
              disabled={auto || minimalHeight10cm}
            />
            <IncrementButton
              value={-1}
              unit={'cm'}
              onClick={() => increment('cm', -1)}
              disabled={auto || minimalHeight}
            />
          </>
        )}
      </div>
      <div className="flex w-full md:w-auto md:flex-grow bg-background-60 rounded-md px-2 py-2 h-full">
        <div className="h-full flex items-center flex-grow justify-center min-w-24">
          <Typography variant="main-title">{formatedHeight}</Typography>
        </div>
        <div className="w-28 md:w-20 h-full gap-2 grid grid-rows-1 grid-cols-2 md:grid-rows-2 md:grid-cols-1 p-1">
          <UnitSelector
            active={unit === 'meter'}
            name="unit-meter"
            onClick={() => setUnit('meter')}
          />
          <UnitSelector
            active={unit === 'foot'}
            name="unit-foot"
            onClick={() => setUnit('foot')}
          />
        </div>
      </div>
      <div className="flex gap-2 h-full">
        {unit === 'foot' && (
          <>
            <IncrementButton
              value={1}
              unit={'inch'}
              onClick={() => increment('inch', 1)}
              disabled={auto}
            />
            <div className="aspect-square bg-background-80 opacity-50 rounded-md items-center justify-center flex-col hidden md:flex" />
          </>
        )}
        {unit === 'meter' && (
          <>
            <IncrementButton
              value={1}
              unit={'cm'}
              onClick={() => increment('cm', 1)}
              disabled={auto}
            />
            <IncrementButton
              value={10}
              unit={'cm'}
              onClick={() => increment('cm', 10)}
              disabled={auto}
            />
          </>
        )}
      </div>
    </div>
  );
}
