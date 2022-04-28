import { useProvideWebsocketApi, useWebsocketAPI, WebSocketApiContext } from './hooks/websocket-api';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Outlet,
} from "react-router-dom";
import { Overview } from './components/Overview';
import { BodyProportions } from './components/proportions/BodyProportions';
import { AppContextProvider } from './components/providers/AppContext';
import { useEffect } from 'react';
import { DataFeedConfigT, DataFeedMessage, DeviceDataMaskT, StartDataFeedT, TrackerDataMaskT } from 'solarxr-protocol';
import { MainLayoutRoute } from './components/MainLayout';
import { SettingsLayoutRoute } from './components/settings/SettingsLayout';
import { TrackersSettings } from './components/settings/pages/TrackersSettings';
import { Navbar } from './components/Navbar';
import { Serial } from './components/settings/pages/Serial';

import { listen } from '@tauri-apps/api/event'
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
              <Outlet></Outlet>
            </SettingsLayoutRoute>
        }>
          <Route path="trackers" element={<TrackersSettings />} />
          <Route path="serial" element={<Serial />} />
        </Route>
        <Route path="*" element={<Navbar></Navbar>}></Route>
      </Routes>
    </>
  )
}


function App() {
  const websocketAPI = useProvideWebsocketApi();

  useEffect(() => {
    const unlisten = listen("server-stdio", (event: Event<[string, string]>) => {
      let [event_type, s] = event.payload;
      if ("stderr" === event_type) {
        // This strange invocation is what lets us lose the line information in the console
        // See more here: https://stackoverflow.com/a/48994308
        setTimeout(console.log.bind(console, `%c[SERVER] %c${s}`, "color:cyan", "color:red"));
      } else if (event_type === "stdout") {
        setTimeout(console.log.bind(console, `%c[SERVER] %c${s}`, "color:cyan", "color:green"));
      } else if (event_type === "error") {
        console.error("Error: %s", s)
      } else if (event_type === "terminated") {
        console.error("Server Process Terminated: %s", s);
      } else if (event_type === "other") {
        console.log("Other process event: %s", s);
      }
      return async () => {
        await unlisten
      }
    });
  }, [])

  return (
    <WebSocketApiContext.Provider value={websocketAPI}>
      <AppContextProvider>
        <Router>
          <div className='bg-primary-1 h-full w-full overflow-hidden'>
            <div className='flex-col h-full'>
              {!websocketAPI.isConnected && (
                <>
                  <Navbar></Navbar>
                  <div className='flex w-full h-full justify-center items-center text-white p-2'>Connection lost to server</div>
                </>
              )}
              {websocketAPI.isConnected && <Layout></Layout>}
            </div>
          </div>
        </Router>
      </AppContextProvider>
    </WebSocketApiContext.Provider>
  );
}

export default App;
