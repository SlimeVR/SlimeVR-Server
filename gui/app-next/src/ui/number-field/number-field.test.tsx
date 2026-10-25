import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { createSignal } from 'solid-js';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { formats, type NumberSpec } from '@/lib/format';
import { initKeyboard } from '@/lib/keyboard';
import { loadLocale } from '@/lib/l10n';
import { setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';
import { NumberField } from '@/ui/number-field';

function renderField(
  initial: number,
  opts: { format?: NumberSpec; step?: number; snap?: boolean } = {}
) {
  const onChange = vi.fn<(value: number) => void>();
  const [value, setValue] = createSignal(initial);
  render(() => (
    <NumberField
      value={value()}
      onChange={(next) => {
        onChange(next);
        setValue(next);
      }}
      min={0}
      max={10}
      step={opts.step ?? 1}
      format={opts.format}
      snap={opts.snap}
    />
  ));
  const input = screen.getByRole('spinbutton');
  input.focus();
  return { onChange, setValue, input };
}

describe('NumberField', () => {
  let stopKeyboard: () => void;
  beforeEach(async () => {
    setPlatform(fakePlatform());
    vi.stubGlobal('fetch', async () => new Response('', { status: 200 }));
    await loadLocale(['en']);
    stopKeyboard = initKeyboard();
  });
  afterEach(() => {
    cleanup();
    stopKeyboard();
  });

  it('shows the number in the input and the unit outside it', () => {
    const { input } = renderField(0.25, { format: formats.seconds });
    expect(input).toHaveValue('0.25');
    expect(screen.getByText('s')).not.toBe(input);
    expect(input).toHaveAttribute('aria-valuetext', '0.25s');
  });

  it('Left/Right at rest step and commit', async () => {
    const { onChange } = renderField(5);
    const user = userEvent.setup();
    await user.keyboard('{ArrowRight}');
    expect(onChange).toHaveBeenLastCalledWith(6);
    await user.keyboard('{ArrowLeft}{ArrowLeft}');
    expect(onChange).toHaveBeenLastCalledWith(4);
  });

  it('Up/Down at rest leave the value alone', async () => {
    const { onChange } = renderField(5);
    await userEvent.setup().keyboard('{ArrowUp}{ArrowDown}');
    expect(onChange).not.toHaveBeenCalled();
  });

  it('Home/End at rest commit min and max', async () => {
    const { onChange } = renderField(5);
    const user = userEvent.setup();
    await user.keyboard('{End}');
    expect(onChange).toHaveBeenLastCalledWith(10);
    await user.keyboard('{Home}');
    expect(onChange).toHaveBeenLastCalledWith(0);
  });

  it('a digit at rest replaces the value', async () => {
    const { onChange, input } = renderField(5);
    await userEvent.setup().keyboard('7');
    expect(input).toHaveValue('7');
    await userEvent.setup().keyboard('{Enter}');
    expect(onChange).toHaveBeenCalledExactlyOnceWith(7);
  });

  it('Up/Down while editing step the draft', async () => {
    const { onChange, input } = renderField(5);
    const user = userEvent.setup();
    await user.keyboard('4{ArrowUp}{ArrowUp}');
    expect(input).toHaveValue('6');
    expect(onChange).not.toHaveBeenCalled();
    await user.keyboard('{Enter}');
    expect(onChange).toHaveBeenCalledExactlyOnceWith(6);
  });

  it('out of range shows the error live and clamps on commit', async () => {
    const { onChange, input } = renderField(5);
    const user = userEvent.setup();
    await user.keyboard('99');
    expect(input).toHaveAttribute('aria-invalid', 'true');
    await user.keyboard('{Enter}');
    expect(onChange).toHaveBeenCalledExactlyOnceWith(10);
    expect(input).not.toHaveAttribute('aria-invalid');
  });

  it('Enter on an invalid draft keeps editing, Escape reverts', async () => {
    const { onChange, input } = renderField(5);
    const user = userEvent.setup();
    await user.keyboard('-{Enter}');
    expect(input).toHaveValue('-');
    expect(input).toHaveAttribute('aria-invalid', 'true');
    await user.keyboard('{Escape}');
    expect(input).toHaveValue('5');
    expect(onChange).not.toHaveBeenCalled();
  });

  it('blur on an invalid draft reverts', async () => {
    const { onChange, input } = renderField(5);
    const user = userEvent.setup();
    await user.keyboard('{Backspace}');
    expect(input).toHaveValue('');
    await user.tab();
    expect(input).toHaveValue('5');
    expect(onChange).not.toHaveBeenCalled();
  });

  it('blur commits a valid draft', async () => {
    const { onChange } = renderField(5);
    const user = userEvent.setup();
    await user.keyboard('3');
    await user.tab();
    expect(onChange).toHaveBeenCalledExactlyOnceWith(3);
  });

  it('entering and leaving edit mode without typing changes nothing', async () => {
    const { onChange } = renderField(0.123, { format: formats.seconds });
    const user = userEvent.setup();
    await user.keyboard('{Enter}{Enter}');
    expect(onChange).not.toHaveBeenCalled();
  });

  it('edits percentages scaled by 100', async () => {
    const { onChange, input } = renderField(0.5, { format: formats.percent });
    expect(input).toHaveValue('50');
    await userEvent.setup().keyboard('25{Enter}');
    expect(onChange).toHaveBeenCalledExactlyOnceWith(0.25);
  });

  it('rounds typed values to the format digits', async () => {
    const { onChange } = renderField(1, { format: formats.seconds });
    await userEvent.setup().keyboard('1.239{Enter}');
    expect(onChange).toHaveBeenCalledExactlyOnceWith(1.24);
  });

  it('does not snap typed values unless snap is set', async () => {
    const plain = renderField(1, { step: 0.5 });
    await userEvent.setup().keyboard('1.3{Enter}');
    expect(plain.onChange).toHaveBeenCalledExactlyOnceWith(1.3);
    cleanup();
    const snapped = renderField(1, { step: 0.5, snap: true });
    await userEvent.setup().keyboard('1.3{Enter}');
    expect(snapped.onChange).toHaveBeenCalledExactlyOnceWith(1.5);
  });

  it('accepts the locale decimal separator and `.`', async () => {
    await loadLocale(['fr']);
    const { onChange, input } = renderField(1.5, { format: formats.meters });
    expect(input).toHaveValue('1,50');
    const user = userEvent.setup();
    await user.keyboard('2,25{Enter}');
    expect(onChange).toHaveBeenLastCalledWith(2.25);
    await user.keyboard('2.5{Enter}');
    expect(onChange).toHaveBeenLastCalledWith(2.5);
  });

  it('keeps the draft over value changes while editing', async () => {
    const { setValue, input } = renderField(5);
    await userEvent.setup().keyboard('3');
    setValue(8);
    expect(input).toHaveValue('3');
  });

  it('step buttons step and keep focus on the input', async () => {
    const { onChange, input } = renderField(5);
    await userEvent
      .setup()
      .click(screen.getByRole('button', { name: 'input-number-increase' }));
    expect(onChange).toHaveBeenCalledExactlyOnceWith(6);
    expect(input).toHaveFocus();
  });

  it('step buttons are disabled at the bounds', () => {
    renderField(10);
    expect(
      screen.getByRole('button', { name: 'input-number-increase' })
    ).toBeDisabled();
    expect(
      screen.getByRole('button', { name: 'input-number-decrease' })
    ).toBeEnabled();
  });
});
