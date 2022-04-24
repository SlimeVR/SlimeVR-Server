import React from "react";

export type SelectOption = { label: string, value: any };

export const Select = React.forwardRef<HTMLSelectElement, { options: SelectOption[], label?: string }>(({  label, options, ...props }, ref) => {
    return (
        <div className="flex flex-col gap-1 text-white">
            {label && <span className="text-sm ">{label}</span>}
            <select {...props} ref={ref} className="w-full mt-0 rounded-md bg-primary-5 border-primary-1 shadow-sm focus:border-gray-300 focus:ring focus:ring-gray-200 focus:ring-opacity-50">
                {options && options.map(({ label, value }) => <option value={value} key={value}>{label}</option>)}
            </select>
        </div>
    )
});