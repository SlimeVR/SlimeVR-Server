import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import { useMemo, useState } from 'react';
import { LogLevel, LogSource } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { DropdownInside } from '@/components/commons/Dropdown';
import { InputInside } from '@/components/commons/Input';
import { Typography } from '@/components/commons/Typography';
import { PopoverMenu } from '@/components/commons/PopoverMenu';
import { CopyIcon } from '@/components/commons/icon/CopyIcon';
import { TuneIcon } from '@/components/commons/icon/TuneIcon';
import { TrashIcon } from '@/components/commons/icon/TrashIcon';
import { FolderIcon } from '@/components/commons/icon/FolderIcon';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { ArrowLeftIcon } from '@/components/commons/icon/ArrowIcons';
import { PauseIcon } from '@/components/commons/icon/PauseIcon';
import { PlayIcon } from '@/components/commons/icon/PlayIcon';
import { useElectron } from '@/hooks/electron';
import { useBreakpoint } from '@/hooks/breakpoint';
import {
  LogFileRow,
  LogRow,
  sourceKey,
  rowsToText,
  sourceColor,
} from '@/hooks/logs';
import { error } from '@/utils/logging';
import { LogList } from './LogList';

const ALL_LOGGERS = '*';

const LEVEL_FILTERS = [
  { level: LogLevel.DEBUG, id: 'settings-logs-level-debug' },
  { level: LogLevel.INFO, id: 'settings-logs-level-info' },
  { level: LogLevel.WARN, id: 'settings-logs-level-warn' },
  { level: LogLevel.ERROR, id: 'settings-logs-level-error' },
];

export interface Viewing {
  file: LogFileRow;
  rows: LogRow[];
}

function SourcesValue({ shown, total }: { shown: LogSource[]; total: number }) {
  if (shown.length === total) {
    return <Typography tag="span" id="settings-logs-source-all" />;
  }
  return (
    <span className="flex items-center gap-1.5">
      {shown.map((source) => (
        <span
          key={source}
          className="w-2 h-2 rounded-full shrink-0"
          style={{ backgroundColor: sourceColor(source) }}
        />
      ))}
      {shown.length === 1 && (
        <Typography
          tag="span"
          id={`settings-logs-source-${sourceKey(shown[0])}`}
        />
      )}
    </span>
  );
}

function loggerKey(row: LogRow) {
  return `${row.source}:${row.logger}`;
}

function LoggerLabel({ row }: { row: LogRow }) {
  return (
    <span className="flex items-center gap-2 min-w-0">
      <span
        className="w-2 h-2 rounded-full shrink-0"
        style={{ backgroundColor: sourceColor(row.source) }}
      />
      {row.logger ? (
        <span className="truncate">{row.logger}</span>
      ) : (
        <Typography
          tag="span"
          id={`settings-logs-source-${sourceKey(row.source)}`}
        />
      )}
    </span>
  );
}

