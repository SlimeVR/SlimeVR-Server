import {
  type Accessor,
  createContext,
  createEffect,
  createSignal,
  createUniqueId,
  type JSX,
  on,
  onCleanup,
  Show,
  useContext,
} from 'solid-js';
import { Dynamic } from 'solid-js/web';
import RotateCcwIcon from '~icons/lucide/rotate-ccw';
import { cx } from '@/lib/cx';
import { t, type MessageId } from '@/lib/l10n';
import type { SettingDef } from '@/lib/settings-def';
import { TextButton } from '@/ui/button';
import { type Field, type FieldProps, idList } from '@/ui/field';

interface RowState {
  disabled: Accessor<boolean>;
  loading: Accessor<boolean>;
  reason: Accessor<MessageId | undefined>;
}

const RowContext = createContext<RowState>({
  disabled: () => false,
  loading: () => false,
  reason: () => undefined,
});

export interface RowScopeProps {
  disabled?: boolean;
  loading?: boolean;
  disabledReason?: MessageId;
  children: JSX.Element;
}

// Every SettingRow inside inherits this state
export function RowScope(props: RowScopeProps) {
  const parent = useContext(RowContext);
  const disabled = () => parent.disabled() || !!props.disabled;
  const state: RowState = {
    disabled,
    loading: () => parent.loading() || !!props.loading,
    reason: () =>
      disabled() ? (props.disabledReason ?? parent.reason()) : undefined,
  };
  return (
    <RowContext.Provider value={state}>{props.children}</RowContext.Provider>
  );
}

export interface SettingFocusTarget {
  key: string;
}

const SettingFocusContext = createContext<{
  target: Accessor<SettingFocusTarget | undefined>;
}>({ target: () => undefined });

export function SettingFocus(props: {
  target: SettingFocusTarget | undefined;
  children: JSX.Element;
}) {
  const focus = { target: () => props.target };
  return (
    <SettingFocusContext.Provider value={focus}>
      {props.children}
    </SettingFocusContext.Provider>
  );
}

export const SETTING_HIGHLIGHT_MS = 1800;

export function useSettingHighlight(
  setting: Accessor<SettingDef>,
  el: () => HTMLElement
): Accessor<boolean> {
  const { target } = useContext(SettingFocusContext);
  const [highlighted, setHighlighted] = createSignal(false);
  createEffect(
    on(target, (next) => {
      if (next?.key !== setting().key) return;
      el().scrollIntoView({ block: 'center', behavior: 'smooth' });
      setHighlighted(true);
      const timeout = setTimeout(
        () => setHighlighted(false),
        SETTING_HIGHLIGHT_MS
      );
      onCleanup(() => clearTimeout(timeout));
    })
  );
  return highlighted;
}

export interface SettingReset {
  readonly modified: boolean;
  onReset: () => void;
}

export function ResetButton(props: {
  reset: SettingReset | undefined;
  disabled?: boolean;
}) {
  return (
    <div class="flex h-6 w-control-sm shrink-0 items-center justify-center">
      <Show
        when={!props.disabled && props.reset?.modified && props.reset}
        fallback={
          <Show when={props.reset}>
            <RotateCcwIcon aria-hidden="true" class="icon icon-sm opacity-30" />
          </Show>
        }
      >
        {(reset) => (
          <TextButton
            label="settings-reset-single"
            variant="ghost"
            size="small"
            iconOnly
            icon={<RotateCcwIcon class="icon icon-sm" />}
            onClick={() => reset().onReset()}
          />
        )}
      </Show>
    </div>
  );
}

export interface RowWiring {
  readonly id: string;
  readonly labelledBy: string;
  readonly describedBy?: string;
  readonly disabled?: true;
  readonly loading?: true;
  readonly disabledReason?: MessageId;
}

// side: after the text, wraps under it when the row is too narrow. below: under
// the text at any width (radio cards, grids, wide ranges)
export type ControlPlacement = 'side' | 'below';

export interface SettingBinding<T> {
  setting: SettingDef;
  field: Field<T>;
  reset?: SettingReset;
}

interface SettingRowBase {
  details?: JSX.Element;
  reset?: SettingReset;
  error?: MessageId;
  controlPlacement?: ControlPlacement;
  // Clicking the title or description presses the control (toggles, checkboxes)
  pressable?: boolean;
  disabled?: boolean;
  disabledReason?: MessageId;
  loading?: boolean;
  // Children stay visible, dimmed and disabled
  childrenDisabled?: boolean;
  // Tooltip on the children's controls while childrenDisabled
  childrenDisabledReason?: MessageId;
  children?: JSX.Element;
}

// With `bind`, control gets the field already merged with the row wiring:
// `bind={feet} control={(f) => <Toggle {...f} />}`
export interface BoundSettingRowProps<T> extends SettingRowBase {
  bind: SettingBinding<T>;
  setting?: undefined;
  control: (field: FieldProps<T>) => JSX.Element;
}

// Without it, control gets the wiring alone: `control={(w) => <Button {...w} />}`
export interface SettingRowProps extends SettingRowBase {
  // Title and description come from the declaration
  setting: SettingDef;
  bind?: undefined;
  control?: (wiring: RowWiring) => JSX.Element;
}

