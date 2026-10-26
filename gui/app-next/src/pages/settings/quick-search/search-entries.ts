import type { MessageId } from '@/lib/l10n';
import type { FocusSettingState } from '../settings-route';
import { pageAvailable, settingsPages } from '../settings-pages';

export const MAX_RESULTS = 30;

export type SearchSection = 'settings' | 'page';

export const SECTION_LABELS: Record<SearchSection, MessageId> = {
  settings: 'navbar-settings',
  page: 'settings-search-section-page',
};

export interface SearchDestination {
  path: string;
  title: MessageId;
  state?: object;
  available?: () => boolean;
}

export interface SearchEntry {
  // Unique across all entries
  id: string;
  section: SearchSection;
  route: string;
  // Router state to navigate with
  state?: object;
  // Fluent ids between the section and the title
  crumbs: MessageId[];
  title: MessageId;
  description?: MessageId;
}

// Every settings page, row and titled group without rows (custom content),
// then the destinations.
export function listSearchEntries(
  base: string,
  destinations: readonly SearchDestination[]
): SearchEntry[] {
  const entries: SearchEntry[] = [];

  for (const { page } of settingsPages) {
    if (!pageAvailable(page)) continue;
    const route = `${base}/${page.path}`;
    entries.push({
      id: route,
      section: 'settings',
      route,
      crumbs: [],
      title: page.title,
    });

    for (const [groupName, group] of Object.entries(page.groups)) {
      const rows = Object.values(group.rows);
      if (!rows.length && group.title)
        entries.push({
          id: `${route}#${groupName}`,
          section: 'settings',
          route,
          crumbs: [page.title],
          title: group.title,
          description: group.description,
        });

      for (const row of rows) {
        if (row.available && !row.available()) continue;
        const state: FocusSettingState = { focusSetting: row.key };
        entries.push({
          id: row.key,
          section: 'settings',
          route,
          state,
          crumbs: group.title ? [page.title, group.title] : [page.title],
          title: row.title,
          description: row.description,
        });
      }
    }
  }

  for (const destination of destinations) {
    if (destination.available && !destination.available()) continue;
    entries.push({
      id: destination.path,
      section: 'page',
      route: destination.path,
      state: destination.state,
      crumbs: [],
      title: destination.title,
    });
  }

  return entries;
}

export function searchWords(query: string): string[] {
  return query.toLowerCase().split(/\s+/).filter(Boolean);
}

export interface IndexedEntry {
  entry: SearchEntry;
  haystack: string;
}

// Lowercased text of the title, description, section and crumbs.
// `text` translates an id (pass `t`, so a memo over it follows the locale).
export function indexEntries(
  entries: readonly SearchEntry[],
  text: (id: MessageId) => string
): IndexedEntry[] {
  return entries.map((entry) => ({
    entry,
    haystack: [
      entry.title,
      entry.description,
      SECTION_LABELS[entry.section],
      ...entry.crumbs,
    ]
      .map((id) => (id ? text(id).toLowerCase() : ''))
      .join('\n'),
  }));
}

// Entries holding every word, first MAX_RESULTS in registry order
export function matchEntries(
  index: readonly IndexedEntry[],
  words: readonly string[]
): SearchEntry[] {
  if (!words.length) return [];
  const matched: SearchEntry[] = [];
  for (const { entry, haystack } of index) {
    if (!words.every((word) => haystack.includes(word))) continue;
    matched.push(entry);
    if (matched.length === MAX_RESULTS) break;
  }
  return matched;
}
