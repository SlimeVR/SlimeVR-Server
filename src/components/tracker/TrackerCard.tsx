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
            [TrackerStatus.NONE]: 'bg-purple-gray-900',
            [TrackerStatus.BUSY]: 'bg-status-warning',
            [TrackerStatus.ERROR]: 'bg-status-error',
            [TrackerStatus.DISCONNECTED]: 'bg-purple-gray-900',
            [TrackerStatus.OCCLUDED]: 'bg-status-warning',
            [TrackerStatus.OK]: 'bg-status-online'
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
                <div className="flex rounded-r-md py-3 ml-[5px] pr-4 pl-4 w-full gap-4 bg-purple-gray-700">
                    <div className="flex flex-grow flex-col truncate gap-2">
                        <div className="flex text-field-title">{trackerName}</div>
                        <div className="flex flex-row gap-4 text-default">
                            {device && device.hardwareStatus &&
                                <>
                                    <div className="flex gap-2 flex-grow">
                                        {device.hardwareStatus.rssi && <div className="flex flex-col justify-around">
                                            <WifiIcon value={device.hardwareStatus?.rssi} />
                                        </div>}
                                        {device.hardwareStatus.ping && <div className="flex w-10">{device.hardwareStatus.ping} ms</div>}
                                    </div>
                                    {device.hardwareStatus.batteryPctEstimate &&
                                        <div className="flex w-1/3 gap-2">
                                            <div className="flex flex-col justify-around">
                                                <BatteryIcon value={device.hardwareStatus.batteryPctEstimate / 100}/>
                                            </div>
                                            <div className="flex">{((device.hardwareStatus.batteryPctEstimate)).toFixed(0)} %</div>
                                        </div>
                                    }
                                </>
                            }
                            <div className="flex w-1/3 gap-0.5 justify-around flex-col">
                                <div className="w-full rounded-full h-1 bg-purple-gray-600">
                                    <div className="h-1 rounded-full bg-accent-darker" style={{width: `${velocity * 100}%`}}></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    {tracker.info?.editable && 
                        <div className="flex flex-col flex-shrink justify-around">
                            <IconButton className="fill-purple-gray-300" icon={<GearIcon/>}/>
                        </div>
                    }
                </div>
            </div>
        </TrackerSettings>
    )

}