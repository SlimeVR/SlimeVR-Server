import { Button } from '@/components/commons/Button';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { Typography } from '@/components/commons/Typography';
import { EmptyLayout } from '@/components/EmptyLayout';
import { useConfig } from '@/hooks/config';
import { useElectron } from '@/hooks/electron';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useCrashReport } from '@/hooks/error-reporting';
import { CrashReportKind } from '@slimevr/gui-shared';
import { error } from '@/utils/logging';
import { Localized } from '@fluent/react';

function CrashReportBanner({ kind }: { kind: CrashReportKind }) {
  const { available, sent, send } = useCrashReport(kind);

  if (!available) return null;

  return (
    <div className="flex flex-col xs:flex-row items-center gap-3 xs:gap-4 w-full max-w-2xl bg-background-70 rounded-lg px-4 py-3 min-h-[64px]">
      <div className="flex-grow text-center xs:text-left">
        <Typography
          id={sent ? 'websocket-report-sent' : 'websocket-report-question'}
        />
      </div>
      {!sent && (
        <Button
          variant="secondary"
          className="flex-shrink-0"
          onClick={send}
          id="websocket-report-send"
        />
      )}
    </div>
  );
}

function Error({ title, desc }: { title: string; desc: string }) {
  const electron = useElectron();
  const { saveConfig } = useConfig();

  const openLogsFolder = async () => {
    if (!electron.isElectron) throw 'invalid state - electron should be here';
    try {
      electron.api.openLogsFolder();
    } catch (err) {
      error('Failed to open logs folder:', err);
    }
  };

  const closeApp = async () => {
    if (!electron.isElectron) throw 'invalid state - electron should be here';
    await saveConfig();
    electron.api.close();
  };

  return (
    <>
      <LoaderIcon slimeState={SlimeState.SAD} size={200} />
      <div className="flex flex-col items-center text-center">
        <Localized id={title}>
          <Typography variant="main-title" />
        </Localized>
        <Localized id={desc}>
          <Typography variant="standard" />
        </Localized>
        {electron.isElectron && (
          <div className="flex gap-2 justify-center mt-4">
            <Localized id="websocket-error-close">
              <Button variant="primary" onClick={closeApp} />
            </Localized>
            <Localized id="websocket-error-logs">
              <Button variant="secondary" onClick={openLogsFolder} />
            </Localized>
          </div>
        )}
      </div>
    </>
  );
}

export function ConnectionLost() {
  const { isFirstConnection, isConnected, timedOut } = useWebsocketAPI();

  // Connected but still waiting on the server's settings defaults
  const isLoading = isConnected || (isFirstConnection && !timedOut);
  const isCrashed = !isLoading && !isFirstConnection && !timedOut;
  const isTimedOut = isFirstConnection && timedOut;

  return (
    <EmptyLayout>
      <div className="flex flex-col w-full h-full p-4">
        <div className="flex flex-grow justify-center items-center">
          <div className="flex flex-col items-center gap-4 -mt-12">
            {isLoading && (
              <>
                <LoaderIcon slimeState={SlimeState.JUMPY} size={200} />
                <div>
                  <Localized id="websocket-connecting">
                    <Typography variant="main-title" />
                  </Localized>
                </div>
              </>
            )}
            {isCrashed && (
              <Error
                title="websocket-connection_lost"
                desc="websocket-connection_lost-desc"
              />
            )}
            {isTimedOut && (
              <Error
                title="websocket-timedout"
                desc="websocket-timedout-desc"
              />
            )}
          </div>
        </div>
        {(isCrashed || isTimedOut) && (
          <div className="flex justify-center">
            <CrashReportBanner
              kind={isTimedOut ? 'timeout' : 'connection_lost'}
            />
          </div>
        )}
      </div>
    </EmptyLayout>
  );
}
