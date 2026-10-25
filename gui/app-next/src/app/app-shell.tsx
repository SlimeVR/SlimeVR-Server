import { createSignal, type JSX, onCleanup, Show } from 'solid-js';
import HomeIcon from '~icons/material-symbols/home';
import DesignIcon from '~icons/material-symbols/palette-outline-rounded';
import GearIcon from '~icons/material-symbols/settings-rounded';
import RulerIcon from '~icons/material-symbols/straighten-rounded';
import { ConnectionGate } from '@/app/connection-gate';
import { paths } from '@/app/routes';
import { TitleBar } from '@/app/title-bar';
import { handleBack } from '@/lib/back-stack';
import { MotionRegion } from '@/ui/motion';
import { NavLink } from '@/ui/nav-link';

const MOUSE_BACK = 3;

// Same as the `shell` breakpoint in styles.css
const RAIL_QUERY = '(width >= 50rem)';

// Arrow axis of the bar follows its layout, so it needs the value in JS
function createRailQuery() {
  const query = window.matchMedia(RAIL_QUERY);
  const [rail, setRail] = createSignal(query.matches);
  const update = () => setRail(query.matches);
  query.addEventListener('change', update);
  onCleanup(() => query.removeEventListener('change', update));
  return rail;
}

// Same destinations at every width: bottom bar on phones, rail from `shell` up
function Destinations() {
  const rail = createRailQuery();

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
          <Destinations />
          <main
            class="
              relative min-h-0 flex-1 overflow-y-auto overscroll-contain @container shell:mr-sm
              shell:mb-sm
            "
          >
            {props.children}
          </main>
        </div>
      </ConnectionGate>
    </div>
  );
}
