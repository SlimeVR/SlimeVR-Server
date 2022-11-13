import { NavLink } from 'react-router-dom';
import { useOnboarding } from '../../../hooks/onboarding';
import { Button } from '../../commons/Button';
import { SlimeVRIcon } from '../../commons/icon/SimevrIcon';
import { Typography } from '../../commons/Typography';

export function DonePage() {
  const { applyProgress, skipSetup } = useOnboarding();

  applyProgress(1);

  return (
    <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
      <div className="flex flex-col gap-5 items-center z-10">
        <SlimeVRIcon></SlimeVRIcon>
        <Typography variant="main-title">You're all set!</Typography>
        <div className="flex flex-col items-center">
          <Typography color="secondary">
            Enjoy your full body experience
          </Typography>
        </div>
        <Button variant="primary" to="/" onClick={skipSetup}>
          Close the guide
        </Button>
      </div>
    </div>
  );
}
