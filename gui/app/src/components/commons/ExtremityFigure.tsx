import { BodyPart } from 'solarxr-protocol';
import {
  ExtremityFigureSpec,
  ExtremityParts,
  ExtremitySide,
} from '@/utils/extremities';

const CIRCLE_RADIUS = 0.0001;

export function ExtremityFigure({
  spec,
  parts,
  side,
  height = spec.height,
  className,
}: {
  spec: ExtremityFigureSpec;
  parts: ExtremityParts;
  side: ExtremitySide;
  height?: number;
  className?: string;
}) {
  const mirror = side === 'left';
  const x = (cx: number) => (mirror ? spec.width - cx : cx);

  return (
    <svg
      width="100%"
      className={className}
      viewBox={`0 0 ${spec.width} ${height}`}
    >
      <image
        width={spec.width}
        height={spec.height}
        transform={
          mirror ? `translate(${spec.width},0) scale(-1,1)` : undefined
        }
        href={spec.image}
      />
      {Object.entries(parts.digits).flatMap(([digit, joints]) =>
        joints.map((part, joint) => {
          const [cx, cy] = spec.anchors[digit][joint];
          return (
            <circle
              key={part}
              className="body-part-circle"
              cx={x(cx)}
              cy={cy}
              r={CIRCLE_RADIUS}
              id={BodyPart[part]}
            />
          );
        })
      )}
      <circle
        className="body-part-circle"
        cx={x(spec.rootAnchor[0])}
        cy={spec.rootAnchor[1]}
        r={CIRCLE_RADIUS}
        id={BodyPart[parts.root]}
      />
    </svg>
  );
}
