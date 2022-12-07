import classNames from 'classnames';
import {
  forwardRef,
  HTMLInputTypeAttribute,
  MouseEvent,
  useMemo,
  useState
} from 'react';
import { EyeIcon } from './icon/EyeIcon';

export interface InputProps {
  type: HTMLInputTypeAttribute;
  placeholder?: string | null;
  label?: string | null;
  autocomplete?: boolean;
  variant?: 'primary' | 'secondary';
}

export const Input = forwardRef<HTMLInputElement, InputProps>(function AppInput(
  { type, placeholder, label, autocomplete, variant = 'primary', ...props },
  ref
) {
  const [forceText, setForceText] = useState(false);

  const togglePassword = (e: MouseEvent<HTMLDivElement>) => {
    e.preventDefault();
    setForceText(!forceText);
  };

  const classes = useMemo(() => {
    const variantsMap = {
      primary: classNames('bg-background-60 border-background-60'),
      secondary: classNames('bg-background-50 border-background-50'),
    };

    return classNames(
      variantsMap[variant],
      'w-full focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent rounded-md bg-background-60 border-background-60 focus:border-accent-background-40 placeholder:text-background-30 text-standard relative'
    );
  }, [variant]);

  return (
    <label className="flex flex-col gap-1">
      {label}
      <div className="relative w-full">
        <input
          type={forceText ? 'text' : type}
          ref={ref}
          className={classNames(classes, { 'pr-10': type === 'password' })}
          placeholder={placeholder || undefined}
          autoComplete={autocomplete ? 'off' : 'on'}
          {...props}
        ></input>
        {type === 'password' && (
          <div
            className="fill-background-10 absolute top-0 h-full flex flex-col justify-center right-0 p-4"
            onClick={togglePassword}
          >
            <EyeIcon></EyeIcon>
          </div>
        )}
      </div>
    </label>
  );
});
