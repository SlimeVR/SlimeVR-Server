import type { Component } from 'solid-js';
import type { MessageId } from '@/lib/l10n';
import type { SettingsPageDef } from '@/lib/settings-def';
import { resetsSettingsPage } from './resets/resets-def';
import { ResetsPage } from './resets/resets-page';

export interface SettingsPageEntry {
  page: SettingsPageDef;
  component: Component;
}

export interface SettingsSection {
  label: MessageId;
  pages: SettingsPageEntry[];
}

export const settingsSections: SettingsSection[] = [
  {
    label: 'settings-sidebar-general',
    pages: [{ page: resetsSettingsPage, component: ResetsPage }],
  },
];

export const settingsPages = settingsSections.flatMap((section) => section.pages);

export function pageAvailable(page: SettingsPageDef) {
  return page.available?.() ?? true;
}
