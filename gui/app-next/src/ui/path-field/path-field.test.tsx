import { cleanup, render, screen, waitFor } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { createSignal } from 'solid-js';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';
import { PathField } from '@/ui/path-field';

describe('PathField', () => {
  afterEach(cleanup);

  function renderField(picked: string | null, initial: string | null = null) {
    const pickFile = vi.fn(async () => picked);
    setPlatform(fakePlatform({ pickFile }));
    const [value, setValue] = createSignal(initial);
    render(() => (
      <PathField
        mode="directory"
        placeholder="Choose folder"
        value={value()}
        onChange={setValue}
      />
    ));
    return { pickFile, value };
  }

  it('picks a folder through the platform dialog', async () => {
    const { pickFile, value } = renderField('/data/bvh');
    await userEvent.click(
      screen.getByRole('button', { name: 'Choose folder' })
    );
    expect(pickFile).toHaveBeenCalledWith(
      expect.objectContaining({ mode: 'directory' })
    );
    await waitFor(() => expect(value()).toBe('/data/bvh'));
    expect(screen.getByText('/data/bvh')).toBeInTheDocument();
  });

  it('keeps the path when the dialog is canceled', async () => {
    const { value } = renderField(null, '/data/bvh');
    await userEvent.click(screen.getByText('/data/bvh'));
    expect(value()).toBe('/data/bvh');
  });

  it('clears the path', async () => {
    const { value } = renderField(null, '/data/bvh');
    await userEvent.click(
      screen.getByRole('button', { name: 'file_input-clear_folder' })
    );
    expect(value()).toBeNull();
    expect(screen.getByText('Choose folder')).toBeInTheDocument();
  });
});
