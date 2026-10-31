import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useLocalization } from '@fluent/react';
import { MountingMethod } from 'solarxr-protocol';
import { Radio } from '@/components/commons/Radio';
import { useResetsSettings } from '@/hooks/resets-settings';
import classNames from 'classnames';

type MountingMethodForm = { mountingMethod: MountingMethod };

export function MountingMethodRadio({ col = false }: { col?: boolean }) {
  const { l10n } = useLocalization();
  const { resetsSettings, setResetsSettings } = useResetsSettings();
  const { control, watch, reset } = useForm<MountingMethodForm>({
    defaultValues: { mountingMethod: MountingMethod.UNKNOWN },
  });

  useEffect(() => {
    if (resetsSettings) {
      reset({ mountingMethod: resetsSettings.mountingMethod });
    }
  }, [resetsSettings?.mountingMethod]);

  useEffect(() => {
    const subscription = watch((values, { type }) => {
      if (type === 'change') {
        setResetsSettings({ mountingMethod: values.mountingMethod });
      }
    });
    return () => subscription.unsubscribe();
  }, []);

  return (
    <div
      className={classNames({
        'grid md:grid-cols-3 gap-3': !col,
        'flex flex-col gap-2': col,
      })}
    >
      <Radio
        control={control}
        name="mountingMethod"
        label={l10n.getString('mounting_method-step')}
        description={l10n.getString('mounting_method-step-description')}
        value={MountingMethod.STEP}
      />
      <Radio
        control={control}
        name="mountingMethod"
        label={l10n.getString('mounting_method-pose')}
        description={l10n.getString('mounting_method-pose-description')}
        value={MountingMethod.POSE}
      />
      <Radio
        control={control}
        name="mountingMethod"
        label={l10n.getString('mounting_method-manual')}
        description={l10n.getString('mounting_method-manual-description')}
        value={MountingMethod.MANUAL}
      />
    </div>
  );
}
