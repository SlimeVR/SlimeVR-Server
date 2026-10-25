import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent, { type UserEvent } from '@testing-library/user-event';
import axe from 'axe-core';
import { createSignal, type JSX, mergeProps } from 'solid-js';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { Checkbox } from '@/ui/checkbox';
import type { FieldProps } from '@/ui/field';
import { type ChoiceOption, RadioGroup, Segmented } from '@/ui/radio-group';
import { TextField } from '@/ui/text-field';
import { Toggle } from '@/ui/toggle';

interface FieldContract<T> {
  name: string;
  render: (field: FieldProps<T>) => JSX.Element;
  values: readonly [T, T];
  edit: (control: HTMLElement, user: UserEvent) => Promise<void>;
  control: () => HTMLElement;
  host?: () => HTMLElement;
}

const LABEL = 'Contract label';
const MESSAGE = 'Contract message';
const REASON = 'dialog-cancel';

function mount<T>(
  contract: FieldContract<T>,
  overrides: Partial<FieldProps<T>> = {}
) {
  const onChange = vi.fn<(value: T) => void>();
  const [value, setValue] = createSignal<T>(contract.values[0]);
  const field = mergeProps(
    {
      get value() {
        return value();
      },
      onChange(next: T) {
        onChange(next);
        setValue(() => next);
      },
      labelledBy: 'contract-label',
      describedBy: 'contract-message',
    },
    overrides
  );
  const { container } = render(() => (
    <div>
      <span id="contract-label">{LABEL}</span>
      <span id="contract-message">{MESSAGE}</span>
      {contract.render(field)}
    </div>
  ));
  const control = contract.control();
  const host = contract.host?.() ?? control;
  return { container, control, host, onChange, value };
}

async function axeViolations(container: HTMLElement): Promise<string[]> {
  const results = await axe.run(container, {
    rules: { 'color-contrast': { enabled: false }, region: { enabled: false } },
  });
  return results.violations.map((v) => `${v.id}: ${v.help}`);
}

function testFieldContract<T>(contract: FieldContract<T>) {
  describe(`${contract.name} field contract`, () => {
    afterEach(cleanup);

    it('commits edits through onChange', async () => {
      const { control, onChange, value } = mount(contract);
      await contract.edit(control, userEvent.setup());
      expect(onChange).toHaveBeenLastCalledWith(contract.values[1]);
      expect(value()).toEqual(contract.values[1]);
    });

    it('is named by labelledBy and described by describedBy', () => {
      const { host } = mount(contract);
      expect(host).toHaveAccessibleName(LABEL);
      expect(host).toHaveAccessibleDescription(MESSAGE);
    });

    it('disabled: aria-disabled, focusable, ignores edits', async () => {
      const { control, host, onChange } = mount(contract, { disabled: true });
      expect(host).toHaveAttribute('aria-disabled', 'true');
      control.focus();
      expect(control).toHaveFocus();
      await contract.edit(control, userEvent.setup());
      expect(onChange).not.toHaveBeenCalled();
    });

    it('disabled reason joins the description', () => {
      const { host } = mount(contract, {
        disabled: true,
        disabledReason: REASON,
      });
      expect(host).toHaveAccessibleDescription(`${MESSAGE} ${REASON}`);
    });

    it('error sets aria-invalid', () => {
      const { host } = mount(contract, { error: 'bad' });
      expect(host).toHaveAttribute('aria-invalid', 'true');
    });

    it('disabled beats error', () => {
      const { host } = mount(contract, { disabled: true, error: true });
      expect(host).toHaveAttribute('aria-disabled', 'true');
      expect(host).not.toHaveAttribute('aria-invalid');
    });

    it('loading is inert and beats disabled and error', async () => {
      const { control, host, onChange } = mount(contract, {
        loading: true,
        disabled: true,
        error: true,
      });
      expect(control.closest('[inert]')).not.toBeNull();
      expect(host).not.toHaveAttribute('aria-disabled');
      expect(host).not.toHaveAttribute('aria-invalid');
      await contract.edit(control, userEvent.setup());
      expect(onChange).not.toHaveBeenCalled();
    });

    it('has no axe violations in its states', async () => {
      for (const overrides of [{}, { error: true }, { disabled: true }]) {
        const { container } = mount(contract, overrides);
        expect(await axeViolations(container)).toEqual([]);
        cleanup();
      }
    });
  });
}

const OPTIONS: ChoiceOption<number>[] = [
  { value: 1, label: 'dialog-cancel' },
  { value: 2, label: 'status-loading' },
  { value: 3, label: 'toast-saved' },
];

testFieldContract<boolean>({
  name: 'Toggle',
  render: (field) => <Toggle {...field} />,
  values: [false, true],
  edit: (control, user) => user.click(control),
  control: () => screen.getByRole('switch'),
});

testFieldContract<boolean>({
  name: 'Checkbox',
  render: (field) => <Checkbox {...field} />,
  values: [false, true],
  edit: (control, user) => user.click(control),
  control: () => screen.getByRole('checkbox'),
});

for (const [name, Group] of [
  ['RadioGroup', RadioGroup],
  ['Segmented', Segmented],
] as const) {
  testFieldContract<number>({
    name,
    render: (field) => <Group {...field} options={OPTIONS} />,
    values: [1, 2],
    edit: (_, user) => user.click(screen.getAllByRole('radio')[1]!),
    control: () => screen.getAllByRole('radio')[0]!,
    host: () => screen.getByRole('radiogroup'),
  });
}

testFieldContract<string>({
  name: 'TextField',
  render: (field) => <TextField {...field} />,
  values: ['', 'b'],
  edit: async (control, user) => {
    await user.type(control, 'b');
    await user.tab();
  },
  control: () => screen.getByRole('textbox'),
});
