import classNames from 'classnames';
import { ReactNode } from 'react';
import { Typography } from '@/components/commons/Typography';
import { Tooltip } from '@/components/commons/Tooltip';

export function SettingsGroupLabel({ id }: { id: string }) {
  return (
    <div className="flex items-center gap-3 uppercase">
      <Typography variant="section-title" id={id} />
      {/* Tinted from the text colour so it shows on any surface it lands on. */}
      <div className="h-[1px] flex-grow bg-background-10/15" />
    </div>
  );
}

export function SettingRow({
  title,
  description,
  details,
  control,
  below,
  controlId,
  disabledReason,
  controlWidth = 'auto',
  disabled = false,
  childrenDisabled = false,
  children,
}: {
  title: string;
  description?: string;
  details?: ReactNode;
  control?: ReactNode;
  below?: ReactNode;
  controlId?: string;
  disabledReason?: string;
  controlWidth?: 'auto' | 'wide';
  disabled?: boolean;
  childrenDisabled?: boolean;
  children?: ReactNode;
}) {
  const hasText = Boolean(description || details);
  const band = (
    <>
      <div className="col-start-1 row-start-1 min-w-0">
        <Typography variant="section-title" id={title} />
      </div>
      {hasText && (
        <div className="col-start-1 row-start-2 col-span-2 xs:col-span-1 min-w-0 max-w-[75ch]">
          {description && <Typography id={description} />}
          {details}
        </div>
      )}
      {control && (
        <div
          className={classNames('flex self-center justify-self-end min-w-0', {
            'col-start-2 row-start-1 xs:row-span-2': controlWidth === 'auto',
            [classNames(
              'col-start-1 col-span-2 w-full',
              hasText ? 'row-start-3' : 'row-start-2',
              'xs:col-start-2 xs:col-span-1 xs:row-start-1 xs:row-span-2 xs:w-56 lg:w-72'
            )]: controlWidth === 'wide',
          })}
        >
          {control}
        </div>
      )}
    </>
  );
  const bandClasses =
    'grid grid-cols-[minmax(0,1fr)_auto] gap-x-6 gap-y-1 items-center';

  return (
    <fieldset
      disabled={disabled}
      className={classNames(
        'flex flex-col gap-3 min-w-0 px-3 py-3 xs:px-4 xs:py-4 transition-colors',
        !disabled && 'hover:bg-background-70/30'
      )}
    >
      <Tooltip
        content={<Typography id={disabledReason ?? ''} />}
        preferedDirection="top"
        disabled={!disabled || !disabledReason}
      >
        {controlId ? (
          <label
            htmlFor={controlId}
            className={classNames(
              bandClasses,
              disabled ? 'opacity-50' : 'cursor-pointer'
            )}
          >
            {band}
          </label>
        ) : (
          <div className={classNames(bandClasses, disabled && 'opacity-50')}>
            {band}
          </div>
        )}
      </Tooltip>
      {below && <div className="min-w-0">{below}</div>}
      {children && (
        <fieldset
          disabled={childrenDisabled}
          className={classNames(
            'flex flex-col gap-4 pl-3 ml-1 xs:pl-4 border-l-2 border-background-60 min-w-0',
            childrenDisabled && 'opacity-50'
          )}
        >
          {children}
        </fieldset>
      )}
    </fieldset>
  );
}

export function SettingsGroup({
  title,
  description,
  details,
  children,
}: {
  title?: string;
  description?: string;
  details?: ReactNode;
  children?: ReactNode;
}) {
  return (
    <div className="flex flex-col gap-3 min-w-0 mt-8 first:mt-0">
      {(title || description || details) && (
        <div className="flex flex-col gap-2 min-w-0 px-3 xs:px-4">
          {title && <SettingsGroupLabel id={title} />}
          {(description || details) && (
            <div className="flex flex-col gap-1 min-w-0 max-w-[75ch]">
              {description && <Typography id={description} />}
              {details}
            </div>
          )}
        </div>
      )}
      <div className="flex flex-col rounded-2xl bg-background-80 divide-y divide-background-70 overflow-hidden">
        {children}
      </div>
    </div>
  );
}

export function SettingsAdvanced({ children }: { children?: ReactNode }) {
  return (
    <fieldset className="flex flex-col mt-10 rounded-2xl border border-background-50 px-2 xs:px-3 pt-4 pb-5 min-w-0">
      <legend className="uppercase px-2 ml-2">
        <Typography variant="section-title" id="settings-advanced_section" />
      </legend>
      <div className="flex flex-col gap-3 min-w-0">{children}</div>
    </fieldset>
  );
}
