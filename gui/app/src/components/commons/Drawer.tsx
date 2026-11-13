import { ReactNode, useEffect, useLayoutEffect, useRef, useState } from 'react';
import { CloseIcon } from './icon/CloseIcon';
import { IconButton } from './IconButton';

const SLIDE_MS = 200;

export function Drawer({
  isOpen,
  onClose,
  title,
  children,
}: {
  isOpen: boolean;
  onClose: () => void;
  title: ReactNode;
  children: ReactNode;
}) {
  const ref = useRef<HTMLDivElement | null>(null);
  const [shown, setShown] = useState(false);

  const setRef = (node: HTMLDivElement | null) => {
    node?.setAttribute('popover', 'manual');
    ref.current = node;
  };

  useLayoutEffect(() => {
    const el = ref.current;
    if (!el) return;
    if (!isOpen) {
      setShown(false);
      const timeout = setTimeout(() => {
        if (el.matches(':popover-open')) el.hidePopover();
      }, SLIDE_MS);
      return () => clearTimeout(timeout);
    }
    if (!el.matches(':popover-open')) el.showPopover();
    el.getBoundingClientRect();
    setShown(true);
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;
    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [isOpen, onClose]);

  return (
    <div
      ref={setRef}
      className="fixed inset-0 top-[44px] m-0 p-0 border-0 bg-transparent w-full h-auto max-w-none max-h-none overflow-hidden"
      style={{ pointerEvents: shown ? undefined : 'none' }}
    >
      <div
        className="absolute inset-0 rounded-t-lg bg-background-90 transition-opacity duration-200"
        style={{ opacity: shown ? 0.5 : 0 }}
        onClick={onClose}
      />
      <div
        className="absolute left-0 w-full text-background-10 max-h-full transition-[bottom] duration-200 overflow-clip"
        style={{ bottom: shown ? 0 : '-100%' }}
      >
        <div className="bg-background-60 rounded-t-lg border-background-40 border-t-2 pb-[env(safe-area-inset-bottom)]">
          <div className="h-12 rounded-t-lg relative flex justify-center items-center">
            {title}
            <IconButton
              labelId="titlebar-close"
              tooltip={false}
              className="absolute right-4 top-3 h-6 w-6 bg-background-70 rounded-full flex justify-center items-center"
              onClick={onClose}
            >
              <CloseIcon size={20} className="stroke-white" />
            </IconButton>
          </div>
          <div
            className="overflow-y-auto"
            style={{ maxHeight: 'calc(100vh - 49px - 44px)' }}
          >
            {children}
          </div>
        </div>
      </div>
    </div>
  );
}
