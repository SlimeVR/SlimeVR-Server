import { useAtomValue } from 'jotai';
import { selectAtom } from 'jotai/utils';
import { BodyPart } from 'solarxr-protocol';
import { createPointerDrag } from './pointer-drag';

export interface TrackerDragPayload {
  trackerId: number;
  label: string;
}

export const trackerDrag = createPointerDrag<TrackerDragPayload, BodyPart>({
  attribute: 'data-drop-body-part',
  serialize: (part) => BodyPart[part],
  parse: (raw) => {
    const part = (BodyPart as unknown as Record<string, number>)[raw];
    return typeof part === 'number' ? part : null;
  },
});

export const bodyPartDropProps = trackerDrag.dropTargetProps;

const draggedTrackerIdAtom = selectAtom(
  trackerDrag.stateAtom,
  (state) => state?.payload.trackerId ?? null
);

export const useIsTrackerBeingDragged = (trackerId: number) =>
  useAtomValue(draggedTrackerIdAtom) === trackerId;

export const hoveredBodyPartAtom = selectAtom(
  trackerDrag.stateAtom,
  (state) => state?.target ?? null
);
