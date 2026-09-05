import classNames from 'classnames';

export function MinimiseIcon({ className }: { className?: string }) {
  return (
    <svg
      width="35"
      height="35"
      className={classNames('stroke-window-icon', className)}
      viewBox="0 0 31 29"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path d="M20.5 15.5H10.5" strokeLinecap="round" />
    </svg>
  );
}
