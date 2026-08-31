export function ButterflyIcon({ width = 16 }: { width?: number }) {
  return (
    <svg
      width={width}
      height={width}
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M11.2 7.6L9.8 4.8"
        stroke="white"
        strokeWidth="1"
        strokeLinecap="round"
      />
      <path
        d="M12.8 7.6L14.2 4.8"
        stroke="white"
        strokeWidth="1"
        strokeLinecap="round"
      />
      <rect x="11" y="6.5" width="2" height="11" rx="1" fill="white" />
      <path
        d="M11 8.5C9.3 4.6 4.3 3.6 2.3 6.3C0.4 8.9 2.4 12.3 5.6 11.9C8 11.6 10 10 11 8.5Z"
        fill="white"
      />
      <path
        d="M13 8.5C14.7 4.6 19.7 3.6 21.7 6.3C23.6 8.9 21.6 12.3 18.4 11.9C16 11.6 14 10 13 8.5Z"
        fill="white"
      />
      <path
        d="M11 14.5C9.6 17.6 5.9 18.6 4.2 16.5C2.6 14.6 4 12 6.7 12.2C8.5 12.4 10 13.4 11 14.5Z"
        fill="white"
      />
      <path
        d="M13 14.5C14.4 17.6 18.1 18.6 19.8 16.5C21.4 14.6 20 12 17.3 12.2C15.5 12.4 14 13.4 13 14.5Z"
        fill="white"
      />
    </svg>
  );
}
