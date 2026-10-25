import { createSignal, For, type JSX, Show } from 'solid-js';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { Dialog } from '@/ui/dialog';
import { Localized } from '@/ui/localized';
import { MotionToolbar, useMotionItem } from '@/ui/motion';

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

type HostButtonVariant = 'secondary' | 'primary' | 'danger';

// Stand-in until the Button primitive exists
function HostButton(props: {
  label: MessageId;
  variant: HostButtonVariant;
  onPress: () => void;
}) {
  let el!: HTMLButtonElement;
  useMotionItem(() => el);
  return (
    <button
      ref={el}
      type="button"
      onClick={() => props.onPress()}
      class="rounded-md px-lg py-sm"
      classList={{
        'bg-surface-control hover:bg-surface-hover':
          props.variant === 'secondary',
        'bg-accent text-on-accent hover:bg-accent-hover':
          props.variant === 'primary',
        'bg-critical text-on-accent': props.variant === 'danger',
      }}
    >
      {t(props.label)}
    </button>
  );
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
        <HostButton
          label={options.cancel ?? 'dialog-cancel'}
          variant="secondary"
          onPress={() => close(false)}
        />
        <HostButton
          label={options.confirm}
          variant={options.danger ? 'danger' : 'primary'}
          onPress={() => close(true)}
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
