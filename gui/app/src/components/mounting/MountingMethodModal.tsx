import { MountingMethod } from 'solarxr-protocol';
import { BaseModal } from '@/components/commons/BaseModal';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { SkiIcon } from '@/components/commons/icon/SkiIcon';
import { StepIcon } from '@/components/commons/icon/StepIcon';
import { TuneIcon } from '@/components/commons/icon/TuneIcon';
import { useResetsSettings } from '@/hooks/resets-settings';
import classNames from 'classnames';

function MethodCard({
  method,
  icon,
  name,
  onSelect,
  recommended = false,
  note,
}: {
  method: MountingMethod;
  icon: React.ReactNode;
  name: string;
  onSelect: (method: MountingMethod) => void;
  recommended?: boolean;
  note?: string;
}) {
  const { resetsSettings } = useResetsSettings();

  const selected = resetsSettings?.mountingMethod == method;

  return (
    <div
      className={classNames(
        'rounded-lg p-4 flex flex-col gap-3 relative bg-background-70 border-2',
        {
          'border-accent-background-20': selected,
          'border-background-50': !selected,
        }
      )}
    >
      {recommended && (
        <div className="bg-accent-background-30 absolute -left-4 -top-5 p-1.5 rounded-lg">
          <Typography
            variant="vr-accessible"
            italic
            id="mounting_method-recommended"
          />
        </div>
      )}
      <div className="fill-background-10">{icon}</div>
      <Typography variant="main-title" bold id={`mounting_method-${name}`} />
      <Typography
        whitespace="whitespace-pre-line"
        id={`mounting_method-${name}-description`}
      />
      {note && <Typography color="text-background-20" id={note} />}
      <Button
        variant="secondary"
        className="self-start mt-auto"
        onClick={() => onSelect(method)}
        id="mounting_method-select"
      />
    </div>
  );
}

export function MountingMethodModal({
  isOpen,
  onClose,
  triggerReset,
}: {
  isOpen: boolean;
  onClose: () => void;
  triggerReset?: (method?: MountingMethod) => void;
}) {
  const { setResetsSettings } = useResetsSettings();

  const onSelect = (method: MountingMethod) => {
    onClose();
    setResetsSettings({ mountingMethod: method });
    if (method !== MountingMethod.UNKNOWN) triggerReset?.(method);
  };

  return (
    <BaseModal isOpen={isOpen} onRequestClose={onClose}>
      <div className="flex flex-col gap-4 p-2 max-w-4xl">
        <Typography
          variant="main-title"
          bold
          id="mounting_method_modal-title"
        />
        <Typography id="mounting_method_modal-description" />
        <div className="grid xs:grid-cols-3 gap-4">
          <MethodCard
            method={MountingMethod.STEP}
            icon={<StepIcon size={32} />}
            name="step"
            recommended
            note="mounting_method-step-needs_positional_head"
            onSelect={onSelect}
          />
          <MethodCard
            method={MountingMethod.POSE}
            icon={<SkiIcon size={32} />}
            name="pose"
            onSelect={onSelect}
          />
          <MethodCard
            method={MountingMethod.MANUAL}
            icon={<TuneIcon size={32} />}
            name="manual"
            onSelect={onSelect}
          />
        </div>
      </div>
    </BaseModal>
  );
}
