import {
  createContext,
  type JSX,
  mergeProps,
  onCleanup,
  splitProps,
  useContext,
} from 'solid-js';
import {
  attachContainer,
  createContainer,
  type MotionContainer,
  type MotionEntry,
  type MotionLayout,
  type MotionOrientation,
} from './core';

export const MotionParent = createContext<MotionContainer | null>(null);

export interface MotionContainerProps extends JSX.HTMLAttributes<HTMLDivElement> {
  entry?: MotionEntry;
}

type ContainerElementProps = MotionContainerProps & {
  orientation?: MotionOrientation;
  trap?: boolean;
};

function renderContainer(layout: MotionLayout, props: ContainerElementProps) {
  const [local, rest] = splitProps(props, [
    'orientation',
    'trap',
    'entry',
    'children',
  ]);
  const parent = useContext(MotionParent);
  const container = createContainer(layout, parent, {
    orientation: () => local.orientation ?? 'vertical',
    entry: () => local.entry ?? 'last',
    trap: () => local.trap ?? false,
  });
  if (parent) onCleanup(attachContainer(container));

  return (
    <div {...rest} ref={(el) => (container.el = el)}>
      <MotionParent.Provider value={parent ? container : null}>
        {local.children}
      </MotionParent.Provider>
    </div>
  );
}

// Children in DOM order along one axis, bumps at the ends
export function MotionList(
  props: MotionContainerProps & { orientation?: MotionOrientation }
) {
  return renderContainer('list', props);
}

export function MotionToolbar(props: MotionContainerProps) {
  return renderContainer(
    'list',
    mergeProps({ role: 'toolbar' as const }, props, {
      orientation: 'horizontal' as const,
    })
  );
}

// Moves to the aligned child in the next row or column, bumps at the edges
export function MotionGrid(props: MotionContainerProps) {
  return renderContainer('grid', props);
}

// A screen area (rail, page, dialog). Its children form a list, moves out of it
// pick the nearest sibling region by geometry. `trap` keeps moves inside.
export function MotionRegion(
  props: MotionContainerProps & {
    orientation?: MotionOrientation;
    trap?: boolean;
  }
) {
  return renderContainer('region', props);
}
