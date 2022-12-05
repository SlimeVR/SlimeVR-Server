import { useTranslation } from 'react-i18next';
import { useOnboarding } from '../../../hooks/onboarding';
import { ArrowLink } from '../../commons/ArrowLink';
import { Button } from '../../commons/Button';
import { Typography } from '../../commons/Typography';

export function ResetTutorialPage() {
  const { t } = useTranslation();
  const { applyProgress, skipSetup } = useOnboarding();

  applyProgress(0.8);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
        <div className="flex flex-col w-full h-full justify-center px-20">
          <div className="flex gap-8">
            <div className="flex flex-col max-w-md gap-3">
              <ArrowLink to="/onboarding/mounting/auto" direction="left">
                {t('onboarding.reset-tutorial.back')}
              </ArrowLink>
              <Typography variant="main-title">
                {t('onboarding.reset-tutorial.title')}
                <span className="mx-2 p-1 bg-accent-background-30 text-standard rounded-md">
                  {t('onboarding.wip')}
                </span>
              </Typography>
              <Typography color="secondary">
                {t('onboarding.reset-tutorial.description')}
              </Typography>
            </div>
          </div>
        </div>
        <div className="w-full py-4 flex flex-row">
          <div className="flex flex-grow">
            <Button variant="secondary" to="/" onClick={skipSetup}>
              {t('onboarding.skip')}
            </Button>
          </div>
          <div className="flex gap-3">
            <Button variant="primary" to="/onboarding/body-proportions/auto">
              {t('onboarding.continue')}
            </Button>
          </div>
        </div>
      </div>
    </>
  );
}
