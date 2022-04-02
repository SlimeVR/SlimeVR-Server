import { Navbar } from './components/Navbar';
import { useProvideWebsocketApi, WebSocketApiContext } from './hooks/websocket-api';
import {
  BrowserRouter as Router,
  Routes,
  Route,
} from "react-router-dom";
import { Overview } from './components/Overview';
import { BigButton } from './components/commons/BigButton';
import { QuickResetIcon, ResetIcon } from './components/commons/icon/ResetIcon';
import { useReset } from './hooks/reset';
import { Settings } from './components/Settings';
import { Button } from './components/commons/Button';
import { useLayout } from './hooks/layout';
import { BodyProportions } from './components/BodyProportions';


function Layout() {
  const { layoutHeight, ref } = useLayout();
  const { reset, timer, reseting } = useReset()

  return (
    <>
      <div ref={ref} className='flex-grow' style={{ height: layoutHeight }}>
        <div className="flex bg-primary-1 h-full ">
          <div className="flex flex-grow gap-10 flex-col bg-primary-2  rounded-tr-3xl">
            <Routes>
              <Route path="/" element={<Overview/>}/>
              <Route path="/proportions" element={<BodyProportions/>}/>
              <Route path="/settings" element={<Settings/>}/>
            </Routes>
          </div>
          <div className="flex flex-col px-8 w-60 gap-8 pb-5 overflow-y-auto">
            <div className='flex'>
              <BigButton text={"Fast reset"} icon={<QuickResetIcon/>} onClick={() => reset(true)} ></BigButton>
            </div>
            <div className='flex'>
              <BigButton text={!reseting ? "Reset" : `${3 - timer}`} icon={<ResetIcon />} onClick={() => reset(false)} disabled={reseting}></BigButton>
            </div>
            <div className='flex flex-grow flex-col justify-end'>
              <Button variant='primary' className='w-full'>Debug</Button>
            </div>
          </div>
        </div>
      </div>
     
    </>
  )
}


function App() {
  const websocketAPI = useProvideWebsocketApi();
  return (
    <WebSocketApiContext.Provider value={websocketAPI}>
        <Router>
          <div className='bg-primary h-full w-full overflow-hidden'>
            <div className='flex-col h-full'>
              {!websocketAPI.isConnected && <div className='flex w-full h-full justify-center items-center text-white p-2'>Connection lost to server</div>}
              {websocketAPI.isConnected &&
                <>
                  <Navbar></Navbar>
                  <Layout></Layout>
                </>
              }
            </div>
          </div>
      </Router>
    </WebSocketApiContext.Provider>
  );
}

export default App;
