export function MetricsIcon({
  size = 18,
  className,
}: {
  size?: number;
  className?: string;
}) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      className={className}
      xmlns="http://www.w3.org/2000/svg"
    >
      <path d="M5 19h14v2H3V3h2v16zm2-8h3v6H7v-6zm5-5h3v11h-3V6zm5-3h3v14h-3V3z" />
    </svg>
  );
}
