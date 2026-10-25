import { createSignal, type JSX, Show } from 'solid-js';
import { Portal } from 'solid-js/web';

const [layers, setLayers] = createSignal<readonly HTMLElement[]>([]);

// Open dialogs and popovers, topmost last
export function pushLayer(el: HTMLElement): () => void {
  setLayers((list) => [...list, el]);
  return () => setLayers((list) => list.filter((l) => l !== el));
}

// Viewport-fixed overlays (focus ring, toasts). The top layer covers anything
// outside it, so they render inside the topmost open layer, else in body.
// Remounts when that layer changes. Layers must not be transformed, or fixed
// children position against them.
export function Floating(props: { children: JSX.Element }) {
  return (
    <Show when={layers().at(-1) ?? document.body} keyed>
      {(mount) => <Portal mount={mount}>{props.children}</Portal>}
    </Show>
  );
}
