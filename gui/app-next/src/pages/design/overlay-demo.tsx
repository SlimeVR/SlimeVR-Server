/* eslint-disable l10n/message-id -- dev demo uses raw strings, t() shows missing ids as-is */
import { createSignal, For, type JSX } from 'solid-js';
import { cx } from '@/lib/cx';
import { Button } from '@/ui/button';
import { confirm, openDialog, TitledDialog } from '@/ui/dialog';
import { Loaded } from '@/ui/loaded';
import { MotionList, MotionRegion, MotionToolbar } from '@/ui/motion';
import { Popover, type PopoverPlacement } from '@/ui/popover';
import {
  StatusState,
  type StatusKind,
  type StatusSize,
} from '@/ui/status-state';
import { showToast } from '@/ui/toast';
import { Toggle } from '@/ui/toggle';

function Section(props: { title: string; children: JSX.Element }) {
  return (
    <MotionRegion class="surface-section flex flex-col gap-md rounded-lg p-lg">
      <span class="text-section">{props.title}</span>
      {props.children}
    </MotionRegion>
  );
}

function DialogDemos() {
  const [basic, setBasic] = createSignal(false);
  const [locked, setLocked] = createSignal(false);
  const [long, setLong] = createSignal(false);
  const [outer, setOuter] = createSignal(false);
  const [inner, setInner] = createSignal(false);
  const [result, setResult] = createSignal('none yet');

  async function runConfirm(danger: boolean) {
    const ok = await confirm({
      title: danger ? 'Forget this tracker?' : 'Apply changes?',
      description: 'This is <b>description</b> text from confirm().',
      confirm: danger ? 'Forget' : 'Apply',
      danger,
    });
    setResult(ok ? 'confirmed' : 'cancelled');
  }

  async function runOpenDialog() {
    const picked = await openDialog<string>((close) => (
      <TitledDialog open onClose={() => close()} title="Pick a fruit">
        <MotionToolbar class="flex gap-sm">
          <For each={['apple', 'pear', 'plum']}>
            {(fruit) => <Button onClick={() => close(fruit)}>{fruit}</Button>}
          </For>
        </MotionToolbar>
      </TitledDialog>
    ));
    setResult(picked ?? 'dismissed');
  }

  return (
    <Section title="Dialogs">
      <MotionToolbar class="flex flex-wrap gap-sm">
        <Button onClick={() => setBasic(true)}>basic</Button>
        <Button onClick={() => setLocked(true)}>not closeable</Button>
        <Button onClick={() => setLong(true)}>long content</Button>
        <Button onClick={() => setOuter(true)}>nested</Button>
        <Button onClick={() => void runConfirm(false)}>confirm()</Button>
        <Button onClick={() => void runConfirm(true)}>confirm() danger</Button>
        <Button onClick={() => void runOpenDialog()}>openDialog()</Button>
      </MotionToolbar>
      <span>last result: {result()}</span>

      <TitledDialog
        open={basic()}
        onClose={() => setBasic(false)}
        title="Basic dialog"
      >
        <p>
          Escape, pad B, backdrop click, or drag the handle down on a narrow
          window.
        </p>
        <MotionToolbar class="flex gap-sm">
          <Button
            onClick={() =>
              showToast({ kind: 'success', message: 'Toast above a dialog' })
            }
          >
            toast
          </Button>
          <Button onClick={() => setBasic(false)}>close</Button>
        </MotionToolbar>
      </TitledDialog>

      <TitledDialog
        open={locked()}
        onClose={() => setLocked(false)}
        closeable={false}
        title="Not closeable"
      >
        <p>Escape, back and backdrop do nothing. Only the button closes it.</p>
        <MotionToolbar class="flex gap-sm">
          <Button onClick={() => setLocked(false)}>done</Button>
        </MotionToolbar>
      </TitledDialog>

      <TitledDialog
        open={long()}
        onClose={() => setLong(false)}
        title="Long content"
      >
        <MotionList class="flex flex-col gap-xs">
          <For each={Array.from({ length: 30 }, (_, i) => i + 1)}>
            {(i) => <Button>row {i}</Button>}
          </For>
        </MotionList>
      </TitledDialog>

      <TitledDialog
        open={outer()}
        onClose={() => setOuter(false)}
        title="Outer dialog"
      >
        <MotionToolbar class="flex gap-sm">
          <Button onClick={() => setInner(true)}>open inner</Button>
          <Button onClick={() => setOuter(false)}>close</Button>
        </MotionToolbar>
        <TitledDialog
          open={inner()}
          onClose={() => setInner(false)}
          title="Inner dialog"
        >
          <p>Back closes this one first.</p>
          <MotionToolbar class="flex gap-sm">
            <Button onClick={() => setInner(false)}>close inner</Button>
          </MotionToolbar>
        </TitledDialog>
      </TitledDialog>
    </Section>
  );
}

const PLACEMENTS: PopoverPlacement[] = [
  'bottom-start',
  'bottom',
  'bottom-end',
  'top-start',
  'top',
  'top-end',
  'left',
  'right',
];

