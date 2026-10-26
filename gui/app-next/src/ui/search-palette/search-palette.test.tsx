import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { createMemo, createSignal } from 'solid-js';
import { afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { HighlightText, SearchPalette } from '@/ui/search-palette';

const ITEMS = ['apple', 'apricot', 'banana'];

beforeAll(() => {
  Element.prototype.scrollIntoView = vi.fn();
});
afterEach(cleanup);

function renderPalette() {
  const onPick = vi.fn<(item: string) => void>();
  const [query, setQuery] = createSignal('');
  render(() => {
    const results = createMemo(() =>
      query() ? ITEMS.filter((item) => item.startsWith(query())) : []
    );
    return (
      <SearchPalette
        query={query()}
        onQuery={setQuery}
        results={results()}
        onPick={onPick}
        placeholder="settings-sidebar-search"
        hint="settings-sidebar-search-hint"
        empty="settings-sidebar-search-empty"
        result={(item) => item}
      />
    );
  });
  return { onPick, input: screen.getByRole('combobox') };
}

describe('SearchPalette', () => {
  it('shows the hint, then the empty state', async () => {
    const { input } = renderPalette();
    expect(
      screen.getByText('settings-sidebar-search-hint')
    ).toBeInTheDocument();
    await userEvent.setup().type(input, 'zz');
    expect(
      screen.getByText('settings-sidebar-search-empty')
    ).toBeInTheDocument();
  });

  it('arrows move the active option without wrapping, Enter picks it', async () => {
    const { input, onPick } = renderPalette();
    const user = userEvent.setup();
    await user.type(input, 'ap');
    const options = screen.getAllByRole('option');
    expect(options[0]).toHaveAttribute('aria-selected', 'true');
    expect(input).toHaveAttribute('aria-activedescendant', options[0]?.id);
    await user.keyboard('{ArrowDown}{ArrowDown}');
    expect(options[1]).toHaveAttribute('aria-selected', 'true');
    await user.keyboard('{Enter}');
    expect(onPick).toHaveBeenCalledExactlyOnceWith('apricot');
  });

  it('a new result list resets the active option', async () => {
    const { input } = renderPalette();
    const user = userEvent.setup();
    await user.type(input, 'a');
    await user.keyboard('{ArrowDown}');
    await user.type(input, 'p');
    expect(screen.getAllByRole('option')[0]).toHaveAttribute(
      'aria-selected',
      'true'
    );
  });

  it('click picks an option', async () => {
    const { input, onPick } = renderPalette();
    const user = userEvent.setup();
    await user.type(input, 'b');
    await user.click(screen.getByRole('option', { name: 'banana' }));
    expect(onPick).toHaveBeenCalledExactlyOnceWith('banana');
  });
});

describe('HighlightText', () => {
  it('marks every word, case insensitive', () => {
    const { container } = render(() => (
      <HighlightText text="Reset mounting reset" words={['reset']} />
    ));
    expect(
      [...container.querySelectorAll('mark')].map((m) => m.textContent)
    ).toEqual(['Reset', 'reset']);
  });
});
