import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { createSignal } from 'solid-js';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { dismiss } from '@/lib/back-stack';
import { initKeyboard } from '@/lib/keyboard';
import { TEXT_COMMIT_DEBOUNCE_MS, TextField } from '@/ui/text-field';

function renderField(initial = 'abc') {
  const onChange = vi.fn<(value: string) => void>();
  const [value, setValue] = createSignal(initial);
  render(() => (
    <TextField value={value()} onChange={onChange} label="dialog-cancel" />
  ));
  return { onChange, setValue, input: screen.getByRole('textbox') };
}

describe('TextField', () => {
  let stopKeyboard: () => void;
  beforeEach(() => {
    stopKeyboard = initKeyboard();
  });
  afterEach(() => {
    cleanup();
    stopKeyboard();
    vi.useRealTimers();
  });

  it('is named by its floating label', () => {
    const { input } = renderField();
    expect(input).toHaveAccessibleName('dialog-cancel');
  });

  it('commits on Enter', async () => {
    const { onChange, input } = renderField('');
    await userEvent.setup().type(input, 'hi{Enter}');
    expect(onChange).toHaveBeenCalledExactlyOnceWith('hi');
  });

  it('Escape reverts the draft', async () => {
    const { onChange, input } = renderField('abc');
    const user = userEvent.setup();
    await user.type(input, 'd{Escape}');
    expect(input).toHaveValue('abc');
    await user.tab();
    expect(onChange).not.toHaveBeenCalled();
  });

  it('commits after a typing pause', async () => {
    vi.useFakeTimers();
    const { onChange, input } = renderField('');
    const user = userEvent.setup({ advanceTimers: vi.advanceTimersByTime });
    await user.type(input, 'abc');
    expect(onChange).not.toHaveBeenCalled();
    vi.advanceTimersByTime(TEXT_COMMIT_DEBOUNCE_MS);
    expect(onChange).toHaveBeenCalledExactlyOnceWith('abc');
  });

  it('keeps the draft over value changes while typing', async () => {
    const { setValue, input } = renderField('abc');
    await userEvent.setup().type(input, 'd');
    setValue('server');
    expect(input).toHaveValue('abcd');
  });

  it('takes value changes when not editing', () => {
    const { setValue, input } = renderField('abc');
    setValue('server');
    expect(input).toHaveValue('server');
  });

  it('focus alone is at rest, Enter starts editing and Enter ends it', async () => {
    const { input } = renderField('abc');
    const user = userEvent.setup();
    input.focus();
    expect(input).toHaveClass('caret-transparent');
    await user.keyboard('{Enter}');
    expect(input).not.toHaveClass('caret-transparent');
    await user.keyboard('{Enter}');
    expect(input).toHaveClass('caret-transparent');
  });

  it('typing at rest starts editing and types', async () => {
    const { input } = renderField('');
    input.focus();
    await userEvent.setup().keyboard('x');
    expect(input).toHaveValue('x');
    expect(input).not.toHaveClass('caret-transparent');
  });

  it('back leaves editing first, then has nothing to dismiss', async () => {
    const { input } = renderField('abc');
    input.focus();
    await userEvent.setup().keyboard('{Enter}');
    expect(dismiss()).toBe(true);
    expect(input).toHaveClass('caret-transparent');
    expect(input).toHaveFocus();
    expect(dismiss()).toBe(false);
  });
});
