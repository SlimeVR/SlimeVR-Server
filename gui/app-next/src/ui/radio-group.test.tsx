import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { createSignal } from 'solid-js';
import { afterEach, describe, expect, it } from 'vitest';
import { type ChoiceOption, Segmented } from '@/ui/radio-group';

const OPTIONS: ChoiceOption<number>[] = [
  { value: 1, label: 'dialog-cancel' },
  { value: 2, label: 'status-loading' },
  { value: 3, label: 'toast-saved' },
];

function renderSegmented(initial: number) {
  const [value, setValue] = createSignal(initial);
  render(() => (
    <Segmented value={value()} onChange={setValue} options={OPTIONS} />
  ));
  return { value, radios: screen.getAllByRole('radio') };
}

describe('Segmented keys', () => {
  afterEach(cleanup);

  it('only the selected option is tabbable', () => {
    const { radios } = renderSegmented(2);
    expect(radios.map((r) => r.tabIndex)).toEqual([-1, 0, -1]);
  });

  it('arrows move and select, Home/End jump', async () => {
    const { value, radios } = renderSegmented(1);
    const user = userEvent.setup();
    radios[0]!.focus();
    await user.keyboard('{ArrowRight}');
    expect(value()).toBe(2);
    expect(radios[1]).toHaveFocus();
    await user.keyboard('{End}');
    expect(value()).toBe(3);
    await user.keyboard('{Home}');
    expect(value()).toBe(1);
  });

  it('does not wrap at the ends', async () => {
    const { value, radios } = renderSegmented(1);
    radios[0]!.focus();
    await userEvent.setup().keyboard('{ArrowLeft}');
    expect(value()).toBe(1);
  });

  it('leaves vertical arrows to navigation', async () => {
    const { value, radios } = renderSegmented(1);
    radios[0]!.focus();
    await userEvent.setup().keyboard('{ArrowDown}');
    expect(value()).toBe(1);
  });
});
