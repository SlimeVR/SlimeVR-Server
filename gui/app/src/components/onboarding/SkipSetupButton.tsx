import { useEffect } from 'react';
import { useLocalization } from '@fluent/react';
import { ArrowRightIcon } from '@/components/commons/icon/ArrowIcons';

export function SkipSetupButton({
  modalVisible,
  onClick,
  visible,
}: {
  onClick: () => void;
  modalVisible: boolean;
  visible: boolean;
}) {
  const { l10n } = useLocalization();

  useEffect(() => {
    if (modalVisible || !visible) return;

    function onEscape(ev: KeyboardEvent) {
      if (ev.key === 'Escape') onClick();
    }

    document.addEventListener('keydown', onEscape, { passive: true });

    return () => document.removeEventListener('keydown', onEscape);
  }, [modalVisible, visible]);

  if (!visible) return null;

  return (
    <button
      type="button"
      title={l10n.getString('onboarding-skip')}
      className="flex items-center gap-1 shrink-0 whitespace-nowrap rounded-md px-2 smol:px-3 h-7 bg-background-60 hover:bg-background-50 text-standard text-background-10 fill-background-10"
      onClick={onClick}
    >
      {l10n.getString('onboarding-skip')}
      <ArrowRightIcon size={14} />
    </button>
  );
}
