import { forwardRef, HTMLInputTypeAttribute } from "react";

export interface InputProps {
    type: HTMLInputTypeAttribute,
    placeholder?: string
}

export const Input = forwardRef<HTMLInputElement, InputProps>(({type, placeholder, ...props}, ref) => {
    return <input 
        type={type} 
        ref={ref} 
        className="text-white bg-primary-5 rounded-lg border-primary-4 focus:border-primary-1" 
        placeholder={placeholder}
        {...props} 
    />;
});