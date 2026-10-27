import classNames from 'classnames';
import { useMemo, useState } from 'react';
import { Clickable } from '@/components/commons/Clickable';
import { Typography } from '@/components/commons/Typography';
import { ArrowDownIcon } from '@/components/commons/icon/ArrowIcons';
import { formatLogTime } from '@/hooks/logs';
import { AnsiSpan } from '@/utils/ansi';
import { FollowList } from './FollowList';
import { LEVEL_CLASSES, LogViewLine } from './log-line';

const MAX_LABEL_WIDTH = 16;

function MessageSpans({ spans, query }: { spans: AnsiSpan[]; query: string }) {
  const text = spans.map((span) => span.text).join('');
  const matchAt = query ? text.toLowerCase().indexOf(query.toLowerCase()) : -1;
  const matchEnd = matchAt + query.length;
  let offset = 0;

  return spans.map((span, i) => {
    const start = offset;
    offset += span.text.length;
    const from = Math.max(matchAt - start, 0);
    const to = Math.min(matchEnd - start, span.text.length);
    if (matchAt < 0 || from >= to) {
      return (
        <span key={i} style={span.style}>
          {span.text}
        </span>
      );
    }
    return (
      <span key={i} style={span.style}>
        {span.text.slice(0, from)}
        <mark className="bg-status-warning/35 text-inherit rounded-sm">
          {span.text.slice(from, to)}
        </mark>
        {span.text.slice(to)}
      </span>
    );
  });
}

function LogLineRow({
  line,
  query,
  labelWidth,
  expanded,
  onToggle,
}: {
  line: LogViewLine;
  query: string;
  labelWidth: number;
  expanded: boolean;
  onToggle: () => void;
}) {
  const [time, millis] = formatLogTime(line.timeMs).split('.');
  const spans = line.spans ?? [{ text: line.message }];

  return (
    <div
      className={classNames(
        'flex items-baseline gap-2 px-1.5 py-px border-l-2 hover:bg-background-70',
        line.level === null ? 'border-transparent' : LEVEL_CLASSES[line.level]
      )}
    >
      <span
        className="shrink-0 w-[13ch] mobile:w-[8ch] text-background-30 tabular-nums"
        title={line.timeTitle ?? undefined}
      >
        {time}
        <span className="mobile:hidden">.{millis}</span>
      </span>
      {labelWidth > 0 && (
        <>
          <span
            className={classNames(
              'shrink-0 w-2 h-2 rounded-full self-start mt-[calc((1lh_-_0.5rem)/2)]',
              line.label === null && 'border border-background-40'
            )}
            style={{ backgroundColor: line.color ?? undefined }}
          />
          <span
            className={classNames(
              'shrink-0 truncate mobile:hidden',
              line.label === null ? 'text-background-40' : 'text-background-20'
            )}
            style={{ width: `${labelWidth}ch` }}
            title={line.label ?? undefined}
          >
            {line.label ?? '-'}
          </span>
        </>
      )}
      <div className="flex-1 min-w-0 whitespace-pre-wrap break-words">
        <MessageSpans spans={spans} query={query} />
        {line.message === '' && '\n'}
        {line.details && (
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
        {line.details && expanded && (
          <pre className="text-status-critical/85 overflow-x-auto mb-1">
            {line.details}
          </pre>
        )}
      </div>
    </div>
  );
}

export function LogView({
  lines,
  query,
}: {
  lines: LogViewLine[];
  query: string;
}) {
  const [expanded, setExpanded] = useState<Set<number>>(new Set());
  const labelWidth = useMemo(
    () =>
      Math.min(
        MAX_LABEL_WIDTH,
        lines.reduce(
          (width, line) => Math.max(width, line.label?.length ?? 0),
          0
        )
      ),
    [lines]
  );

  const toggle = (id: number) =>
    setExpanded((current) => {
      const next = new Set(current);
      if (!next.delete(id)) next.add(id);
      return next;
    });

  return (
    <FollowList
      rows={lines}
      renderRow={(line) => (
        <LogLineRow
          line={line}
          query={query}
          labelWidth={labelWidth}
          expanded={expanded.has(line.id)}
          onToggle={() => toggle(line.id)}
        />
      )}
    />
  );
}
