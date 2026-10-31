export interface SearchAvailability {
  isElectron: boolean;
  isVrcSupported: boolean;
}

declare const declared: unique symbol;

interface SettingInput {
  title: string;
  description?: string;
  available?: (ctx: SearchAvailability) => boolean;
}

export interface SettingDef extends SettingInput {
  readonly [declared]: true;
  key: string;
}

export interface SettingsGroupDef<Row extends string = string> {
  title?: string;
  description?: string;
  rows: Record<Row, SettingDef>;
}

export interface SettingsPageDef<
  Groups extends Record<string, SettingsGroupDef> = Record<string, SettingsGroupDef>,
> {
  path: string;
  title: string;
  available?: (ctx: SearchAvailability) => boolean;
  groups: Groups;
}

interface GroupInput {
  title?: string;
  description?: string;
  rows: Record<string, SettingInput>;
}

interface PageInput {
  path: string;
  title: string;
  available?: (ctx: SearchAvailability) => boolean;
  groups: Record<string, GroupInput>;
}

type DefinedPage<T extends PageInput> = SettingsPageDef<{
  [G in keyof T['groups']]: SettingsGroupDef<keyof T['groups'][G]['rows'] & string>;
}>;

export function defineSettingsPage<const T extends PageInput>(
  input: T
): DefinedPage<T> {
  const groups = Object.fromEntries(
    Object.entries(input.groups).map(([groupName, group]) => [
      groupName,
      {
        ...group,
        rows: Object.fromEntries(
          Object.entries(group.rows).map(([rowName, row]) => [
            rowName,
            { ...row, key: `${input.path}.${groupName}.${rowName}` },
          ])
        ),
      },
    ])
  );
  return { ...input, groups } as unknown as DefinedPage<T>;
}
