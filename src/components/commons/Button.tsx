import classNames from "classnames";
import { ReactChild, useMemo } from "react";



export function Button({ children, variant, disabled, ...props }: { children: ReactChild, variant: 'primary' | 'secondary' } & React.ButtonHTMLAttributes<HTMLButtonElement>) {

    const classes = useMemo(() => {
        const variantsMap  = {
            primary: 'text-white bg-primary-5 hover:bg-primary-1 focus:ring-4 focus:outline-none focus:ring-primary-2',
            secondary: 'text-white hover:bg-primary-1 focus:ring-primary-2'
        }

        const variantsMapDisabled  = {
            primary: 'bg-gray-800 hover:bg-gray-800',
            secondary: 'bg-gray-800 hover:bg-gray-800'
        }

        return classNames(variantsMap[variant], 'focus:ring-4 rounded-lg text-sm px-5 py-2.5 text-center font-medium', disabled ? variantsMapDisabled[variant] : false);

    }, [variant, disabled])
    return <button type="button" {...props} className={classes} disabled={disabled}>{children}</button>
}