import { Navbar } from './components/Navbar';
import { useProvideWebsocketApi, useWebsocketAPI, WebSocketApiContext } from './hooks/websocket-api';
import {
  BrowserRouter as Router,
  Routes,
  Route,
} from "react-router-dom";
import { Overview } from './components/Overview';
import { BigButton } from './components/commons/BigButton';
import { QuickResetIcon, ResetIcon } from './components/commons/icon/ResetIcon';
import { useReset } from './hooks/reset';
import { Button } from './components/commons/Button';
import { useLayout } from './hooks/layout';
import { BodyProportions } from './components/proportions/BodyProportions';
import { BVHButton } from './components/BVHButton';
import { AppContextProvider } from './components/providers/AppContext';
import { useEffect } from 'react';
import { DataFeedConfigT, DataFeedMessage, DeviceDataMaskT, ResetType, StartDataFeedT, TrackerDataMaskT } from 'slimevr-protocol';
import { Settings } from './components/Settings';


function Layout() {
  const { layoutHeight, ref } = useLayout();
  const { reset, timer, reseting } = useReset()

  const { sendDataFeedPacket } = useWebsocketAPI();

  useEffect(() => {
    const trackerData = new TrackerDataMaskT();
    trackerData.position = true;
    trackerData.rotation = true;
    trackerData.info = true;
    trackerData.status = true;
    trackerData.temp = true;

    const dataMask = new DeviceDataMaskT();
    dataMask.deviceData = true;
    dataMask.trackerData = trackerData;

    const config = new DataFeedConfigT();
    config.dataMask = dataMask;
    config.minimumTimeSinceLast = 100;
    config.syntheticTrackersMask = trackerData

    const startDataFeed = new StartDataFeedT() 
    startDataFeed.dataFeeds = [config]
    sendDataFeedPacket(DataFeedMessage.StartDataFeed, startDataFeed)
  }, [])



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
          <div className="flex flex-col px-8 w-60  gap-8 pb-5 overflow-y-auto">
            <div className='flex'>
              <BigButton text={"Fast reset"} icon={<QuickResetIcon/>} onClick={() => reset(ResetType.Quick)} ></BigButton>
            </div>
            <div className='flex'>
              <BigButton text={!reseting ? "Reset" : `${3 - timer}`} icon={<ResetIcon />} onClick={() => reset(ResetType.Full)} disabled={reseting}></BigButton>
            </div>
            <div className='flex'>
              <BVHButton></BVHButton>
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
      <AppContextProvider>
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
      </AppContextProvider>
    </WebSocketApiContext.Provider>
  );
}

export default App;
