import { Button } from '../../../../commons/Button';
import { PersonFrontIcon } from '../../../../commons/PersonFrontIcon';
import { TipBox } from '../../../../commons/TipBox';
import { Typography } from '../../../../commons/Typography';

export function PutTrackersOnStep({ nextStep }: { nextStep: () => void }) {
  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            Put on your trackers
          </Typography>
          <div>
            <Typography color="secondary">
              To calibrate your proportions, we're gonna use the trackers you
            </Typography>
            <Typography color="secondary">
              just assigned. Put on all your trackers, you can see which are
            </Typography>
            <Typography color="secondary">
              which in the figure to the right.
            </Typography>
          </div>
          <div className="flex">
            <TipBox>
              Not sure which tracker is which? Shake a tracker and it will
              highlight the corresponding item.
            </TipBox>
          </div>
        </div>

        <div className="flex">
          <Button variant="primary" onClick={nextStep}>
            I have all my trackers on
          </Button>
        </div>
      </div>
      <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-16">
        <PersonFrontIcon width={150}></PersonFrontIcon>
      </div>
    </>
  );
}
