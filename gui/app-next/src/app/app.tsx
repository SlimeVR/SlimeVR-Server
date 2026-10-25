import { DesignPage } from '@/pages/design/design-page';
import { DialogHost } from '@/ui/dialog';
import { MotionRoot } from '@/ui/motion';
import { ToastHost } from '@/ui/toast';

export function App() {
  return (
    <MotionRoot>
      <DesignPage />
      <DialogHost />
      <ToastHost />
    </MotionRoot>
  );
}
