/* eslint-disable l10n/message-id -- dev demo uses raw strings, t() shows missing ids as-is */
import { createSignal } from 'solid-js';
import TrackingIcon from '~icons/material-symbols/sports-martial-arts-rounded';
import { formats } from '@/lib/format';
import { defineSettingsPage } from '@/lib/settings-def';
import { TextButton } from '@/ui/button';
import { NumberField } from '@/ui/number-field';
import { Page } from '@/ui/page';
import { RadioGroup } from '@/ui/radio-group';
import { Section } from '@/ui/section';
import { SettingRow } from '@/ui/setting-row';
import { SettingsAdvanced, SettingsGroup } from '@/ui/settings-group';
import { Toggle } from '@/ui/toggle';
import { Toolbar } from '@/ui/toolbar';

type Filter = 'none' | 'smoothing' | 'prediction';

const demo = defineSettingsPage({
  path: 'demo',
  title: 'Tracking',
  icon: TrackingIcon,
  groups: {
    filtering: {
      title: 'Filtering',
      rows: {
        type: {
          title: 'Filtering type',
          description: 'Choose the filtering type for your trackers.',
        },
        strength: {
          title: 'Filter strength',
          description: 'How strong the selected filter is.',
        },
      },
    },
    legs: {
      title: 'Leg tracking',
      rows: {
        skating: {
          title: 'Skating correction',
          description: 'Corrects for foot sliding, but can decrease accuracy.',
        },
        skatingStrength: {
          title: 'Skating correction strength',
          description:
            'A higher value will result in more aggressive correction.',
        },
        floorClip: {
          title: 'Floor clip',
          description: 'Prevents feet clipping through the floor.',
        },
        disabled: {
          title: 'Disabled row',
          description: "Reason shows as the control's tooltip.",
        },
      },
    },
    advanced: { rows: { row: { title: 'Advanced row' } } },
    danger: { rows: { row: { title: 'Danger row' } } },
  },
});
const { filtering, legs } = demo.groups;

export function ContainersDemo() {
  const [filter, setFilter] = createSignal<Filter>('none');
  const [strength, setStrength] = createSignal(0.25);
  const [skating, setSkating] = createSignal(true);
  const [skatingStrength, setSkatingStrength] = createSignal(0.5);
  const [floorClip, setFloorClip] = createSignal(true);
  const [loading, setLoading] = createSignal(false);

  return (
    <div class="flex flex-col gap-xl">
      <Page
        title="Tracking"
        icon={<TrackingIcon class="icon" />}
        gap="2xl"
        actions={
          <TextButton
            label="Toggle group loading"
            size="small"
            onClick={() => setLoading(!loading())}
          />
        }
      >
        <SettingsGroup group={filtering} loading={loading()}>
          <SettingRow
            setting={filtering.rows.type}
            controlPlacement="below"
            childrenDisabled={filter() === 'none'}
            childrenDisabledReason="Pick a filter first"
            control={(w) => (
              <RadioGroup
                value={filter()}
                onChange={setFilter}
                class="grid gap-sm @lg:grid-cols-3"
                options={[
                  {
                    value: 'none',
                    label: 'No filtering',
                    description: 'Use rotations as is.',
                  },
                  {
                    value: 'smoothing',
                    label: 'Smoothing',
                    description: 'Smooths movements but adds some latency.',
                  },
                  {
                    value: 'prediction',
                    label: 'Prediction',
                    description: 'Reduces latency, increases jitter.',
                  },
                ]}
                {...w}
              />
            )}
          >
            <SettingRow
              setting={filtering.rows.strength}
              control={(w) => (
                <NumberField
                  value={strength()}
                  onChange={setStrength}
                  min={0}
                  max={1}
                  step={0.05}
                  format={formats.percent}
                  {...w}
                />
              )}
            />
          </SettingRow>
        </SettingsGroup>
        <SettingsGroup group={legs} loading={loading()}>
          <SettingRow
            setting={legs.rows.skating}
            pressable
            childrenDisabled={!skating()}
            childrenDisabledReason="Turn on skating correction first"
            control={(w) => (
              <Toggle value={skating()} onChange={setSkating} {...w} />
            )}
          >
            <SettingRow
              setting={legs.rows.skatingStrength}
              control={(w) => (
                <NumberField
                  value={skatingStrength()}
                  onChange={setSkatingStrength}
                  min={0}
                  max={1}
                  step={0.05}
                  format={formats.percent}
                  {...w}
                />
              )}
            />
          </SettingRow>
          <SettingRow
            setting={legs.rows.floorClip}
            pressable
            control={(w) => (
              <Toggle value={floorClip()} onChange={setFloorClip} {...w} />
            )}
          />
          <SettingRow
            setting={legs.rows.disabled}
            disabled
            disabledReason="Not available"
            error="An error message placed by the row"
            control={(w) => <Toggle value={false} onChange={() => {}} {...w} />}
          />
        </SettingsGroup>
        <SettingsAdvanced>
          <SettingsGroup>
            <SettingRow
              setting={demo.groups.advanced.rows.row}
              control={(w) => <Toggle value onChange={() => {}} {...w} />}
            />
          </SettingsGroup>
        </SettingsAdvanced>
        <SettingsAdvanced title="Danger zone" danger>
          <SettingsGroup>
            <SettingRow
              setting={demo.groups.danger.rows.row}
              control={() => <TextButton label="Reset" variant="danger" />}
            />
          </SettingsGroup>
        </SettingsAdvanced>
      </Page>
      <Section
        title="Section"
        description="A titled block of a page."
        actions={
          <Toolbar label="Section actions">
            <TextButton label="One" size="small" />
            <TextButton label="Two" size="small" variant="ghost" />
          </Toolbar>
        }
      >
        <p>Section body on the section surface.</p>
      </Section>
    </div>
  );
}
