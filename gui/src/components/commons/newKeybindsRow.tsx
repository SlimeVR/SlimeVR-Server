import { Typography } from './Typography';
import './KeybindRow.scss';
import { ReactNode, useEffect, useState } from 'react';
import { Control, UseFormGetValues, useWatch } from 'react-hook-form';
import { NumberSelector } from './NumberSelector';
import { useLocaleConfig } from '@/i18n/config';

const createKeybindDisplay = (keybind: string[]): ReactNode | null => {
  console.log(keybind.length);
  if (keybind.length <= 1) {
    return (
      <div className="flex min-h-[50px] text-lg items-center justifiy-center">
        Click to edit keybind
      </div>
    );
  }
  return keybind.map((key, i) => {
    return (
      <div key={i} className="flex flex-row">
        <div className="flex p-2 rounded-xl min-w-[50px] min-h-[50px] text-lg justify-center items-center bg-background-70">
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
  control,
  index,
  getValue,
  openKeybindRecorderModal,
}: {
  id?: string;
  control: Control<any>;
  index: number;
  getValue: UseFormGetValues<any>;
  openKeybindRecorderModal: (index: number) => void;
}) {
  const [keybindDisplay, setKeybindDisplay] = useState<ReactNode>(null);
  const [binding, setBinding] = useState<string[]>();
  const { currentLocales } = useLocaleConfig();
  const secondsFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  });

  const handleOpenModal = () => {
    openKeybindRecorderModal(index);
  };

  useEffect(() => {
    setBinding(getValue(`keybinds.${index}.binding`));
  });

  useEffect(() => {
    if (binding != null) setKeybindDisplay(createKeybindDisplay(binding));
  }, [binding]);

  return (
    <div className="keybind-row bg-background-60 rounded-xl hover:ring-2 hover:ring-accent">
      <label className="text-sm font-medium text-background-10 p-2">
        <Typography id={`settings-keybinds_${id}`} />
      </label>
      <div
        className="flex gap-2 min-h-[42px] items-center px-3 py-2 rounded-lg bg-background-80 hover:ring-2 hover:ring-purple-500"
        onClick={handleOpenModal}
      >
        <div className="flex flex-grow gap-2 justify-center">
          {keybindDisplay}
        </div>
      </div>
      <NumberSelector
        control={control}
        name={`keybinds.${index}.delay`}
        valueLabelFormat={(value) => secondsFormat.format(value)}
        min={0}
        max={10}
        step={0.2}
      />
    </div>
  );
}
