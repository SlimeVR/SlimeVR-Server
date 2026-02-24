import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { QuizButton } from './SlimeSetQuestion';
import { HumanIcon } from '@/components/commons/icon/HumanIcon';
import { HeadsetIcon } from '@/components/commons/icon/HeadsetIcon';
import { useState } from 'react';
import { SittingIcon } from '@/components/commons/icon/SittingIcon';
import { VMCFileUpload } from '@/components/settings/pages/VMCSettings';

export function QuizMocapPosQuestion() {
  const { applyProgress, setMocapPos, mocapPos, playspace, setPlayspace } =
    useOnboarding();

  const [headTracker, setHeadTracker] = useState(true);

  applyProgress(0.4);

  const canContinue = (!headTracker || mocapPos) && playspace;

  return (
    <div className="grid w-full h-full justify-center items-center">
      <div className="flex flex-col gap-8 max-w-xl p-2">
        <div className="flex flex-col gap-2">
          <Typography
            variant="main-title"
            id="onboarding-quiz-mocap_preferences-title"
          />
          <Typography
            whitespace="whitespace-pre-wrap"
            id="onboarding-quiz-mocap_preferences-desc"
          />
        </div>
        <div className="flex flex-col gap-6">
          <div className="flex gap-2 flex-col">
            <div className="flex flex-col gap-2">
              <Typography
                variant="section-title"
                id="onboarding-quiz-mocap_preferences-playspace-title"
              />
              <Typography id="onboarding-quiz-mocap_preferences-playspace-desc" />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <QuizButton
                active={playspace === 'sitting'}
                onClick={() => setPlayspace('sitting')}
                icon={<SittingIcon size={50} />}
                name="onboarding-quiz-mocap_preferences-playspace-sitting"
              />
              <QuizButton
                active={playspace === 'standing'}
                onClick={() => setPlayspace('standing')}
                icon={<HumanIcon width={50} />}
                name="onboarding-quiz-mocap_preferences-playspace-standing"
              />
            </div>
          </div>
        </div>
        <div className="flex flex-col gap-6">
          <div className="flex gap-2 flex-col">
            <div className="flex flex-col gap-2">
              <Typography
                variant="section-title"
                id="onboarding-quiz-mocap_preferences-vrm_model-title"
              />
              <Typography id="onboarding-quiz-mocap_preferences-vrm_model-desc" />
            </div>
            <VMCFileUpload />
          </div>
        </div>
        <div className="flex flex-col gap-6">
          <div className="flex gap-2 flex-col">
            <div className="flex flex-col gap-2">
              <Typography
                variant="section-title"
                id="onboarding-quiz-mocap_preferences-head_tracker-title"
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <QuizButton
                active={headTracker}
                onClick={() => setHeadTracker(true)}
                icon={<HeadsetIcon width={50} />}
                name="onboarding-quiz-mocap_preferences-head_tracker-yes"
              />
              <QuizButton
                active={!headTracker}
                onClick={() => {
                  setHeadTracker(false);
                  setMocapPos(undefined);
                }}
                icon={<HeadsetIcon width={50} disabled />}
                name="onboarding-quiz-mocap_preferences-head_tracker-no"
              />
            </div>
          </div>
        </div>
        {headTracker && (
          <div className="flex flex-col gap-6">
            <div className="flex gap-2 flex-col">
              <div className="flex flex-col gap-2">
                <Typography
                  variant="section-title"
                  id="onboarding-quiz-mocap_preferences-head_tracker_location-title"
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <QuizButton
                  active={mocapPos === 'forehead'}
                  onClick={() => setMocapPos('forehead')}
                  icon={
                    <img
                      src="/images/quiz/quiz_mocap-pos_forehead.webp"
                      className="w-44"
                    />
                  }
                  name="onboarding-quiz-mocap_preferences-head_tracker_location-forehead"
                />
                <QuizButton
                  active={mocapPos === 'face'}
                  onClick={() => setMocapPos('face')}
                  icon={
                    <img
                      src="/images/quiz/quiz_mocap-pos_face.webp"
                      className="w-44"
                    />
                  }
                  name="onboarding-quiz-mocap_preferences-head_tracker_location-face"
                />
              </div>
            </div>
          </div>
        )}

        <div className="flex justify-between pb-4">
          <Button
            variant="secondary"
            id="onboarding-quiz_back"
            to="/onboarding/quiz/usage"
          />
          <Button
            variant="primary"
            id="onboarding-quiz_continue"
            disabled={!canContinue}
            to="/"
          />
        </div>
      </div>
    </div>
  );
}
