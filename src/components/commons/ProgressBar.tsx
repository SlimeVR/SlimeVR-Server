import classNames from 'classnames';
import { useMemo } from 'react';

export function ProgressBar({
  progress,
  parts = 1,
  height = 10,
}: {
  progress: number;
  parts?: number;
  height?: number;
}) {
  const Bar = ({ index }: { index: number }) => {
    const value = useMemo(
      () => Math.min(Math.max((progress * parts) / 1 - index, 0), 1),
      [index, progress]
    );
    return (
      <div
        className="flex relative flex-grow bg-background-50 rounded-lg overflow-hidden"
        style={{ height: `${height}px` }}
      >
        <div
          className={classNames(
            'bg-accent-background-20 rounded-lg overflow-hidden absolute top-0'
          )}
          style={{
            width: `${value * 100}%`,
            height: `${height}px`,
          }}
        ></div>
      </div>
    );
  };

  return (
    <div className="flex w-full flex-row gap-2">
      {Array.from({ length: parts }).map((_, key) => (
        <Bar index={key} key={key}></Bar>
      ))}
    </div>
  );
}
