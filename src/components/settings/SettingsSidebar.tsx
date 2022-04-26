import { NavLink } from "react-router-dom"



export function SettingsSidebar() {
    return (
        <div className="flex flex-col px-8 w-72 gap-8 pb-5 overflow-y-auto">
            <div className="flex flex-col gap-3 pt-4 text-purple-100 font-bold">
                <div className="flex">TRACKER SETTINGS</div>
                <div className="flex flex-col gap-2 font-medium">
                    <NavLink to="/settings/trackers" state={{ scrollTo: 'steamvr' }}  className="pl-5 py-2 hover:bg-primary-5 rounded-lg">SteamVR</NavLink>
                    <NavLink to="/settings/trackers" state={{ scrollTo: 'filtering' }} className="pl-5 py-2 hover:bg-primary-5 rounded-lg">Filtering</NavLink>
                    <NavLink to="/settings/serial" className="pl-5 py-2 hover:bg-primary-5 rounded-lg">Serial</NavLink>
                </div>
            </div>
            {/* <div className="flex flex-col gap-3 pt-4 text-purple-100 font-bold">
                <div className="flex">USER INTERFACE</div>
                <div className="flex flex-col gap-2 font-medium">
                    <NavLink to="" className="pl-3 py-2 hover:bg-primary-5 rounded-lg">Widgets</NavLink>
                </div>
            </div> */}
        </div>
    )

}