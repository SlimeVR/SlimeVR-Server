import classNames from "classnames";
import { useEffect, useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { useWebsocketAPI } from "../../hooks/websocket-api";
import { Button } from "../commons/Button";
import { NumberSelector } from "../commons/NumberSelector";
import { AutomaticCalibration } from "./AutomaticCalibration";
import { BodyView } from "./BodyView";

import { ChangeSkeletonConfigRequestT, RpcMessage, SkeletonBone, SkeletonConfigRequestT, SkeletonConfigResponseT, SkeletonResetAllRequestT } from 'slimevr-protocol';

const bodyPartLabels = {
    [SkeletonBone.NONE]: "None",
    [SkeletonBone.HEAD]: "Head shift",
    [SkeletonBone.NECK]: "Neck length",
    [SkeletonBone.TORSO]: "Torso length",
    [SkeletonBone.CHEST]: "Chest distance",
    [SkeletonBone.WAIST]: "Waist distance",
    [SkeletonBone.HIP_OFFSET]: "Hip offset",
    [SkeletonBone.HIPS_WIDTH]: "Hips width",
    [SkeletonBone.LEGS_LENGTH]: "Legs length",
    [SkeletonBone.KNEE_HEIGHT]: "Knee height",
    [SkeletonBone.FOOT_LENGTH]: "Foot length",
    [SkeletonBone.FOOT_OFFSET]: "Foot offset",
    [SkeletonBone.SKELETON_OFFSET]: "Skeleton offset",
    [SkeletonBone.CONTROLLER_DISTANCE_Z]: "Controller distance z",
    [SkeletonBone.CONTROLLER_DISTANCE_Y]: "Controller distance y",
    [SkeletonBone.ELBOW_DISTANCE]: "Elbow distance",
    [SkeletonBone.UPPER_ARM_DISTANCE]: "Upper arm distance"
}

type BodyProportionsForm = ({ [key: string]: number });


export function BodyProportions() {
    const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
    const [selectedBodyPart, setSelectedBodyPart] = useState<number | null>(null);
    const { control, reset, watch, handleSubmit } = useForm<BodyProportionsForm>({ defaultValues: Object.values(SkeletonBone).filter((bone) => !isNaN(+bone)).reduce((curr, bone) => ({ ...curr, [bone]: 0 }), {}) });
    const [config, setConfig] = useState<SkeletonConfigResponseT | null>(null); 
    const [step, setStep] = useState(1);

    const bodyParts = useMemo(() => {
        return config?.skeletonParts.map(({ bone, value }) => ({ bone, label: bodyPartLabels[bone], value })) || []
    }, [config])


    const toFormData = (data: SkeletonConfigResponseT): BodyProportionsForm => data.skeletonParts.reduce((curr, part) => ({ ...curr, [part.bone]: part.value }), {})

    useRPCPacket(RpcMessage.SkeletonConfigResponse, (data: SkeletonConfigResponseT) => {
        setConfig(data);
        reset(toFormData(data));
    })

    useEffect(() => {
        sendRPCPacket(RpcMessage.SkeletonConfigRequest, new SkeletonConfigRequestT())
    }, [])


    const onSubmit = async (data: BodyProportionsForm) => {
        if (!config) return ;

        const curr = toFormData(config);
        for (const key in curr) {
            if (curr[key] !== data[key]) {
                const configChange = new ChangeSkeletonConfigRequestT();

                console.log(key);
                configChange.bone = +key;
                configChange.value = data[key];

                await sendRPCPacket(RpcMessage.ChangeSkeletonConfigRequest, configChange);
            }
        }
    }

    useEffect(() => {
        const subscription = watch(() => handleSubmit(onSubmit)());
        return () => subscription.unsubscribe();
    })


    const handleResetAll = () => {
        sendRPCPacket(RpcMessage.SkeletonResetAllRequest, new SkeletonResetAllRequestT());
    }

    
    const onMouseEnterSelector = (id: number) => {
        setSelectedBodyPart(id);
    }

    const roundedStep = (value: number, add: boolean) => {
        if (!add) {
            return (Math.round(value * 200) - step * 2) / 200;
        } else {
            return (Math.round(value * 200) + step * 2) / 200;
        }
    }


    return (
        <div className="flex flex-col overflow-y-auto">
            <div className="flex  text-2xl px-8 pt-8 text-white font-bold">
                Body Proportions
            </div>
            <div className="flex p-5 gap-8 h-full justify-center">
                <div className="bg-primary-1 rounded-lg w-72 justify-center p-10 hidden sm:flex">
                    <BodyView selectedBodyPart={selectedBodyPart || 0}></BodyView>
                </div>
                <div className="flex-col flex gap-4">
                    <div className="flex gap-3">
                        <AutomaticCalibration></AutomaticCalibration>
                        <Button variant="primary" onClick={handleResetAll}>Reset all</Button>
                    </div>
                    <div className="flex flex-col rounded-lg py-2 text-white">
                        <div className="font-semibold text-xl">Precision</div>
                        <div className="bg-primary-5 flex text-white  rounded-lg overflow-hidden flex-grow h-10 cursor-pointer">
                            <div onClick={() => setStep(0.5)} className={classNames("hover:bg-primary-1 flex flex-grow justify-center items-center", { 'bg-primary-3': step === 0.5 })}>0.5</div>
                            <div onClick={() => setStep(1)} className={classNames("hover:bg-primary-1 flex flex-grow justify-center items-center", { 'bg-primary-3': step === 1 })}>1</div>
                            <div onClick={() => setStep(5)} className={classNames("hover:bg-primary-1 flex flex-grow justify-center items-center", { 'bg-primary-3': step === 5 })}>5</div>
                        </div>
                    </div>
                    <div className="flex flex-col">
                        {bodyParts.map(({label, bone}) => 
                            <div key={bone} onMouseEnter={() => onMouseEnterSelector(bone)} className={classNames('px-3 rounded-lg py-2', { 'bg-primary-1 ': bone === selectedBodyPart })}>
                                <NumberSelector
                                    variant="big" 
                                    control={control} 
                                    label={label} 
                                    min={-1} 
                                    max={1.5}
                                    step={roundedStep}
                                    name={`${bone}`} 
                                    valueLabelFormat={(value) => `${Number(value * 100).toFixed(1).replace(/[.,]0$/, "")}cm` }
                                ></NumberSelector>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}