/* eslint-disable l10n/message-id -- dev demo uses raw strings, t() shows missing ids as-is */
import { createSignal } from 'solid-js';
import TrackingIcon from '~icons/material-symbols/sports-martial-arts-rounded';
import { formats } from '@/lib/format';
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
        <SettingsGroup title="Filtering" loading={loading()}>
          <SettingRow
            title="Filtering type"
            description="Choose the filtering type for your trackers."
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
              title="Filter strength"
              description="How strong the selected filter is."
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
        <SettingsGroup title="Leg tracking" loading={loading()}>
          <SettingRow
            title="Skating correction"
            description="Corrects for foot sliding, but can decrease accuracy."
            pressable
            childrenDisabled={!skating()}
            childrenDisabledReason="Turn on skating correction first"
            control={(w) => (
              <Toggle value={skating()} onChange={setSkating} {...w} />
            )}
          >
            <SettingRow
              title="Skating correction strength"
              description="A higher value will result in more aggressive correction."
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
            title="Floor clip"
            description="Prevents feet clipping through the floor."
            pressable
            control={(w) => (
              <Toggle value={floorClip()} onChange={setFloorClip} {...w} />
            )}
          />
          <SettingRow
            title="Disabled row"
            description="Reason shows as the control's tooltip."
            disabled
            disabledReason="Not available"
            error="An error message placed by the row"
            control={(w) => <Toggle value={false} onChange={() => {}} {...w} />}
          />
        </SettingsGroup>
        <SettingsAdvanced>
          <SettingsGroup>
            <SettingRow
              title="Advanced row"
              control={(w) => <Toggle value onChange={() => {}} {...w} />}
            />
          </SettingsGroup>
        </SettingsAdvanced>
        <SettingsAdvanced title="Danger zone" danger>
          <SettingsGroup>
            <SettingRow
              title="Danger row"
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
