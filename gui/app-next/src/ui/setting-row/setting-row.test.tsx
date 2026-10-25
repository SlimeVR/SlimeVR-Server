import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { SettingRow } from '@/ui/setting-row';
import { SettingsGroup } from '@/ui/settings-group';
import { Toggle } from '@/ui/toggle';

describe('SettingRow', () => {
  afterEach(cleanup);

  it('names and describes the control', () => {
    render(() => (
      <SettingRow
        title="toast-saved"
        description="toast-not-saved"
        control={(w) => <Toggle value={false} onChange={() => {}} {...w} />}
      />
    ));
    const control = screen.getByRole('switch');
    expect(control).toHaveAccessibleName('toast-saved');
    expect(control).toHaveAccessibleDescription('toast-not-saved');
  });

  it('pressing the row presses the control', async () => {
    const onChange = vi.fn();
    render(() => (
      <SettingRow
        title="toast-saved"
        pressable
        control={(w) => <Toggle value={false} onChange={onChange} {...w} />}
      />
    ));
    await userEvent.setup().click(screen.getByText('toast-saved'));
    expect(onChange).toHaveBeenCalledExactlyOnceWith(true);
  });

  it('a row that is not disabled keeps the field disabled', () => {
    render(() => (
      <SettingRow
        title="toast-saved"
        control={(w) => (
          <Toggle value={false} onChange={() => {}} disabled {...w} />
        )}
      />
    ));
    expect(screen.getByRole('switch')).toHaveAttribute('aria-disabled', 'true');
  });

  it('children stay visible and inherit childrenDisabled', () => {
    render(() => (
      <SettingRow
        title="toast-saved"
        childrenDisabled
        control={(w) => <Toggle value={false} onChange={() => {}} {...w} />}
      >
        <SettingRow
          title="toast-not-saved"
          control={(w) => <Toggle value={false} onChange={() => {}} {...w} />}
        />
      </SettingRow>
    ));
    const [own, child] = screen.getAllByRole('switch');
    expect(own).not.toHaveAttribute('aria-disabled');
    expect(child).toHaveAccessibleName('toast-not-saved');
    expect(child).toHaveAttribute('aria-disabled', 'true');
  });

  it('disabled reasons reach nested controls', () => {
    render(() => (
      <SettingsGroup disabled disabledReason="dialog-cancel">
        <SettingRow
          title="toast-saved"
          control={(w) => <Toggle value={false} onChange={() => {}} {...w} />}
        />
        <SettingRow
          title="input-copy-done"
          childrenDisabled
          childrenDisabledReason="input-copy-failed"
        >
          <SettingRow
            title="toast-not-saved"
            control={(w) => <Toggle value={false} onChange={() => {}} {...w} />}
          />
        </SettingRow>
      </SettingsGroup>
    ));
    const [fromGroup, nested] = screen.getAllByRole('switch');
    expect(fromGroup).toHaveAccessibleDescription('dialog-cancel');
    // The group's reason wins over the row's own children reason
    expect(nested).toHaveAccessibleDescription('dialog-cancel');
  });

  it('childrenDisabledReason reaches the children', () => {
    render(() => (
      <SettingRow
        title="toast-saved"
        childrenDisabled
        childrenDisabledReason="input-copy-failed"
      >
        <SettingRow
          title="toast-not-saved"
          control={(w) => <Toggle value={false} onChange={() => {}} {...w} />}
        />
      </SettingRow>
    ));
    expect(screen.getByRole('switch')).toHaveAccessibleDescription(
      'input-copy-failed'
    );
  });

  it('rows inherit loading from the group', () => {
    render(() => (
      <SettingsGroup title="toast-saved" loading>
        <SettingRow
          title="toast-not-saved"
          control={(w) => <Toggle value onChange={() => {}} {...w} />}
        />
      </SettingsGroup>
    ));
    const control = screen.getByRole('switch', { hidden: true });
    expect(control).toHaveAttribute('inert');
    expect(screen.getByRole('region')).toHaveAccessibleName('toast-saved');
  });
});
