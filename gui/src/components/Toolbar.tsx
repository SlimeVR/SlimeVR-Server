import { ResetType } from 'solarxr-protocol';
import { ResetButton } from './home/ResetButton';
import { ClearMountingButton } from './ClearMountingButton';

export function Toolbar() {
  return (
    <div className="flex p-2 gap-2 bg-background-70 rounded-md mr-2 my-2">
      <ResetButton type={ResetType.Yaw} size="small"></ResetButton>
      <ResetButton type={ResetType.Full} size="small"></ResetButton>
      <ResetButton type={ResetType.Mounting} size="small"></ResetButton>
      <ClearMountingButton></ClearMountingButton>
    </div>
  );
}
