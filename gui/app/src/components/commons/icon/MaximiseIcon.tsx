import classNames from 'classnames';

export function MaximiseIcon({ className }: { className?: string }) {
  return (
    <svg
      width="35"
      height="35"
      className={classNames('stroke-window-icon', className)}
      viewBox="0 0 31 29"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path d="M18 11.5H14C13.1716 11.5 12.5 12.1716 12.5 13V17C12.5 17.8284 13.1716 18.5 14 18.5H18C18.8284 18.5 19.5 17.8284 19.5 17V13C19.5 12.1716 18.8284 11.5 18 11.5Z" />
    </svg>
  );
}
