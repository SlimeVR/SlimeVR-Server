import { defaultConfig, UnitType, useConfig } from '@/hooks/config';
import { useLocaleConfig } from '@/i18n/config';
import convert from 'convert';
import { useMemo } from 'react';

export function HeightDisplay({
  height,
  /**
   * Unit type of only the input height
   */
  heightUnit = 'm',
  unitDisplay = 'short',
  className,
  roundInches = false,
}: {
  heightUnit?: 'm' | 'cm' | 'in';
  height: number;
  unitDisplay?: 'narrow' | 'short' | 'long';
  className?: string;
  roundInches?: boolean;
}) {
  const { currentLocales } = useLocaleConfig();
  const { config } = useConfig();

  const cmFormat = useMemo(
    () =>
      new Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'centimeter',
        unitDisplay,
        maximumFractionDigits: 2,
      }),
    [currentLocales, unitDisplay]
  );

  const [footFormat, inchesFormat] = useMemo(
    () => [
      new Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'foot',
        unitDisplay: 'narrow',
        maximumFractionDigits: 0,
        roundingMode: 'trunc',
      }),
      new Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'inch',
        unitDisplay: 'narrow',
        maximumFractionDigits: 1,
        roundingMode: 'trunc',
      }),
    ],
    [currentLocales]
  );

  const value = useMemo(() => {
    const unitSystem = config?.unitSystem ?? defaultConfig.unitSystem;

    if (unitSystem === UnitType.Metric) {
      return cmFormat.format(convert(height, heightUnit).to('cm'));
    } else {
      let totalInches = convert(height, heightUnit).to('inches');
      if (roundInches) totalInches = Math.round(totalInches);

      const feet = Math.trunc(totalInches / 12);
      const remainingInches = totalInches % 12;
      return (
        (feet ? footFormat.format(feet) : '') +
        inchesFormat.format(remainingInches)
      );
    }
  }, [config?.unitSystem, height, heightUnit]);

  return <span className={className}>{value}</span>;
}
