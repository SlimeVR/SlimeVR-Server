import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { PopoverMenu, PopoverMenuItem } from '@/components/commons/PopoverMenu';
import { DownloadIcon } from '@/components/commons/icon/DownloadIcon';
import { ArrowDownIcon } from '@/components/commons/icon/ArrowIcons';
import { useBreakpoint } from '@/hooks/breakpoint';
import { LogFileRow, sourceKey } from '@/hooks/logs';

/** Exports the ticked files, or with nothing ticked, opens a menu: every log file or one source's files. */
export function ExportMenu({
  files,
  selectedFiles,
  busy,
  onExport,
}: {
  files: LogFileRow[];
  selectedFiles: LogFileRow[];
  busy: boolean;
  onExport: (files: LogFileRow[]) => void;
}) {
  const { l10n } = useLocalization();
  const { isMobile } = useBreakpoint('mobile');
  const sources = [...new Set(files.map((file) => file.source))];
  const exportId = 'settings-logs-export_all';

  if (selectedFiles.length > 0) {
    return (
      <Button
        variant="primary"
        className={busy ? 'animate-pulse pointer-events-none' : undefined}
        onClick={() => onExport(selectedFiles)}
        icon={<DownloadIcon width={16} className="fill-background-10" />}
        badge={isMobile ? selectedFiles.length : undefined}
        id="settings-logs-export-selected"
        vars={{ count: selectedFiles.length }}
        iconOnly={isMobile}
      />
    );
  }

  return (
    <PopoverMenu
      drawerTitle={
        <Typography variant="section-title" id="settings-logs-export-title" />
      }
      trigger={(triggerProps, open) => (
        <Button
          variant="primary"
          id={isMobile ? exportId : undefined}
          iconOnly={isMobile}
          className={busy ? 'animate-pulse pointer-events-none' : undefined}
          disabled={files.length === 0}
          {...triggerProps}
          icon={<DownloadIcon width={16} className="fill-background-10" />}
        >
          <span className="flex items-center gap-2">
            <Typography tag="span" id={exportId} />
            <span
              className={classNames(
                'flex fill-background-10 transition-transform',
                open && 'rotate-180'
              )}
            >
              <ArrowDownIcon size={12} />
            </span>
          </span>
        </Button>
      )}
    >
      {({ close }) => (
        <>
          <PopoverMenuItem
            onClick={() => {
              close();
              onExport(files);
            }}
            description={
              <Typography
                color="text-background-20"
                id="settings-logs-export-all-description"
              />
            }
          >
            <Typography bold id="settings-logs-export-all" />
          </PopoverMenuItem>
          {sources.length > 1 &&
            sources.map((source) => (
              <PopoverMenuItem
                key={sourceKey(source)}
                onClick={() => {
                  close();
                  onExport(files.filter((file) => file.source === source));
                }}
              >
                <Typography
                  bold
                  id="settings-logs-export-source"
                  vars={{
                    source: l10n.getString(
                      `settings-logs-source-${sourceKey(source)}`
                    ),
                  }}
                />
              </PopoverMenuItem>
            ))}
        </>
      )}
    </PopoverMenu>
  );
}
