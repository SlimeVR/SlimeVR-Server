/**
 * Spatial focus navigation over the DOM: which elements can take focus, and
 * which one lies nearest in a given direction. No input-device knowledge and no
 * state, so it is driven by whatever wants to move a cursor around.
 */

export type Direction = 'up' | 'down' | 'left' | 'right';

/** Everything a user can put focus on. */
export const FOCUSABLE_SELECTOR = [
  'a[href]',
  'button',
  'input:not([type="hidden"])',
  'select',
  'textarea',
  '[tabindex]:not([tabindex="-1"])',
  '[role="button"]',
  '[role="link"]',
  '[role="menuitem"]',
  '[role="radio"]',
  '[role="tab"]',
  '[role="checkbox"]',
  '[role="switch"]',
  '[role="combobox"]',
  '[role="spinbutton"]',
  '[role="slider"]',
].join(',');

export const ARROW_KEY: Record<Direction, string> = {
  up: 'ArrowUp',
  down: 'ArrowDown',
  left: 'ArrowLeft',
  right: 'ArrowRight',
};

export function isFocusable(el: HTMLElement): boolean {
  // Natively disabled elements cannot take focus at all, so there is nothing to
  // aim at. `aria-disabled` ones can, and should: a control that is off for a
  // reason usually carries a tooltip saying why, and that is worth reaching.
  if ((el as HTMLButtonElement).disabled) return false;
  // Radios are exempt from the tabindex check: a browser reports -1 for the
  // unchecked members of a group, but the cursor still has to reach them.
  const isRadio = el.tagName === 'INPUT' && (el as HTMLInputElement).type === 'radio';
  if (el.tabIndex < 0 && !isRadio) return false;
  if (el.closest('[aria-hidden="true"], [inert]')) return false;
  if (el.hasAttribute('hidden')) return false;
  if (el.getClientRects().length === 0) return false;
  const cs = getComputedStyle(el);
  if (cs.visibility === 'hidden' || cs.visibility === 'collapse') return false;
  if (cs.display === 'contents') return false;
  return true;
}

export function collectFocusables(root: ParentNode = document): HTMLElement[] {
  return Array.from(root.querySelectorAll<HTMLElement>(FOCUSABLE_SELECTOR)).filter(
    isFocusable
  );
}

/**
 * A layout marks each of its areas `data-nav-region="page"` or `"shell"`, shell
 * meaning the furniture around the content: top bar, navbar, settings sidebar.
 *
 * Two things come from that. The cursor starts in a page region rather than in
 * the shell, and a move prefers to stay in the region it began in. Areas are
 * marked one by one because a grid places them as siblings, so there is no
 * wrapper to point at, and because the navbar and the top bar have to count as
 * different regions even though both are shell.
 */
export function pageRegions(): HTMLElement[] {
  return Array.from(document.querySelectorAll<HTMLElement>('[data-nav-region="page"]'));
}

/**
 * Visible right now, as opposed to merely present. Something scrolled above the
 * viewport reports a negative `top` and would win "topmost"; something inside a
 * collapsed panel still reports a full-size rect while clipped to nothing.
 */
function onScreen(el: HTMLElement, r: DOMRect): boolean {
  if (r.bottom <= 0 || r.right <= 0) return false;
  if (r.top >= window.innerHeight || r.left >= window.innerWidth) return false;

  for (let n = el.parentElement; n; n = n.parentElement) {
    const cs = getComputedStyle(n);
    if (cs.overflowX === 'visible' && cs.overflowY === 'visible') continue;
    const b = n.getBoundingClientRect();
    if (b.width < 1 || b.height < 1) return false;
    if (r.bottom <= b.top || r.top >= b.bottom) return false;
    if (r.right <= b.left || r.left >= b.right) return false;
  }
  return true;
}

/**
 * Where the cursor starts. `data-nav-entry` is how a page says so, because the
 * starting control is a design decision rather than something derivable.
 *
 * Without one, fall back to topmost then leftmost of the page's visible
 * controls. Geometric rather than DOM order, since a grid places its areas in
 * whatever order the template says: in the main layout the toolbar renders
 * after the content yet sits above it.
 *
 * Returns nothing when the page has no visible controls yet, meaning it is
 * still rendering.
 */
export function firstFocusable(): HTMLElement | undefined {
  const entry = document.querySelector<HTMLElement>('[data-nav-entry]');
  if (entry) {
    if (isFocusable(entry)) return entry;
    const inside = collectFocusables(entry)[0];
    if (inside) return inside;
  }

  const regions = pageRegions();
  const visible = (
    regions.length
      ? regions.flatMap((region) => collectFocusables(region))
      : collectFocusables()
  )
    .map((el) => ({ el, r: el.getBoundingClientRect() }))
    .filter(({ el, r }) => onScreen(el, r));
  if (!visible.length) return undefined;

  // The topmost control need not be the first one: a header row is usually
  // `items-center`, so a taller right-aligned action sits a few pixels above
  // the rest of its row. Rows are found by vertical overlap for that reason,
  // never by a pixel threshold, and the leftmost of the first row wins.
  const highest = visible.reduce((a, b) => (b.r.top < a.r.top ? b : a));
  const firstRow = visible.filter(
    ({ r }) => r.top < highest.r.bottom && r.bottom > highest.r.top
  );
  return firstRow.reduce((a, b) => (b.r.left < a.r.left ? b : a)).el;
}

