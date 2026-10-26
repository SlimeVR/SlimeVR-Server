import type { RouteDefinition } from '@solidjs/router';
import { BodyPage } from '@/pages/body/body-page';
import { DesignPage } from '@/pages/design/design-page';
import { HomePage } from '@/pages/home/home-page';
import { onboardingRoute } from '@/pages/onboarding/onboarding-route';
import { settingsRoute } from '@/pages/settings/settings-route';
import { SetupPage } from '@/pages/setup/setup-page';

export const paths = {
  home: '/',
  body: '/body',
  setup: '/setup',
  onboarding: '/onboarding',
  settings: '/settings',
  design: '/design',
} as const;

export const routes: RouteDefinition[] = [
  { path: paths.home, component: HomePage },
  { path: paths.body, component: BodyPage },
  { path: paths.setup, component: SetupPage },
  onboardingRoute(paths.onboarding),
  settingsRoute({ base: paths.settings, wizard: paths.onboarding }),
  ...(import.meta.env.DEV ? [{ path: paths.design, component: DesignPage }] : []),
];
