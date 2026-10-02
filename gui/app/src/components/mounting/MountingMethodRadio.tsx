import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useLocalization } from '@fluent/react';
import { MountingMethod } from 'solarxr-protocol';
import { Radio } from '@/components/commons/Radio';
import { useResetsSettings } from '@/hooks/resets-settings';

type MountingMethodForm = { mountingMethod: string };

export function MountingMethodRadio() {
  const { l10n } = useLocalization();
  const { resetsSettings, setResetsSettings } = useResetsSettings();
  const { control, watch, reset } = useForm<MountingMethodForm>({
    defaultValues: { mountingMethod: String(MountingMethod.UNKNOWN) },
  });

  useEffect(() => {
    if (resetsSettings) {
      reset({ mountingMethod: String(resetsSettings.mountingMethod) });
    }
  }, [resetsSettings?.mountingMethod]);

  useEffect(() => {
    const subscription = watch((values, { type }) => {
      if (type === 'change') {
        setResetsSettings({ mountingMethod: Number(values.mountingMethod) });
      }
    });
    return () => subscription.unsubscribe();
  }, [resetsSettings]);

  return (
    <div className="grid md:grid-cols-3 flex-col gap-3">
      <Radio
        control={control}
        name="mountingMethod"
        label={l10n.getString('mounting_method-step')}
        description={l10n.getString('mounting_method-step-description')}
        value={String(MountingMethod.STEP)}
      />
      <Radio
        control={control}
        name="mountingMethod"
        label={l10n.getString('mounting_method-pose')}
        description={l10n.getString('mounting_method-pose-description')}
        value={String(MountingMethod.POSE)}
      />
      <Radio
        control={control}
        name="mountingMethod"
        label={l10n.getString('mounting_method-manual')}
        description={l10n.getString('mounting_method-manual-description')}
        value={String(MountingMethod.MANUAL)}
      />
    </div>
  );
}
