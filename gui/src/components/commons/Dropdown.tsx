import classNames from 'classnames';
import { ReactNode, useEffect, useRef, useState } from 'react';
import { Control, Controller, UseControllerProps } from 'react-hook-form';
import { ArrowDownIcon, ArrowUpIcon } from './icon/ArrowIcons';
import { a11yClick } from '@/utils/a11y';

type DropdownItem = {
  value: string;
  label: ReactNode;
};

export type DropdownDirection = 'up' | 'down';

type DropdownProps = {
  direction?: DropdownDirection;
  variant?: 'primary' | 'secondary' | 'tertiary';
  alignment?: 'left' | 'right';
  display?: 'fit' | 'block';
  placeholder: string;
  control: Control<any>;
  name: string;
  items: DropdownItem[];
  maxHeight?: string | number;
  rules?: UseControllerProps<any>['rules'];
};

function DropdownItem({
  item,
  variant,
  onSelected,
  isOpen,
  value,
}: {
  item: DropdownItem;
  variant: Required<DropdownProps>['variant'];
  onSelected: () => void;
  isOpen: boolean;
  value: any;
}) {
  const variantStyles = {
    primary:
      'text-background-20 checked-hover:text-background-10 checked-hover:bg-background-50 focus:text-background-10 focus:bg-background-50',
    secondary:
      'text-background-20 checked-hover:text-background-10 checked-hover:bg-background-60 focus:text-background-10 focus:bg-background-60',
    tertiary:
      'bg-accent-background-30 checked-hover:bg-accent-background-20 focus:bg-accent-background-20',
  };

  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (isOpen && value === item.value) {
      ref.current?.focus();
      ref.current?.scrollIntoView();
    }
  }, [isOpen]);

  return (
    <div
      className={classNames(
        'py-2 px-4 min-w-max cursor-pointer outline-none',
        variantStyles[variant]
      )}
      onClick={onSelected}
      onKeyDown={(e) => a11yClick(e) && onSelected()}
      tabIndex={isOpen ? 0 : -1}
      aria-hidden={!isOpen}
      data-checked={item.value === value}
      ref={ref}
    >
      {item.label}
    </div>
  );
}

function DropdownList({
  isOpen,
  onSelect,
  value,
  display,
  alignment,
  direction,
  items,
  variant,
  maxHeight,
}: {
  isOpen: boolean;
  onSelect: (item: DropdownItem) => void;
  value: any;
} & Pick<
  Required<DropdownProps>,
  'display' | 'alignment' | 'direction' | 'items' | 'variant' | 'maxHeight'
>) {
  const variantStyles = {
    primary: 'bg-background-60',
    secondary: 'bg-background-70',
    tertiary: 'bg-accent-background-30',
  };

  const getDisplayClasses = () => {
    if (display === 'block') {
      return 'inset-x-0';
    }
    return alignment === 'left' ? 'left-0 w-max' : 'right-0 w-max';
  };

  return (
    <div
      className={classNames(
        'grid absolute overflow-hidden transition-[grid-template-rows] rounded',
        isOpen ? 'grid-rows-[1fr]' : 'grid-rows-[0fr]',
        direction === 'up' ? 'bottom-full mb-3' : 'top-full mt-3',
        getDisplayClasses(),
        variantStyles[variant]
      )}
      onTransitionEnd={(e) => {
        if (!isOpen) {
          (e.target as HTMLDivElement).scrollTo({ top: 0 });
        }
      }}
    >
      <ul
        className="flex flex-col min-h-0 text-sm overflow-y-scroll dropdown-scroll overscroll-contain"
        style={{ maxHeight }}
      >
        {items.map((item) => (
          <DropdownItem
            item={item}
            variant={variant}
            onSelected={() => onSelect(item)}
            isOpen={isOpen}
            key={item.value}
            value={value}
          />
        ))}
      </ul>
    </div>
  );
}

export function Dropdown({
  direction = 'up',
  variant = 'primary',
  alignment = 'right',
  display = 'fit',
  placeholder,
  control,
  name,
  items,
  maxHeight = '50vh',
  rules,
}: DropdownProps) {
  const [isOpen, setIsOpen] = useState(false);

  const variantStyles = {
    primary: 'bg-background-60 hover:bg-background-50',
    secondary: 'bg-background-70 hover:bg-background-60',
    tertiary: 'bg-accent-background-30 hover:bg-accent-background-20',
  };

  const displayStyles = {
    fit: 'w-fit',
    block: 'w-full',
  };

  const getShownValue = (value: any) =>
    value
      ? (items.find((item) => item.value === value)?.label ?? placeholder)
      : placeholder;

  const ref = useRef<HTMLDivElement>(null);

  return (
    <Controller
      name={name}
      control={control}
      rules={rules}
      render={({ field: { onChange, value } }) => (
        <>
          <div
            className={classNames(
              'min-h-[42px] text-background-10 text-left dropdown relative',
              displayStyles[display]
            )}
            onClick={() => setIsOpen(!isOpen)}
            onKeyDown={(e) => a11yClick(e) && setIsOpen(!isOpen)}
            onBlur={(e) => {
              if (e.currentTarget.contains(e.relatedTarget)) {
                return;
              }

              setIsOpen(false);
            }}
          >
            <div
              className={classNames(
                'flex flex-row justify-between items-center gap-2 pl-3 pr-5 py-3 rounded-md cursor-pointer focus:ring-4',
                variantStyles[variant]
              )}
              tabIndex={0}
              ref={ref}
            >
              {getShownValue(value)}
              <div className="fill-background-10">
                {direction === 'up' ? (
                  <ArrowUpIcon size={16} />
                ) : (
                  <ArrowDownIcon size={16} />
                )}
              </div>
            </div>
            <DropdownList
              alignment={alignment}
              direction={direction}
              display={display}
              isOpen={isOpen}
              items={items}
              onSelect={(item: DropdownItem) => {
                onChange(item.value);
                ref.current?.focus();
              }}
              variant={variant}
              maxHeight={maxHeight}
              value={value}
            />
          </div>
        </>
      )}
    ></Controller>
  );
}
