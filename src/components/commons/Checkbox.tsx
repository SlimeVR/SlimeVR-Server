import { forwardRef, useId } from 'react'

export const CheckBox = forwardRef<HTMLInputElement, { label: string }>(({ label, ...props }, ref) => {
    const id = useId();

    return (
        <div className="flex items-center gap-3 text-white">
            <input ref={ref} id={id} {...props} className="flex flex-col rounded-sm " type="checkbox" />
            <label htmlFor={id}>{label}</label>
        </div>
    )
});