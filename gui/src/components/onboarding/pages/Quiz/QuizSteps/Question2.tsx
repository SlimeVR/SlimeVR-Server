import { useOnboarding } from '@/hooks/onboarding';
import classNames from 'classnames';
import { useState, useContext } from 'react';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { QuizContext } from '@/App';
import { Localized } from '@fluent/react';

export function QuizPage2() {
  const { setUsage } = useContext(QuizContext);
  const { applyProgress } = useOnboarding();
  const [outline, setOutline] = useState<'vrchat' | 'mocap' | 'vtubing'>();
  const [to, setTo] = useState('');
  const [disabled, setDisabled] = useState(true);

  applyProgress(0.2);

  return (
    <div className="flex flex-col w-full h-full xs:justify-center items-center">
      <div className="flex flex-col gap-2">
        <div className="flex gap-2 items-center">
          <Typography variant="main-title" id="onboarding-quiz-q2-title" />
        </div>
        <div className="">
          <div className={classNames('flex flex-col gap-2 flex-grow p-2')}>
            <Typography
              whitespace="whitespace-pre-wrap"
              id="onboarding-quiz-q2-description"
            />
          </div>
          <div className="flex gap-2 px-2 p-6">
            <div
              onClick={() => {
                setOutline('vrchat');
                setUsage('vrchat');
                setTo('/onboarding/quiz/Q3');
                setDisabled(false);
              }}
              className={classNames(
                'rounded-lg overflow-hidden transition-[box-shadow] duration-200 ease-linear hover:bg-background-50 cursor-pointer bg-background-60',
                outline === 'vrchat' &&
                  'outline outline-3 outline-accent-background-40'
              )}
            >
              <div className="flex flex-col justify-center rounded-md py-3 pr-4 pl-4 w-full gap-2 box-border">
                <div className="min-h-9 flex text-default justify-center gap-5 flex-wrap items-center">
                  <Typography id="onboarding-quiz-q2-answer-1" />
                </div>
              </div>
            </div>
            <div
              onClick={() => {
                setOutline('mocap');
                setUsage('mocap');
                setTo('/onboarding/quiz/Q4');
                setDisabled(false);
              }}
              className={classNames(
                'rounded-lg overflow-hidden transition-[box-shadow] duration-200 ease-linear hover:bg-background-50 cursor-pointer bg-background-60',
                outline === 'mocap' &&
                  'outline outline-3 outline-accent-background-40'
              )}
            >
              <div className="flex flex-col justify-center rounded-md py-3 pr-4 pl-4 w-full gap-2 box-border">
                <div className="min-h-9 flex text-default justify-center gap-5 flex-wrap items-center">
                  <Typography id="onboarding-quiz-q2-answer-2" />
                </div>
              </div>
            </div>
            <div
              onClick={() => {
                setOutline('vtubing');
                setUsage('vtubing');
                setTo('/onboarding/quiz/Q4');
                setDisabled(false);
              }}
              className={classNames(
                'rounded-lg overflow-hidden transition-[box-shadow] duration-200 ease-linear hover:bg-background-50 cursor-pointer bg-background-60',
                outline === 'vtubing' &&
                  'outline outline-3 outline-accent-background-40'
              )}
            >
              <div className="flex flex-col justify-center rounded-md py-3 pr-4 pl-4 w-full gap-2 box-border">
                <div className="min-h-9 flex text-default justify-center gap-5 flex-wrap items-center">
                  <Typography id="onboarding-quiz-q2-answer-3" />
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className="flex px-2 p-6">
          <Localized id="onboarding-quiz_continue">
            <Button
              to={to}
              children="Continue"
              variant="primary"
              disabled={disabled}
            />
          </Localized>
        </div>
      </div>
    </div>
  );
}
