import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useOnboarding } from '@/hooks/onboarding';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import classNames from 'classnames';
import { Dropdown } from '@/components/commons/Dropdown';
import { DonglePage } from './Dongle';
import { WifiCredsPage } from './WifiCreds';
import { FirmwareToolSettings } from '@/components/firmware-tool/FirmwareTool';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import {
    RpcMessage,
    SettingsResponseT,
    ChangeSettingsRequestT,
    ModelSettingsT,
    SettingsRequestT,
    VRCOSCSettingsT,
    ResetsSettingsT,
    ModelTogglesT,
} from 'solarxr-protocol';
export function QuizPage() {
    const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
    const { applyProgress} = useOnboarding();
    const [ continueButton, setContinueButton] = useState(true);
    const [ step, setStep] = useState("Q1");
    const [settings, setSettings] = useState<SettingsResponseT>();
    interface OnboardingQuestions {
        slimeSet: 'butterfly' | 'regular-slime';
        firmware: 'Yes' | 'No';
        usage: 'vrchat' | 'mocap' | 'vtubing';
        runtime: 'steamvr' | 'standalone'
        mocapPos: 'Forehead' | 'Face'
    }
    const { control, watch, handleSubmit } = useForm<OnboardingQuestions>();
    const [answers, setAnswers] = useState<OnboardingQuestions>({
        slimeSet: 'regular-slime',
        firmware: 'No', 
        usage: 'vrchat', 
        runtime: 'steamvr',
        mocapPos: 'Forehead'})

    applyProgress(0.2);

    useEffect(() => {
        sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT())
    }, []);

    useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
        setSettings(settings);
    })

    useEffect(() => {
        const subscription = watch(() => handleSubmit(onSubmit)());
        return () => subscription.unsubscribe();
    }, []);

    const onSubmit = (answers: OnboardingQuestions ) => {
        console.log(answers);
        setContinueButton(false);
        setAnswers(answers);
    }

    const applySettings = () => {
        if (!settings?.modelSettings || !settings?.vrcOsc) throw 'settings should be set';
        const req = new ChangeSettingsRequestT()
        const modelSettings = new ModelSettingsT();
        const oscSettings = new VRCOSCSettingsT();
        const resetSettings = new ResetsSettingsT();

        if (answers.usage === 'mocap') {
            const toggles = Object.assign(new ModelTogglesT(), settings.modelSettings.toggles);
            toggles.selfLocalization = true;
            modelSettings.toggles = toggles;
            req.modelSettings = modelSettings;

            if (answers.mocapPos === 'Forehead') {
                const resets = Object.assign(resetSettings, settings.resetsSettings);
                resets.resetHmdPitch = true;
                req.resetsSettings = resets;
            }
        }

        if (answers.runtime === 'standalone') {
            const osc = Object.assign(oscSettings, settings.vrcOsc.oscSettings);
            osc.enabled = true;
            oscSettings.oscSettings = osc;
            req.vrcOsc = oscSettings;
        }
        sendRPCPacket(RpcMessage.ChangeSettingsRequest, req);
    }
    
    const questions = {
        SlimeSet: {
            options: ["Butterfly", "Regular Slime"],
        },
        // thirdParty: {
        //    options: ["Official", "DIY/Third Party"]     want to keep this here just in case, but dont have a use for it for now
        // },
        usage: {
            options: ["VR Gaming", "Mocap (Motion Capture)", "VTubing"]
        },
        // experience: {
        //     options: ["Beginner", "Intermediate", "Experienced"]     this question should stay here, but wait for until we get around to rearranging the settings, as this can help with that
        // },
        firmware: {
            options: ["Yes", "No"]
        },
        runtime: {
            options: ["SteamVR", "Standalone"]
        },
        mocapPos: {
            options: ["Forehead", "Above Forehead"]
        },
    };

        return (
            <div className="flex flex-col w-full h-full xs:justify-center items-center">
                {step == "Q1" && (
                    <div className="flex flex-col gap-2">
                        <div className="flex gap-2 items-center">
                            <Typography
                            variant="main-title"
                            id="onboarding-quiz-q1-title"
                            />
                        </div>
                        <div className={classNames('flex flex-col gap-2 flex-grow p-2')}>
                            <Typography
                            whitespace="whitespace-pre-wrap"
                            id="onboarding-quiz-q1-description"
                            />
                        </div>
                        <div className="flex px-2 p-6">
                            <Dropdown
                                control={control}
                                name={"slimeSet"}
                                display="block"
                                placeholder={"Select Answer here"}
                                items={questions.SlimeSet.options.map(i => ({value: i, label: i}))}
                                direction='down'
                            />
                        </div>
                        <div className="flex px-2 p-6">
                            <Button 
                            disabled={continueButton}
                            children="Continue"
                            variant="primary"
                            onClick={() => {
                                setContinueButton(true);
                                if (answers.slimeSet === 'regular-slime') {
                                    setStep("Update?");
                                } else {
                                    setStep("Q3");
                                }
                            }}
                            />
                        </div>
                    </div>
                )}
                {step == "Q3" && (
                    <div className="flex flex-col gap-2">
                        <div className="flex gap-2 items-center">
                            <Typography
                            variant="main-title"
                            id="onboarding-wifi_creds-dongle-title"
                            />
                        </div>
                        <div className={classNames('flex flex-col gap-2 flex-grow p-2')}>
                            <Typography
                            whitespace="whitespace-pre-wrap"
                            id="onboarding-wifi_creds-dongle-description"
                            />
                        </div>
                        <div className="flex px-2 p-6">
                            <Dropdown
                                control={control}
                                name={"usage"}
                                display="block"
                                placeholder={"Select Answer here"}
                                items={questions.usage.options.map(i => ({value: i, label: i}))}
                                direction='down'
                            />
                        </div>
                        <div className="flex px-2 p-6">
                            <Button 
                            disabled={continueButton}
                            children="Continue"
                            variant="primary"
                            onClick={() => {
                                if (answers.usage === "vrchat") {
                                    setStep("Q4");
                                } else if (answers.usage === 'mocap' || answers.usage === 'vtubing') {
                                    setStep("Q5")
                                } else {
                                    if (answers.slimeSet === 'butterfly') {
                                        setStep("Dongle");
                                    } else {
                                        if (answers.firmware === 'Yes') {
                                            setStep("FirmUpd");

                                        } else {setStep("Wifi");}
                                    }
                                    
                                }
                            }}
                            />
                        </div>
                    </div>
                )}
                {step == "Q4" && (
                    <div className="flex flex-col gap-2">
                        <div className="flex gap-2 items-center">
                            <Typography
                            variant="main-title"
                            id="onboarding-wifi_creds-dongle-title"
                            />
                        </div>
                        <div className={classNames('flex flex-col gap-2 flex-grow p-2')}>
                            <Typography
                            whitespace="whitespace-pre-wrap"
                            id="onboarding-wifi_creds-dongle-description"
                            />
                        </div>
                        <div className="flex px-2 p-6">
                            <Dropdown
                                control={control}
                                name={"runtime"}
                                display="block"
                                placeholder={"Select Answer here"}
                                items={questions.runtime.options.map(i => ({value: i, label: i}))}
                                direction='down'
                            />
                        </div>
                        <div className="flex px-2 p-6">
                            <Button 
                            disabled={continueButton}
                            children="Continue"
                            variant="primary"
                            onClick={() => {
                                if (answers.slimeSet === 'butterfly') {
                                    setStep("Dongle");
                                } else {
                                    if (answers.firmware === 'Yes') {
                                        setStep("FirmUpd");

                                    } else {setStep("Wifi");}
                                }
                            }}
                            />
                        </div>
                    </div>
                )}
                {step == "Q5" && (
                    <div className="flex flex-col gap-2">
                        <div className="flex gap-2 items-center">
                            <Typography
                            variant="main-title"
                            id="onboarding-wifi_creds-dongle-title"
                            />
                        </div>
                        <div className={classNames('flex flex-col gap-2 flex-grow p-2')}>
                            <Typography
                            whitespace="whitespace-pre-wrap"
                            id="onboarding-wifi_creds-dongle-description"
                            />
                        </div>
                        <div className="flex px-2 p-6">
                            <Dropdown
                                control={control}
                                name={"mocapPos"}
                                display="block"
                                placeholder={"Select Answer here"}
                                items={questions.mocapPos.options.map(i => ({value: i, label: i}))}
                                direction='down'
                            />
                        </div>
                        <div className="flex px-2 p-6">
                            <Button 
                            disabled={continueButton}
                            children="Continue"
                            variant="primary"
                            onClick={() => {
                                if (answers.slimeSet === 'butterfly') {
                                    setStep("Dongle");
                                } else {
                                    if (answers.firmware === 'Yes') {
                                        setStep("FirmUpd");

                                    } else {setStep("Wifi");}
                                }
                            }}
                            />
                        </div>
                    </div>
                )}
                {step == "Update?" && (
                    <div className="flex flex-col gap-2">
                        <div className="flex gap-2 items-center">
                            <Typography
                            variant="main-title"
                            id="onboarding-wifi_creds-dongle-title"
                            />
                        </div>
                        <div className={classNames('flex flex-col gap-2 flex-grow p-2')}>
                            <Typography
                            whitespace="whitespace-pre-wrap"
                            id="onboarding-wifi_creds-dongle-description"
                            />
                        </div>
                        <div className="flex px-2 p-6">
                            <Dropdown
                                control={control}
                                name={"firmware"}
                                display="block"
                                placeholder={"Select Answer here"}
                                items={questions.firmware.options.map(i => ({value: i, label: i}))}
                                direction='down'
                            />
                        </div>
                        <div className="flex px-2 p-6">
                            <Button 
                            disabled={continueButton}
                            children="Continue"
                            variant="primary"
                            onClick={() => {
                                setStep("Q3");
                                setContinueButton(true);
                            }}
                            />
                        </div>
                    </div>
                )}
                {step == "Dongle" && (
                    <DonglePage/>
                )}
                {step == "Wifi" && (
                    <WifiCredsPage/>
                )}
                {step == "FirmUpd" && (
                    <FirmwareToolSettings/>
                )}
            </div>
        );
    }