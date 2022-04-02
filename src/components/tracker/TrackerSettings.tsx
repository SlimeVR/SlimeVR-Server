import { ReactChild, useMemo, useState } from "react";
import { AssignTrackerRequestT, DeviceStatusT, InboundUnion, TrackerPosition } from "slimevr-protocol/dist/server";
import { Button } from "../commons/Button";
import { AppModal } from "../Modal";
import { Select } from "../commons/Select";
import { useWebsocketAPI } from "../../hooks/websocket-api";
import { useForm } from "react-hook-form";


export function TrackerSettings({ status, children }: { status: DeviceStatusT, children: ReactChild }) {

    const { sendPacket } = useWebsocketAPI();
    const { register, handleSubmit, reset } = useForm({ defaultValues: { mountingPosition: 0, mountingRotation: 0 } });


    const [open, setOpen] = useState(false);

    const positions = useMemo(() => Object.keys(TrackerPosition).filter((position: string) => isNaN(+position)).map((role, index) =>( { label: role, value: index })), [])
    const rotations = useMemo(() => [{ label: 'FRONT', value: 180 }, { label: 'LEFT', value: 90 }, { label: 'RIGHT', value: -90 }, { label: 'BACK', value: 0 }], [])


    const handleSaveSettings = ({ mountingPosition, mountingRotation }:  { mountingPosition: number, mountingRotation: number }) => {
        const assignreq = new AssignTrackerRequestT();

        assignreq.mountingRotation = mountingRotation;
        assignreq.mountingPosition = mountingPosition;
        assignreq.id = status.id;

        sendPacket(InboundUnion.AssignTrackerRequest, assignreq, true).then((res) => {
            if (res) setOpen(false);
        });
    }


    const openSettings = () => {

        if (!status.editable)
            return;

        setOpen(true)
        reset({
            mountingRotation: status.mountingRotation,
            mountingPosition: status.mountingPosition
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
                name={<>{status.mountingPosition ? `${TrackerPosition[status.mountingPosition]} Settings` : 'Tracker Settings'}</>}
            >
                <form onSubmit={handleSubmit(handleSaveSettings)} className="flex flex-col gap-5">
                    <div className="flex flex-col gap-5">
                        <Select {...register("mountingPosition")} label="Tracker role" options={positions}></Select>
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