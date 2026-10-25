import { type JSX, Show } from 'solid-js';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { Localized } from '@/ui/localized';

export type StatusKind = 'loading' | 'error' | 'empty' | 'success';
export type StatusSize = 'page' | 'panel' | 'inline';

const SLIME: Record<StatusKind, string> = {
  loading: '/images/jumping-slime.webm',
  error: '/images/sad-slime.webm',
  empty: '/images/curious-slime.webm',
  success: '/images/happy-slime.webm',
};

const LAYOUT: Record<StatusSize, string> = {
  inline: 'flex-row gap-sm',
  panel: 'flex-col justify-center gap-lg p-xl',
  page: 'min-h-full flex-1 flex-col justify-center gap-lg p-2xl',
};

const SLIME_PX: Record<StatusSize, number> = {
  page: 128,
  panel: 85,
  inline: 32,
};

export interface StatusStateProps {
  kind: StatusKind;
  size?: StatusSize;
  title?: MessageId;
  description?: MessageId;
  args?: MessageArgs;
  glow?: boolean;
  action?: JSX.Element;
}

function reducedMotion(): boolean {
  return (
    window.matchMedia?.('(prefers-reduced-motion: reduce)').matches ?? false
  );
}

// Loading, error, empty and success screens. `kind` picks the slime.
export function StatusState(props: StatusStateProps) {
  const size = () => props.size ?? 'panel';
  const px = () => SLIME_PX[size()];

  const slime = () => (
    <video
      aria-hidden="true"
      src={SLIME[props.kind]}
      autoplay={!reducedMotion()}
      loop
      muted
      playsinline
      class="relative aspect-square bg-transparent"
      style={{ width: `${px()}px` }}
    />
  );

  return (
    <div
      role={props.kind === 'loading' ? 'status' : undefined}
      aria-label={
        props.kind === 'loading' && !props.title
          ? t('status-loading')
          : undefined
      }
      class={cx(
        'flex items-center overflow-hidden rounded-[inherit] text-center',
        LAYOUT[size()]
      )}
    >
      <Show when={props.glow && size() !== 'inline'} fallback={slime()}>
        <div class="relative flex items-center justify-center">
          <div
            class="
              pointer-events-none absolute aspect-square rounded-full bg-radial from-accent/25
              to-accent/0 to-70%
            "
            style={{ width: `${px() * 3}px` }}
          />
          {slime()}
        </div>
      </Show>
      <Show when={props.title || props.description}>
        <div class="flex max-w-xs flex-col gap-xs">
          <Show when={props.title}>
            {(title) => <p class="text-section">{t(title())}</p>}
          </Show>
          <Show when={props.description}>
            {(description) => (
              <p>
                <Localized id={description()} args={props.args} />
              </p>
            )}
          </Show>
        </div>
      </Show>
      {props.action}
    </div>
  );
}
