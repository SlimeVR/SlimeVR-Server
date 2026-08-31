import classNames from 'classnames';
import { Typography } from '@/components/commons/Typography';

export function SideLegend({
  mirror,
  toggleMirror,
}: {
  mirror: boolean;
  toggleMirror: () => void;
}) {
  return (
    <div
      className="flex items-center gap-1 bg-background-80 rounded-full p-1 w-fit cursor-pointer"
      onClick={toggleMirror}
    >
      <div
        className={classNames(
          'flex items-center gap-2 px-3 py-1',
          mirror ? 'order-2' : 'order-1'
        )}
      >
        <span className="w-2.5 h-2.5 rounded-full bg-background-10 outline outline-4 outline-assign-left" />
        <Typography bold id="tracker_assignment-side-left" />
      </div>
      <div
        className={classNames(
          'flex items-center gap-2 px-3 py-1 rounded-full',
          mirror ? 'order-1' : 'order-2'
        )}
      >
        <span className="w-2.5 h-2.5 rounded-full  bg-background-10 outline outline-4 outline-assign-right" />
        <Typography bold id="tracker_assignment-side-right" />
      </div>
    </div>
  );
}
