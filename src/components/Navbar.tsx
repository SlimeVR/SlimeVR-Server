import classnames from 'classnames';
import {
    useMatch,
    NavLink,
} from "react-router-dom";
import { CubeIcon } from './icon/CubeIcon';
import { SlimeVRIcon } from './icon/SimevrIcon';


export function NavButton({ to, children, match }: { to: string, children: React.ReactChild, match?: string }) {

    const doesMatch = useMatch({
        path: match || to,
    });

    return (
        <NavLink to={to} className={classnames("flex flex-grow flex-row gap-3 py-3 px-8 rounded-t-md  group ", { 'bg-primary-2': doesMatch, 'hover:bg-primary-3': !doesMatch })}>
            <div className="flex align-middle justify-center justify-items-center flex-col">
                <CubeIcon  className={ classnames("fill-primary-3 group-hover:fill-white", { 'fill-white': doesMatch })}></CubeIcon>
            </div>
            <div className="flex text-white text-md">{children}</div>
        </NavLink  >
    )
}


export function Navbar() {
    return (
        <div className='flex bg-primary-1 gap-2'>
            <div className="flex px-8 py-2 justify-around">
                <div className="flex flex-row gap-3">
                    <div className="flex justify-around flex-col">
                        <SlimeVRIcon></SlimeVRIcon>
                    </div>
                    <div className="flex text-white text-xl justify-around flex-col">SlimeVR</div>
                </div>
               
            </div>
            <div className="flex px-5 gap-5 pt-1">
                <NavButton to="/">Overview</NavButton>
                <NavButton to="manage">Manage Trackers</NavButton>
            </div>
        </div>
    )
}