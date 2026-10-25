import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { Toggle } from '@/ui/toggle';

describe('Toggle', () => {
  afterEach(cleanup);

  it('a label names the switch and toggles it', async () => {
    const onChange = vi.fn();
    render(() => (
      <Toggle value={false} onChange={onChange} label="dialog-cancel" />
    ));
    expect(screen.getByRole('switch')).toHaveAccessibleName('dialog-cancel');
    await userEvent.setup().click(screen.getByText('dialog-cancel'));
    expect(onChange).toHaveBeenCalledExactlyOnceWith(true);
  });
});
