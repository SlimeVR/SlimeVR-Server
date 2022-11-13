import classNames from 'classnames';
import { useMemo } from 'react';

export function WifiIcon({
  value,
  disabled = false,
}: {
  value: number;
  disabled?: boolean;
}) {
  const percent = useMemo(
    () =>
      value
        ? Math.max(
            Math.min(((value - -95) * (100 - 0)) / (-40 - -95) + 0, 100)
          ) / 100
        : 0,
    [value]
  );

  const y = useMemo(() => (percent ? (1 - percent) * 13 : 0), [percent]);

  const col = useMemo(() => {
    const colorsMap: { [key: number]: string } = {
      0.4: 'fill-status-success',
      0.2: 'fill-status-warning',
      0: 'fill-status-critical',
    };

    const val = Object.keys(colorsMap)
      .filter((key) => +key < percent)
      .sort((a, b) => +b - +a)[0];
    return disabled
      ? 'fill-background-40'
      : colorsMap[+val] || 'fill-background-10';
  }, [percent, disabled]);

  return (
    <svg
      width="16"
      height="13"
      viewBox="0 0 16 13"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M7.799 12.378L15.585 2.67801C13.3492 0.95947 10.6129 0.01903 7.793 1.00136e-05C4.9725 0.0172 2.23528 0.95782 0 2.67801L7.786 12.378L7.793 12.385L7.799 12.378Z"
        fill="#3D6381"
      />
      <mask
        id="mask0_0_1"
        style={{ maskType: 'alpha' }}
        maskUnits="userSpaceOnUse"
        x="0"
        width="16"
        height="13"
        className={classNames(col, 'opacity-100')}
      >
        <path d="M0 2.712L7.782 12.392V12.407L7.795 12.396L15.577 2.716C13.3449 0.980306 10.6044 0.026036 7.777 0C4.95656 0.021826 2.22242 0.975276 0 2.712Z" />
      </mask>
      <g mask="url(#mask0_0_1)" className={classNames(col)}>
        <path
          style={{ transform: `translateY(${y}px)` }}
          d="M0 2.712L7.782 12.392V12.407L7.795 12.396L15.577 2.716C13.3449 0.980306 10.6044 0.026036 7.777 0C4.95656 0.021826 2.22242 0.975276 0 2.712Z"
        />
      </g>
    </svg>
  );
}
