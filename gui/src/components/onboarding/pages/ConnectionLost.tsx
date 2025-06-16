import { Button } from '@/components/commons/Button';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { Typography } from '@/components/commons/Typography';
import { EmptyLayout } from '@/components/EmptyLayout';
import { useIsTauri } from '@/hooks/breakpoint';
import { useConfig } from '@/hooks/config';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { error } from '@/utils/logging';
import { Localized } from '@fluent/react';
import { invoke } from '@tauri-apps/api/core';
import { getCurrentWindow } from '@tauri-apps/api/window';

function Error({ title, desc }: { title: string; desc: string }) {
  const isTauri = useIsTauri();
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
    <>
      <LoaderIcon slimeState={SlimeState.SAD} size={200}></LoaderIcon>
      <div>
        <Localized id={title}>
          <Typography variant="main-title"></Typography>
        </Localized>
        <Localized id={desc}>
          <Typography variant="standard"></Typography>
        </Localized>
        {isTauri && (
          <div className="flex gap-2 justify-center mt-4">
            <Localized id="websocket-error-close">
              <Button variant="primary" onClick={closeApp}></Button>
            </Localized>
            <Localized id="websocket-error-logs">
              <Button variant="secondary" onClick={openLogsFolder}></Button>
            </Localized>
          </div>
        )}
      </div>
    </>
  );
}

export function ConnectionLost() {
  const { isFirstConnection, timedOut } = useWebsocketAPI();

  const isLoading = isFirstConnection && !timedOut;
  const isCrashed = !isFirstConnection && !timedOut;
  const isTimedOut = isFirstConnection && timedOut;
  return (
    <EmptyLayout>
      <div className="flex w-full h-full justify-center items-center p-4">
        <div className="flex flex-col items-center gap-4 -mt-12">
          {isLoading && (
            <>
              <LoaderIcon slimeState={SlimeState.JUMPY} size={200}></LoaderIcon>
              <div>
                <Localized id="websocket-connecting">
                  <Typography variant="main-title"></Typography>
                </Localized>
              </div>
            </>
          )}
          {isCrashed && (
            <Error
              title="websocket-connection_lost"
              desc="websocket-connection_lost-desc"
            ></Error>
          )}
          {isTimedOut && (
            <Error
              title="websocket-timedout"
              desc="websocket-timedout-desc"
            ></Error>
          )}
        </div>
      </div>
    </EmptyLayout>
  );
}
