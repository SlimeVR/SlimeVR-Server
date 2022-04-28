import classNames from "classnames";
import React, { ReactChild } from "react";


export function IconButton({ icon, className, ...props }: { icon: ReactChild, className?: string } & React.HTMLAttributes<HTMLDivElement>) {
    return (
        <div {...props} className={classNames("px-2 rounded-full h-8 w-8 flex justify-center items-center hover:bg-purple-gray-600", className)}>
            {icon}
        </div>
    )
}