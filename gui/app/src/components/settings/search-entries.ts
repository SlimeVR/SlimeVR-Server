import { FocusSettingState } from './SettingRow';
import { SearchAvailability } from './settings-definitions';
import { settingsPages } from './settings-pages';
import { SearchSection, searchDestinations } from './search-destinations';

export interface SearchEntry {
  // Unique across all entries
  id: string;
  section: SearchSection;
  route: string;
  // Router state to navigate with
  state?: object;
  // Fluent ids between the section and the title
  crumbs: string[];
  title: string;
  description: string | null;
}

export const SECTIONS: Record<SearchSection, { label: string; color: string }> = {
  settings: { label: 'navbar-settings', color: 'text-accent-lighter' },
  page: {
    label: 'settings-search-section-page',
    color: 'text-status-success',
  },
};

// Every settings page, row and titled group without rows (custom content),
// then the pages outside the settings
export function listSearchEntries(ctx: SearchAvailability) {
  const entries: SearchEntry[] = [];

  for (const { page } of settingsPages) {
    if (page.available && !page.available(ctx)) continue;
    const route = `/settings/${page.path}`;
    entries.push({
      id: route,
      section: 'settings',
      route,
      crumbs: [],
      title: page.title,
      description: null,
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
          description: group.description ?? null,
        });

      for (const row of rows) {
        if (row.available && !row.available(ctx)) continue;
        const state: FocusSettingState = { focusSetting: row.key };
        entries.push({
          id: row.key,
          section: 'settings',
          route,
          state,
          crumbs: group.title ? [page.title, group.title] : [page.title],
          title: row.title,
          description: row.description ?? null,
        });
      }
    }
  }

  for (const destination of searchDestinations) {
    if (destination.available && !destination.available(ctx)) continue;
    entries.push({
      id: destination.path,
      section: destination.section,
      route: destination.path,
      state: destination.state,
      crumbs: [],
      title: destination.title,
      description: null,
    });
  }

  return entries;
}
