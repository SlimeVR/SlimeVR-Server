import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { Checkbox } from '@/ui/checkbox';

describe('Checkbox', () => {
  afterEach(cleanup);

  it('follows the value prop, not the click', async () => {
    const onChange = vi.fn();
    render(() => (
      <Checkbox value={false} onChange={onChange} label="dialog-cancel" />
    ));
    const box = screen.getByRole('checkbox', { name: 'dialog-cancel' });
    await userEvent.setup().click(box);
    expect(onChange).toHaveBeenCalledWith(true);
    expect(box).not.toBeChecked();
  });
});
