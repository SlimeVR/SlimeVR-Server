import { WrenchIcon } from "@/components/commons/icon/WrenchIcons";
import { SettingsPageLayout, SettingsPagePaneLayout } from "../SettingsPageLayout";
import { Typography } from "@/components/commons/Typography"; 
import { useLocalization } from "@fluent/react";
import { DefaultValues, useForm } from "react-hook-form";
import { useEffect, useRef, useState } from "react";
import { KeybindInput } from "@/components/commons/Keybind";
import { Button } from "@/components/commons/Button";
import { useWebsocketAPI } from "@/hooks/websocket-api";
import { KeybindRequestT, KeybindResponseT, RpcMessage, Keybind, KeybindT, KeybindName, ChangeKeybindRequestT } from 'solarxr-protocol';
import { ResetButtonIcon } from "@/components/home/ResetButton";


export type KeybindsForm = {
    names: {
        fullResetName: KeybindName;
        yawResetName: KeybindName;
        mountingResetName: KeybindName;
        pauseTrackingName: KeybindName;
    }
    bindings: {
        fullResetBinding: string[];
        yawResetBinding: string[];
        mountingResetBinding: string[];
        pauseTrackingBinding: string[];
    };
    delays: {
        fullResetDelay: bigint;
        yawResetDelay: bigint;
        mountingResetDelay: bigint;
        pauseTrackingDelay: bigint;
    }
}

const defaultValues: KeybindsForm = {
    names: {
        fullResetName: KeybindName.FULL_RESET,
        yawResetName: KeybindName.YAW_RESET,
        mountingResetName: KeybindName.MOUNTING_RESET,
        pauseTrackingName: KeybindName.PAUSE_TRACKING
    },
    bindings: {
        fullResetBinding: ["CTRL", "ALT", "SHIFT", "Y"],
        yawResetBinding: ["CTRL", "ALT", "SHIFT", "U"],
        mountingResetBinding: ["CTRL", "ALT", "SHIFT", "I"],
        pauseTrackingBinding: ["CTRL", "ALT", "SHIFT", "O"]
    },
    delays: {
        fullResetDelay: 0n,
        yawResetDelay: 0n,
        mountingResetDelay: 0n,
        pauseTrackingDelay: 0n
    }
}

export function useKeybindsForm() {
    const { register, reset, handleSubmit, formState, control, getValues, watch } =
    useForm<KeybindsForm>({
        defaultValues,
    });

    return {
        control,
        register,
        reset,
        handleSubmit,
        formState,
        getValues,
        watch
    };
}

