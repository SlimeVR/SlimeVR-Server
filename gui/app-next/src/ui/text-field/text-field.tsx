import {
  createEffect,
  createSignal,
  createUniqueId,
  on,
  onCleanup,
  Show,
  untrack,
} from 'solid-js';
import EyeIcon from '~icons/heroicons/eye-solid';
import EyeSlashIcon from '~icons/heroicons/eye-slash-solid';
import { cx } from '@/lib/cx';
import { useBack } from '@/lib/back-stack';
import { t, type MessageId } from '@/lib/l10n';
import { Button } from '@/ui/button';
import { fieldBlocked, type FieldProps, fieldState, idList } from '@/ui/field';
import { MotionList, useMotionItem } from '@/ui/motion';
import { Skeleton } from '@/ui/skeleton';
import { Tooltip } from '@/ui/tooltip';

export const TEXT_COMMIT_DEBOUNCE_MS = 500;

export interface TextFieldProps extends FieldProps<string> {
  label?: MessageId;
  placeholder?: MessageId;
  type?: 'text' | 'password';
  autocomplete?: string;
  maxLength?: number;
  sensitive?: boolean;
  class?: string;
}

function TextFieldBody(props: TextFieldProps) {
  let el!: HTMLInputElement;
  const inputId = createUniqueId();
  const labelId = createUniqueId();
  const tipId = createUniqueId();
  const state = () => fieldState(props);
  const reason = () =>
    state() === 'disabled' ? props.disabledReason : undefined;
  const password = () => props.type === 'password';

  const [draft, setDraft] = createSignal(untrack(() => props.value));
  const [focused, setFocused] = createSignal(false);
  const [revealed, setRevealed] = createSignal(false);
  // At rest arrows move focus; editing owns them for the caret
  const [editing, setEditing] = createSignal(false);
  let dirty = false;
  let timer: ReturnType<typeof setTimeout> | undefined;

  useMotionItem(() => el, { owns: () => (editing() ? 'all' : 'none') });

  // Escape and pad B leave editing before they close anything else
  useBack(
    () => {
      revert();
      setEditing(false);
    },
    { when: editing }
  );

  // Server echoes never overwrite what the user is typing
  createEffect(
    on(
      () => props.value,
      (value) => {
        if (!dirty) setDraft(value);
      }
    )
  );

  function commit() {
    clearTimeout(timer);
    if (!dirty) return;
    dirty = false;
    if (draft() !== props.value) props.onChange(draft());
  }

  function revert() {
    clearTimeout(timer);
    dirty = false;
    setDraft(props.value);
  }

  onCleanup(commit);

  function startEditing() {
    if (!fieldBlocked(state())) setEditing(true);
  }

  function onInput(e: InputEvent & { currentTarget: HTMLInputElement }) {
    if (fieldBlocked(state())) return;
    setEditing(true);
    setDraft(e.currentTarget.value);
    dirty = true;
    clearTimeout(timer);
    timer = setTimeout(commit, TEXT_COMMIT_DEBOUNCE_MS);
  }

  // Keyboard and pad still reach a disabled field (needed for tooltip), a click does not focus it
  function onMouseDown(e: MouseEvent) {
    if (fieldBlocked(state())) e.preventDefault();
    else startEditing();
  }

  // Pad A arrives as a synthetic Enter
  function onKeyDown(e: KeyboardEvent) {
    if (e.key === 'Enter') {
      if (editing()) {
        commit();
        setEditing(false);
        return;
      }
      e.preventDefault();
      startEditing();
      return;
    }
    if (editing() || e.ctrlKey || e.metaKey || e.altKey) return;
    // At rest any typing key starts editing and still types
    if (
      e.key === 'F2' ||
      e.key.length === 1 ||
      e.key === 'Backspace' ||
      e.key === 'Delete'
    )
      startEditing();
  }

  // A blocked field never floats on focus, it must not look editable
  const loading = () => state() === 'loading';

  const floating = () =>
    loading() || (focused() && !fieldBlocked(state())) || draft().length > 0;
  const placeholder = () => {
    if (!props.placeholder || loading()) return undefined;
    if (props.label && !floating()) return undefined;
    return t(props.placeholder);
  };

  return (
    <>
      <input
        ref={el}
        id={props.id ?? inputId}
        type={password() && !revealed() ? 'password' : 'text'}
        value={draft()}
        placeholder={placeholder()}
        autocomplete={props.autocomplete ?? 'off'}
        maxLength={props.maxLength}
        readOnly={fieldBlocked(state())}
        aria-labelledby={idList(props.labelledBy, props.label && labelId)}
        aria-describedby={idList(props.describedBy, reason() && tipId)}
        aria-disabled={state() === 'disabled' ? 'true' : undefined}
        aria-invalid={state() === 'error' ? 'true' : undefined}
        class={cx(
          `
            peer min-h-control w-full rounded-md bg-surface-control px-3 text-standard text-fg
            placeholder:text-fg-muted placeholder:italic aria-disabled:cursor-not-allowed
            aria-disabled:opacity-50 aria-invalid:ring-1 aria-invalid:ring-critical
          `,
          loading() && 'text-transparent',
          !editing() && 'caret-transparent',
          props.label && 'pt-[1.15rem] pb-[0.15rem]',
          password() && 'pr-12',
          (password() || props.sensitive) && 'sentry-mask'
        )}
        onFocus={() => setFocused(true)}
        onBlur={() => {
          setFocused(false);
          setEditing(false);
          commit();
        }}
        onInput={onInput}
        onKeyDown={onKeyDown}
        onMouseDown={onMouseDown}
      />
      <Show when={props.label}>
        {(label) => (
          <span
            id={labelId}
            class={cx(
              'pointer-events-none absolute left-3 text-standard text-fg transition-all',
              'duration-150 ease-out peer-aria-disabled:opacity-50',
              floating()
                ? 'top-2 origin-left scale-[0.8] font-bold'
                : 'top-1/2 -translate-y-1/2 italic'
            )}
          >
            {t(label())}
          </span>
        )}
      </Show>
      <Show when={loading()}>
        <Skeleton
          class={cx(
            'pointer-events-none absolute left-3 w-1/3',
            props.label ? 'bottom-[0.6rem]' : 'top-1/2 -translate-y-1/2'
          )}
        />
      </Show>
      <Show when={password() && !loading()}>
        <span class="absolute inset-y-0 right-1.5 flex items-center">
          <Button
            variant="ghost"
            size="small"
            iconOnly
            icon={revealed() ? <EyeSlashIcon /> : <EyeIcon />}
            label={revealed() ? 'input-password-hide' : 'input-password-show'}
            pressed={revealed()}
            disabled={state() === 'disabled'}
            onClick={() => setRevealed((on) => !on)}
          />
        </span>
      </Show>
      <Tooltip id={tipId} anchor={() => el} text={reason()} />
    </>
  );
}

// The reveal button sits inside the input box, so geometry would never reach it:
// input and button are steps of one horizontal list. The input registers from
// TextFieldBody, inside the list, since useMotionItem reads the enclosing container.
export function TextField(props: TextFieldProps) {
  return (
    <MotionList
      orientation="horizontal"
      entry="first"
      class={cx('relative', props.class)}
      inert={fieldState(props) === 'loading'}
    >
      <TextFieldBody {...props} />
    </MotionList>
  );
}
