import { useState } from 'react';
import { BaseModal } from './BaseModal';
import { Button, ButtonProps } from './Button';

export type ButtonConfirmModal = Omit<ButtonProps, 'onClick' | 'to'> & {
  onConfirm: () => void;
  onCancel: (reason: 'cancel' | 'backdrop') => void;

  confirmLabel: string;
  cancelLabel: string;
};

export function ButtonConfirmModal(props: ButtonConfirmModal) {
  const {
    onConfirm,
    onCancel,
    cancelLabel,
    confirmLabel,
    children,
    ...buttonProps
  } = props;

  const [isOpen, setOpen] = useState(false);

  return (
    <>
      <BaseModal
        isOpen={isOpen}
        onRequestClose={() => {
          setOpen(false);
          onCancel('backdrop');
        }}
        appendClasses="w-full max-w-2xl"
      >
        <div className="flex flex-col justify-between gap-4">
          {children}
          <div className="flex w-full justify-between gap-2 xs:flex-row flex-col">
            <Button
              id={cancelLabel}
              variant="tertiary"
              onClick={() => {
                setOpen(false);
                onCancel('cancel');
              }}
            />
            <Button
              id={confirmLabel}
              variant="primary"
              onClick={() => {
                setOpen(false);
                onConfirm();
              }}
            />
          </div>
        </div>
      </BaseModal>
      <Button {...buttonProps} onClick={() => setOpen(true)} />
    </>
  );
}
