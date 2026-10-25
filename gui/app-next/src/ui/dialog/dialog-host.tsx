import { createSignal, For, type JSX, Show } from 'solid-js';
import type { MessageArgs, MessageId } from '@/lib/l10n';
import { TextButton } from '@/ui/button';
import { Dialog } from './dialog';
import { Localized } from '@/ui/localized';
import { MotionToolbar } from '@/ui/motion';

interface HostEntry {
  render: () => JSX.Element;
}

const [entries, setEntries] = createSignal<readonly HostEntry[]>([]);

// App-wide dialogs opened by services. Page dialogs render <Dialog> in place.
export function openDialog<T>(
  render: (close: (result?: T) => void) => JSX.Element
): Promise<T | undefined> {
  return new Promise((resolve) => {
    const entry: HostEntry = { render: () => render(close) };
    function close(result?: T) {
      setEntries((list) => list.filter((e) => e !== entry));
      resolve(result);
    }
    setEntries((list) => [...list, entry]);
  });
}

export interface ConfirmOptions {
  title: MessageId;
  description?: MessageId;
  args?: MessageArgs;
  confirm: MessageId;
  cancel?: MessageId;
  danger?: boolean;
}

// Resolves false on cancel, Escape, back or backdrop
export async function confirm(options: ConfirmOptions): Promise<boolean> {
  const result = await openDialog<boolean>((close) => (
    <Dialog open onClose={() => close(false)} title={options.title}>
      <Show when={options.description}>
        {(description) => (
          <p>
            <Localized id={description()} args={options.args} />
          </p>
        )}
      </Show>
      <MotionToolbar entry="first" class="flex justify-end gap-sm">
        <TextButton
          label={options.cancel ?? 'dialog-cancel'}
          variant="secondary"
          onClick={() => close(false)}
        />
        <TextButton
          label={options.confirm}
          variant={options.danger ? 'danger' : 'primary'}
          onClick={() => close(true)}
        />
      </MotionToolbar>
    </Dialog>
  ));
  return result ?? false;
}

// Mount once inside MotionRoot
export function DialogHost() {
  return <For each={entries()}>{(entry) => entry.render()}</For>;
}
