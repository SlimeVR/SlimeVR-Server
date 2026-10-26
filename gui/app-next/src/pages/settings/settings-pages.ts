import type { Component } from 'solid-js';
import type { MessageId } from '@/lib/l10n';
import type { SettingsPageDef } from '@/lib/settings-def';
import { behaviorSettingsPage } from './behavior/behavior-def';
import { BehaviorPage } from './behavior/behavior-page';
import { resetsSettingsPage } from './resets/resets-def';
import { ResetsPage } from './resets/resets-page';
import { trackersSettingsPage } from './trackers/trackers-def';
import { TrackersPage } from './trackers/trackers-page';
import { trackingSettingsPage } from './tracking/tracking-def';
import { TrackingPage } from './tracking/tracking-page';

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
    pages: [
      { page: trackingSettingsPage, component: TrackingPage },
      { page: resetsSettingsPage, component: ResetsPage },
      { page: trackersSettingsPage, component: TrackersPage },
    ],
  },
  {
    label: 'settings-sidebar-interface',
    pages: [{ page: behaviorSettingsPage, component: BehaviorPage }],
  },
];

export const settingsPages = settingsSections.flatMap((section) => section.pages);

export function pageAvailable(page: SettingsPageDef) {
  return page.available?.() ?? true;
}
