import classNames from "classnames";
import { useEffect, useMemo, useRef, useState } from "react";
import { BodyPart, DeviceDataT, TrackerDataT, TrackerStatus } from "solarxr-protocol";
import { WifiIcon } from "../commons/icon/WifiIcon";
import { BatteryIcon } from "../commons/icon/BatteryIcon";
import { TrackerSettings } from "./TrackerSettings";
import { IconButton } from "../commons/ButtonIcon";
import { GearIcon } from "../commons/icon/GearIcon";
import { QuaternionFromQuatT } from "../../maths/quaternion";


export function TrackerCard({ tracker, device }:  { tracker: TrackerDataT, device?: DeviceDataT }) {

    const previousRot = useRef<{ x: number, y: number, z: number, w: number }>(tracker.rotation!)
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
        return statusMap[tracker.status];
    }, [tracker.status]);

    useEffect(() => {
        if (tracker.rotation) {
            const rot = QuaternionFromQuatT(tracker.rotation).mul(QuaternionFromQuatT(previousRot.current).inverse());
            const dif = Math.min(1, (rot.x**2 + rot.y**2 + rot.z**2) * 2.5)
            setVelocity(dif);
            previousRot.current = tracker.rotation;
        }
    }, [tracker.rotation])

    const trackerName = useMemo(() => {
        if (device?.customName)
            return device.customName;
        if (tracker.info?.bodyPart)
            return BodyPart[tracker.info?.bodyPart];
        return device?.hardwareInfo?.displayName || 'NONE';

    }, [tracker.info, device?.customName, device?.hardwareInfo?.displayName])

    return (
        <TrackerSettings tracker={tracker} device={device} >
            <div  className={classNames("flex rounded-l-md rounded-r-xl", statusClass)}>
                <div className="flex bg-primary-4 rounded-r-md py-3 ml-1 pr-2 pl-4 w-full gap-3">
                    <div className="flex flex-grow flex-col truncate gap-2">
                        <div className="flex text-white font-bold">{trackerName}</div>
                        <div className="flex flex-row gap-4 ">
                            {device && device.hardwareStatus &&
                                <>
                                    <div className="flex gap-2 flex-grow">
                                        {device.hardwareStatus.rssi && <div className="flex flex-col justify-around">
                                            <WifiIcon value={device.hardwareStatus?.rssi} />
                                        </div>}
                                        {device.hardwareStatus.ping && <div className="flex text-gray-400 text-sm  w-10">{device.hardwareStatus.ping} ms</div>}
                                    </div>
                                    {device.hardwareStatus.batteryPctEstimate &&
                                        <div className="flex w-1/3 gap-2">
                                            <div className="flex flex-col justify-around">
                                                <BatteryIcon value={device.hardwareStatus.batteryPctEstimate / 100}/>
                                            </div>
                                            <div className="flex text-gray-400 text-sm">{((device.hardwareStatus.batteryPctEstimate)).toFixed(0)} %</div>
                                        </div>
                                    }
                                </>
                            }
                          
                            <div className="flex  w-1/3 gap-0.5 justify-around flex-col">
                                <div className="w-full bg-gray-200 rounded-full h-1 dark:bg-gray-700">
                                    <div className="bg-misc-3 h-1 rounded-full" style={{width: `${velocity * 100}%`}}></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    {tracker.info?.editable && 
                        <div className="flex flex-col flex-shrink justify-around">
                            <IconButton icon={<GearIcon/>}/>
                        </div>
                    }
                </div>
            </div>
        </TrackerSettings>
    )

}