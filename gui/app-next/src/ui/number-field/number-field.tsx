import {
  createMemo,
  createSignal,
  createUniqueId,
  onCleanup,
  Show,
} from 'solid-js';
import { useBack } from '@/lib/back-stack';
import { cx } from '@/lib/cx';
import {
  decimalSeparator,
  fmt,
  type LengthSpec,
  numberFormatter,
  numberParts,
  type NumberSpec,
  parseNumber,
} from '@/lib/format';
import { t, type MessageId } from '@/lib/l10n';
import { fieldBlocked, type FieldProps, fieldState, idList } from '@/ui/field';
import { useMotionItem } from '@/ui/motion';
import { Skeleton } from '@/ui/skeleton';
import { type StepRange, stepKeys, stepValue } from './step-keys';
import { TextTooltip } from '@/ui/tooltip';

export interface NumberFieldProps extends FieldProps<number> {
  min: number;
  max: number;
  step?: number;
  bigStep?: number;
  format?: NumberSpec | LengthSpec;
  snap?: boolean;
  class?: string;
}

const PLAIN_SPEC: NumberSpec = { maximumFractionDigits: 2 };

function lengthOf(format: NumberSpec | LengthSpec | undefined) {
  return format && 'spec' in format ? format : undefined;
}

const REST_STEP_KEYS = new Set([
  'ArrowLeft',
  'ArrowRight',
  'PageUp',
  'PageDown',
  'Home',
  'End',
]);
const EDIT_STEP_KEYS = new Set(['ArrowUp', 'ArrowDown', 'PageUp', 'PageDown']);

