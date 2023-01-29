import classNames from 'classnames';
import { ReactNode } from 'react';
import ReactModal from 'react-modal';

export function BaseModal({
  children,
  ...props
}: {
  isOpen: boolean;
  children: ReactNode;
} & ReactModal.Props) {
  return (
    <ReactModal
      {...props}
      shouldCloseOnOverlayClick
      shouldCloseOnEsc
      overlayClassName={
        props.overlayClassName ||
        classNames(
          'fixed top-0 right-0 left-0 bottom-0 flex flex-col justify-center items-center w-full h-full bg-background-90 bg-opacity-60 z-20'
        )
      }
      className={
        props.className ||
        classNames(
          props.className as string,
          'items-center focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent outline-none bg-background-60 p-6 rounded-lg text-white'
        )
      }
    >
      {children}
    </ReactModal>
  );
}
