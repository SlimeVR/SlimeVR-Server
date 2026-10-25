import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { createSignal } from 'solid-js';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { initKeyboard, useShortcut } from '@/lib/keyboard';
import { fakePlatform } from '@/lib/platform-fake';
import { setPlatform } from '@/lib/platform';
import { KeybindRecorder } from '@/ui/keybind-recorder';

function renderRecorder(initial: string[] = ['CTRL', 'A']) {
  const onChange = vi.fn<(keys: string[]) => void>();
  const [value, setValue] = createSignal(initial);
  render(() => (
    <>
      <KeybindRecorder
        value={value()}
        onChange={(keys) => {
          onChange(keys);
          setValue(keys);
        }}
        labelledBy="label"
      />
      <button type="button">after</button>
    </>
  ));
  const recorder = screen.getAllByRole('button')[0]!;
  recorder.focus();
  return { onChange, recorder };
}

describe('KeybindRecorder', () => {
  let stopKeyboard: () => void;
  beforeEach(() => {
    setPlatform(fakePlatform());
    stopKeyboard = initKeyboard();
  });
  afterEach(() => {
    cleanup();
    stopKeyboard();
  });

  it('shows the binding at rest', () => {
    const { recorder } = renderRecorder(['CTRL', 'SHIFT', 'UP']);
    expect(recorder).toHaveTextContent('CTRL+SHIFT+↑');
  });

  it('records modifiers and a key, commits when all are released', async () => {
    const { onChange } = renderRecorder();
    const user = userEvent.setup();
    await user.keyboard('{Enter}{Control>}{Shift>}k');
    expect(onChange).not.toHaveBeenCalled();
    await user.keyboard('{/Shift}{/Control}');
    expect(onChange).toHaveBeenCalledExactlyOnceWith(['CTRL', 'SHIFT', 'K']);
  });

  it('keys go to the recorder, not to shortcuts', async () => {
    const shortcut = vi.fn();
    render(() => {
      useShortcut('Mod+K', shortcut);
      return null;
    });
    renderRecorder();
    await userEvent.setup().keyboard('{Enter}{Control>}k{/Control}');
    expect(shortcut).not.toHaveBeenCalled();
  });

  it('Escape cancels and keeps the binding', async () => {
    const { onChange, recorder } = renderRecorder();
    await userEvent.setup().keyboard('{Enter}{Escape}');
    expect(onChange).not.toHaveBeenCalled();
    expect(recorder).toHaveTextContent('CTRL+A');
    expect(recorder).toHaveFocus();
  });

  it('Tab cancels and still moves focus', async () => {
    const { onChange } = renderRecorder();
    const user = userEvent.setup();
    await user.keyboard('{Enter}');
    await user.tab();
    expect(onChange).not.toHaveBeenCalled();
    expect(screen.getByText('after')).toHaveFocus();
  });

  it('an unsupported key is not recorded', async () => {
    const { onChange } = renderRecorder();
    const user = userEvent.setup();
    await user.keyboard('{Enter}{Control>}{ContextMenu}');
    expect(screen.getByRole('status')).toHaveTextContent(
      'settings-keybinds-error-unsupported-key'
    );
    await user.keyboard('{/Control}');
    expect(onChange).toHaveBeenCalledExactlyOnceWith(['CTRL']);
  });

  it('suspends global shortcuts in the platform while recording', async () => {
    const keybindRecording = vi.fn();
    setPlatform(fakePlatform({ keybindRecording }));
    renderRecorder();
    const user = userEvent.setup();
    await user.keyboard('{Enter}');
    expect(keybindRecording).toHaveBeenLastCalledWith(true);
    await user.keyboard('{Escape}');
    expect(keybindRecording).toHaveBeenLastCalledWith(false);
  });

  it('a disabled recorder does not record', async () => {
    const onChange = vi.fn();
    render(() => <KeybindRecorder value={[]} onChange={onChange} disabled />);
    const recorder = screen.getByRole('button');
    recorder.focus();
    await userEvent.setup().keyboard('{Enter}{Control>}k{/Control}');
    expect(onChange).not.toHaveBeenCalled();
  });
});
