import classNames from 'classnames';
import { CheckIcon } from './icon/CheckIcon';
import { Typography } from './Typography';
import {
  FC,
  ReactNode,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from 'react';
import { useElemSize } from '@/hooks/layout';
import { useDebouncedEffect } from '@/hooks/timeout';

export function VerticalStep({
  active,
  index,
  children,
  title,
}: {
  active: number;
  index: number;
  children: ReactNode;
  title: string;
}) {
  const ref = useRef<HTMLDivElement | null>(null);
  const refTop = useRef<HTMLLIElement | null>(null);
  const [shouldAnimate, setShouldAnimate] = useState(false);
  const { height } = useElemSize(ref);

  const isSelected = active === index;
  const isPrevious = active > index;

  useEffect(() => {
    if (!refTop.current) return;
    if (isSelected)
      setTimeout(() => {
        if (!refTop.current) return;
        refTop.current.scrollIntoView({ behavior: 'smooth' });
      }, 500);
  }, [isSelected]);

  useLayoutEffect(() => {
    setShouldAnimate(true);
  }, [active]);

  // Make it so it wont try to animate the size
  // if we are not changing active step
  useDebouncedEffect(
    () => {
      setShouldAnimate(false);
    },
    [active],
    1000
  );

  return (
    <li className="mb-10 scroll-m-4" ref={refTop}>
      <span
        className={classNames(
          'absolute flex items-center justify-center w-8 h-8 rounded-full -left-4 transition-colors fill-background-10',
          {
            'bg-accent-background-20': isSelected || isPrevious,
            'bg-background-40': !isSelected && !isPrevious,
          }
        )}
      >
        {isPrevious ? (
          <CheckIcon></CheckIcon>
        ) : (
          <Typography variant="section-title">{index + 1}</Typography>
        )}
      </span>
      <div className="ml-7 pt-1.5">
        <div className="px-1">
          <Typography variant="section-title">{title}</Typography>
        </div>
        <div
          style={{ height: !isSelected ? 0 : height }}
          className={classNames('overflow-clip px-1', {
            'duration-500 transition-[height]': shouldAnimate,
          })}
        >
          <div ref={ref}>{children}</div>
        </div>
      </div>
    </li>
  );
}

type VerticalStepComponentType = FC<{
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
  isActive: boolean;
}>;

export type VerticalStep = {
  title: string;
  id?: string;
  component: VerticalStepComponentType;
};

export default function VerticalStepper({ steps }: { steps: VerticalStep[] }) {
  const [currStep, setStep] = useState(0);

  const nextStep = () => {
    if (currStep + 1 === steps.length) return;
    setStep(currStep + 1);
  };

  const prevStep = () => {
    if (currStep - 1 < 0) return;
    setStep(currStep - 1);
  };

  const goTo = (id: string) => {
    const step = steps.findIndex(({ id: stepId }) => stepId === id);
    if (step === -1) throw new Error('step not found');

    setStep(step);
  };

  return (
    <ol className="relative border-l border-gray-700 text-gray-400">
      {steps.map(({ title, component: StepComponent }, index) => (
        <VerticalStep active={currStep} index={index} title={title} key={index}>
          <StepComponent
            nextStep={nextStep}
            prevStep={prevStep}
            goTo={goTo}
            isActive={currStep === index}
          ></StepComponent>
        </VerticalStep>
      ))}
    </ol>
  );
}
