import classNames from 'classnames';
import {
  forwardRef,
  ReactNode,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from 'react';
import {
  Control,
  FieldError,
  useController,
  UseControllerProps,
} from 'react-hook-form';
import { ArrowDownIcon, ArrowUpIcon } from './icon/ArrowIcons';
import { a11yClick } from '@/utils/a11y';
import { createPortal } from 'react-dom';

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
  name: string;
  items: DropdownItem[];
  maxHeight?: string | number;
  error?: FieldError;
};

function DropdownItem({
  item,
  variant,
  onSelected,
  isOpen,
  value,
  innerFocusValue,
  name,
}: {
  item: DropdownItem;
  variant: Required<DropdownProps>['variant'];
  onSelected: () => void;
  isOpen: boolean;
  value: any;
  innerFocusValue: string | null;
  name: string;
}) {
  const variantStyles = {
    primary:
      'text-background-20 checked-hover:text-background-10 checked-hover:bg-background-50 focus:text-background-10 focus:bg-background-50',
    secondary:
      'text-background-20 checked-hover:text-background-10 checked-hover:bg-background-60 focus:text-background-10 focus:bg-background-60',
    tertiary:
      'bg-accent-background-30 checked-hover:bg-accent-background-20 focus:bg-accent-background-20 text-background-10',
  };

  const ref = useRef<HTMLDivElement>(null);

  useLayoutEffect(() => {
    if (!innerFocusValue) {
      return;
    }
    if (innerFocusValue === item.value) {
      ref.current?.scrollIntoView({ block: 'nearest' });
    }
  }, [innerFocusValue]);

  useLayoutEffect(() => {
    if (!isOpen) {
      return;
    }

    if (
      innerFocusValue === item.value ||
      (!innerFocusValue && item.value === value)
    ) {
      ref.current?.scrollIntoView({ block: 'nearest' });
    }
  }, [isOpen]);

  return (
    <div
      className={classNames(
        'py-2 px-4 min-w-max cursor-pointer',
        variantStyles[variant],
        innerFocusValue === item.value && 'ring-inset ring-4'
      )}
      onClick={onSelected}
      onKeyDown={(e) => a11yClick(e) && onSelected()}
      tabIndex={-1}
      aria-hidden={!isOpen}
      data-checked={item.value === value}
      ref={ref}
      id={`__dropdownList-${name}-item-${item.value}`}
    >
      {item.label}
    </div>
  );
}

type DropdownListProps = {
  isOpen: boolean;
  onSelect: (item: DropdownItem) => void;
  value: any;
  innerFocusValue: string | null;
} & Pick<
  Required<DropdownProps>,
  | 'display'
  | 'alignment'
  | 'direction'
  | 'items'
  | 'variant'
  | 'maxHeight'
  | 'name'
>;

const DropdownList = forwardRef<HTMLDivElement, DropdownListProps>(function (
  {
    isOpen,
    onSelect,
    value,
    innerFocusValue,
    display,
    alignment,
    direction,
    items,
    variant,
    maxHeight,
    name,
  },
  ref
) {
  const variantStyles = {
    primary: 'bg-background-60',
    secondary: 'bg-background-70',
    tertiary: 'bg-accent-background-30',
  };

  const getDisplayStyle = () => {
    if (display === 'block') {
      return {
        left: 'var(--dropdown-field-left)',
        right: 'var(--dropdown-field-right)',
      };
    }
    return alignment === 'left'
      ? { left: 'var(--dropdown-field-left)' }
      : { right: 'var(--dropdown-field-right)' };
  };

  const directionStyles = {
    up: {
      bottom: 'calc(var(--dropdown-field-top) + 0.75rem)',
    },
    down: {
      top: 'calc(var(--dropdown-field-bottom) + 0.75rem)',
    },
  };

  return (
    <div
      className={classNames(
        'grid fixed z-50 overflow-hidden transition-[grid-template-rows] rounded',
        isOpen ? 'grid-rows-[1fr]' : 'grid-rows-[0fr]',
        variantStyles[variant]
      )}
      style={{
        ...getDisplayStyle(),
        ...directionStyles[direction],
      }}
      onTransitionEnd={(e) => {
        if (!isOpen) {
          (e.target as HTMLDivElement).scrollTo({ top: 0 });
        }
      }}
      ref={ref}
      id={`__dropdownList-${name}`}
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
            innerFocusValue={innerFocusValue}
            name={name}
          />
        ))}
      </ul>
    </div>
  );
});

