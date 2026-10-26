import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { createSignal } from 'solid-js';
import { afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { defineSettingsPage } from '@/lib/settings-def';
import {
  SettingFocus,
  type SettingFocusTarget,
  SettingRow,
} from '@/ui/setting-row';
import { SettingsGroup } from '@/ui/settings-group';
import { Toggle } from '@/ui/toggle';

const page = defineSettingsPage({
  path: 'test',
  title: 'toast-saved',
  icon: () => null,
  groups: {
    main: {
      title: 'toast-saved',
      rows: {
        saved: { title: 'toast-saved', description: 'toast-not-saved' },
        plain: { title: 'toast-saved' },
        notSaved: { title: 'toast-not-saved' },
        copyDone: { title: 'input-copy-done' },
      },
    },
  },
});
const { rows } = page.groups.main;

beforeAll(() => {
  Element.prototype.scrollIntoView = () => {};
});

describe('SettingRow', () => {
  afterEach(cleanup);

  it('names and describes the control from the declaration', () => {
    render(() => (
      <SettingRow
        setting={rows.saved}
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
        setting={rows.plain}
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
        setting={rows.plain}
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
        setting={rows.plain}
        childrenDisabled
        control={(w) => <Toggle value={false} onChange={() => {}} {...w} />}
      >
        <SettingRow
          setting={rows.notSaved}
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
          setting={rows.plain}
          control={(w) => <Toggle value={false} onChange={() => {}} {...w} />}
        />
        <SettingRow
          setting={rows.copyDone}
          childrenDisabled
          childrenDisabledReason="input-copy-failed"
        >
          <SettingRow
            setting={rows.notSaved}
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
        setting={rows.plain}
        childrenDisabled
        childrenDisabledReason="input-copy-failed"
      >
        <SettingRow
          setting={rows.notSaved}
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
      <SettingsGroup group={page.groups.main} loading>
        <SettingRow
          setting={rows.notSaved}
          control={(w) => <Toggle value onChange={() => {}} {...w} />}
        />
      </SettingsGroup>
    ));
    const control = screen.getByRole('switch', { hidden: true });
    expect(control).toHaveAttribute('inert');
    expect(screen.getByRole('region')).toHaveAccessibleName('toast-saved');
  });

  it('reset is a button only while modified', async () => {
    const [modified, setModified] = createSignal(false);
    const onReset = vi.fn();
    render(() => (
      <SettingRow
        setting={rows.plain}
        reset={{
          get modified() {
            return modified();
          },
          onReset,
        }}
      />
    ));
    expect(screen.queryByRole('button')).toBeNull();
    setModified(true);
    await userEvent
      .setup()
      .click(screen.getByRole('button', { name: 'settings-reset-single' }));
    expect(onReset).toHaveBeenCalledOnce();
  });

  it('scrolls to the targeted row each time it is targeted', () => {
    const scroll = vi.fn();
    Element.prototype.scrollIntoView = scroll;
    const [target, setTarget] = createSignal<SettingFocusTarget>();
    render(() => (
      <SettingFocus target={target()}>
        <SettingRow setting={rows.plain} />
        <SettingRow setting={rows.notSaved} />
      </SettingFocus>
    ));
    setTarget({ key: rows.notSaved.key });
    setTarget({ key: rows.notSaved.key });
    expect(scroll).toHaveBeenCalledTimes(2);
    setTarget({ key: 'test.main.unknown' });
    expect(scroll).toHaveBeenCalledTimes(2);
  });

  it('a bound row merges the field with the row state', () => {
    const onChange = vi.fn();
    render(() => (
      <SettingsGroup disabled disabledReason="dialog-cancel">
        <SettingRow
          bind={{
            setting: rows.saved,
            field: { value: true, onChange, loading: false },
            reset: { modified: true, onReset: () => {} },
          }}
          control={(f) => <Toggle {...f} />}
        />
      </SettingsGroup>
    ));
    const control = screen.getByRole('switch');
    expect(control).toHaveAccessibleName('toast-saved');
    expect(control).toHaveAttribute('aria-checked', 'true');
    expect(control).toHaveAttribute('aria-disabled', 'true');
    expect(control).toHaveAccessibleDescription(
      'toast-not-saved dialog-cancel'
    );
    // Disabled by the group, so no reset either
    expect(screen.queryByRole('button')).toBeNull();
  });

  it('a bound row takes its reset from the binding', () => {
    render(() => (
      <SettingRow
        bind={{
          setting: rows.saved,
          field: { value: true, onChange: () => {} },
          reset: { modified: true, onReset: () => {} },
        }}
        control={(f) => <Toggle {...f} />}
      />
    ));
    expect(
      screen.getByRole('button', { name: 'settings-reset-single' })
    ).toBeInTheDocument();
  });

  it('a disabled row has no reset button, even when modified', () => {
    render(() => (
      <SettingRow
        setting={rows.plain}
        disabled
        reset={{ modified: true, onReset: () => {} }}
      />
    ));
    expect(screen.queryByRole('button')).toBeNull();
  });
});
