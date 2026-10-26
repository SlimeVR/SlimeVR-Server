import {
  createEffect,
  createSignal,
  createUniqueId,
  For,
  Index,
  type JSX,
  on,
  Show,
} from 'solid-js';
import SearchIcon from '~icons/heroicons/magnifying-glass-20-solid';
import BackIcon from '~icons/material-symbols/arrow-back-rounded';
import { cx } from '@/lib/cx';
import { t, type MessageId } from '@/lib/l10n';
import { TextButton } from '@/ui/button';
import { useMotionItem } from '@/ui/motion';
import { StatusState } from '@/ui/status-state';

export interface SearchPaletteProps<T> {
  query: string;
  onQuery: (query: string) => void;
  results: readonly T[];
  onPick: (item: T) => void;
  placeholder: MessageId;
  // Phones get a back button in place of the search icon (no Esc key there)
  onClose?: () => void;
  // Shown while the query is empty
  hint: MessageId;
  // Shown when the query matches nothing
  empty: MessageId;
  // One result row (icon, text), `active` is the row Enter opens
  result: (item: T, active: () => boolean) => JSX.Element;
}

function escapeRegExp(text: string) {
  return text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

// Marks every occurrence of the query words, case insensitive
export function HighlightText(props: {
  text: string;
  words: readonly string[];
}) {
  const parts = () => {
    if (!props.words.length) return [props.text];
    const pattern = new RegExp(
      `(${props.words.map(escapeRegExp).join('|')})`,
      'gi'
    );
    return props.text.split(pattern);
  };

  return (
    <Index each={parts()}>
      {(part, index) =>
        index % 2 === 1 ? (
          <mark class="bg-transparent text-accent-strong">{part()}</mark>
        ) : (
          <>{part()}</>
        )
      }
    </Index>
  );
}

function Kbd(props: { class?: string; children: JSX.Element }) {
  return (
    <kbd
      aria-hidden="true"
      class={cx(
        'shrink-0 rounded-md px-xs py-2xs font-[inherit] whitespace-nowrap',
        props.class
      )}
    >
      {props.children}
    </kbd>
  );
}

function KeyHint(props: { keys: string; label: MessageId }) {
  return (
    <span class="flex items-center gap-sm">
      <Kbd class="bg-surface-control">{props.keys}</Kbd>
      {t(props.label)}
    </span>
  );
}

export function SearchPalette<T>(props: SearchPaletteProps<T>) {
  let input!: HTMLInputElement;
  let list: HTMLDivElement | undefined;
  const listId = createUniqueId();
  const optionId = (index: number) => `${listId}-${index}`;
  const [active, setActive] = createSignal(0);

  useMotionItem(() => input, { owns: 'all' });

  createEffect(
    on(
      () => props.results,
      () => setActive(0)
    )
  );

  createEffect(() => {
    const index = active();
    list
      ?.querySelector(`#${CSS.escape(optionId(index))}`)
      ?.scrollIntoView({ block: 'nearest' });
  });

  function onKeyDown(e: KeyboardEvent) {
    const count = props.results.length;
    if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
      e.preventDefault();
      if (!count) return;
      const step = e.key === 'ArrowDown' ? 1 : -1;
      setActive((i) => Math.min(count - 1, Math.max(0, i + step)));
      return;
    }
    if (e.key === 'Enter') {
      const item = props.results[active()];
      if (item === undefined) return;
      e.preventDefault();
      props.onPick(item);
    }
  }

  return (
    <div class="flex min-h-0 grow flex-col">
      <div class="flex min-h-control items-center gap-md border-b border-surface-control px-lg py-xs">
        <Show when={props.onClose}>
          {(close) => (
            <span class="flex shrink-0 sm:hidden">
              <TextButton
                variant="ghost"
                size="small"
                iconOnly
                label="settings-sidebar-search-close"
                icon={<BackIcon />}
                onClick={() => close()()}
              />
            </span>
          )}
        </Show>
        <SearchIcon
          aria-hidden="true"
          class={cx(
            'icon shrink-0 text-accent-strong',
            props.onClose && 'hidden sm:block'
          )}
          style={{ '--icon-size': '22px' }}
        />
        <input
          ref={input}
          autofocus
          type="text"
          role="combobox"
          autocomplete="off"
          aria-autocomplete="list"
          aria-expanded={props.results.length > 0}
          aria-controls={listId}
          aria-activedescendant={
            props.results.length ? optionId(active()) : undefined
          }
          aria-label={t(props.placeholder)}
          placeholder={t(props.placeholder)}
          value={props.query}
          onInput={(e) => props.onQuery(e.currentTarget.value)}
          onKeyDown={onKeyDown}
          class="
            min-w-0 grow bg-transparent py-lg text-section outline-none placeholder:text-fg-muted
          "
        />
        <Kbd class="hidden bg-surface-control text-fg-muted sm:block">Esc</Kbd>
      </div>
      <Show
        when={props.results.length}
        fallback={
          <StatusState
            kind={props.query.trim() ? 'error' : 'empty'}
            description={props.query.trim() ? props.empty : props.hint}
          />
        }
      >
        <div
          ref={list}
          id={listId}
          role="listbox"
          class="flex min-h-0 grow flex-col gap-xs overflow-y-auto p-sm sm:max-h-[50vh] sm:grow-0"
        >
          <For each={props.results}>
            {(item, index) => (
              // eslint-disable-next-line jsx-a11y/click-events-have-key-events -- keys live on the combobox input (aria-activedescendant)
              <div
                id={optionId(index())}
                role="option"
                tabIndex={-1}
                aria-selected={index() === active()}
                onPointerMove={() => setActive(index())}
                onClick={() => props.onPick(item)}
                class="
                  flex cursor-pointer items-center gap-md rounded-lg px-md py-sm transition-colors
                  aria-selected:bg-surface-control
                "
              >
                <div class="min-w-0 grow">
                  {props.result(item, () => index() === active())}
                </div>
                <Show when={index() === active()}>
                  <Kbd class="hidden bg-surface sm:block">↵</Kbd>
                </Show>
              </div>
            )}
          </For>
        </div>
      </Show>
      <div
        aria-hidden="true"
        class="
          hidden items-center gap-xl border-t border-surface-control bg-surface-well px-lg py-sm
          sm:flex
        "
      >
        <KeyHint keys="↑↓" label="settings-sidebar-search-navigate" />
        <KeyHint keys="↵" label="settings-sidebar-search-open" />
        <KeyHint keys="Esc" label="settings-sidebar-search-close" />
      </div>
    </div>
  );
}
