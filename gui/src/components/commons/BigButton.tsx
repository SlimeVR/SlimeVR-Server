import classNames from 'classnames';
import React, { ReactNode } from 'react';

export function BigButton({
  text,
  icon,
  disabled,
  onClick,
  ...props
}: {
  text: string;
  disabled?: boolean;
  icon: ReactNode;
} & React.HTMLAttributes<HTMLButtonElement>) {
  return (
    <button
      disabled={disabled}
      onClick={onClick}
      {...props}
      type="button"
      className={classNames(
        'flex flex-col justify-center rounded-md py-3 gap-1 px-3 cursor-pointer items-center',
        {
          'bg-background-60 hover:bg-background-60 cursor-not-allowed text-background-40 fill-background-40':
            disabled,
          'bg-background-60 hover:bg-background-50 text-standard fill-background-10':
            !disabled,
        },
        props.className
      )}
    >
      <div className="flex justify-around">{icon}</div>
      <div className="flex text-default flex-grow items-center">{text}</div>
    </button>
  );
}
