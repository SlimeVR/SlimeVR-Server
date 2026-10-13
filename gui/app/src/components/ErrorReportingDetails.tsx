import classNames from 'classnames';
import { Localized } from '@fluent/react';
import { CheckIcon } from './commons/icon/CheckIcon';
import { Typography } from './commons/Typography';

const COLLECTED_DATA = [
  'errors',
  'system',
  'recordings',
  'usage',
  'proportions',
] as const;

export function ErrorReportingDetails({
  className = 'bg-background-70',
}: {
  className?: string;
}) {
  return (
    <div
      className={classNames('flex flex-col gap-3 rounded-lg p-4', className)}
    >
      <Localized
        id="settings-interface-behavior-error_tracking-no_personal_data"
        elems={{ b: <b /> }}
      >
        <Typography />
      </Localized>
      <Typography
        bold
        id="settings-interface-behavior-error_tracking-collects"
      />
      <ul className="gap-x-6 gap-y-2">
        {COLLECTED_DATA.map((item) => (
          <li key={item} className="flex items-start gap-2">
            <div className="mt-1 shrink-0 fill-accent-background-10">
              <CheckIcon size={14} />
            </div>
            <Typography
              id={`settings-interface-behavior-error_tracking-collects-${item}`}
            />
          </li>
        ))}
      </ul>
      <Typography id="settings-interface-behavior-error_tracking-footer" />
    </div>
  );
}
