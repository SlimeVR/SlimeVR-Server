import classNames from 'classnames';
import { KeyboardEvent, ReactNode, useEffect, useRef, useState } from 'react';
import { useLocalization } from '@fluent/react';
import { ProfileKind, ProfileOperationStatus } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Clickable } from '@/components/commons/Clickable';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { CopyIcon } from '@/components/commons/icon/CopyIcon';
import { FolderIcon } from '@/components/commons/icon/FolderIcon';
import { PencilIcon } from '@/components/commons/icon/PencilIcon';
import { PlusIcon } from '@/components/commons/icon/PlusIcon';
import { TrashIcon } from '@/components/commons/icon/TrashIcon';
import { IconButton } from '@/components/commons/IconButton';
import { InputInside } from '@/components/commons/Input';
import { Typography } from '@/components/commons/Typography';
import { SegmentedControl } from '@/components/commons/SegmentedControl';
import {
  ProfileNames,
  profileErrorId,
  useProfileOperations,
  useProfiles,
} from '@/hooks/profiles';
import {
  SettingsGroup,
  useFocusSetting,
} from '@/components/settings/SettingRow';
import {
  SettingDef,
  SettingsGroupDef,
  defineSettingsPage,
} from '@/components/settings/settings-definitions';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';

export const profilesSettingsPage = defineSettingsPage({
  path: 'profiles',
  title: 'settings-sidebar-profiles',
  groups: {
    settings: {
      title: 'settings-profiles-settings',
      description: 'settings-profiles-settings-description',
      rows: { create: { title: 'settings-profiles-new_settings' } },
    },
    user: {
      title: 'settings-profiles-user',
      description: 'settings-profiles-user-description',
      rows: { create: { title: 'settings-profiles-new_user' } },
    },
  },
});

type Operations = ReturnType<typeof useProfileOperations>;

function withoutIsolates(text: string) {
  return text.replace(/[\u2066-\u2069]/g, '');
}

function ErrorLine({ status }: { status: ProfileOperationStatus | null }) {
  if (status === null) return null;
  return (
    <Typography id={profileErrorId(status)} color="text-status-critical" />
  );
}

function InlineNameEditor({
  initial,
  unchangedFrom,
  confirmId,
  onConfirm,
  onCancel,
  children,
}: {
  initial: string;
  unchangedFrom?: string;
  confirmId: string;
  onConfirm: (name: string) => Promise<ProfileOperationStatus>;
  onCancel: () => void;
  children?: ReactNode;
}) {
  const { l10n } = useLocalization();
  const inputRef = useRef<HTMLInputElement>(null);
  const [name, setName] = useState(initial);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<ProfileOperationStatus | null>(null);

  useEffect(() => {
    inputRef.current?.focus();
    inputRef.current?.select();
  }, []);

  const trimmed = name.trim();
  const canConfirm = !busy && trimmed.length > 0 && trimmed !== unchangedFrom;

  const confirm = async () => {
    if (!canConfirm) return;
    setBusy(true);
    const status = await onConfirm(trimmed);
    setBusy(false);
    if (status !== ProfileOperationStatus.OK) setError(status);
  };

  const onKeyDown = (e: KeyboardEvent) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      confirm();
      return;
    }
    if (e.key === 'Escape') {
      e.stopPropagation();
      onCancel();
    }
  };

  return (
    <div className="flex flex-col gap-2" onKeyDown={onKeyDown}>
      <div className="flex flex-row gap-2 items-center flex-wrap">
        <div className="flex-grow min-w-[12rem]">
          <InputInside
            ref={inputRef}
            name="profile-name"
            label={l10n.getString('settings-profiles-name')}
            value={name}
            onChange={(e) => {
              setName(e.currentTarget.value);
              setError(null);
            }}
          />
        </div>
        <Button
          variant="primary"
          id={confirmId}
          disabled={!canConfirm}
          loading={busy}
          onClick={confirm}
        />
        <Button
          variant="tertiary"
          id="settings-profiles-cancel"
          onClick={onCancel}
        />
      </div>
      {children}
      <ErrorLine status={error} />
    </div>
  );
}

type RowMode = 'idle' | 'rename' | 'duplicate' | 'delete';

