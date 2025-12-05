import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { MouseEventHandler, ReactNode, useMemo, useState } from 'react';
import {
  Label,
  UpdateBoneParams,
  useManualProportions,
} from '@/hooks/manual-proportions';
import { useLocaleConfig } from '@/i18n/config';
import { Typography } from '@/components/commons/Typography';
import {
  ArrowDownIcon,
  ArrowUpIcon,
} from '@/components/commons/icon/ArrowIcons';
import { Tooltip } from '@/components/commons/Tooltip';
import { useBreakpoint } from '@/hooks/breakpoint';

function IncrementButton({
  children,
  onClick,
  bgDark,
}: {
  children: ReactNode;
  onClick?: MouseEventHandler<HTMLDivElement>;
  bgDark: boolean;
}) {
  return (
    <div
      onClick={onClick}
      className={classNames(
        'p-3 rounded-lg xs:w-10 xs:h-10 flex flex-col justify-center items-center cursor-pointer',
        'hover:bg-opacity-50 active:bg-accent-background-30',
        { 'bg-background-60': bgDark, 'bg-background-40': !bgDark }
      )}
    >
      <Typography variant="vr-accessible" bold>
        {children}
      </Typography>
    </div>
  );
}

function OpenGroupButton({
  part,
  open,
  toggleOpen,
}: {
  part: Label;
  open: boolean;
  toggleOpen: () => void;
}) {
  const { isXs } = useBreakpoint('xs');

  return (
    <div
      className={classNames(
        'flex items-center fill-background-20',
        part.type === 'bone' && 'opacity-50 cursor-not-allowed',
        part.type !== 'bone' && 'hover:scale-110'
      )}
      onClick={toggleOpen}
    >
      {open ? (
        <ArrowUpIcon size={isXs ? 50 : 30} />
      ) : (
        <ArrowDownIcon size={isXs ? 50 : 30} />
      )}
    </div>
  );
}

