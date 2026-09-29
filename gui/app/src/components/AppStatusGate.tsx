import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useErrorReporting } from '@/hooks/error-reporting';

export const CONNECTING_PATH = '/connecting';
export const ERROR_REPORTING_CONSENT_PATH = '/error-reporting-consent';

const STATUS_PATHS = [CONNECTING_PATH, ERROR_REPORTING_CONSENT_PATH];

export function AppStatusGate() {
  const { isConnected } = useWebsocketAPI();
  const { needsAnswer } = useErrorReporting();
  const location = useLocation();

  const statusPath = needsAnswer
    ? ERROR_REPORTING_CONSENT_PATH
    : !isConnected
      ? CONNECTING_PATH
      : null;
  const onStatusPath = STATUS_PATHS.includes(location.pathname);
  const returnTo: string = onStatusPath
    ? (location.state?.returnTo ?? '/')
    : location.pathname + location.search;

  if (statusPath && location.pathname !== statusPath) {
    return <Navigate to={statusPath} replace state={{ returnTo }} />;
  }
  if (!statusPath && onStatusPath) {
    return <Navigate to={returnTo} replace />;
  }
  return <Outlet />;
}
