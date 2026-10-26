import { createUniqueId, type JSX, Show } from 'solid-js';
import { cx } from '@/lib/cx';
import { t, type MessageId } from '@/lib/l10n';
import type { SettingsGroupDef } from '@/lib/settings-def';
import { MotionList } from '@/ui/motion';
import { RowScope } from '@/ui/setting-row';

export interface SettingsGroupProps {
  // Title and description come from the declaration
  group?: SettingsGroupDef;
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
  const title = () => props.group?.title;
  const description = () => props.group?.description;

  return (
    <section
      aria-labelledby={title() && titleId}
      class="flex min-w-0 flex-col gap-md"
    >
      <Show when={title() || description() || props.details}>
        <div class="flex min-w-0 flex-col gap-sm px-md">
          <Show when={title()}>
            {(title) => (
              <div class="flex items-center gap-md uppercase">
                <h2 id={titleId} class="text-section">
                  {t(title())}
                </h2>
                <div class="h-px grow bg-line" />
              </div>
            )}
          </Show>
          <Show when={description() || props.details}>
            <div class="flex max-w-[75ch] min-w-0 flex-col gap-xs">
              <Show when={description()}>
                {(description) => <p>{t(description())}</p>}
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
