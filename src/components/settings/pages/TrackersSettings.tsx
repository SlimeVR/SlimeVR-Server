import { useEffect, useRef } from "react";
import { useForm } from "react-hook-form";
import { useLocation } from "react-router-dom";
import { ChangeSettingsRequestT, FilteringSettingsT, RpcMessage, SettingsRequestT, SettingsResponseT, SteamVRTrackersSettingT } from "solarxr-protocol";
import { useWebsocketAPI } from "../../../hooks/websocket-api";
import { CheckBox } from "../../commons/Checkbox";
import { NumberSelector } from "../../commons/NumberSelector";
import { Select } from "../../commons/Select";

interface SettingsForm {
    trackers: {
        waist: boolean,
        chest: boolean,
        legs: boolean,
        knees: boolean,
        elbows: boolean,
    }
    filtering: {
        type: number;
        intensity: number;
        ticks: number;
    }
}


export function TrackersSettings() {

    const { state } = useLocation();
    const pageRef = useRef<HTMLFormElement | null>(null);


    const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
    const { register, reset, control, watch, handleSubmit } = useForm<SettingsForm>({ defaultValues: { filtering: { intensity: 0, ticks: 0 } } });

    const onSubmit = (values: SettingsForm) => {
        const settings = new ChangeSettingsRequestT();

        if (values.trackers) {
            const trackers = new SteamVRTrackersSettingT();
            trackers.waist = values.trackers.waist;
            trackers.chest = values.trackers.chest;
            trackers.legs = values.trackers.legs;
            trackers.knees = values.trackers.knees;
            trackers.elbows = values.trackers.elbows;
            settings.steamVrTrackers = trackers;
        }

        const filtering = new FilteringSettingsT();
        filtering.type = values.filtering.type;
        filtering.intensity = values.filtering.intensity;
        filtering.ticks = values.filtering.ticks;

        settings.filtering = filtering;
        sendRPCPacket(RpcMessage.ChangeSettingsRequest, settings)
    }


    useEffect(() => {
        const subscription = watch(() => handleSubmit(onSubmit)());
        return () => subscription.unsubscribe();
    }, [])

    
    useEffect(() => {
        sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
    }, [])

    useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
        reset({
            ...(settings.steamVrTrackers ? {trackers: settings.steamVrTrackers} : {}),
            ...(settings.filtering ? {filtering: settings.filtering} : {})
        })
    })


    useEffect(() => {

        const typedState: { scrollTo: string } = state as any;

        if (!pageRef.current || (!typedState || !typedState.scrollTo))
            return ;
            
        const elem = pageRef.current.querySelector(`#${typedState.scrollTo}`)
        if (elem)
            elem.scrollIntoView({ behavior: 'smooth' })
    }, [state])


    return (
        <form className="px-8 flex flex-col gap-20 py-4" ref={pageRef}>
            <div className="flex flex-col gap-2" id="steamvr">
                <div className="flex gap-5 text-secondary-heading">
                    SteamVR Trackers
                </div>
                <div className="flex  flex-col  text-default">
                    <p>Enable or disable specific tracking  parts.</p>
                    <p>Useful if you want more control over what SlimeVR does.</p>
                </div>
                <div className="grid grid-cols-2  gap-5 pt-5">
                    <CheckBox outlined {...register('trackers.waist')} label="Waist" />
                    <CheckBox outlined {...register('trackers.chest')} label="Chest"/>
                    <CheckBox outlined {...register('trackers.legs')} label="Legs"/>
                    <CheckBox outlined {...register('trackers.knees')} label="Knees"/>
                    <CheckBox outlined {...register('trackers.elbows')} label="Elbows"/>
                </div>
            </div>
            <div className="flex flex-col gap-2" id="filtering">
                <div className="flex gap-5 text-secondary-heading">
                    Filtering
                </div>
                <div className="flex flex-col text-default">
                    <p>Choose the filtering type for your trackers.</p>
                    <p>Extrapolation predicts movement while interpolation smoothens movement.</p>
                </div>
                <div className="flex  gap-5 pt-5">
                    <Select {...register('filtering.type')} label="Filtering Type" options={[{ label: 'None', value: 0 }, { label: 'Interpolation', value: 1 }, {label: 'Extrapolation', value: 2 }]}></Select>
                    <NumberSelector variant="smol" control={control} name="filtering.intensity" label="Intensity" valueLabelFormat={(value) => `${value}%`} min={0} max={100} step={10}></NumberSelector>
                    <NumberSelector variant="smol" control={control} name="filtering.ticks" label="Ticks" min={0} max={80} step={1}></NumberSelector>
                </div>
            </div>
        </form>
    )
}