export function NumberField(props: NumberFieldProps) {
  let el!: HTMLInputElement;
  let box!: HTMLDivElement;
  const tipId = createUniqueId();
  const state = () => fieldState(props);
  const reason = () =>
    state() === 'disabled' ? props.disabledReason : undefined;

  const length = () => lengthOf(props.format);
  const spec = (): NumberSpec =>
    length()?.spec ?? (props.format as NumberSpec | undefined) ?? PLAIN_SPEC;
  const scale = () => length()?.scale ?? 1;
  const step = () => {
    const ls = length();
    return props.step ?? (ls ? ls.step / ls.scale : 1);
  };
  // Typed digits are display units, a percent shows the value times 100
  const editScale = () => scale() * (spec().style === 'percent' ? 100 : 1);

  const [editing, setEditing] = createSignal(false);
  const [draft, setDraft] = createSignal('');
  // Entering edit mode does not change the value until something is typed
  let dirty = false;
  let selectOnClick = false;

  useMotionItem(() => el, {
    owns: () => (editing() ? 'all' : 'horizontal'),
  });

  // Escape and pad B leave editing before they close anything else
  useBack(() => setEditing(false), { when: editing });

  const parsed = createMemo(() => parseNumber(draft()) / editScale());
  const draftInvalid = () =>
    editing() &&
    dirty &&
    (Number.isNaN(parsed()) || parsed() < props.min || parsed() > props.max);
  const shown = () =>
    editing() && !Number.isNaN(parsed()) ? parsed() : props.value;
  const parts = () => numberParts(spec(), shown() * scale());
  const invalid = () =>
    state() === 'error' || (state() === 'ready' && draftInvalid());

  function range(value: number): StepRange {
    return {
      value,
      min: props.min,
      max: props.max,
      step: step(),
      bigStep: props.bigStep,
    };
  }

  function toDisplay(value: number) {
    return Math.round(value * editScale() * 1e9) / 1e9;
  }

  function rawOf(value: number) {
    return numberParts(spec(), value * scale()).raw;
  }

  // Clamp, snap, then round to the digits the format shows
  function normalize(value: number) {
    let next = Math.min(props.max, Math.max(props.min, value));
    if (props.snap)
      next = props.min + Math.round((next - props.min) / step()) * step();
    const digits =
      numberFormatter(spec()).resolvedOptions().maximumFractionDigits ?? 2;
    const factor = editScale() * 10 ** digits;
    return Math.round(next * factor) / factor;
  }

  function setValue(value: number) {
    if (value !== props.value) props.onChange(value);
  }

  function startEditing(text?: string) {
    if (fieldBlocked(state())) return;
    dirty = text !== undefined;
    setDraft(text ?? rawOf(props.value));
    setEditing(true);
  }

  function editDraft(text: string) {
    dirty = true;
    setDraft(text);
  }

  // Enter on an invalid draft keeps editing, blur reverts it
  function commit(source: 'enter' | 'blur') {
    if (!editing()) return;
    if (dirty && Number.isNaN(parsed())) {
      if (source === 'blur') setEditing(false);
      return;
    }
    setEditing(false);
    if (dirty) setValue(normalize(parsed()));
  }

  onCleanup(() => commit('blur'));

  function stepBy(direction: 1 | -1, big = false) {
    if (fieldBlocked(state())) return;
    const by = direction * (big ? (props.bigStep ?? step()) : step());
    if (editing()) editDraft(rawOf(stepValue(range(shown()), by)));
    else setValue(stepValue(range(props.value), by));
  }

  function canStep(direction: 1 | -1) {
    if (fieldBlocked(state())) return false;
    return direction > 0 ? shown() < props.max : shown() > props.min;
  }

  function onKeyDown(e: KeyboardEvent) {
    if (fieldBlocked(state()) || e.ctrlKey || e.metaKey || e.altKey) return;
    if (editing()) {
      if (e.key === 'Enter') {
        e.preventDefault();
        commit('enter');
        return;
      }
      if (EDIT_STEP_KEYS.has(e.key)) {
        e.preventDefault();
        editDraft(rawOf(stepKeys(e.key, range(shown()))!));
      }
      return;
    }
    if (e.key === 'Enter' || e.key === 'F2') {
      e.preventDefault();
      startEditing();
      el.select();
      return;
    }
    if (REST_STEP_KEYS.has(e.key)) {
      e.preventDefault();
      setValue(stepKeys(e.key, range(props.value))!);
      return;
    }
    if (e.key === 'Backspace' || e.key === 'Delete') {
      e.preventDefault();
      startEditing('');
      return;
    }
    // Spreadsheet style: the key replaces the value and still types
    if (
      /^[0-9+-]$/.test(e.key) ||
      e.key === '.' ||
      e.key === decimalSeparator()
    ) {
      startEditing('');
      return;
    }
    if (e.key.length === 1) e.preventDefault();
  }

  function onInput(e: InputEvent & { currentTarget: HTMLInputElement }) {
    if (fieldBlocked(state())) return;
    if (!editing()) setEditing(true);
    editDraft(e.currentTarget.value);
  }

  // Keyboard and pad still reach a disabled field (needed for tooltip), a click does not focus it
  function onMouseDown(e: MouseEvent) {
    if (fieldBlocked(state())) {
      e.preventDefault();
      return;
    }
    if (editing()) return;
    startEditing();
    selectOnClick = true;
  }

  // The click places the caret on mouseup, select after it
  function onClick() {
    if (!selectOnClick) return;
    selectOnClick = false;
    el.select();
  }

  // Clicks on the affixes or padding edit too
  function onAreaMouseDown(e: MouseEvent) {
    if (e.target === el) return;
    e.preventDefault();
    if (fieldBlocked(state())) return;
    el.focus();
    if (!editing()) startEditing();
    el.select();
  }

  return (
    <div
      ref={box}
      data-focus-ring
      inert={state() === 'loading'}
      class={cx(
        `
          flex min-h-control w-full min-w-(--field-min,0px) items-stretch overflow-hidden rounded-md
          bg-surface-control text-standard text-fg has-aria-disabled:opacity-50
          has-aria-invalid:ring-1 has-aria-invalid:ring-critical
        `,
        props.class
      )}
    >
      <StepButton
        label="input-number-decrease"
        glyph={'-'}
        hidden={state() === 'loading'}
        enabled={canStep(-1)}
        onStep={() => stepBy(-1)}
      />
      <div
        class={cx(
          'relative flex min-w-0 flex-1 items-center justify-center gap-xs px-xs',
          state() === 'loading' && 'text-transparent'
        )}
        role="presentation"
        onMouseDown={onAreaMouseDown}
      >
        <Show when={parts().prefix}>
          <span aria-hidden="true">{parts().prefix}</span>
        </Show>
        <input
          ref={el}
          id={props.id}
          type="text"
          inputmode="decimal"
          role="spinbutton"
          autocomplete="off"
          value={editing() ? draft() : parts().number}
          readOnly={fieldBlocked(state())}
          aria-valuenow={toDisplay(props.value)}
          aria-valuemin={toDisplay(props.min)}
          aria-valuemax={toDisplay(props.max)}
          aria-valuetext={fmt(spec(), props.value * scale())}
          aria-labelledby={idList(props.labelledBy)}
          aria-describedby={idList(props.describedBy, reason() && tipId)}
          aria-disabled={state() === 'disabled' ? 'true' : undefined}
          aria-invalid={invalid() ? 'true' : undefined}
          class={cx(
            `
              field-sizing-content min-w-[2ch] bg-transparent text-center
              aria-disabled:cursor-not-allowed
            `,
            !editing() && 'cursor-default caret-transparent'
          )}
          onKeyDown={onKeyDown}
          onInput={onInput}
          onMouseDown={onMouseDown}
          onClick={onClick}
          onBlur={() => commit('blur')}
        />
        <Show when={parts().suffix}>
          <span aria-hidden="true">{parts().suffix}</span>
        </Show>
        <Show when={state() === 'loading'}>
          <Skeleton class="absolute w-16" />
        </Show>
      </div>
      <StepButton
        label="input-number-increase"
        glyph="+"
        hidden={state() === 'loading'}
        enabled={canStep(1)}
        onStep={() => stepBy(1)}
      />
      <TextTooltip id={tipId} anchor={() => box} text={reason()} />
    </div>
  );
}

function StepButton(props: {
  label: MessageId;
  glyph: string;
  hidden: boolean;
  enabled: boolean;
  onStep: () => void;
}) {
  return (
    <button
      type="button"
      tabIndex={-1}
      aria-label={t(props.label)}
      disabled={!props.enabled}
      class={cx(
        `
          flex min-w-12 shrink-0 items-center justify-center text-section leading-none
          transition-colors enabled:hover:bg-surface-hover disabled:opacity-30
        `,
        props.hidden && 'invisible'
      )}
      onMouseDown={(e) => e.preventDefault()}
      onClick={() => props.onStep()}
    >
      <span aria-hidden="true">{props.glyph}</span>
    </button>
  );
}
