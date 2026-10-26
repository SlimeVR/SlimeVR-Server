import { describe, expect, it } from 'vitest';
import { setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';
import { resetsSettingsPage } from '../resets/resets-def';
import {
  indexEntries,
  listSearchEntries,
  matchEntries,
  MAX_RESULTS,
  type SearchEntry,
  searchWords,
} from './search-entries';

const text = (id: string) => id;

describe('listSearchEntries', () => {
  setPlatform(fakePlatform());
  const entries = listSearchEntries('/settings', [
    { path: '/', title: 'navbar-home' },
    { path: '/hidden', title: 'navbar-onboarding', available: () => false },
  ]);

  it('lists the page, then each row with its focus key', () => {
    expect(entries.find((e) => e.id === '/settings/resets')).toMatchObject({
      id: '/settings/resets',
      route: '/settings/resets',
      crumbs: [],
      title: 'settings-sidebar-resets',
    });
    const feet = resetsSettingsPage.groups.main.rows.resetMountingFeet;
    expect(entries.find((e) => e.id === feet.key)).toMatchObject({
      route: '/settings/resets',
      state: { focusSetting: feet.key },
      crumbs: ['settings-sidebar-resets'],
      title: feet.title,
    });
  });

  it('puts the group title in the crumbs', () => {
    const yaw = resetsSettingsPage.groups.tapResets.rows.yawReset;
    expect(entries.find((e) => e.id === yaw.key)?.crumbs).toEqual([
      'settings-sidebar-resets',
      'settings-general-gesture_control-subtitle',
    ]);
  });

  it('adds available destinations as pages', () => {
    expect(entries.find((e) => e.id === '/')).toMatchObject({
      section: 'page',
      route: '/',
      title: 'navbar-home',
    });
    expect(entries.find((e) => e.id === '/hidden')).toBeUndefined();
  });

  it('has unique ids', () => {
    const ids = entries.map((e) => e.id);
    expect(new Set(ids).size).toBe(ids.length);
  });
});

describe('matchEntries', () => {
  function entry(id: string, patch: Partial<SearchEntry> = {}): SearchEntry {
    return { id, section: 'settings', route: '/', crumbs: [], title: id, ...patch };
  }

  it('needs every word, in any field', () => {
    const index = indexEntries(
      [
        entry('alpha-one', { crumbs: ['group-beta'] }),
        entry('alpha-two', { description: 'gamma-text' }),
      ],
      text
    );
    expect(matchEntries(index, searchWords('ALPHA beta')).map((e) => e.id)).toEqual([
      'alpha-one',
    ]);
    expect(matchEntries(index, searchWords('gamma')).map((e) => e.id)).toEqual([
      'alpha-two',
    ]);
  });

  it('matches the section label', () => {
    const index = indexEntries([entry('x', { section: 'page' })], text);
    expect(matchEntries(index, ['settings-search-section-page'])).toHaveLength(1);
  });

  it('returns nothing for an empty query', () => {
    const index = indexEntries([entry('a')], text);
    expect(matchEntries(index, searchWords('   '))).toEqual([]);
  });

  it('caps the results', () => {
    const many = Array.from({ length: MAX_RESULTS + 5 }, (_, i) => entry(`row-${i}`));
    expect(matchEntries(indexEntries(many, text), ['row'])).toHaveLength(MAX_RESULTS);
  });
});
