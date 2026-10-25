import { createSignal, For } from 'solid-js';
import { Segmented } from '@/ui/radio-group';
import { AssetsDemo } from './assets-demo';
import { ContainersDemo } from './containers-demo';
import { IconGallery } from './icon-gallery';
import { MotionDemo } from './motion-demo';
import { OverlayDemo } from './overlay-demo';
import { PrimitivesDemo } from './primitives-demo';

// Dev only token and primitive showcase

const ROLES = ['app', 'sidebar', 'page', 'section', 'card', 'overlay'] as const;

const ROLE_CLASS: Record<(typeof ROLES)[number], string> = {
  app: 'surface-app',
  sidebar: 'surface-sidebar',
  page: 'surface-page',
  section: 'surface-section',
  card: 'surface-card',
  overlay: 'surface-overlay',
};

const THEMES = [
  'slime',
  'space',
  'midnight',
  'light',
  'fire',
  'citrus',
  'trans',
  'asexual',
  'snep',
  'fox',
] as const;

const SPACING = ['2xs', 'xs', 'sm', 'md', 'lg', 'xl', '2xl'] as const;

// Padding, since w-* named steps are container sizes
const SPACING_CLASS: Record<(typeof SPACING)[number], string> = {
  '2xs': 'pl-2xs',
  xs: 'pl-xs',
  sm: 'pl-sm',
  md: 'pl-md',
  lg: 'pl-lg',
  xl: 'pl-xl',
  '2xl': 'pl-2xl',
};

type Theme = (typeof THEMES)[number];

const [theme, setThemeSignal] = createSignal<Theme>(
  (document.documentElement.dataset.theme as Theme | undefined) ?? 'slime'
);

function setTheme(next: Theme) {
  document.documentElement.dataset.theme = next;
  setThemeSignal(next);
}

export function DesignPage() {
  return (
    <div class="surface-app flex min-h-full flex-col gap-xl p-xl">
      <Segmented
        value={theme()}
        onChange={setTheme}
        options={THEMES.map((value) => ({ value, label: value }))}
      />

      <div class="grid grid-cols-2 gap-lg md:grid-cols-3">
        <For each={ROLES}>
          {(role) => (
            <div
              class={`${ROLE_CLASS[role]} flex flex-col gap-sm rounded-lg p-lg`}
            >
              <span class="text-section">{role}</span>
              <div class="bg-surface-control hover:bg-surface-hover rounded-md p-sm">
                control / hover
              </div>
              <div class="bg-surface-well rounded-md p-sm">well</div>
            </div>
          )}
        </For>
      </div>

      <div class="surface-page flex flex-col gap-sm rounded-lg p-lg">
        <span class="text-title">title</span>
        <span class="text-section">section</span>
        <span class="text-standard">standard</span>
        <span class="text-fg-muted">muted</span>
        <span class="text-fg-disabled">disabled</span>
        <span class="font-mono">mono 0123456789</span>
        <div class="flex flex-wrap gap-sm">
          <span class="bg-accent text-on-accent hover:bg-accent-hover rounded-md px-md py-xs">
            accent
          </span>
          <span class="bg-accent-subtle text-accent-strong rounded-md px-md py-xs">
            accent subtle
          </span>
          <span class="text-success">success</span>
          <span class="text-warning">warning</span>
          <span class="text-critical">critical</span>
          <span class="text-special">special</span>
          <span class="text-assign-left">left</span>
          <span class="text-assign-right">right</span>
        </div>
        <div class="border-line border-t" />
        <div class="flex items-end gap-sm">
          <For each={SPACING}>
            {(step) => (
              <div class="flex flex-col items-center gap-2xs">
                <div class={`${SPACING_CLASS[step]} bg-accent h-lg w-0`} />
                <span>{step}</span>
              </div>
            )}
          </For>
        </div>
        <div class="flex max-w-md flex-col gap-xs">
          <span>max-w-md must stay 28rem (named spacing collision check)</span>
          <div class="bg-surface-control h-xs" />
        </div>
      </div>

      <AssetsDemo />
      <PrimitivesDemo />
      <ContainersDemo />
      <MotionDemo />
      <OverlayDemo />
      <IconGallery />
    </div>
  );
}
