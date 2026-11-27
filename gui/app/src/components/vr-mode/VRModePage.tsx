import { useEffect } from 'react';
import { useBreakpoint } from '@/hooks/breakpoint';
import { NavLink, useNavigate } from 'react-router-dom';
import { SkeletonVisualizerWidget } from '@/components/widgets/SkeletonVisualizerWidget';
import { Checklist } from '@/components/commons/icon/ChecklistIcon';
import { PreviewControls } from '@/components/Sidebar';
import { Vector3 } from 'three';

export function VRModePage() {
  const nav = useNavigate();
  const { isMobile } = useBreakpoint('mobile');

  useEffect(() => {
    if (!isMobile) nav('/');
  }, [isMobile]);

  return (
    <div className="flex flex-col gap-2 h-full rounded-t-lg relative">
      <SkeletonVisualizerWidget
        onInit={(context) => {
          context.addView({
            left: 0,
            bottom: 0,
            width: 1,
            height: 1,
            position: new Vector3(3, 2.5, -3),
            onHeightChange(v, newHeight) {
              v.controls.target.set(0, newHeight / 2.4, 0.1);
              const scale = Math.max(1, newHeight) / 1;
              v.camera.zoom = 1 / scale;
            },
          });
        }}
      />
      <NavLink
        to="/checklist"
        className="xs:hidden absolute z-50 h-12 w-12 rounded-full bg-accent-background-30 bottom-3 right-3 flex justify-center items-center fill-background-10"
      >
        <Checklist />
      </NavLink>
      <PreviewControls open />
    </div>
  );
}
