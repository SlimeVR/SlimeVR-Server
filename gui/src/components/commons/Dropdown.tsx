import classNames from 'classnames';
import {
  forwardRef,
  ReactNode,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from 'react';
import { Control, useController, UseControllerProps } from 'react-hook-form';
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

type DropdownListProps = {
  isOpen: boolean;
  onSelect: (item: DropdownItem) => void;
  value: any;
} & Pick<
  Required<DropdownProps>,
  'display' | 'alignment' | 'direction' | 'items' | 'variant' | 'maxHeight'
>;

const DropdownList = forwardRef<HTMLDivElement, DropdownListProps>(function (
  {
    isOpen,
    onSelect,
    value,
    display,
    alignment,
    direction,
    items,
    variant,
    maxHeight,
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
        'grid fixed overflow-hidden transition-[grid-template-rows] rounded',
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
});

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
  const {
    field: { value, onChange },
  } = useController({ name, control, rules });

  useEffect(() => {
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

  return (
    <>
      <div
        className={classNames(
          'min-h-[42px] text-background-10 text-left dropdown',
          displayStyles[display]
        )}
        onClick={() => setIsOpen(!isOpen)}
        onKeyDown={(e) => a11yClick(e) && setIsOpen(!isOpen)}
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
          />,
          document.body
        )}
      </div>
    </>
  );
}
