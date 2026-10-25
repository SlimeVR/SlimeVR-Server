import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { clearToasts, dismissToast, showToast, toasts } from '@/ui/toast';

beforeEach(() => vi.useFakeTimers());

afterEach(() => {
  clearToasts();
  vi.useRealTimers();
});

describe('toasts', () => {
  it('expires after its duration', () => {
    showToast({ kind: 'success', message: 'toast-saved', durationMs: 1000 });
    vi.advanceTimersByTime(999);
    expect(toasts()).toHaveLength(1);
    vi.advanceTimersByTime(1);
    expect(toasts()).toHaveLength(0);
  });

  it('coalesces by key and restarts the timer', () => {
    const first = showToast({ kind: 'success', message: 'toast-saved', key: 'k' });
    vi.advanceTimersByTime(1500);
    const second = showToast({ kind: 'success', message: 'toast-saved', key: 'k' });
    expect(second).toBe(first);
    expect(toasts()).toHaveLength(1);
    vi.advanceTimersByTime(1500);
    expect(toasts()).toHaveLength(1);
    vi.advanceTimersByTime(500);
    expect(toasts()).toHaveLength(0);
  });

  it('keeps the same toast object when nothing changed', () => {
    showToast({ kind: 'success', message: 'toast-saved', key: 'k' });
    const before = toasts()[0];
    showToast({ kind: 'success', message: 'toast-saved', key: 'k' });
    expect(toasts()[0]).toBe(before);
  });

  it('shows each keyless toast', () => {
    showToast({ kind: 'error', message: 'toast-not-saved' });
    showToast({ kind: 'error', message: 'toast-not-saved' });
    expect(toasts()).toHaveLength(2);
  });

  it('keeps at most three, dropping the oldest', () => {
    const ids = [1, 2, 3, 4].map(() =>
      showToast({ kind: 'error', message: 'toast-not-saved' })
    );
    expect(toasts().map((t) => t.id)).toEqual(ids.slice(1));
  });

  it('dismisses by id', () => {
    const id = showToast({ kind: 'error', message: 'toast-not-saved' });
    dismissToast(id);
    expect(toasts()).toHaveLength(0);
  });
});
