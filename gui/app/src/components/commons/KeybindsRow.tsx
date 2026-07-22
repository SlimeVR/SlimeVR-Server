import { Typography } from './Typography';
import { Control, useWatch } from 'react-hook-form';
import { KeybindForm } from '@/components/settings/pages/KeybindSettings';
import { NumberSelector } from './NumberSelector';
import { useLocaleConfig } from '@/i18n/config';
import { Kbd } from './Kbd';
import { RecordIcon } from './icon/RecordIcon';
import { ResetIcon } from './icon/ResetIcon';
import { Tooltip } from './Tooltip';
import classNames from 'classnames';

function KeyBindKeyList({ keybind }: { keybind: string[] }) {
  if (keybind.length === 0) {
    return (
      <div className="flex items-center justify-center gap-2 px-4 py-2 rounded-xl fill-background-10 border border-dashed border-background-50 bg-background-90 group-hover:border-accent-background-40 transition-all">
        <RecordIcon width={16} />
        <Typography id="settings-keybinds-click-to-record" />
      </div>
    );
  }

  return (
    <div className="group flex items-center justify-start md:justify-center gap-1.5 flex-wrap">
      {keybind.map((key, i) => (
        <div key={i} className="flex items-center gap-1.5">
          <Kbd className="px-2 md:px-4 py-2 min-w-[34px] md:min-w-[40px] group-hover:border-accent-background-40">
            {key}
          </Kbd>
          {i < keybind.length - 1 && (
            <Typography variant="standard" bold textAlign="text-center">
              +
            </Typography>
          )}
        </div>
      ))}
    </div>
  );
}

function ResetButton({
  isModified,
  onClick,
}: {
  isModified?: boolean;
  onClick: () => void;
}) {
  return (
    <Tooltip
      content={<Typography id="settings-keybinds-reset-single" />}
      preferedDirection="top"
    >
      <button
        type="button"
        disabled={!isModified}
        className={classNames(
          'w-7 h-7 rounded-xl flex items-center justify-center transition-all',
          {
            'text-accent hover:bg-background-60 cursor-pointer opacity-100':
              isModified,
            'text-background-40 opacity-30 cursor-not-allowed': !isModified,
          }
        )}
        onClick={onClick}
      >
        <ResetIcon size={16} />
      </button>
    </Tooltip>
  );
}

export function KeybindsRow({
  id,
  control,
  index,
  openKeybindRecorderModal,
  onResetSingle,
  isModified,
}: {
  id?: string;
  control: Control<KeybindForm>;
  index: number;
  openKeybindRecorderModal: (index: number) => void;
  onResetSingle: (index: number) => void;
  isModified?: boolean;
}) {
  const binding =
    useWatch({
      control,
      name: `keybinds.${index}.binding`,
    }) ?? [];

  const { currentLocales } = useLocaleConfig();
  const secondsFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  });

  return (
    <tr className="bg-background-80 flex flex-col md:table-row p-4 md:p-0 gap-3 md:gap-0 border-b border-background-60 md:border-b-0 relative">
      <td className="py-2 md:py-4 pl-0 md:pl-6 pr-0 md:pr-4 block md:table-cell align-middle">
        <div className="flex items-center justify-between md:justify-start gap-2.5">
          <div className="flex items-center gap-2.5">
            <div className="w-7 h-7 hidden md:flex items-center justify-center flex-shrink-0">
              <ResetButton
                isModified={isModified}
                onClick={() => onResetSingle(index)}
              />
            </div>
            <div className="flex items-center m-0 p-0 leading-none">
              <Typography
                id={`settings-keybinds_${id}`}
                variant="standard"
                bold
                whitespace="whitespace-nowrap"
              />
            </div>
          </div>

          <div className="md:hidden">
            <ResetButton
              isModified={isModified}
              onClick={() => onResetSingle(index)}
            />
          </div>
        </div>
      </td>

      <td className="py-2 md:py-4 px-0 md:px-4 block md:table-cell align-middle">
        <div className="flex justify-start md:justify-center items-center">
          <Tooltip
            content={<Typography id="settings-keybinds-change-shortcut" />}
            preferedDirection="top"
          >
            <div
              className="cursor-pointer rounded-xl transition-all hover:scale-105 active:scale-95"
              onClick={() => openKeybindRecorderModal(index)}
            >
              <KeyBindKeyList keybind={binding} />
            </div>
          </Tooltip>
        </div>
      </td>

      <td className="py-2 md:py-4 px-0 md:px-6 block md:table-cell align-middle">
        <div className="flex flex-col items-start md:items-end gap-1 pt-2 md:pt-0 justify-center">
          <span className="md:hidden">
            <Typography id="keybind_config-keybind_delay" />
          </span>
          <NumberSelector
            control={control}
            name={`keybinds.${index}.delay`}
            valueLabelFormat={(value) => secondsFormat.format(value)}
            min={0}
            max={10}
            step={0.2}
          />
        </div>
      </td>
    </tr>
  );
}
