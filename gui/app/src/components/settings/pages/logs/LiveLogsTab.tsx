import { useLocalization } from '@fluent/react';
import { useMemo, useState } from 'react';
import { LogLevel, LogSource } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { CopyIcon } from '@/components/commons/icon/CopyIcon';
import { FolderIcon } from '@/components/commons/icon/FolderIcon';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { ArrowLeftIcon } from '@/components/commons/icon/ArrowIcons';
import { LogView } from '@/components/commons/log-view/LogView';
import {
  DotLabel,
  FilterDropdown,
  FilterPlace,
  LevelFilter,
  LogFilterBar,
} from '@/components/commons/log-view/LogFilterBar';
import {
  ClearLogButton,
  LogFooter,
  PauseLogButton,
} from '@/components/commons/log-view/LogFooter';
import {
  LogViewLine,
  passesLevel,
  passesQuery,
} from '@/components/commons/log-view/log-line';
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

const ALL_LOGGERS = '*';

export interface Viewing {
  file: LogFileRow;
  rows: LogRow[];
}

function SourcesValue({ shown, total }: { shown: LogSource[]; total: number }) {
  if (shown.length === total) {
    return <Typography tag="span" id="settings-logs-source-all" />;
  }
  return (
    <span className="flex items-center gap-1.5 min-w-0">
      {shown.map((source) => (
        <span
          key={source}
          className="w-2 h-2 rounded-full shrink-0"
          style={{ backgroundColor: sourceColor(source) }}
        />
      ))}
      {shown.length === 1 && (
        <span className="truncate">
          <Typography
            tag="span"
            whitespace="whitespace-nowrap"
            id={`settings-logs-source-${sourceKey(shown[0])}`}
          />
        </span>
      )}
    </span>
  );
}

function loggerKey(row: LogRow) {
  return `${row.source}:${row.logger}`;
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

  const visible = useMemo(
    () =>
      rows.filter(
        (row) =>
          !hiddenSources.includes(row.source) &&
          passesLevel(row.level, minLevel) &&
          (logger === ALL_LOGGERS || loggerKey(row) === logger) &&
          passesQuery(row.message, query)
      ),
    [rows, hiddenSources, minLevel, logger, query]
  );

  const lines = useMemo(
    () =>
      visible.map(
        (row): LogViewLine => ({
          id: row.id,
          timeMs: row.timeMs,
          timeTitle: null,
          level: row.level,
          label:
            row.logger ||
            l10n.getString(`settings-logs-source-${sourceKey(row.source)}`),
          color: sourceColor(row.source),
          message: row.message,
          spans: null,
          details: row.stackTrace,
        })
      ),
    [visible, l10n]
  );

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

  const filtersActive =
    hiddenSources.length > 0 ||
    minLevel !== LogLevel.INFO ||
    logger !== ALL_LOGGERS;

  const renderFilters = (place: FilterPlace) => (
    <>
      {sources.length > 1 && (
        <FilterDropdown
          place={place}
          name="logs-sources"
          label={l10n.getString('settings-logs-sources')}
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
              <DotLabel color={sourceColor(source)}>
                <Typography
                  tag="span"
                  id={`settings-logs-source-${sourceKey(source)}`}
                />
              </DotLabel>
            ),
          }))}
        />
      )}
      <LevelFilter
        place={place}
        name="logs-level"
        value={minLevel}
        onChange={setMinLevel}
      />
      <FilterDropdown
        place={place}
        name="logs-logger"
        label={l10n.getString('settings-logs-logger')}
        value={logger}
        onChange={setLogger}
        items={[
          {
            value: ALL_LOGGERS,
            label: <Typography tag="span" id="settings-logs-logger-all" />,
          },
          ...loggers.map(([key, row]) => ({
            value: key,
            label: (
              <DotLabel color={sourceColor(row.source)}>
                {row.logger || (
                  <Typography
                    tag="span"
                    whitespace="whitespace-nowrap"
                    id={`settings-logs-source-${sourceKey(row.source)}`}
                  />
                )}
              </DotLabel>
            ),
          })),
        ]}
      />
    </>
  );

  return (
    <div className="flex flex-col gap-2 flex-grow h-0">
      <LogFilterBar
        renderFilters={renderFilters}
        filtersActive={filtersActive}
        searchName="logs-search"
        searchPlaceholder={l10n.getString('settings-logs-search')}
        query={query}
        onQueryChange={setQuery}
      />

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

        <LogView key={viewing ? 'file' : 'live'} lines={lines} query={query} />

        <LogFooter
          status={viewing ? 'file' : paused ? 'paused' : 'live'}
          shown={visible.length}
          total={rows.length}
        >
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
            <ClearLogButton onClick={onClear} iconOnly={compactBar} />
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
            <PauseLogButton
              paused={paused}
              onToggle={onTogglePause}
              iconOnly={compactBar}
            />
          )}
        </LogFooter>
      </div>
    </div>
  );
}
