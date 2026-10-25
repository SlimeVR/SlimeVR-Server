import { createEffect, type JSX, onCleanup, onMount } from 'solid-js';
import * as v from 'valibot';
import { definePref } from '@/lib/storage';
import { Floating } from '@/ui/floating';
import { MotionParent } from './containers';
import {
  createContainer,
  initMotion,
  motionActive,
  padActivate,
  padBack,
  padConnected,
  padMove,
  setMotionRoot,
} from './core';
import { startGamepad } from './gamepad';
import { MotionRing } from './motion-ring';

export const controllerMotionPref = definePref(
  'controllerNav',
  v.boolean(),
  true
);

export function MotionRoot(props: { children: JSX.Element }) {
  const root = createContainer('root', null);
  setMotionRoot(root);
  onCleanup(() => setMotionRoot(null));

  onMount(() => onCleanup(initMotion()));

  createEffect(() => {
    if (!controllerMotionPref()) return;
    onCleanup(
      startGamepad({
        move: padMove,
        activate: padActivate,
        back: padBack,
        connected: padConnected,
      })
    );
  });

  createEffect(() =>
    document.documentElement.classList.toggle('motion-active', motionActive())
  );

  return (
    <MotionParent.Provider value={root}>
      {props.children}
      <Floating>
        <MotionRing />
      </Floating>
    </MotionParent.Provider>
  );
}
