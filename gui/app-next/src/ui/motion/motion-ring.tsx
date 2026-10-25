import { createEffect, createSignal, onCleanup, onMount } from 'solid-js';
import { type Direction, motionActive, motionEditing, onBump } from './core';
import { motionTuning } from './tuning';

const BUMP: Record<Direction, [number, number]> = {
  up: [0, -1],
  down: [0, 1],
  left: [-1, 0],
  right: [1, 0],
};

function reducedMotion(): boolean {
  return (
    window.matchMedia?.('(prefers-reduced-motion: reduce)').matches ?? false
  );
}

// Overlay that glides to the focused element while directional motion is in use
export function MotionRing() {
  let ring!: HTMLDivElement;
  let frame = 0;
  // Element the ring currently sits on, null while hidden
  let placedOn: HTMLElement | null = null;
  const [target, setTarget] = createSignal<HTMLElement | null>(null);

  function place() {
    cancelAnimationFrame(frame);
    frame = 0;
    const el = target();
    if (!el || !el.isConnected || !motionActive()) {
      ring.style.opacity = '0';
      placedOn = null;
      return;
    }
    const r = el.getBoundingClientRect();
    const pad = motionTuning.ringPaddingPx;
    // Glide only to a new target; appearing, scrolling and resizing snap
    const glide = placedOn !== null && placedOn !== el;
    ring.style.transitionDuration = glide
      ? `${motionTuning.ringGlideMs}ms`
      : '0ms';
    ring.style.transform = `translate(${r.left - pad}px, ${r.top - pad}px)`;
    ring.style.width = `${r.width + pad * 2}px`;
    ring.style.height = `${r.height + pad * 2}px`;
    ring.style.opacity = '1';
    placedOn = el;
  }

  function schedule() {
    if (!frame) frame = requestAnimationFrame(place);
  }

  // A `data-focus-ring` ancestor takes the ring for its inner focus target
  function ringOf(el: HTMLElement): HTMLElement {
    return el.closest<HTMLElement>('[data-focus-ring]') ?? el;
  }

  function onFocusIn(e: FocusEvent) {
    setTarget(e.target instanceof HTMLElement ? ringOf(e.target) : null);
  }

  function onFocusOut(e: FocusEvent) {
    if (!e.relatedTarget) setTarget(null);
  }

  onMount(() => {
    const active = document.activeElement;
    if (active instanceof HTMLElement && active !== document.body)
      setTarget(ringOf(active));
    document.addEventListener('focusin', onFocusIn);
    document.addEventListener('focusout', onFocusOut);
    window.addEventListener('scroll', place, true);
    window.addEventListener('resize', place);
    const stopBump = onBump((dir) => {
      if (!placedOn || reducedMotion() || !ring.animate) return;
      const [x, y] = BUMP[dir];
      const px = motionTuning.bumpPx;
      ring.animate(
        [
          { translate: '0 0' },
          { translate: `${x * px}px ${y * px}px` },
          { translate: '0 0' },
        ],
        { duration: motionTuning.bumpMs, easing: 'ease-out' }
      );
    });
    onCleanup(() => {
      document.removeEventListener('focusin', onFocusIn);
      document.removeEventListener('focusout', onFocusOut);
      window.removeEventListener('scroll', place, true);
      window.removeEventListener('resize', place);
      stopBump();
      cancelAnimationFrame(frame);
    });
  });

  createEffect(() => {
    const el = target();
    motionActive();
    schedule();
    if (!el || typeof ResizeObserver === 'undefined') return;
    const observer = new ResizeObserver(schedule);
    observer.observe(el);
    onCleanup(() => observer.disconnect());
  });

  return (
    <div
      ref={ring}
      aria-hidden="true"
      data-editing={motionEditing() ? '' : undefined}
      class="
        pointer-events-none fixed top-0 left-0 z-50 rounded-md border-2 border-focus opacity-0
        transition-[transform,width,height] ease-out motion-reduce:transition-none
        data-editing:border-4 data-editing:border-accent
      "
    />
  );
}
