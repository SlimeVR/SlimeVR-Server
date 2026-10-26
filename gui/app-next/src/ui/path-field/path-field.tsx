import { createUniqueId, Show } from 'solid-js';
import CloseIcon from '~icons/material-symbols/close-rounded';
import FileIcon from '~icons/material-symbols/draft-rounded';
import FolderIcon from '~icons/material-symbols/folder-rounded';
import { cx } from '@/lib/cx';
import { t, type MessageId } from '@/lib/l10n';
import { platform, type FileFilter } from '@/lib/platform';
import { TextButton } from '@/ui/button';
import { fieldBlocked, type FieldProps, fieldState, idList } from '@/ui/field';
import { MotionList, useMotionItem } from '@/ui/motion';
import { Skeleton } from '@/ui/skeleton';
import { TextTooltip } from '@/ui/tooltip';

export interface PathFieldProps extends FieldProps<string | null> {
  mode: 'open' | 'directory';
  placeholder: string;
  title?: string;
  filters?: FileFilter[];
  class?: string;
}

function PathFieldBody(props: PathFieldProps) {
  let el!: HTMLButtonElement;
  const tipId = createUniqueId();
  const state = () => fieldState(props);
  const reason = () =>
    state() === 'disabled' ? props.disabledReason : undefined;
  useMotionItem(() => el);

  async function pick() {
    if (fieldBlocked(state())) return;
    const path = await platform.pickFile?.({
      mode: props.mode,
      title: props.title,
      filters: props.filters,
      defaultPath: props.value ?? undefined,
    });
    if (path) props.onChange(path);
  }

  return (
    <>
      <button
        ref={el}
        type="button"
        id={props.id}
        aria-labelledby={props.labelledBy}
        aria-describedby={idList(props.describedBy, reason() && tipId)}
        aria-disabled={state() === 'disabled' ? 'true' : undefined}
        aria-invalid={state() === 'error' ? 'true' : undefined}
        class={cx(
          `
            flex min-h-control w-full items-center gap-sm rounded-md bg-surface-control pl-3
            text-left text-standard text-fg not-aria-disabled:hover:bg-surface-hover
            aria-disabled:cursor-not-allowed aria-disabled:opacity-50 aria-invalid:ring-1
            aria-invalid:ring-critical
          `,
          props.value ? 'pr-12' : 'pr-3'
        )}
        onClick={() => void pick()}
      >
        <Show
          when={state() !== 'loading'}
          fallback={<Skeleton class="w-1/3" />}
        >
          <span class="flex shrink-0 icon-md" aria-hidden="true">
            {props.mode === 'directory' ? <FolderIcon /> : <FileIcon />}
          </span>
          <Show
            when={props.value}
            fallback={
              <span class="min-w-0 grow truncate">{props.placeholder}</span>
            }
          >
            {(path) => (
              // rtl cuts the start of a long path, so the last part stays visible
              <span
                dir="rtl"
                title={path()}
                class="sentry-mask min-w-0 grow truncate text-left"
              >
                <bdi dir="ltr">{path()}</bdi>
              </span>
            )}
          </Show>
        </Show>
      </button>
      <Show when={state() !== 'loading' && props.value}>
        <span class="absolute inset-y-0 right-1.5 flex items-center">
          <TextButton
            label={
              props.mode === 'directory'
                ? 'file_input-clear_folder'
                : 'file_input-clear'
            }
            variant="ghost"
            size="small"
            iconOnly
            icon={<CloseIcon class="icon" />}
            disabled={state() === 'disabled'}
            onClick={() => props.onChange(null)}
          />
        </span>
      </Show>
      <TextTooltip id={tipId} anchor={() => el} text={reason()} />
    </>
  );
}

export function PathField(props: PathFieldProps) {
  return (
    <MotionList
      orientation="horizontal"
      entry="first"
      class={cx('relative w-full min-w-(--field-min,0px)', props.class)}
      inert={fieldState(props) === 'loading'}
    >
      <PathFieldBody {...props} />
    </MotionList>
  );
}

export interface TextPathFieldProps extends Omit<
  PathFieldProps,
  'placeholder' | 'title'
> {
  placeholder: MessageId;
  title?: MessageId;
}

export function TextPathField(props: TextPathFieldProps) {
  return (
    <PathField
      {...props}
      placeholder={t(props.placeholder)}
      title={props.title && t(props.title)}
    />
  );
}
