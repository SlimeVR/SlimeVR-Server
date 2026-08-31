import classNames from 'classnames';
import { Typography } from '@/components/commons/Typography';

function Tab({
  id,
  active,
  disabled,
}: {
  id: string;
  active?: boolean;
  disabled?: boolean;
}) {
  return (
    <div
      className={classNames(
        'px-4 py-1.5 rounded-md',
        active && 'bg-background-50',
        disabled && 'opacity-40 cursor-not-allowed',
        !disabled && !active && 'cursor-pointer hover:bg-background-60'
      )}
    >
      <Typography bold={active} id={id} />
    </div>
  );
}

export function TrackerAssignmentTabs() {
  return (
    <div className="flex items-center gap-1 bg-background-70 rounded-lg p-1 w-fit">
      <Tab id="tracker_assignment-tab-body" active />
      <Tab id="tracker_assignment-tab-fingers" disabled />
      <Tab id="tracker_assignment-tab-toes" disabled />
    </div>
  );
}
