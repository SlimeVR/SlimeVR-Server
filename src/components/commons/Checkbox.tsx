import classNames from 'classnames';
import { forwardRef, useId } from 'react'

export const CheckBox = forwardRef<HTMLInputElement, { label: string, outlined?: boolean }>(({ label, outlined, ...props }, ref) => {
    const id = useId();

    return (
        <div className={classNames('flex items-center gap-3', { 'rounded-md bg-purple-gray-700 pl-4 text-emphasised': outlined })}>
            <input  ref={ref} id={id} {...props} className="flex flex-col rounded-sm text-accent-lighter focus:ring-purple-gray-700" type="checkbox" />
            <label htmlFor={id} className="w-full py-3">{label}</label>
        </div>
    )
});