import { useAtomValue } from 'jotai';
import { selectAtom } from 'jotai/utils';
import { BodyPart } from '@/utils/body-part';
import { createPointerDrag } from './pointer-drag';

export interface TrackerDragPayload {
  trackerId: number;
  label: string;
}

export const trackerDrag = createPointerDrag<TrackerDragPayload, BodyPart>({
  attribute: 'data-drop-body-part',
  serialize: (part) => part,
  parse: (raw) => {
    const part = (BodyPart as unknown as Record<string, string>)[raw];
    return typeof part === 'string' ? (part as BodyPart) : null;
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
