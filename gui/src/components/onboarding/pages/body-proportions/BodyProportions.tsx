import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import {
  MouseEventHandler,
  ReactNode,
  useEffect,
  useRef,
  UIEvent,
  MouseEvent,
  useMemo,
} from 'react';
import {
  LabelType,
  ProportionChangeType,
  useManualProportions,
} from '../../../../hooks/manual-proportions';
import { useLocaleConfig } from '../../../../i18n/config';
import { Typography } from '../../../commons/Typography';
import { ArrowDownIcon, ArrowUpIcon } from '../../../commons/icon/ArrowIcons';
import { useBreakpoint } from '../../../../hooks/breakpoint';

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
        'p-3 rounded-lg xs:w-16 xs:h-16 mobile:w-10 flex flex-col justify-center items-center',
        'bg-background-60 hover:bg-opacity-50 active:bg-accent-background-30'
      )}
    >
      <Typography variant="mobile-title" bold>
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
  const { bodyParts, dispatch, state, setRatioMode } = useManualProportions();
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const tall = useBreakpoint('tall');

  const srcollerRef = useRef<HTMLDivElement | null>(null);

  const cmFormat = Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'centimeter',
    maximumFractionDigits: 1,
  });
  const percentageFormat = Intl.NumberFormat(currentLocales, {
    style: 'percent',
    maximumFractionDigits: 1,
  });
  const configFormat = Intl.NumberFormat(currentLocales, {
    signDisplay: 'always',
    maximumFractionDigits: 1,
  });

  useEffect(() => {
    if (type === 'linear') {
      setRatioMode(false);
    } else {
      setRatioMode(true);
    }
  }, [type]);

  useEffect(() => {
    if (srcollerRef.current && bodyParts.length > 0) {
      moveToIndex(1);
    }
  }, [srcollerRef, bodyParts.length]);

  const handleUIEvent = (e: UIEvent<HTMLDivElement>) => {
    const target = e.target as HTMLDivElement;

    const itemHeight = target.offsetHeight / 3;

    const atSnappingPoint = target.scrollTop % itemHeight === 0;

    if (atSnappingPoint) {
      const index = target.scrollTop / itemHeight;
      const elem = srcollerRef.current?.childNodes[index + 1] as HTMLDivElement;
      const id = elem.getAttribute('itemid');
      if (id) selectNew(id);
    }
  };

  const clickPart = (id: string) => (e: MouseEvent<HTMLDivElement>) => {
    const target = e.target as HTMLDivElement;
    const snap = target.closest<HTMLDivElement>('.snap-start');
    console.log(snap);
    if (srcollerRef.current && snap) {
      console.log(`${snap.offsetTop} - ${srcollerRef.current.offsetHeight}`)
      srcollerRef.current.scroll({
        top: snap.offsetTop - srcollerRef.current.offsetHeight,
        behavior: 'smooth',
      });
    }
    selectNew(id);
  };

  const moveToIndex = (index: number, smooth = true) => {
    // We add one because of the offset placeholder
    const elem = srcollerRef.current?.childNodes[index + 1] as HTMLDivElement;
    console.log(elem.offsetTop);
    if (srcollerRef.current) {
      const scrollBound = srcollerRef.current.getBoundingClientRect();
      const elemBound = elem.getBoundingClientRect();
      srcollerRef.current.scroll({
        top: elemBound.top - scrollBound.height - elemBound.height,
        behavior: smooth ? 'smooth' : 'auto',
      });
    }

    const id = elem.getAttribute('itemid');
    if (id) selectNew(id);
  };

  const selectNew = (id: string) => {
    const part = bodyParts.find(({ label }) => label === id);
    if (!part) return;

    const { value: originalValue, label, type, ...props } = part;

    const value =
      'index' in props && props.index !== undefined
        ? props.bones[props.index].value
        : originalValue;

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

  const seletedLabel = useMemo(() => {
    return bodyParts.find(({ label }) => label === state.currentLabel);
  }, [state]);

  const move = (action: 'next' | 'prev') => {
    const elem = srcollerRef.current?.querySelector(
      `div[itemid=${state.currentLabel}]`
    );

    const moveId = (id: string, elem: HTMLDivElement) => {
      if (srcollerRef.current) {
        const scrollBound = srcollerRef.current.getBoundingClientRect();
        const elemBound = elem.getBoundingClientRect();
        srcollerRef.current.scroll({
          top: elemBound.top - scrollBound.height - elemBound.height,
          behavior: 'smooth',
        });
      }
      selectNew(id);
    };

    if (action === 'prev') {
      const prevElem = elem?.previousSibling as HTMLDivElement;
      const prevId = prevElem.getAttribute('itemid');
      if (!prevId) return;
      moveId(prevId, prevElem);
    }

    if (action === 'next') {
      const nextElem = elem?.nextSibling as HTMLDivElement;
      const nextId = nextElem.getAttribute('itemid');
      if (!nextId) return;
      moveId(nextId, nextElem);
    }
  };

  return (
    (bodyParts.length > 0 && (
      <div className="flex w-full gap-3">
        <div className="flex items-center mobile:justify-center mobile:flex-col gap-2 my-2">
          {!precise && (
            <div className="mobile:order-2">
              <IncrementButton
                onClick={() =>
                  seletedLabel?.type === LabelType.GroupPart
                    ? dispatch({
                        type: ProportionChangeType.Ratio,
                        value: -0.05,
                      })
                    : dispatch({
                        type: ProportionChangeType.Linear,
                        value: -5,
                      })
                }
              >
                {configFormat.format(-5)}
              </IncrementButton>
            </div>
          )}
          <div className="mobile:order-1">
            <IncrementButton
              onClick={() =>
                seletedLabel?.type === LabelType.GroupPart
                  ? dispatch({
                      type: ProportionChangeType.Ratio,
                      value: -0.01,
                    })
                  : dispatch({
                      type: ProportionChangeType.Linear,
                      value: -1,
                    })
              }
            >
              {configFormat.format(-1)}
            </IncrementButton>
          </div>
          {precise && (
            <div className="mobile:order-2">
              <IncrementButton
                onClick={() =>
                  seletedLabel?.type === LabelType.GroupPart
                    ? dispatch({
                        type: ProportionChangeType.Ratio,
                        value: -0.005,
                      })
                    : dispatch({
                        type: ProportionChangeType.Linear,
                        value: -0.5,
                      })
                }
              >
                {configFormat.format(-0.5)}
              </IncrementButton>
            </div>
          )}
        </div>
        <div className="flex flex-grow flex-col">
          <div className="flex justify-center">
            <div
              onClick={() => move('prev')}
              className={classNames(
                'h-12 w-32 rounded-lg bg-background-60 flex flex-col justify-center',
                'items-center fill-background-10',
                srcollerRef?.current?.scrollTop ?? 0 > 0
                  ? 'opacity-100 active:bg-accent-background-30'
                  : 'opacity-50'
              )}
            >
              <ArrowUpIcon size={32}></ArrowUpIcon>
            </div>
          </div>
          <div
            ref={srcollerRef}
            onScroll={handleUIEvent}
            className="h-60 tall:h-[25rem] flex-grow flex-col overflow-y-auto snap-y snap-mandatory snap-always no-scrollbar"
          >
            {tall.isTall && <div className="h-20 snap-start"></div>}
            <div className="h-20 snap-start "></div>
            {bodyParts.map((part) => {
              const { label, value: originalValue, type, ...props } = part;
              const value =
                'index' in props && props.index !== undefined
                  ? props.bones[props.index].value
                  : originalValue;

              const selected = state.currentLabel === label;

              return (
                <div
                  key={label}
                  itemID={label}
                  onClick={clickPart(label)}
                  className="snap-start h-20 flex-col flex justify-center"
                >
                  <div
                    className={classNames(
                      'h-16 p-3 rounded-lg flex w-full items-center justify-between px-6 transition-colors',
                      'duration-300 bg-background-60',
                      (selected && 'opacity-100') ||
                        'opacity-50 active:bg-accent-background-30'
                    )}
                  >
                    <Typography variant="section-title" bold>
                      {l10n.getString(label)}
                    </Typography>
                    <Typography variant="mobile-title" bold>
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
              );
            })}
            <div className="h-20 snap-start "></div>
            {tall.isTall && <div className="h-20 snap-start"></div>}
          </div>
          <div className="flex justify-center">
            <div
              onClick={() => move('next')}
              className={classNames(
                'h-12 w-32 rounded-lg bg-background-60 flex flex-col justify-center',
                'items-center fill-background-10',
                srcollerRef?.current?.scrollTop !==
                  (srcollerRef?.current?.scrollHeight ?? 0) -
                    (srcollerRef?.current?.offsetHeight ?? 0)
                  ? 'opacity-100 active:bg-accent-background-30'
                  : 'opacity-50'
              )}
            >
              <ArrowDownIcon size={32}></ArrowDownIcon>
            </div>
          </div>
        </div>

        <div className="flex items-center mobile:justify-center mobile:flex-col gap-2">
          {precise && (
            <div className="mobile:order-2">
              <IncrementButton
                onClick={() =>
                  seletedLabel?.type === LabelType.GroupPart
                    ? dispatch({
                        type: ProportionChangeType.Ratio,
                        value: 0.005,
                      })
                    : dispatch({
                        type: ProportionChangeType.Linear,
                        value: 0.5,
                      })
                }
              >
                {configFormat.format(+0.5)}
              </IncrementButton>
            </div>
          )}

          <div className="mobile:order-1">
            <IncrementButton
              onClick={() =>
                seletedLabel?.type === LabelType.GroupPart
                  ? dispatch({
                      type: ProportionChangeType.Ratio,
                      value: 0.01,
                    })
                  : dispatch({
                      type: ProportionChangeType.Linear,
                      value: 1,
                    })
              }
            >
              {configFormat.format(+1)}
            </IncrementButton>
          </div>
          {!precise && (
            <div className="mobile:order-2">
              <IncrementButton
                onClick={() =>
                  seletedLabel?.type === LabelType.GroupPart
                    ? dispatch({
                        type: ProportionChangeType.Ratio,
                        value: 0.05,
                      })
                    : dispatch({
                        type: ProportionChangeType.Linear,
                        value: 5,
                      })
                }
              >
                {configFormat.format(+5)}
              </IncrementButton>
            </div>
          )}
        </div>
      </div>
    )) || <></>
  );
}
