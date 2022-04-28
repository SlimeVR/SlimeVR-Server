import { ReactChild } from "react";
import { NavLink } from "react-router-dom";
import { ResetType } from "solarxr-protocol";
import { useLayout } from "../hooks/layout";
import { BVHButton } from "./BVHButton";
import { Button } from "./commons/Button";
import { CubeIcon } from "./commons/icon/CubeIcon";
import { GearIcon } from "./commons/icon/GearIcon";
import { Navbar, NavButton } from "./Navbar";
import { ResetButton } from "./ResetButton";


export function MainLayoutRoute({ children }: { children: ReactChild }) {

    const { layoutHeight, ref } = useLayout<HTMLDivElement>();
  
    return (
      <>
       <Navbar>
         <>
          <NavButton to="/" icon={<CubeIcon></CubeIcon>}>Overview</NavButton>
          <NavButton to="/proportions" icon={<GearIcon></GearIcon>}>Body proportions</NavButton>
         </>
       </Navbar>
        <div ref={ref} className='flex-grow' style={{ height: layoutHeight }}>
          <div className="flex h-full ">
            <div className="flex flex-grow gap-10 flex-col rounded-tr-3xl bg-purple-gray-800">
              {children}
            </div>
            <div className="flex flex-col px-8 w-60 gap-8 pb-5 overflow-y-auto">
              <div className='flex'>
                <ResetButton type={ResetType.Quick}></ResetButton>
              </div>
              <div className='flex'>
                <ResetButton type={ResetType.Full}></ResetButton>
              </div>
              <div className='flex'>
                <BVHButton></BVHButton>
              </div>
              <div className='flex flex-grow flex-col justify-end'>
                {/* <Button variant='primary' className='w-full'>Debug</Button> */}
                <NavLink to="/settings/trackers" className="flex gap-5 group cursor-pointer">
                  <div className="flex rounded-full p-2 fill-purple-gray-300 bg-purple-gray-700 group-hover:fill-white"><GearIcon></GearIcon></div>
                  <div className="flex flex-col justify-around text-section-indicator group-hover:text-purple-gray-300">Settings</div>
                </NavLink>
              </div>
            </div>
          </div>
        </div>
      </>
    )
  }