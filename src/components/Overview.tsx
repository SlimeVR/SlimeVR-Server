import { useMemo, useState } from "react";
import { BodyPart, DataFeedMessage, DataFeedUpdateT, DeviceDataT, TrackerDataT } from "solarxr-protocol";
import { useWebsocketAPI } from "../hooks/websocket-api";
import { TrackerCard } from "./tracker/TrackerCard";
// import { TrackerCard } from "./tracker/TrackerCard";

interface FlatDeviceTracker {
    device?: DeviceDataT;
    tracker: TrackerDataT;
}


export function Overview() {
  
    const { useDataFeedPacket } = useWebsocketAPI();
    const [list, setDevicesList] = useState<DeviceDataT[]>([]);
    const [syntheticlist, setSyntheticTrackersList] = useState<TrackerDataT[]>([]);

    useDataFeedPacket(DataFeedMessage.DataFeedUpdate, (packet: DataFeedUpdateT) => {
        setDevicesList(packet.devices)
        setSyntheticTrackersList(packet.syntheticTrackers)
    })

    const trackers = useMemo(() => list.reduce<FlatDeviceTracker[]>((curr, device) => ([...curr, ...device.trackers.map((tracker) => ({ tracker, device }))]), []), [list]);
    
    const asignedTrackers = useMemo(() => 
        trackers.filter(({ tracker: { info } }) => {
            return info && info.bodyPart !== BodyPart.NONE
        })
    , [trackers]);

    const unasignedTrackers = useMemo(() => 
        trackers.filter(({ tracker: { info } }) => {
            return info && info.bodyPart === BodyPart.NONE
        })
    , [trackers]);

    return (
        <div className="overflow-y-auto flex flex-col gap-8">
            <div className="flex px-8 pt-8 text-secondary-heading">
                Assigned Trackers
            </div>
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-5 sm:grid-cols-1 px-8">
                {asignedTrackers.map(({ tracker, device }, index) => <TrackerCard key={index} tracker={tracker} device={device}/>)}
            </div>
            {syntheticlist.length > 0 &&
                <>
                    <div className="flex px-8 pt-8 text-secondary-heading">
                        External Trackers
                    </div>
                    <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-5 sm:grid-cols-1 px-8">
                        {syntheticlist.map((tracker, index) => <TrackerCard key={index} tracker={tracker} />)}
                    </div>
                </>

            }
            {unasignedTrackers.length > 0 &&
                <>
                    <div className="flex px-8 pt-8 text-secondary-heading">
                        Unassigned Trackers
                    </div>
                    <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-5 sm:grid-cols-1 px-8">
                        {unasignedTrackers.map(({tracker, device}, index) => <TrackerCard key={index} tracker={tracker} device={device}/>)}
                    </div>
                </>

            }
        </div>
        
    )
}