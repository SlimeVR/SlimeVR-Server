import classNames from 'classnames';
import ReactModal from 'react-modal';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import {
  AssignTrackerRequestT,
  BodyPart,
  QuatT,
  RpcMessage,
  TrackerIdT,
} from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { TrackerPartCard } from '@/components/tracker/TrackerPartCard';
import { TrackerSelectionMenu } from './TrackerSelectionMenu';
import { useLocalization } from '@fluent/react';
import { useAtomValue } from 'jotai';
import {
  assignedTrackersAtom,
  FlatDeviceTracker,
} from '@/store/app-store';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { FootIcon } from '@/components/commons/icon/FootIcon';

export function ToeAssignmentModal({
  isOpen,
  side,
  onClose,
}: {
  isOpen: boolean;
  side: 'left' | 'right';
  onClose: () => void;
}) {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();
  const assignedTrackers = useAtomValue(assignedTrackersAtom);
  const [selectedRole, setSelectedRole] = useState<BodyPart>(BodyPart.NONE);

  // Defined inside component to ensure BodyPart enum is fully resolved
  // (Vite's CJS-to-ESM dep cache may not include newly added enum values)
  const TOE_LABELS: Record<number, string> = useMemo(() => ({
    [BodyPart.LEFT_TOES_ABDUCTOR_HALLUCIS]: 'Left Big Toe',
    [BodyPart.LEFT_TOES_DIGITORUM_BREVIS]: 'Left Middle Toes',
    [BodyPart.LEFT_TOES_ABDUCTOR_DIGITI_MINIMI]: 'Left Pinky Toe',
    [BodyPart.RIGHT_TOES_ABDUCTOR_HALLUCIS]: 'Right Big Toe',
    [BodyPart.RIGHT_TOES_DIGITORUM_BREVIS]: 'Right Middle Toes',
    [BodyPart.RIGHT_TOES_ABDUCTOR_DIGITI_MINIMI]: 'Right Pinky Toe',
  }), []);

  const TOE_PARTS = useMemo(() => ({
    left: [
      {
        part: BodyPart.LEFT_TOES_ABDUCTOR_HALLUCIS,
        cx: 110,
        cy: 30,
      },
      {
        part: BodyPart.LEFT_TOES_DIGITORUM_BREVIS,
        cx: 73,
        cy: 40,
      },
      {
        part: BodyPart.LEFT_TOES_ABDUCTOR_DIGITI_MINIMI,
        cx: 50,
        cy: 62,
      },
    ],
    right: [
      {
        part: BodyPart.RIGHT_TOES_ABDUCTOR_HALLUCIS,
        cx: 77,
        cy: 30,
      },
      {
        part: BodyPart.RIGHT_TOES_DIGITORUM_BREVIS,
        cx: 114,
        cy: 40,
      },
      {
        part: BodyPart.RIGHT_TOES_ABDUCTOR_DIGITI_MINIMI,
        cx: 137,
        cy: 62,
      },
    ],
  }), []);

  const toeParts = TOE_PARTS[side];
  const DOT_SIZE = 18;

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const containerRef = useRef<HTMLDivElement | null>(null);
  const cardsRef = useRef<HTMLDivElement | null>(null);
  const footRef = useRef<HTMLDivElement | null>(null);

  const drawLines = useCallback(() => {
    const canvas = canvasRef.current;
    const container = containerRef.current;
    const cards = cardsRef.current;
    const foot = footRef.current;
    if (!canvas || !container || !cards || !foot) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    canvas.width = container.clientWidth;
    canvas.height = container.clientHeight;
    const containerRect = container.getBoundingClientRect();

    ctx.strokeStyle = '#608AAB';
    ctx.lineWidth = 2;

    toeParts.forEach(({ part, cx, cy }) => {
      const card = cards.querySelector(`[data-part="${part}"]`) as HTMLElement;
      if (!card) return;

      const cardRect = card.getBoundingClientRect();
      const footRect = foot.getBoundingClientRect();

      // Card connection point: right edge, vertical center
      const cardX = cardRect.right - containerRect.left;
      const cardY = cardRect.top + cardRect.height / 2 - containerRect.top;

      // Dot position relative to container
      const dotX = footRect.left - containerRect.left + cx;
      const dotY = footRect.top - containerRect.top + cy;

      ctx.beginPath();
      ctx.moveTo(cardX, cardY);
      ctx.lineTo(cardX + 20, cardY);
      ctx.lineTo(dotX, dotY);
      ctx.stroke();
    });
  }, [toeParts]);

  useEffect(() => {
    // Delay to let modal finish rendering/animating
    const timer = setTimeout(drawLines, 100);
    window.addEventListener('resize', drawLines);
    return () => {
      clearTimeout(timer);
      window.removeEventListener('resize', drawLines);
    };
  }, [drawLines, isOpen]);

  const trackerPartGrouped = useMemo(
    () =>
      assignedTrackers.reduce<{ [key: number]: FlatDeviceTracker[] }>(
        (curr, td) => {
          const key = td.tracker.info?.bodyPart || BodyPart.NONE;
          return {
            ...curr,
            [key]: [...(curr[key] || []), td],
          };
        },
        {}
      ),
    [assignedTrackers]
  );

  const assignedRoles = useMemo(
    () =>
      assignedTrackers.map(
        ({ tracker }) => tracker.info?.bodyPart || BodyPart.NONE
      ),
    [assignedTrackers]
  );

  const onTrackerSelected = (tracker: FlatDeviceTracker | null) => {
    const assign = (
      role: BodyPart,
      rotation: QuatT | null,
      trackerId: TrackerIdT | null
    ) => {
      const assignreq = new AssignTrackerRequestT();
      assignreq.bodyPosition = role;
      assignreq.mountingOrientation = rotation;
      assignreq.trackerId = trackerId;
      assignreq.allowDriftCompensation = false;
      sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
    };

    // Unassign existing trackers from this role
    (trackerPartGrouped[selectedRole] || []).forEach((td) =>
      assign(
        BodyPart.NONE,
        td.tracker.info?.mountingOrientation || null,
        td.tracker.trackerId
      )
    );

    if (!tracker) {
      setSelectedRole(BodyPart.NONE);
      return;
    }
    assign(
      selectedRole,
      tracker.tracker.info?.mountingOrientation || null,
      tracker.tracker.trackerId
    );
    setSelectedRole(BodyPart.NONE);
  };

  const sideLabel = side === 'left' ? 'Left' : 'Right';

  return (
    <>
      <TrackerSelectionMenu
        bodyPart={selectedRole}
        isOpen={selectedRole !== BodyPart.NONE}
        onClose={() => setSelectedRole(BodyPart.NONE)}
        onTrackerSelected={onTrackerSelected}
      />
      <ReactModal
        isOpen={isOpen && selectedRole === BodyPart.NONE}
        shouldCloseOnOverlayClick
        shouldCloseOnEsc
        onRequestClose={onClose}
        overlayClassName={classNames(
          'fixed top-0 right-0 left-0 bottom-0 flex flex-col items-center justify-center w-full h-full bg-background-90 bg-opacity-95 z-20'
        )}
        className={classNames(
          'focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent outline-none z-10 max-w-xl w-full mx-auto'
        )}
      >
        <div className="flex w-full flex-col items-center gap-6 px-4">
          <Typography variant="main-title" bold>
            {sideLabel} Foot — Toe Assignment
          </Typography>
          <Typography color="text-background-30">
            Click a toe position to assign a tracker
          </Typography>

          <div ref={containerRef} className="flex flex-row items-center gap-10 relative">
            <canvas
              ref={canvasRef}
              className="absolute w-full h-full top-0 left-0 z-0 pointer-events-none"
            />
            {/* Toe cards */}
            <div ref={cardsRef} className="flex flex-col gap-4 min-w-[180px] z-10">
              {toeParts.map(({ part }) => (
                <div
                  key={part}
                  data-part={part}
                  className={classNames(
                    'flex flex-col gap-1 hover:bg-background-50 cursor-pointer px-3 py-2 rounded-md transition-colors',
                    assignedRoles.includes(part) && 'border border-status-success border-opacity-50'
                  )}
                  onClick={() => setSelectedRole(part)}
                >
                  <Typography variant="section-title" whitespace="whitespace-nowrap">
                    {TOE_LABELS[part] || BodyPart[part]}
                  </Typography>
                  <Typography color="text-background-30">
                    {trackerPartGrouped[part]?.[0]
                      ? `${trackerPartGrouped[part][0].tracker.info?.customName || trackerPartGrouped[part][0].tracker.info?.displayName}`
                      : l10n.getString('tracker-part_card-unassigned')}
                  </Typography>
                </div>
              ))}
            </div>

            {/* Foot graphic with dots */}
            <div ref={footRef} className="relative flex-shrink-0 fill-background-30 z-10">
              <FootIcon width={200} flipped={side === 'right'} />
              {toeParts.map(({ part, cx, cy }) => (
                <div
                  key={part}
                  className="absolute z-10 cursor-pointer"
                  onClick={() => setSelectedRole(part)}
                  style={{
                    top: cy - DOT_SIZE / 2,
                    left: cx - DOT_SIZE / 2,
                  }}
                >
                  <div
                    className={classNames(
                      'rounded-full outline-background-90 transition-all',
                      'hover:bg-accent-background-40 hover:scale-125',
                      assignedRoles.includes(part)
                        ? 'bg-status-success'
                        : 'bg-background-10'
                    )}
                    style={{
                      width: DOT_SIZE,
                      height: DOT_SIZE,
                      boxShadow: '0px 0px 8px rgba(0,0,0,0.6)',
                    }}
                  />
                </div>
              ))}
            </div>
          </div>

          <div className="flex w-full justify-center pt-4">
            <Button variant="primary" onClick={onClose}>
              Back
            </Button>
          </div>
        </div>
      </ReactModal>
    </>
  );
}
