import { LogSource } from 'solarxr-protocol';
import { Dispatch, SetStateAction, useEffect, useMemo, useRef } from 'react';
import { Button } from '@/components/commons/Button';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { EyeIcon } from '@/components/commons/icon/EyeIcon';
import { DownloadIcon } from '@/components/commons/icon/DownloadIcon';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useLocaleConfig } from '@/i18n/config';
import {
  logFileSession,
  LogFileRow,
  sourceKey,
  sourceColor,
} from '@/hooks/logs';

function useFormatters() {
  const { currentLocales } = useLocaleConfig();
  return useMemo(() => {
    const megabytes = Intl.NumberFormat(currentLocales, {
      style: 'unit',
      unit: 'megabyte',
      maximumFractionDigits: 1,
    });
    const kilobytes = Intl.NumberFormat(currentLocales, {
      style: 'unit',
      unit: 'kilobyte',
      maximumFractionDigits: 0,
    });
    const date = Intl.DateTimeFormat(currentLocales, {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
    });
    return {
      size: (bytes: number) =>
        bytes >= 1_000_000
          ? megabytes.format(bytes / 1_000_000)
          : kilobytes.format(Math.max(1, bytes / 1000)),
      date: (timeMs: number) => date.format(timeMs),
    };
  }, [currentLocales]);
}

function SelectAllCheckbox({
  name,
  selected,
  total,
  onChange,
}: {
  name: string;
  selected: number;
  total: number;
  onChange: (all: boolean) => void;
}) {
  const ref = useRef<HTMLInputElement>(null);
  const partial = selected > 0 && selected < total;

  useEffect(() => {
    if (ref.current) ref.current.indeterminate = partial;
  }, [partial]);

  return (
    <CheckboxInternal
      ref={ref}
      name={name}
      checked={selected === total}
      onChange={() => onChange(selected < total)}
    />
  );
}

export const fileKey = (file: LogFileRow) =>
  `${sourceKey(file.source)}/${file.name}`;

export function LogFilesTab({
  files,
  selected,
  setSelected,
  onView,
  onSave,
}: {
  files: LogFileRow[];
  selected: Set<string>;
  setSelected: Dispatch<SetStateAction<Set<string>>>;
  onView: (file: LogFileRow) => void;
  onSave: (file: LogFileRow) => void;
}) {
  const { isMobile } = useBreakpoint('mobile');
  const format = useFormatters();
  const groups = useMemo(() => {
    const bySource = new Map<LogSource, LogFileRow[]>();
    for (const file of files) {
      bySource.set(file.source, [...(bySource.get(file.source) ?? []), file]);
    }
    return [...bySource.entries()];
  }, [files]);

  const toggle = (file: LogFileRow) =>
    setSelected((current) => {
      const next = new Set(current);
      if (!next.delete(fileKey(file))) next.add(fileKey(file));
      return next;
    });

  const setGroup = (group: LogFileRow[], all: boolean) =>
    setSelected((current) => {
      const next = new Set(current);
      for (const file of group) {
        if (all) next.add(fileKey(file));
        else next.delete(fileKey(file));
      }
      return next;
    });

  if (files.length === 0) {
    return <Typography id="settings-logs-files-empty" />;
  }

  return (
    <div className="flex flex-col gap-4">
      {groups.map(([source, sourceFiles]) => (
        <div key={sourceKey(source)} className="flex flex-col gap-2">
          <div className="grid grid-cols-[24px_minmax(0,1fr)] gap-2.5 items-center px-2.5">
            <SelectAllCheckbox
              name={`logs-files-all-${sourceKey(source)}`}
              selected={
                sourceFiles.filter((file) => selected.has(fileKey(file))).length
              }
              total={sourceFiles.length}
              onChange={(all) => setGroup(sourceFiles, all)}
            />
            <div className="flex flex-wrap items-center gap-x-3 gap-y-1 min-w-0">
              <span
                className="w-2.5 h-2.5 rounded-full shrink-0"
                style={{ backgroundColor: sourceColor(source) }}
              />
              <span className="uppercase">
                <Typography
                  variant="section-title"
                  id={`settings-logs-source-${sourceKey(source)}`}
                />
              </span>
              <Typography
                tag="span"
                color="text-background-20"
                id="settings-logs-files-count"
                vars={{ count: sourceFiles.length }}
              />
              <span className="w-px h-3.5 bg-background-10/15" />
              <Typography tag="span" color="text-background-20">
                {format.size(
                  sourceFiles.reduce((total, file) => total + file.size, 0)
                )}
              </Typography>
              <div className="h-px flex-grow bg-background-10/15" />
            </div>
          </div>
          <div className="flex flex-col rounded-lg bg-background-80 divide-y divide-background-70 overflow-hidden">
            {sourceFiles.map((file) => {
              const session = logFileSession(file.name);
              return (
                <div
                  key={file.name}
                  title={file.name}
                  onClick={(e) => {
                    if ((e.target as HTMLElement).closest('label, button'))
                      return;
                    toggle(file);
                  }}
                  className="grid grid-cols-[24px_minmax(0,1fr)_auto_auto] gap-x-3 mobile:gap-x-2 items-center px-2.5 py-2 cursor-pointer transition-colors hover:bg-background-70/30"
                >
                  <CheckboxInternal
                    name={`logs-file-${fileKey(file)}`}
                    checked={selected.has(fileKey(file))}
                    onChange={() => toggle(file)}
                  />
                  <div className="flex flex-col min-w-0">
                    <div className="flex items-center gap-2 min-w-0">
                      <span className="min-w-0 truncate">
                        <Typography
                          bold
                          whitespace="whitespace-nowrap"
                          tag="span"
                        >
                          {session ? format.date(session.startMs) : file.name}
                        </Typography>
                      </span>
                      {session && session.index > 0 && (
                        <span className="shrink-0 flex items-center h-5 px-2 rounded-full bg-background-60">
                          <Typography
                            tag="span"
                            id="settings-logs-files-part"
                            vars={{ part: session.index + 1 }}
                          />
                        </span>
                      )}
                      {file.current && (
                        <span className="shrink-0 flex items-center h-5 px-2 rounded-full bg-status-success/20">
                          <Typography
                            tag="span"
                            color="text-status-success"
                            id="settings-logs-files-current"
                          />
                        </span>
                      )}
                    </div>
                    {session && (
                      <span className="font-mono text-[11px] text-background-20 truncate">
                        {file.name}
                      </span>
                    )}
                  </div>
                  <span className="text-background-10 tabular-nums text-right">
                    {format.size(file.size)}
                  </span>
                  <div className="flex gap-1.5">
                    <Button
                      variant="secondary"
                      size="small"
                      onClick={() => onView(file)}
                      icon={
                        <span className="flex fill-background-10">
                          <EyeIcon width={14} />
                        </span>
                      }
                      id="settings-logs-files-view"
                      iconOnly={isMobile}
                    />
                    <Button
                      variant="secondary"
                      size="small"
                      onClick={() => onSave(file)}
                      icon={
                        <span className="flex fill-background-10">
                          <DownloadIcon
                            width={14}
                            className="fill-background-10"
                          />
                        </span>
                      }
                      id="settings-logs-files-save"
                      iconOnly={isMobile}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );
}
