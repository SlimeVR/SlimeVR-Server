import classNames from 'classnames';
import { Timeline, TimelineItem } from './Timeline';
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
  last,
}: {
  active: number;
  index: number;
  children: ReactNode;
  title: string;
  last: boolean;
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
      }, 300);
  }, [isSelected]);

  useLayoutEffect(() => {
    setShouldAnimate(true);
  }, [active]);

  // A collapsed step is only clipped, not unmounted, so keep its controls out
  // of the tab order and out of spatial focus nav until the step is open.
  useEffect(() => {
    if (ref.current) ref.current.inert = !isSelected;
  }, [isSelected]);

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
    <TimelineItem
      itemRef={refTop}
      className="scroll-m-4"
      state={isSelected ? 'current' : isPrevious ? 'done' : 'todo'}
      number={index + 1}
      last={last}
      title={title}
    >
      <div
        style={{ height: !isSelected ? 0 : height }}
        className={classNames('overflow-clip', {
          'duration-300 transition-[height]': shouldAnimate,
        })}
      >
        <div ref={ref}>
          <div className="pt-3">{children}</div>
        </div>
      </div>
    </TimelineItem>
  );
}

export type VerticalStepComponentProps = {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
  isActive: boolean;
};
type VerticalStepComponentType = FC<VerticalStepComponentProps>;

export type VerticalStep = {
  title: string;
  id?: string;
  component: VerticalStepComponentType;
};

export default function VerticalStepper({
  steps,
  onStepChange,
}: {
  steps: VerticalStep[];
  onStepChange?: (index: number, id?: string) => void;
}) {
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

  useEffect(() => {
    onStepChange?.(currStep, steps[currStep].id);
  }, [currStep]);

  return (
    <Timeline>
      {steps.map(({ title, component: StepComponent }, index) => (
        <VerticalStep
          active={currStep}
          index={index}
          title={title}
          last={index === steps.length - 1}
          key={index}
        >
          <StepComponent
            nextStep={nextStep}
            prevStep={prevStep}
            goTo={goTo}
            isActive={currStep === index}
          />
        </VerticalStep>
      ))}
    </Timeline>
  );
}
