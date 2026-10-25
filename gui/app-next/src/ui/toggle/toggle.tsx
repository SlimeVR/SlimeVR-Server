import { createUniqueId, Show } from 'solid-js';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { fieldBlocked, type FieldProps, fieldState, idList } from '@/ui/field';
import { useMotionItem } from '@/ui/motion';
import { TextTooltip } from '@/ui/tooltip';

export interface ToggleProps extends FieldProps<boolean> {
  label?: MessageId;
  args?: MessageArgs;
}

export function Toggle(props: ToggleProps) {
  let el!: HTMLButtonElement;
  const tipId = createUniqueId();
  const state = () => fieldState(props);
  const reason = () =>
    state() === 'disabled' ? props.disabledReason : undefined;
  useMotionItem(() => el);

  function onClick() {
    if (fieldBlocked(state())) return;
    props.onChange(!props.value);
  }

  return (
    <>
      <label
        class={cx(
          'inline-flex items-center gap-sm font-bold',
          props.label && 'min-h-control cursor-pointer',
          'has-aria-disabled:cursor-not-allowed has-aria-disabled:opacity-50'
        )}
      >
        <button
          ref={el}
          type="button"
          role="switch"
          id={props.id}
          aria-checked={state() !== 'loading' && props.value}
          aria-labelledby={props.labelledBy}
          aria-describedby={idList(props.describedBy, reason() && tipId)}
          aria-disabled={state() === 'disabled' ? 'true' : undefined}
          aria-invalid={state() === 'error' ? 'true' : undefined}
          inert={state() === 'loading'}
          class="
            group relative h-4 w-10 shrink-0 rounded-full bg-surface-control transition-colors
            aria-checked:bg-accent aria-disabled:cursor-not-allowed aria-invalid:ring-1
            aria-invalid:ring-critical inert:animate-pulse inert:bg-line
          "
          onClick={onClick}
        >
          <span
            class="
              absolute top-1 left-1 size-2 rounded-full bg-fg transition-[left]
              group-aria-checked:left-7 inert:opacity-0
            "
          />
        </button>
        <Show when={props.label}>
          {(label) => <span>{t(label(), props.args)}</span>}
        </Show>
      </label>
      <TextTooltip id={tipId} anchor={() => el} text={reason()} />
    </>
  );
}
