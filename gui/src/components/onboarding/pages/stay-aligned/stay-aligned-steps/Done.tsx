import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { SkeletonVisualizerWidget } from '@/components/widgets/SkeletonVisualizerWidget';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { EnableStayAlignedRequestT, RpcMessage } from 'solarxr-protocol';

export function DoneStep({
  resetSteps,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  resetSteps: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();

  // Enable Stay Aligned
  useEffect(() => {
    const req = new EnableStayAlignedRequestT();
    sendRPCPacket(RpcMessage.EnableStayAlignedRequest, req);
  }, []);

  return (
    <div className="flex flex-col items-center w-full justify-center gap-5">
      <div className="flex gap-1 flex-col justify-center items-center">
        <Typography variant="section-title">
          {l10n.getString('onboarding-stay_aligned-done-title')}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('onboarding-stay_aligned-done-description')}
        </Typography>
      </div>

      <div className="flex gap-3">
        <Button
          variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
          onClick={resetSteps}
        >
          {l10n.getString('onboarding-stay_aligned-done-restart')}
        </Button>
        <Button variant="primary" to="/settings/trackers">
          {l10n.getString('onboarding-stay_aligned-done-done')}
        </Button>
      </div>

      <SkeletonVisualizerWidget />
    </div>
  );
}
