/* eslint-disable l10n/message-id -- dev demo uses raw strings, t() shows missing ids as-is */
import { createSignal, For, type JSX } from 'solid-js';
import GearIcon from '~icons/material-symbols/settings-rounded';
import { cx } from '@/lib/cx';
import { Button, type ButtonVariant } from '@/ui/button';
import { Checkbox } from '@/ui/checkbox';
import type { FieldProps } from '@/ui/field';
import { MotionGrid, MotionRegion } from '@/ui/motion';
import { type ChoiceOption, RadioGroup, Segmented } from '@/ui/radio-group';
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
  const [surface, setSurface] = createSignal<Surface>('section');

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
              <div class="flex gap-sm">
                <Button
                  variant={variant}
                  label="Press"
                  disabled={state === 'disabled'}
                  disabledReason="Disabled because of a reason"
                  loading={state === 'loading'}
                />
                <Button
                  variant={variant}
                  label="Settings"
                  icon={<GearIcon />}
                  iconOnly
                  size="small"
                  disabled={state === 'disabled'}
                />
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
