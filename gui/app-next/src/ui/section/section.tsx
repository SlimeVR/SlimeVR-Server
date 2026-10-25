import { createUniqueId, type JSX, Show } from 'solid-js';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { MotionList } from '@/ui/motion';
import { Toolbar } from '@/ui/toolbar';

export interface SectionProps {
  title?: MessageId;
  titleArgs?: MessageArgs;
  description?: MessageId;
  descriptionArgs?: MessageArgs;
  actions?: JSX.Element;
  class?: string;
  children: JSX.Element;
}

export function Section(props: SectionProps) {
  const titleId = createUniqueId();

  return (
    <section
      aria-labelledby={props.title && titleId}
      class={cx(
        'flex min-w-0 flex-col gap-md rounded-lg bg-surface p-lg @container surface-section',
        props.class
      )}
    >
      <Show when={props.title || props.description || props.actions}>
        <header class="flex min-w-0 flex-col gap-xs">
          <div class="flex flex-wrap items-center gap-md">
            <Show when={props.title}>
              {(title) => (
                <h2 id={titleId} class="grow text-section">
                  {t(title(), props.titleArgs)}
                </h2>
              )}
            </Show>
            <Show when={props.actions}>
              <Toolbar>{props.actions}</Toolbar>
            </Show>
          </div>
          <Show when={props.description}>
            {(description) => (
              <p class="max-w-[75ch]">
                {t(description(), props.descriptionArgs)}
              </p>
            )}
          </Show>
        </header>
      </Show>
      <MotionList class="flex min-w-0 flex-col gap-md">
        {props.children}
      </MotionList>
    </section>
  );
}
