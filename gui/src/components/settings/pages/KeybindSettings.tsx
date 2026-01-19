import { WrenchIcon } from "@/components/commons/icon/WrenchIcons";
import { SettingsPageLayout, SettingsPagePaneLayout } from "../SettingsPageLayout";
import { Typography } from "@/components/commons/Typography"; 
import { useLocalization } from "@fluent/react";
import { useForm } from "react-hook-form";
import { useEffect, useRef, useState } from "react";
import { KeybindInput } from "@/components/commons/Keybind";
import { useWebsocketAPI } from "@/hooks/websocket-api";
import { KeybindRequestT, KeybindResponseT, RpcMessage, Keybind, KeybindT } from 'solarxr-protocol';


export type KeybindsForm = {
    fullResetBinding: string[];
    yawResetBinding: string[];
    mountingResetBinding: string[];
    pauseTrackingBinding: string[];
}

const defaultValues: KeybindsForm = {
    fullResetBinding: ["CTRL", "ALT", "SHIFT", "Y"],
    yawResetBinding: ["CTRL", "ALT", "SHIFT", "U"],
    mountingResetBinding: ["CTRL", "ALT", "SHIFT", "I"],
    pauseTrackingBinding: ["CTRL", "ALT", "SHIFT", "O"]
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
    const { sendRPCPacket, useRPCPacket} = useWebsocketAPI();

    const [requestedKeybinds, setRequestedKeybinds] = useState<KeybindT[] | null>();

    useEffect(() => {
        sendRPCPacket(
            RpcMessage.KeybindRequest,
            new KeybindRequestT()
        );
    }, []);


    useRPCPacket(
        RpcMessage.KeybindResponse,
        ({ keybind }: KeybindResponseT) => {
            setRequestedKeybinds(keybind)

            const keybindValues: KeybindsForm = {
                fullResetBinding: keybind[0].keybindValue.split("+"),

            }
        }
    )

    /*
    const keybindElements = requestedKeybinds?.map(requestedKeybind => {
        <KeybindInput 
            name=""
            label={requestedKeybind.keybindName.toString()}
            value={
                requestedKeybind.keybindValue != null ?
                    requestedKeybind.keybindValue.toString()
                    : ""
            }
            delay={requestedKeybind.keybindDelay}
            />
        }
    )
        */

    console.log(requestedKeybinds)

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
                        {
                            <>
                                <KeybindInput 
                                    name="keybinds.fullResetBinding"
                                    label="Full Reset"
                                    control={control}
                                    delay={2.0}
                                />
                                <div className="flex flex-col pt-4" />
                                {/*}
                                <KeybindInput
                                    name="keybinds.yawResetBinding"
                                    label="Yaw Reset"
                                />
                                <div className="flex flex-col pt-4" />
                                <KeybindInput
                                    name="keybinds.mountingResetBinding"
                                    label="Mounting Reset"
                                />
                                <div className="flex flex-col pt-4" />
                                <KeybindInput                            
                                    name="keybinds.pauseTrackingBinding"
                                    label="Pause Tracking"
                                    />  
                                    */
                                }
                            </>
                        }                          
                    </>
                </SettingsPagePaneLayout>
            </form>
        </SettingsPageLayout>
    )
}