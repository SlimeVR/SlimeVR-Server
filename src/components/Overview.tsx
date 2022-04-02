import { useMemo, useState } from "react";
import { OutboundUnion, TrackerPosition } from "slimevr-protocol/dist/server";
import { DeviceStatusT } from "slimevr-protocol/dist/slimevr-protocol/server/device-status";
import { TrackersListT } from "slimevr-protocol/dist/slimevr-protocol/server/trackers-list";
import { useWebsocketAPI } from "../hooks/websocket-api";
import { TrackerCard } from "./tracker/TrackerCard";


export function Overview() {
  
    const { usePacket } = useWebsocketAPI();
    const [list, setTrackersList] = useState<DeviceStatusT[]>([]);

    usePacket(OutboundUnion.TrackersList, (packet: TrackersListT) => {
        setTrackersList(packet.trackers)
    })

    const unasignedTrackers = useMemo(() => list.filter(({ editable, mountingPosition }) => editable && mountingPosition === TrackerPosition.NONE), [list]);

    return (
        <div className="overflow-y-auto flex flex-col gap-8">
            <div className="flex text-white text-2xl px-8 pt-8  font-bold">
                Tracker Overview
            </div>
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-5 sm:grid-cols-1 px-8">
                {list.filter(({ editable, mountingPosition }) => editable && mountingPosition !== TrackerPosition.NONE).map((trackerStatus, index) => <TrackerCard key={index} status={trackerStatus}/>)}
                {/* {list.filter(({ computed }) => computed).map((trackerStatus, index) => <TrackerCard key={index} status={trackerStatus}/>)} */}
            </div>
            {unasignedTrackers.length > 0 &&
                <>
                    <div className="flex text-white text-2xl px-8 pt-8  font-bold">
                        Unassigned Trackers
                    </div>
                    <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-5 sm:grid-cols-1 px-8">
                        {unasignedTrackers.map((trackerStatus, index) => <TrackerCard key={index} status={trackerStatus}/>)}
                    </div>
                </>

            }
        </div>
        
    )
}