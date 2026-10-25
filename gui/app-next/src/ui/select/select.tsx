import {
  createEffect,
  createSignal,
  createUniqueId,
  For,
  type JSX,
  on,
  onCleanup,
  Show,
} from 'solid-js';
import CheckIcon from '~icons/material-symbols/check-rounded';
import CollapseIcon from '~icons/material-symbols/keyboard-arrow-up-rounded';
import ExpandIcon from '~icons/material-symbols/keyboard-arrow-down-rounded';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { fieldBlocked, type FieldProps, fieldState, idList } from '@/ui/field';
import { useMotionItem } from '@/ui/motion';
import { Popover, type PopoverPlacement } from '@/ui/popover';
import { Skeleton } from '@/ui/skeleton';
import { TextTooltip } from '@/ui/tooltip';

export type SelectOption<T> = {
  render?: () => JSX.Element;
} & (
  | { value: T; label: MessageId; args?: MessageArgs; text?: undefined }
  | { value: T; text: string; label?: undefined; args?: undefined }
);

interface SelectCommon<T> {
  options: readonly SelectOption<T>[];
  label?: MessageId;
  placeholder?: MessageId;
  placement?: PopoverPlacement;
  class?: string;
}

export interface SelectProps<T> extends FieldProps<T>, SelectCommon<T> {
  renderValue?: (option: SelectOption<T>) => JSX.Element;
}

export interface MultiSelectProps<T> extends FieldProps<T[]>, SelectCommon<T> {
  renderValue?: (selected: SelectOption<T>[]) => JSX.Element;
}

export const TYPEAHEAD_RESET_MS = 500;

export function optionText<T>(option: SelectOption<T>): string {
  return option.text ?? t(option.label!, option.args);
}

type Action = 'all' | 'none';
type NavEntry = Action | number;

interface SelectBoxProps<T> {
  field: Omit<FieldProps<unknown>, 'value' | 'onChange'> & SelectCommon<T>;
  shown: () => JSX.Element | undefined;
  isSelected: (value: T) => boolean;
  pick: (value: T) => void;
  multi?: { all: () => void; none: () => void };
}

