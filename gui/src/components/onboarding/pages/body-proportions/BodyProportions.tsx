import { Localized, useLocalization } from '@fluent/react';
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

function IncrementButton({
  children,
  onClick,
}: {
  children: ReactNode;
  onClick?: MouseEventHandler<HTMLDivElement>;
}) {
  return (
    <div
      onClick={onClick}
      className={classNames(
        'p-3 rounded-lg xs:w-10 xs:h-10 flex flex-col justify-center items-center cursor-pointer',
        'bg-background-40 hover:bg-opacity-50 active:bg-accent-background-30'
      )}
    >
      <Typography variant="vr-accessible" bold>
        {children}
      </Typography>
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
          'flex justify-center gap-6 mobile:gap-2 p-2 mobile:px-2 px-4',
          part.type === 'group-part'
            ? 'bg-background-50 group/child-buttons'
            : 'bg-background-70 group/buttons'
        )}
      >
        <div
          className={classNames(
            'h-16 rounded-lg flex w-full items-center mobile:items-start transition-colors mobile:flex-col mobile:gap-2 mobile:py-2 mobile:h-auto',
            'duration-300'
          )}
        >
          <div className="flex flex-grow" onClick={toggleOpen}>
            <Typography variant="section-title" bold>
              {l10n.getString(part.label)}
            </Typography>
            <Tooltip
              content={
                <Localized id={`${part.label}-desc`}>
                  <Typography
                    variant="standard"
                    whitespace="whitespace-pre-wrap"
                  ></Typography>
                </Localized>
              }
              preferedDirection="bottom"
              mode="corner"
            >
              <div className="info-icon hover:opacity-100 opacity-65 ml-1 scale-[0.65] border-2 border-solid text-xs w-5 h-5 flex justify-center items-center rounded-full">
                i
              </div>
            </Tooltip>
          </div>

          <div className="flex gap-4 items-center mobile:justify-center mobile:w-full">
            <div
              className={classNames(
                'flex items-center gap-2 my-2 opacity-75',
                part.type === 'group-part'
                  ? 'group-hover/child-buttons:opacity-100'
                  : 'group-hover/buttons:opacity-100'
              )}
            >
              {!precise && (
                <IncrementButton onClick={() => boneIncrement(-5)}>
                  {configFormat.format(-5)}
                </IncrementButton>
              )}
              <IncrementButton onClick={() => boneIncrement(-1)}>
                {configFormat.format(-1)}
              </IncrementButton>
              {precise && (
                <IncrementButton onClick={() => boneIncrement(-0.5)}>
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
              className={classNames(
                'flex items-center gap-2 my-2 opacity-75',
                part.type === 'group-part'
                  ? 'group-hover/child-buttons:opacity-100'
                  : 'group-hover/buttons:opacity-100'
              )}
            >
              {precise && (
                <IncrementButton onClick={() => boneIncrement(+0.5)}>
                  {configFormat.format(+0.5)}
                </IncrementButton>
              )}
              <IncrementButton onClick={() => boneIncrement(+1)}>
                {configFormat.format(+1)}
              </IncrementButton>
              {!precise && (
                <IncrementButton onClick={() => boneIncrement(+5)}>
                  {configFormat.format(+5)}
                </IncrementButton>
              )}
            </div>
          </div>
        </div>
        {type === 'ratio' && part.type !== 'group-part' && (
          <div
            className={classNames(
              'flex items-center fill-background-20 hover:scale-110',
              part.type === 'bone' && 'opacity-50'
            )}
            onClick={toggleOpen}
          >
            {open ? (
              <ArrowUpIcon size={50}></ArrowUpIcon>
            ) : (
              <ArrowDownIcon size={50}></ArrowDownIcon>
            )}
          </div>
        )}
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
              ></ProportionItem>
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
            ></ProportionItem>
          ))}
        </div>
      </div>
    )) || <></>
  );
}
