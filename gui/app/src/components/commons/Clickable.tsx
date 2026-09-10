import classNames from 'classnames';
import { ComponentProps } from 'react';
import { NavLink, type NavLinkProps } from 'react-router-dom';
import { Tooltip, type Direction } from './Tooltip';
import { Typography } from './Typography';

type NativeButtonProps = Omit<
  ComponentProps<'button'>,
  'aria-pressed' | 'aria-expanded' | 'aria-current' | 'aria-label'
>;

type CommonProps = {
  /** Toggle or selection state, e.g. a segmented option. Button mode only. */
  pressed?: boolean;
  /** Disclosure state, e.g. an accordion header. Button mode only. */
  expanded?: boolean;
  /** Marks the active entry in a stepper or nav. */
  current?: 'step' | 'page' | true;
  /** Accessible name, for when the children don't already give one. */
  label?: string;
  /** Fluent id. Opts this surface into a tooltip (hover *and* focus). */
  tooltipId?: string;
  tooltipDirection?: Direction;
};

type ButtonProps = CommonProps &
  NativeButtonProps & { [dataAttr: `data-${string}`]: unknown } & {
    to?: undefined;
  };

type LinkProps = CommonProps &
  Omit<NavLinkProps, 'className' | 'children'> & {
    [dataAttr: `data-${string}`]: unknown;
  } & {
    /** Router path. Renders a `NavLink` instead of a `<button>`. */
    to: NavLinkProps['to'];
    className?: string;
    children?: ComponentProps<'button'>['children'];
  };

/**
 * A clickable surface that keeps whatever styling you give it and only adds
 * the semantics: tab focus, Enter/Space, and a default `type="button"` so a
 * `<button>` does not submit a surrounding form unless asked to.
 *
 * Pass `to` and it renders a router `NavLink` instead, so the same call site
 * covers both an action and a navigation.
 *
 * Reach for this anywhere you would have put `onClick` on a `<div>`. Use
 * `Button` for the styled variants and `IconButton` for icon-only controls.
 *
 * No tooltip by default, since the children are already the label. Pass
 * `tooltipId` where the extra explanation earns its place.
 *
 * It cannot wrap another button or link. If the surface already contains one,
 * leave the container inert and make the inner control the real button.
 */
export function Clickable({
  className,
  pressed,
  expanded,
  current,
  label,
  tooltipId,
  tooltipDirection = 'bottom',
  ...props
}: ButtonProps | LinkProps) {
  const surface =
    props.to !== undefined ? (
      <NavLink
        {...(props as LinkProps)}
        aria-current={current}
        aria-label={label}
        className={classNames('text-left', className)}
      />
    ) : (
      <button
        type="button"
        {...(props as ButtonProps)}
        aria-pressed={pressed}
        aria-expanded={expanded}
        aria-current={current}
        aria-label={label}
        className={classNames('text-left', className)}
      />
    );

  if (!tooltipId) return surface;

  return (
    <Tooltip
      preferedDirection={tooltipDirection}
      content={<Typography id={tooltipId} />}
      spacing={5}
    >
      {surface}
    </Tooltip>
  );
}
