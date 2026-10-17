import classNames from 'classnames';
import { Clickable } from './Clickable';
import React, { ReactNode, useMemo } from 'react';
import { NavLink } from 'react-router-dom';
import { LoaderIcon, SlimeState } from './icon/LoaderIcon';
import { Typography } from './Typography';
import { Localized, LocalizedProps, useLocalization } from '@fluent/react';

function ButtonContent({
  loading,
  icon,
  children,
}: {
  loading: boolean;
  icon?: ReactNode;
  children: ReactNode;
}) {
  return (
    <>
      <div
        className={classNames(
          { 'opacity-0': loading },
          'flex flex-row gap-2 justify-center items-center'
        )}
      >
        {icon && (
          <div className="flex justify-center items-center fill-background-10 w-5">
            {icon}
          </div>
        )}
        {children}
      </div>
      {loading && (
        <div className="absolute top-0 left-0 w-full h-full flex justify-center items-center fill-background-10">
          <LoaderIcon slimeState={SlimeState.JUMPY} />
        </div>
      )}
    </>
  );
}

export type ButtonProps = {
  children?: ReactNode;
  icon?: ReactNode;
  variant: 'primary' | 'secondary' | 'tertiary' | 'quaternary' | 'danger';
  to?: string;
  loading?: boolean;
  rounded?: boolean;
  size?: 'default' | 'small';
  iconOnly?: boolean;
  badge?: ReactNode;
  state?: any;
  id?: string;
} & React.ButtonHTMLAttributes<HTMLButtonElement> &
  Omit<LocalizedProps, 'id'>;

export function Button({
  children,
  variant,
  disabled,
  to,
  loading = false,
  state = {},
  icon,
  rounded = false,
  size = 'default',
  iconOnly = false,
  badge,
  attrs,
  id,
  vars,
  elems,
  ...props
}: ButtonProps) {
  const { l10n } = useLocalization();
  const showLabel = !iconOnly || !id;
  const accessibleName = !showLabel ? l10n.getString(id, vars) : undefined;

  const classes = useMemo(() => {
    const variantsMap = {
      primary: classNames({
        'bg-accent-background-30 hover:bg-accent-background-20 text-background-10':
          !disabled,
        'bg-accent-background-40 hover:bg-accent-background-40 cursor-not-allowed text-accent-background-10':
          disabled,
      }),
      secondary: classNames({
        'bg-background-60 hover:bg-background-50 text-background-10': !disabled,
        'bg-background-60 hover:bg-background-60 cursor-not-allowed text-background-40':
          disabled,
      }),
      tertiary: classNames({
        'bg-background-50 hover:bg-background-40 text-background-10': !disabled,
        'bg-background-50 hover:bg-background-50 cursor-not-allowed text-background-40':
          disabled,
      }),
      quaternary: classNames({
        'bg-background-70 hover:bg-background-60 text-background-10': !disabled,
        'bg-background-70 hover:bg-background-70 cursor-not-allowed text-background-40':
          disabled,
      }),
      danger: classNames({
        'bg-background-60 hover:bg-status-critical/30 text-background-10':
          !disabled,
        'bg-background-60 cursor-not-allowed text-background-40': disabled,
      }),
    };
    return classNames(
      variantsMap[variant],
      'text-standard text-center relative flex items-center justify-center',
      {
        'rounded-full p-2 text-center min-h-[35px] min-w-[35px]': rounded,
        'rounded-md py-3 min-h-[48px]': !rounded && size === 'default',
        'rounded-md py-1.5 min-h-[36px]': !rounded && size === 'small',
        'px-5': !rounded && size === 'default' && showLabel,
        'px-3':
          !rounded &&
          ((size === 'default' && !showLabel) ||
            (size === 'small' && showLabel)),
        'px-2.5': !rounded && size === 'small' && !showLabel,
      },
      props.className
    );
  }, [variant, disabled, rounded, size, showLabel, props.className]);

  const content = to ? (
    <NavLink
      to={to}
      aria-label={accessibleName}
      className={classes}
      state={state}
      onClick={(ev) => {
        if (disabled) {
          ev.preventDefault();
          return;
        }
        if (props.onClick) return props.onClick(ev as any);
      }}
    >
      <ButtonContent icon={icon} loading={loading}>
        {showLabel && id && (
          <Localized attrs={attrs} vars={vars} elems={elems} id={id}>
            {children}
          </Localized>
        )}
        {showLabel && !id && children}
      </ButtonContent>
      {badge !== undefined && (
        <span className="absolute -top-1.5 -right-1.5 flex items-center justify-center min-w-5 h-5 px-1 rounded-full bg-background-10">
          <Typography tag="span" bold color="text-background-90">
            {badge}
          </Typography>
        </span>
      )}
    </NavLink>
  ) : (
    <Clickable
      {...props}
      label={accessibleName}
      tooltipId={showLabel ? undefined : id}
      tooltipVars={vars}
      className={classes}
      disabled={disabled}
    >
      <ButtonContent icon={icon} loading={loading}>
        {showLabel && id && (
          <Localized attrs={attrs} vars={vars} elems={elems} id={id}>
            {children}
          </Localized>
        )}
        {showLabel && !id && children}
      </ButtonContent>
      {badge !== undefined && (
        <span className="absolute -top-1.5 -right-1.5 flex items-center justify-center min-w-5 h-5 px-1 rounded-full bg-background-10">
          <Typography tag="span" bold color="text-background-90">
            {badge}
          </Typography>
        </span>
      )}
    </Clickable>
  );

  return content;
}