function ProfileRow({
  kind,
  name,
  active,
  operations,
}: {
  kind: ProfileKind;
  name: string;
  active: boolean;
  operations: Operations;
}) {
  const { l10n } = useLocalization();
  const [mode, setMode] = useState<RowMode>('idle');
  const [error, setError] = useState<ProfileOperationStatus | null>(null);

  const finish = (status: ProfileOperationStatus) => {
    if (status === ProfileOperationStatus.OK) {
      setMode('idle');
      setError(null);
    } else {
      setError(status);
    }
    return status;
  };

  return (
    <div className="flex flex-col gap-2 px-3 py-2 xs:px-4 transition-colors hover:bg-background-70/30">
      {mode === 'idle' && (
        <div className="flex items-center gap-2 min-w-0">
          <Clickable
            className="flex flex-grow items-center gap-3 min-w-0 py-1 cursor-pointer"
            current={active || undefined}
            onClick={() => {
              if (!active) operations.switchProfile(kind, name).then(finish);
            }}
          >
            <span
              className={
                active
                  ? 'w-6 h-6 shrink-0 flex items-center justify-center rounded-full bg-accent-background-30 fill-background-10'
                  : 'w-6 h-6 shrink-0 rounded-full border-2 border-background-40'
              }
            >
              {active && <CheckIcon size={11} />}
            </span>
            <Typography bold={active} truncate>
              {name}
            </Typography>
          </Clickable>
          <div className="flex items-center gap-1 shrink-0 fill-background-10">
            <IconButton
              labelId="settings-profiles-rename"
              className="p-2 rounded-md hover:bg-background-60 cursor-pointer"
              onClick={() => setMode('rename')}
            >
              <PencilIcon size={18} />
            </IconButton>
            <IconButton
              labelId="settings-profiles-duplicate"
              className="p-2 rounded-md hover:bg-background-60 cursor-pointer"
              onClick={() => setMode('duplicate')}
            >
              <CopyIcon size={18} />
            </IconButton>
            <IconButton
              labelId={
                active
                  ? 'settings-profiles-delete-active'
                  : 'settings-profiles-delete'
              }
              disabled={active}
              className={
                active
                  ? 'p-2 rounded-md fill-background-40 cursor-not-allowed'
                  : 'p-2 rounded-md hover:bg-status-critical/30 cursor-pointer'
              }
              onClick={() => setMode('delete')}
            >
              <TrashIcon size={18} />
            </IconButton>
          </div>
        </div>
      )}
      {mode !== 'idle' && (
        <div className="flex flex-col gap-2 pl-9">
          {mode === 'rename' && (
            <InlineNameEditor
              initial={name}
              unchangedFrom={name}
              confirmId="settings-profiles-save"
              onConfirm={(newName) =>
                operations.renameProfile(kind, name, newName).then(finish)
              }
              onCancel={() => setMode('idle')}
            />
          )}
          {mode === 'duplicate' && (
            <InlineNameEditor
              initial={withoutIsolates(
                l10n.getString('settings-profiles-copy_name', { name })
              )}
              confirmId="settings-profiles-create"
              onConfirm={(newName) =>
                operations.createProfile(kind, newName, name).then(finish)
              }
              onCancel={() => setMode('idle')}
            />
          )}
          {mode === 'delete' && (
            <div className="flex flex-row gap-2 items-center flex-wrap justify-between">
              <Typography
                id="settings-profiles-delete_confirm"
                vars={{ name }}
              />
              <div className="flex gap-2">
                <Button
                  variant="danger"
                  id="settings-profiles-delete_action"
                  onClick={() =>
                    operations.deleteProfile(kind, name).then(finish)
                  }
                />
                <Button
                  variant="tertiary"
                  id="settings-profiles-cancel"
                  onClick={() => {
                    setMode('idle');
                    setError(null);
                  }}
                />
              </div>
            </div>
          )}
          {mode === 'delete' && <ErrorLine status={error} />}
        </div>
      )}
      {mode === 'idle' && error !== null && (
        <div className="pl-9">
          <ErrorLine status={error} />
        </div>
      )}
    </div>
  );
}

