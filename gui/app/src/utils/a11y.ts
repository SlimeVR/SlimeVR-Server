import { error } from './logging';

// The default focus style lives in tailwind.config.ts (addBase, a global
// :focus-visible outline). These two cover cases that a global rule can't:
// FOCUS_RING_PEER  - the real focus target is a visually-hidden `peer` input.
// FOCUS_RING_WITHIN - the ring belongs on a wrapper, keyed off a focused child.
// The `.controller-active` variants drop the `-visible` requirement, since
// gamepad navigation focuses programmatically and never sets :focus-visible.
export const FOCUS_RING_PEER =
  'peer-focus:ring-0 peer-focus-visible:outline-none peer-focus-visible:ring-2 peer-focus-visible:ring-accent-background-10 ' +
  '[.controller-active_&]:peer-focus:ring-2 [.controller-active_&]:peer-focus:ring-accent-background-10';
export const FOCUS_RING_WITHIN =
  'has-[:focus-visible]:ring-2 has-[:focus-visible]:ring-accent-background-10 ' +
  '[.controller-active_&]:has-[:focus]:ring-2 [.controller-active_&]:has-[:focus]:ring-accent-background-10';

export function a11yClick(event: React.KeyboardEvent | React.MouseEvent): boolean {
  if (event.type === 'click') {
    return true;
  } else if (event.type === 'keydown') {
    const keyboard = event as React.KeyboardEvent;
    return keyboard.key === 'Enter' || keyboard.key === ' ';
  }
  return false;
}

/** Number, or a custom stepper returning the next value from the current one. */
export type Step = number | ((value: number, add: boolean) => number);

/**
 * Arrow / Home / End / PageUp / PageDown handling for numeric widgets. Clamps
 * to `[min, max]` and only ever hands `onChange` a number. Without a `bigStep`,
 * PageUp and PageDown fall back to `step`.
 */
export function stepKeys({
  value,
  min,
  max,
  step,
  bigStep,
  onChange,
  disabled,
}: {
  value: number;
  min: number;
  max: number;
  step: Step;
  bigStep?: Step;
  onChange: (value: number) => void;
  disabled?: boolean;
}) {
  const apply = (by: Step, add: boolean) =>
    typeof by === 'number' ? value + (add ? by : -by) : by(value, add);

  return (e: React.KeyboardEvent) => {
    if (disabled) return;

    let next: number;
    switch (e.key) {
      case 'ArrowUp':
      case 'ArrowRight':
        next = apply(step, true);
        break;
      case 'ArrowDown':
      case 'ArrowLeft':
        next = apply(step, false);
        break;
      case 'PageUp':
        next = apply(bigStep ?? step, true);
        break;
      case 'PageDown':
        next = apply(bigStep ?? step, false);
        break;
      case 'Home':
        next = min;
        break;
      case 'End':
        next = max;
        break;
      default:
        return;
    }

    e.preventDefault();
    onChange(Math.min(max, Math.max(min, +next.toFixed(4))));
  };
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
