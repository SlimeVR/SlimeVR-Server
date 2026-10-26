import { FilteringType } from 'solarxr-protocol';
import { settingRows } from '@/domain/settings/setting-field';
import { formats } from '@/lib/format';
import { NumberField } from '@/ui/number-field';
import { type ChoiceOption, RadioGroup } from '@/ui/radio-group';
import { type SettingBinding, SettingRow } from '@/ui/setting-row';
import { SettingsAdvanced, SettingsGroup } from '@/ui/settings-group';
import { Toggle } from '@/ui/toggle';
import { SettingsPane } from '../settings-pane';
import { trackingSettingsPage } from './tracking-def';
import { skeletonDefaults, skeletonSettings } from './tracking-settings';

const { filtering, legFk, position, constraints, ratios } =
  trackingSettingsPage.groups;

const filteringOptions: ChoiceOption<FilteringType>[] = [
  {
    value: FilteringType.NONE,
    label: 'settings-general-tracker_mechanics-filtering-type-none',
    description:
      'settings-general-tracker_mechanics-filtering-type-none-description',
  },
  {
    value: FilteringType.SMOOTHING,
    label: 'settings-general-tracker_mechanics-filtering-type-smoothing',
    description:
      'settings-general-tracker_mechanics-filtering-type-smoothing-description',
  },
  {
    value: FilteringType.PREDICTION,
    label: 'settings-general-tracker_mechanics-filtering-type-prediction',
    description:
      'settings-general-tracker_mechanics-filtering-type-prediction-description',
  },
];

const filteringRow = settingRows(
  skeletonSettings,
  skeletonDefaults,
  'filtering'
);
const toggleRow = settingRows(skeletonSettings, skeletonDefaults, 'toggles');
const ratioRow = settingRows(skeletonSettings, skeletonDefaults, 'ratios');

function ToggleRow(props: { bind: SettingBinding<boolean> }) {
  return (
    <SettingRow
      bind={props.bind}
      pressable
      control={(f) => <Toggle {...f} />}
    />
  );
}

function RatioRow(props: { bind: SettingBinding<number>; min?: number }) {
  return (
    <SettingRow
      bind={props.bind}
      control={(f) => (
        <NumberField
          {...f}
          min={props.min ?? 0}
          max={1}
          step={0.05}
          format={formats.percent}
        />
      )}
    />
  );
}

export function TrackingPage() {
  const filteringType = filteringRow(filtering.rows.type);
  const skating = toggleRow(legFk.rows.skatingCorrection);

  return (
    <SettingsPane page={trackingSettingsPage}>
      <SettingsGroup group={filtering}>
        <SettingRow
          bind={filteringType}
          controlPlacement="below"
          childrenDisabled={filteringType.field.value === FilteringType.NONE}
          control={(f) => (
            <RadioGroup
              {...f}
              options={filteringOptions}
              class="grid gap-sm @lg:grid-cols-3"
            />
          )}
        >
          <RatioRow bind={filteringRow(filtering.rows.amount)} />
        </SettingRow>
      </SettingsGroup>

      <SettingsGroup group={legFk}>
        <SettingRow
          bind={skating}
          pressable
          childrenDisabled={!skating.field.value}
          control={(f) => <Toggle {...f} />}
        >
          <RatioRow
            bind={ratioRow(legFk.rows.skatingCorrectionStrength)}
            min={0.05}
          />
        </SettingRow>
        <ToggleRow bind={toggleRow(legFk.rows.floorClip)} />
        <ToggleRow bind={toggleRow(legFk.rows.footPlant)} />
        <ToggleRow bind={toggleRow(legFk.rows.toeSnap)} />
      </SettingsGroup>

      <SettingsGroup group={position}>
        <ToggleRow bind={toggleRow(position.rows.useTrackerPositions)} />
        <ToggleRow bind={toggleRow(position.rows.mocapMode)} />
      </SettingsGroup>

      <SettingsAdvanced>
        <SettingsGroup group={constraints}>
          <ToggleRow bind={toggleRow(constraints.rows.enforceConstraints)} />
        </SettingsGroup>

        <SettingsGroup group={ratios}>
          <RatioRow bind={ratioRow(ratios.rows.imputeSpineFromUpperToLower)} />
          <RatioRow bind={ratioRow(ratios.rows.spineCurvature)} />
          <RatioRow
            bind={ratioRow(ratios.rows.interpolateUpperLegsTwistWithLowerLegs)}
          />
        </SettingsGroup>
      </SettingsAdvanced>
    </SettingsPane>
  );
}
