import classNames from 'classnames';
import {
  FC,
  MouseEventHandler,
  ReactNode,
  useEffect,
  useRef,
  useState
} from 'react';
import { useElemSize } from '../../hooks/layout';
import { CheckIcon } from '../commons/icon/CheckIcon';
import { Typography } from '../commons/Typography';

type StepComponentType = FC<{
  nextStep: () => void;
  prevStep: () => void;
  resetSteps: () => void;
  variant: 'alone' | 'onboarding';
}>;
type Step = { type: 'numbered' | 'fullsize'; component: StepComponentType };

export function StepContainer({
  children,
  width,
  active,
  type,
  step,
  variant,
}: {
  type: 'numbered' | 'fullsize';
  variant: 'alone' | 'onboarding';
  children: ReactNode;
  width: number;
  active: boolean;
  step: number;
}) {
  return (
    <div
      className={classNames(
        'step-container transition-transform duration-500 w-full p-8 rounded-lg flex gap-4 h-full',
        !active && 'opacity-40 pointer-events-none',
        variant === 'onboarding' && 'bg-background-70',
        variant === 'alone' && 'bg-background-60'
      )}
      style={{
        minWidth: width,
        width,
      }}
    >
      {type === 'numbered' && (
        <div className="flex flex-col">
          <div className="bg-accent-background-40 rounded-full h-8 w-8 flex flex-col items-center justify-center">
            <Typography variant="section-title" bold>
              {step + 1}
            </Typography>
          </div>
        </div>
      )}
      {children}
    </div>
  );
}

export function StepDot({
  active,
  done,
  onClick,
}: {
  active?: boolean;
  done?: boolean;
  onClick?: MouseEventHandler<HTMLDivElement>;
}) {
  return (
    <div
      className={classNames(
        'flex h-4 w-4 rounded-full justify-center items-center fill-background-10 transition-all',
        active || done ? 'bg-accent-background-20 ' : 'bg-background-60'
      )}
      onClick={onClick}
    >
      {active && (
        <div className="flex h-2 w-2 rounded-full bg-background-10"></div>
      )}
      {done && <CheckIcon />}
    </div>
  );
}

export function StepperSlider({
  variant,
  steps,
}: {
  variant: 'alone' | 'onboarding';
  steps: Step[];
}) {
  const ref = useRef<HTMLDivElement | null>(null);
  const { width } = useElemSize(ref);
  const [stepsContainers, setSteps] = useState(0);
  const [step, setStep] = useState(0);

  useEffect(() => {
    if (!ref.current) return;
    const stepsContainers =
      ref.current.getElementsByClassName('step-container');
    setSteps(stepsContainers.length);
  }, [ref]);

  const nextStep = () => {
    if (step + 1 === stepsContainers) return;
    setStep(step + 1);
  };

  const prevStep = () => {
    if (step - 1 < 0) return;
    setStep(step - 1);
  };

  const resetSteps = () => {
    setStep(0);
  };

  return (
    <div className="w-full flex flex-col gap-4">
      <div className="w-full flex" ref={ref}>
        <div
          className="transition-transform duration-500 flex gap-8"
          style={{ transform: `translateX(-${(width + 32) * step}px)` }}
        >
          {steps.map(({ type, component: StepComponent }, index) => (
            <StepContainer
              variant={variant}
              key={index}
              type={type}
              width={width}
              active={index === step}
              step={step}
            >
              <StepComponent
                variant={variant}
                nextStep={nextStep}
                prevStep={prevStep}
                resetSteps={resetSteps}
              />
            </StepContainer>
          ))}
        </div>
      </div>
      <div className="flex justify-center items-center gap-2">
        {Array.from({ length: stepsContainers }).map((_, index) => (
          <div key={index} className="flex items-center gap-2">
            {index !== 0 && (
              <div className="w-5 h-1 bg-background-50 rounded-full"></div>
            )}
            <StepDot
              active={index === step}
              done={index < step}
              // onClick={() => setStep(index)}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
