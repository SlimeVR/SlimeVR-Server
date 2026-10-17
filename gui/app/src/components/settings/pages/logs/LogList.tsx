import { Localized } from '@fluent/react';
import classNames from 'classnames';
import { useVirtualizer } from '@tanstack/react-virtual';
import { useLayoutEffect, useMemo, useRef, useState } from 'react';
import { LogLevel } from 'solarxr-protocol';
import { Clickable } from '@/components/commons/Clickable';
import { Typography } from '@/components/commons/Typography';
import { ArrowDownIcon } from '@/components/commons/icon/ArrowIcons';
import { formatLogTime, LogRow, sourceColor, sourceKey } from '@/hooks/logs';

const LEVEL_CLASSES: Record<LogLevel, string> = {
  [LogLevel.TRACE]: 'border-transparent',
  [LogLevel.DEBUG]: 'border-transparent',
  [LogLevel.INFO]: 'border-background-50',
  [LogLevel.WARN]: 'border-status-warning bg-status-warning/5',
  [LogLevel.ERROR]: 'border-status-critical bg-status-critical/10',
  [LogLevel.FATAL]: 'border-status-critical bg-status-critical/10',
};

function LogRowView({
  row,
  expanded,
  onToggle,
  query,
}: {
  row: LogRow;
  expanded: boolean;
  onToggle: () => void;
  query: string;
}) {
  const [time, millis] = formatLogTime(row.timeMs).split('.');
  const matchAt = query
    ? row.message.toLowerCase().indexOf(query.toLowerCase())
    : -1;

  return (
    <div
      className={classNames(
        'flex items-baseline gap-2 px-1.5 py-px border-l-2 hover:bg-background-70',
        LEVEL_CLASSES[row.level]
      )}
    >
      <span className="shrink-0 w-[13ch] mobile:w-[8ch] text-background-30 tabular-nums">
        {time}
        <span className="mobile:hidden">.{millis}</span>
      </span>
      <span
        className="shrink-0 w-2 h-2 rounded-full self-start mt-[calc((1lh_-_0.5rem)/2)]"
        style={{ backgroundColor: sourceColor(row.source) }}
      />
      {row.logger ? (
        <span
          className="shrink-0 w-[16ch] text-background-20 truncate mobile:hidden"
          title={row.logger}
        >
          {row.logger}
        </span>
      ) : (
        <Localized id={`settings-logs-source-${sourceKey(row.source)}`}>
          <span className="shrink-0 w-[16ch] text-background-20 truncate mobile:hidden" />
        </Localized>
      )}
      <div className="flex-1 min-w-0 whitespace-pre-wrap break-words">
        {matchAt < 0 ? (
          row.message
        ) : (
          <>
            {row.message.slice(0, matchAt)}
            <mark className="bg-status-warning/35 text-inherit rounded-sm">
              {row.message.slice(matchAt, matchAt + query.length)}
            </mark>
            {row.message.slice(matchAt + query.length)}
          </>
        )}
        {row.stackTrace && (
          <Clickable
            onClick={onToggle}
            expanded={expanded}
            className="ml-2 inline-flex items-center gap-1 fill-accent-background-10"
          >
            <Typography
              tag="span"
              color="text-accent-background-10"
              id={
                expanded
                  ? 'settings-logs-details-hide'
                  : 'settings-logs-details-show'
              }
            />
            <span
              className={classNames(
                'flex transition-transform',
                expanded && 'rotate-180'
              )}
            >
              <ArrowDownIcon size={10} />
            </span>
          </Clickable>
        )}
        {row.stackTrace && expanded && (
          <pre className="text-status-critical/85 overflow-x-auto mb-1">
            {row.stackTrace}
          </pre>
        )}
      </div>
    </div>
  );
}

export function LogList({ rows, query }: { rows: LogRow[]; query: string }) {
  const scrollRef = useRef<HTMLDivElement>(null);
  const lastScrollTop = useRef(0);
  const [atBottom, setAtBottom] = useState(true);
  const [lastSeenId, setLastSeenId] = useState(-1);
  const [expanded, setExpanded] = useState<Set<number>>(new Set());

  const virtualizer = useVirtualizer({
    count: rows.length,
    getScrollElement: () => scrollRef.current,
    estimateSize: () => 20,
    overscan: 20,
    getItemKey: (index) => rows[index].id,
  });

  const newestId = useMemo(
    () => rows.reduce((newest, row) => Math.max(newest, row.id), -1),
    [rows]
  );
  const unseen = useMemo(
    () => (atBottom ? 0 : rows.filter((row) => row.id > lastSeenId).length),
    [rows, atBottom, lastSeenId]
  );

  useLayoutEffect(() => {
    if (!atBottom || rows.length === 0) return;
    virtualizer.scrollToIndex(rows.length - 1, { align: 'end' });
    setLastSeenId(newestId);
  }, [newestId, rows.length, atBottom]);

  const onScroll = () => {
    const el = scrollRef.current;
    if (!el) return;
    const movedUp = el.scrollTop < lastScrollTop.current;
    lastScrollTop.current = el.scrollTop;
    const bottom = el.scrollTop + el.clientHeight >= el.scrollHeight - 24;
    // The virtualizer's own corrections only move down, only the user scrolls up
    if (bottom) {
      setAtBottom(true);
      return;
    }
    if (movedUp) setAtBottom(false);
  };

  const toggle = (id: number) =>
    setExpanded((current) => {
      const next = new Set(current);
      if (!next.delete(id)) next.add(id);
      return next;
    });

  return (
    <div className="relative flex-grow h-0 flex flex-col">
      <div
        ref={scrollRef}
        onScroll={onScroll}
        className="flex-grow overflow-auto font-mono text-[12px] leading-[1.55] select-text"
      >
        <div
          className="relative w-full"
          style={{ height: virtualizer.getTotalSize() }}
        >
          {virtualizer.getVirtualItems().map((item) => {
            const row = rows[item.index];
            return (
              <div
                key={item.key}
                data-index={item.index}
                ref={virtualizer.measureElement}
                className="absolute left-0 w-full"
                style={{ transform: `translateY(${item.start}px)` }}
              >
                <LogRowView
                  row={row}
                  expanded={expanded.has(row.id)}
                  onToggle={() => toggle(row.id)}
                  query={query}
                />
              </div>
            );
          })}
        </div>
      </div>
      {unseen > 0 && (
        <Clickable
          onClick={() => setAtBottom(true)}
          className="absolute left-1/2 -translate-x-1/2 bottom-3 flex items-center gap-2 bg-accent-background-30 hover:bg-accent-background-20 rounded-full px-4 py-1.5 shadow-lg fill-background-10"
        >
          <ArrowDownIcon size={12} />
          <Typography id="settings-logs-new_lines" vars={{ count: unseen }} />
        </Clickable>
      )}
    </div>
  );
}
