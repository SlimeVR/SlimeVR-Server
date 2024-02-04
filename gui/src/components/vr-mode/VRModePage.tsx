import { useEffect } from 'react';
import { useBreakpoint } from '@/hooks/breakpoint';
import { WidgetsComponent } from '@/components/WidgetsComponent';
import { useNavigate } from 'react-router-dom';

export function VRModePage() {
  const nav = useNavigate();
  const { isMobile } = useBreakpoint('mobile');

  useEffect(() => {
    if (!isMobile) nav('/');
  }, [isMobile]);

  return (
    <div className="p-2 flex flex-col gap-2 h-full">
      <WidgetsComponent></WidgetsComponent>
    </div>
  );
}
