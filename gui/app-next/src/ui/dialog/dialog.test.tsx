import { cleanup, fireEvent, render, screen } from '@solidjs/testing-library';
import { createSignal } from 'solid-js';
import { afterEach, describe, expect, it } from 'vitest';
import { dismiss, modalOpen } from '@/lib/back-stack';
import { confirm, Dialog, DialogHost } from '@/ui/dialog';
import { Floating } from '@/ui/floating';

afterEach(cleanup);

function renderDialog(closeable = true) {
  const [open, setOpen] = createSignal(true);
  render(() => (
    <Dialog
      open={open()}
      onClose={() => setOpen(false)}
      closeable={closeable}
      title="dialog-title"
    >
      <p>body</p>
    </Dialog>
  ));
  return {
    open,
    setOpen,
    dialog: screen.getByRole('dialog', { hidden: true }),
  };
}

describe('Dialog', () => {
  it('opens and registers a modal back entry', () => {
    const { dialog } = renderDialog();
    expect(dialog).toHaveAttribute('open');
    expect(modalOpen()).toBe(true);
    expect(dialog).toHaveAccessibleName('dialog-title');
  });

  it('closes through the back stack', () => {
    const { open, dialog } = renderDialog();
    expect(dismiss()).toBe(true);
    expect(open()).toBe(false);
    expect(dialog).not.toHaveAttribute('open');
    expect(modalOpen()).toBe(false);
  });

  it('swallows back when not closeable', () => {
    const { open } = renderDialog(false);
    expect(dismiss()).toBe(true);
    expect(open()).toBe(true);
    expect(modalOpen()).toBe(true);
  });

  it('drops its back entry on unmount', () => {
    renderDialog();
    cleanup();
    expect(modalOpen()).toBe(false);
  });
});

describe('Floating', () => {
  it('renders in body, then inside the open dialog', () => {
    const { setOpen, dialog } = renderDialog();
    render(() => (
      <Floating>
        <span>floating</span>
      </Floating>
    ));
    expect(dialog).toContainElement(screen.getByText('floating'));
    setOpen(false);
    expect(dialog).not.toContainElement(screen.getByText('floating'));
  });
});

describe('confirm', () => {
  it('resolves true on confirm', async () => {
    render(() => <DialogHost />);
    const result = confirm({ title: 'confirm-title', confirm: 'confirm-ok' });
    fireEvent.click(await screen.findByText('confirm-ok'));
    await expect(result).resolves.toBe(true);
    expect(screen.queryByText('confirm-ok')).toBeNull();
  });

  it('resolves false on cancel and on back', async () => {
    render(() => <DialogHost />);
    const cancelled = confirm({
      title: 'confirm-title',
      confirm: 'confirm-ok',
    });
    fireEvent.click(await screen.findByText('dialog-cancel'));
    await expect(cancelled).resolves.toBe(false);

    const dismissed = confirm({
      title: 'confirm-title',
      confirm: 'confirm-ok',
    });
    await screen.findByText('confirm-ok');
    dismiss();
    await expect(dismissed).resolves.toBe(false);
  });
});
