import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { COPY_FEEDBACK_MS, CopyButton } from '@/ui/copy-button';
import { clearToasts, toasts } from '@/ui/toast';

describe('CopyButton', () => {
  afterEach(() => {
    cleanup();
    clearToasts();
    vi.useRealTimers();
  });

  it('copies the text read at click time and announces it for a moment', async () => {
    vi.useFakeTimers();
    const user = userEvent.setup({ advanceTimers: vi.advanceTimersByTime });
    const writeText = vi
      .spyOn(navigator.clipboard, 'writeText')
      .mockResolvedValue();
    let text = 'first';
    render(() => <CopyButton text={() => text} label="dialog-cancel" />);
    text = 'second';
    await user.click(screen.getByRole('button'));
    expect(writeText).toHaveBeenCalledExactlyOnceWith('second');
    expect(screen.getByRole('status')).toHaveTextContent('input-copy-done');
    vi.advanceTimersByTime(COPY_FEEDBACK_MS);
    expect(screen.getByRole('status')).toHaveTextContent('');
  });

  it('shows an error toast when the clipboard refuses', async () => {
    const user = userEvent.setup();
    vi.spyOn(navigator.clipboard, 'writeText').mockRejectedValue(
      new Error('denied')
    );
    render(() => <CopyButton text="x" label="dialog-cancel" />);
    await user.click(screen.getByRole('button'));
    await vi.waitFor(() =>
      expect(toasts().map((t) => t.message)).toEqual(['input-copy-failed'])
    );
    expect(screen.getByRole('status')).toHaveTextContent('');
  });
});
