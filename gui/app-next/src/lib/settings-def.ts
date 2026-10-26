import type { Component } from 'solid-js';
import type { MessageId } from '@/lib/l10n';

// Rows and groups only come from defineSettingsPage, so search sees every row
declare const declared: unique symbol;

export type SettingsIcon = Component<{ class?: string }>;

interface SettingInput {
  title: MessageId;
  description?: MessageId;
  available?: () => boolean;
}

export interface SettingDef<Name extends string = string> extends SettingInput {
  readonly [declared]: true;
  name: Name;
  key: string;
}

export interface SettingsGroupDef<Row extends string = string> {
  readonly [declared]: true;
  title?: MessageId;
  description?: MessageId;
  rows: { [R in Row]: SettingDef<R> };
}

export interface SettingsPageDef<
  Groups extends Record<string, SettingsGroupDef> = Record<string, SettingsGroupDef>,
> {
  path: string;
  title: MessageId;
  icon: SettingsIcon;
  available?: () => boolean;
  groups: Groups;
}

interface GroupInput {
  title?: MessageId;
  description?: MessageId;
  rows: Record<string, SettingInput>;
}

interface PageInput {
  path: string;
  title: MessageId;
  icon: SettingsIcon;
  available?: () => boolean;
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
            {
              ...row,
              name: rowName,
              key: `${input.path}.${groupName}.${rowName}`,
            },
          ])
        ),
      },
    ])
  );
  return { ...input, groups } as unknown as DefinedPage<T>;
}
