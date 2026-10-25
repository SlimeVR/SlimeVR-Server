import { type JSX, onMount, Show } from 'solid-js';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { focusEntry, MotionRegion } from '@/ui/motion';
import { Toolbar } from '@/ui/toolbar';

export type PageGap = 'lg' | 'xl' | '2xl';

export interface PageProps {
  title?: MessageId;
  titleArgs?: MessageArgs;
  icon?: JSX.Element;
  actions?: JSX.Element;
  gap?: PageGap;
  children: JSX.Element;
}

const GAP: Record<PageGap, string> = {
  lg: 'gap-lg',
  xl: 'gap-xl',
  '2xl': 'gap-2xl',
};

export function Page(props: PageProps) {
  let el!: HTMLDivElement;

  onMount(() => focusEntry(el));

  return (
    <div
      ref={el}
      class="
        relative w-full min-w-0 rounded-lg bg-surface p-md pb-xl surface-page @lg:p-lg @lg:pb-2xl
      "
    >
      <MotionRegion class="flex w-full min-w-0 flex-col gap-lg @container">
        <Show when={props.title || props.icon || props.actions}>
          <header class="flex flex-wrap items-center gap-lg">
            <Show when={props.icon}>
              <div
                class="
                  flex size-10 flex-none items-center justify-center rounded-full bg-accent-muted
                  text-on-accent icon-md
                "
              >
                {props.icon}
              </div>
            </Show>
            <Show when={props.title}>
              {(title) => (
                <h1 class="text-title">{t(title(), props.titleArgs)}</h1>
              )}
            </Show>
            <Show when={props.actions}>
              <Toolbar class="grow">{props.actions}</Toolbar>
            </Show>
          </header>
        </Show>
        <div class={cx('flex min-w-0 flex-col', GAP[props.gap ?? 'xl'])}>
          {props.children}
        </div>
      </MotionRegion>
    </div>
  );
}
