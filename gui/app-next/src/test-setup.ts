import '@testing-library/jest-dom/vitest';

// jsdom has no `inert` property, so Solid's `inert={x}` would never reach the
// attribute. Browsers reflect it, do the same here.
if (!('inert' in HTMLElement.prototype)) {
  Object.defineProperty(HTMLElement.prototype, 'inert', {
    configurable: true,
    get(this: HTMLElement) {
      return this.hasAttribute('inert');
    },
    set(this: HTMLElement, value: boolean) {
      this.toggleAttribute('inert', !!value);
    },
  });
}

// jsdom logs "Not implemented" for these. The router scrolls on navigation,
// axe probes canvas. A no-op scroll and a missing 2d context are enough.
window.scrollTo = () => {};
HTMLCanvasElement.prototype.getContext = () => null;
