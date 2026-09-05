import { Typography } from '@/components/commons/Typography';
import { DongleTelemetry } from '@/components/tracker/DongleTelemetry';
import { TrackerConnectionGroup } from '@/store/app-store';
import { useLocalization } from '@fluent/react';
import { ConnectionGroupIcon } from '@/components/tracker/TrackerConnectionGroup';
import { DongleStatus } from 'solarxr-protocol';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';
import { useEffect } from 'react';

export function GroupTelemetryOverlay({
  group,
  onClose,
}: {
  group: TrackerConnectionGroup | null;
  onClose: () => void;
}) {
  const { l10n } = useLocalization();

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [onClose]);

  if (!group) return null;

  const trackers = [...group.assigned, ...group.unassigned];
  const disconnected =
    group.kind === 'dongle' && group.status === DongleStatus.DISCONNECTED;

  const groupName =
    group.kind === 'dongle'
      ? group.dongleName || 'Dongle'
      : l10n.getString(`home-connection_group-${group.kind}`);

  return (
    <div className="absolute inset-0 z-40 flex items-center justify-center px-4 py-2">
      <div
        className="absolute inset-0 bg-background-90 bg-opacity-50 rounded-md"
        onClick={onClose}
      />

      <div className="relative z-50 w-full max-h-full bg-background-70 p-6 rounded-lg text-background-10 border border-background-50/50 overflow-y-auto flex flex-col gap-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <ConnectionGroupIcon
              kind={group.kind}
              disconnected={disconnected}
            />
            <Typography variant="mobile-title">{groupName}</Typography>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="flex items-center justify-center fill-background-10 bg-background-50 hover:bg-background-40 rounded-full w-9 h-9 shrink-0 cursor-pointer transition-colors"
          >
            <CrossIcon size={16} />
          </button>
        </div>
        <DongleTelemetry trackers={trackers} variant="modal" />
      </div>
    </div>
  );
}
