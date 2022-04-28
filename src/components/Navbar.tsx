import classnames from 'classnames';
import { ReactChild } from 'react';
import {
    useMatch,
    NavLink,
} from "react-router-dom";
import { SlimeVRIcon } from './commons/icon/SimevrIcon';
import { appWindow } from '@tauri-apps/api/window'
import { MinimiseIcon } from './commons/icon/MinimiseIcon';
import { MaximiseIcon } from './commons/icon/MaximiseIcon';
import { CloseIcon } from './commons/icon/CloseIcon';

export function NavButton({ to, children, match, icon }: { to: string, children: ReactChild, match?: string, icon: ReactChild }) {

    const doesMatch = useMatch({
        path: match || to,
    });

    return (
        <NavLink to={to} className={classnames("flex flex-grow flex-row gap-3 py-3 px-8 rounded-t-md group select-text text-emphasised", { 'bg-purple-gray-800 ': doesMatch, 'hover:bg-purple-gray-600': !doesMatch })}>
            <div className="flex align-middle justify-center justify-items-center flex-col">
                <div className={classnames("group-hover:fill-accent-lighter ", { 'fill-accent-lighter': doesMatch, 'fill-purple-gray-600': !doesMatch })}>{icon}</div>
            </div>
            <div className={classnames("flex", { 'text-purple-gray-100': doesMatch, 'text-purple-gray-300': !doesMatch })}>{children}</div>
        </NavLink>
    )
}


export function Navbar({ children }: { children?: ReactChild }) {
    return (
        <div data-tauri-drag-region className='flex gap-2 min-h-[56px]'>
            <div className="flex px-8 py-2 pt-3 justify-around" data-tauri-drag-region>
                <div className="flex flex-row gap-3" data-tauri-drag-region>
                    <NavLink to="/" className="flex justify-around flex-col select-all" data-tauri-drag-region>
                        <SlimeVRIcon></SlimeVRIcon>
                    </NavLink>
                    <div className="flex justify-around flex-col text-extra-emphasised" data-tauri-drag-region>SlimeVR</div>
                </div>
            </div>
            {children && <div className="flex px-5 gap-2 pt-2">
                {children}
            </div>}
            <div className="flex flex-grow justify-end px-2 gap-2" data-tauri-drag-region>
                <div className='flex flex-col justify-around ' onClick={() => appWindow.minimize()}>
                    <MinimiseIcon className="rounded-full hover:bg-purple-gray-700"></MinimiseIcon>
                </div>
                <div className='flex flex-col justify-around ' onClick={() => appWindow.toggleMaximize()}>
                    <MaximiseIcon className="rounded-full hover:bg-purple-gray-700"></MaximiseIcon>
                </div>
                <div className='flex flex-col justify-around ' onClick={() => appWindow.close()}>
                    <CloseIcon className="rounded-full hover:bg-purple-gray-700"></CloseIcon>
                </div>
            </div>
        </div>
    )
}