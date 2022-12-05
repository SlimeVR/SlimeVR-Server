import { useTranslation } from 'react-i18next';
import { useOnboarding } from '../../../hooks/onboarding';
import { ArrowLink } from '../../commons/ArrowLink';
import { Button } from '../../commons/Button';
import { Typography } from '../../commons/Typography';

export function EnterVRPage() {
  const { t } = useTranslation();
  const { applyProgress, skipSetup } = useOnboarding();

  applyProgress(0.6);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
        <div className="flex flex-col w-full h-full justify-center items-center">
          <div className="flex gap-8">
            <div className="flex flex-col max-w-md gap-3">
              <ArrowLink to="/onboarding/trackers-assign" direction="left">
                {t('onboarding.enter-vr.back')}
              </ArrowLink>
              <Typography variant="main-title">
                {t('onboarding.enter-vr.title')}
              </Typography>
              <Typography color="secondary">
                {t('onboarding.enter-vr.description')}
              </Typography>
            </div>
            {/* <div className="flex flex-col flex-grow gap-3 rounded-xl fill-background-50">
              <Typography variant="main-title">Illustration HERE</Typography>
            </div> */}
          </div>
        </div>
        <div className="w-full py-4 flex flex-row">
          <div className="flex flex-grow">
            <Button variant="secondary" to="/" onClick={skipSetup}>
              {t('onboarding.skip')}
            </Button>
          </div>
          <div className="flex gap-3">
            <Button variant="primary" to="/onboarding/mounting/auto">
              {t('onboarding.enter-vr.ready')}
            </Button>
          </div>
        </div>
      </div>
    </>
  );
}
