import { useOnboarding } from '@/hooks/onboarding';
import { useLocalization } from '@fluent/react';
import { useState } from 'react';
import { SkipSetupWarningModal } from '@/components/onboarding/SkipSetupWarningModal';
import classNames from 'classnames';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { track } from '@/utils/sentry';
import { MountingMethod } from 'solarxr-protocol';
import { useResetsSettings } from '@/hooks/resets-settings';
import { MANUAL_MOUNTING_PATH, STEP_MOUNTING_PATH } from '@/hooks/mounting-method';
import { SkiIcon } from '@/components/commons/icon/SkiIcon';
import { StepIcon } from '@/components/commons/icon/StepIcon';
import { TuneIcon } from '@/components/commons/icon/TuneIcon';

function MethodCard({
  method,
  name,
  to,
  icon,
  recommended = false,
  alonePage,
  variant = alonePage ? 'tertiary' : 'secondary',
  decoration,
}: {
  alonePage: boolean;
  method: MountingMethod;
  name: string;
  to: string;
  icon: React.ReactNode;
  recommended?: boolean;
  variant?: 'primary' | 'secondary' | 'tertiary';
  decoration?: React.ReactNode;
}) {
  const { setResetsSettings } = useResetsSettings();

  return (
    <div
      className={classNames(
        'rounded-lg p-4 flex relative',
        !alonePage && 'bg-background-70',
        alonePage && 'bg-background-60'
      )}
    >
      {recommended && (
        <div className="bg-accent-background-30 absolute -left-4 -top-5 p-1.5 rounded-lg">
          <Typography
            variant="vr-accessible"
            italic
            id="mounting_method-recommended"
          />
        </div>
      )}
      {decoration}
      <div className="flex flex-col gap-4">
        <div className="flex flex-grow flex-col gap-3 max-w-sm">
          <div className="fill-background-10">{icon}</div>
          <Typography
            variant="main-title"
            bold
            id={`mounting_method-${name}`}
          />
          <Typography
            whitespace="whitespace-pre-line"
            id={`mounting_method-${name}-description`}
          />
        </div>
        <Button
          variant={variant}
          to={to}
          className="self-start mt-auto"
          state={{ alonePage: alonePage }}
          onClick={() => {
            setResetsSettings({ mountingMethod: method });
            track('mounting_choose', { choose: name });
          }}
          id="mounting_method-select"
        />
      </div>
    </div>
  );
}

export function MountingChoose() {
  const { l10n } = useLocalization();
  const { applyProgress, skipSetup, state } = useOnboarding();
  const [animated, setAnimated] = useState(false);
  const [showWarning, setShowWarning] = useState(false);

  applyProgress(0.55);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full xs:justify-center relative overflow-y-auto px-4 pb-4">
        <div className="flex flex-col gap-8 justify-center">
          <div className="xs:w-10/12 xs:max-w-[666px]">
            <Typography variant="main-title">
              {l10n.getString('onboarding-choose_mounting')}
            </Typography>
            <Typography variant="standard" whitespace="whitespace-pre-line">
              {l10n.getString('onboarding-choose_mounting-description')}
            </Typography>
          </div>
          <div className="grid xs:grid-cols-3 w-full xs:flex-row mobile:flex-col gap-4 [&>div]:grow">
            <MethodCard
              alonePage={state.alonePage}
              method={MountingMethod.STEP}
              name="step"
              to={STEP_MOUNTING_PATH}
              icon={<StepIcon size={32} />}
              recommended
              variant="primary"
            />
            <MethodCard
              alonePage={state.alonePage}
              method={MountingMethod.POSE}
              name="pose"
              to="/onboarding/mounting/auto"
              icon={<SkiIcon size={32} />}
            />
            <MethodCard
              alonePage={state.alonePage}
              method={MountingMethod.MANUAL}
              name="manual"
              to={MANUAL_MOUNTING_PATH}
              icon={<TuneIcon size={32} />}
              decoration={
                <img
                  onMouseEnter={() => setAnimated(() => true)}
                  onAnimationEnd={() => setAnimated(() => false)}
                  src="/images/boxslime.webp"
                  className={classNames(
                    'absolute w-[100px] -right-2 -top-10',
                    animated && 'animate-[bounce_1s_1]'
                  )}
                />
              }
            />
          </div>
          {!state.alonePage && (
            <Button
              variant="secondary"
              className="self-start"
              to="/onboarding/trackers-assign"
            >
              {l10n.getString('onboarding-previous_step')}
            </Button>
          )}
        </div>
      </div>
      <SkipSetupWarningModal
        accept={skipSetup}
        onClose={() => setShowWarning(false)}
        isOpen={showWarning}
      />
    </>
  );
}
