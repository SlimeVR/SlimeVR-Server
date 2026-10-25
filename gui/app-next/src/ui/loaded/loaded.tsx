import { type Accessor, type JSX, Show } from 'solid-js';
import type { MessageArgs, MessageId } from '@/lib/l10n';
import { StatusState, type StatusSize } from '@/ui/status-state';

// Same shape as a solarxr resource (ui cannot import solarxr)
export interface LoadableResource<T> {
  value: Accessor<T | undefined>;
  state: Accessor<'never-loaded' | 'stale' | 'fresh'>;
}

export interface LoadedEmpty<T> {
  when: (data: T) => boolean;
  title: MessageId;
  description?: MessageId;
  args?: MessageArgs;
  action?: JSX.Element;
}

export interface LoadedProps<T> {
  of: LoadableResource<T>;
  size?: StatusSize;
  // Shown while never loaded instead of the loading slime, chosen per page
  skeleton?: JSX.Element;
  empty?: LoadedEmpty<T>;
  children: (data: Accessor<T>) => JSX.Element;
}

// Never-loaded shows the skeleton, else the loading slime. Stale keeps rendering the last value,
// controls disable themselves. Offline is the connection gate's screen.
export function Loaded<T>(props: LoadedProps<T>) {
  const data = () =>
    props.of.state() === 'never-loaded' ? undefined : props.of.value();

  return (
    <Show
      when={data()}
      fallback={
        props.skeleton ?? <StatusState kind="loading" size={props.size} />
      }
    >
      {(loaded) => (
        <Show
          when={props.empty?.when(loaded()) ? props.empty : undefined}
          fallback={props.children(loaded as Accessor<T>)}
        >
          {(empty) => (
            <StatusState
              kind="empty"
              size={props.size}
              title={empty().title}
              description={empty().description}
              args={empty().args}
              action={empty().action}
            />
          )}
        </Show>
      )}
    </Show>
  );
}
