import { cleanup, render, screen } from '@solidjs/testing-library';
import { createSignal } from 'solid-js';
import { afterEach, describe, expect, it } from 'vitest';
import { type DotOption, DotMultiSelect } from '@/ui/select';

const SOURCES: DotOption<string>[] = [
  { value: 'server', text: 'Server', color: '#a78bfa', detail: '12' },
  { value: 'gui', text: 'GUI', color: '#34d399' },
  { value: 'driver', text: 'Driver', color: '#fbbf24' },
];

describe('DotMultiSelect', () => {
  afterEach(cleanup);

  function renderSources(initial: string[]) {
    const [value, setValue] = createSignal(initial);
    render(() => (
      <DotMultiSelect
        value={value()}
        onChange={setValue}
        options={SOURCES}
        allLabel="dialog-cancel"
        labelledBy="label"
      />
    ));
    return { setValue, trigger: screen.getByRole('combobox') };
  }

  it('one pick shows its label, several show their dots, all show allLabel', () => {
    const { setValue, trigger } = renderSources(['gui']);
    expect(trigger).toHaveTextContent('GUI');
    setValue(['server', 'driver']);
    expect(trigger).toHaveTextContent('input-select-count');
    expect(trigger.querySelectorAll('.rounded-full')).toHaveLength(2);
    setValue(SOURCES.map((s) => s.value));
    expect(trigger).toHaveTextContent('dialog-cancel');
  });

  it('rows show the dot, label and detail', () => {
    renderSources([]);
    const row = screen.getAllByRole('option', { hidden: true })[0]!;
    expect(row).toHaveTextContent('Server12');
    expect(row.querySelector('.rounded-full')).not.toBeNull();
  });
});
