/* eslint-disable l10n/message-id -- dev demo uses raw strings, t() shows missing ids as-is */
import { createSignal, For, type JSX } from 'solid-js';
import GearIcon from '~icons/material-symbols/settings-rounded';
import { cx } from '@/lib/cx';
import { formats } from '@/lib/format';
import { TextButton, type ButtonSize, type ButtonVariant } from '@/ui/button';
import { Checkbox } from '@/ui/checkbox';
import { CopyButton } from '@/ui/copy-button';
import type { FieldProps } from '@/ui/field';
import { FileInput } from '@/ui/file-input';
import { KeybindRecorder } from '@/ui/keybind-recorder';
import { MotionGrid, MotionRegion } from '@/ui/motion';
import { NumberField } from '@/ui/number-field';
import { type ChoiceOption, RadioGroup, Segmented } from '@/ui/radio-group';
import {
  type DotOption,
  DotMultiSelect,
  MultiSelect,
  Select,
  type SelectOption,
} from '@/ui/select';
import { TextField } from '@/ui/text-field';
import { Toggle } from '@/ui/toggle';

type DemoState = 'default' | 'disabled' | 'error' | 'loading';

const STATES: readonly DemoState[] = [
  'default',
  'disabled',
  'error',
  'loading',
];

type DemoField = Pick<
  FieldProps<unknown>,
  'loading' | 'disabled' | 'error' | 'disabledReason'
>;

const STATE_FIELD: Record<DemoState, DemoField> = {
  default: {},
  disabled: { disabled: true, disabledReason: 'Disabled because of a reason' },
  error: { error: true },
  loading: { loading: true },
};

type Surface = 'app' | 'sidebar' | 'page' | 'section' | 'card' | 'overlay';

const SURFACE_CLASS: Record<Surface, string> = {
  app: 'surface-app',
  sidebar: 'surface-sidebar',
  page: 'surface-page',
  section: 'surface-section',
  card: 'surface-card',
  overlay: 'surface-overlay',
};

const SURFACES: ChoiceOption<Surface>[] = [
  { value: 'app', label: 'app' },
  { value: 'sidebar', label: 'sidebar' },
  { value: 'page', label: 'page' },
  { value: 'section', label: 'section' },
  { value: 'card', label: 'card' },
  { value: 'overlay', label: 'overlay' },
];

const VARIANTS: readonly ButtonVariant[] = [
  'primary',
  'secondary',
  'ghost',
  'danger',
];

const OPTIONS: ChoiceOption<string>[] = [
  { value: 'a', label: 'Alpha', description: 'First option' },
  { value: 'b', label: 'Beta', description: 'Second option' },
  { value: 'c', label: 'Gamma' },
];

const SIZES: readonly ButtonSize[] = ['default', 'small'];

const SOURCES: DotOption<string>[] = [
  { value: 'server', text: 'Server', color: '#a78bfa', detail: '12' },
  { value: 'gui', text: 'GUI', color: '#34d399', detail: '3' },
  { value: 'driver', text: 'Driver', color: '#fbbf24', detail: '--' },
  { value: 'feeder', text: 'Feeder', color: '#f472b6', detail: '--' },
];

const PORTS: SelectOption<string>[] = [
  { value: 'COM3', text: 'COM3' },
  { value: 'COM4', text: 'COM4 (CH340)' },
  { value: 'ttyUSB0', text: '/dev/ttyUSB0' },
  { value: 'ttyACM0', text: '/dev/ttyACM0' },
];

function Row(props: {
  title: string;
  children: (state: DemoState) => JSX.Element;
}) {
  return (
    <div class="flex flex-col gap-sm">
      <span class="text-section">{props.title}</span>
      <MotionGrid entry="nearest" class="grid grid-cols-4 items-start gap-md">
        <For each={STATES}>
          {(state) => (
            <div class="flex flex-col gap-xs">
              <span class="text-fg-muted">{state}</span>
              {props.children(state)}
            </div>
          )}
        </For>
      </MotionGrid>
    </div>
  );
}

