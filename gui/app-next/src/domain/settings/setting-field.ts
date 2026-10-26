import type { Accessor } from 'solid-js';
import { saveSettings, type SaveOptions } from '@/domain/settings/save-settings';
import type { SettingDef } from '@/lib/settings-def';
import type { Pref } from '@/lib/storage';
import type { Fields, Settings } from '@/solarxr/settings';
import type { Field } from '@/ui/field';
import type { SettingBinding, SettingReset } from '@/ui/setting-row';

type Defaults<C> = Accessor<Partial<Fields<C>> | null | undefined>;

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
  defaults: Defaults<C>,
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

// Keys holding a nested table (skeleton toggles, ratios, filtering)
type TableKey<C> = {
  [K in keyof Fields<C>]: NonNullable<Fields<C>[K]> extends object ? K : never;
}[keyof Fields<C>];

type Table<C, S extends keyof Fields<C>> = Fields<NonNullable<Fields<C>[S]>>;

type RowBinder<F> = <K extends keyof F & string>(
  setting: SettingDef<K>
) => SettingBinding<NonNullable<F[K]>>;

// Copy of an object-API table with some fields replaced, keeps its pack()
function withFields<T extends object>(table: T, values: Partial<T>): T {
  return Object.assign(Object.create(Object.getPrototypeOf(table)) as T, table, values);
}

function tableOf<C, S extends TableKey<C>>(
  values: Partial<Fields<C>> | null | undefined,
  table: S
) {
  return values?.[table] as Partial<Table<C, S>> | null | undefined;
}

// settingField over one key of a nested table. A save sends the whole table
// with that key replaced
export function tableSettingField<
  C,
  S extends TableKey<C>,
  K extends keyof Table<C, S>,
>(
  settings: Settings<unknown, C>,
  table: S,
  key: K,
  options?: SaveOptions
): Field<NonNullable<Table<C, S>[K]>> {
  return {
    get value() {
      return tableOf(fieldsOf(settings), table)?.[key] as NonNullable<Table<C, S>[K]>;
    },
    onChange(value) {
      const current = tableOf(fieldsOf(settings), table);
      if (!current) return;
      const values: Partial<Table<C, S>> = {};
      values[key] = value;
      const patch: Partial<Fields<C>> = {};
      patch[table] = withFields(current, values) as Fields<C>[S];
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

// settingsReset over keys of a nested table
export function tableSettingsReset<
  C,
  S extends TableKey<C>,
  K extends keyof Table<C, S>,
>(
  settings: Settings<unknown, C>,
  defaults: Defaults<C>,
  table: S,
  keys: readonly K[]
): SettingReset {
  return {
    get modified() {
      const current = tableOf(fieldsOf(settings), table);
      const fallback = tableOf(defaults(), table);
      if (!current || !fallback) return false;
      return keys.some((key) => current[key] !== fallback[key]);
    },
    onReset() {
      const current = tableOf(fieldsOf(settings), table);
      const fallback = tableOf(defaults(), table);
      if (!current || !fallback) return;
      const values: Partial<Table<C, S>> = {};
      for (const key of keys) values[key] = fallback[key];
      const patch: Partial<Fields<C>> = {};
      patch[table] = withFields(current, values) as Fields<C>[S];
      saveSettings(settings, patch);
    },
  };
}

// Per page: bind the settings and their defaults once, then one binding per
// declared row. The row name is the settings key, or the key inside `table`
export function settingRows<C>(
  settings: Settings<unknown, C>,
  defaults: Defaults<C>
): RowBinder<Fields<C>>;
export function settingRows<C, S extends TableKey<C>>(
  settings: Settings<unknown, C>,
  defaults: Defaults<C>,
  table: S
): RowBinder<Table<C, S>>;
export function settingRows<C>(
  settings: Settings<unknown, C>,
  defaults: Defaults<C>,
  table?: TableKey<C>
): unknown {
  return (setting: SettingDef) => {
    if (table === undefined) {
      const key = setting.name as keyof Fields<C>;
      return {
        setting,
        field: settingField(settings, key),
        reset: settingsReset(settings, defaults, [key]),
      };
    }
    const key = setting.name as keyof Table<C, TableKey<C>>;
    return {
      setting,
      field: tableSettingField(settings, table, key),
      reset: tableSettingsReset(settings, defaults, table, [key]),
    };
  };
}

// Row over a GUI pref, reset goes back to the pref's fallback
export function prefRow<T>(setting: SettingDef, pref: Pref<T>): SettingBinding<T> {
  return {
    setting,
    field: {
      get value() {
        return pref();
      },
      onChange: (value) => pref.set(value),
    },
    reset: {
      get modified() {
        return pref() !== pref.fallback;
      },
      onReset: () => pref.set(pref.fallback),
    },
  };
}