// Text that presses the control through a native label when the row is pressable
function PressText(props: {
  for: string | undefined;
  tag: 'div' | 'p';
  id?: string;
  class?: string;
  children: JSX.Element;
}) {
  return (
    <Dynamic
      component={props.for ? 'label' : props.tag}
      for={props.for}
      id={props.id}
      class={cx(props.for && 'block cursor-pointer', props.class)}
    >
      {props.children}
    </Dynamic>
  );
}

export interface SettingRowComponent {
  <T>(props: BoundSettingRowProps<T>): JSX.Element;
  (props: SettingRowProps): JSX.Element;
}

function SettingRowImpl<T>(props: BoundSettingRowProps<T> | SettingRowProps) {
  const parent = useContext(RowContext);
  const id = createUniqueId();
  const titleId = `${id}-title`;
  const textId = `${id}-text`;
  const setting = () => props.bind?.setting ?? (props.setting as SettingDef);
  let el!: HTMLDivElement;
  const highlighted = useSettingHighlight(setting, () => el);

  const disabled = () => parent.disabled() || !!props.disabled;
  const loading = () => parent.loading() || !!props.loading;
  const reason = () =>
    disabled() ? (props.disabledReason ?? parent.reason()) : undefined;
  const hasText = () =>
    !!(setting().description || props.details || props.error);
  const placement = () => props.controlPlacement ?? 'side';

  const wiring: RowWiring = {
    id,
    labelledBy: titleId,
    get describedBy() {
      return idList(hasText() && textId);
    },
    get disabled() {
      return disabled() ? true : undefined;
    },
    get loading() {
      return loading() ? true : undefined;
    },
    get disabledReason() {
      return reason();
    },
  };

  function boundField(bind: SettingBinding<T>): FieldProps<T> {
    return {
      get value() {
        return bind.field.value;
      },
      onChange: (value) => bind.field.onChange(value),
      get loading() {
        return bind.field.loading || loading();
      },
      get disabled() {
        return bind.field.disabled || disabled();
      },
      get error() {
        return bind.field.error;
      },
      id: wiring.id,
      labelledBy: wiring.labelledBy,
      get describedBy() {
        return wiring.describedBy;
      },
      get disabledReason() {
        return wiring.disabledReason;
      },
    };
  }

  function renderControl() {
    if (props.bind) return props.control(boundField(props.bind));
    return props.control?.(wiring);
  }

  const childState: RowState = {
    disabled: () => disabled() || !!props.childrenDisabled,
    loading,
    // A disabled row passes its own reason down
    reason: () =>
      reason() ??
      (props.childrenDisabled ? props.childrenDisabledReason : undefined),
  };

  const pressFor = () =>
    props.pressable && props.control && !disabled() ? id : undefined;

  return (
    <div
      ref={el}
      class={cx(
        `
          flex min-w-0 flex-col gap-md py-md pr-md pl-xs transition-colors @lg:py-lg @lg:pr-lg
          @lg:pl-sm
        `,
        highlighted()
          ? 'bg-accent-subtle/30'
          : !props.disabled &&
              (disabled()
                ? 'hover:bg-surface-hover/15'
                : 'hover:bg-surface-hover/30')
      )}
    >
      <div class="flex min-w-0 items-start gap-sm">
        <ResetButton
          reset={props.reset ?? props.bind?.reset}
          disabled={
            disabled() ||
            loading() ||
            !!props.bind?.field.disabled ||
            !!props.bind?.field.loading
          }
        />
        <div class="flex min-w-0 flex-1 flex-wrap items-center gap-x-xl gap-y-sm">
          <div
            class={cx(
              'flex min-w-0 flex-[999_1_12rem] flex-col gap-xs',
              disabled() && 'opacity-50'
            )}
          >
            <PressText
              for={pressFor()}
              tag="div"
              id={titleId}
              class="text-section"
            >
              {t(setting().title)}
            </PressText>
            <Show when={hasText()}>
              <div
                id={textId}
                class="flex max-w-[75ch] min-w-0 flex-col gap-xs"
              >
                <Show when={setting().description}>
                  {(description) => (
                    <PressText for={pressFor()} tag="p">
                      {t(description())}
                    </PressText>
                  )}
                </Show>
                {props.details}
                <Show when={props.error}>
                  {(error) => <p class="text-critical">{t(error())}</p>}
                </Show>
              </div>
            </Show>
          </div>
          <Show when={placement() === 'side' && props.control}>
            <div
              class="
                flex min-w-0 flex-[1_1_auto] justify-end [--field-min:14rem]
                @3xl:[--field-min:18rem]
              "
            >
              {renderControl()}
            </div>
          </Show>
        </div>
      </div>
      <Show when={placement() === 'below' && props.control}>
        <div class="min-w-0 pl-[calc(var(--spacing-control-sm)+var(--spacing-sm))]">
          {renderControl()}
        </div>
      </Show>
      <RowContext.Provider value={childState}>
        <div
          class="
            -mr-md ml-xs flex min-w-0 flex-col gap-lg border-l-2 border-surface-control pl-md
            empty:hidden @lg:-mr-lg @lg:pl-lg
          "
        >
          {props.children}
        </div>
      </RowContext.Provider>
    </div>
  );
}

export const SettingRow: SettingRowComponent = SettingRowImpl;