export function PrimitivesDemo() {
  const [on, setOn] = createSignal(true);
  const [text, setText] = createSignal('');
  const [secret, setSecret] = createSignal('hunter2');
  const [choice, setChoice] = createSignal('a');
  const [seconds, setSeconds] = createSignal(0.25);
  const [percent, setPercent] = createSignal(0.5);
  const [surface, setSurface] = createSignal<Surface>('section');
  const [port, setPort] = createSignal<string | undefined>(undefined);
  const [ports, setPorts] = createSignal<string[]>(['COM3']);
  const [sources, setSources] = createSignal<string[]>(['server', 'gui']);
  const [file, setFile] = createSignal<File | null>(null);
  const [keys, setKeys] = createSignal<string[]>(['CTRL', 'ALT', 'SHIFT', 'Y']);

  return (
    <MotionRegion
      class={cx(
        'flex flex-col gap-lg rounded-lg p-lg',
        SURFACE_CLASS[surface()]
      )}
    >
      <div class="flex flex-wrap items-center justify-between gap-md">
        <span class="text-title">Primitives</span>
        <Segmented value={surface()} onChange={setSurface} options={SURFACES} />
      </div>
      <For each={VARIANTS}>
        {(variant) => (
          <Row title={`Button ${variant}`}>
            {(state) => (
              <div class="flex items-center gap-sm">
                <For each={SIZES}>
                  {(size) => (
                    <>
                      <TextButton
                        variant={variant}
                        size={size}
                        label="Press"
                        disabled={state === 'disabled'}
                        disabledReason="Disabled because of a reason"
                        loading={state === 'loading'}
                      />
                      <TextButton
                        variant={variant}
                        size={size}
                        label="Settings"
                        icon={<GearIcon />}
                        iconOnly
                        disabled={state === 'disabled'}
                        loading={state === 'loading'}
                      />
                    </>
                  )}
                </For>
              </div>
            )}
          </Row>
        )}
      </For>
      <Row title="Toggle">
        {(state) => (
          <div class="flex flex-col items-start gap-sm">
            <Toggle {...STATE_FIELD[state]} value={on()} onChange={setOn} />
            <Toggle
              {...STATE_FIELD[state]}
              value={on()}
              onChange={setOn}
              label="Enabled"
            />
          </div>
        )}
      </Row>
      <Row title="Checkbox">
        {(state) => (
          <Checkbox
            {...STATE_FIELD[state]}
            value={on()}
            onChange={setOn}
            label="Enabled"
          />
        )}
      </Row>
      <Row title="TextField">
        {(state) => (
          <div class="flex flex-col gap-sm">
            <TextField
              {...STATE_FIELD[state]}
              value={text()}
              onChange={setText}
              label="Name"
              placeholder="Type a name"
            />
            <TextField
              {...STATE_FIELD[state]}
              value={secret()}
              onChange={setSecret}
              label="Password"
              type="password"
            />
          </div>
        )}
      </Row>
      <Row title="NumberField">
        {(state) => (
          <div class="flex flex-col gap-sm">
            <NumberField
              {...STATE_FIELD[state]}
              value={seconds()}
              onChange={setSeconds}
              min={0}
              max={3}
              step={0.05}
              format={formats.seconds}
            />
            <NumberField
              {...STATE_FIELD[state]}
              value={percent()}
              onChange={setPercent}
              min={0.1}
              max={1}
              step={0.1}
              format={formats.percent}
            />
          </div>
        )}
      </Row>
      <Row title="Select">
        {(state) => (
          <div class="flex flex-col gap-sm">
            <Select
              {...STATE_FIELD[state]}
              value={port()}
              onChange={setPort}
              options={PORTS}
              label="Port"
              placeholder="Pick a port"
            />
            <MultiSelect
              {...STATE_FIELD[state]}
              value={ports()}
              onChange={setPorts}
              options={PORTS}
              placeholder="Pick ports"
            />
            <DotMultiSelect
              {...STATE_FIELD[state]}
              value={sources()}
              onChange={setSources}
              options={SOURCES}
              label="Sources"
              allLabel="All sources"
            />
          </div>
        )}
      </Row>
      <Row title="FileInput">
        {(state) => (
          <FileInput
            {...STATE_FIELD[state]}
            value={file()}
            onChange={setFile}
            accept=".vrm, .glb, .gltf"
          />
        )}
      </Row>
      <Row title="KeybindRecorder">
        {(state) => (
          <KeybindRecorder
            {...STATE_FIELD[state]}
            value={keys()}
            onChange={setKeys}
          />
        )}
      </Row>
      <Row title="CopyButton">
        {(state) => (
          <div class="flex items-center gap-sm">
            <For each={SIZES}>
              {(size) => (
                <>
                  <CopyButton
                    text="SlimeVR"
                    label="Copy"
                    size={size}
                    disabled={state === 'disabled'}
                    disabledReason="Disabled because of a reason"
                  />
                  <CopyButton
                    text="SlimeVR"
                    label="Copy"
                    iconOnly
                    size={size}
                    disabled={state === 'disabled'}
                  />
                </>
              )}
            </For>
          </div>
        )}
      </Row>
      <Row title="Segmented">
        {(state) => (
          <Segmented
            {...STATE_FIELD[state]}
            value={choice()}
            onChange={setChoice}
            options={OPTIONS}
          />
        )}
      </Row>
      <Row title="RadioGroup">
        {(state) => (
          <RadioGroup
            {...STATE_FIELD[state]}
            value={choice()}
            onChange={setChoice}
            options={OPTIONS}
          />
        )}
      </Row>
    </MotionRegion>
  );
}
