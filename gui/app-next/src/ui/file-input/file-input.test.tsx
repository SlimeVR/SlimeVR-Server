import { cleanup, fireEvent, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { createSignal } from 'solid-js';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { acceptsFile, FileInput } from '@/ui/file-input';

const ACCEPT = 'model/gltf-binary, .vrm, image/*';

describe('acceptsFile', () => {
  it('matches extensions, exact MIME types and wildcards', () => {
    expect(acceptsFile(new File([], 'Avatar.VRM'), ACCEPT)).toBe(true);
    expect(
      acceptsFile(new File([], 'a.glb', { type: 'model/gltf-binary' }), ACCEPT)
    ).toBe(true);
    expect(
      acceptsFile(new File([], 'a.png', { type: 'image/png' }), ACCEPT)
    ).toBe(true);
    expect(
      acceptsFile(new File([], 'a.txt', { type: 'text/plain' }), ACCEPT)
    ).toBe(false);
  });
});

describe('FileInput', () => {
  afterEach(cleanup);

  function renderInput(shownName?: string) {
    const onChange = vi.fn<(file: File | null) => void>();
    const [value, setValue] = createSignal<File | null>(null);
    const { container } = render(() => (
      <FileInput
        value={value()}
        onChange={(file) => {
          onChange(file);
          setValue(file);
        }}
        accept={ACCEPT}
        shownName={shownName}
      />
    ));
    const input =
      container.querySelector<HTMLInputElement>('input[type="file"]')!;
    return { onChange, input, container };
  }

  it('shows the prompt, then the picked file name', async () => {
    const { input, container } = renderInput();
    expect(container).toHaveTextContent('tips-file_select');
    await userEvent.setup().upload(input, new File([], 'avatar.vrm'));
    expect(container).toHaveTextContent('avatar.vrm');
  });

  it('prefers shownName over the file name', () => {
    const { container } = renderInput('My model');
    expect(container).toHaveTextContent('My model');
  });

  it('clear resets to null', async () => {
    const { input, onChange } = renderInput();
    const user = userEvent.setup();
    await user.upload(input, new File([], 'avatar.vrm'));
    await user.click(screen.getByRole('button', { name: 'file_input-clear' }));
    expect(onChange).toHaveBeenLastCalledWith(null);
  });

  it('ignores dropped files that do not match accept', () => {
    const { input, onChange } = renderInput();
    const box = input.parentElement!;
    const files = [new File([], 'notes.txt', { type: 'text/plain' })];
    fireEvent.drop(box, { dataTransfer: { files } });
    expect(onChange).not.toHaveBeenCalled();
    const good = [new File([], 'avatar.vrm')];
    fireEvent.drop(box, { dataTransfer: { files: good } });
    expect(onChange).toHaveBeenCalledExactlyOnceWith(good[0]);
  });
});
