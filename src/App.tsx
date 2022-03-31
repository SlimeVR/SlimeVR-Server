import { Navbar } from './components/Navbar';
import { useProvideWebsocketApi, WebSocketApiContext } from './hooks/websocket-api';
import {
  BrowserRouter as Router,
  Routes,
  Route,
} from "react-router-dom";
import { Overview } from './components/Overview';
import { Manage } from './components/Manage';



function App() {
  const websocketAPI = useProvideWebsocketApi();

  return (
    <WebSocketApiContext.Provider value={websocketAPI}>
        <Router>
          <div className='bg-primary h-full w-full overflow-hidden'>
            <div className='flex-col h-full'>
              <Navbar></Navbar>
              <div className='flex-grow h-full'>
                <Routes>
                  <Route  path="/" element={<Overview/>}/>
                  <Route path="manage" element={<Manage/>}/>
                </Routes>
              </div>
            </div>
          </div>
      </Router>
    </WebSocketApiContext.Provider>
  );
}

export default App;
