import { useState } from 'react';
import { BodyPart } from 'solarxr-protocol';

/**
 * I dedicate this hook to @uriel ;)
 */
export function useChokerWarning<T>({
  next,
  getBodyPart = (value: T) => value as unknown as BodyPart,
}: {
  next: (role: T) => void;
  getBodyPart?: (value: T) => BodyPart;
}) {
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
      if (
        getBodyPart(role) === BodyPart.NECK &&
        !sessionStorage.getItem('neckWarning')
      ) {
        setCurrentBodyPart(role);
        setShouldShowChokerWarn(true);
      } else {
        next(role);
      }
    },
  };
}
