import {
  Navigate,
  type RouteDefinition,
  type RouteSectionProps,
  useLocation,
} from '@solidjs/router';
import { createMemo, Show, untrack } from 'solid-js';
import { createMediaQuery, SHELL_QUERY } from '@/lib/media';
import { MotionRegion } from '@/ui/motion';
import { Page } from '@/ui/page';
import { SettingFocus, type SettingFocusTarget } from '@/ui/setting-row';
import { type SettingsLinks, SettingsMenu } from './settings-menu';
import { SettingsBase } from './settings-pane';
import { settingsPages } from './settings-pages';

export interface FocusSettingState {
  focusSetting?: string;
}

function SettingsLayout(props: RouteSectionProps & { links: SettingsLinks }) {
  // Fixed per route
  const base = untrack(() => props.links.base);
  const location = useLocation<FocusSettingState>();
  const target = createMemo((): SettingFocusTarget | undefined => {
    const key = location.state?.focusSetting;
    return key ? { key } : undefined;
  });

  return (
    <div class="flex min-h-full items-start gap-sm">
      <MotionRegion
        class="
          sticky top-0 hidden max-h-full w-60 shrink-0 overflow-y-auto rounded-lg bg-surface p-md
          surface-page shell:flex shell:flex-col
        "
      >
        <SettingsMenu links={props.links} />
      </MotionRegion>
      <div class="flex min-w-0 grow">
        <SettingsBase.Provider value={base}>
          <SettingFocus target={target()}>{props.children}</SettingFocus>
        </SettingsBase.Provider>
      </div>
    </div>
  );
}

// Phones drill in from the list, wider screens open the first page
function SettingsIndex(props: { links: SettingsLinks }) {
  const sidebar = createMediaQuery(SHELL_QUERY);
  const first = () => {
    const page = settingsPages[0]?.page;
    return page ? `${props.links.base}/${page.path}` : props.links.base;
  };

  return (
    <Show when={!sidebar()} fallback={<Navigate href={first()} />}>
      <Page title="navbar-settings">
        <SettingsMenu links={props.links} chevron />
      </Page>
    </Show>
  );
}

export function settingsRoute(links: SettingsLinks): RouteDefinition {
  return {
    path: links.base,
    component: (props: RouteSectionProps) => (
      <SettingsLayout {...props} links={links} />
    ),
    children: [
      { path: '/', component: () => <SettingsIndex links={links} /> },
      ...settingsPages.map(({ page, component }) => ({
        path: `/${page.path}`,
        component,
      })),
      { path: '/*', component: () => <Navigate href={links.base} /> },
    ],
  };
}
