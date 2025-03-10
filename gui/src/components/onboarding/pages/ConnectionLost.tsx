import { Button } from '@/components/commons/Button';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { Typography } from '@/components/commons/Typography';
import { EmptyLayout } from '@/components/EmptyLayout';
import { useConfig } from '@/hooks/config';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { error } from '@/utils/logging';
import { Localized } from '@fluent/react';
import { invoke } from '@tauri-apps/api/core';
import { getCurrentWindow } from '@tauri-apps/api/window';

export function ConnectionLost() {
  const { isFirstConnection } = useWebsocketAPI();
  const { saveConfig } = useConfig();

  const openLogsFolder = async () => {
    try {
      await invoke<string | null>('open_logs_folder');
    } catch (err) {
      error('Failed to open logs folder:', err);
    }
  };

  const closeApp = async () => {
    await saveConfig();
    await invoke('update_window_state');
    await getCurrentWindow().destroy();
  };

  return (
    <EmptyLayout>
      <div className="flex w-full h-full justify-center items-center p-4">
        <div className="flex flex-col items-center gap-4 -mt-12">
          {isFirstConnection && (
            <>
              <LoaderIcon slimeState={SlimeState.JUMPY} size={200}></LoaderIcon>
              <div>
                <Localized id="websocket-connecting">
                  <Typography variant="main-title"></Typography>
                </Localized>
              </div>
            </>
          )}
          {!isFirstConnection && (
            <>
              <LoaderIcon slimeState={SlimeState.SAD} size={200}></LoaderIcon>
              <div>
                <Localized id="websocket-connection_lost">
                  <Typography variant="main-title"></Typography>
                </Localized>
                <Localized id="websocket-connection_lost-desc">
                  <Typography variant="standard"></Typography>
                </Localized>
                <div className="flex gap-2 justify-center mt-4">
                  <Localized id="websocket-connection_lost-close">
                    <Button variant="primary" onClick={closeApp}></Button>
                  </Localized>
                  <Localized id="websocket-connection_lost-logs">
                    <Button
                      variant="secondary"
                      onClick={openLogsFolder}
                    ></Button>
                  </Localized>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </EmptyLayout>
  );
}
