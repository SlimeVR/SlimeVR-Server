import { error } from './logging';

export function a11yClick(event: React.KeyboardEvent | React.MouseEvent) {
  if (event.type === 'click') {
    return true;
  }

  if (event.type === 'keydown') {
    const keyboard = event as React.KeyboardEvent;
    return keyboard.key === 'Enter' || keyboard.key === ' ';
  }

  return false;
}

export function waitUntil(
  condition: (() => boolean) | (() => Promise<boolean>),
  time: number,
  tries?: number
): Promise<void> {
  let remaining = tries;
  return new Promise((resolve, rej) => {
    const isPromise = typeof condition() !== 'boolean';
    const interval = setInterval(() => {
      if (remaining && --remaining === 0) {
        error(new Error('waitUntil ran out of tries'));
        clearInterval(interval);
        resolve();
      }
      const boolPromise = condition();
      if (!isPromise && boolPromise) {
        clearInterval(interval);
        resolve();
      } else if (isPromise) {
        (boolPromise as Promise<boolean>)
          .then((bool) => {
            if (!bool) return;
            clearInterval(interval);
            resolve();
          })
          .catch(rej);
      }
    }, time);
  });
}
