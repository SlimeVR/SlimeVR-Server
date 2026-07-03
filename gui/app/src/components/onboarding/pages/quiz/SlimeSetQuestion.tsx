import { useOnboarding } from '@/hooks/onboarding';
import { ReactNode } from 'react';
import { Typography } from '@/components/commons/Typography';
import classNames from 'classnames';
import { USBIcon } from '@/components/commons/icon/UsbIcon';
import { useNavigate } from 'react-router-dom';

export function QuizButton({
  name,
  active,
  icon,
  onClick,
}: {
  active?: boolean;
  name: string;
  icon: ReactNode;
  onClick: () => void;
}) {
  return (
    <div
      onClick={onClick}
      className={classNames(
        'flex rounded-lg bg-background-60 hover:bg-background-50 cursor-pointer',
        'p-4 outline outline-2 flex-col gap-4 items-center justify-between fill-background-20',
        {
          'outline-accent-background-30': active,
          'outline-transparent': !active,
        }
      )}
    >
      {icon}
      <Typography id={name} variant="section-title" />
    </div>
  );
}

export function QuizSlimeSetQuestion() {
  const { applyProgress, setSlimeSet, slimeSet } = useOnboarding();
  const nav = useNavigate();

  applyProgress(0.2);

  const next = (type: typeof slimeSet) => {
    setSlimeSet(type);
    switch (type) {
      case 'butterfly':
      case 'dongle-slime':
        nav('/onboarding/dongle');
        break;
      case 'slime-v1':
      case 'wifi-slime':
        nav('/onboarding/wifi-creds');
        break;
    }
  };

  return (
    <div className="grid w-full h-full justify-center items-center">
      <div className="flex flex-col gap-8 max-w-xl p-2">
        <div className="flex flex-col gap-2">
          <Typography
            variant="main-title"
            id="onboarding-quiz-slimeset-title"
          />
          <Typography id="onboarding-quiz-slimeset-description" />
        </div>

        <div className="flex flex-col gap-6">
          <div className="flex gap-2 flex-col">
            <Typography
              variant="section-title"
              id="onboarding-quiz-slimeset-official-sets"
            />
            <div className="grid grid-cols-2 gap-4">
              <QuizButton
                active={slimeSet === 'slime-v1'}
                onClick={() => next('slime-v1')}
                icon={
                  <img
                    src="/images/trackers/v1_2_slime.webp"
                    className="w-60"
                  />
                }
                name="onboarding-quiz-slimeset-answer-regular"
              />
              <QuizButton
                active={slimeSet === 'butterfly'}
                onClick={() => next('butterfly')}
                icon={
                  <img
                    src="/images/trackers/butterfly_slime.webp"
                    className="w-60"
                  />
                }
                name="onboarding-quiz-slimeset-answer-butterfly"
              />
            </div>
          </div>
          <div className="flex gap-2 flex-col">
            <Typography
              variant="section-title"
              id="onboarding-quiz-slimeset-thirdparty-sets"
            />
            <div className="grid grid-cols-2 gap-4">
              <QuizButton
                onClick={() => next('wifi-slime')}
                icon={
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    height="50"
                    viewBox="0 0 24 24"
                    width="50"
                    className="fill-background-20"
                  >
                    <path d="M0 0h24v24H0z" fill="none" />
                    <path d="M1 9l2 2c4.97-4.97 13.03-4.97 18 0l2-2C16.93 2.93 7.08 2.93 1 9zm8 8l3 3 3-3c-1.65-1.66-4.34-1.66-6 0zm-4-4l2 2c2.76-2.76 7.24-2.76 10 0l2-2C15.14 9.14 8.87 9.14 5 13z" />
                  </svg>
                }
                name="onboarding-quiz-slimeset-answer-wifi"
              />
              <QuizButton
                onClick={() => next('dongle-slime')}
                icon={
                  <div className="fill-background-20">
                    <USBIcon size={50} />
                  </div>
                }
                name="onboarding-quiz-slimeset-answer-dongle"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
