import { cx } from '@/lib/cx';

// Pulsing placeholder for a value that has not loaded yet. Callers size it
// (width, and height when it is not a text line).
export function Skeleton(props: { class?: string }) {
  return (
    <span
      aria-hidden="true"
      class={cx('block h-3 animate-pulse rounded-sm bg-line', props.class)}
    />
  );
}
