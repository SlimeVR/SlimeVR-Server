import { useProvideWebsocketApi, useWebsocketAPI, WebSocketApiContext } from './hooks/websocket-api';
import {
  BrowserRouter as Router,
  Routes,
  Route,
} from "react-router-dom";
import { Overview } from './components/Overview';
import { BodyProportions } from './components/proportions/BodyProportions';
import { AppContextProvider } from './components/providers/AppContext';
import { useEffect } from 'react';
import { DataFeedConfigT, DataFeedMessage, DeviceDataMaskT, StartDataFeedT, TrackerDataMaskT } from 'solarxr-protocol';
import { Settings } from './components/settings/Settings';
import { MainLayoutRoute } from './components/MainLayout';
import { SettingsLayoutRoute } from './components/settings/SettingsLayout';

import { emit, listen } from '@tauri-apps/api/event'
import type { Event } from '@tauri-apps/api/event'

function Layout() {
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
      <Routes>
        <Route path="/" element={
            <MainLayoutRoute>
              <Overview/>
            </MainLayoutRoute>
        }/>
        <Route path="/proportions" element={
            <MainLayoutRoute>
              <BodyProportions/>
            </MainLayoutRoute>
        }/>
        <Route path="/settings" element={
            <SettingsLayoutRoute>
              <Settings/>
            </SettingsLayoutRoute>
        }/>
      </Routes>
    </>
  )
}


function App() {
  const websocketAPI = useProvideWebsocketApi();

  listen("server-stdio", (event: Event<[string, string]>) => {
    let [event_type, s] = event.payload;
    if ("stderr" == event_type) {
      // This strange invocation is what lets us lose the line information in the console
      // See more here: https://stackoverflow.com/a/48994308
      setTimeout(console.error.bind(console, s));
    } else if ("stdout" == event_type) {
      setTimeout(console.log.bind(console, s));
    } else if ("error" == event_type) {
      console.error("Error: %s", s)
    }
  });

  return (
    <WebSocketApiContext.Provider value={websocketAPI}>
      <AppContextProvider>
        <Router>
          <div className='bg-primary-1 h-full w-full overflow-hidden'>
            <div className='flex-col h-full'>
              {!websocketAPI.isConnected && <div className='flex w-full h-full justify-center items-center text-white p-2'>Connection lost to server</div>}
              {websocketAPI.isConnected && <Layout></Layout>}
            </div>
          </div>
        </Router>
      </AppContextProvider>
    </WebSocketApiContext.Provider>
  );
}

export default App;
