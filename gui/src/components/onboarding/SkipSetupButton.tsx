import classNames from 'classnames';
import { useEffect } from 'react';
import { EscapeIcon } from '@/components/commons/icon/EscapeIcon';

export function SkipSetupButton({
  modalVisible,
  onClick,
  visible,
}: {
  onClick: () => void;
  modalVisible: boolean;
  visible: boolean;
}) {
  if (!visible) return <></>;
  useEffect(() => {
    if (modalVisible) return;

    function onEscape(ev: KeyboardEvent) {
      if (ev.key === 'Escape') onClick();
    }

    document.addEventListener('keydown', onEscape, { passive: true });

    return () => document.removeEventListener('keydown', onEscape);
  }, [modalVisible]);

  return (
    <button
      type="button"
      className={classNames(
        'text-background-40 hover:text-background-30',
        'stroke-background-40 hover:stroke-background-30',
        'absolute xs:-top-10 xs:right-4 mobile:top-0 mobile:right-0'
      )}
      onClick={onClick}
    >
      <div className="flex flex-col justify-center items-center">
        <EscapeIcon size={42}></EscapeIcon>
        <p className="text-standard">ESC</p>
      </div>
    </button>
  );
}
