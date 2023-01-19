import { BodyPart } from 'solarxr-protocol';

export const bodypartToString = (id: BodyPart) =>
  BodyPart[id].replace(/_/g, ' ');

type Vector3 = { x: number; y: number; z: number };
export const formatVector3 = ({ x, y, z }: Vector3, precision = 0) =>
  `${x.toFixed(precision)} / ${y.toFixed(precision)} / ${z.toFixed(precision)}`;
