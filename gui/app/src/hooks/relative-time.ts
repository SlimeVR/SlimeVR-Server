import { useCallback, useEffect, useMemo, useState } from 'react';
import { useLocaleConfig } from '@/i18n/config';

export function useRelativeTime(refreshMs = 1_000) {
  const { currentLocales } = useLocaleConfig();
  const [now, setNow] = useState(() => Date.now());

  useEffect(() => {
    const interval = window.setInterval(() => setNow(Date.now()), refreshMs);
    return () => window.clearInterval(interval);
  }, [refreshMs]);

  const format = useMemo(
    () => new Intl.RelativeTimeFormat(currentLocales, { numeric: 'auto' }),
    [currentLocales]
  );

  return useCallback(
    (timestamp: bigint | null | undefined) => {
      if (!timestamp) return null;

      const elapsedSeconds = Math.max(0, Math.floor((now - Number(timestamp)) / 1000));
      if (elapsedSeconds < 60) return format.format(-elapsedSeconds, 'second');

      const elapsedMinutes = Math.floor(elapsedSeconds / 60);
      if (elapsedMinutes < 60) return format.format(-elapsedMinutes, 'minute');

      return format.format(-Math.floor(elapsedMinutes / 60), 'hour');
    },
    [format, now]
  );
}
