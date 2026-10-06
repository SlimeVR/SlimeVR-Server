import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { MountingMethod, ResetType } from 'solarxr-protocol';
import { useResetsSettings } from './resets-settings';
import { UseResetOptions } from './reset';

export const STEP_MOUNTING_PATH = '/onboarding/mounting/step';
export const MANUAL_MOUNTING_PATH = '/onboarding/mounting/manual';

export function useMountingMethod(
  options: UseResetOptions,
  triggerReset: (method?: MountingMethod) => void
) {
  const navigate = useNavigate();
  const { resetsSettings, setResetsSettings } = useResetsSettings();
  const [pickerOpen, setPickerOpen] = useState(false);

  const openManualMounting = () =>
    navigate(MANUAL_MOUNTING_PATH, { state: { alonePage: true } });

  const onClick = () => {
    if (options.type !== ResetType.MOUNTING) return triggerReset();
    switch (resetsSettings?.mountingMethod) {
      case MountingMethod.UNKNOWN:
        return setPickerOpen(true);
      case MountingMethod.MANUAL:
        return openManualMounting();
      default:
        return triggerReset();
    }
  };

  const onPick = (method: MountingMethod) => {
    setPickerOpen(false);
    setResetsSettings({ mountingMethod: method });
    if (method === MountingMethod.MANUAL) openManualMounting();
    else triggerReset(method);
  };

  return {
    onClick,
    onPick,
    pickerOpen,
    closePicker: () => setPickerOpen(false),
  };
}
