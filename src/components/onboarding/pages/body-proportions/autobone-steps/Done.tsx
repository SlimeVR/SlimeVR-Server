import { Typography } from '../../../../commons/Typography';

export function DoneStep() {
  return (
    <div className="flex flex-col items-center w-full justify-center gap-5">
      <div className="flex gap-1 flex-col justify-center items-center">
        <Typography variant="section-title">
          Body measured and saved!
        </Typography>
        <Typography color="secondary">
          Your body proportions calibration is complete!
        </Typography>
      </div>
      {/* <Button variant="primary">Continue to next step</Button> */}
    </div>
  );
}
