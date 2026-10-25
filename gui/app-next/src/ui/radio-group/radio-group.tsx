import { createUniqueId, For, type JSX, Show } from 'solid-js';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { fieldBlocked, type FieldProps, fieldState, idList } from '@/ui/field';
import {
  type Direction,
  move,
  type MotionOwns,
  useMotionItem,
} from '@/ui/motion';
import { TextTooltip } from '@/ui/tooltip';

export interface ChoiceOption<T> {
  value: T;
  label: MessageId;
  args?: MessageArgs;
  description?: MessageId;
}

export interface ChoiceProps<T> extends FieldProps<T> {
  options: readonly ChoiceOption<T>[];
  class?: string;
}

type Step = 'prev' | 'next' | 'first' | 'last';

const HORIZONTAL_KEYS: Record<string, Step | undefined> = {
  ArrowLeft: 'prev',
  ArrowRight: 'next',
  Home: 'first',
  End: 'last',
};

const ALL_KEYS: Record<string, Step | undefined> = {
  ...HORIZONTAL_KEYS,
  ArrowUp: 'prev',
  ArrowDown: 'next',
};

const KEY_DIR: Record<string, Direction | undefined> = {
  ArrowUp: 'up',
  ArrowDown: 'down',
  ArrowLeft: 'left',
  ArrowRight: 'right',
};

// Roving tabindex: only the selected option is tabbable, and motion skips the
// others, so Tab and directional moves both land on the selection.
// Arrows select (no wrap), past the ends they move out of the group.
function onOptionKey<T>(
  e: KeyboardEvent & { currentTarget: HTMLElement },
  props: ChoiceProps<T>,
  keys: Record<string, Step | undefined>
) {
  const step = keys[e.key];
  if (!step || e.ctrlKey || e.altKey || e.metaKey || e.shiftKey) return;
  e.preventDefault();
  const group = e.currentTarget.closest('[role="radiogroup"]');
  const radios = [
    ...(group?.querySelectorAll<HTMLElement>('[role="radio"]') ?? []),
  ];
  const i = radios.indexOf(e.currentTarget);
  let target = i + (step === 'prev' ? -1 : 1);
  if (step === 'first') target = 0;
  if (step === 'last') target = radios.length - 1;
  const option = props.options[target];
  const dir = KEY_DIR[e.key];
  if (fieldBlocked(fieldState(props)) || !option) {
    if (dir) move(dir);
    return;
  }
  if (target === i) return;
  radios[target]!.focus();
  props.onChange(option.value);
}

function ChoiceButton<T>(props: {
  field: ChoiceProps<T>;
  option: ChoiceOption<T>;
  tabbable: boolean;
  owns: MotionOwns;
  keys: Record<string, Step | undefined>;
  class: string;
  children: JSX.Element;
}) {
  let el!: HTMLButtonElement;
  useMotionItem(() => el, { owns: () => props.owns });
  const selected = () =>
    fieldState(props.field) !== 'loading' &&
    props.option.value === props.field.value;

  function onClick() {
    if (fieldBlocked(fieldState(props.field))) return;
    props.field.onChange(props.option.value);
  }

  return (
    <button
      ref={el}
      type="button"
      role="radio"
      aria-checked={selected()}
      aria-disabled={
        fieldState(props.field) === 'disabled' ? 'true' : undefined
      }
      tabIndex={props.tabbable ? 0 : -1}
      class={props.class}
      onClick={onClick}
      onKeyDown={(e) => onOptionKey(e, props.field, props.keys)}
    >
      {props.children}
    </button>
  );
}

function renderGroup<T>(
  props: ChoiceProps<T>,
  layout: {
    owns: MotionOwns;
    keys: Record<string, Step | undefined>;
    class: string;
  },
  renderOption: (option: ChoiceOption<T>) => {
    class: string;
    children: JSX.Element;
  }
) {
  let el!: HTMLDivElement;
  const tipId = createUniqueId();
  const state = () => fieldState(props);
  const reason = () =>
    state() === 'disabled' ? props.disabledReason : undefined;
  const tabbable = () =>
    Math.max(
      0,
      props.options.findIndex((o) => o.value === props.value)
    );

  return (
    <>
      <div
        ref={el}
        role="radiogroup"
        id={props.id}
        aria-labelledby={props.labelledBy}
        aria-describedby={idList(props.describedBy, reason() && tipId)}
        aria-disabled={state() === 'disabled' ? 'true' : undefined}
        aria-invalid={state() === 'error' ? 'true' : undefined}
        inert={state() === 'loading'}
        class={cx(layout.class, props.class)}
      >
        <For each={props.options}>
          {(option, i) => {
            const rendered = renderOption(option);
            return (
              <ChoiceButton
                field={props}
                option={option}
                tabbable={i() === tabbable()}
                owns={layout.owns}
                keys={layout.keys}
                class={rendered.class}
              >
                {rendered.children}
              </ChoiceButton>
            );
          }}
        </For>
      </div>
      <TextTooltip id={tipId} anchor={() => el} text={reason()} />
    </>
  );
}

// Stacked option cards with an optional description
export function RadioGroup<T>(props: ChoiceProps<T>) {
  return renderGroup(
    props,
    {
      owns: 'all',
      keys: ALL_KEYS,
      class: `
        flex flex-col gap-sm rounded-md aria-disabled:opacity-50
        aria-invalid:ring-1 aria-invalid:ring-critical
      `,
    },
    (option) => ({
      class: `
        group flex w-full items-start gap-md rounded-md border-2 border-transparent
        bg-surface-control p-3 text-left
        not-aria-disabled:hover:bg-surface-hover
        aria-checked:border-accent
        aria-disabled:cursor-not-allowed
      `,
      children: (
        <>
          <span
            class="
              mt-0.5 flex size-4 shrink-0 items-center justify-center rounded-full border-2
              border-fg-muted group-aria-checked:border-accent in-[[inert]]:animate-pulse
              in-[[inert]]:border-transparent in-[[inert]]:bg-line
            "
          >
            <span class="size-2 rounded-full bg-accent opacity-0 group-aria-checked:opacity-100" />
          </span>
          <span class="flex flex-col gap-xs">
            <span class="font-bold">{t(option.label, option.args)}</span>
            <Show when={option.description}>
              {(description) => <span>{t(description(), option.args)}</span>}
            </Show>
          </span>
        </>
      ),
    })
  );
}

// Options side by side in one track
export function Segmented<T>(props: ChoiceProps<T>) {
  return renderGroup(
    props,
    {
      owns: 'horizontal',
      keys: HORIZONTAL_KEYS,
      class: `
        flex min-h-control min-w-0 flex-wrap items-stretch gap-0.5 rounded-md bg-surface-well p-1
        aria-disabled:opacity-50
        aria-invalid:ring-1 aria-invalid:ring-critical
      `,
    },
    (option) => ({
      class: `
        flex min-w-0 items-center rounded px-3.5 py-1.5 font-bold text-fg-muted transition-colors
        in-[[inert]]:animate-pulse in-[[inert]]:bg-fg/15 in-[[inert]]:text-transparent
        not-aria-disabled:hover:text-fg
        aria-checked:bg-accent aria-checked:text-on-accent
        aria-disabled:cursor-not-allowed
      `,
      children: <span class="min-w-0">{t(option.label, option.args)}</span>,
    })
  );
}
