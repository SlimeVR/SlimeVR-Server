import classnames from 'classnames';
import { ReactChild } from 'react';
import {
    useMatch,
    NavLink,
} from "react-router-dom";
import { CubeIcon } from './commons/icon/CubeIcon';
import { GearIcon } from './commons/icon/GearIcon';
import { SlimeVRIcon } from './commons/icon/SimevrIcon';


export function NavButton({ to, children, match, icon }: { to: string, children: ReactChild, match?: string, icon: ReactChild }) {

    const doesMatch = useMatch({
        path: match || to,
    });

    return (
        <NavLink to={to} className={classnames("flex flex-grow flex-row gap-3 py-3 px-8 rounded-t-md  group ", { 'bg-primary-2': doesMatch, 'hover:bg-primary-3': !doesMatch })}>
            <div className="flex align-middle justify-center justify-items-center flex-col">
                <div className={classnames("fill-primary-3 group-hover:fill-white", { 'fill-misc-3': doesMatch })}>{icon}</div>
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
                    <div className="flex text-white text-xl justify-around flex-col font-bold">SlimeVR</div>
                </div>
            </div>
            <div className="flex px-5 gap-2 pt-2">
                <NavButton to="/" icon={<CubeIcon></CubeIcon>}>Overview</NavButton>
                <NavButton to="/proportions" icon={<GearIcon></GearIcon>}>Body proportions</NavButton>
                <NavButton to="/settings" icon={<GearIcon></GearIcon>}>Settings</NavButton>
            </div>
        </div>
    )
}