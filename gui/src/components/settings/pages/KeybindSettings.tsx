import { WrenchIcon } from "@/components/commons/icon/WrenchIcons";
import { SettingsPageLayout, SettingsPagePaneLayout } from "../SettingsPageLayout";
import { Typography } from "@/components/commons/Typography"; 
import { Localized, useLocalization } from "@fluent/react";
import { Input } from "@/components/commons/Input";
import { useForm } from "react-hook-form";
import { useEffect, useRef, useState } from "react";
import { RecordBVHRequest } from "solarxr-protocol";
import { Keybind } from "@/components/commons/Keybind";


export type KeybindsForm = {
    fullResetBinding: string;
    yawResetBinding: string;
    mountingResetBinding: string;
    pauseTrackingBinding: string;
}

const defaultValues: KeybindsForm = {
    fullResetBinding: "CTRL+ALT+SHIFT+Y",
    yawResetBinding: "CTRL+ALT+SHIFT+U",
    mountingResetBinding: "CTRL+ALT+SHIFT+I",
    pauseTrackingBinding: "CTRL+ALT+SHIFT+O"
}

export function useKeybindsForm() {
    const { register, reset, handleSubmit, formState, control } =
    useForm<KeybindsForm>({
        defaultValues,
        reValidateMode: 'onSubmit',
    });

    return {
        control,
        register,
        reset,
        handleSubmit,
        formState
    };
}


export function KeybindSettings() {
    const { l10n } = useLocalization();
    const { control } = useKeybindsForm();

    const [key, setKey] = useState("");
    const [recordedKeybind, setRecordedKeybind] = useState("");
    const keyCountRef = useRef(0);
    const ref = useRef();


    const handleKeyDown = (event: any) => {
        if (keyCountRef.current < 3) {
            setKey(event.key);
            setRecordedKeybind(recordedKeybind + "+" + key);
            keyCountRef.current++;
        }
    }

    
    return (
        <SettingsPageLayout>
            <form className="flex flex-col gap-2 w-full">
                <SettingsPagePaneLayout icon={<WrenchIcon />} id="keybinds">
                    <>
                        <Typography variant="main-title">
                            {l10n.getString('settings-general-keybinds')}
                        </Typography>
                        <div className="flex flex-col pt-2 pb-4">
                            {l10n.
                                getString(
                                    'settings-keybinds-description'
                                )
                                .split('\n')
                                .map((line, i) => (
                                    <Typography key={i}>{line}</Typography>
                                ))}
                        </div>
                        <Keybind 
                            name="keybinds.fullResetBinding"
                            label="Full Reset"
                        />
                        <div className="flex flex-col pt-4" />
                        <Keybind
                            name="keybinds.yawResetBinding"
                            label="Yaw Reset"
                        />
                        <div className="flex flex-col pt-4" />
                        <Keybind
                            name="keybinds.mountingResetBinding"
                            label="Mounting Reset"
                        />
                        <div className="flex flex-col pt-4" />
                        <Keybind                            
                            name="keybinds.pauseTrackingBinding"
                            label="Pause Tracking"
                        />                            
                    </>
                </SettingsPagePaneLayout>
            </form>
        </SettingsPageLayout>
    )
}