import { DeviceStatusT } from "slimevr-protocol/dist/server";
import { BatteryIcon } from "./icon/BatteryIcon";
import { CircleIcon } from "./icon/CircleIcon";
import { GearIcon } from "./icon/GearIcon";
import { WifiIcon } from "./icon/WifiIcon";

export function TrackerCard({ status }:  { status: DeviceStatusT }) {
    return (
        <div className="flex rounded-l-md rounded-r-xl bg-green-400" >
            <div className="flex bg-primary-3 rounded-r-md py-3 ml-1 px-5 w-full">
                <div className="flex flex-grow flex-col">
                    <div className="flex text-white text-ellipsis">{status.name}</div>
                    <div className="flex flex-row gap-4">
                        <div className="flex gap-2">
                            <div className="flex flex-col justify-around">
                                <WifiIcon />
                            </div>
                            <div className="flex text-gray-400 text-sm">{status.ping} ms</div>
                        </div>
                        <div className="flex gap-2">
                            <div className="flex flex-col justify-around">
                                <BatteryIcon />
                            </div>
                            <div className="flex text-gray-400 text-sm">{((status.battery / 256) * 100).toFixed(0)} %</div>
                        </div>
                    </div>
                </div>
                <div className="flex flex-col justify-around">
                    <GearIcon />
                </div>
            </div>
        </div>
    )

}