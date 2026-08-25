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
import { CheckIcon } from './icon/CheckIcon';
import { ProgressBar } from './ProgressBar';
import { a11yClick } from '@/utils/a11y';
import './Dropdown.scss';
import { Typography } from './Typography';

export type DropdownItem = {
  value: string;
  label: ReactNode;
};

export type DropdownDirection = 'up' | 'down';

function isItemSelected(
  selection: DropdownSingleSelection | DropdownMultipleSelection,
  itemValue: string
): boolean {
  return selection.multiple
    ? selection.value.includes(itemValue)
    : selection.value === itemValue;
}

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
  renderValue?: () => ReactNode;
};

type DropdownSingleSelection = {
  multiple?: false;
  value: string;
  onChange: (value: string) => void;
};

type DropdownMultipleSelection = {
  multiple: true;
  value: string[];
  onChange: (value: string[]) => void;
};

function DropdownItem({
  item,
  variant,
  onSelected,
  isOpen,
  checked,
  multiple,
  innerFocusValue,
  name,
}: {
  item: DropdownItem;
  variant: Required<DropdownProps>['variant'];
  onSelected: () => void;
  isOpen: boolean;
  checked: boolean;
  multiple?: boolean;
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

    if (innerFocusValue === item.value || (!innerFocusValue && checked)) {
      ref.current?.scrollIntoView({ block: 'nearest' });
    }
  }, [isOpen]);

  return (
    <div
      className={classNames(
        'py-2 px-2 min-w-max cursor-pointer text-standard-bold transition-colors select-none',
        variantStyles[variant],
        innerFocusValue === item.value && 'ring-inset ring-4'
      )}
      onClick={(e) => {
        e.stopPropagation();
        onSelected();
      }}
      onKeyDown={(e) => a11yClick(e) && onSelected()}
      tabIndex={-1}
      aria-hidden={!isOpen}
      ref={ref}
      id={`__dropdownList-${name}-item-${item.value}`}
    >
      <div className="flex items-center gap-2.5">
        {multiple && (
          <div
            className={classNames(
              'w-4 h-4 rounded flex items-center justify-center transition-colors shrink-0',
              checked
                ? 'bg-accent-background-30 text-background-10'
                : 'bg-background-50 border border-background-40'
            )}
          >
            {checked && <CheckIcon size={9} className="fill-current" />}
          </div>
        )}
        <div className="flex-1 min-w-0">{item.label}</div>
      </div>
    </div>
  );
}

type DropdownListProps = {
  isOpen: boolean;
  onSelect: (item: DropdownItem) => void;
  isSelected: (value: string) => boolean;
  multiple?: boolean;
  onSelectAll?: () => void;
  onDeselectAll?: () => void;
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
    isSelected,
    multiple,
    onSelectAll,
    onDeselectAll,
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
        'dropdown-popover fixed inset-auto m-0 border-0 p-0 rounded overflow-hidden flex flex-col',
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
      {multiple && (onSelectAll || onDeselectAll) && (
        <div className="flex items-center justify-between px-3 py-2 border-b border-background-10/10 text-xs font-bold bg-background-80/60 select-none">
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              onSelectAll?.();
            }}
            className="text-background-10 hover:text-background-20 transition-colors"
          >
            <Typography id="dropdown_select-all" />
          </button>
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              onDeselectAll?.();
            }}
            className="text-background-30 hover:text-background-10 transition-colors"
          >
            <Typography id="dropdown_unselect-all" />
          </button>
        </div>
      )}
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
            checked={isSelected(item.value)}
            multiple={multiple}
            innerFocusValue={innerFocusValue}
            name={name}
          />
        ))}
      </ul>
    </div>
  );
});

export function DropdownInside(
  props: DropdownProps & (DropdownSingleSelection | DropdownMultipleSelection)
) {
  const {
    direction = 'up',
    variant = 'primary',
    alignment = 'right',
    display = 'fit',
    placeholder,
    name,
    items,
    maxHeight = '50vh',
    error,
    loading,
    renderValue,
  } = props;

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

  const getShownValue = (): ReactNode => {
    if (renderValue) return renderValue();
    if (props.multiple) {
      return props.value.length > 0
        ? `${props.value.length} selected`
        : placeholder;
    }
    return props.value
      ? (items.find((item) => item.value === props.value)?.label ?? placeholder)
      : placeholder;
  };

  const selectItem = (item: DropdownItem) => {
    if (props.multiple) {
      const exists = props.value.includes(item.value);
      props.onChange(
        exists
          ? props.value.filter((v) => v !== item.value)
          : [...props.value, item.value]
      );
    } else {
      props.onChange(item.value);
    }
  };

  const ref = useRef<HTMLDivElement>(null);
  const listRef = useRef<HTMLDivElement>(null);

  const anchorName = `--dropdown-anchor-${useId().replace(/:/g, '')}`;

  const [innerFocusIndex, setInnerFocusIndex] = useState<number | null>(null);
  const getCurrentActiveIndex = () => {
    return props.multiple
      ? items.findIndex((item) => props.value.includes(item.value))
      : items.findIndex((item) => item.value === props.value);
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
        onKeyDown={(e) => {
          if (loading) {
            return;
          }
          if (!isOpen) {
            if (a11yClick(e)) {
              setInnerFocusIndex(getCurrentActiveIndex());
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

              selectItem(items[innerFocusIndex]);
              if (!props.multiple) setIsOpen(false);
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
          onClick={(e) => {
            e.stopPropagation();
            if (!loading) setIsOpen((o) => !o);
          }}
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
          <span className="min-w-0 truncate">{getShownValue()}</span>
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
          multiple={props.multiple}
          onSelectAll={() =>
            props.multiple && props.onChange(items.map((i) => i.value))
          }
          onDeselectAll={() => props.multiple && props.onChange([])}
          onSelect={(item: DropdownItem) => {
            ref.current?.focus();
            selectItem(item);
          }}
          variant={variant}
          maxHeight={maxHeight}
          isSelected={(value) => isItemSelected(props, value)}
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
