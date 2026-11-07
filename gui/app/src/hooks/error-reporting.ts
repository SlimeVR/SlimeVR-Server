import { useEffect, useState } from 'react';
import { atom, useAtom, useAtomValue } from 'jotai';
import {
  ChangeErrorReportingSettingsRequestT,
  ErrorReportingConsent,
  ErrorReportingSettingsRequestT,
  ErrorReportingSettingsResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { CrashReportKind, ErrorReportingState } from '@slimevr/gui-shared';
import { useWebsocketAPI } from './websocket-api';
import { applyErrorReporting } from '@/utils/sentry';

export type ErrorReportingAnswer =
  | ErrorReportingConsent.ALLOWED
  | ErrorReportingConsent.DENIED;

/** What the server said */
export const serverErrorReportingAtom = atom<ErrorReportingState | null>(null);
/** What the user chose, until the server confirms it */
const pendingAnswerAtom = atom<ErrorReportingAnswer | null>(null);

export const sessionConsentAtom = atom(
  (get) => get(serverErrorReportingAtom)?.consent ?? ErrorReportingConsent.UNDECIDED
);

export function useProvideErrorReporting() {
  const { useRPCPacket, sendRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const [server, setServer] = useAtom(serverErrorReportingAtom);
  const [pending, setPending] = useAtom(pendingAnswerAtom);
  const consent = useAtomValue(sessionConsentAtom);

  useConnectedEffect(() => {
    sendRPCPacket(
      RpcMessage.ErrorReportingSettingsRequest,
      new ErrorReportingSettingsRequestT()
    );
  }, []);

  useRPCPacket(
    RpcMessage.ErrorReportingSettingsResponse,
    (res: ErrorReportingSettingsResponseT) =>
      setServer({
        consent: res.consent,
        userId: res.userId?.toString() ?? '',
        sessionId: res.sessionId?.toString() ?? '',
      })
  );

  useConnectedEffect(() => {
    if (!server || pending === null) return;
    if (server.consent === pending) {
      setPending(null);
      return;
    }
    sendRPCPacket(
      RpcMessage.ChangeErrorReportingSettingsRequest,
      new ChangeErrorReportingSettingsRequestT(pending)
    );
  }, [server, pending]);

  useEffect(() => {
    const identity = server
      ? { userId: server.userId, sessionId: server.sessionId }
      : null;
    applyErrorReporting(consent, identity);
  }, [consent, server?.userId, server?.sessionId]);
}

export function useErrorReporting() {
  const { isConnected } = useWebsocketAPI();
  const server = useAtomValue(serverErrorReportingAtom);
  const [pending, setPending] = useAtom(pendingAnswerAtom);

  return {
    setting: pending ?? server?.consent ?? null,
    needsAnswer:
      isConnected &&
      server?.consent === ErrorReportingConsent.UNDECIDED &&
      pending === null,
    answer: (consent: ErrorReportingAnswer) => setPending(consent),
  };
}

export function useCrashReport(kind: CrashReportKind) {
  const consent = useAtomValue(sessionConsentAtom);
  const [sent, setSent] = useState(false);
  // Only Electron main has the server crash data
  const isElectron = !!window.electronAPI;
  const alreadyReported = consent === ErrorReportingConsent.ALLOWED;

  return {
    available: isElectron && !alreadyReported,
    sent,
    send: async () => {
      if (await window.electronAPI.sendCrashReport(kind)) setSent(true);
    },
  };
}
