import { useEffect, useState } from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useErrorReporting } from '@/hooks/error-reporting';
import { ConnectionLost } from './onboarding/pages/ConnectionLost';
import { ErrorReportingConsentPage } from './ErrorReportingConsent';

export const CONNECTING_PATH = '/connecting';
export const ERROR_REPORTING_CONSENT_PATH = '/error-reporting-consent';

const STATUS_PATHS = [CONNECTING_PATH, ERROR_REPORTING_CONSENT_PATH];

interface ReturnTo {
  path: string;
  state: unknown;
}

function StatusPage({ path }: { path: string }) {
  return path === ERROR_REPORTING_CONSENT_PATH ? (
    <ErrorReportingConsentPage />
  ) : (
    <ConnectionLost />
  );
}

export function AppStatusGate() {
  const { isConnected } = useWebsocketAPI();
  const { needsAnswer } = useErrorReporting();
  const location = useLocation();
  const [appShown, setAppShown] = useState(false);

  const statusPath = needsAnswer
    ? ERROR_REPORTING_CONSENT_PATH
    : !isConnected
      ? CONNECTING_PATH
      : null;
  const onStatusPath = STATUS_PATHS.includes(location.pathname);

  useEffect(() => {
    if (!statusPath && !onStatusPath) setAppShown(true);
  }, [statusPath, onStatusPath]);

  const returnTo: ReturnTo = onStatusPath
    ? (location.state?.returnTo ?? { path: '/', state: null })
    : { path: location.pathname + location.search, state: location.state };

  if (!appShown && statusPath && location.pathname !== statusPath) {
    return <Navigate to={statusPath} replace state={{ returnTo }} />;
  }
  if ((appShown || !statusPath) && onStatusPath) {
    return <Navigate to={returnTo.path} replace state={returnTo.state} />;
  }

  const covering = appShown ? statusPath : null;
  return (
    <>
      {covering && <StatusPage path={covering} />}
      <div className={covering ? 'hidden' : 'contents'}>
        <Outlet />
      </div>
    </>
  );
}