export function DropdownInside({
  direction = 'up',
  variant = 'primary',
  alignment = 'right',
  display = 'fit',
  placeholder,
  name,
  items,
  maxHeight = '50vh',
  value,
  onChange,
  error,
}: DropdownProps & { value: string; onChange: (value: string) => void }) {
  const [isOpen, setIsOpen] = useState(false);

  useLayoutEffect(() => {
    ref.current?.focus();
  }, [value]);

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
  const listRef = useRef<HTMLDivElement>(null);

  const [innerFocusIndex, setInnerFocusIndex] = useState<number | null>(null);
  const getCurrentActiveIndex = () => {
    return items.findIndex((item) => item.value === value);
  };
  const innerFocusPrev = () => {
    const current = innerFocusIndex ?? getCurrentActiveIndex();

    setInnerFocusIndex(current > 0 ? current - 1 : current);
  };
  const innerFocusNext = () => {
    const current = innerFocusIndex ?? getCurrentActiveIndex();

    setInnerFocusIndex(current < items.length - 1 ? current + 1 : current);
  };

  const updateFieldBoundingRect = () => {
    if (!ref.current || !listRef.current) {
      return;
    }
    const boundingRect = ref.current.getBoundingClientRect();

    const left = boundingRect.left;
    const right = window.innerWidth - boundingRect.right;
    const top = window.innerHeight - boundingRect.top;
    const bottom = boundingRect.bottom;
    listRef.current?.style.setProperty('--dropdown-field-left', `${left}px`);
    listRef.current?.style.setProperty('--dropdown-field-right', `${right}px`);
    listRef.current?.style.setProperty('--dropdown-field-top', `${top}px`);
    listRef.current?.style.setProperty(
      '--dropdown-field-bottom',
      `${bottom}px`
    );
  };
  useLayoutEffect(updateFieldBoundingRect, [ref.current]);
  window.addEventListener('scroll', updateFieldBoundingRect, true);
  window.addEventListener('resize', updateFieldBoundingRect, true);

  useEffect(() => {
    if (!isOpen) {
      setInnerFocusIndex(null);
    }
  }, [isOpen]);

  return (
    <>
      <div
        className={classNames(
          'min-h-[42px] text-background-10 text-left dropdown',
          displayStyles[display]
        )}
        onClick={() => setIsOpen(!isOpen)}
        onKeyDown={(e) => {
          if (!isOpen) {
            if (a11yClick(e)) {
              setInnerFocusIndex(
                items.findIndex((item) => item.value === value)
              );
              setIsOpen(!isOpen);
              e.preventDefault();
              return;
            }

            if (e.key === 'ArrowDown') {
              setInnerFocusIndex(0);
              setIsOpen(true);
              e.preventDefault();
              return;
            }

            if (e.key === 'ArrowUp') {
              setInnerFocusIndex(items.length - 1);
              setIsOpen(true);
              e.preventDefault();
              return;
            }
          } else {
            if (a11yClick(e)) {
              e.preventDefault();
              if (innerFocusIndex === null) {
                setIsOpen(false);
                return;
              }

              onChange(items[innerFocusIndex].value);
              setIsOpen(false);
            }
            switch (e.key) {
              case 'ArrowUp':
                innerFocusPrev();
                e.preventDefault();
                return;
              case 'ArrowDown':
                innerFocusNext();
                e.preventDefault();
                return;
              case 'Escape':
                setIsOpen(false);
                return;
              case 'Home':
                setInnerFocusIndex(0);
                return;
              case 'End':
                setInnerFocusIndex(items.length - 1);
                return;
            }
          }
        }}
        onBlur={(e) => {
          if (
            e.currentTarget.contains(e.relatedTarget) ||
            listRef?.current?.contains(e.relatedTarget)
          ) {
            return;
          }

          setIsOpen(false);
        }}
      >
        <div
          className={classNames(
            'flex flex-row justify-between items-center gap-2 pl-3 pr-5 py-3 rounded-md cursor-pointer focus:ring-4 relative',
            variantStyles[variant]
          )}
          tabIndex={0}
          ref={ref}
          aria-controls={`__dropdownList-${name}`}
          aria-activedescendant={
            innerFocusIndex === null
              ? ''
              : `__dropdownList-${name}-item-${items[innerFocusIndex].value}`
          }
          role="combobox"
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
        {error?.message && (
          <div className="text-status-critical">{error.message}</div>
        )}
        {createPortal(
          <DropdownList
            alignment={alignment}
            direction={direction}
            display={display}
            isOpen={isOpen}
            items={items}
            onSelect={(item: DropdownItem) => {
              ref.current?.focus();
              onChange(item.value);
            }}
            variant={variant}
            maxHeight={maxHeight}
            value={value}
            ref={listRef}
            innerFocusValue={
              innerFocusIndex === null ? null : items[innerFocusIndex].value
            }
            name={name}
          />,
          document.body
        )}
      </div>
    </>
  );
}

export function Dropdown({
  control,
  name,
  rules,
  ...props
}: DropdownProps & {
  control: Control<any>;
  rules?: UseControllerProps<any>['rules'];
}) {
  const {
    field: { value, onChange },
  } = useController({ name, control, rules });

  return (
    <DropdownInside value={value} name={name} {...props} onChange={onChange} />
  );
}
