import { Button } from '../../../../commons/Button';
import { FromtOfChairIcon } from '../../../../commons/icon/FrontOfChair';
import { Typography } from '../../../../commons/Typography';

export function PreparationStep({ nextStep }: { nextStep: () => void }) {
  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            Preparation
          </Typography>
          <div>
            <Typography color="secondary">
              Grab a chair and stand in front of it. such that you can
            </Typography>
            <Typography color="secondary">sit down at any moment.</Typography>
          </div>
        </div>

        <div className="flex">
          <Button variant="primary" onClick={nextStep}>
            I’m in front of a chair
          </Button>
        </div>
      </div>
      <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-16">
        <FromtOfChairIcon />
      </div>
    </>
  );
}
