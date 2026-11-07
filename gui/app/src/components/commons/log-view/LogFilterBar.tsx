import { useLocalization } from '@fluent/react';
import { ReactNode } from 'react';
import { LogLevel } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import {
  DropdownInside,
  DropdownInsideProps,
} from '@/components/commons/Dropdown';
import { InputInside } from '@/components/commons/Input';
import { PopoverMenu } from '@/components/commons/PopoverMenu';
import { Typography } from '@/components/commons/Typography';
import { TuneIcon } from '@/components/commons/icon/TuneIcon';
import { LEVEL_FILTERS } from './log-line';

export type FilterPlace = 'inline' | 'drawer';

type DistributiveOmit<T, K extends PropertyKey> = T extends unknown
  ? Omit<T, K>
  : never;

export function FilterDropdown({
  place,
  name,
  ...props
}: { place: FilterPlace } & DistributiveOmit<
  DropdownInsideProps,
  | 'variant'
  | 'direction'
  | 'display'
  | 'menuWidth'
  | 'alignment'
  | 'inside'
  | 'placeholder'
>) {
  return (
    <div className={place === 'inline' ? 'w-40' : undefined}>
      <DropdownInside
        {...props}
        name={`${name}-${place}`}
        variant={place === 'drawer' ? 'secondary' : 'primary'}
        direction="down"
        display="block"
        menuWidth={place === 'inline' ? 'w-60' : undefined}
        alignment="left"
        inside="label"
        placeholder=""
      />
    </div>
  );
}

export function LevelFilter({
  place,
  name,
  value,
  onChange,
}: {
  place: FilterPlace;
  name: string;
  value: LogLevel;
  onChange: (level: LogLevel) => void;
}) {
  const { l10n } = useLocalization();
  return (
    <FilterDropdown
      place={place}
      name={name}
      label={l10n.getString('settings-logs-level')}
      value={String(value)}
      onChange={(level) => onChange(Number(level))}
      items={LEVEL_FILTERS.map(({ level, id }) => ({
        value: String(level),
        label: <Typography tag="span" id={id} />,
      }))}
    />
  );
}

export function DotLabel({
  color,
  children,
}: {
  color: string;
  children: ReactNode;
}) {
  return (
    <span className="flex items-center gap-2 min-w-0">
      <span
        className="w-2 h-2 rounded-full shrink-0"
        style={{ backgroundColor: color }}
      />
      <span className="truncate">{children}</span>
    </span>
  );
}

export function LogFilterBar({
  renderFilters,
  filtersActive,
  searchName,
  searchPlaceholder,
  query,
  onQueryChange,
  leading,
  trailing,
}: {
  renderFilters: (place: FilterPlace) => ReactNode;
  filtersActive: boolean;
  searchName: string;
  searchPlaceholder: string;
  query: string;
  onQueryChange: (query: string) => void;
  leading?: ReactNode;
  trailing?: ReactNode;
}) {
  return (
    <div className="flex flex-wrap gap-2 items-center">
      {leading}
      <div className="contents mobile:hidden">{renderFilters('inline')}</div>
      <div className="flex-grow basis-48 min-w-0">
        <InputInside
          name={searchName}
          type="text"
          placeholder={searchPlaceholder}
          value={query}
          onChange={(e) => onQueryChange((e.target as HTMLInputElement).value)}
        />
      </div>
      <div className="xs:hidden">
        <PopoverMenu
          drawerTitle={
            <Typography variant="section-title" id="settings-logs-filters" />
          }
          trigger={(triggerProps) => (
            <Button
              variant="secondary"
              {...triggerProps}
              id="settings-logs-filters"
              iconOnly
              icon={
                <span className="relative flex fill-background-10">
                  <TuneIcon size={18} />
                  {filtersActive && (
                    <span className="absolute -top-1 -right-1 w-2 h-2 rounded-full bg-accent-background-10" />
                  )}
                </span>
              }
            />
          )}
        >
          <div className="flex flex-col gap-4 p-4">
            {renderFilters('drawer')}
          </div>
        </PopoverMenu>
      </div>
      {trailing}
    </div>
  );
}
