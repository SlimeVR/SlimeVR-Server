import { ReactChild } from "react";
import { NavLink } from "react-router-dom";
import { useLayout } from "../../hooks/layout";
import { CloseIcon } from "../commons/icon/CloseIcon";
import { Navbar } from "../Navbar";
import { SettingsSidebar } from "./SettingsSidebar";

export function SettingsLayoutRoute({ children }: { children: ReactChild }) {

    const { layoutHeight, ref } = useLayout<HTMLDivElement>();
  
    return (
      <>
       <Navbar></Navbar>
        <div ref={ref} className='flex-grow' style={{ height: layoutHeight }}>
          <div className="flex bg-primary-1 h-full ">
            <SettingsSidebar></SettingsSidebar>
            <div className="flex flex-grow gap-10 flex-col bg-primary-2 rounded-tl-3xl overflow-hidden">
                <div className="relative overflow-y-auto overflow-x-hidden">
                    {children}
                    <div className="absolute top-0 right-0 p-5">
                        <NavLink to="/" className="flex gap-5 group cursor-pointer">
                          <div className="flex bg-primary-4 rounded-full p-2 fill-purple-300 group-hover:fill-white"><CloseIcon></CloseIcon></div>
                        </NavLink>
                    </div>
                </div>
            </div>
          </div>
        </div>
      </>
    )
  }