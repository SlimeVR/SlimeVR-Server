import classNames from 'classnames';
import React, { ReactNode, useMemo } from 'react';
import { NavLink } from 'react-router-dom';
import { LoaderIcon, SlimeState } from './icon/LoaderIcon';
import { Localized, LocalizedProps } from '@fluent/react';

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
  variant: 'primary' | 'secondary' | 'tertiary' | 'quaternary';
  to?: string;
  loading?: boolean;
  rounded?: boolean;
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
  attrs,
  id,
  vars,
  elems,
  ...props
}: ButtonProps) {
  const classes = useMemo(() => {
    const variantsMap = {
      primary: classNames({
        'bg-accent-background-30 hover:bg-accent-background-20 text-standard text-background-10':
          !disabled,
        'bg-accent-background-40 hover:bg-accent-background-40 cursor-not-allowed text-accent-background-10':
          disabled,
      }),
      secondary: classNames({
        'bg-background-60 hover:bg-background-50 text-standard text-background-10':
          !disabled,
        'bg-background-60 hover:bg-background-60 cursor-not-allowed text-background-40':
          disabled,
      }),
      tertiary: classNames({
        'bg-background-50 hover:bg-background-40 text-standard text-background-10':
          !disabled,
        'bg-background-50 hover:bg-background-50 cursor-not-allowed text-background-40':
          disabled,
      }),
      quaternary: classNames({
        'bg-background-70 hover:bg-background-60 text-standard text-background-10':
          !disabled,
        'bg-background-70 hover:bg-background-70 cursor-not-allowed text-background-40':
          disabled,
      }),
    };
    return classNames(
      variantsMap[variant],
      'focus:ring-4 text-center relative flex items-center justify-center',
      {
        'rounded-full p-2 text-center min-h-[35px] min-w-[35px]': rounded,
        'rounded-md px-5 py-2.5': !rounded,
      },
      props.className
    );
  }, [variant, disabled, rounded, props.className]);

  const content = to ? (
    <NavLink
      to={to}
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
        {id && (
          <Localized attrs={attrs} vars={vars} elems={elems} id={id}>
            {children}
          </Localized>
        )}
        {!id && children}
      </ButtonContent>
    </NavLink>
  ) : (
    <button type="button" {...props} className={classes} disabled={disabled}>
      <ButtonContent icon={icon} loading={loading}>
        {id && (
          <Localized attrs={attrs} vars={vars} elems={elems} id={id}>
            {children}
          </Localized>
        )}
        {!id && children}
      </ButtonContent>
    </button>
  );

  return content;
}
