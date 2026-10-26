import { useMatch } from '@solidjs/router';
import { type JSX, Show } from 'solid-js';
import HomeIcon from '~icons/material-symbols/home';
import DesignIcon from '~icons/material-symbols/palette-outline-rounded';
import GearIcon from '~icons/material-symbols/settings-rounded';
import RulerIcon from '~icons/material-symbols/straighten-rounded';
import { ConnectionGate } from '@/app/connection-gate';
import { paths } from '@/app/routes';
import { TitleBar } from '@/app/title-bar';
import { handleBack } from '@/lib/back-stack';
import { createMediaQuery, SHELL_QUERY } from '@/lib/media';
import { MotionRegion } from '@/ui/motion';
import { NavLink } from '@/ui/nav-link';

const MOUSE_BACK = 3;

// Same destinations at every width: bottom bar on phones, rail from `shell` up
function Destinations() {
  const rail = createMediaQuery(SHELL_QUERY);

  return (
    <MotionRegion
      orientation={rail() ? 'vertical' : 'horizontal'}
      class="
        order-last flex justify-around gap-sm px-sm pt-sm shell:order-first shell:flex-col
        shell:w-27.5 shell:shrink-0 shell:justify-start shell:pt-0 shell:pb-sm
      "
    >
      <NavLink to={paths.home} end label="navbar-home" icon={<HomeIcon />} />
      <NavLink
        to={paths.body}
        label="navbar-body_proportions"
        icon={<RulerIcon class="icon -rotate-45" />}
      />
      <Show when={import.meta.env.DEV}>
        <NavLink
          to={paths.design}
          label="navbar-design"
          icon={<DesignIcon />}
        />
      </Show>
      <div class="contents shell:mt-auto shell:flex shell:flex-col">
        <NavLink
          to={paths.settings}
          label="navbar-settings"
          icon={<GearIcon />}
        />
      </div>
    </MotionRegion>
  );
}

export function AppShell(props: { children: JSX.Element }) {
  // Setup runs without destinations, the flow footer owns the bottom
  const onboarding = useMatch(() => `${paths.onboarding}/*`);

  function onPointerUp(e: PointerEvent) {
    if (e.button !== MOUSE_BACK) return;
    if (handleBack()) e.preventDefault();
  }

  return (
    <div
      class="flex h-dvh flex-col bg-surface text-fg surface-sidebar"
      onPointerUp={onPointerUp}
    >
      <TitleBar />
      <ConnectionGate>
        <div class="flex min-h-0 flex-1 flex-col shell:flex-row">
          <Show when={!onboarding()}>
            <Destinations />
          </Show>
          <main
            classList={{ 'shell:ml-sm': !!onboarding() }}
            class="
              relative min-h-0 flex-1 overflow-y-auto overscroll-contain @container shell:mb-sm
              shell:scrollbar-gutter-stable
            "
          >
            {props.children}
          </main>
        </div>
      </ConnectionGate>
    </div>
  );
}
