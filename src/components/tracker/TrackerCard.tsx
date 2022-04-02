import classNames from "classnames";
import { useEffect, useMemo, useState } from "react";
import { DeviceStatusT, TrackerPosition } from "slimevr-protocol/dist/server";
import { TrackerStatus } from "slimevr-protocol/dist/slimevr-protocol/server/tracker-status";
import { IconButton } from "../commons/ButtonIcon";
import { BatteryIcon } from "../commons/icon/BatteryIcon";
import { GearIcon } from "../commons/icon/GearIcon";
import { WifiIcon } from "../commons/icon/WifiIcon";
import { TrackerSettings } from "./TrackerSettings";
import { Quaternion } from '../../maths/quaternion';


export function TrackerCard({ status }:  { status: DeviceStatusT }) {

    const [previousRot, setPreviousRot] = useState<{ x: number, y: number, z: number, w: number }>({ x: 0, y: 0, z: 0, w: 0 })
    const [velocity, setVelocity] = useState<number>(0);

    const statusClass = useMemo(() => {
        const statusMap: { [key: number]: string } = {
            [TrackerStatus.NONE]: 'bg-cyan-800',
            [TrackerStatus.BUSY]: 'bg-misc-4',
            [TrackerStatus.ERROR]: 'bg-misc-2',
            [TrackerStatus.DISCONNECTED]: 'bg-gray-800',
            [TrackerStatus.OCCLUDED]: 'bg-misc-4',
            [TrackerStatus.OK]: 'bg-misc-1'
        }
        return statusMap[status.status];
    }, [status]);

    useEffect(() => {
        if (status.rotation) {
            const rot = Quaternion.from(status.rotation).mult(Quaternion.from(previousRot).inverse());
            const dif = Math.min(100, (rot.x**2 + rot.y**2 + rot.z**2) * 2.5)
            setVelocity(dif);
            setPreviousRot(status.rotation);
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [status])


    return (
        <TrackerSettings status={status} >
            <div  className={classNames("flex rounded-l-md rounded-r-xl", statusClass)}>
                <div className="flex bg-primary-4 rounded-r-md py-3 ml-1 pr-2 pl-4 w-full gap-3">
                    <div className="flex flex-grow flex-col truncate gap-2">
                        <div className="flex text-white font-bold">{!status.computed && status.editable ? TrackerPosition[status.mountingPosition] : status.name}</div>
                        <div className="flex flex-row gap-4 ">
                            <div className="flex gap-2 flex-grow">
                                <div className="flex flex-col justify-around">
                                    <WifiIcon value={status.signal} />
                                </div>
                                <div className="flex text-gray-400 text-sm  w-10">{status.ping} ms</div>
                            </div>
                            <div className="flex  w-1/3 gap-2">
                                <div className="flex flex-col justify-around">
                                    <BatteryIcon value={status.battery / 256}/>
                                </div>
                                <div className="flex text-gray-400 text-sm">{((status.battery / 255) * 100).toFixed(0)} %</div>
                            </div>
                            <div className="flex  w-1/3 gap-0.5 justify-around flex-col">
                                <div className="w-full bg-gray-200 rounded-full h-1 dark:bg-gray-700">
                                    <div className="bg-misc-3 h-1 rounded-full" style={{width: `${velocity * 100}%`}}></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    {status.editable && 
                        <div className="flex flex-col flex-shrink justify-around">
                            <IconButton icon={<GearIcon/>}/>
                        </div>
                    }
                </div>
            </div>
        </TrackerSettings>
    )

}