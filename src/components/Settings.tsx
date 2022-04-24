import { useWebsocketAPI } from "../hooks/websocket-api";
import { CheckBox } from "./commons/Checkbox";
import { useForm } from "react-hook-form";
import { useEffect } from "react";
import { Select } from "./commons/Select";
import { NumberSelector } from "./commons/NumberSelector";
import { WIFIButton } from "./WifiButton";
import { ChangeSettingsRequestT, FilteringSettingsT, RpcMessage, SettingsRequestT, SettingsResponseT, SteamVRTrackersSettingT } from "slimevr-protocol";

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

export function Settings() {

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

    return (
        <form className="px-8 flex flex-col gap-4 pt-4">
            <div className="flex text-white text-2xl font-bold">
                Settings
            </div>
            <div className="flex flex-col gap-8">
                <div className="flex flex-col gap-2">
                    <div className="flex text-gray-300 text-xl font-bold gap-5">
                        SteamVR Trackers
                    </div>
                    <div className="flex text-gray-300 text-xl font-bold gap-5">
                        <CheckBox {...register('trackers.waist')} label="Waist" />
                        <CheckBox {...register('trackers.chest')} label="Chest"/>
                        <CheckBox {...register('trackers.legs')} label="Legs"/>
                        <CheckBox {...register('trackers.knees')} label="Knees"/>
                        <CheckBox {...register('trackers.elbows')} label="Elbows"/>
                    </div>
                </div>
                <div className="flex flex-col gap-2">
                    <div className="flex text-gray-300 text-xl font-bold gap-5">
                        Trackers Filtering
                    </div>
                    <div className="flex text-gray-300 text-xl font-bold gap-5">
                        <Select {...register('filtering.type')} label="Filtering Type" options={[{ label: 'None', value: 0 }, { label: 'Interpolation', value: 1 }, {label: 'Extrapolation', value: 2 }]}></Select>
                        <NumberSelector variant="smol" control={control} name="filtering.intensity" label="Intensity" valueLabelFormat={(value) => `${value}%`} min={0} max={100} step={10}></NumberSelector>
                        <NumberSelector variant="smol" control={control} name="filtering.ticks" label="Ticks" min={0} max={80} step={1}></NumberSelector>
                    </div>
                </div>
                <div className="flex flex-col gap-2">
                    <div className="flex text-gray-300 text-xl font-bold gap-5">
                        Wifi Settings
                    </div>
                    <div className="flex text-gray-300 text-xl font-bold gap-5">
                        <WIFIButton>Open Wifi Settings</WIFIButton>
                    </div>
                </div>
            </div>
        </form>
    )

}