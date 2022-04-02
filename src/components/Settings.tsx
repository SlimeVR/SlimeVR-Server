import { useWebsocketAPI } from "../hooks/websocket-api";
import { CheckBox } from "./commons/Checkbox";
import { useForm } from "react-hook-form";
import { useEffect } from "react";
import { FilteringSettings, FilteringSettingsT, InboundUnion, OutboundUnion, SettingsRequestT, SteamVRTrackersSettingT } from "slimevr-protocol/dist/server";
import { ChangeSettingsRequestT } from "slimevr-protocol/dist/slimevr-protocol/server/change-settings-request";
import { SettingsResponseT } from "slimevr-protocol/dist/slimevr-protocol/server/settings-response";
import { Select } from "./commons/Select";
import { NumberSelector } from "./commons/NumberSelector";


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

    const { sendPacket, usePacket } = useWebsocketAPI();
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
        sendPacket(InboundUnion.ChangeSettingsRequest, settings)
    }


    useEffect(() => {
        const subscription = watch(() => handleSubmit(onSubmit)());
        return () => subscription.unsubscribe();
    }, [watch])

    
    useEffect(() => {
        sendPacket(InboundUnion.SettingsRequest, new SettingsRequestT());
    }, [])

    usePacket(OutboundUnion.SettingsResponse, (settings: SettingsResponseT) => {
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
                        {/* <CheckBox {...register('trackers.chest')} label="Chest"/>
                        <CheckBox {...register('trackers.legs')} label="Legs"/>
                        <CheckBox {...register('trackers.knees')} label="Knees"/>
                        <CheckBox {...register('trackers.elbows')} label="Elbows"/> */}
                    </div>
                </div>
            </div>
        </form>
    )

}