import { A } from '@solidjs/router';
import { createUniqueId, For, type JSX, Show, splitProps } from 'solid-js';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { useMotionItem } from '@/ui/motion';
import { Tooltip } from '@/ui/tooltip';

export type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger';
export type ButtonSize = 'default' | 'small';

type ButtonBase = {
  variant?: ButtonVariant;
  size?: ButtonSize;
  // Accessible name and tooltip while icon only
  name?: string;
  icon?: JSX.Element;
  iconOnly?: boolean;
  // Pill / circle instead of the rounded rectangle
  round?: boolean;
  loading?: boolean;
  disabled?: boolean;
  disabledReason?: string;
  badge?: JSX.Element;
  autofocus?: boolean;
  id?: string;
  class?: string;
  onClick?: (e: MouseEvent) => void;
  // Hidden while icon only
  children?: JSX.Element;
};

type ButtonAction = {
  to?: undefined;
  type?: 'button' | 'submit';
  pressed?: boolean;
  expanded?: boolean;
  controls?: string;
  ref?: HTMLButtonElement | ((el: HTMLButtonElement) => void);
};

type ButtonLink = {
  to: string;
  state?: unknown;
  replace?: boolean;
  ref?: HTMLAnchorElement | ((el: HTMLAnchorElement) => void);
};

export type ButtonProps = ButtonBase & (ButtonAction | ButtonLink);

const VARIANT: Record<ButtonVariant, string> = {
  primary: `
    bg-accent text-on-accent
    not-aria-disabled:hover:bg-accent-hover
  `,
  secondary: `
    bg-surface-control text-fg
    not-aria-disabled:hover:bg-surface-hover
  `,
  ghost: `
    text-fg
    not-aria-disabled:hover:bg-surface-hover
  `,
  danger: `
    bg-surface-control text-fg
    not-aria-disabled:hover:bg-critical/30
  `,
};

const SIZE: Record<ButtonSize, { label: string; icon: string }> = {
  default: { label: 'min-h-control px-5', icon: 'size-control shrink-0' },
  small: { label: 'min-h-control-sm px-3', icon: 'size-control-sm shrink-0' },
};

const DOT_DELAYS = [0, 150, 300];

export function Button(props: ButtonProps) {
  let el!: HTMLElement;
  const tipId = createUniqueId();
  useMotionItem(() => el);

  const iconOnly = () => !!props.iconOnly && !!props.name;
  const reason = () => (props.disabled ? props.disabledReason : undefined);
  const tooltip = () => reason() ?? (iconOnly() ? props.name : undefined);

  function onClick(e: MouseEvent) {
    if (props.disabled || props.loading) {
      e.preventDefault();
      return;
    }
    props.onClick?.(e);
  }

  function setRef(node: HTMLElement) {
    el = node;
    (props.ref as ((el: HTMLElement) => void) | undefined)?.(node);
  }

  const className = () =>
    cx(
      'relative inline-flex items-center justify-center text-standard',
      props.round ? 'rounded-full' : 'rounded-md',
      'transition-colors aria-disabled:cursor-not-allowed aria-disabled:opacity-50',
      VARIANT[props.variant ?? 'secondary'],
      SIZE[props.size ?? 'default'][iconOnly() ? 'icon' : 'label'],
      props.loading && 'cursor-progress',
      props.class
    );

  function content() {
    return (
      <>
        <span
          class={cx(
            'flex items-center justify-center gap-sm',
            props.loading && 'opacity-0'
          )}
        >
          <Show when={props.icon}>
            {(icon) => <span class="flex icon-md">{icon()}</span>}
          </Show>
          <Show when={!iconOnly()}>{props.children}</Show>
        </span>
        <Show when={props.loading}>
          <span
            class={cx(
              'absolute inset-0 flex items-center justify-center',
              iconOnly() ? 'gap-0.75' : 'gap-1.5'
            )}
            aria-hidden="true"
          >
            <For each={DOT_DELAYS}>
              {(delay) => (
                <span
                  class={cx(
                    'animate-pulse rounded-full bg-current',
                    iconOnly() ? 'size-1' : 'size-1.5'
                  )}
                  style={{ 'animation-delay': `${delay}ms` }}
                />
              )}
            </For>
          </span>
        </Show>
        <Show when={props.badge}>
          {(badge) => (
            <span
              class="
                absolute -top-1.5 -right-1.5 flex h-5 min-w-5 items-center justify-center
                rounded-full bg-accent px-1 font-bold text-on-accent
              "
            >
              {badge()}
            </span>
          )}
        </Show>
      </>
    );
  }

  const link = () => (props.to === undefined ? undefined : props);
  const action = () => (props.to === undefined ? props : undefined);

  return (
    <>
      <Show
        when={link()}
        fallback={
          <button
            ref={(node) => setRef(node)}
            id={props.id}
            type={action()?.type ?? 'button'}
            autofocus={props.autofocus}
            aria-label={iconOnly() ? props.name : undefined}
            aria-describedby={reason() ? tipId : undefined}
            aria-disabled={props.disabled ? 'true' : undefined}
            aria-pressed={action()?.pressed}
            aria-expanded={action()?.expanded}
            aria-controls={action()?.controls}
            class={className()}
            onClick={onClick}
          >
            {content()}
          </button>
        }
      >
        {(link) => (
          <A
            ref={(node) => setRef(node)}
            href={link().to}
            state={link().state}
            replace={link().replace}
            id={props.id}
            autofocus={props.autofocus}
            aria-label={iconOnly() ? props.name : undefined}
            aria-describedby={reason() ? tipId : undefined}
            aria-disabled={props.disabled ? 'true' : undefined}
            class={className()}
            onClick={onClick}
          >
            {content()}
          </A>
        )}
      </Show>
      <Tooltip id={tipId} anchor={() => el}>
        {tooltip()}
      </Tooltip>
    </>
  );
}

export type TextButtonProps = Omit<
  ButtonBase,
  'name' | 'disabledReason' | 'children'
> & {
  label: MessageId;
  args?: MessageArgs;
  disabledReason?: MessageId;
} & (ButtonAction | ButtonLink);

// Translated label: visible text, or the name while icon only
export function TextButton(props: TextButtonProps) {
  const [local, rest] = splitProps(props, ['label', 'args', 'disabledReason']);
  const text = () => t(local.label, local.args);

  return (
    <Button
      {...rest}
      name={text()}
      disabledReason={
        local.disabledReason ? t(local.disabledReason) : undefined
      }
    >
      {text()}
    </Button>
  );
}
