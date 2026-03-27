import { Typography } from './Typography';
import './KeybindRow.scss';
import { ReactNode } from 'react';

const createKeybindDisplay = (keybind: string[]): ReactNode | null => {
  return keybind.map((key, i) => {
    return (
      <div className="flex flex-row">
        <div
          key={i}
          className="flex rounded-xl min-w-[50px] min-h-[50px] text-lg justify-center items-center bg-background-70"
        >
          {key ?? ''}
        </div>
        <div className="flex justify-center items-center text-lg gap-2 pl-3">
          {i < keybind.length - 1 ? '+' : ''}
        </div>
      </div>
    );
  });
};

export function NewKeybindsRow({
  id,
  keybind,
  delay,
}: {
  id?: string;
  keybind?: string[];
  delay?: number;
}) {
  return (
    <div className="keybind-row bg-background-60 rounded-xl hover:ring-2 hover:ring-accent">
      <label className="text-sm font-medium text-background-10 p-2">
        <Typography id={`settings-keybinds_${id}`} />
      </label>
      <div className="flex gap-2 min-h-[42px] items-center px-3 py-2 rounded-lg bg-background-80">
        <div className="flex flex-grow gap-2 justify-center">
          {keybind != null ? createKeybindDisplay(keybind) : ''}
        </div>
      </div>
      <div>{delay}</div>
    </div>
  );
}
