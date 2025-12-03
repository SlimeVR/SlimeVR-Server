import { Typography } from '@/components/commons/Typography';
import { useBreakpoint } from '@/hooks/breakpoint';
import { EYE_HEIGHT_TO_HEIGHT_RATIO } from '@/hooks/height';
import { useLocaleConfig } from '@/i18n/config';
import classNames from 'classnames';
import convert from 'convert';
import { useMemo, useState } from 'react';

function IncrementButton({
  value,
  unit,
  disabled = false,
  onClick,
}: {
  value: number;
  disabled?: boolean;
  unit: 'foot' | 'inch' | 'cm';
  onClick: () => void;
}) {
  const { isXs } = useBreakpoint('xs');
  const { currentLocales } = useLocaleConfig();

  const format = useMemo(() => {
    if (unit == 'cm') return `${value > 0 ? '+' : ''}${value}`;
    const feetFormatter = new Intl.NumberFormat(currentLocales, {
      style: 'unit',
      unit: unit,
      unitDisplay: 'narrow',
      maximumFractionDigits: 0,
    });
    return `${value > 0 ? '+' : ''}${feetFormatter.format(value)}`;
  }, [currentLocales, value]);

  return (
    <div
      className={classNames(
        'flex rounded-md items-center justify-center flex-row xs:flex-col w-full gap-1 p-3 xs:p-2 xs:w-[75px] xs:h-[75px]',
        {
          'cursor-not-allowed bg-background-80 opacity-50': disabled,
          'bg-background-50 hover:bg-background-40 cursor-pointer': !disabled,
        }
      )}
      onClick={() => !disabled && onClick()}
    >
      <Typography
        variant={isXs ? 'mobile-title' : 'section-title'}
        color={disabled ? 'text-background-40' : 'primary'}
      >
        {format}
      </Typography>
      {unit == 'cm' && isXs && (
        <Typography
          id={`unit-${unit}`}
          color={disabled ? 'text-background-40' : 'primary'}
        />
      )}
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
  return (
    <div
      className={classNames(
        {
          'bg-accent-background-30': active,
          'hover:bg-background-40 bg-background-40': !active,
        },
        'flex items-center justify-center rounded-md outline-background-10 cursor-pointer'
      )}
      onClick={onClick}
    >
      <Typography id={name} />
    </div>
  );
}

function formatInFoot(meters: number, locale: string[]) {
  const totalInches = Math.round(convert(meters, 'meter').to('inch'));
  const feet = Math.floor(totalInches / 12);
  const inches = totalInches % 12;

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

const round4Digit = (value: number) => Math.round(value * 10000) / 10000;

export function HeightSelectionInput({
  disabled = false,
  hmdHeight,
  setHmdHeight,
}: {
  disabled?: boolean;
  hmdHeight: number;
  setHmdHeight: (height: number) => void;
}) {
  if (!hmdHeight) disabled = true;

  const { isXs } = useBreakpoint('xs');
  const [unit, setUnit] = useState<'meter' | 'foot'>('meter');
  const { currentLocales } = useLocaleConfig();

  const formattedHeight = useMemo(() => {
    if (!hmdHeight) return '--';

    const fullHeight = hmdHeight / EYE_HEIGHT_TO_HEIGHT_RATIO;
    const displayHeight = round4Digit(fullHeight);

    if (unit === 'meter') {
      return new Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'meter',
        maximumFractionDigits: 2,
        minimumFractionDigits: 2,
      }).format(displayHeight);
    }

    return formatInFoot(displayHeight, currentLocales);
  }, [hmdHeight, unit]);

  const incrementMath = (unit: 'inch' | 'cm' | 'foot', value: number) => {
    const incrementInMeters = convert(value, unit).to('meter');
    const oldFull = hmdHeight / EYE_HEIGHT_TO_HEIGHT_RATIO;
    const newFull = oldFull + incrementInMeters;
    const newEye = newFull * EYE_HEIGHT_TO_HEIGHT_RATIO;

    return round4Digit(newEye);
  };

  const increment = (unit: 'inch' | 'cm' | 'foot', value: number) => {
    const newEye = incrementMath(unit, value);
    setHmdHeight(newEye);
  };

  const canIcrement = (
    unit: 'inch' | 'cm' | 'foot',
    value: number,
    max: number
  ) => {
    const newEye = incrementMath(unit, value);
    return value < 0 ? newEye >= max : newEye < max;
  };

  const handleUnitChange = (newUnit: 'meter' | 'foot') => {
    if (!hmdHeight || newUnit === unit) return;

    const fullHeight = hmdHeight / EYE_HEIGHT_TO_HEIGHT_RATIO;
    let snappedHeight;

    if (newUnit === 'foot') {
      // Snap to nearest inch
      const totalInches = Math.round(convert(fullHeight, 'meter').to('inch'));
      snappedHeight = convert(totalInches, 'inch').to('meter');
    } else {
      // Snap to nearest centimeter
      const totalCm = Math.round(convert(fullHeight, 'meter').to('cm'));
      snappedHeight = convert(totalCm, 'cm').to('meter');
    }

    const newEyeHeight = round4Digit(
      snappedHeight * EYE_HEIGHT_TO_HEIGHT_RATIO
    );
    setHmdHeight(newEyeHeight);
    setUnit(newUnit);
  };

  return (
    <div className="flex gap-2 xs:h-[75px] w-full items-center">
      <div className="grid grid-rows-2 xs:grid-cols-2 gap-2 h-full">
        {unit === 'foot' && (
          <>
            <IncrementButton
              value={-1}
              unit={'foot'}
              onClick={() => increment('foot', -1)}
              disabled={disabled || !canIcrement('foot', -1, 0.9)}
            />
            <IncrementButton
              value={-1}
              unit={'inch'}
              onClick={() => increment('inch', -1)}
              disabled={disabled || !canIcrement('inch', -1, 0.9)}
            />
          </>
        )}
        {unit === 'meter' && (
          <>
            <IncrementButton
              value={-10}
              unit={'cm'}
              onClick={() => increment('cm', -10)}
              disabled={disabled || !canIcrement('cm', -10, 0.9)}
            />
            <IncrementButton
              value={-1}
              unit={'cm'}
              onClick={() => increment('cm', -1)}
              disabled={disabled || !canIcrement('cm', -1, 0.9)}
            />
          </>
        )}
      </div>
      <div className="flex w-full xs:w-auto xs:flex-grow bg-background-50 rounded-md px-2 py-2 h-full">
        <div className="h-full flex items-center flex-grow justify-center min-w-24">
          <Typography variant="main-title">{formattedHeight}</Typography>
        </div>
        <div className="w-[60px] xs:w-20 h-full gap-2 grid p-1">
          <UnitSelector
            active={unit === 'meter'}
            name={isXs ? 'unit-meter' : 'unit-cm'}
            onClick={() => handleUnitChange('meter')}
          />
          <UnitSelector
            active={unit === 'foot'}
            name="unit-foot"
            onClick={() => handleUnitChange('foot')}
          />
        </div>
      </div>
      <div className="xs:grid grid-rows-2 grid-cols-2 gap-2 h-full flex flex-col-reverse">
        {unit === 'foot' && (
          <>
            <IncrementButton
              value={1}
              unit={'inch'}
              onClick={() => increment('inch', 1)}
              disabled={disabled || !canIcrement('inch', 1, 2.4)}
            />
            <IncrementButton
              value={1}
              unit={'foot'}
              onClick={() => increment('foot', 1)}
              disabled={disabled || !canIcrement('foot', 1, 2.4)}
            />
          </>
        )}
        {unit === 'meter' && (
          <>
            <IncrementButton
              value={1}
              unit={'cm'}
              onClick={() => increment('cm', 1)}
              disabled={disabled || !canIcrement('cm', 1, 2.4)}
            />
            <IncrementButton
              value={10}
              unit={'cm'}
              onClick={() => increment('cm', 10)}
              disabled={disabled || !canIcrement('cm', 10, 2.4)}
            />
          </>
        )}
      </div>
    </div>
  );
}
