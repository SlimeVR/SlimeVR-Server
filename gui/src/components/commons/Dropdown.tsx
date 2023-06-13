import classNames from 'classnames';
import { useEffect, useState } from 'react';
import { Control, Controller } from 'react-hook-form';
import { a11yClick } from '../utils/a11y';

export interface DropdownItem {
  label: string;
  value: string;
}

export type DropdownDirection = 'up' | 'down';

export function Dropdown({
  direction = 'up',
  variant = 'primary',
  alignment = 'right',
  display = 'fit',
  placeholder,
  control,
  name,
  items = [],
}: {
  direction?: DropdownDirection;
  variant?: 'primary' | 'secondary' | 'tertiary';
  alignment?: 'right' | 'left';
  display?: 'fit' | 'block';
  placeholder: string;
  control: Control<any>;
  name: string;
  items: DropdownItem[];
}) {
  const [isOpen, setOpen] = useState(false);
  useEffect(() => {
    if (!isOpen) return;

    function onWheelEvent() {
      if (isOpen && !document.querySelector('div.dropdown-scroll:hover')) {
        setOpen(false);
      }
    }

    function onTouchEvent(event: TouchEvent) {
      // Check if we touch scroll outside of the dropdown
      if (
        isOpen &&
        !document
          .querySelector('div.dropdown-scroll')
          ?.contains(event.target as HTMLDivElement)
      ) {
        setOpen(false);
      }
    }

    function onClick(event: MouseEvent) {
      const isInDropdownScroll = document
        .querySelector('div.dropdown-scroll')
        ?.contains(event.target as HTMLDivElement);
      const isInDropdown = document
        .querySelector('div.dropdown')
        ?.contains(event.target as HTMLDivElement);
      if (isOpen && !isInDropdownScroll && !isInDropdown) {
        setOpen(false);
      }
    }

    document.addEventListener('click', onClick, false);
    document.addEventListener('touchmove', onTouchEvent, false);
    // TS doesn't let me specify { passive: true }, but I believe it will work anyways
    document.addEventListener('wheel', onWheelEvent, { passive: true });
    return () => {
      document.removeEventListener('wheel', onWheelEvent);
      document.removeEventListener('click', onClick);
      document.removeEventListener('touchmove', onTouchEvent);
    };
  }, [isOpen]);

  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, value } }) => (
        <>
          {isOpen && (
            <div
              className="absolute top-0 left-0 w-full h-full bg-transparent"
              onClick={() => setOpen(false)}
            ></div>
          )}
          <div
            className={classNames(
              'relative',
              display === 'fit' && 'w-fit',
              display === 'block' && 'w-full'
            )}
          >
            <div
              className={classNames(
                'min-h-[35px] text-background-10 px-5 py-2.5 rounded-md focus:ring-4 text-center dropdown',
                'flex cursor-pointer',
                variant == 'primary' &&
                  'bg-background-60 hover:bg-background-50',
                variant == 'secondary' &&
                  'bg-background-70 hover:bg-background-60',
                variant == 'tertiary' &&
                  'bg-accent-background-30 hover:bg-accent-background-20'
              )}
              onClick={() => setOpen((open) => !open)}
              onKeyDown={(ev) => a11yClick(ev) && setOpen((open) => !open)}
              tabIndex={0}
            >
              <div className="flex-grow">
                {items.find((i) => i.value == value)?.label || placeholder}
              </div>
              <div
                className={classNames(
                  'ml-2',
                  direction == 'up' && 'rotate-180',
                  direction == 'down' && 'rotate-0'
                )}
              >
                <svg
                  className="justify-end w-4 h-4 "
                  aria-hidden="true"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M19 9l-7 7-7-7"
                  ></path>
                </svg>
              </div>
            </div>
            {isOpen && (
              <div
                className={classNames(
                  'absolute z-10 rounded shadow min-w-max max-h-[50vh]',
                  'overflow-y-auto dropdown-scroll',
                  display === 'fit' && 'w-fit',
                  display === 'block' && 'w-full',
                  direction === 'up' && 'bottom-[45px]',
                  direction === 'down' && 'top-[45px]',
                  variant == 'primary' && 'bg-background-60',
                  variant == 'secondary' && 'bg-background-70',
                  variant == 'tertiary' && 'bg-accent-background-30',
                  alignment === 'right' && 'right-0',
                  alignment === 'left' && 'left-0'
                )}
              >
                <ul className="py-1 text-sm flex flex-col ">
                  {items.map((item) => (
                    <li
                      className={classNames(
                        'py-2 px-4 min-w-max cursor-pointer pr-2',
                        variant == 'primary' &&
                          'hover:bg-background-50 text-background-20 hover:text-background-10',
                        variant == 'secondary' &&
                          'hover:bg-background-60 text-background-20 hover:text-background-10',
                        variant == 'tertiary' &&
                          value !== item.value &&
                          'bg-accent-background-30 hover:bg-accent-background-20',
                        variant == 'tertiary' &&
                          value === item.value &&
                          'bg-accent-background-20'
                      )}
                      onClick={() => {
                        onChange(item.value);
                        setOpen(false);
                      }}
                      onKeyDown={(ev) => {
                        if (!a11yClick(ev)) return;
                        onChange(item.value);
                        setOpen(false);
                      }}
                      key={item.value}
                      tabIndex={0}
                    >
                      {item.label}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        </>
      )}
    />
  );
}
