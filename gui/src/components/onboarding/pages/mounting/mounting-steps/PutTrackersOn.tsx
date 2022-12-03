import { useTrackers } from '../../../../../hooks/tracker';
import { BodyDisplay } from '../../../../commons/BodyDisplay';
import { Button } from '../../../../commons/Button';
import { TipBox } from '../../../../commons/TipBox';
import { Typography } from '../../../../commons/Typography';

export function PutTrackersOnStep({ nextStep }: { nextStep: () => void }) {
  const { trackers } = useTrackers();

  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            Put on your trackers
          </Typography>
          <div>
            <Typography color="secondary">
              To calibrate mounting rotations, we're gonna use the
            </Typography>
            <Typography color="secondary">
              trackers you just assigned. Put on all your trackers,
            </Typography>
            <Typography color="secondary">
              you can see which are which in the figure to the right.
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
        <BodyDisplay
          trackers={trackers}
          width={150}
          dotsSize={15}
          variant="dots"
        />
      </div>
    </>
  );
}
