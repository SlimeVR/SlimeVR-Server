import {
  createMemoryHistory,
  MemoryRouter,
  useNavigate,
} from '@solidjs/router';
import { cleanup, render, screen, waitFor } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import {
  ChangeResetsSettingsRequestT,
  MountingMethod,
  ResetsSettingsResponseT,
  SettingsDefaultsResponseT,
} from 'solarxr-protocol';
import { onMount } from 'solid-js';
import { afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';
import { setClient } from '@/solarxr/client';
import { createFakeClient, type FakeClient } from '@/solarxr/testing';
import { resetsSettingsPage } from './resets/resets-def';
import { settingsRoute, type FocusSettingState } from './settings-route';

let fake: FakeClient;
let wide = false;
const scrollIntoView = vi.fn();

beforeAll(() => {
  setPlatform(fakePlatform({ kind: 'desktop' }));
  fake = createFakeClient();
  setClient(fake);
  window.matchMedia = (query: string) =>
    ({
      matches: wide,
      media: query,
      addEventListener: () => {},
      removeEventListener: () => {},
    }) as unknown as MediaQueryList;
  Element.prototype.scrollIntoView = scrollIntoView;
});
afterEach(() => {
  cleanup();
  wide = false;
});

function renderAt(path: string) {
  const history = createMemoryHistory();
  history.set({ value: path });
  const routes = [
    settingsRoute({ base: '/settings', wizard: '/onboarding' }),
    { path: '/onboarding', component: () => <p>wizard</p> },
  ];
  render(() => <MemoryRouter history={history}>{routes}</MemoryRouter>);
  return history;
}

function loadResets(patch: Partial<ResetsSettingsResponseT>) {
  const defaults = new SettingsDefaultsResponseT();
  defaults.resets = new ResetsSettingsResponseT();
  fake.push(Object.assign(new ResetsSettingsResponseT(), patch), defaults);
}

describe('settings route', () => {
  it('lists the pages at /settings on phones', () => {
    const history = renderAt('/settings');
    expect(history.get()).toBe('/settings');
    expect(
      screen.getByRole('heading', { name: 'navbar-settings' })
    ).toBeInTheDocument();
  });

  it('opens the first page at /settings beside the sidebar', async () => {
    wide = true;
    const history = renderAt('/settings');
    await waitFor(() => expect(history.get()).toBe('/settings/tracking'));
  });

  it('sends unknown pages back to /settings', async () => {
    const history = renderAt('/settings/nope');
    await waitFor(() => expect(history.get()).toBe('/settings'));
  });

  it('scrolls to the row named in the router state', async () => {
    scrollIntoView.mockClear();
    const focus: FocusSettingState = {
      focusSetting:
        resetsSettingsPage.groups.resetBehaviour.rows.saveMountingReset.key,
    };
    // State only exists through an in-app navigation, like search results
    function Search() {
      const navigate = useNavigate();
      onMount(() => navigate('/settings/resets', { state: focus }));
      return null;
    }
    const history = createMemoryHistory();
    history.set({ value: '/search' });
    render(() => (
      <MemoryRouter history={history}>
        {[
          settingsRoute({ base: '/settings', wizard: '/onboarding' }),
          { path: '/search', component: Search },
        ]}
      </MemoryRouter>
    ));
    await waitFor(() =>
      expect(scrollIntoView).toHaveBeenCalledWith({
        block: 'center',
        behavior: 'smooth',
      })
    );
  });
});

describe('resets page', () => {
  it('saves a toggle and resets it to the default', async () => {
    renderAt('/settings/resets');
    loadResets({
      resetMountingFeet: true,
      mountingMethod: MountingMethod.POSE,
    });
    const user = userEvent.setup();
    const toggle = screen.getByRole('switch', {
      name: 'settings-general-fk_settings-leg_fk-reset_mounting_feet-v1',
    });
    expect(toggle).toHaveAttribute('aria-checked', 'true');

    await user.click(toggle);
    expect(
      fake.sentOf(ChangeResetsSettingsRequestT).at(-1)?.resetMountingFeet
    ).toBe(false);
    // Shown right away, before the server answers
    expect(toggle).toHaveAttribute('aria-checked', 'false');

    // Confirmed: only the method still differs from the defaults
    loadResets({
      resetMountingFeet: false,
      mountingMethod: MountingMethod.POSE,
    });
    expect(
      screen.getAllByRole('button', { name: 'settings-reset-single' })
    ).toHaveLength(1);
  });

  it('dims the mounting options unless the method is automatic', () => {
    renderAt('/settings/resets');
    loadResets({ mountingMethod: MountingMethod.MANUAL });
    const toggle = screen.getByRole('switch', {
      name: 'settings-general-fk_settings-leg_fk-reset_mounting_feet-v1',
    });
    expect(toggle).toHaveAttribute('aria-disabled', 'true');
  });
});
