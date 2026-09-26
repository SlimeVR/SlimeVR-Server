import { BodyPart } from 'solarxr-protocol';

export const bodypartToString = (id: BodyPart) => BodyPart[id].replace(/_/g, ' ');

type Vector3 = { x: number; y: number; z: number };
export const formatVector3 = ({ x, y, z }: Vector3, precision = 0) =>
  `${x.toFixed(precision)} / ${y.toFixed(precision)} / ${z.toFixed(precision)}`;

/**
 * Convert an ASCII string to a number with it's bytes represented in little endian
 */
export function magic(strings: TemplateStringsArray): number {
  return (
    strings
      // joins strings
      .join('')
      // splits per character
      .split('')
      .reduce((prev, cur, i) => prev + (cur.charCodeAt(0) << (i * 8)), 0)
  );
}
