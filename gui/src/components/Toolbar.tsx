import { ResetType } from 'solarxr-protocol';
import { ResetButton } from './home/ResetButton';

export function Toolbar() {
  return (
    <div className="flex p-2 gap-2 bg-background-70 rounded-md mr-2 my-2">
      <ResetButton type={ResetType.Yaw} variant="small"></ResetButton>
      <ResetButton type={ResetType.Full} variant="small"></ResetButton>
      <ResetButton type={ResetType.Mounting} variant="small"></ResetButton>
      {/* <ClearMountingButton></ClearMountingButton> */}
    </div>
  );
}
