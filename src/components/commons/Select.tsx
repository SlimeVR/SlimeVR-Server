import React from "react";

export type SelectOption = { label: string, value: any };

export const Select = React.forwardRef<HTMLSelectElement, { options: SelectOption[], label?: string }>(({  label, options, ...props }, ref) => {
    return (
        <div className="flex flex-col gap-1 ">
            {label && <span className="text-field-title">{label}</span>}
            <select {...props} ref={ref} className="w-full mt-0 rounded-md border-purple-gray-600 text-field-title bg-purple-gray-600 shadow-sm focus:border-purple-gray-600 focus:ring focus:ring-purple-gray-600 focus:ring-opacity-50">
                {options && options.map(({ label, value }) => <option value={value} key={value}>{label}</option>)}
            </select>
        </div>
    )
});