function freeProfileName(template: (n: number) => string, names: string[]) {
  const taken = new Set(names.map((name) => name.toLowerCase()));
  let n = names.length + 1;
  while (taken.has(template(n).toLowerCase())) n++;
  return template(n);
}

function NewProfileRow({
  kind,
  setting,
  profiles,
  operations,
}: {
  kind: ProfileKind;
  setting: SettingDef;
  profiles: ProfileNames;
  operations: Operations;
}) {
  const { l10n } = useLocalization();
  const { ref, highlighted } = useFocusSetting<HTMLDivElement>(setting.key);
  const [creating, setCreating] = useState(false);
  const [copyActive, setCopyActive] = useState(false);

  const close = () => {
    setCreating(false);
    setCopyActive(false);
  };

  return (
    <div
      ref={ref}
      className={classNames(
        'transition-colors',
        creating && 'px-3 py-2 xs:px-4',
        highlighted && 'bg-accent-background-40/30'
      )}
    >
      {!creating && (
        <Clickable
          onClick={() => setCreating(true)}
          className="group flex w-full items-center gap-3 px-3 py-3 xs:px-4 cursor-pointer fill-background-10 transition-colors hover:bg-background-70/30"
        >
          <span className="w-6 h-6 shrink-0 flex items-center justify-center rounded-full border-2 border-dashed border-background-40 transition-colors group-hover:border-accent-background-20 group-hover:fill-accent-background-10">
            <PlusIcon size={14} />
          </span>
          <Typography id={setting.title} />
        </Clickable>
      )}
      {creating && (
        <div className="flex items-start gap-3">
          <span className="w-6 h-6 mt-3 shrink-0 rounded-full border-2 border-dashed border-background-40" />
          <div className="flex-grow min-w-0">
            <InlineNameEditor
              initial={withoutIsolates(
                freeProfileName(
                  (n) =>
                    l10n.getString('settings-profiles-default_name', { n }),
                  profiles.names
                )
              )}
              confirmId="settings-profiles-create"
              onConfirm={async (name) => {
                const status = await operations.createProfile(
                  kind,
                  name,
                  copyActive ? profiles.active : ''
                );
                if (status === ProfileOperationStatus.OK) close();
                return status;
              }}
              onCancel={close}
            >
              <div className="flex flex-wrap items-center gap-x-3 gap-y-1">
                <Typography bold id="settings-profiles-start_from" />
                <SegmentedControl
                  variant="tertiary"
                  value={copyActive ? 'copy' : 'defaults'}
                  onChange={(value) => setCopyActive(value === 'copy')}
                  options={[
                    {
                      value: 'defaults',
                      labelId: 'settings-profiles-start_from-defaults',
                    },
                    {
                      value: 'copy',
                      labelId: 'settings-profiles-start_from-copy',
                      vars: { name: profiles.active },
                    },
                  ]}
                />
              </div>
            </InlineNameEditor>
          </div>
        </div>
      )}
    </div>
  );
}

function ProfileGroup({
  kind,
  group,
  profiles,
  operations,
}: {
  kind: ProfileKind;
  group: SettingsGroupDef<'create'>;
  profiles: ProfileNames;
  operations: Operations;
}) {
  return (
    <SettingsGroup group={group}>
      {profiles.names.map((name) => (
        <ProfileRow
          key={name}
          kind={kind}
          name={name}
          active={name === profiles.active}
          operations={operations}
        />
      ))}
      <NewProfileRow
        kind={kind}
        setting={group.rows.create}
        profiles={profiles}
        operations={operations}
      />
    </SettingsGroup>
  );
}

export function ProfilesSettings() {
  const profiles = useProfiles();
  const operations = useProfileOperations();

  return (
    <SettingsPagePaneLayout
      icon={<FolderIcon />}
      title={profilesSettingsPage.title}
    >
      {profiles && (
        <>
          <ProfileGroup
            kind={ProfileKind.SETTINGS}
            group={profilesSettingsPage.groups.settings}
            profiles={profiles.settings}
            operations={operations}
          />
          <ProfileGroup
            kind={ProfileKind.USER}
            group={profilesSettingsPage.groups.user}
            profiles={profiles.user}
            operations={operations}
          />
        </>
      )}
    </SettingsPagePaneLayout>
  );
}
