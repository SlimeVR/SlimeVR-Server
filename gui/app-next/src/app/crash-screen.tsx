import { ErrorBoundary, type JSX, Show } from 'solid-js';
import { MinimalTitleBar } from '@/app/title-bar';
import { lastReportId, reportError } from '@/domain/error-reporting';
import { t } from '@/lib/l10n';
import { TextButton } from '@/ui/button';
import { CopyButton } from '@/ui/copy-button';
import { StatusState } from '@/ui/status-state';

function CrashScreen(props: { eventId: string | undefined }) {
  return (
    <div class="flex h-screen flex-col bg-surface text-fg surface-sidebar">
      <MinimalTitleBar />
      <div class="flex min-h-0 flex-1 flex-col overflow-y-auto">
        <StatusState
          kind="error"
          size="page"
          title="crash_screen-title"
          description="crash_screen-description"
          action={
            <div class="flex flex-col items-center gap-md">
              <Show when={props.eventId}>
                {(id) => (
                  <p class="font-mono select-text">
                    {t('crash_screen-event_id', { id: id() })}
                  </p>
                )}
              </Show>
              <div class="flex flex-wrap justify-center gap-sm">
                <Show when={props.eventId}>
                  {(id) => <CopyButton text={id()} label="crash_screen-copy" />}
                </Show>
                <TextButton
                  variant="primary"
                  label="crash_screen-reload"
                  onClick={() => window.location.reload()}
                />
              </div>
            </div>
          }
        />
      </div>
    </div>
  );
}

export function CrashBoundary(props: { children: JSX.Element }) {
  return (
    <ErrorBoundary
      fallback={(error) => {
        reportError(error, { tags: { boundary: 'app' } });
        return <CrashScreen eventId={lastReportId()} />;
      }}
    >
      {props.children}
    </ErrorBoundary>
  );
}
