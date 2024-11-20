import classNames from 'classnames';

export function FileIcon({
  width = 24,
  isDragging = false,
}: {
  width?: number;
  isDragging?: boolean;
}) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      width={width}
      height="24"
      fill="currentColor"
      className={classNames('transition-transform', isDragging && 'scale-150')}
    >
      <path
        fillRule="evenodd"
        d="M 5.625 1.5 L 9 1.5 C 11.071 1.5 12.75 3.179 12.75 5.25 L 12.75 7.125 C 12.75 8.161 13.59 9 14.625 9 L 16.5 9 C 18.571 9 20.25 10.679 20.25 12.75 L 20.25 20.625 C 20.25 21.66 19.41 22.5 18.375 22.5 L 5.625 22.5 C 4.589 22.5 3.75 21.661 3.75 20.625 L 3.75 3.375 C 3.75 2.339 4.59 1.5 5.625 1.5 Z"
        clipRule="evenodd"
      />
      <path d="M14.25 5.25a5.23 5.23 0 00-1.279-3.434 9.768 9.768 0 016.963 6.963A5.23 5.23 0 0016.5 7.5h-1.875a.375.375 0 01-.375-.375V5.25z" />
    </svg>
  );
}