function PopoverDemo(props: { placement: PopoverPlacement }) {
  const [open, setOpen] = createSignal(false);
  const [anchor, setAnchor] = createSignal<HTMLButtonElement>();
  return (
    <>
      <Button
        ref={(el) => setAnchor(el)}
        expanded={open()}
        onClick={() => setOpen((o) => !o)}
      >
        {props.placement}
      </Button>
      <Popover
        open={open()}
        onClose={() => setOpen(false)}
        anchor={anchor}
        placement={props.placement}
        class="surface-overlay m-xs max-h-[60dvh] rounded-md bg-surface p-sm shadow-lg"
      >
        <MotionList class="flex flex-col gap-xs">
          <For each={['first item', 'second item', 'third item']}>
            {(label) => <Button onClick={() => setOpen(false)}>{label}</Button>}
          </For>
        </MotionList>
      </Popover>
    </>
  );
}

function PopoverDemos() {
  const [inDialog, setInDialog] = createSignal(false);
  return (
    <Section title="Popovers (press outside, Escape or pad B closes)">
      <MotionToolbar class="flex flex-wrap gap-sm">
        <For each={PLACEMENTS}>
          {(placement) => <PopoverDemo placement={placement} />}
        </For>
        <Button onClick={() => setInDialog(true)}>popover in dialog</Button>
      </MotionToolbar>
      <span>Try them near the window edges for the flip fallbacks.</span>
      <TitledDialog
        open={inDialog()}
        onClose={() => setInDialog(false)}
        title="Popover inside a dialog"
      >
        <MotionToolbar class="flex gap-sm">
          <PopoverDemo placement="bottom-start" />
          <Button onClick={() => setInDialog(false)}>close</Button>
        </MotionToolbar>
      </TitledDialog>
    </Section>
  );
}

function ToastDemos() {
  let count = 0;
  return (
    <Section title="Toasts">
      <MotionToolbar class="flex flex-wrap gap-sm">
        <Button
          onClick={() =>
            showToast({ kind: 'success', message: 'Saved', key: 'demo-saved' })
          }
        >
          success, coalesced
        </Button>
        <Button
          onClick={() =>
            showToast({ kind: 'error', message: `Not saved (#${++count})` })
          }
        >
          error, each shown
        </Button>
        <Button
          onClick={() =>
            showToast({
              kind: 'success',
              message: 'Sticks around for 10s',
              durationMs: 10_000,
            })
          }
        >
          long
        </Button>
      </MotionToolbar>
    </Section>
  );
}

const KINDS: StatusKind[] = ['loading', 'error', 'empty', 'success'];
const SIZES: StatusSize[] = ['inline', 'panel'];

function StatusDemos() {
  const [glow, setGlow] = createSignal(false);
  return (
    <Section title="Status states">
      <MotionToolbar class="flex gap-sm">
        <Toggle label="glow" value={glow()} onChange={setGlow} />
      </MotionToolbar>
      <For each={SIZES}>
        {(size) => (
          <div class="grid grid-cols-2 gap-md md:grid-cols-4">
            <For each={KINDS}>
              {(kind) => (
                <div class="surface-card rounded-md">
                  <StatusState
                    kind={kind}
                    size={size}
                    glow={glow()}
                    title={`${kind} ${size}`}
                    description="Description with <b>markup</b>."
                  />
                </div>
              )}
            </For>
          </div>
        )}
      </For>
      <div class="surface-card flex h-80 flex-col rounded-md">
        <StatusState
          kind="empty"
          size="page"
          glow={glow()}
          title="page size"
          description="Fills its container."
        />
      </div>
    </Section>
  );
}

type FakeState = 'never-loaded' | 'stale' | 'fresh';

function LoadedDemo() {
  const [state, setState] = createSignal<FakeState>('never-loaded');
  const [items, setItems] = createSignal<string[] | undefined>(undefined);
  const [skeleton, setSkeleton] = createSignal(false);

  function set(next: FakeState, value?: string[]) {
    setState(next);
    if (value) setItems(value);
  }

  return (
    <Section title="Loaded">
      <MotionToolbar class="flex flex-wrap gap-sm">
        <Button onClick={() => set('never-loaded')}>never-loaded</Button>
        <Button onClick={() => set('fresh', ['alpha', 'beta', 'gamma'])}>
          fresh
        </Button>
        <Button onClick={() => set('stale')}>stale (keeps last value)</Button>
        <Button onClick={() => set('fresh', [])}>fresh, empty</Button>
        <Toggle
          label="skeleton while loading"
          value={skeleton()}
          onChange={setSkeleton}
        />
      </MotionToolbar>
      <span>state: {state()}</span>
      <div class="surface-card rounded-md">
        <Loaded
          of={{
            value: () => {
              const list = items();
              return list && { items: list };
            },
            state,
          }}
          empty={{
            when: (d) => d.items.length === 0,
            title: 'Nothing here',
            description: 'The list is empty.',
          }}
          skeleton={
            skeleton() ? (
              <div class="flex flex-col gap-xs p-lg">
                <For each={[1, 2, 3]}>
                  {() => (
                    <div class="h-lg w-1/3 animate-pulse rounded-sm bg-surface-control" />
                  )}
                </For>
              </div>
            ) : undefined
          }
        >
          {(data) => (
            <ul
              class={cx(
                'flex flex-col gap-xs p-lg',
                state() === 'stale' && 'opacity-60'
              )}
            >
              <For each={data().items}>{(item) => <li>{item}</li>}</For>
            </ul>
          )}
        </Loaded>
      </div>
    </Section>
  );
}

export function OverlayDemo() {
  return (
    <div class="flex flex-col gap-lg">
      <DialogDemos />
      <PopoverDemos />
      <ToastDemos />
      <StatusDemos />
      <LoadedDemo />
    </div>
  );
}
