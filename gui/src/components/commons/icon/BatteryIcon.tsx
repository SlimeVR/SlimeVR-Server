import classNames from 'classnames';
import { useMemo } from 'react';

export function BatteryIcon({
  value,
  disabled = false,
  charging,
}: {
  value: number;
  disabled?: boolean;
  charging: boolean;
}) {
  const col = useMemo(() => {
    const colorsMap: { [key: number]: string } = {
      0.4: 'fill-status-success',
      0.2: 'fill-status-warning',
      0: 'fill-status-critical',
    };

    const val = Object.keys(colorsMap)
      .filter((key) => +key < value)
      .sort((a, b) => +b - +a)[0];
    return disabled
      ? 'fill-background-40'
      : colorsMap[+val] || 'fill-background-10';
  }, [value, disabled]);

  return (
    <svg
      width="19"
      height="11"
      viewBox="0 0 19 9"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M11.0833 0H1.31203C0.995003 0.00131561 0.691347 0.121213 0.467167 0.333594C0.242986 0.545976 0.116428 0.83365 0.115039 1.134V7.383C0.114754 7.68458 0.240506 7.97399 0.464808 8.18799C0.689109 8.40198 0.993714 8.52315 1.31203 8.525H11.0833V0Z"
        fill="#3D6381"
      />
      <path
        d="M15.0005 8.525C15.3175 8.52368 15.6212 8.40379 15.8454 8.19141C16.0696 7.97902 16.1961 7.69135 16.1975 7.391V5.968H17.9972V2.558H16.1975V1.134C16.1961 0.83365 16.0696 0.545976 15.8454 0.333594C15.6212 0.121213 15.3175 0.00131561 15.0005 0H10.9672V8.525H15.0005Z"
        fill="#3D6381"
      />
      <mask
        id="mask0_4_39"
        style={{ maskType: 'alpha' }}
        maskUnits="userSpaceOnUse"
        x="0"
        y="0"
        width="18"
        height="9"
      >
        <path
          d="M11.0833 0H1.31203C0.995003 0.00131561 0.691347 0.121213 0.467167 0.333594C0.242986 0.545976 0.116428 0.83365 0.115039 1.134V7.383C0.114754 7.68458 0.240506 7.97399 0.464808 8.18799C0.689109 8.40198 0.993714 8.52315 1.31203 8.525H11.0833V0Z"
          fill="#3D6381"
        />
        <path
          d="M15.0005 8.525C15.3175 8.52368 15.6212 8.40379 15.8454 8.19141C16.0696 7.97902 16.1961 7.69135 16.1975 7.391V5.968H17.9972V2.558H16.1975V1.134C16.1961 0.83365 16.0696 0.545976 15.8454 0.333594C15.6212 0.121213 15.3175 0.00131561 15.0005 0H10.9672V8.525H15.0005Z"
          fill="#3D6381"
        />
      </mask>
      <g mask="url(#mask0_4_39)" className={classNames(col, 'opacity-100')}>
        <rect width={value * 18} height="9" />
      </g>
      {charging && (
        <path
          d="M 0.93561138,11.744353 2.4349252,6.1488377 H 0.0312815 L 3.5761014,0.00903018 2.2061799,5.1216451 h 2.4534885 z"
          fill="#081e30"
          transform="translate(5,-1)"
        />
      )}
    </svg>
  );
}
