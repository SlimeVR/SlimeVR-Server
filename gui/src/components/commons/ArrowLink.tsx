import classNames from 'classnames';
import { ReactChild, useMemo } from 'react';
import { NavLink } from 'react-router-dom';
import { ArrowLeftIcon, ArrowRightIcon } from './icon/ArrowIcons';

export function ArrowLink({
  to,
  children,
  direction = 'left',
  variant = 'flat',
}: {
  to: string;
  children: ReactChild;
  direction?: 'left' | 'right';
  variant?: 'flat' | 'boxed';
}) {
  const classes = useMemo(() => {
    const variantsMap = {
      flat: classNames('justify-start'),
      boxed: classNames(
        'justify-between bg-background-70 rounded-md hover:bg-background-60 p-3'
      ),
    };
    return classNames(
      variantsMap[variant],
      'flex gap-2 hover:fill-background-10 hover:text-background-10 fill-background-30 text-background-30'
    );
  }, [variant]);

  return (
    <NavLink to={to} className={classes}>
      {direction === 'left' && (
        <div className="flex flex-col justify-center">
          <ArrowLeftIcon></ArrowLeftIcon>
        </div>
      )}
      {children}
      {direction === 'right' && (
        <div className="flex flex-col justify-center">
          <ArrowRightIcon></ArrowRightIcon>
        </div>
      )}
    </NavLink>
  );
}
