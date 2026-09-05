import { atom, useAtomValue, useSetAtom } from 'jotai';
import { selectAtom } from 'jotai/utils';
import { MouseEvent, PointerEvent, useEffect, useRef, useState } from 'react';

const DRAG_THRESHOLD = 6;

export interface DropTargetCodec<T> {
  attribute: string;
  serialize: (target: T) => string;
  parse: (raw: string) => T | null;
}

export interface DragState<P, T> {
  payload: P;
  x: number;
  y: number;
  target: T | null;
}

/**
 * Builds one self-contained drag system. Targets are compared with `===`, so
 * `T` should be a primitive such as a numeric enum.
 */
export const createPointerDrag = <P, T>(codec: DropTargetCodec<T>) => {
  const stateAtom = atom<DragState<P, T> | null>(null);
  const targetAtom = selectAtom(stateAtom, (state) => state?.target ?? null);
  const activeAtom = selectAtom(stateAtom, (state) => state !== null);

  const selector = `[${codec.attribute}]`;

  const hitTest = (x: number, y: number): T | null => {
    const el = document.elementFromPoint(x, y)?.closest<HTMLElement>(selector);
    const raw = el?.getAttribute(codec.attribute);
    return raw != null ? codec.parse(raw) : null;
  };

  const dropTargetProps = (target: T) => ({
    [codec.attribute]: codec.serialize(target),
  });

  /** A null payload means there is nothing to drag here. */
  const useDraggable = (payload: P | null, onDrop: (target: T | null) => void) => {
    const setDrag = useSetAtom(stateAtom);
    const [start, setStart] = useState<{ x: number; y: number } | null>(null);
    const [isDragging, setIsDragging] = useState(false);
    const suppressNextClickRef = useRef(false);
    const payloadRef = useRef(payload);
    payloadRef.current = payload;

    const reset = () => {
      setStart(null);
      setIsDragging(false);
      setDrag(null);
    };

    const onPointerDown = (e: PointerEvent) => {
      if (e.button !== 0 || !payloadRef.current) return;
      setStart({ x: e.clientX, y: e.clientY });
      e.currentTarget.setPointerCapture(e.pointerId);
    };

    const onPointerMove = (e: PointerEvent) => {
      if (!start) return;

      if (!isDragging) {
        const dx = e.clientX - start.x;
        const dy = e.clientY - start.y;
        if (Math.hypot(dx, dy) < DRAG_THRESHOLD) return;
        setIsDragging(true);
      }

      if (!payloadRef.current) return;

      setDrag({
        payload: payloadRef.current,
        x: e.clientX,
        y: e.clientY,
        target: hitTest(e.clientX, e.clientY),
      });
    };

    const onPointerUp = (e: PointerEvent) => {
      const target = isDragging ? hitTest(e.clientX, e.clientY) : null;
      const wasDragging = isDragging;
      if (wasDragging) suppressNextClickRef.current = true;
      reset();
      if (wasDragging) onDrop(target);
    };

    const onPointerCancel = () => reset();

    const onClick = (e: MouseEvent) => {
      if (suppressNextClickRef.current) {
        suppressNextClickRef.current = false;
        e.preventDefault();
        e.stopPropagation();
      }
    };

    useEffect(() => {
      if (!isDragging) return;

      const onKeyDown = (e: KeyboardEvent) => {
        if (e.key === 'Escape') reset();
      };
      window.addEventListener('keydown', onKeyDown);
      return () => window.removeEventListener('keydown', onKeyDown);
    }, [isDragging]);

    return {
      dragProps: {
        onPointerDown,
        onPointerMove,
        onPointerUp,
        onPointerCancel,
        onClick,
      },
      isDragging,
    };
  };

  const useIsDragHovering = (target: T) => useAtomValue(targetAtom) === target;
  const useIsDragActive = () => useAtomValue(activeAtom);
  const useDragGhost = () => useAtomValue(stateAtom);

  return {
    stateAtom,
    dropTargetProps,
    useDraggable,
    useIsDragHovering,
    useIsDragActive,
    useDragGhost,
  };
};
