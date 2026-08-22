import classNames from 'classnames';
import {
  CSSProperties,
  forwardRef,
  ReactNode,
  useEffect,
  useId,
  useLayoutEffect,
  useRef,
  useState,
} from 'react';
import {
  Control,
  FieldError,
  FieldPath,
  FieldValues,
  useController,
  UseControllerProps,
} from 'react-hook-form';
import { ArrowDownIcon, ArrowUpIcon } from './icon/ArrowIcons';
import { ProgressBar } from './ProgressBar';
import { a11yClick } from '@/utils/a11y';
import './Dropdown.scss';

export type DropdownItem = {
  value: string;
  label: ReactNode;
};

export type DropdownDirection = 'up' | 'down';

type DropdownProps = {
  direction?: DropdownDirection;
  variant?: 'primary' | 'secondary' | 'tertiary' | 'quaternary';
  alignment?: 'left' | 'right';
  display?: 'fit' | 'block';
  placeholder: ReactNode;
  name: string;
  items: DropdownItem[];
  maxHeight?: string | number;
  error?: FieldError;
  loading?: boolean;
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
  value: string;
  innerFocusValue: string | null;
  name: string;
}) {
  const variantStyles = {
    primary:
      'text-background-20 checked-hover:text-background-10 checked-hover:bg-background-50 focus:text-background-10 focus:bg-background-50',
    secondary:
      'text-background-20 checked-hover:text-background-10 checked-hover:bg-background-40 focus:text-background-10 focus:bg-background-40',
    tertiary:
      'bg-accent-background-30 checked-hover:bg-accent-background-20 focus:bg-accent-background-20 text-background-10',
    quaternary:
      'text-background-20 checked-hover:text-background-10 checked-hover:bg-background-60 focus:text-background-10 focus:bg-background-60',
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
  value: string;
  innerFocusValue: string | null;
  anchorName: string;
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
    anchorName,
  },
  ref
) {
  const variantStyles = {
    primary: 'bg-background-60',
    secondary: 'bg-background-50',
    tertiary: 'bg-accent-background-30',
    quaternary: 'bg-background-70',
  };

  const columnArea =
    display === 'block'
      ? 'center'
      : alignment === 'left'
        ? 'span-right'
        : 'span-left';
  const rowArea = direction === 'up' ? 'top' : 'bottom';

  const ulRef = useRef<HTMLUListElement>(null);

  const setDivRef = (node: HTMLDivElement | null) => {
    node?.setAttribute('popover', 'manual');
    if (typeof ref === 'function') {
      ref(node);
    } else if (ref) {
      ref.current = node;
    }
  };

  return (
    <div
      className={classNames(
        'dropdown-popover fixed inset-auto m-0 border-0 p-0 rounded overflow-hidden',
        direction === 'up' ? 'mb-3' : 'mt-3',
        variantStyles[variant],
        display === 'block' ? 'w-full' : 'w-fit'
      )}
      style={
        {
          positionAnchor: anchorName,
          positionArea: `${rowArea} ${columnArea}`,
          positionTryFallbacks: 'flip-block',
        } as CSSProperties
      }
      onTransitionEnd={(e) => {
        if (e.propertyName === 'opacity' && !isOpen) {
          ulRef.current?.scrollTo({ top: 0 });
        }
      }}
      ref={setDivRef}
      id={`__dropdownList-${name}`}
    >
      <ul
        ref={ulRef}
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
  loading,
}: DropdownProps & { value: string; onChange: (value: string) => void }) {
  const [isOpen, setIsOpen] = useState(false);

  const variantStyles = {
    primary: 'bg-background-60 hover:bg-background-50',
    secondary: 'bg-background-50 hover:bg-background-40',
    tertiary: 'bg-accent-background-30 hover:bg-accent-background-20',
    quaternary: 'bg-background-70 hover:bg-background-60',
  };

  const displayStyles = {
    fit: 'w-fit',
    block: 'w-full',
  };

  const getShownValue = (value: string) =>
    value
      ? (items.find((item) => item.value === value)?.label ?? placeholder)
      : placeholder;

  const ref = useRef<HTMLDivElement>(null);
  const listRef = useRef<HTMLDivElement>(null);

  const anchorName = `--dropdown-anchor-${useId().replace(/:/g, '')}`;

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

  useEffect(() => {
    if (!isOpen) {
      setInnerFocusIndex(null);
    }
  }, [isOpen]);

  useEffect(() => {
    if (loading) {
      setIsOpen(false);
    }
  }, [loading]);

  useEffect(() => {
    const el = listRef.current;
    if (!el) {
      return;
    }
    if (isOpen && !el.matches(':popover-open')) {
      el.showPopover();
    } else if (!isOpen && el.matches(':popover-open')) {
      el.hidePopover();
    }
  }, [isOpen]);

  return (
    <>
      <div
        className={classNames(
          'min-h-[42px] min-w-0 text-background-10 text-left dropdown',
          displayStyles[display]
        )}
        onClick={() => !loading && setIsOpen(!isOpen)}
        onKeyDown={(e) => {
          if (loading) {
            return;
          }
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
          if (e.currentTarget.contains(e.relatedTarget)) {
            return;
          }

          setIsOpen(false);
        }}
      >
        <div
          className={classNames(
            'flex flex-row justify-between items-center gap-2 pl-3 pr-5 py-3 rounded-md focus:ring-4 relative min-w-0 overflow-hidden',
            loading ? 'cursor-not-allowed opacity-60' : 'cursor-pointer',
            variantStyles[variant]
          )}
          tabIndex={loading ? -1 : 0}
          aria-disabled={loading}
          ref={ref}
          style={{ anchorName } as CSSProperties}
          aria-controls={`__dropdownList-${name}`}
          aria-activedescendant={
            innerFocusIndex === null
              ? ''
              : `__dropdownList-${name}-item-${items[innerFocusIndex].value}`
          }
          role="combobox"
        >
          <span className="min-w-0 truncate">{getShownValue(value)}</span>
          <div className="fill-background-10 shrink-0">
            {direction === 'up' ? (
              <ArrowUpIcon size={16} />
            ) : (
              <ArrowDownIcon size={16} />
            )}
          </div>
          {loading && (
            <div className="absolute left-0 right-0 bottom-0">
              <ProgressBar indeterminate height={3} bottom />
            </div>
          )}
        </div>
        {error?.message && (
          <div className="text-status-critical">{error.message}</div>
        )}
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
          anchorName={anchorName}
          innerFocusValue={
            innerFocusIndex === null ? null : items[innerFocusIndex].value
          }
          name={name}
        />
      </div>
    </>
  );
}

export function Dropdown<T extends FieldValues = FieldValues>({
  control,
  name,
  rules,
  ...props
}: DropdownProps & {
  control: Control<T>;
  name: FieldPath<T>;
  rules?: UseControllerProps<T, FieldPath<T>>['rules'];
}) {
  const {
    field: { value, onChange },
  } = useController({ name, control, rules });

  return (
    <DropdownInside value={value} name={name} {...props} onChange={onChange} />
  );
}
