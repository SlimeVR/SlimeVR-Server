import classNames from 'classnames';
import { useEffect, useState } from 'react';
import { defineSettingsPage } from '@/components/settings/settings-definitions';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { Typography } from '@/components/commons/Typography';
import { SegmentedControl } from '@/components/commons/SegmentedControl';
import { FileIcon } from '@/components/commons/icon/FileIcon';
import {
  LogFileRow,
  parseLogFile,
  useLiveLogs,
  useLogFileReader,
  useLogsInfo,
  zipLogFiles,
  LogRow,
} from '@/hooks/logs';
import { saveFile } from '@/utils/save-file';
import { error } from '@/utils/logging';
import { ExportMenu } from './ExportMenu';
import { LiveLogsTab, Viewing } from './LiveLogsTab';
import { fileKey, LogFilesTab } from './LogFilesTab';

export const logsSettingsPage = defineSettingsPage({
  path: 'logs',
  title: 'settings-logs',
  groups: {},
});

export function LogsSettings() {
  const { sources, files, refresh } = useLogsInfo();
  const [tab, setTab] = useState<'live' | 'files'>('live');
  // Rows shown while paused; the live list keeps growing underneath
  const [frozen, setFrozen] = useState<LogRow[] | null>(null);
  const [viewing, setViewing] = useState<Viewing | null>(null);
  const [busy, setBusy] = useState(false);
  const [selected, setSelected] = useState<Set<string>>(new Set());

  const live = useLiveLogs(sources.filter((o) => o.live).map((o) => o.source));
  const readFile = useLogFileReader();

  useEffect(() => {
    if (tab === 'files') refresh();
  }, [tab]);

  const run = async (action: () => Promise<void>) => {
    if (busy) return;
    setBusy(true);
    try {
      await action();
    } catch (e) {
      error(e, 'logs page action failed');
    } finally {
      setBusy(false);
    }
  };

  const view = (file: LogFileRow) =>
    run(async () => {
      setViewing({ file, rows: parseLogFile(await readFile(file), file) });
      setTab('live');
    });

  const save = (file: LogFileRow) =>
    run(async () => {
      const data = await readFile(file);
      await saveFile(new Blob([data], { type: 'text/plain' }), file.name, [
        '.log',
      ]);
    });

  const exportZip = (toExport: LogFileRow[]) =>
    run(async () => {
      const zip = await zipLogFiles(toExport, readFile);
      // Date and time, so two exports on the same day don't suggest the same name
      const stamp = new Date()
        .toISOString()
        .slice(0, 19)
        .replace('T', '_')
        .replaceAll(':', '-');
      await saveFile(zip, `slimevr-logs_${stamp}.zip`, ['.zip']);
    });

  return (
    <SettingsPagePaneLayout
      icon={<FileIcon width={22} />}
      title={logsSettingsPage.title}
      className={classNames(
        tab === 'live' ? 'h-full' : 'min-h-full',
        'mobile:gap-2 mobile:pb-3'
      )}
      contentClassName="flex flex-col flex-grow gap-4 mobile:gap-2"
      hideTitleOnMobile
      actions={
        <>
          <SegmentedControl
            variant="secondary"
            value={tab}
            onChange={setTab}
            options={[
              { value: 'live', labelId: 'settings-logs-tab-live' },
              {
                value: 'files',
                labelId: 'settings-logs-tab-files',
                vars: { count: files.length },
              },
            ]}
          />
          <div className="flex-grow" />
          <ExportMenu
            files={files}
            selectedFiles={files.filter((file) => selected.has(fileKey(file)))}
            busy={busy}
            onExport={exportZip}
          />
        </>
      }
    >
      <div className="mobile:hidden">
        <Typography id="settings-logs-description" />
      </div>

      {tab === 'live' ? (
        <LiveLogsTab
          rows={viewing?.rows ?? frozen ?? live.rows}
          sources={
            viewing ? [viewing.file.source] : sources.map((o) => o.source)
          }
          viewing={viewing}
          onBack={() => {
            setViewing(null);
            setTab('files');
          }}
          paused={frozen !== null}
          onTogglePause={() => setFrozen((rows) => (rows ? null : live.rows))}
          onClear={() => {
            live.clear();
            if (frozen) setFrozen([]);
          }}
        />
      ) : (
        <LogFilesTab
          files={files}
          selected={selected}
          setSelected={setSelected}
          onView={view}
          onSave={save}
        />
      )}
    </SettingsPagePaneLayout>
  );
}
