import { useNavigate } from '@solidjs/router';
import {
  type Component,
  createEffect,
  createMemo,
  createSignal,
  For,
  on,
  Show,
} from 'solid-js';
import { Dynamic } from 'solid-js/web';
import PageIcon from '~icons/heroicons/document-solid';
import SearchIcon from '~icons/heroicons/magnifying-glass-20-solid';
import GearIcon from '~icons/material-symbols/settings-rounded';
import { cx } from '@/lib/cx';
import { useShortcut } from '@/lib/keyboard';
import { t } from '@/lib/l10n';
import { platform } from '@/lib/platform';
import { Dialog } from '@/ui/dialog';
import { Pressable } from '@/ui/pressable';
import { HighlightText, SearchPalette } from '@/ui/search-palette';
import {
  indexEntries,
  listSearchEntries,
  matchEntries,
  type SearchDestination,
  type SearchEntry,
  type SearchSection,
  searchWords,
  SECTION_LABELS,
} from './search-entries';

const [open, setOpen] = createSignal(false);

export function openQuickSearch() {
  setOpen(true);
}

const SECTION_ICONS: Record<SearchSection, Component<{ class?: string }>> = {
  settings: GearIcon,
  page: PageIcon,
};

function shortcutLabel() {
  return platform.os === 'macos' ? '⌘ K' : 'Ctrl K';
}

function Crumbs(props: { entry: SearchEntry }) {
  return (
    <span class="flex flex-wrap items-center gap-x-xs">
      <Show
        when={props.entry.crumbs.length}
        fallback={t(SECTION_LABELS[props.entry.section])}
      >
        <For each={props.entry.crumbs}>
          {(crumb, index) => (
            <>
              <Show when={index() > 0}>
                <span aria-hidden="true">{'>'}</span>
              </Show>
              <span>{t(crumb)}</span>
            </>
          )}
        </For>
      </Show>
    </span>
  );
}

export function QuickSearch(props: {
  base: string;
  destinations: readonly SearchDestination[];
}) {
  const navigate = useNavigate();
  const [query, setQuery] = createSignal('');
  const index = createMemo(() =>
    indexEntries(listSearchEntries(props.base, props.destinations), (id) =>
      t(id)
    )
  );
  const words = createMemo(() => searchWords(query()));
  const results = createMemo(() => matchEntries(index(), words()));

  useShortcut('Mod+K', openQuickSearch, {
    scope: 'global',
    label: 'titlebar-search',
    inTextEntry: true,
  });

  createEffect(
    on(open, (isOpen) => {
      if (isOpen) setQuery('');
    })
  );

  function pick(entry: SearchEntry) {
    setOpen(false);
    navigate(entry.route, { state: entry.state });
  }

  return (
    <Dialog
      open={open()}
      onClose={() => setOpen(false)}
      label={t('settings-sidebar-search')}
      class="
        h-dvh w-full overflow-hidden surface-page sm:mx-auto sm:mt-[12vh] sm:h-fit sm:max-h-[76vh]
        sm:w-xl sm:max-w-[calc(100%-2rem)] sm:rounded-lg
      "
    >
      <SearchPalette
        query={query()}
        onQuery={setQuery}
        results={results()}
        onPick={pick}
        onClose={() => setOpen(false)}
        placeholder="settings-sidebar-search"
        hint="settings-sidebar-search-hint"
        empty="settings-sidebar-search-empty"
        result={(entry, active) => (
          <span class="flex items-center gap-md">
            <span
              aria-hidden="true"
              class={cx(
                `
                  flex size-control-sm shrink-0 items-center justify-center rounded-md
                  transition-colors
                `,
                active() ? 'bg-accent-muted' : 'bg-surface-control'
              )}
            >
              <Dynamic
                component={SECTION_ICONS[entry.section]}
                class="icon icon-sm"
              />
            </span>
            <span class="flex min-w-0 flex-col">
              <span class="font-bold">
                <HighlightText text={t(entry.title)} words={words()} />
              </span>
              <Crumbs entry={entry} />
            </span>
          </span>
        )}
      />
    </Dialog>
  );
}

export function QuickSearchField() {
  return (
    <Pressable
      onClick={openQuickSearch}
      class="rounded-md bg-surface-control transition-colors hover:bg-surface-hover"
    >
      <span class="flex min-h-control-sm items-center gap-sm px-md">
        <SearchIcon aria-hidden="true" class="icon shrink-0" />
        <span class="min-w-0 grow truncate">{t('titlebar-search')}</span>
        <kbd
          aria-hidden="true"
          class="hidden shrink-0 rounded-md bg-surface px-xs whitespace-nowrap md:inline"
        >
          {shortcutLabel()}
        </kbd>
      </span>
    </Pressable>
  );
}
