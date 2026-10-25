import { createUniqueId, Show } from 'solid-js';
import CheckIcon from '~icons/material-symbols/check-rounded';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { fieldBlocked, type FieldProps, fieldState, idList } from '@/ui/field';
import { useMotionItem } from '@/ui/motion';
import { TextTooltip } from '@/ui/tooltip';

export interface CheckboxProps extends FieldProps<boolean> {
  label?: MessageId;
  args?: MessageArgs;
}

export function Checkbox(props: CheckboxProps) {
  let el!: HTMLInputElement;
  const tipId = createUniqueId();
  const state = () => fieldState(props);
  const reason = () =>
    state() === 'disabled' ? props.disabledReason : undefined;
  useMotionItem(() => el);

  function onClick(e: MouseEvent) {
    if (fieldBlocked(state())) e.preventDefault();
  }

  function onChange(e: Event & { currentTarget: HTMLInputElement }) {
    const next = e.currentTarget.checked;
    e.currentTarget.checked = props.value;
    props.onChange(next);
  }

  return (
    <>
      <label
        class={cx(
          'inline-flex items-center gap-sm font-bold',
          'has-aria-disabled:cursor-not-allowed has-aria-disabled:opacity-50',
          props.label && 'min-h-control'
        )}
        inert={state() === 'loading'}
      >
        <span class="relative inline-grid size-5 shrink-0">
          <input
            ref={el}
            type="checkbox"
            id={props.id}
            checked={state() !== 'loading' && props.value}
            aria-labelledby={props.labelledBy}
            aria-describedby={idList(props.describedBy, reason() && tipId)}
            aria-disabled={state() === 'disabled' ? 'true' : undefined}
            aria-invalid={state() === 'error' ? 'true' : undefined}
            class="
              peer col-start-1 row-start-1 size-5 cursor-pointer appearance-none rounded-md
              bg-surface-control checked:bg-accent aria-disabled:cursor-not-allowed
              aria-invalid:ring-1 aria-invalid:ring-critical in-[[inert]]:animate-pulse
              in-[[inert]]:bg-line
            "
            onClick={onClick}
            onChange={onChange}
          />
          <span
            class="
              pointer-events-none col-start-1 row-start-1 hidden place-self-center text-on-accent
              icon-sm peer-checked:flex
            "
          >
            <CheckIcon />
          </span>
        </span>
        <Show when={props.label}>
          {(label) => <span>{t(label(), props.args)}</span>}
        </Show>
      </label>
      <TextTooltip id={tipId} anchor={() => el} text={reason()} />
    </>
  );
}
