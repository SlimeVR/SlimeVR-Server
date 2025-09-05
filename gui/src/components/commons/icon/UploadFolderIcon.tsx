import classNames from 'classnames';

export function UploadFolderIcon({
  width = 24,
  isDragging = false,
}: {
  width?: number;
  isDragging?: boolean;
}) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 5.5499997 4.7600007"
      width={width}
      fill="currentColor"
      className={classNames('transition-transform', isDragging && 'scale-150')}
    >
      <path
        fill-rule="evenodd"
        d="m 4.76,4.76 c 0.44,0 0.79,-0.36 0.79,-0.79 v -2.39 c 0,-0.44 -0.36,-0.79 -0.79,-0.79 H 3.34 c -0.05,0 -0.1,-0.02 -0.14,-0.06 L 2.64,0.17 C 2.53,0.06 2.38,0 2.22,0 H 0.79 C 0.35,0 0,0.36 0,0.79 V 3.97 C 0,4.41 0.36,4.76 0.79,4.76 Z M 2.58,3.71 c 0,0.26 0.4,0.26 0.4,0 V 2.6 L 3.44,3.06 C 3.63,3.23 3.89,2.97 3.72,2.78 L 2.93,1.99 c -0.08,-0.08 -0.2,-0.08 -0.28,0 L 1.86,2.78 C 1.65,2.97 1.95,3.26 2.14,3.06 L 2.6,2.6 Z"
        clip-rule="evenodd"
      />
    </svg>
  );
}
