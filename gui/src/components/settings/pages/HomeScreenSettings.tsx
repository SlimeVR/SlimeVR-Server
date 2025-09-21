import { CheckBox } from '@/components/commons/Checkbox';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { HomeIcon } from '@/components/commons/icon/HomeIcon';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { Config, useConfig } from '@/hooks/config';
import {
  trackingchecklistIdtoLabel,
  useTrackingChecklist,
} from '@/hooks/tracking-checklist';
import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { ReactNode, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { TrackingChecklistStepId } from 'solarxr-protocol';

type StepsForm = { steps: Record<TrackingChecklistStepId, boolean> };
export function TrackingChecklistSettings({
  variant,
}: {
  variant: 'settings' | 'modal';
}) {
  const { l10n } = useLocalization();
  const { ignoredSteps, steps, ignoreStep } = useTrackingChecklist();

  const { control, reset, handleSubmit } = useForm<StepsForm>({
    defaultValues: {
      steps: steps.reduce(
        (curr, { id }) => ({ [id]: !ignoredSteps.includes(id), ...curr }),
        {}
      ),
    },
    mode: 'onChange',
  });

  useEffect(() => {
    reset({
      steps: steps.reduce(
        (curr, { id }) => ({ [id]: !ignoredSteps.includes(id), ...curr }),
        {}
      ),
    });
  }, [ignoredSteps]);

  const onSubmit = (values: StepsForm) => {
    for (const [id, value] of Object.entries(values.steps)) {
      const stepId = +id;
      if (!stepId) continue;

      // doing it this way prevents calling ignore step for every step.
      // that prevent sending a packet for steps that didnt change
      if (!value && !ignoredSteps.includes(stepId)) {
        ignoreStep(stepId, true);
      }

      if (value && ignoredSteps.includes(stepId)) {
        ignoreStep(stepId, false);
      }
    }
  };

  return (
    <div className="flex flex-col">
      <div className="flex flex-col pt-4 pb-2">
        <Typography bold>Active Steps</Typography>
        <Typography color="secondary">
          List all the steps that will show in the tracking checklist. You can
          either disable or enable ignorable steps
        </Typography>
      </div>
      <form
        className="grid grid-cols-2 gap-2 mobile:flex flex-col"
        onChange={handleSubmit(onSubmit)}
      >
        {steps
          .filter((step) => step.enabled)
          .map((step) => (
            <div key={step.id}>
              <CheckBox
                control={control}
                name={`steps.${step.id}`}
                disabled={!step.ignorable || !step.enabled}
                label={l10n.getString(trackingchecklistIdtoLabel[step.id])}
                outlined
                color={variant === 'settings' ? 'primary' : 'secondary'}
              ></CheckBox>
            </div>
          ))}
      </form>
    </div>
  );
}

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
    <div
      className={classNames(
        'w-40 aspect-video bg-background-70 flex-col flex rounded-lg border-2 group cursor-pointer',
        {
          'border-accent-background-20': active,
          'border-background-50 hover:border-background-40': !active,
        }
      )}
      onClick={onClick}
    >
      <div className="px-2 pt-2 pb-1">
        <Typography id={name}></Typography>
      </div>
      <div
        className={classNames('h-[2px] w-full mb-2', {
          'group-hover:bg-background-40 bg-background-50': !active,
          'bg-accent-background-20': active,
        })}
      ></div>
      {children}
    </div>
  );
}

export function HomeLayoutSettings() {
  const { config, setConfig } = useConfig();

  const setLayout = (layout: Config['homeLayout']) =>
    setConfig({ homeLayout: layout });

  return (
    <div className="flex flex-col gap-2">
      <div className="flex flex-col pt-4 pb-2">
        <Typography bold>Trackers list layout</Typography>
        <Typography color="secondary">
          Select one of the possible layouts of the home screen
        </Typography>
      </div>
      <div className="flex gap-4">
        <LayoutSelector
          name="Grid"
          active={config?.homeLayout === 'default'}
          onClick={() => setLayout('default')}
        >
          <div className="grid grid-cols-2 gap-2 p-2">
            <div className="h-2 rounded-lg bg-background-40"></div>
            <div className="h-2 rounded-lg bg-background-40"></div>
            <div className="h-2 rounded-lg bg-background-40"></div>
            <div className="h-2 rounded-lg bg-background-40"></div>
          </div>
        </LayoutSelector>
        <LayoutSelector
          name="Table"
          active={config?.homeLayout === 'table'}
          onClick={() => setLayout('table')}
        >
          <div className="grid grid-cols-1 gap-2 p-2">
            <div className="h-2 rounded-lg bg-background-40"></div>
            <div className="h-2 rounded-lg bg-background-40"></div>
            <div className="h-2 rounded-lg bg-background-40"></div>
            <div className="h-2 rounded-lg bg-background-40"></div>
          </div>
        </LayoutSelector>
      </div>
    </div>
  );
}

export function HomeScreenSettings() {
  return (
    <SettingsPageLayout>
      <div className="flex flex-col gap-2">
        <SettingsPagePaneLayout icon={<HomeIcon></HomeIcon>}>
          <Typography variant="main-title" id="home">
            Home Screen Settings
          </Typography>
          <HomeLayoutSettings></HomeLayoutSettings>
        </SettingsPagePaneLayout>
        <SettingsPagePaneLayout icon={<CheckIcon size={18}></CheckIcon>}>
          <Typography variant="main-title" id="checklist">
            Tracking Checklist
          </Typography>
          <TrackingChecklistSettings variant="settings"></TrackingChecklistSettings>
        </SettingsPagePaneLayout>
      </div>
    </SettingsPageLayout>
  );
}
