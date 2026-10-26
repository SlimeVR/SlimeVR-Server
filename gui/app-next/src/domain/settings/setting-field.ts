import type { Accessor } from 'solid-js';
import { saveSettings, type SaveOptions } from '@/domain/settings/save-settings';
import type { SettingDef } from '@/lib/settings-def';
import type { Fields, Settings } from '@/solarxr/settings';
import type { Field } from '@/ui/field';
import type { SettingBinding, SettingReset } from '@/ui/setting-row';

function fieldsOf<C>(settings: Settings<unknown, C>) {
  return settings.value() as Partial<Fields<C>> | undefined;
}

// Live autosave field over one settings key. Loading until the first response,
// disabled while the values are stale (disconnected). The value is undefined
// while loading, primitives hide it then
export function settingField<C, K extends keyof Fields<C>>(
  settings: Settings<unknown, C>,
  key: K,
  options?: SaveOptions
): Field<NonNullable<Fields<C>[K]>> {
  return {
    get value() {
      return fieldsOf(settings)?.[key] as NonNullable<Fields<C>[K]>;
    },
    onChange(value) {
      const patch: Partial<Fields<C>> = {};
      patch[key] = value;
      saveSettings(settings, patch, options);
    },
    get loading() {
      return settings.state() === 'never-loaded';
    },
    get disabled() {
      return settings.state() === 'stale';
    },
  };
}

// Reset of one or more keys in a single save. Modified while any key differs
// from its default, never before both sides loaded
export function settingsReset<C, K extends keyof Fields<C>>(
  settings: Settings<unknown, C>,
  defaults: Accessor<Partial<Fields<C>> | null | undefined>,
  keys: readonly K[]
): SettingReset {
  return {
    get modified() {
      const current = fieldsOf(settings);
      const fallback = defaults();
      if (!current || !fallback) return false;
      return keys.some((key) => current[key] !== fallback[key]);
    },
    onReset() {
      const fallback = defaults();
      if (!fallback) return;
      const patch: Partial<Fields<C>> = {};
      for (const key of keys) patch[key] = fallback[key];
      saveSettings(settings, patch);
    },
  };
}

// Per page: bind the settings and their defaults once, then one binding per
// declared row. The row name is the settings key
export function settingRows<C>(
  settings: Settings<unknown, C>,
  defaults: Accessor<Partial<Fields<C>> | null | undefined>
) {
  return <K extends keyof Fields<C> & string>(
    setting: SettingDef<K>
  ): SettingBinding<NonNullable<Fields<C>[K]>> => ({
    setting,
    field: settingField(settings, setting.name),
    reset: settingsReset(settings, defaults, [setting.name]),
  });
}
