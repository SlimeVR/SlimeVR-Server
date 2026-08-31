import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { BaseModal } from '@/components/commons/BaseModal';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { useConfig } from '@/hooks/config';
import { useLocalization } from '@fluent/react';

export function AssignmentSettingsModal({
  isOpen,
  onClose,
}: {
  isOpen: boolean;
  onClose: () => void;
}) {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();

  const { control, watch } = useForm<{
    showAllBodyParts: boolean;
    mirrorView: boolean;
  }>({
    defaultValues: {
      showAllBodyParts: config?.assignShowAllBodyParts ?? false,
      mirrorView: config?.mirrorView ?? false,
    },
  });
  const { showAllBodyParts, mirrorView } = watch();

  useEffect(() => {
    setConfig({
      assignShowAllBodyParts: showAllBodyParts,
      mirrorView: mirrorView,
    });
  }, [showAllBodyParts, mirrorView]);

  return (
    <BaseModal
      isOpen={isOpen}
      appendClasses="max-w-md w-full"
      closeable
      onRequestClose={onClose}
    >
      <div className="flex flex-col gap-2">
        <Typography
          variant="main-title"
          id="onboarding-assign_trackers-settings"
        />
        <CheckBox
          control={control}
          label={l10n.getString('onboarding-assign_trackers-mirror_view')}
          name="mirrorView"
          variant="toggle"
        />
        <CheckBox
          control={control}
          label={l10n.getString('onboarding-assign_trackers-show_all')}
          name="showAllBodyParts"
          variant="toggle"
        />
        <div className="flex justify-end">
          <Button
            variant="tertiary"
            onClick={onClose}
            id="onboarding-assign_trackers-settings-close"
          />
        </div>
      </div>
    </BaseModal>
  );
}
