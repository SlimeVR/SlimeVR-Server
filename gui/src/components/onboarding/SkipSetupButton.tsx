import classNames from 'classnames';
import { EscapeIcon } from '../commons/icon/EscapeIcon';

export function SkipSetupButton({ onClick }: { onClick?: () => void }) {
  return (
    <button
      type="button"
      className={classNames(
        'text-background-40 hover:text-background-30',
        'stroke-background-40 hover:stroke-background-30',
        'absolute top-0 right-0'
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