/**
 * Best focus target from origin rect `o` moving in `dir`, or null. `from` is the
 * element the move starts at, excluded from the results.
 *
 * Candidates are ranked by region before distance: the region the move started
 * in, then any page region, then the shell. The top bar spans the window and the
 * navbar runs its full height, so on distance alone either one steals moves that
 * had a perfectly good target in the page. Every region stays reachable, just
 * only once the better-ranked ones have nothing left in the travel direction.
 */
export function pickBest(
  o: DOMRect,
  dir: Direction,
  from?: Element | null
): HTMLElement | null {
  const oCx = o.left + o.width / 2;
  const oCy = o.top + o.height / 2;
  const horizontal = dir === 'left' || dir === 'right';

  const regionOf = (el: Element) => el.closest('[data-nav-region]');
  // Null origin region means there is nothing to compare against, as when
  // resuming from a remembered rect whose element is long gone.
  const originRegion = from ? regionOf(from) : null;
  const rankOf = (el: Element) => {
    const region = regionOf(el);
    if (originRegion && region === originRegion) return 0;
    return region?.getAttribute('data-nav-region') === 'page' ? 1 : 2;
  };

  let best: HTMLElement | null = null;
  let bestScore = Infinity;
  let bestRank = Infinity;

  for (const el of collectFocusables()) {
    if (el === from) continue;
    const c = el.getBoundingClientRect();
    if (c.width === 0 && c.height === 0) continue;
    const cCx = c.left + c.width / 2;
    const cCy = c.top + c.height / 2;

    // Must lie strictly in the travel direction.
    let primaryGap: number;
    if (dir === 'right') {
      if (c.left < o.right - 2 || cCx <= oCx) continue;
      primaryGap = c.left - o.right;
    } else if (dir === 'left') {
      if (c.right > o.left + 2 || cCx >= oCx) continue;
      primaryGap = o.left - c.right;
    } else if (dir === 'down') {
      if (c.top < o.bottom - 2 || cCy <= oCy) continue;
      primaryGap = c.top - o.bottom;
    } else {
      if (c.bottom > o.top + 2 || cCy >= oCy) continue;
      primaryGap = o.top - c.bottom;
    }
    primaryGap = Math.max(0, primaryGap);

    const [oStart, oEnd, cStart, cEnd] = horizontal
      ? [o.top, o.bottom, c.top, c.bottom]
      : [o.left, o.right, c.left, c.right];
    const overlapLen = Math.max(0, Math.min(oEnd, cEnd) - Math.max(oStart, cStart));
    const crossGap =
      overlapLen > 0 ? 0 : Math.max(oStart, cStart) - Math.min(oEnd, cEnd);
    const overlapRatio =
      overlapLen / Math.max(1, Math.min(oEnd - oStart, cEnd - cStart));

    // Candidates that only touch at a corner.
    if (primaryGap <= 2 && overlapRatio === 0) continue;

    const euclid = Math.hypot(cCx - oCx, cCy - oCy);
    const score =
      primaryGap * 1 + crossGap * 2 + euclid * 0.25 + (1 - overlapRatio) * 100;

    const rank = rankOf(el);
    if (rank > bestRank) continue;
    if (rank === bestRank && score >= bestScore) continue;
    best = el;
    bestScore = score;
    bestRank = rank;
  }

  return best;
}

/**
 * Nearest ancestor of `el` that scrolls along `dir`'s axis and is not already
 * at its edge that way.
 */
export function scrollableToward(el: HTMLElement, dir: Direction): HTMLElement | null {
  const vertical = dir === 'up' || dir === 'down';
  for (let n = el.parentElement; n; n = n.parentElement) {
    const overflow = vertical
      ? getComputedStyle(n).overflowY
      : getComputedStyle(n).overflowX;
    if (overflow !== 'auto' && overflow !== 'scroll') continue;
    const pos = vertical ? n.scrollTop : n.scrollLeft;
    const span = vertical ? n.clientHeight : n.clientWidth;
    const max = vertical
      ? n.scrollHeight - n.clientHeight
      : n.scrollWidth - n.clientWidth;
    // `overflow-y: auto` makes the computed `overflow-x` auto as well, so a
    // plain vertical column looks sideways-scrollable for the few pixels a
    // scrollbar or a scaled child adds. Require real distance, or the column
    // swallows every horizontal move made inside it.
    if (max <= Math.max(8, span * 0.1)) continue;
    const atEdge = dir === 'up' || dir === 'left' ? pos <= 1 : pos >= max - 1;
    if (!atEdge) return n;
  }
  return null;
}

/**
 * Dispatch a real keydown so widgets run their own logic. `code` matters to
 * handlers that read it, such as the keybind recorder.
 *
 * @returns whether something consumed it.
 */
export function synthKey(target: EventTarget | null, key: string): boolean {
  if (!target) return false;
  const ev = new KeyboardEvent('keydown', {
    key,
    code: key,
    bubbles: true,
    cancelable: true,
  });
  target.dispatchEvent(ev);
  return ev.defaultPrevented;
}
