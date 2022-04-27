import classnames from 'classnames';
import { ReactChild } from 'react';
import {
    useMatch,
    NavLink,
} from "react-router-dom";
import { CubeIcon } from './commons/icon/CubeIcon';
import { GearIcon } from './commons/icon/GearIcon';
import { SlimeVRIcon } from './commons/icon/SimevrIcon';
import { appWindow } from '@tauri-apps/api/window'
import { MinimiseIcon } from './commons/icon/MinimiseIcon';
import { MaximiseIcon } from './commons/icon/MaximiseIcon';
import { CloseIcon } from './commons/icon/CloseIcon';
import { useWebsocketAPI } from '../hooks/websocket-api';


export function NavButton({ to, children, match, icon }: { to: string, children: ReactChild, match?: string, icon: ReactChild }) {

    const doesMatch = useMatch({
        path: match || to,
    });

    return (
        <NavLink to={to} className={classnames("flex flex-grow flex-row gap-3 py-3 px-8 rounded-t-md group font-bold select-text", { 'bg-primary-2 ': doesMatch, 'hover:bg-primary-3': !doesMatch })}>
            <div className="flex align-middle justify-center justify-items-center flex-col">
                <div className={classnames("fill-primary-3 group-hover:fill-white", { 'fill-misc-3': doesMatch })}>{icon}</div>
            </div>
            <div className={classnames("flex text-md ", { 'text-white': doesMatch, 'text-gray-400': !doesMatch })}>{children}</div>
        </NavLink>
    )
}


export function Navbar({ children }: { children?: ReactChild }) {
    return (
        <div data-tauri-drag-region className='flex bg-primary-1 gap-2'>
            <div className="flex px-8 py-2 pt-3 justify-around" data-tauri-drag-region>
                <div className="flex flex-row gap-3" data-tauri-drag-region>
                    <NavLink to="/" className="flex justify-around flex-col select-all" data-tauri-drag-region>
                        <SlimeVRIcon></SlimeVRIcon>
                    </NavLink>
                    <div className="flex text-white text-xl justify-around flex-col font-bold" data-tauri-drag-region>SlimeVR</div>
                </div>
            </div>
            {children && <div className="flex px-5 gap-2 pt-2">
                {children}
            </div>}
            <div className="flex flex-grow justify-end px-2 gap-2" data-tauri-drag-region>
                <div className='flex flex-col justify-around text-white' onClick={() => appWindow.minimize()}>
                    <MinimiseIcon className="hover:bg-primary-5 rounded-full"></MinimiseIcon>
                </div>
                <div className='flex flex-col justify-around text-white' onClick={() => appWindow.toggleMaximize()}>
                    <MaximiseIcon className="hover:bg-primary-5 rounded-full"></MaximiseIcon>
                </div>
                <div className='flex flex-col justify-around text-white' onClick={() => appWindow.close()}>
                    <CloseIcon className="hover:bg-primary-5 rounded-full"></CloseIcon>
                </div>
            </div>
        </div>
    )
}