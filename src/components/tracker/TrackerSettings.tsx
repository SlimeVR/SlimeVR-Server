import { ReactChild, useMemo, useState } from "react";
import { Button } from "../commons/Button";
import { AppModal } from "../Modal";
import { Select } from "../commons/Select";
import { useWebsocketAPI } from "../../hooks/websocket-api";
import { useForm } from "react-hook-form";
import { AssignTrackerRequestT, BodyPart, DeviceDataT, RpcMessage, TrackerDataT } from "solarxr-protocol";
import { FixEuler, QuaternionFromQuatT, QuaternionToQuatT } from "../../maths/quaternion";
import { Quaternion } from "math3d";


const rotationToQuatMap = {
    FRONT: 180,
    LEFT: 90,
    RIGHT: -90,
    BACK: 0
}

export function TrackerSettings({ tracker, device, children }: { tracker: TrackerDataT, device?: DeviceDataT, children: ReactChild }) {

    const { sendRPCPacket } = useWebsocketAPI();
    const { register, handleSubmit, reset } = useForm({ defaultValues: { bodyPosition: 0, mountingRotation: rotationToQuatMap.BACK } });


    const [open, setOpen] = useState(false);

    const positions = useMemo(() => Object.keys(BodyPart).filter((position: string) => isNaN(+position)).map((role, index) =>( { label: role, value: index })), [])
    const rotations = useMemo(() => [
        { label: 'FRONT', value: rotationToQuatMap.FRONT }, 
        { label: 'LEFT', value: rotationToQuatMap.LEFT }, 
        { label: 'RIGHT', value: rotationToQuatMap.RIGHT }, 
        { label: 'BACK', value: rotationToQuatMap.BACK }
    ], []);


    const handleSaveSettings = ({ bodyPosition, mountingRotation }:  { bodyPosition: number, mountingRotation: number }) => {
        const assignreq = new AssignTrackerRequestT();

        assignreq.bodyPosition = bodyPosition;

    
        assignreq.mountingRotation = QuaternionToQuatT(Quaternion.Euler(0, +mountingRotation, 0));
        assignreq.trackerId = tracker.trackerId;

        sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq)
        setOpen(false);
    }


    const openSettings = () => {

        if (!tracker.info?.editable)
            return;

        setOpen(true)
        reset({
            bodyPosition: tracker.info?.bodyPart,
            mountingRotation: tracker.info?.mountingOrientation ? FixEuler(QuaternionFromQuatT(tracker.info?.mountingOrientation!).eulerAngles.y) : rotationToQuatMap.BACK
        })
    }


    return (
        <>
            <div onClick={openSettings}>
                {children}
            </div>
            <AppModal 
                isOpen={open} 
                onRequestClose={() => setOpen(false)}
                name={<>{tracker.info?.bodyPart ? `${BodyPart[tracker.info?.bodyPart]} Settings` : 'Tracker Settings'}</>}
            >
                <form onSubmit={handleSubmit(handleSaveSettings)} className="flex flex-col gap-5">
                    <div className="flex flex-col gap-5">
                        <Select {...register("bodyPosition")} label="Tracker role" options={positions}></Select>
                        <Select {...register("mountingRotation")} label="Mounting Rotation" options={rotations}></Select>
                    </div>
                    <div className="flex items-center justify-between">
                        <Button variant="primary" type="submit" >Save</Button>
                        <Button variant="primary" type="button" onClick={() => setOpen(false)}>Close</Button>
                    </div>
                </form>
            </AppModal>
        </>
    )
}