function ProportionItem({
  type,
  part,
  precise,
  onBoneChange,
}: {
  type: 'linear' | 'ratio';
  part: Label;
  precise: boolean;
  onBoneChange: (params: UpdateBoneParams) => void;
}) {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();

  const { cmFormat, percentageFormat, configFormat } = useMemo(() => {
    const cmFormat = Intl.NumberFormat(currentLocales, {
      style: 'unit',
      unit: 'centimeter',
      maximumFractionDigits: 1,
    });
    const percentageFormat = new Intl.NumberFormat(currentLocales, {
      style: 'percent',
      maximumFractionDigits: 1,
    });
    const configFormat = Intl.NumberFormat(currentLocales, {
      signDisplay: 'always',
      maximumFractionDigits: 1,
    });
    return {
      cmFormat,
      percentageFormat,
      configFormat,
    };
  }, [currentLocales]);

  const [open, setOpen] = useState(false);

  const toggleOpen = () => {
    if (part.type === 'bone') return;
    setOpen((open) => !open);
  };

  const boneIncrement = (addition: number) => {
    const newValue =
      part.unit === 'cm'
        ? (Math.round(part.value * 200) + addition * 2) / 200
        : // In the case of unit === percent we send only the added percent and not the value with added percent to it
          // this is so the percent added is relative to the whole group and not the bone as 1% added to the bone is not 1% of the group
          addition / 100;

    if (part.type === 'bone') {
      onBoneChange({
        type: 'bone',
        newValue,
        bone: part.bone,
      });
    }
    if (part.type === 'group-part') {
      onBoneChange({
        type: 'group-part',
        newValue,
        bone: part.bone,
        group: part.group,
      });
    }
    if (part.type === 'group') {
      onBoneChange({
        type: 'group',
        newValue,
        group: part.label,
      });
    }
  };

  return (
    <div className={classNames('flex flex-col rounded-md overflow-clip')}>
      <div
        key={part.label}
        itemID={part.label}
        className={classNames(
          'flex justify-center gap-6 mobile:gap-2 p-2 mobile:pt-0 mobile:px-2 px-4',
          part.type === 'group-part' ? 'bg-background-50' : 'bg-background-70'
        )}
      >
        <div
          className={classNames(
            'xs:h-16 rounded-lg flex w-full items-center mobile:items-start transition-colors mobile:flex-col mobile:gap-2 mobile:py-2',
            'duration-300'
          )}
        >
          <div
            className="flex flex-grow w-full xs:w-auto items-center"
            onClick={toggleOpen}
          >
            <Typography variant="section-title" bold>
              {l10n.getString(part.label)}
            </Typography>
            <Tooltip
              content={
                <Typography
                  variant="standard"
                  whitespace="whitespace-pre-wrap"
                  id={`${part.label}-desc`}
                />
              }
              preferedDirection="bottom"
            >
              <div className="hover:opacity-100 opacity-65 ml-1 scale-[0.65] border-2 border-solid text-xs w-5 h-5 flex justify-center items-center rounded-full cursor-help">
                i
              </div>
            </Tooltip>
            <div className="xs:hidden flex flex-grow justify-end">
              {type === 'ratio' && part.type !== 'group-part' && (
                <OpenGroupButton
                  open={open}
                  toggleOpen={() => {}}
                  part={part}
                />
              )}
            </div>
          </div>

          <div className="flex gap-4 items-center mobile:justify-center mobile:w-full">
            <div
              className={classNames('flex items-center gap-2 my-2 opacity-100')}
            >
              {!precise && (
                <IncrementButton
                  bgDark={part.type !== 'group-part'}
                  onClick={() => boneIncrement(-5)}
                >
                  {configFormat.format(-5)}
                </IncrementButton>
              )}
              <IncrementButton
                bgDark={part.type !== 'group-part'}
                onClick={() => boneIncrement(-1)}
              >
                {configFormat.format(-1)}
              </IncrementButton>
              {precise && (
                <IncrementButton
                  bgDark={part.type !== 'group-part'}
                  onClick={() => boneIncrement(-0.5)}
                >
                  {configFormat.format(-0.5)}
                </IncrementButton>
              )}
            </div>
            <div className="text-xl font-bold min-w-24 text-center">
              {part.unit === 'percent'
                ? /* Make number rounding so it's based on .5 decimals */
                  percentageFormat.format(Math.round(part.ratio * 200) / 200)
                : cmFormat.format(part.value * 100)}
              {part.unit === 'percent' && (
                <p className="text-standard">{`(${cmFormat.format(
                  part.value * 100
                )})`}</p>
              )}
            </div>
            <div
              className={classNames('flex items-center gap-2 my-2 opacity-100')}
            >
              {precise && (
                <IncrementButton
                  bgDark={part.type !== 'group-part'}
                  onClick={() => boneIncrement(+0.5)}
                >
                  {configFormat.format(+0.5)}
                </IncrementButton>
              )}
              <IncrementButton
                bgDark={part.type !== 'group-part'}
                onClick={() => boneIncrement(+1)}
              >
                {configFormat.format(+1)}
              </IncrementButton>
              {!precise && (
                <IncrementButton
                  bgDark={part.type !== 'group-part'}
                  onClick={() => boneIncrement(+5)}
                >
                  {configFormat.format(+5)}
                </IncrementButton>
              )}
            </div>
          </div>
        </div>
        <div className="hidden xs:flex">
          {type === 'ratio' && part.type !== 'group-part' && (
            <OpenGroupButton open={open} toggleOpen={toggleOpen} part={part} />
          )}
        </div>
      </div>
      {part.type === 'group' && (
        <div
          className="bg-background-50 grid"
          style={{
            gridTemplateRows: open ? '1fr' : '0fr',
            transition: 'grid-template-rows 0.2s ease-in-out',
          }}
        >
          <div className="overflow-hidden">
            {part.bones.map((part) => (
              <ProportionItem
                type={type}
                key={part.label}
                precise={precise}
                part={part}
                onBoneChange={onBoneChange}
              />
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export function BodyProportions({
  precise,
  type,
  variant: _variant = 'onboarding',
}: {
  precise: boolean;
  type: 'linear' | 'ratio';
  variant: 'onboarding' | 'alone';
}) {
  const { bodyPartsGrouped, changeBoneValue } = useManualProportions({
    type,
  });

  return (
    (bodyPartsGrouped.length > 0 && (
      <div className="flex w-full gap-3">
        <div className="flex flex-grow flex-col gap-2 p-2">
          {bodyPartsGrouped.map((part) => (
            <ProportionItem
              type={type}
              key={part.label}
              part={part}
              precise={precise}
              onBoneChange={changeBoneValue}
            />
          ))}
        </div>
      </div>
    )) || <></>
  );
}
