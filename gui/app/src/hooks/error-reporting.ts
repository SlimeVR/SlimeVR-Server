import { useEffect } from 'react';
import { atom, useAtom, useAtomValue } from 'jotai';
import {
  ChangeErrorReportingSettingsRequestT,
  ErrorReportingConsent,
  ErrorReportingSettingsRequestT,
  ErrorReportingSettingsResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { ErrorReportingState } from '@slimevr/gui-shared';
import { useWebsocketAPI } from './websocket-api';
import { applyErrorReporting, applyErrorReportingConsent } from '@/utils/sentry';

export type ErrorReportingAnswer =
  | ErrorReportingConsent.ALLOWED
  | ErrorReportingConsent.DENIED;

interface PendingAnswer {
  consent: ErrorReportingAnswer;
  /** Only used if the server has no answer yet */
  offline: boolean;
}

export const serverErrorReportingAtom = atom<ErrorReportingState | null>(null);
const pendingAnswerAtom = atom<PendingAnswer | null>(null);

export function useProvideErrorReporting() {
  const { useRPCPacket, sendRPCPacket, isConnected } = useWebsocketAPI();
  const [server, setServer] = useAtom(serverErrorReportingAtom);
  const [pending, setPending] = useAtom(pendingAnswerAtom);

  useEffect(() => {
    if (!isConnected) return;
    sendRPCPacket(
      RpcMessage.ErrorReportingSettingsRequest,
      new ErrorReportingSettingsRequestT()
    );
  }, [isConnected]);

  useRPCPacket(
    RpcMessage.ErrorReportingSettingsResponse,
    (res: ErrorReportingSettingsResponseT) =>
      setServer({
        consent: res.consent,
        userId: res.userId?.toString() ?? '',
        sessionId: res.sessionId?.toString() ?? '',
      })
  );

  useEffect(() => {
    if (!isConnected || !server || !pending) return;
    if (server.consent === pending.consent) {
      setPending(null);
    } else if (!pending.offline || server.consent === ErrorReportingConsent.UNDECIDED) {
      sendRPCPacket(
        RpcMessage.ChangeErrorReportingSettingsRequest,
        new ChangeErrorReportingSettingsRequestT(pending.consent)
      );
    } else {
      // Offline answer, the earlier answer wins
      setPending(null);
    }
  }, [isConnected, server, pending]);

  useEffect(() => {
    if (server) applyErrorReporting(server);
  }, [server]);

  const consent = server?.consent;
  useEffect(() => {
    if (consent !== undefined) applyErrorReportingConsent(consent);
  }, [consent]);
}

export function useErrorReporting() {
  const { isConnected, isFirstConnection, timedOut } = useWebsocketAPI();
  const server = useAtomValue(serverErrorReportingAtom);
  const [pending, setPending] = useAtom(pendingAnswerAtom);

  const unanswered =
    pending === null &&
    (server === null || server.consent === ErrorReportingConsent.UNDECIDED);
  // The server can't ask when it crashed or never started
  const serverUnreachable = !isConnected && (!isFirstConnection || timedOut);

  return {
    consent: pending?.consent ?? server?.consent ?? null,
    needsAnswer: unanswered && (isConnected ? server !== null : serverUnreachable),
    answer: (consent: ErrorReportingAnswer) =>
      setPending({ consent, offline: !isConnected }),
  };
}
