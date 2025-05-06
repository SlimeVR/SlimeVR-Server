import { Typography } from '@/components/commons/Typography';
import { useLocaleConfig } from '@/i18n/config';
import { angleIsNearZero } from '@/maths/angle';
import { TrackerDataT } from 'solarxr-protocol';

export function StayAlignedInfo({
  color,
  tracker,
}: {
  color: 'primary' | 'secondary';
  tracker: TrackerDataT;
}) {
  const { currentLocales } = useLocaleConfig();
  const degreeFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'degree',
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  });
  const errorFormat = new Intl.NumberFormat(currentLocales, {
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  });

  const stayAligned = tracker.stayAligned;
  if (!stayAligned) {
    return <></>;
  }

  const locked = stayAligned.locked ? '🔒' : '';

  const delta = `Δ=${degreeFormat.format(stayAligned.yawCorrectionInDeg)}`;

  const errors = [];
  const maxErrorToShow = 0.1;
  if (!angleIsNearZero(stayAligned.lockedErrorInDeg, maxErrorToShow)) {
    errors.push(`L=${errorFormat.format(stayAligned.lockedErrorInDeg)}`);
  }
  if (!angleIsNearZero(stayAligned.centerErrorInDeg, maxErrorToShow)) {
    errors.push(`C=${errorFormat.format(stayAligned.centerErrorInDeg)}`);
  }
  if (!angleIsNearZero(stayAligned.neighborErrorInDeg, maxErrorToShow)) {
    errors.push(`N=${errorFormat.format(stayAligned.neighborErrorInDeg)}`);
  }

  const error = errors.length > 0 ? `(${errors.join(', ')})` : '';

  return (
    <Typography color={color} whitespace="whitespace-nowrap">
      {locked} {delta} {error}
    </Typography>
  );
}
