import classNames from 'classnames';
import { Children, createContext, ReactNode, useContext } from 'react';
import {
  BodyInteractions,
  BodyInteractionsProps,
} from '@/components/commons/BodyInteractions';

/** Where a group of cards sits, so its connector knows which way to leave it */
export type ExtremitySlot = {
  direction: 'left' | 'right';
  edge: 'side' | 'cap';
};

/** Whether a digit's parts stack down its card or spread across it */
export type DigitFlow = 'rows' | 'columns';

export type ExtremityLayoutValue = {
  compact: boolean;
  mirrored: boolean;
  digit: (name: string, slot: ExtremitySlot, flow: DigitFlow) => ReactNode;
  figure: (height?: number) => ReactNode;
  interactions: Omit<
    BodyInteractionsProps,
    | 'figure'
    | 'leftControls'
    | 'rightControls'
    | 'topControls'
    | 'bottomControls'
  >;
};

const LayoutContext = createContext<ExtremityLayoutValue | null>(null);
const SlotContext = createContext<ExtremitySlot>({
  direction: 'left',
  edge: 'side',
});

export function useExtremityLayout() {
  const value = useContext(LayoutContext);
  if (!value)
    throw new Error('Extremity layouts only render inside ExtremityAssignment');
  return value;
}

export function ExtremityLayoutProvider({
  value,
  children,
}: {
  value: ExtremityLayoutValue;
  children: ReactNode;
}) {
  return (
    <LayoutContext.Provider value={value}>{children}</LayoutContext.Provider>
  );
}

/** Cards for one digit, or 'root' for the hand or foot itself */
export function Digit({
  name,
  flow = 'rows',
}: {
  name: string;
  flow?: DigitFlow;
}) {
  const { digit } = useExtremityLayout();
  const slot = useContext(SlotContext);

  return <>{digit(name, slot, flow)}</>;
}

/** Digits stacked beside the figure, `className` spaces them along the column */
export function DigitColumn({
  className,
  children,
}: {
  className?: string;
  children: ReactNode;
}) {
  return (
    <div className={classNames('flex flex-col gap-2 h-full', className)}>
      {children}
    </div>
  );
}

/** Digits spread across the figure, written in the order the right side shows */
export function DigitRow({ children }: { children: ReactNode }) {
  const { mirrored } = useExtremityLayout();
  const digits = Children.toArray(children);

  return (
    <div
      className={classNames(
        'gap-1 smol:gap-2 py-1',
        digits.length > 1
          ? 'grid grid-cols-3 [&>*]:w-full'
          : 'flex justify-center'
      )}
    >
      {mirrored ? digits.reverse() : digits}
    </div>
  );
}

function Slot({
  direction,
  edge,
  children,
}: ExtremitySlot & { children: ReactNode }) {
  return (
    <SlotContext.Provider value={{ direction, edge }}>
      <div
        className={classNames(
          edge === 'side' && 'h-full',
          direction === 'right' && 'text-right'
        )}
      >
        {children}
      </div>
    </SlotContext.Provider>
  );
}

export function ExtremityFrame({
  near,
  far,
  top,
  bottom,
  figureHeight,
}: {
  near?: ReactNode;
  far?: ReactNode;
  top?: ReactNode;
  bottom?: ReactNode;
  figureHeight?: number;
}) {
  const { mirrored, interactions, figure } = useExtremityLayout();
  const left = mirrored ? far : near;
  const right = mirrored ? near : far;

  return (
    <BodyInteractions
      {...interactions}
      figure={figure(figureHeight)}
      leftControls={
        left && (
          <Slot direction="right" edge="side">
            {left}
          </Slot>
        )
      }
      rightControls={
        right && (
          <Slot direction="left" edge="side">
            {right}
          </Slot>
        )
      }
      topControls={
        top && (
          <Slot direction="left" edge="cap">
            {top}
          </Slot>
        )
      }
      bottomControls={
        bottom && (
          <Slot direction="left" edge="cap">
            {bottom}
          </Slot>
        )
      }
    />
  );
}
