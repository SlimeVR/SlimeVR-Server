import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { Pressable } from '@/ui/pressable';

describe('Pressable', () => {
  afterEach(cleanup);

  it('is a button named by its content and fires onClick', async () => {
    const onClick = vi.fn();
    render(() => (
      <Pressable onClick={onClick}>
        <span>Left foot</span>
      </Pressable>
    ));
    await userEvent
      .setup()
      .click(screen.getByRole('button', { name: 'Left foot' }));
    expect(onClick).toHaveBeenCalledOnce();
  });

  it('reflects pressed as aria-pressed', () => {
    render(() => (
      <Pressable onClick={() => {}} pressed label="Tracker">
        <span />
      </Pressable>
    ));
    expect(screen.getByRole('button', { name: 'Tracker' })).toHaveAttribute(
      'aria-pressed',
      'true'
    );
  });

  it('disabled stays focusable and ignores presses', async () => {
    const onClick = vi.fn();
    render(() => (
      <Pressable onClick={onClick} disabled label="Tracker">
        <span />
      </Pressable>
    ));
    const button = screen.getByRole('button', { name: 'Tracker' });
    await userEvent.setup().click(button);
    expect(onClick).not.toHaveBeenCalled();
    expect(button).toHaveAttribute('aria-disabled', 'true');
    expect(button).not.toBeDisabled();
  });
});
