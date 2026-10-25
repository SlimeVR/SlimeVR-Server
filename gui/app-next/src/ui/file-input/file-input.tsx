import { createSignal, createUniqueId, Show } from 'solid-js';
import CloseIcon from '~icons/material-symbols/close-rounded';
import FileIcon from '~icons/material-symbols/draft-rounded';
import UploadIcon from '~icons/material-symbols/upload-file-rounded';
import { cx } from '@/lib/cx';
import type { MessageId } from '@/lib/l10n';
import { TextButton } from '@/ui/button';
import { fieldBlocked, type FieldProps, fieldState, idList } from '@/ui/field';
import { Localized } from '@/ui/localized';
import { MotionList, useMotionItem } from '@/ui/motion';
import { Skeleton } from '@/ui/skeleton';
import { TextTooltip } from '@/ui/tooltip';

export interface FileInputProps extends FieldProps<File | null> {
  // Same syntax as the input `accept` attribute: MIME types, `type/*`, `.ext`
  accept: string;
  // Empty state text, `<u>` marks the browse word
  prompt?: MessageId;
  // Shown instead of the file name, e.g. the model name the server reports
  shownName?: string;
  class?: string;
}

// Drops bypass the picker filter, so they are checked here
export function acceptsFile(file: File, accept: string): boolean {
  const name = file.name.toLowerCase();
  return accept
    .split(',')
    .map((a) => a.trim().toLowerCase())
    .some((a) => {
      if (!a) return false;
      if (a.startsWith('.')) return name.endsWith(a);
      if (a.endsWith('/*')) return file.type.startsWith(a.slice(0, -1));
      return file.type === a;
    });
}

function FileInputBody(props: FileInputProps) {
  let el!: HTMLInputElement;
  const tipId = createUniqueId();
  const state = () => fieldState(props);
  const reason = () =>
    state() === 'disabled' ? props.disabledReason : undefined;
  const [dragging, setDragging] = createSignal(false);
  useMotionItem(() => el);

  const shown = () => props.shownName ?? props.value?.name;
  const filled = () => state() !== 'loading' && !dragging() && !!shown();

  function pick(file: File | undefined) {
    if (!file || fieldBlocked(state()) || !acceptsFile(file, props.accept))
      return;
    props.onChange(file);
  }

  function clear() {
    el.value = '';
    props.onChange(null);
  }

  function onDrop(e: DragEvent) {
    e.preventDefault();
    setDragging(false);
    pick(e.dataTransfer?.files[0]);
  }

  return (
    <>
      <div
        data-focus-ring
        class={cx(
          `
            relative flex w-full items-center gap-sm rounded-md border-2 border-line px-lg
            transition-colors has-aria-disabled:opacity-50 has-aria-invalid:border-critical
            has-[input:not([aria-disabled]):hover]:border-accent
          `,
          filled()
            ? 'min-h-control pr-14'
            : 'min-h-32 justify-center border-dashed',
          dragging() && 'border-accent bg-accent/10'
        )}
        onDragOver={(e) => e.preventDefault()}
        onDragEnter={(e) => {
          e.preventDefault();
          if (!fieldBlocked(state())) setDragging(true);
        }}
        onDragLeave={(e) => {
          if (!e.currentTarget.contains(e.relatedTarget as Node | null))
            setDragging(false);
        }}
        onDrop={onDrop}
      >
        <Show when={state() === 'loading'}>
          <Skeleton class="w-1/3" />
        </Show>
        <Show when={state() !== 'loading'}>
          <span class="flex shrink-0 icon-lg" aria-hidden="true">
            {filled() ? <FileIcon /> : <UploadIcon />}
          </span>
        </Show>
        <Show
          when={filled()}
          fallback={
            <span class={cx(state() === 'loading' && 'hidden')}>
              <Localized
                id={props.prompt ?? 'tips-file_select'}
                elems={{ u: (text) => <span class="underline">{text}</span> }}
              />
            </span>
          }
        >
          <span class="min-w-0 truncate">{shown()}</span>
        </Show>
        <input
          ref={el}
          type="file"
          id={props.id}
          accept={props.accept}
          aria-labelledby={props.labelledBy}
          aria-describedby={idList(props.describedBy, reason() && tipId)}
          aria-disabled={state() === 'disabled' ? 'true' : undefined}
          aria-invalid={state() === 'error' ? 'true' : undefined}
          class="
            absolute inset-0 size-full cursor-pointer opacity-0 outline-none
            aria-disabled:cursor-not-allowed
          "
          onClick={(e) => {
            if (fieldBlocked(state())) e.preventDefault();
          }}
          onChange={(e) => pick(e.currentTarget.files?.[0])}
        />
      </div>
      <Show when={filled()}>
        <span class="absolute inset-y-0 right-1.5 flex items-center">
          <TextButton
            variant="ghost"
            size="small"
            iconOnly
            icon={<CloseIcon />}
            label="file_input-clear"
            disabled={state() === 'disabled'}
            onClick={clear}
          />
        </span>
      </Show>
      <TextTooltip id={tipId} anchor={() => el} text={reason()} />
    </>
  );
}

export function FileInput(props: FileInputProps) {
  return (
    <MotionList
      orientation="horizontal"
      entry="first"
      class={cx('relative', props.class)}
      inert={fieldState(props) === 'loading'}
    >
      <FileInputBody {...props} />
    </MotionList>
  );
}
