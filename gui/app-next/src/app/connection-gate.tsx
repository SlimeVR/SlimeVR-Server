import {
  createEffect,
  createSignal,
  type JSX,
  Match,
  onMount,
  Show,
  Switch,
} from 'solid-js';
import { ErrorReportingConsent } from 'solarxr-protocol';
import {
  answerConsent,
  crashReportAvailable,
  errorReportingSettings,
  needsConsentAnswer,
  sendCrashReport,
} from '@/domain/error-reporting';
import { ErrorReportingDetails } from '@/domain/error-reporting/error-reporting-details';
import { cx } from '@/lib/cx';
import { t } from '@/lib/l10n';
import { platform, type CrashKind } from '@/lib/platform';
import { flushPrefs } from '@/lib/storage';
import { client } from '@/solarxr/client';
import { TextButton } from '@/ui/button';
import { focusEntry, MotionRegion } from '@/ui/motion';
import { StatusState } from '@/ui/status-state';

type GateScreen = 'connection' | 'consent';

function CrashReportBanner(props: { kind: CrashKind }) {
  const [sent, setSent] = createSignal(false);
  const [sending, setSending] = createSignal(false);

  async function send() {
    setSending(true);
    try {
      if (await sendCrashReport(props.kind)) setSent(true);
    } finally {
      setSending(false);
    }
  }

  return (
    <Show when={crashReportAvailable()}>
      <div
        class="
          flex w-full max-w-2xl flex-wrap items-center justify-between gap-md rounded-lg bg-surface
          px-lg py-md surface-section
        "
      >
        <p>
          {t(sent() ? 'websocket-report-sent' : 'websocket-report-question')}
        </p>
        <Show when={!sent()}>
          <TextButton
            label="websocket-report-send"
            loading={sending()}
            onClick={send}
          />
        </Show>
      </div>
    </Show>
  );
}

function ConnectionScreen() {
  const timedOut = () => client.status() === 'connecting' && client.timedOut();
  const lost = () => client.status() === 'disconnected';

  async function close() {
    await flushPrefs();
    platform.window?.close();
  }

  return (
    <Show
      when={timedOut() || lost()}
      fallback={
        <StatusState kind="loading" size="page" title="websocket-connecting" />
      }
    >
      <div class="flex flex-1 flex-col items-center gap-lg p-lg">
        <StatusState
          kind="error"
          size="page"
          title={
            timedOut() ? 'websocket-timedout' : 'websocket-connection_lost'
          }
          description={
            timedOut()
              ? 'websocket-timedout-desc'
              : 'websocket-connection_lost-desc'
          }
          action={
            <Show when={platform.window}>
              <div class="flex flex-wrap justify-center gap-sm">
                <TextButton
                  variant="primary"
                  label="websocket-error-close"
                  onClick={close}
                />
                <Show when={platform.folders}>
                  {(folders) => (
                    <TextButton
                      label="websocket-error-logs"
                      onClick={() => void folders().openLogs()}
                    />
                  )}
                </Show>
              </div>
            </Show>
          }
        />
        <CrashReportBanner kind={timedOut() ? 'timeout' : 'connection_lost'} />
      </div>
    </Show>
  );
}

function ConsentScreen() {
  let el!: HTMLDivElement;
  onMount(() => focusEntry(el));

  return (
    <div
      ref={el}
      class="flex min-h-0 flex-1 justify-center overflow-y-auto p-lg"
    >
      <MotionRegion class="flex w-full max-w-2xl flex-col items-center gap-lg">
        <StatusState
          kind="empty"
          title="error_collection_modal-title"
          description="error_collection_modal-subtitle"
        />
        <ErrorReportingDetails />
        <p class="text-center">{t('error_collection_modal-change_later')}</p>
        <div class="flex w-full flex-wrap-reverse justify-between gap-md">
          <TextButton
            variant="ghost"
            label="error_collection_modal-cancel"
            onClick={() => answerConsent(ErrorReportingConsent.DENIED)}
          />
          <TextButton
            variant="primary"
            label="error_collection_modal-confirm"
            onClick={() => answerConsent(ErrorReportingConsent.ALLOWED)}
          />
        </div>
      </MotionRegion>
    </div>
  );
}

export function ConnectionGate(props: { children: JSX.Element }) {
  const [shown, setShown] = createSignal(false);

  function screen(): GateScreen | null {
    if (client.status() !== 'connected') return 'connection';
    if (!shown() && errorReportingSettings.state() !== 'fresh')
      return 'connection';
    if (needsConsentAnswer()) return 'consent';
    return null;
  }

  createEffect(() => {
    if (!screen()) setShown(true);
  });

  return (
    <>
      <Switch>
        <Match when={screen() === 'connection'}>
          <ConnectionScreen />
        </Match>
        <Match when={screen() === 'consent'}>
          <ConsentScreen />
        </Match>
      </Switch>
      <Show when={shown()}>
        <div
          class={cx('min-h-0 flex-1 flex-col', screen() ? 'hidden' : 'flex')}
        >
          {props.children}
        </div>
      </Show>
    </>
  );
}
