import { useState } from 'react';
import { BodyPart } from 'solarxr-protocol';

/**
 * I dedicate this hook to @uriel ;)
 */
export function useChockerWarning<T>({ next }: { next: (role: T) => void }) {
  const [shouldShowChockerWarn, setShouldShowChockerWarn] = useState(false);
  const [currentBodyPart, setCurrentBodyPart] = useState<T | null>(null);

  return {
    shouldShowChockerWarn,
    closeChockerWarning: (cancel: boolean) => {
      setShouldShowChockerWarn(false);
      if (!cancel) {
        sessionStorage.setItem('neckWarning', 'true');
        if (currentBodyPart) next(currentBodyPart);
      }
    },
    tryOpenChockerWarning: (role: T) => {
      if (role === BodyPart.NECK && !sessionStorage.getItem('neckWarning')) {
        setCurrentBodyPart(role);
        setShouldShowChockerWarn(true);
      } else {
        next(role);
      }
    },
  };
}
