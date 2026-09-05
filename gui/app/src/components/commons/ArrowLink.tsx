import { openUrl } from '@/hooks/crossplatform';
import classNames from 'classnames';
import { ReactNode, useMemo } from 'react';
import { NavLink } from 'react-router-dom';
import { ArrowLeftIcon, ArrowRightIcon } from './icon/ArrowIcons';

export function ArrowLink({
  to,
  children,
  state,
  direction = 'left',
  variant = 'flat',
}: {
  to: string;
  children: ReactNode;
  state?: any;
  direction?: 'left' | 'right';
  variant?: 'flat' | 'boxed' | 'boxed-2';
}) {
  const classes = useMemo(() => {
    const variantsMap = {
      flat: classNames('justify-start'),
      boxed: classNames(
        'justify-between bg-background-70 rounded-md hover:bg-background-60 p-3'
      ),
      'boxed-2': classNames(
        'justify-between bg-background-60 rounded-md hover:bg-background-50 p-3'
      ),
    };
    return classNames(
      variantsMap[variant],
      'flex gap-2 hover:fill-background-10 fill-background-30 text-background-10'
    );
  }, [variant]);

  const content = (
    <>
      {direction === 'left' && (
        <div className="flex flex-col justify-center">
          <ArrowLeftIcon />
        </div>
      )}
      {children}
      {direction === 'right' && (
        <div className="flex flex-col justify-center">
          <ArrowRightIcon />
        </div>
      )}
    </>
  );

  const isExternal = /^https?:\/\//.test(to);

  if (isExternal) {
    return (
      <span
        onClick={() => openUrl(to)}
        className={classNames(classes, 'cursor-pointer')}
      >
        {content}
      </span>
    );
  }

  return (
    <NavLink to={to} state={state} className={classes}>
      {content}
    </NavLink>
  );
}
