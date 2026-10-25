import type { JSX } from 'solid-js';
import { cx } from '@/lib/cx';

export type KbdVariant = 'default' | 'active' | 'invalid' | 'empty';

const VARIANT: Record<KbdVariant, string> = {
  default: 'border-line',
  active: 'animate-pulse border-accent',
  invalid: 'animate-wiggle border-critical bg-critical/20',
  empty: 'border-dashed border-line',
};

export function Kbd(props: {
  variant?: KbdVariant;
  class?: string;
  children?: JSX.Element;
}) {
  return (
    <kbd
      class={cx(
        `
          inline-flex min-h-6 min-w-8 items-center justify-center rounded-sm border bg-surface-well
          px-sm font-sans text-standard font-bold transition-colors
        `,
        VARIANT[props.variant ?? 'default'],
        props.class
      )}
    >
      {props.children}
    </kbd>
  );
}
