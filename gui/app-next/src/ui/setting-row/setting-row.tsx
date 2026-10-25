import {
  type Accessor,
  createContext,
  createUniqueId,
  type JSX,
  Show,
  useContext,
} from 'solid-js';
import { Dynamic } from 'solid-js/web';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { idList } from '@/ui/field';

interface RowState {
  disabled: Accessor<boolean>;
  loading: Accessor<boolean>;
  reason: Accessor<MessageId | undefined>;
  nested: boolean;
}

const RowContext = createContext<RowState>({
  disabled: () => false,
  loading: () => false,
  reason: () => undefined,
  nested: false,
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
    nested: parent.nested,
  };
  return (
    <RowContext.Provider value={state}>{props.children}</RowContext.Provider>
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

export interface SettingRowProps {
  title: MessageId;
  titleArgs?: MessageArgs;
  description?: MessageId;
  descriptionArgs?: MessageArgs;
  details?: JSX.Element;
  error?: MessageId;
  control?: (wiring: RowWiring) => JSX.Element;
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

export function SettingRow(props: SettingRowProps) {
  const parent = useContext(RowContext);
  const id = createUniqueId();
  const titleId = `${id}-title`;
  const textId = `${id}-text`;

  const disabled = () => parent.disabled() || !!props.disabled;
  const loading = () => parent.loading() || !!props.loading;
  const reason = () =>
    disabled() ? (props.disabledReason ?? parent.reason()) : undefined;
  const hasText = () => !!(props.description || props.details || props.error);
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

  const childState: RowState = {
    disabled: () => disabled() || !!props.childrenDisabled,
    loading,
    // A disabled row passes its own reason down
    reason: () =>
      reason() ??
      (props.childrenDisabled ? props.childrenDisabledReason : undefined),
    nested: true,
  };

  const pressFor = () =>
    props.pressable && props.control && !disabled() ? id : undefined;

  return (
    <div
      class={cx(
        'flex min-w-0 flex-col gap-md py-md pl-xs transition-colors @lg:py-lg @lg:pl-sm',
        !parent.nested && 'pr-md @lg:pr-lg',
        !disabled() && 'hover:bg-surface-hover/30'
      )}
    >
      <div class="flex min-w-0 items-start gap-sm">
        {/* Reset button slot */}
        <div class="h-6 w-7 shrink-0" />
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
              {t(props.title, props.titleArgs)}
            </PressText>
            <Show when={hasText()}>
              <div
                id={textId}
                class="flex max-w-[75ch] min-w-0 flex-col gap-xs"
              >
                <Show when={props.description}>
                  {(description) => (
                    <PressText for={pressFor()} tag="p">
                      {t(description(), props.descriptionArgs)}
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
            {(control) => (
              <div
                class="
                  flex min-w-0 flex-[1_1_auto] justify-end [--field-min:14rem]
                  @3xl:[--field-min:18rem]
                "
              >
                {control()(wiring)}
              </div>
            )}
          </Show>
        </div>
      </div>
      <Show when={placement() === 'below' && props.control}>
        {(control) => <div class="min-w-0 pl-9">{control()(wiring)}</div>}
      </Show>
      <RowContext.Provider value={childState}>
        <div
          class="
            ml-xs flex min-w-0 flex-col gap-md border-l-2 border-surface-control pl-md empty:hidden
            @lg:pl-lg
          "
        >
          {props.children}
        </div>
      </RowContext.Provider>
    </div>
  );
}