function SelectBox<T>(props: SelectBoxProps<T>) {
  let trigger!: HTMLButtonElement;
  const listId = createUniqueId();
  const labelId = createUniqueId();
  const tipId = createUniqueId();
  const state = () => fieldState(props.field);
  const reason = () =>
    state() === 'disabled' ? props.field.disabledReason : undefined;

  const [open, setOpen] = createSignal(false);
  const [active, setActive] = createSignal(0);
  // Opened by a pointer: the active option stays unmarked until a key moves it
  const [keyed, setKeyed] = createSignal(false);
  let typed = '';
  let typedTimer: ReturnType<typeof setTimeout> | undefined;
  onCleanup(() => clearTimeout(typedTimer));

  useMotionItem(() => trigger, { owns: () => (open() ? 'all' : 'none') });

  const nav = (): NavEntry[] => [
    ...(props.multi ? (['all', 'none'] as const) : []),
    ...props.field.options.map((_, i) => i),
  ];
  const entryId = (entry: NavEntry | undefined) =>
    entry === undefined ? undefined : `${listId}-${entry}`;

  createEffect(() => {
    if (fieldBlocked(state())) setOpen(false);
  });

  createEffect(
    on([open, active], ([isOpen, index]) => {
      if (!isOpen) return;
      const id = entryId(nav()[index]);
      if (id)
        document.getElementById(id)?.scrollIntoView?.({ block: 'nearest' });
    })
  );

  function show(byKey: boolean) {
    if (fieldBlocked(state())) return;
    setKeyed(byKey);
    const selected = props.field.options.findIndex((o) =>
      props.isSelected(o.value)
    );
    const first = props.multi ? 2 : 0;
    setActive(first + Math.max(0, selected));
    setOpen(true);
  }

  function activate(entry: NavEntry | undefined) {
    if (fieldBlocked(state())) return;
    if (entry === 'all') props.multi?.all();
    else if (entry === 'none') props.multi?.none();
    else if (entry !== undefined) {
      const option = props.field.options[entry];
      if (!option) return;
      props.pick(option.value);
      if (!props.multi) setOpen(false);
    }
  }

  function typeahead(key: string) {
    clearTimeout(typedTimer);
    typedTimer = setTimeout(() => (typed = ''), TYPEAHEAD_RESET_MS);
    typed += key.toLowerCase();
    const entries = nav();

    const cycling = typed.length > 1 && [...typed].every((c) => c === typed[0]);
    const prefix = cycling ? typed[0]! : typed;
    const from = active() + (cycling || typed.length === 1 ? 1 : 0);
    for (let n = 0; n < entries.length; n++) {
      const entry = entries[(from + n) % entries.length];
      if (typeof entry !== 'number') continue;
      const option = props.field.options[entry]!;
      if (optionText(option).toLowerCase().startsWith(prefix)) {
        setActive((from + n) % entries.length);
        return;
      }
    }
  }

  function onKeyDown(e: KeyboardEvent) {
    if (e.ctrlKey || e.altKey || e.metaKey) return;
    if (!open()) {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        show(true);
      }
      return;
    }
    const last = nav().length - 1;
    switch (e.key) {
      case 'ArrowUp':
      case 'ArrowLeft':
        setActive((i) => Math.max(0, i - 1));
        break;
      case 'ArrowDown':
      case 'ArrowRight':
        setActive((i) => Math.min(last, i + 1));
        break;
      case 'Home':
      case 'PageUp':
        setActive(0);
        break;
      case 'End':
      case 'PageDown':
        setActive(last);
        break;
      case 'Enter':
        activate(nav()[active()]);
        break;
      case ' ':
        if (typed) typeahead(' ');
        else activate(nav()[active()]);
        break;
      case 'Tab':
        setOpen(false);
        return;
      default:
        if (e.key.length !== 1) return;
        typeahead(e.key);
    }
    setKeyed(true);
    e.preventDefault();
  }

  function onClick() {
    if (open()) setOpen(false);
    else show(false);
  }

  // Rows are never focused (activedescendant), one listener serves them all
  function onListClick(e: MouseEvent) {
    const row = (e.target as Element).closest<HTMLElement>('[data-entry]');
    const entry = row?.dataset.entry;
    if (entry === undefined) return;
    activate(entry === 'all' || entry === 'none' ? entry : Number(entry));
  }

  function keepFocus(e: MouseEvent) {
    e.preventDefault();
  }

  const loading = () => state() === 'loading';
  const hasValue = () => !loading() && props.shown() !== undefined;
  // Loading floats the label so the skeleton sits on the value line
  const floating = () => loading() || hasValue() || open();
  const shownText = () => {
    if (loading()) return undefined;
    if (hasValue()) return props.shown();
    if (props.field.label && !floating()) return undefined;
    return props.field.placeholder && t(props.field.placeholder);
  };

  function optionClass(index: number) {
    return cx(
      'flex min-w-max cursor-pointer items-center gap-md p-sm transition-colors select-none',
      'hover:bg-surface-hover',
      keyed() &&
        nav()[active()] === index &&
        'bg-surface-hover ring-2 ring-focus ring-inset'
    );
  }

  function actionClass(action: Action) {
    return cx(
      'cursor-pointer rounded-sm transition-colors',
      keyed() && nav()[active()] === action && 'ring-2 ring-focus'
    );
  }

  return (
    <>
      <button
        ref={trigger}
        type="button"
        role="combobox"
        id={props.field.id}
        aria-haspopup="listbox"
        aria-expanded={open()}
        aria-controls={listId}
        aria-activedescendant={open() ? entryId(nav()[active()]) : undefined}
        aria-labelledby={idList(
          props.field.labelledBy,
          props.field.label && labelId
        )}
        aria-describedby={idList(props.field.describedBy, reason() && tipId)}
        aria-disabled={state() === 'disabled' ? 'true' : undefined}
        aria-invalid={state() === 'error' ? 'true' : undefined}
        inert={state() === 'loading'}
        class={cx(
          `
            relative flex min-h-control w-full min-w-(--field-min,0px) items-center rounded-md
            bg-surface-control pr-11 pl-3 text-left text-standard text-fg
            not-aria-disabled:hover:bg-surface-hover aria-disabled:cursor-not-allowed
            aria-disabled:opacity-50 aria-invalid:ring-1 aria-invalid:ring-critical
          `,
          props.field.label && 'pt-[1.15rem] pb-[0.15rem]',
          props.field.class
        )}
        onClick={onClick}
        onKeyDown={onKeyDown}
        onBlur={() => setOpen(false)}
      >
        <Show when={props.field.label}>
          {(label) => (
            <span
              id={labelId}
              class={cx(
                'pointer-events-none absolute left-3 transition-all duration-150 ease-out',
                floating()
                  ? 'top-2 origin-left scale-[0.8] font-bold'
                  : 'top-1/2 -translate-y-1/2 italic'
              )}
            >
              {t(label())}
            </span>
          )}
        </Show>
        <Show
          when={!loading()}
          fallback={
            <Skeleton class={cx('w-1/3', props.field.label && 'mt-1')} />
          }
        >
          <span
            class={cx(
              'min-w-0 grow truncate',
              !hasValue() && 'text-fg-muted italic'
            )}
          >
            {shownText()}
          </span>
          <span class="pointer-events-none absolute inset-y-0 right-5 flex items-center icon-sm">
            {open() ? <CollapseIcon /> : <ExpandIcon />}
          </span>
        </Show>
      </button>
      <Popover
        open={open()}
        onClose={() => setOpen(false)}
        anchor={() => trigger}
        placement={props.field.placement}
        class="
          my-md max-h-[50dvh] min-w-[anchor-size(width)] overflow-hidden rounded-sm
          bg-surface-control opacity-0 transition-[opacity,display,overlay] transition-discrete
          duration-120 ease-out open:opacity-100 starting:open:opacity-0
          motion-reduce:transition-none
        "
      >
        <div role="presentation" onMouseDown={keepFocus} onClick={onListClick}>
          <Show when={!!props.multi}>
            <div
              class="
                flex items-center justify-between gap-md border-b border-line bg-surface-well/60
                px-md py-sm font-bold select-none
              "
            >
              <span
                id={entryId('all')}
                data-entry="all"
                class={cx(actionClass('all'), 'text-fg')}
              >
                {t('dropdown_select-all')}
              </span>
              <span
                id={entryId('none')}
                data-entry="none"
                class={cx(actionClass('none'), 'text-fg-muted hover:text-fg')}
              >
                {t('dropdown_unselect-all')}
              </span>
            </div>
          </Show>
          <div
            id={listId}
            role="listbox"
            aria-labelledby={idList(
              props.field.labelledBy,
              props.field.label && labelId
            )}
            aria-multiselectable={props.multi ? 'true' : undefined}
            class="flex flex-col"
          >
            <For each={props.field.options}>
              {(option, i) => (
                <div
                  id={entryId(i())}
                  role="option"
                  aria-selected={props.isSelected(option.value)}
                  data-entry={i()}
                  class={optionClass(i())}
                >
                  <Show
                    when={props.multi}
                    fallback={
                      <span class="flex size-4 shrink-0 items-center justify-center icon-sm">
                        <Show when={props.isSelected(option.value)}>
                          <CheckIcon />
                        </Show>
                      </span>
                    }
                  >
                    <CheckMark selected={props.isSelected(option.value)} />
                  </Show>
                  {option.render ? (
                    option.render()
                  ) : (
                    <span class="min-w-0 grow">{optionText(option)}</span>
                  )}
                </div>
              )}
            </For>
          </div>
        </div>
      </Popover>
      <TextTooltip id={tipId} anchor={() => trigger} text={reason()} />
    </>
  );
}

