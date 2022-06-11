import classNames from "classnames";
import { useMemo, useState } from "react";
import { AutoBoneEpochResponseT, AutoBoneProcessRequestT, AutoBoneProcessStatusResponseT, AutoBoneProcessType, RpcMessage, SkeletonConfigRequestT, SkeletonPartT } from "solarxr-protocol";
import { useWebsocketAPI } from "../../hooks/websocket-api";
import { Button } from "../commons/Button";
import { AppModal } from "../Modal";
import { bodyPartLabels } from "./BodyProportions";

export function AutomaticCalibration() {
    const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();

    const [isOpen, setOpen] = useState(false);
    const [isProcessRunning, setProcessRunning] = useState(false);
    const [hasRecording, setHasRecording] = useState(false);
    const [hasCalibration, setHasCalibration] = useState(false);
    const [progress, setProgress] = useState(0);
    const [skeletonParts, setSkeletonParts] = useState<SkeletonPartT[] | null>(null);

    const bodyParts = useMemo(() => {
        return skeletonParts?.map(({ bone, value }) => ({ bone, label: bodyPartLabels[bone], value })) || []
    }, [skeletonParts])

    const startProcess = (processType: AutoBoneProcessType) => {
        // Don't allow multiple processes at once (for now atleast)
        if (isProcessRunning) {
            return;
        }

        setProcessRunning(true);
        setProgress(0);

        const processRequest = new AutoBoneProcessRequestT();
        processRequest.processType = processType;
        
        sendRPCPacket(RpcMessage.AutoBoneProcessRequest, processRequest)
    }

    const startRecording = () => {
        setHasRecording(false);
        startProcess(AutoBoneProcessType.RECORD);
    }

    const startProcessing = () => {
        setHasCalibration(false);
        startProcess(AutoBoneProcessType.PROCESS);
    }

    useRPCPacket(RpcMessage.AutoBoneProcessStatusResponse, (data: AutoBoneProcessStatusResponseT) => {
        if (data.completed) {
            setProcessRunning(false);
            setProgress(1);
        }

        if (data.processType) {
            if (data.message) {
                console.log(AutoBoneProcessType[data.processType], ": ", data.message);
            }

            if (data.total > 0 && data.current >= 0) {
                setProgress(data.current / data.total);
            }

            if (data.completed) {
                console.log("Process ", AutoBoneProcessType[data.processType], " has completed");

                switch (data.processType) {
                    case AutoBoneProcessType.RECORD:
                        setHasRecording(data.success);
                        break;

                    case AutoBoneProcessType.PROCESS:
                        setHasCalibration(data.success);
                        break;

                    case AutoBoneProcessType.APPLY:
                        // Update skeleton config when applied
                        sendRPCPacket(RpcMessage.SkeletonConfigRequest, new SkeletonConfigRequestT())
                        break;
                }
            }
        }
    })

    useRPCPacket(RpcMessage.AutoBoneEpochResponse, (data: AutoBoneEpochResponseT) => {
        setProgress(data.currentEpoch/data.totalEpochs);

        // Probably not necessary to show to the user
        console.log("Epoch ", data.currentEpoch, "/", data.totalEpochs, " (Error ", data.epochError, ")");

        setSkeletonParts(data.adjustedSkeletonParts);
    })

    return (
        <>
            <Button variant="primary" onClick={() => setOpen(true)}>Automatic calibration</Button>
            <AppModal
                isOpen={isOpen}
                name={<>Automatic Calibration</>}
                onRequestClose={() => setOpen(false)}
            >
                <>
                    <div className="flex w-full justify-center gap-3">
                    <Button variant="primary" onClick={startRecording} disabled={isProcessRunning}>Start Recording</Button>
                    <Button variant="primary" onClick={() => startProcess(AutoBoneProcessType.SAVE)} disabled={isProcessRunning || !hasRecording}>Save Recording</Button>
                    <Button variant="primary" onClick={startProcessing} disabled={isProcessRunning}>Start Calibration</Button>
                    </div>
                    <div className="flex flex-col w-full h-12 p-2">
                        <div className="w-full rounded-full h-full overflow-hidden relative bg-purple-gray-800">
                            <div className={classNames("h-full top-0 left-0 bg-purple-gray-300", { 'transition-all': progress > 0})} style={{width: `${progress * 100}%`}}></div>
                        </div>
                    </div>
                    <div className="flex flex-col w-full p-2">
                        {bodyParts.map(({label, bone, value}) =>
                            <div key={bone} className="px-3 rounded-lg py-2 hover:bg-purple-gray-600">
                                <div className="flex flex-row gap-5">
                                    <div className="flex flex-grow justify-start items-center text-field-title">{label}</div>
                                    <div className="flex justify-center items-center w-16 text-field-title">{`${Number(value * 100).toFixed(1).replace(/[.,]0$/, "")}cm`}</div>
                                </div>
                            </div>
                        )}
                    </div>
                    <div className="flex w-full justify-between mt-3">
                        <Button variant="primary" onClick={() => setOpen(false)}>Close</Button>
                        <Button variant="primary" onClick={() => startProcess(AutoBoneProcessType.APPLY)} disabled={isProcessRunning || !hasCalibration}>Apply values</Button>
                    </div>
                </>
            </AppModal>
        </>
    )

}