import { useState } from "react";
import { OutboundUnion } from "slimevr-protocol/dist/server";
import { DeviceStatusT } from "slimevr-protocol/dist/slimevr-protocol/server/device-status";
import { TrackersListT } from "slimevr-protocol/dist/slimevr-protocol/server/trackers-list";
import { useWebsocketAPI } from "../hooks/websocket-api";
import { BigButton } from "./BigButton";
import { QuickResetIcon, ResetIcon } from "./icon/ResetIcon";
import { TrackerCard } from "./TrackerCard";


export function Overview() {

    const { usePacket } = useWebsocketAPI();
    const [list, setTrackersList] = useState<DeviceStatusT[]>([]);


    usePacket(OutboundUnion.TrackersList, (packet: TrackersListT) => {
        setTrackersList(packet.trackers)
    })

    return (
        <div className="flex bg-primary-1 h-full">
            <div className="flex flex-grow gap-10 flex-col bg-primary-2  rounded-tr-3xl">
                <div className="flex text-white text-2xl px-8 pt-8">
                    Tracker Overview
                </div>
                <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-5 sm:grid-cols-1 overflow-y-auto px-8">
                    {list.map((trackerStatus, index) => <TrackerCard key={index} status={trackerStatus}/>)}
                </div>
            </div>
            <div className="flex flex-col px-8 xs:w-1/4 sm:w-1/4 gap-8">
                <BigButton text="Quick reset" icon={<QuickResetIcon/>}></BigButton>
                <BigButton text="Reset Position" icon={<ResetIcon />}></BigButton>
            </div>
        </div>
    )
}