export function LiveLogsTab({
  rows,
  sources,
  viewing,
  onBack,
  paused,
  onTogglePause,
  onClear,
}: {
  rows: LogRow[];
  sources: LogSource[];
  viewing: Viewing | null;
  onBack: () => void;
  paused: boolean;
  onTogglePause: () => void;
  onClear: () => void;
}) {
  const { l10n } = useLocalization();
  const electron = useElectron();
  const [copied, setCopied] = useState(false);
  const { isNmd: compactBar } = useBreakpoint('nmd');
  const [hiddenSources, setHiddenSources] = useState<LogSource[]>([]);
  const [minLevel, setMinLevel] = useState(LogLevel.INFO);
  const [logger, setLogger] = useState(ALL_LOGGERS);
  const [query, setQuery] = useState('');

  const loggers = useMemo(() => {
    const byKey = new Map<string, LogRow>();
    for (const row of rows) {
      const key = loggerKey(row);
      if (!byKey.has(key)) byKey.set(key, row);
    }
    return [...byKey.entries()].sort(
      ([, a], [, b]) => a.source - b.source || a.logger.localeCompare(b.logger)
    );
  }, [rows]);

  const visible = useMemo(() => {
    const needle = query.toLowerCase();
    return rows.filter(
      (row) =>
        !hiddenSources.includes(row.source) &&
        row.level >= minLevel &&
        (logger === ALL_LOGGERS || loggerKey(row) === logger) &&
        (!needle || row.message.toLowerCase().includes(needle))
    );
  }, [rows, hiddenSources, minLevel, logger, query]);

  const copy = async () => {
    try {
      await navigator.clipboard.writeText(
        rowsToText(visible, (o) =>
          l10n.getString(`settings-logs-source-${sourceKey(o)}`)
        )
      );
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (e) {
      error(e, 'failed to copy logs');
    }
  };

  const live = !viewing && !paused;

  const filtersActive =
    hiddenSources.length > 0 ||
    minLevel !== LogLevel.INFO ||
    logger !== ALL_LOGGERS;

  const renderFilters = (place: 'inline' | 'drawer') => (
    <>
      {sources.length > 1 && (
        <div className={place === 'inline' ? 'w-40' : undefined}>
          <DropdownInside
            name={`logs-sources-${place}`}
            variant={place === 'drawer' ? 'secondary' : 'primary'}
            direction="down"
            display="block"
            menuWidth={place === 'inline' ? 'w-60' : undefined}
            alignment="left"
            label={l10n.getString('settings-logs-sources')}
            inside="label"
            placeholder=""
            multiple
            value={sources
              .filter((source) => !hiddenSources.includes(source))
              .map(String)}
            onChange={(values) =>
              setHiddenSources(
                sources.filter((source) => !values.includes(String(source)))
              )
            }
            renderValue={() => (
              <SourcesValue
                shown={sources.filter(
                  (source) => !hiddenSources.includes(source)
                )}
                total={sources.length}
              />
            )}
            items={sources.map((source) => ({
              value: String(source),
              label: (
                <span className="flex items-center gap-2">
                  <span
                    className="w-2 h-2 rounded-full shrink-0"
                    style={{ backgroundColor: sourceColor(source) }}
                  />
                  <Typography
                    tag="span"
                    id={`settings-logs-source-${sourceKey(source)}`}
                  />
                </span>
              ),
            }))}
          />
        </div>
      )}
      <div className={place === 'inline' ? 'w-40' : undefined}>
        <DropdownInside
          name={`logs-level-${place}`}
          variant={place === 'drawer' ? 'secondary' : 'primary'}
          direction="down"
          display="block"
          menuWidth={place === 'inline' ? 'w-60' : undefined}
          alignment="left"
          label={l10n.getString('settings-logs-level')}
          inside="label"
          placeholder=""
          value={String(minLevel)}
          onChange={(value) => setMinLevel(Number(value))}
          items={LEVEL_FILTERS.map(({ level, id }) => ({
            value: String(level),
            label: <Typography tag="span" id={id} />,
          }))}
        />
      </div>
      <div className={place === 'inline' ? 'w-40' : undefined}>
        <DropdownInside
          name={`logs-logger-${place}`}
          variant={place === 'drawer' ? 'secondary' : 'primary'}
          direction="down"
          display="block"
          menuWidth={place === 'inline' ? 'w-60' : undefined}
          alignment="left"
          label={l10n.getString('settings-logs-logger')}
          inside="label"
          placeholder=""
          value={logger}
          onChange={setLogger}
          items={[
            {
              value: ALL_LOGGERS,
              label: <Typography tag="span" id="settings-logs-logger-all" />,
            },
            ...loggers.map(([key, row]) => ({
              value: key,
              label: <LoggerLabel row={row} />,
            })),
          ]}
        />
      </div>
    </>
  );

  return (
    <div className="flex flex-col gap-2 flex-grow h-0">
      <div className="flex flex-wrap gap-2 items-center">
        <div className="contents mobile:hidden">{renderFilters('inline')}</div>
        <div className="flex-grow basis-48 min-w-0">
          <InputInside
            name="logs-search"
            type="text"
            placeholder={l10n.getString('settings-logs-search')}
            value={query}
            onChange={(e) => setQuery((e.target as HTMLInputElement).value)}
          />
        </div>
        <div className="xs:hidden">
          <PopoverMenu
            drawerTitle={
              <Typography variant="section-title" id="settings-logs-filters" />
            }
            trigger={(triggerProps) => (
              <Button
                variant="secondary"
                {...triggerProps}
                id="settings-logs-filters"
                iconOnly
                icon={
                  <span className="relative flex fill-background-10">
                    <TuneIcon size={18} />
                    {filtersActive && (
                      <span className="absolute -top-1 -right-1 w-2 h-2 rounded-full bg-accent-background-10" />
                    )}
                  </span>
                }
              />
            )}
          >
            <div className="flex flex-col gap-4 p-4">
              {renderFilters('drawer')}
            </div>
          </PopoverMenu>
        </div>
      </div>

      <div className="bg-background-80 rounded-lg flex-grow h-0 min-h-[160px] flex flex-col p-2">
        {viewing && (
          <div className="flex flex-wrap items-center gap-2 pb-2 mb-1.5 border-b-2 border-background-60">
            <Button
              variant="secondary"
              size="small"
              onClick={onBack}
              icon={
                <span className="flex fill-background-10">
                  <ArrowLeftIcon size={12} />
                </span>
              }
              id="settings-logs-back"
            />
            <Typography
              id="settings-logs-viewing"
              vars={{ name: viewing.file.name }}
            />
          </div>
        )}

        <LogList key={viewing ? 'file' : 'live'} rows={visible} query={query} />

        <div className="border-t-2 pt-2 mt-1.5 border-background-60 flex flex-wrap gap-2 items-center">
          <span className="flex items-center gap-1.5">
            <span
              className={classNames(
                'w-2 h-2 rounded-full',
                live ? 'bg-status-success animate-pulse' : 'bg-status-warning'
              )}
            />
            <Typography
              tag="span"
              color={live ? 'text-status-success' : 'text-status-warning'}
              id={
                viewing
                  ? 'settings-logs-status-file'
                  : paused
                    ? 'settings-logs-status-paused'
                    : 'settings-logs-status-live'
              }
            />
          </span>
          <Typography
            tag="span"
            color="text-background-20"
            id={
              visible.length === rows.length
                ? 'settings-logs-lines'
                : 'settings-logs-lines-filtered'
            }
            vars={{ shown: visible.length, total: rows.length }}
          />
          <div className="flex-grow" />
          <Button
            variant="secondary"
            size="small"
            onClick={copy}
            disabled={visible.length === 0}
            icon={
              <span className="flex fill-background-10">
                {copied ? <CheckIcon size={12} /> : <CopyIcon size={14} />}
              </span>
            }
            id="settings-logs-copy"
            iconOnly={compactBar}
          />
          {!viewing && (
            <Button
              variant="secondary"
              size="small"
              onClick={onClear}
              icon={
                <span className="flex fill-background-10">
                  <TrashIcon size={14} />
                </span>
              }
              id="settings-logs-clear"
              iconOnly={compactBar}
            />
          )}
          {electron.isElectron && (
            <Button
              variant="secondary"
              size="small"
              onClick={() => electron.api.openLogsFolder()}
              icon={
                <span className="flex text-background-10">
                  <FolderIcon width={14} />
                </span>
              }
              id="settings-logs-open_folder"
              iconOnly={compactBar}
            />
          )}
          {!viewing && (
            <Button
              variant="secondary"
              size="small"
              onClick={onTogglePause}
              icon={
                <span className="flex fill-background-10">
                  {paused ? <PlayIcon width={14} /> : <PauseIcon width={14} />}
                </span>
              }
              id={paused ? 'settings-logs-resume' : 'settings-logs-pause'}
              iconOnly={compactBar}
            />
          )}
        </div>
      </div>
    </div>
  );
}
