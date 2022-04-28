import classNames from "classnames";
import { ReactChild, useMemo } from "react";



export function Button({ children, variant, disabled, ...props }: { children: ReactChild, variant: 'primary' } & React.ButtonHTMLAttributes<HTMLButtonElement>) {

    const classes = useMemo(() => {
        const variantsMap  = {
            primary: classNames('text-field-title focus:ring-4 focus:outline-none focus:ring-primary-2', { 'bg-purple-gray-600 hover:bg-purple-gray-500': !disabled, 'bg-purple-gray-900 hover:bg-purple-gray-900 text-section-indicator': disabled }),
        }
        return classNames(variantsMap[variant], 'focus:ring-4 rounded-lg px-5 py-2.5 text-center font-medium');

    }, [variant, disabled])
    return <button type="button" {...props} className={classes} disabled={disabled}>{children}</button>
}