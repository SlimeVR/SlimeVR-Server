import { createUniqueId, type JSX, Show } from 'solid-js';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { MotionList } from '@/ui/motion';
import { RowScope } from '@/ui/setting-row';

export interface SettingsGroupProps {
  title?: MessageId;
  titleArgs?: MessageArgs;
  description?: MessageId;
  descriptionArgs?: MessageArgs;
  details?: JSX.Element;
  // Inherited by every row inside
  disabled?: boolean;
  disabledReason?: MessageId;
  loading?: boolean;
  children: JSX.Element;
}

// Label and rule, then the rows in one card split by dividers
export function SettingsGroup(props: SettingsGroupProps) {
  const titleId = createUniqueId();

  return (
    <section
      aria-labelledby={props.title && titleId}
      class="flex min-w-0 flex-col gap-md"
    >
      <Show when={props.title || props.description || props.details}>
        <div class="flex min-w-0 flex-col gap-sm px-md">
          <Show when={props.title}>
            {(title) => (
              <div class="flex items-center gap-md uppercase">
                <h2 id={titleId} class="text-section">
                  {t(title(), props.titleArgs)}
                </h2>
                <div class="h-px grow bg-line" />
              </div>
            )}
          </Show>
          <Show when={props.description || props.details}>
            <div class="flex max-w-[75ch] min-w-0 flex-col gap-xs">
              <Show when={props.description}>
                {(description) => (
                  <p>{t(description(), props.descriptionArgs)}</p>
                )}
              </Show>
              {props.details}
            </div>
          </Show>
        </div>
      </Show>
      <RowScope
        disabled={props.disabled}
        disabledReason={props.disabledReason}
        loading={props.loading}
      >
        <MotionList
          class="
            flex flex-col divide-y divide-surface-hover overflow-hidden rounded-lg bg-surface
            @container surface-section
          "
        >
          {props.children}
        </MotionList>
      </RowScope>
    </section>
  );
}

export interface SettingsAdvancedProps {
  title?: MessageId;
  danger?: boolean;
  children: JSX.Element;
}

// Outlined block with the title sitting in a gap of the outline
export function SettingsAdvanced(props: SettingsAdvancedProps) {
  return (
    <fieldset
      class={cx(
        'flex min-w-0 flex-col rounded-lg border px-sm pt-lg pb-xl @container',
        props.danger ? 'border-critical' : 'border-line'
      )}
    >
      <legend class="ml-sm px-sm text-section uppercase">
        {t(props.title ?? 'settings-advanced_section')}
      </legend>
      <MotionList class="flex min-w-0 flex-col gap-md">
        {props.children}
      </MotionList>
    </fieldset>
  );
}
