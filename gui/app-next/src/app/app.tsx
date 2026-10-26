import { HashRouter, type RouteSectionProps } from '@solidjs/router';
import { withSentryRouterRouting } from '@sentry/solid/solidrouter';
import { AppShell } from '@/app/app-shell';
import { CrashBoundary } from '@/app/crash-screen';
import { useHistoryBack } from '@/app/history-back';
import { paths, routes } from '@/app/routes';
import { searchDestinations } from '@/app/search-destinations';
import { useShortcut } from '@/lib/keyboard';
import { DOCS_SITE } from '@/lib/links';
import { platform } from '@/lib/platform';
import { QuickSearch } from '@/pages/settings/quick-search/quick-search';
import { DialogHost } from '@/ui/dialog';
import { MotionRoot } from '@/ui/motion';
import { ToastHost } from '@/ui/toast';

const Router = withSentryRouterRouting(HashRouter);

function AppRoot(props: RouteSectionProps) {
  useHistoryBack();
  useShortcut('F1', () => platform.openUrl(DOCS_SITE), {
    scope: 'global',
    label: 'titlebar-docs',
  });

  return (
    <MotionRoot>
      <AppShell>{props.children}</AppShell>
      <QuickSearch base={paths.settings} destinations={searchDestinations} />
      <DialogHost />
      <ToastHost />
    </MotionRoot>
  );
}

export function App() {
  return (
    <CrashBoundary>
      <Router root={AppRoot}>{routes}</Router>
    </CrashBoundary>
  );
}
