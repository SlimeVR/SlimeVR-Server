import { createRoot, createSignal } from 'solid-js';
import { afterEach, describe, expect, it, vi } from 'vitest';
import {
  canHandleBack,
  dismiss,
  handleBack,
  modalOpen,
  pushBack,
  setBackFallback,
  useBack,
} from '@/lib/back-stack';

afterEach(() => setBackFallback(null));

describe('back stack', () => {
  it('handles the most recent entry first', () => {
    const calls: string[] = [];
    const removeA = pushBack(() => calls.push('a'));
    const removeB = pushBack(() => calls.push('b'));
    expect(handleBack()).toBe(true);
    removeB();
    expect(handleBack()).toBe(true);
    removeA();
    expect(calls).toEqual(['b', 'a']);
    expect(handleBack()).toBe(false);
  });

  it('falls back to history under the overlays', () => {
    const back = vi.fn();
    const [can, setCan] = createSignal(false);
    setBackFallback({ can, back });
    expect(canHandleBack()).toBe(false);
    expect(handleBack()).toBe(false);
    setCan(true);
    expect(canHandleBack()).toBe(true);
    const overlay = vi.fn();
    const remove = pushBack(overlay);
    handleBack();
    expect(overlay).toHaveBeenCalledOnce();
    expect(back).not.toHaveBeenCalled();
    remove();
    handleBack();
    expect(back).toHaveBeenCalledOnce();
  });

  it('dismiss closes overlays but never goes back in history', () => {
    const back = vi.fn();
    setBackFallback({ can: () => true, back });
    const overlay = vi.fn();
    const remove = pushBack(overlay);
    expect(dismiss()).toBe(true);
    remove();
    expect(dismiss()).toBe(false);
    expect(overlay).toHaveBeenCalledOnce();
    expect(back).not.toHaveBeenCalled();
  });

  it('tracks modal entries', () => {
    const removePlain = pushBack(() => {});
    expect(modalOpen()).toBe(false);
    const removeModal = pushBack(() => {}, { modal: true });
    expect(modalOpen()).toBe(true);
    removeModal();
    removePlain();
    expect(modalOpen()).toBe(false);
  });

  it('useBack pushes while `when` holds and on top of older entries', () => {
    const calls: string[] = [];
    const removeOld = pushBack(() => calls.push('old'));
    const [open, setOpen] = createSignal(false);
    const dispose = createRoot((dispose) => {
      useBack(() => calls.push('overlay'), { when: open });
      return dispose;
    });
    handleBack();
    setOpen(true);
    handleBack();
    setOpen(false);
    handleBack();
    dispose();
    removeOld();
    expect(calls).toEqual(['old', 'overlay', 'old']);
  });
});
