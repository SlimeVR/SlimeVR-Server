import { ExtremityDescriptor } from '@/utils/extremities';
import {
  BodyPartAssignment,
  ExtremityAssignmentViewProps,
} from './BodyPartAssignment';

export type {
  ExtremityGroupRenderer,
  ExtremityRow,
} from './BodyPartAssignment';

export function ExtremityAssignment({
  descriptor,
  ...props
}: Omit<ExtremityAssignmentViewProps, 'view'> & {
  descriptor: ExtremityDescriptor;
}) {
  return (
    <BodyPartAssignment
      {...props}
      view={{
        kind: 'extremity',
        descriptor,
      }}
    />
  );
}
