import { useState } from 'react';
import { BodyPart } from 'solarxr-protocol';

/**
 * I dedicate this hook to @uriel ;)
 */
export function useChokerWarning<T>({ next }: { next: (role: T) => void }) {
  const [shouldShowChokerWarn, setShouldShowChokerWarn] = useState(false);
  const [currentBodyPart, setCurrentBodyPart] = useState<T | null>(null);

  return {
    shouldShowChokerWarn,
    closeChokerWarning: (cancel: boolean) => {
      setShouldShowChokerWarn(false);
      if (!cancel) {
        sessionStorage.setItem('neckWarning', 'true');
        if (currentBodyPart) next(currentBodyPart);
      }
    },
    tryOpenChokerWarning: (role: T) => {
      if (role === BodyPart.NECK && !sessionStorage.getItem('neckWarning')) {
        setCurrentBodyPart(role);
        setShouldShowChokerWarn(true);
      } else {
        next(role);
      }
    },
  };
}
