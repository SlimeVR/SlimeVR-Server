import { useVirtualizer } from '@tanstack/react-virtual';
import { ReactNode, useLayoutEffect, useMemo, useRef, useState } from 'react';
import { Clickable } from '@/components/commons/Clickable';
import { Typography } from '@/components/commons/Typography';
import { ArrowDownIcon } from '@/components/commons/icon/ArrowIcons';

export function FollowList<T extends { id: number }>({
  rows,
  renderRow,
}: {
  rows: T[];
  renderRow: (row: T) => ReactNode;
}) {
  const scrollRef = useRef<HTMLDivElement>(null);
  const lastScrollTop = useRef(0);
  const [atBottom, setAtBottom] = useState(true);
  const [lastSeenId, setLastSeenId] = useState(-1);

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
  }, [newestId, rows, atBottom]);

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
          {virtualizer.getVirtualItems().map((item) => (
            <div
              key={item.key}
              data-index={item.index}
              ref={virtualizer.measureElement}
              className="absolute left-0 w-full"
              style={{ transform: `translateY(${item.start}px)` }}
            >
              {renderRow(rows[item.index])}
            </div>
          ))}
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
