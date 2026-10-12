import { CheckboxInternal } from '@/components/commons/Checkbox';
import { Clickable } from '@/components/commons/Clickable';
import { Typography } from '@/components/commons/Typography';
import {
  Config,
  defaultConfig,
  defaultTrackerDisplay,
  defaultTrackersTableColumns,
  TrackerDisplayConfig,
  trackersTableColumnOrder,
  TrackersTableColumnsConfig,
  useConfig,
} from '@/hooks/config';
import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { ReactNode } from 'react';
import { track } from '@/utils/sentry';

export function LayoutSelector({
  children,
  name,
  active = false,
  onClick,
}: {
  children: ReactNode;
  name: string;
  active: boolean;
  onClick: () => void;
}) {
  return (
    <Clickable
      pressed={active}
      className={classNames(
        'w-40 aspect-video bg-background-70 flex-col flex rounded-lg border-2 group cursor-pointer text-left',
        {
          'border-accent-background-20': active,
          'border-background-50 hover:border-background-40': !active,
        }
      )}
      onClick={onClick}
    >
      <div className="px-2 pt-2 pb-1">
        <Typography id={name} />
      </div>
      <div
        className={classNames('h-[2px] w-full mb-2', {
          'group-hover:bg-background-40 bg-background-50': !active,
          'bg-accent-background-20': active,
        })}
      />
      {children}
    </Clickable>
  );
}

const trackersTableColumnLabel: Record<
  keyof TrackersTableColumnsConfig,
  string
> = {
  type: 'tracker-table-column-type',
  battery: 'tracker-table-column-battery',
  ping: 'tracker-table-column-ping',
  tps: 'tracker-table-column-tps',
  rotation: 'tracker-table-column-rotation',
  temperature: 'tracker-table-column-temperature',
  linearAcceleration: 'tracker-table-column-linear-acceleration',
  position: 'tracker-table-column-position',
  stayAligned: 'tracker-table-column-stay_aligned',
  url: 'tracker-table-column-url',
};

export function TrackersTableColumnsSettings({
  variant,
}: {
  variant: 'settings' | 'modal';
}) {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const columns = config?.trackersTableColumns ?? defaultTrackersTableColumns;

  const toggleColumn = (key: keyof TrackersTableColumnsConfig) => {
    setConfig({
      trackersTableColumns: { ...columns, [key]: !columns[key] },
    });
  };

  return (
    <div className="flex flex-col gap-2">
      <div className="flex flex-col pt-4 pb-2">
        <Typography bold id="settings-home-table_columns" />
        <Typography color="secondary" id="settings-home-table_columns-desc" />
      </div>
      <div className="grid sm:grid-cols-2 md:grid-cols-3 gap-2">
        {trackersTableColumnOrder.map((key) => (
          <CheckboxInternal
            key={key}
            variant="toggle"
            outlined
            color={variant === 'settings' ? 'primary' : 'secondary'}
            name={key}
            checked={columns[key]}
            onChange={() => toggleColumn(key)}
            label={l10n.getString(trackersTableColumnLabel[key])}
          />
        ))}
      </div>
    </div>
  );
}

const trackerDisplayToggleOrder: (keyof TrackerDisplayConfig)[] = [
  'showBatteryVoltage',
  'showNumericSignal',
];

const trackerDisplayToggleLabel: Record<keyof TrackerDisplayConfig, string> = {
  showBatteryVoltage: 'settings-home-tracker_display-battery_voltage',
  showNumericSignal: 'settings-home-tracker_display-numeric_signal',
};

export function TrackerDisplaySettings({
  variant,
}: {
  variant: 'settings' | 'modal';
}) {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const trackerDisplay = config?.trackerDisplay ?? defaultTrackerDisplay;

  const toggle = (key: keyof TrackerDisplayConfig) => {
    setConfig({
      trackerDisplay: { ...trackerDisplay, [key]: !trackerDisplay[key] },
    });
  };

  return (
    <div className="flex flex-col gap-2">
      <div className="flex flex-col pt-4 pb-2">
        <Typography bold id="settings-home-tracker_display" />
        <Typography color="secondary" id="settings-home-tracker_display-desc" />
      </div>
      <div className="grid sm:grid-cols-2 gap-2">
        {trackerDisplayToggleOrder.map((key) => (
          <CheckboxInternal
            key={key}
            variant="toggle"
            outlined
            color={variant === 'settings' ? 'primary' : 'secondary'}
            name={key}
            checked={trackerDisplay[key]}
            onChange={() => toggle(key)}
            label={l10n.getString(trackerDisplayToggleLabel[key])}
          />
        ))}
      </div>
    </div>
  );
}

export function SkeletonPreviewSettings({
  variant,
}: {
  variant: 'settings' | 'modal';
}) {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const mesh =
    (config?.skeletonPreviewStyle ?? defaultConfig.skeletonPreviewStyle) ===
    'mesh';

  return (
    <div className="flex flex-col gap-2">
      <div className="flex flex-col pt-4 pb-2">
        <Typography bold id="settings-interface-behavior-skeleton_mesh" />
        <Typography
          color="secondary"
          id="settings-interface-behavior-skeleton_mesh-description"
        />
      </div>
      <div className="grid sm:grid-cols-2 gap-2">
        <CheckboxInternal
          variant="toggle"
          outlined
          color={variant === 'settings' ? 'primary' : 'secondary'}
          name="skeletonMesh"
          checked={mesh}
          onChange={() =>
            setConfig({ skeletonPreviewStyle: mesh ? 'lines' : 'mesh' })
          }
          label={l10n.getString(
            'settings-interface-behavior-skeleton_mesh-label'
          )}
        />
      </div>
    </div>
  );
}

export function HomeLayoutSettings({
  variant,
}: {
  variant: 'settings' | 'modal';
}) {
  const { config, setConfig } = useConfig();

  const setLayout = (layout: Config['homeLayout']) => {
    setConfig({ homeLayout: layout });
    track('change_layout', { layout, from: variant });
  };

  return (
    <div className="flex flex-col gap-2">
      <div className="flex flex-col pt-4 pb-2">
        <Typography bold id="settings-home-list-layout" />
        <Typography color="secondary" id="settings-home-list-layout-desc" />
      </div>
      <div className="flex gap-4">
        <LayoutSelector
          name="settings-home-list-layout-grid"
          active={config?.homeLayout === 'default'}
          onClick={() => setLayout('default')}
        >
          <div className="grid grid-cols-2 gap-2 p-2">
            <div className="h-2 rounded-lg bg-background-40" />
            <div className="h-2 rounded-lg bg-background-40" />
            <div className="h-2 rounded-lg bg-background-40" />
            <div className="h-2 rounded-lg bg-background-40" />
          </div>
        </LayoutSelector>
        <LayoutSelector
          name="settings-home-list-layout-table"
          active={config?.homeLayout === 'table'}
          onClick={() => setLayout('table')}
        >
          <div className="grid grid-cols-1 gap-2 p-2">
            <div className="h-2 rounded-lg bg-background-40" />
            <div className="h-2 rounded-lg bg-background-40" />
            <div className="h-2 rounded-lg bg-background-40" />
            <div className="h-2 rounded-lg bg-background-40" />
          </div>
        </LayoutSelector>
      </div>
      <SkeletonPreviewSettings variant={variant} />
      <TrackerDisplaySettings variant={variant} />
      {config?.homeLayout === 'table' && (
        <TrackersTableColumnsSettings variant={variant} />
      )}
    </div>
  );
}
