import { Typography } from './Typography';
import './KeybindRow.scss';
import { useEffect, useState } from 'react';
import {
  Control,
  FieldPath,
  FieldValues,
  UseFormGetValues,
} from 'react-hook-form';
import { NumberSelector } from './NumberSelector';
import { useLocaleConfig } from '@/i18n/config';

function KeyBindKeyList({ keybind }: { keybind: string[] }) {
  if (keybind.length <= 1) {
    return (
      <div className="flex h-full text-section-title items-center justifiy-center">
        Click to edit keybind
      </div>
    );
  }
  return keybind.map((key, i) => {
    return (
      <div key={i} className="flex flex-row">
        <div className="flex flex-wrap p-2 rounded-lg min-w-[50px] text-standard-bold justify-center items-center bg-background-80 mobile:text-sm">
          {key ?? ''}
        </div>
        <div className="flex justify-center items-center text-section-title mobile:text-sm gap-2 pl-3">
          {i < keybind.length - 1 ? '+' : ''}
        </div>
      </div>
    );
  });
}

export function KeybindsRow<T extends FieldValues = FieldValues>({
  id,
  control,
  index,
  getValue,
  openKeybindRecorderModal,
}: {
  id?: string;
  control: Control<T>;
  index: number;
  getValue: UseFormGetValues<any>;
  openKeybindRecorderModal: (index: number) => void;
}) {
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

  return (
    <div className="keybind-row bg-background-60 rounded-xl h-full keybinds-small:flex keybinds-small:flex-col keybinds-small:justify-center keybinds-small:items-center p-2">
      <label className="text-sm font-medium text-background-10 keybinds-small:flex keybinds-small:py-2 keybinds-small:justify-center keybinds-small:align-middle">
        <Typography id={`settings-keybinds_${id}`} />
      </label>
      <div
        className="flex gap-2 h-full items-center rounded-lg bg-background-70 hover:bg-background-50 w-full"
        onClick={handleOpenModal}
      >
        <div className="flex flex-grow gap-2 justify-center p-2 h-[50px]">
          {binding != null && <KeyBindKeyList keybind={binding} />}
        </div>
      </div>
      <NumberSelector
        control={control}
        name={`keybinds.${index}.delay` as FieldPath<T>}
        valueLabelFormat={(value) => secondsFormat.format(value)}
        min={0}
        max={10}
        step={0.2}
      />
    </div>
  );
}
