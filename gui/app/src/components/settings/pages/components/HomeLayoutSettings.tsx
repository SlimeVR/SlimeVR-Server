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
import { DropdownInside } from '@/components/commons/Dropdown';
import { SettingRow, SettingsGroup } from '@/components/settings/SettingRow';
import classNames from 'classnames';
import { ReactNode } from 'react';
import { track } from '@/utils/sentry';
import { isEqual } from '@react-hookz/deep-equal';
import { defineSettingsPage } from '@/components/settings/settings-definitions';

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

export function TrackersTableColumnToggles({
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
  );
}

export function TrackersTableColumnsSettings({
  variant,
}: {
  variant: 'settings' | 'modal';
}) {
  return (
    <div className="flex flex-col gap-2">
      <div className="flex flex-col pt-4 pb-2">
        <Typography bold id="settings-home-table_columns" />
        <Typography id="settings-home-table_columns-desc" />
      </div>
      <TrackersTableColumnToggles variant={variant} />
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

export function TrackerDisplayToggles({
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
  );
}

export function TrackerDisplaySettings({
  variant,
}: {
  variant: 'settings' | 'modal';
}) {
  return (
    <div className="flex flex-col gap-2">
      <div className="flex flex-col pt-4 pb-2">
        <Typography bold id="settings-home-tracker_display" />
        <Typography id="settings-home-tracker_display-desc" />
      </div>
      <TrackerDisplayToggles variant={variant} />
    </div>
  );
}

/**
 * The home screen's settings, shared by its settings page and the quick
 * settings modal on the home screen so both read the same.
 */
export const homeSettingsPage = defineSettingsPage({
  path: 'home',
  title: 'settings-sidebar-home',
  groups: {
    main: {
      rows: {
        layout: {
          title: 'settings-home-list-layout',
          description: 'settings-home-list-layout-desc',
        },
        tableColumns: {
          title: 'settings-home-table_columns',
          description: 'settings-home-table_columns-desc',
        },
      },
    },
    display: {
      title: 'settings-home-display_group',
      rows: {
        skeletonMesh: {
          title: 'settings-interface-behavior-skeleton_mesh',
          description: 'settings-interface-behavior-skeleton_mesh-description',
        },
        trackerDisplay: {
          title: 'settings-home-tracker_display',
          description: 'settings-home-tracker_display-desc',
        },
      },
    },
  },
});

const { groups } = homeSettingsPage;

export function HomeLayoutSettings({
  variant,
}: {
  variant: 'settings' | 'modal';
}) {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const mesh =
    (config?.skeletonPreviewStyle ?? defaultConfig.skeletonPreviewStyle) ===
    'mesh';
  const columns = config?.trackersTableColumns ?? defaultTrackersTableColumns;
  const trackerDisplay = config?.trackerDisplay ?? defaultTrackerDisplay;

  const resetLayout = {
    reset: {
      modified:
        (config?.homeLayout ?? defaultConfig.homeLayout) !==
        defaultConfig.homeLayout,
      onReset: () => setConfig({ homeLayout: defaultConfig.homeLayout }),
    },
  };
  const resetColumns = {
    reset: {
      modified: !isEqual(columns, defaultTrackersTableColumns),
      onReset: () =>
        setConfig({ trackersTableColumns: defaultTrackersTableColumns }),
    },
  };
  const resetMesh = {
    reset: {
      modified: !mesh === (defaultConfig.skeletonPreviewStyle === 'mesh'),
      onReset: () =>
        setConfig({ skeletonPreviewStyle: defaultConfig.skeletonPreviewStyle }),
    },
  };
  const resetTrackerDisplay = {
    reset: {
      modified: !isEqual(trackerDisplay, defaultTrackerDisplay),
      onReset: () => setConfig({ trackerDisplay: defaultTrackerDisplay }),
    },
  };

  const setLayout = (layout: Config['homeLayout']) => {
    setConfig({ homeLayout: layout });
    track('change_layout', { layout, from: variant });
  };

  return (
    <div className="flex flex-col">
      <SettingsGroup group={groups.main}>
        <SettingRow
          setting={groups.main.rows.layout}
          {...resetLayout}
          below={
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
          }
        />
        <SettingRow
          setting={groups.main.rows.tableColumns}
          disabled={config?.homeLayout !== 'table'}
          {...resetColumns}
          below={<TrackersTableColumnToggles variant={variant} />}
        />
      </SettingsGroup>

      <SettingsGroup group={groups.display}>
        <SettingRow
          setting={groups.display.rows.skeletonMesh}
          controlWidth="wide"
          {...resetMesh}
          control={
            <DropdownInside
              name="skeletonPreviewStyle"
              display="block"
              direction="down"
              placeholder=""
              value={mesh ? 'mesh' : 'lines'}
              onChange={(value) =>
                setConfig({
                  skeletonPreviewStyle: value as Config['skeletonPreviewStyle'],
                })
              }
              items={[
                {
                  label: l10n.getString(
                    'settings-interface-behavior-skeleton_mesh-lines'
                  ),
                  value: 'lines',
                },
                {
                  label: l10n.getString(
                    'settings-interface-behavior-skeleton_mesh-mesh'
                  ),
                  value: 'mesh',
                },
              ]}
            />
          }
        />
        <SettingRow
          setting={groups.display.rows.trackerDisplay}
          {...resetTrackerDisplay}
          below={<TrackerDisplayToggles variant={variant} />}
        />
      </SettingsGroup>
    </div>
  );
}
