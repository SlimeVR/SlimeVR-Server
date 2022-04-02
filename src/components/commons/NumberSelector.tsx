import classNames from "classnames";
import { useMemo } from "react";
import { Control, Controller } from "react-hook-form";
import { Button } from "./Button";



export function NumberSelector({ label, valueLabelFormat, control, name, min, max, step, variant }: { label: string, valueLabelFormat?: (value: number) => string, control: Control<any>, name: string, min: number, max: number, step: number, variant: 'smol' | 'big' }) {
    
    const variantClass = useMemo(() => {
        const variantsMap = {
            smol: {
                container: 'flex flex-col gap-1',
                label: 'flex text-sm text-white',
                value: 'flex justify-center items-center w-10 text-white text-lg font-bold'
            },
            big: {
                container: 'flex flex-row gap-5 ',
                label: 'flex flex-grow text-lg font-bold text-white justify-start items-center',
                value: 'flex justify-center items-center w-16 text-white text-lg font-bold'
            }
        };
        return variantsMap[variant];
    }, [variant])
    
    return (
        <Controller
            control={control}
            name={name}
            render={({ field: { onChange, value } }) => (
                <div className={classNames(variantClass.container)}>
                    <div className={classNames(variantClass.label)}>{label}</div>
                    <div className="flex gap-3">
                        <div className="flex">
                            <Button variant="primary" onClick={() => onChange(value - step)} disabled={value <= min}>-</Button>
                        </div>
                        <div className={classNames(variantClass.value)}>{valueLabelFormat ? valueLabelFormat(value) : value}</div>
                        <div className="flex">
                            <Button variant="primary" onClick={() => onChange(value + step)} disabled={value >= max}>+</Button>
                        </div>
                    </div>
                </div>
            )}
        />
    )
}