export function KeybindSettings() {
    const { l10n } = useLocalization();
    const { control, reset, handleSubmit, watch, getValues } = useKeybindsForm();
    const { sendRPCPacket, useRPCPacket} = useWebsocketAPI();

    const onSubmit = (values: KeybindsForm) => {
        const keybinds = new ChangeKeybindRequestT();

        const fullResetKeybind = new KeybindT();
        fullResetKeybind.keybindName = values.names.fullResetName;
        fullResetKeybind.keybindValue = values.bindings.fullResetBinding.join("+");
        fullResetKeybind.keybindDelay = values.delays.fullResetDelay;
        keybinds.keybind.push(fullResetKeybind);

        const yawResetKeybind = new KeybindT();
        yawResetKeybind.keybindName = values.names.yawResetName;
        yawResetKeybind.keybindValue = values.bindings.yawResetBinding.join("+");
        yawResetKeybind.keybindDelay = values.delays.yawResetDelay;
        keybinds.keybind.push(yawResetKeybind);

        const mountingResetKeybind = new KeybindT();
        mountingResetKeybind.keybindName = values.names.mountingResetName;
        mountingResetKeybind.keybindValue = values.bindings.mountingResetBinding.join("+");
        mountingResetKeybind.keybindDelay = values.delays.mountingResetDelay;
        keybinds.keybind.push(mountingResetKeybind);

        const pauseTrackingKeybind = new KeybindT();
        pauseTrackingKeybind.keybindName = values.names.pauseTrackingName;
        pauseTrackingKeybind.keybindValue = values.bindings.pauseTrackingBinding.join("+");
        pauseTrackingKeybind.keybindDelay = values.delays.pauseTrackingDelay;
        keybinds.keybind.push(pauseTrackingKeybind);

        sendRPCPacket(RpcMessage.ChangeKeybindRequest, keybinds);
    };

    useEffect(() => {
        const subscription = watch(() => handleSubmit(onSubmit)())
        return () => subscription.unsubscribe()
    }, [])

    useEffect(() => {
        sendRPCPacket(
            RpcMessage.KeybindRequest,
            new KeybindRequestT()
        );
    }, []);


    useRPCPacket(
        RpcMessage.KeybindResponse,
        ({ keybind }: KeybindResponseT) => {
            if (!keybind) return;

            console.log(`Keybind Name ${keybind[0].keybindName}`)
            console.log(`Keybind value ${keybind[0].keybindValue}`)
            console.log(`Keybind Delay ${keybind[0].keybindDelay}`)
            const keybindValues: KeybindsForm = {
                names: {
                    fullResetName: KeybindName.FULL_RESET,
                    yawResetName: KeybindName.YAW_RESET,
                    mountingResetName: KeybindName.MOUNTING_RESET,
                    pauseTrackingName: KeybindName.PAUSE_TRACKING,
                },
                bindings: {
                    fullResetBinding:
                    (typeof keybind[0].keybindValue === "string"
                        ? keybind[0].keybindValue
                        : ""
                    ).split("+"),

                    yawResetBinding:
                    (typeof keybind[1].keybindValue === "string"
                        ? keybind[1].keybindValue
                        : ""
                    ).split("+"),

                    mountingResetBinding:
                    (typeof keybind[2].keybindValue === "string"
                        ? keybind[2].keybindValue
                        : ""
                    ).split("+"),

                    pauseTrackingBinding:
                    (typeof keybind[3].keybindValue === "string"
                        ? keybind[3].keybindValue
                        : ""
                    ).split("+"),
                },
                delays: {
                    fullResetDelay: keybind[0].keybindDelay ?? 0n,
                    yawResetDelay: keybind[1].keybindDelay ?? 0n,
                    mountingResetDelay: keybind[2].keybindDelay ?? 0n,
                    pauseTrackingDelay: keybind[3].keybindDelay ?? 0n,
                },
            }
            console.log(keybindValues)
            //Is this the correct syntax for setting the form with received data?
            reset({ ...getValues(), ...keybindValues });
        }
    )



    const handleResetButton = () => {
        reset(defaultValues)
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
                        {
                            <>
                                <KeybindInput 
                                    label="Full Reset"
                                    control={control}
                                    bindingName="bindings.fullResetBinding"
                                    delayName="delays.fullResetDelay"
                                />
                                <div className="flex flex-col pt-4" />
                                
                                <KeybindInput
                                    bindingName="bindings.yawResetBinding"
                                    delayName="delays.yawResetDelay"
                                    label="Yaw Reset"
                                    control={control}
                                />
                                <div className="flex flex-col pt-4" />
                                <KeybindInput
                                    bindingName="bindings.mountingResetBinding"
                                    delayName="delays.mountingResetDelay"
                                    label="Mounting Reset"
                                    control={control}
                                />
                                <div className="flex flex-col pt-4" />
                                <KeybindInput                            
                                    bindingName="bindings.pauseTrackingBinding"
                                    delayName="delays.pauseTrackingDelay"
                                    label="Pause Tracking"
                                    control={control}
                                    />  
                                    
                            </>
                        }
                        <div className="flex flex-col pt-4" />                     
                        <Button
                            className="flex flex-col"
                            onClick={handleResetButton}
                            variant='primary'
                            >Reset all
                        </Button>       
                    </>
                </SettingsPagePaneLayout>
            </form>
        </SettingsPageLayout>
    )
}