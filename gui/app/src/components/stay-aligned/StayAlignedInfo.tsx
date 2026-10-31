import { Typography } from '@/components/commons/Typography';
import { useLocaleConfig } from '@/i18n/config';
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

  const stayAligned = tracker.stayAligned;
  if (!stayAligned) {
    return <></>;
  }

  const locked = stayAligned.locked ? '🔒' : '';
  const delta = degreeFormat.format(stayAligned.yawCorrectionInDeg);

  return (
    <Typography color={color} whitespace="whitespace-nowrap">
      {locked} {delta}
    </Typography>
  );
}