function CheckMark(props: { selected: boolean }): JSX.Element {
  return (
    <span
      class={cx(
        'flex size-4 shrink-0 items-center justify-center rounded-sm transition-colors icon-xs',
        props.selected
          ? 'bg-accent text-on-accent'
          : 'border border-line bg-surface-hover'
      )}
    >
      <Show when={props.selected}>
        <CheckIcon />
      </Show>
    </span>
  );
}

export function Select<T>(props: SelectProps<T>) {
  const selected = () => props.options.find((o) => o.value === props.value);
  return (
    <SelectBox
      field={props}
      shown={() => {
        const option = selected();
        if (!option) return undefined;
        return props.renderValue
          ? props.renderValue(option)
          : optionText(option);
      }}
      isSelected={(value) => value === props.value}
      pick={(value) => {
        if (value !== props.value) props.onChange(value);
      }}
    />
  );
}

export function MultiSelect<T>(props: MultiSelectProps<T>) {
  const selected = () =>
    props.options.filter((o) => props.value.includes(o.value));
  return (
    <SelectBox
      field={props}
      shown={() => {
        const list = selected();
        if (list.length === 0) return undefined;
        if (props.renderValue) return props.renderValue(list);
        if (list.length === 1) return optionText(list[0]!);
        return t('input-select-count', { count: list.length });
      }}
      isSelected={(value) => props.value.includes(value)}
      pick={(value) =>
        props.onChange(
          props.value.includes(value)
            ? props.value.filter((v) => v !== value)
            : [...props.value, value]
        )
      }
      multi={{
        all: () => props.onChange(props.options.map((o) => o.value)),
        none: () => props.onChange([]),
      }}
    />
  );
}
