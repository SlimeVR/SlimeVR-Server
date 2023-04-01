import classNames from 'classnames';
import { useMemo } from 'react';

export function ProgressBar({
  progress,
  parts = 1,
  height = 10,
  colorClass = 'bg-accent-background-20',
  animated = false,
}: {
  progress: number;
  parts?: number;
  height?: number;
  colorClass?: string;
  animated?: boolean;
}) {
  return (
    <div className="flex w-full flex-row gap-2">
      {Array.from({ length: parts }).map((_, key) => (
        <Bar
          index={key}
          key={key}
          progress={progress}
          height={height}
          colorClass={colorClass}
          animated={animated}
          parts={parts}
        ></Bar>
      ))}
    </div>
  );
}

export function Bar({
  index,
  progress,
  parts,
  height,
  animated,
  colorClass,
}: {
  index: number;
  progress: number;
  parts: number;
  height: number;
  colorClass: string;
  animated: boolean;
}) {
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
          'rounded-lg overflow-hidden absolute top-0',
          animated && 'transition-[width,background-color]',
          colorClass
        )}
        style={{
          width: `${value * 100}%`,
          height: `${height}px`,
        }}
      ></div>
    </div>
  );
}
