import classNames from "classnames";
import { useMemo } from "react";
import { Control, Controller } from "react-hook-form";
import { Button } from "./Button";



export function NumberSelector({ label, valueLabelFormat, control, name, min, max, step, variant }: { label: string, valueLabelFormat?: (value: number) => string, control: Control<any>, name: string, min: number, max: number, step: number | ((value: number, add: boolean) => number), variant: 'smol' | 'big' }) {
    
    const variantClass = useMemo(() => {
        const variantsMap = {
            smol: {
                container: 'flex flex-col gap-1',
                label: 'flex text-field-title',
                value: 'flex justify-center items-center w-10 text-field-title'
            },
            big: {
                container: 'flex flex-row gap-5',
                label: 'flex flex-grow justify-start items-center text-field-title',
                value: 'flex justify-center items-center w-16 text-field-title'
            }
        };
        return variantsMap[variant];
    }, [variant])


    const stepFn = typeof step === 'function' ? step : (value: number, add: boolean) => add ? value + step : value - step;

    
    return (
        <Controller
            control={control}
            name={name}
            render={({ field: { onChange, value } }) => (
                <div className={classNames(variantClass.container)}>
                    <div className={classNames(variantClass.label)}>{label}</div>
                    <div className="flex gap-3">
                        <div className="flex">
                            <Button variant="primary" onClick={() => onChange(stepFn(value, false))} disabled={stepFn(value, false) <= min}>-</Button>
                        </div>
                        <div className={classNames(variantClass.value)}>{valueLabelFormat ? valueLabelFormat(value) : value}</div>
                        <div className="flex">
                            <Button variant="primary" onClick={() => onChange(stepFn(value, true))} disabled={stepFn(value, true) >= max}>+</Button>
                        </div>
                    </div>
                </div>
            )}
        />
    )
}