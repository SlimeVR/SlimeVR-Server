import { useEffect } from 'react';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useNavigate } from 'react-router-dom';
import { SkeletonVisualizerWidget } from '@/components/widgets/SkeletonVisualizerWidget';

export function VRModePage() {
  const nav = useNavigate();
  const { isMobile } = useBreakpoint('mobile');

  useEffect(() => {
    if (!isMobile) nav('/');
  }, [isMobile]);

  return (
    <div className="flex flex-col gap-2 h-full rounded-t-lg">
      <SkeletonVisualizerWidget></SkeletonVisualizerWidget>
    </div>
  );
}
