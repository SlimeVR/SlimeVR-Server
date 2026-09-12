import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import { useConfig } from '@/hooks/config';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { CompareIcon } from '@/components/commons/icon/CompareIcon';
import { TogglePill, TogglePillOption } from '@/components/commons/TogglePill';
import {
  BodyAssignmentViewProps,
  BodyPartAssignment,
} from './BodyPartAssignment';

export function ShowAllPartsToggle({ compact }: { compact?: boolean }) {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();

  return (
    <div className={classNames('w-fit shrink-0', compact && '[&_label]:h-fit')}>
      <CheckboxInternal
        variant="toggle"
        name="showAllBodyParts"
        checked={config?.assignShowAllBodyParts ?? false}
        onChange={() =>
          setConfig({
            assignShowAllBodyParts: !config?.assignShowAllBodyParts,
          })
        }
        label={l10n.getString(
          compact
            ? 'onboarding-assign_trackers-show_all-short'
            : 'onboarding-assign_trackers-show_all'
        )}
      />
    </div>
  );
}

export function MirrorLegend({ compact }: { compact?: boolean }) {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const mirror = config?.mirrorView ?? false;

  const pill = (side: 'left' | 'right') => {
    const oposite = side == 'left' ? 'right' : 'left';
    if (mirror) side = oposite;
    return (
      <TogglePillOption
        compact={compact}
        dotClass={`outline-assign-${side}`}
        labelId={`onboarding-assign_trackers-side-${side}`}
      />
    );
  };

  return (
    <TogglePill
      compact={compact}
      pressed={mirror}
      label={l10n.getString('onboarding-assign_trackers-mirror')}
      onClick={() => setConfig({ mirrorView: !mirror })}
    >
      {pill('right')}
      <CompareIcon width={22} />
      {pill('left')}
    </TogglePill>
  );
}

export function BodyAssignment({
  ...props
}: Omit<BodyAssignmentViewProps, 'view'>) {
  return (
    <BodyPartAssignment
      {...props}
      view={{
        kind: 'body',
      }}
    />
  );
}
