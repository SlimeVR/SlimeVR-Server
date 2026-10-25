import { render, screen } from '@solidjs/testing-library';
import { createSignal } from 'solid-js';
import { describe, expect, it } from 'vitest';
import { type LoadableResource, Loaded } from '@/ui/loaded';

type State = 'never-loaded' | 'stale' | 'fresh';

function fakeResource(state: State, value?: { items: string[] }) {
  const [s, setState] = createSignal<State>(state);
  const [v, setValue] = createSignal(value);
  const resource: LoadableResource<{ items: string[] }> = {
    value: v,
    state: s,
  };
  return { resource, setState, setValue };
}

function renderLoaded(resource: LoadableResource<{ items: string[] }>) {
  render(() => (
    <Loaded
      of={resource}
      empty={{ when: (d) => d.items.length === 0, title: 'empty-title' }}
    >
      {(data) => <p>{data().items.join(',')}</p>}
    </Loaded>
  ));
}

describe('Loaded', () => {
  it('shows loading while never loaded', () => {
    renderLoaded(fakeResource('never-loaded').resource);
    expect(screen.getByRole('status')).toHaveAccessibleName('status-loading');
  });

  it('renders data when fresh and keeps it when stale', () => {
    const { resource, setState } = fakeResource('fresh', { items: ['a', 'b'] });
    renderLoaded(resource);
    expect(screen.getByText('a,b')).toBeInTheDocument();
    setState('stale');
    expect(screen.getByText('a,b')).toBeInTheDocument();
  });

  it('shows the empty state', () => {
    renderLoaded(fakeResource('fresh', { items: [] }).resource);
    expect(screen.getByText('empty-title')).toBeInTheDocument();
  });
});
