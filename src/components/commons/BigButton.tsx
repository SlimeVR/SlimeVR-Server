import classNames from 'classnames';
import React, { ReactChild } from 'react';

export function BigButton({
  text,
  icon,
  disabled,
  onClick,
  ...props
}: {
  text: string;
  disabled?: boolean;
  icon: ReactChild;
} & React.HTMLAttributes<HTMLButtonElement>) {
  return (
    <button
      disabled={disabled}
      onClick={onClick}
      {...props}
      type="button"
      className={classNames(
        'flex w-full justify-center rounded-md py-5 gap-5 px-5 cursor-pointer items-center ',
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
      <div className="flex text-default flex-grow">{text}</div>
    </button>
  );
}
