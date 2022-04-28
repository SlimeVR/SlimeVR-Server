import { forwardRef, HTMLInputTypeAttribute } from "react";

export interface InputProps {
    type: HTMLInputTypeAttribute,
    placeholder?: string
}

export const Input = forwardRef<HTMLInputElement, InputProps>(({type, placeholder, ...props}, ref) => {
    return <input 
        type={type} 
        ref={ref} 
        className="rounded-lg bg-purple-gray-500 border-purple-gray-500 focus:border-purple-gray-500 placeholder:text-purple-gray-300 text-field-title" 
        placeholder={placeholder}
        {...props} 
    />;
});