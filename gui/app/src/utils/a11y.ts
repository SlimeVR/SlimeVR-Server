import { error } from './logging';

// The default focus style lives in tailwind.config.ts (addBase, a global
// :focus-visible outline). These two cover cases that a global rule can't:
// FOCUS_RING_PEER  - the real focus target is a visually-hidden `peer` input.
// FOCUS_RING_WITHIN - the ring belongs on a wrapper, keyed off a focused child.
export const FOCUS_RING_PEER =
  'peer-focus:ring-0 peer-focus-visible:outline-none peer-focus-visible:ring-2 peer-focus-visible:ring-accent-background-10';
export const FOCUS_RING_WITHIN =
  'has-[:focus-visible]:ring-2 has-[:focus-visible]:ring-accent-background-10';

export function a11yClick(event: React.KeyboardEvent | React.MouseEvent) {
  if (event.type === 'click') {
    return true;
  } else if (event.type === 'keydown') {
    const keyboard = event as React.KeyboardEvent;
    return keyboard.key === 'Enter' || keyboard.key === ' ';
  }
}

export function waitUntil(
  condition: (() => boolean) | (() => Promise<boolean>),
  time: number,
  tries?: number
): Promise<void> {
  return new Promise((resolve, rej) => {
    const isPromise = typeof condition() !== 'boolean';
    const interval = setInterval(() => {
      if (tries && --tries === 0) {
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
