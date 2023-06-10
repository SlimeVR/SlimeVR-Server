import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { MouseEventHandler, ReactNode, useEffect } from 'react';
import {
  LabelType,
  ProportionChangeType,
  useManualProportions,
} from '../../../../hooks/manual-proportions';
import { useLocaleConfig } from '../../../../i18n/config';
import { Typography } from '../../../commons/Typography';
import { useBreakpoint } from '../../../../hooks/breakpoint';

function IncrementButton({
  children,
  onClick,
}: {
  children: ReactNode;
  onClick?: MouseEventHandler<HTMLDivElement>;
}) {
  const { isMobile } = useBreakpoint('mobile');

  return (
    <div
      onClick={onClick}
      className={classNames(
        'p-3 rounded-lg xs:w-16 xs:h-16 mobile:w-10 flex flex-col justify-center items-center bg-background-60 hover:bg-opacity-50'
      )}
    >
      <Typography variant={isMobile ? 'section-title' : 'main-title'} bold>
        {children}
      </Typography>
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
  const { isMobile } = useBreakpoint('mobile');

  const [bodyParts, _ratioMode, currentSelection, dispatch, setRatioMode] =
    useManualProportions();
  const { currentLocales } = useLocaleConfig();
  const { l10n } = useLocalization();
  const cmFormat = Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'centimeter',
    maximumFractionDigits: 1,
  });
  const configFormat = Intl.NumberFormat(currentLocales, {
    signDisplay: 'always',
    maximumFractionDigits: 1,
  });
  const percentageFormat = Intl.NumberFormat(currentLocales, {
    style: 'percent',
    maximumFractionDigits: 1,
  });

  useEffect(() => {
    if (type === 'linear') {
      setRatioMode(false);
    } else {
      setRatioMode(true);
    }
  }, [type]);

  return (
    <div className="relative w-full">
      <div
        className={classNames(
          'flex flex-col xs:overflow-y-scroll xs:overflow-x-hidden xs:max-h-[450px] xs:h-[54vh]',
          'w-full px-1 gap-3 xs:gradient-mask-b-90'
        )}
      >
        <>
          {bodyParts.map(({ label, type, value: originalValue, ...props }) => {
            const value =
              'index' in props && props.index !== undefined
                ? props.bones[props.index].value
                : originalValue;
            const selected = isMobile || currentSelection.label === label;

            const selectNew = () => {
              switch (type) {
                case LabelType.Bone: {
                  if (!('bone' in props)) throw 'unreachable';
                  dispatch({
                    ...props,
                    label,
                    value,
                    type: ProportionChangeType.Bone,
                  });
                  break;
                }
                case LabelType.Group: {
                  if (!('bones' in props)) throw 'unreachable';
                  dispatch({
                    ...props,
                    label,
                    value,
                    type: ProportionChangeType.Group,
                    index: undefined,
                    parentLabel: label,
                  });
                  break;
                }
                case LabelType.GroupPart: {
                  if (!('index' in props)) throw 'unreachable';
                  dispatch({
                    ...props,
                    label,
                    // If this isn't done, we are replacing total
                    // with percentage value
                    value: originalValue,
                    type: ProportionChangeType.Group,
                    index: props.index,
                  });
                }
              }
            };

            return (
              <div className="flex" key={label}>
                <div
                  className={classNames(
                    'flex gap-2 transition-opacity duration-300',
                    !selected && 'opacity-0 pointer-events-none'
                  )}
                >
                  {!precise && (
                    <IncrementButton
                      onClick={() => {
                        selectNew();
                        return type === LabelType.GroupPart
                          ? dispatch({
                              type: ProportionChangeType.Ratio,
                              value: -0.05,
                            })
                          : dispatch({
                              type: ProportionChangeType.Linear,
                              value: -5,
                            });
                      }}
                    >
                      {configFormat.format(-5)}
                    </IncrementButton>
                  )}
                  <IncrementButton
                    onClick={() => {
                      selectNew();
                      return type === LabelType.GroupPart
                        ? dispatch({
                            type: ProportionChangeType.Ratio,
                            value: -0.01,
                          })
                        : dispatch({
                            type: ProportionChangeType.Linear,
                            value: -1,
                          });
                    }}
                  >
                    {configFormat.format(-1)}
                  </IncrementButton>
                  {precise && (
                    <IncrementButton
                      onClick={() => {
                        selectNew();
                        return type === LabelType.GroupPart
                          ? dispatch({
                              type: ProportionChangeType.Ratio,
                              value: -0.005,
                            })
                          : dispatch({
                              type: ProportionChangeType.Linear,
                              value: -0.5,
                            });
                      }}
                    >
                      {configFormat.format(-0.5)}
                    </IncrementButton>
                  )}
                </div>
                <div
                  className="flex flex-grow flex-col px-2"
                  onClick={selectNew}
                >
                  <div
                    key={label}
                    className={classNames(
                      'p-3 rounded-lg xs:h-16 flex w-full items-center justify-between xs:px-6 mobile:px-3 transition-colors duration-300 bg-background-60',
                      (selected && 'opacity-100') || 'opacity-50'
                    )}
                  >
                    <Typography variant="section-title" bold>
                      {l10n.getString(label)}
                    </Typography>
                    <Typography
                      variant={isMobile ? 'section-title' : 'main-title'}
                      bold
                    >
                      {type === LabelType.GroupPart
                        ? /* Make number rounding so it's based on .5 decimals */
                          percentageFormat.format(Math.round(value * 200) / 200)
                        : cmFormat.format(value * 100)}
                      {type === LabelType.GroupPart && (
                        <p className="text-standard">{`(${cmFormat.format(
                          value * originalValue * 100
                        )})`}</p>
                      )}
                    </Typography>
                  </div>
                </div>
                <div
                  className={classNames(
                    'flex gap-2 transition-opacity duration-300',
                    !selected && 'opacity-0 pointer-events-none'
                  )}
                >
                  {precise && (
                    <IncrementButton
                      onClick={() => {
                        selectNew();
                        return type === LabelType.GroupPart
                          ? dispatch({
                              type: ProportionChangeType.Ratio,
                              value: 0.005,
                            })
                          : dispatch({
                              type: ProportionChangeType.Linear,
                              value: 0.5,
                            });
                      }}
                    >
                      {configFormat.format(+0.5)}
                    </IncrementButton>
                  )}
                  <IncrementButton
                    onClick={() => {
                      selectNew();
                      return type === LabelType.GroupPart
                        ? dispatch({
                            type: ProportionChangeType.Ratio,
                            value: 0.01,
                          })
                        : dispatch({
                            type: ProportionChangeType.Linear,
                            value: 1,
                          });
                    }}
                  >
                    {configFormat.format(+1)}
                  </IncrementButton>
                  {!precise && (
                    <IncrementButton
                      onClick={() => {
                        selectNew();
                        return type === LabelType.GroupPart
                          ? dispatch({
                              type: ProportionChangeType.Ratio,
                              value: 0.05,
                            })
                          : dispatch({
                              type: ProportionChangeType.Linear,
                              value: 5,
                            });
                      }}
                    >
                      {configFormat.format(+5)}
                    </IncrementButton>
                  )}
                </div>
              </div>
            );
          })}
        </>
      </div>
    </div>
  );
}
