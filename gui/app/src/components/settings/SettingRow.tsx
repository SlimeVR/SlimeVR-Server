import classNames from 'classnames';
import { ReactNode, useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { Typography } from '@/components/commons/Typography';
import { Tooltip } from '@/components/commons/Tooltip';
import { SettingResetButton, SettingResetProps } from './SettingReset';
import { SettingDef, SettingsGroupDef } from './settings-definitions';

export function SettingsGroupLabel({ id }: { id: string }) {
  return (
    <div className="flex items-center gap-3 uppercase">
      <Typography variant="section-title" id={id} />
      {/* Tinted from the text colour so it shows on any surface it lands on. */}
      <div className="h-[1px] flex-grow bg-background-10/15" />
    </div>
  );
}

export interface FocusSettingState {
  focusSetting?: string;
}

export function useFocusSetting<T extends HTMLElement>(settingKey: string) {
  const ref = useRef<T>(null);
  const [highlighted, setHighlighted] = useState(false);
  const { state, key } = useLocation() as {
    state: FocusSettingState | null;
    key: string;
  };
  const targeted = state?.focusSetting === settingKey;

  useEffect(() => {
    if (!targeted) return;
    ref.current?.scrollIntoView({ block: 'center', behavior: 'smooth' });
    setHighlighted(true);
    const timeout = setTimeout(() => setHighlighted(false), 1800);
    return () => clearTimeout(timeout);
  }, [targeted, key]);

  return { ref, highlighted };
}

export function SettingRow({
  setting,
  description,
  details,
  control,
  below,
  controlId,
  disabledReason,
  controlWidth = 'auto',
  reset,
  disabled = false,
  childrenDisabled = false,
  children,
}: {
  setting: SettingDef;
  description?: string | null;
  details?: ReactNode;
  control?: ReactNode;
  below?: ReactNode;
  controlId?: string;
  disabledReason?: string;
  controlWidth?: 'auto' | 'wide';
  reset?: SettingResetProps;
  disabled?: boolean;
  childrenDisabled?: boolean;
  children?: ReactNode;
}) {
  const { ref, highlighted } = useFocusSetting<HTMLFieldSetElement>(
    setting.key
  );
  const descriptionId =
    description === undefined
      ? setting.description
      : (description ?? undefined);
  const hasText = Boolean(descriptionId || details);
  const band = (
    <>
      <div className="col-start-1 row-start-1 min-w-0">
        <Typography variant="section-title" id={setting.title} />
      </div>
      {hasText && (
        <div className="col-start-1 row-start-2 col-span-2 xs:col-span-1 min-w-0 max-w-[75ch]">
          {descriptionId && <Typography id={descriptionId} />}
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
    'grid grid-cols-[minmax(0,1fr)_auto] gap-x-6 gap-y-1 items-center flex-1 min-w-0';

  return (
    <fieldset
      ref={ref}
      disabled={disabled}
      className={classNames(
        'flex flex-col gap-3 min-w-0 pl-1 pr-3 py-3 xs:pl-2 xs:pr-4 xs:py-4 transition-colors',
        !disabled && !highlighted && 'hover:bg-background-70/30',
        highlighted && 'bg-accent-background-40/30'
      )}
    >
      <Tooltip
        content={<Typography id={disabledReason ?? ''} />}
        preferedDirection="top"
        disabled={!disabled || !disabledReason}
      >
        <div
          className={classNames(
            'flex items-start gap-2 min-w-0',
            disabled && 'opacity-50'
          )}
        >
          <div
            className={classNames(
              'w-7 shrink-0 flex items-center justify-center',
              control && controlWidth === 'auto' ? 'h-12 xs:h-6' : 'h-6'
            )}
          >
            {reset && <SettingResetButton {...reset} />}
          </div>
          {controlId ? (
            <label
              htmlFor={controlId}
              className={classNames(bandClasses, !disabled && 'cursor-pointer')}
            >
              {band}
            </label>
          ) : (
            <div className={bandClasses}>{band}</div>
          )}
        </div>
      </Tooltip>
      {below && (
        <div className={classNames('min-w-0 pl-9', disabled && 'opacity-50')}>
          {below}
        </div>
      )}
      {children && (
        <fieldset
          disabled={childrenDisabled}
          className={classNames(
            'flex flex-col gap-4 pl-3 ml-1 xs:pl-4 border-l-2 border-background-60 min-w-0 [&>fieldset]:pr-0',
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
  group,
  details,
  children,
}: {
  group?: SettingsGroupDef;
  details?: ReactNode;
  children?: ReactNode;
}) {
  const title = group?.title;
  const description = group?.description;

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
      <div className="flex flex-col rounded-lg bg-background-80 divide-y divide-background-70 overflow-hidden">
        {children}
      </div>
    </div>
  );
}

export function SettingsAdvanced({ children }: { children?: ReactNode }) {
  return (
    <fieldset className="flex flex-col mt-10 rounded-lg border border-background-10/15 px-2 xs:px-3 pt-4 pb-5 min-w-0">
      <legend className="uppercase px-2 ml-2">
        <Typography variant="section-title" id="settings-advanced_section" />
      </legend>
      <div className="flex flex-col gap-3 min-w-0">{children}</div>
    </fieldset>
  );
}
