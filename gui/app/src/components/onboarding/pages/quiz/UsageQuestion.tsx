import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { QuizButton } from './SlimeSetQuestion';
import { useNavigate } from 'react-router-dom';
import { HeadsetIcon } from '@/components/commons/icon/HeadsetIcon';
import { HumanIcon } from '@/components/commons/icon/HumanIcon';

export function QuizUsageQuestion() {
  const { applyProgress, setUsage, usage } = useOnboarding();
  const nav = useNavigate();

  applyProgress(0.8);

  const next = (type: typeof usage) => {
    setUsage(type);
    if (type === 'vr-gaming') {
      if (window.__ANDROID__) {
        nav('/');
      } else {
        nav('/onboarding/quiz/runtime');
      }
    } else nav('/onboarding/quiz/mocap-pos');
  };

  return (
    <div className="grid w-full h-full justify-center items-center">
      <div className="flex flex-col gap-8 max-w-xl p-2">
        <div className="flex flex-col gap-2">
          <Typography variant="main-title" id="onboarding-quiz-usage-title" />
          <Typography
            whitespace="whitespace-pre-wrap"
            id="onboarding-quiz-usage-description"
          />
        </div>
        <div className="flex flex-col gap-6">
          <div className="grid grid-cols-2 gap-4">
            <QuizButton
              active={usage === 'vr-gaming'}
              icon={<HeadsetIcon width={50} />}
              name="onboarding-quiz-usage-answer-VRC"
              onClick={() => next('vr-gaming')}
            />
            <QuizButton
              active={usage === 'mocap'}
              icon={<HumanIcon width={50} />}
              name="onboarding-quiz-usage-answer-mocap_vtubing"
              onClick={() => next('mocap')}
            />
          </div>
        </div>
        <div className="flex">
          <Button
            to={'/onboarding/quiz/slime-set'}
            variant="secondary"
            id="onboarding-quiz_back"
          />
        </div>
      </div>
    </div>
  );
}
