/* eslint-disable no-restricted-imports -- dev demo registers raw elements, primitives come later */
import { createSignal, For, type JSX, onCleanup, Show } from 'solid-js';
import { canHandleBack, setBackFallback, useBack } from '@/lib/back-stack';
import { useShortcut } from '@/lib/keyboard';
import {
  type Direction,
  MotionGrid,
  MotionList,
  MotionRegion,
  MotionToolbar,
  motionActive,
  motionEditing,
  onBump,
  type MotionItemOptions,
  useMotionItem,
} from '@/ui/motion';

// Dev only playground for arrows, Ctrl/Alt+Arrow, Tab and gamepad

const CONTROL =
  'bg-surface-control hover:bg-surface-hover rounded-md px-md py-xs text-left';

function DemoButton(
  props: { children: JSX.Element; onClick?: () => void } & MotionItemOptions
) {
  let ref!: HTMLButtonElement;
  useMotionItem(() => ref, props);
  return (
    <button
      ref={ref}
      type="button"
      class={CONTROL}
      onClick={() => props.onClick?.()}
    >
      {props.children}
    </button>
  );
}

function DemoInput(props: { type: string; label: string } & MotionItemOptions) {
  let ref!: HTMLInputElement;
  useMotionItem(() => ref, props);
  return (
    <label class="flex items-center gap-sm">
      <span class="w-24 shrink-0">{props.label}</span>
      <input
        ref={ref}
        type={props.type}
        class="bg-surface-well rounded-md px-sm py-xs"
      />
    </label>
  );
}

// Pad: A enters edit mode, then left/right change the value. Keyboard: owns left/right.
function DemoDial() {
  let ref!: HTMLButtonElement;
  const [value, setValue] = createSignal(0);
  const { editing } = useMotionItem(() => ref, {
    owns: 'horizontal',
    edit: true,
  });
  function onKeyDown(e: KeyboardEvent) {
    if (e.key !== 'ArrowLeft' && e.key !== 'ArrowRight') return;
    e.preventDefault();
    setValue((v) => v + (e.key === 'ArrowLeft' ? -1 : 1));
  }
  return (
    <button ref={ref} type="button" class={CONTROL} onKeyDown={onKeyDown}>
      dial {value()} {editing() ? '(editing)' : '(pad: A to edit)'}
    </button>
  );
}

function Status() {
  const [bump, setBump] = createSignal<Direction | null>(null);
  const [shortcutHits, setShortcutHits] = createSignal(0);
  onCleanup(onBump(setBump));
  useShortcut('Mod+K', () => setShortcutHits((n) => n + 1));
  return (
    <div class="font-mono flex flex-wrap gap-lg">
      <span>active: {String(motionActive())}</span>
      <span>editing: {motionEditing() ? 'yes' : 'no'}</span>
      <span>last bump: {bump() ?? '-'}</span>
      <span>Mod+K hits: {shortcutHits()}</span>
    </div>
  );
}

// Inline stand-ins for overlays until dialogs exist. Closing puts focus back on the opener.
function DemoOverlay(props: {
  title: string;
  modal?: boolean;
  onClose: () => void;
  children?: JSX.Element;
}) {
  const opener = document.activeElement;
  function close() {
    props.onClose();
    if (opener instanceof HTMLElement) opener.focus();
  }
  useBack(close, {
    get modal() {
      return props.modal;
    },
  });
  return (
    <MotionRegion class="surface-overlay flex flex-col gap-sm rounded-lg p-md">
      <span>
        {props.title} {props.modal ? '(modal: Mod+K suspended)' : ''}
      </span>
      <MotionList class="flex flex-col gap-sm">
        {props.children}
        <DemoButton onClick={close}>close</DemoButton>
      </MotionList>
    </MotionRegion>
  );
}

function BackStackDemo() {
  const [page, setPage] = createSignal(1);
  const [panel, setPanel] = createSignal(false);
  const [dialog, setDialog] = createSignal(false);
  const [log, setLog] = createSignal<string[]>([]);

  function record(line: string) {
    setLog((list) => [line, ...list].slice(0, 5));
  }

  // Fake router history under the overlays: pad B goes back, Escape does not
  setBackFallback({
    can: () => page() > 1,
    back: () => {
      record(`history: page ${page()} -> ${page() - 1}`);
      setPage((p) => p - 1);
    },
  });
  onCleanup(() => setBackFallback(null));

  return (
    <div class="flex flex-col gap-sm">
      <span class="font-mono">
        page {page()}, can handle back: {String(canHandleBack())}
      </span>
      <MotionToolbar class="flex flex-wrap gap-sm">
        <DemoButton onClick={() => setPage((p) => p + 1)}>next page</DemoButton>
        <DemoButton onClick={() => setPanel(true)}>open panel</DemoButton>
      </MotionToolbar>
      <Show when={panel()}>
        <DemoOverlay
          title="panel"
          onClose={() => {
            record('panel closed');
            setPanel(false);
          }}
        >
          <DemoInput type="text" label="Escape here" owns="horizontal" />
          <DemoButton onClick={() => setDialog(true)}>
            open dialog on top
          </DemoButton>
        </DemoOverlay>
      </Show>
      <Show when={dialog()}>
        <DemoOverlay
          title="dialog"
          modal
          onClose={() => {
            record('dialog closed');
            setDialog(false);
          }}
        />
      </Show>
      <ol class="font-mono flex flex-col">
        <For each={log()}>{(line) => <li>{line}</li>}</For>
      </ol>
    </div>
  );
}

export function MotionDemo() {
  return (
    <div class="surface-page flex flex-col gap-md rounded-lg p-lg">
      <span class="text-section">motion</span>
      <Status />
      <div class="flex gap-lg">
        <MotionRegion class="surface-sidebar flex w-40 shrink-0 flex-col gap-sm rounded-lg p-md">
          <For each={['home', 'body', 'setup', 'settings']}>
            {(name) => <DemoButton>{name}</DemoButton>}
          </For>
        </MotionRegion>

        <MotionRegion class="flex flex-1 flex-col gap-lg">
          <MotionToolbar class="flex gap-sm">
            <For each={['bold', 'italic', 'underline', 'strike']}>
              {(name) => <DemoButton>{name}</DemoButton>}
            </For>
          </MotionToolbar>

          <MotionGrid class="grid grid-cols-3 gap-sm">
            <For each={Array.from({ length: 8 }, (_, i) => i + 1)}>
              {(n) => <DemoButton>card {n}</DemoButton>}
            </For>
          </MotionGrid>

          <MotionList class="flex flex-col gap-sm">
            <DemoInput type="text" label="text" owns="horizontal" />
            <DemoInput type="number" label="number" owns="all" />
            <DemoInput type="range" label="range" owns="horizontal" />
            <DemoDial />
          </MotionList>

          <BackStackDemo />

          <MotionList class="bg-surface-well flex h-40 flex-col gap-xs overflow-auto rounded-md p-sm">
            <For each={Array.from({ length: 20 }, (_, i) => i + 1)}>
              {(n) => <DemoButton>scroll row {n}</DemoButton>}
            </For>
          </MotionList>

          <MotionRegion
            trap
            class="surface-card flex flex-col gap-sm rounded-lg p-md"
          >
            <span>trapped region: moves bump, leave with Tab or the mouse</span>
            <MotionToolbar class="flex gap-sm">
              <DemoButton>ok</DemoButton>
              <DemoButton>cancel</DemoButton>
            </MotionToolbar>
          </MotionRegion>
        </MotionRegion>
      </div>
    </div>
  );
}
