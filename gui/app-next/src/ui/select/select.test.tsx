import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { createSignal } from 'solid-js';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { initKeyboard } from '@/lib/keyboard';
import { MultiSelect, Select, type SelectOption } from '@/ui/select';

const FRUITS: SelectOption<string>[] = [
  { value: 'apple', text: 'Apple' },
  { value: 'banana', text: 'Banana' },
  { value: 'blueberry', text: 'Blueberry' },
  { value: 'cherry', text: 'Cherry' },
];

function renderSelect(initial = 'apple') {
  const onChange = vi.fn<(value: string) => void>();
  const [value, setValue] = createSignal(initial);
  render(() => (
    <Select
      value={value()}
      onChange={(v) => {
        onChange(v);
        setValue(v);
      }}
      options={FRUITS}
      labelledBy="label"
    />
  ));
  return { onChange, trigger: screen.getByRole('combobox') };
}

function activeOption(trigger: HTMLElement) {
  const id = trigger.getAttribute('aria-activedescendant');
  return id ? document.getElementById(id)?.textContent : undefined;
}

describe('Select', () => {
  let stopKeyboard: () => void;
  beforeEach(() => {
    stopKeyboard = initKeyboard();
  });
  afterEach(() => {
    cleanup();
    stopKeyboard();
  });

  it('shows the selected option as its value', () => {
    const { trigger } = renderSelect('banana');
    expect(trigger).toHaveTextContent('Banana');
  });

  it('opens on Enter with the selection active, focus stays on the trigger', async () => {
    const { trigger } = renderSelect('blueberry');
    trigger.focus();
    await userEvent.setup().keyboard('{Enter}');
    expect(trigger).toHaveAttribute('aria-expanded', 'true');
    expect(activeOption(trigger)).toBe('Blueberry');
    expect(trigger).toHaveFocus();
  });

  it('marks the active option only once keys are used', async () => {
    const { trigger } = renderSelect('banana');
    const user = userEvent.setup();
    await user.click(trigger);
    const option = screen.getAllByRole('option', { hidden: true })[1]!;
    expect(option).not.toHaveClass('ring-2');
    await user.keyboard('{ArrowUp}{ArrowDown}');
    expect(option).toHaveClass('ring-2');
    await user.keyboard('{Escape}{Enter}');
    expect(option).toHaveClass('ring-2');
  });

  it('arrows move, Enter selects and closes', async () => {
    const { trigger, onChange } = renderSelect('apple');
    trigger.focus();
    await userEvent.setup().keyboard('{Enter}{ArrowDown}{ArrowDown}{Enter}');
    expect(onChange).toHaveBeenCalledExactlyOnceWith('blueberry');
    expect(trigger).toHaveAttribute('aria-expanded', 'false');
  });

  it('arrows stop at the ends', async () => {
    const { trigger } = renderSelect('apple');
    trigger.focus();
    await userEvent.setup().keyboard('{Enter}{ArrowUp}');
    expect(activeOption(trigger)).toBe('Apple');
    await userEvent.setup().keyboard('{End}{ArrowDown}');
    expect(activeOption(trigger)).toBe('Cherry');
  });

  it('type-ahead jumps by prefix and cycles on a repeated letter', async () => {
    const { trigger } = renderSelect('apple');
    trigger.focus();
    const user = userEvent.setup();
    await user.keyboard('{Enter}c');
    expect(activeOption(trigger)).toBe('Cherry');
    cleanup();
    const second = renderSelect('apple').trigger;
    second.focus();
    await user.keyboard('{Enter}bb');
    expect(activeOption(second)).toBe('Blueberry');
  });

  it('Escape closes without selecting', async () => {
    const { trigger, onChange } = renderSelect('apple');
    trigger.focus();
    await userEvent.setup().keyboard('{Enter}{ArrowDown}{Escape}');
    expect(trigger).toHaveAttribute('aria-expanded', 'false');
    expect(onChange).not.toHaveBeenCalled();
  });

  it('arrows at rest are left to navigation', async () => {
    const { trigger, onChange } = renderSelect('apple');
    trigger.focus();
    await userEvent.setup().keyboard('{ArrowDown}');
    expect(trigger).toHaveAttribute('aria-expanded', 'false');
    expect(onChange).not.toHaveBeenCalled();
  });
});

describe('MultiSelect', () => {
  afterEach(cleanup);

  function renderMulti(initial: string[]) {
    const [value, setValue] = createSignal(initial);
    render(() => (
      <MultiSelect
        value={value()}
        onChange={setValue}
        options={FRUITS}
        labelledBy="label"
      />
    ));
    return { value, trigger: screen.getByRole('combobox') };
  }

  it('toggles options and stays open', async () => {
    const { value, trigger } = renderMulti(['apple']);
    trigger.focus();
    await userEvent.setup().keyboard('{Enter}{ArrowDown}{Enter}');
    expect(value()).toEqual(['apple', 'banana']);
    expect(trigger).toHaveAttribute('aria-expanded', 'true');
    await userEvent.setup().keyboard('{ArrowUp}{Enter}');
    expect(value()).toEqual(['banana']);
  });

  it('header actions select all and none', async () => {
    const { value, trigger } = renderMulti([]);
    const user = userEvent.setup();
    await user.click(trigger);
    await user.click(screen.getByText('dropdown_select-all'));
    expect(value()).toEqual(FRUITS.map((f) => f.value));
    await user.click(screen.getByText('dropdown_unselect-all'));
    expect(value()).toEqual([]);
  });

  it('renders options and the closed value through the render hooks', async () => {
    const options: SelectOption<string>[] = FRUITS.map((f) => ({
      ...f,
      render: () => <b>{`row ${f.value}`}</b>,
    }));
    render(() => (
      <MultiSelect
        value={['apple', 'cherry']}
        onChange={() => {}}
        options={options}
        renderValue={(list) => `picked ${list.length}`}
      />
    ));
    expect(screen.getByRole('combobox')).toHaveTextContent('picked 2');
    expect(
      screen.getAllByRole('option', { hidden: true })[1]
    ).toHaveTextContent('row banana');
  });

  it('shows one label, then a count', () => {
    const { trigger } = renderMulti(['cherry']);
    expect(trigger).toHaveTextContent('Cherry');
    cleanup();
    expect(renderMulti(['apple', 'cherry']).trigger).toHaveTextContent(
      'input-select-count'
    );
  });
});
