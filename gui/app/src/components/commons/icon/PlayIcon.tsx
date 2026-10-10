export function PlayIcon({ width = 33 }: { width?: number }) {
  return (
    <svg width={width} height="29" viewBox="0 0 24 24">
      <path d="M 4.8398087,0.31374371 21.480589,9.976131 a 2.3403036,2.3403036 0 0 1 0,4.047737 L 4.8398087,23.686256 A 2.3203976,2.3203976 0 0 1 1.354257,21.679602 V 2.3203979 A 2.3203976,2.3203976 0 0 1 4.8398087,0.31374371 Z" />
    </svg>
  );
}

export function PlayCircleIcon({ width = 24 }: { width?: number }) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width={width}
      viewBox="0 0 24 24"
      fill="inherit"
    >
      <path
        fillRule="evenodd"
        d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12Zm14.024-.983a1.125 1.125 0 0 1 0 1.966l-5.603 3.113A1.125 1.125 0 0 1 9 15.113V8.887c0-.857.921-1.4 1.671-.983l5.603 3.113Z"
        clipRule="evenodd"
      />
    </svg>
  );
}
