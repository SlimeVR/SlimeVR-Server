export function LowerArmIcon({
  width = 24,
  flipped = false,
}: {
  width?: number;
  flipped?: boolean;
}) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      x="0px"
      y="0px"
      width={width}
      viewBox="0 0 50 50"
    >
      <path
        transform={flipped ? 'scale(-1,1) translate(-50,0)' : undefined}
        d="M27.203 1.98c-1.019 0-1.954.56-2.437 1.458L17.26 15.858c-.157.291-.24.619-.24.95v16.22l-4.74 5.49a1.001 1.001 0 0 0 .156 1.407l9.386 7.818a.998.998 0 0 0 1.416-.17l8.354-10.443a13.001 13.001 0 0 0 2.668-7.072L35 20.83A2.83 2.83 0 0 0 32.17 18h-.008a2.83 2.83 0 0 0-2.73 2.086l-2.41 5.935-2.026-3.019L25 16l4.734-10.09c.847-1.836-.491-3.93-2.513-3.93h-.018z"
      ></path>
    </svg>
  );
}
