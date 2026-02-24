import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import { QuizButton } from './SlimeSetQuestion';
import { SteamIcon } from '@/components/commons/icon/SteamIcon';
import { HeadsetIcon } from '@/components/commons/icon/HeadsetIcon';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/commons/Button';

export function QuizRuntimeQuestion() {
  const { applyProgress, setVrcOSC, vrcOsc } = useOnboarding();
  const nav = useNavigate();

  applyProgress(0.4);

  const next = (type: typeof vrcOsc) => {
    setVrcOSC(type);
    nav('/');
  };

  return (
    <div className="flex flex-col w-full h-full items-center justify-center">
      <div className="flex flex-col gap-12 max-w-xl p-2">
        <div className="flex flex-col gap-2">
          <Typography variant="main-title" id="onboarding-quiz-usage-title" />
        </div>

        <div className="flex flex-col gap-6">
          <div className="flex gap-2 flex-col">
            <div className="grid grid-cols-2 gap-4">
              <QuizButton
                active={!vrcOsc}
                onClick={() => next(false)}
                icon={<SteamIcon size={50} />}
                name="onboarding-quiz-runtime-answer-steamvr"
              />
              <QuizButton
                active={vrcOsc}
                onClick={() => next(true)}
                icon={<HeadsetIcon width={80} />}
                name="onboarding-quiz-runtime-answer-standalone"
              />
            </div>
          </div>
        </div>
        <div className="flex">
          <Button
            to={'/onboarding/quiz/usage'}
            variant="secondary"
            id="onboarding-quiz_back"
          />
        </div>
      </div>
    </div>
  );
}
