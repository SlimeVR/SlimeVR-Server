import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { MainLayout } from '@/components/MainLayout';
import {
  SkeletonPreviewView,
  SkeletonVisualizerWidget,
} from '@/components/widgets/SkeletonVisualizerWidget';
import { QuaternionFromQuatT } from '@/maths/quaternion';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useConfig } from '@/hooks/config';
import { resetChimeSound, restartAndPlay } from '@/sounds/sounds';
import classNames from 'classnames';
import { RefObject, useCallback, useEffect, useRef, useState } from 'react';
import { useLocalization } from '@fluent/react';
import {
  BodyPart,
  ConnectToWebRTCRequestT,
  ConnectToWebRTCResponseT,
  RpcMessage,
  StartVideoTrackerCalibrationRequestT,
  VideoTrackerCalibrationCameraT,
  VideoTrackerCalibrationProgressResponseT,
  VideoTrackerCalibrationStatus,
  WebRTCVideoProvider,
} from 'solarxr-protocol';
import { Matrix4, Quaternion, Vector3 } from 'three';

type ConnectToWebRTCRpcDetail = {
  message: ConnectToWebRTCResponseT;
  txId: number | null;
};

const VC_WEBRTC_LOG = '[video-calibration:webrtc]';
const VC_CALIB_LOG = '[video-calibration:calibration]';

type VideoStreamStatus = 'loading' | 'connecting' | 'ready' | 'error';

function waitForIceGatheringComplete(peerConnection: RTCPeerConnection) {
  console.log(
    VC_WEBRTC_LOG,
    'ICE gathering initial state:',
    peerConnection.iceGatheringState
  );

  if (peerConnection.iceGatheringState === 'complete') {
    console.log(VC_WEBRTC_LOG, 'ICE gathering already complete');
    return Promise.resolve();
  }

  return new Promise<void>((resolve) => {
    const onIceGatheringStateChange = () => {
      console.log(
        VC_WEBRTC_LOG,
        'ICE gathering state:',
        peerConnection.iceGatheringState
      );
      if (peerConnection.iceGatheringState !== 'complete') return;

      peerConnection.removeEventListener(
        'icegatheringstatechange',
        onIceGatheringStateChange
      );
      console.log(VC_WEBRTC_LOG, 'ICE gathering finished');
      resolve();
    };

    peerConnection.addEventListener(
      'icegatheringstatechange',
      onIceGatheringStateChange
    );
  });
}

function asText(value: string | Uint8Array | null | undefined) {
  if (typeof value === 'string') return value;
  if (value instanceof Uint8Array) return new TextDecoder().decode(value);
  return '';
}

function getCalibrationStatusId(
  status: VideoTrackerCalibrationStatus | null,
  startRequested: boolean
) {
  if (status === null) {
    return startRequested
      ? 'video-calibration-status-starting'
      : 'video-calibration-status-idle';
  }

  switch (status) {
    case VideoTrackerCalibrationStatus.CALIBRATE_CAMERA:
      return 'video-calibration-status-calibrate_camera';
    case VideoTrackerCalibrationStatus.CAPTURE_FORWARD_POSE:
      return 'video-calibration-status-capture_forward_pose';
    case VideoTrackerCalibrationStatus.CAPTURE_BENT_OVER_POSE:
      return 'video-calibration-status-capture_bent_over_pose';
    case VideoTrackerCalibrationStatus.CALIBRATE_TRACKERS:
      return 'video-calibration-status-calibrate_trackers';
    case VideoTrackerCalibrationStatus.CALIBRATE_SKELETON_OFFSETS:
      return 'video-calibration-status-calibrate_skeleton_offsets';
    case VideoTrackerCalibrationStatus.DONE:
      return 'video-calibration-status-done';
    default:
      return 'video-calibration-status-idle';
  }
}

function getCalibrationInstructionId(
  status: VideoTrackerCalibrationStatus | null
): string | null {
  if (status == null) return null;

  switch (status) {
    case VideoTrackerCalibrationStatus.CALIBRATE_CAMERA:
      return 'video-calibration-instruction-calibrate_camera';
    case VideoTrackerCalibrationStatus.CAPTURE_FORWARD_POSE:
      return 'video-calibration-instruction-capture_forward_pose';
    case VideoTrackerCalibrationStatus.CAPTURE_BENT_OVER_POSE:
      return 'video-calibration-instruction-capture_bent_over_pose';
    case VideoTrackerCalibrationStatus.CALIBRATE_TRACKERS:
    case VideoTrackerCalibrationStatus.CALIBRATE_SKELETON_OFFSETS:
      return 'video-calibration-instruction-calibrate_trackers';
    default:
      return null;
  }
}

function applyCalibrationCameraToView(
  view: SkeletonPreviewView,
  camera: VideoTrackerCalibrationCameraT
) {
  const near = 0.01;
  const far = 1000;

  const worldToCamera = QuaternionFromQuatT(camera.worldToCamera).normalize();
  const cameraToWorld = worldToCamera.clone().invert();
  const cvCameraToThreeCamera = new Quaternion().setFromAxisAngle(
    new Vector3(1, 0, 0),
    Math.PI
  );
  const cameraRotation = cameraToWorld.clone().multiply(cvCameraToThreeCamera);

  const worldOriginInCamera = camera.worldOriginInCamera;
  const cameraPosition = worldOriginInCamera
    ? new Vector3(
        worldOriginInCamera.x,
        worldOriginInCamera.y,
        worldOriginInCamera.z
      )
        .applyQuaternion(cameraToWorld)
        .multiplyScalar(-1)
    : new Vector3();

  const projectionMatrix = new Matrix4().set(
    (2 * camera.fx) / camera.width,
    0,
    1 - (2 * camera.tx) / camera.width,
    0,
    0,
    (2 * camera.fy) / camera.height,
    (2 * camera.ty) / camera.height - 1,
    0,
    0,
    0,
    -(far + near) / (far - near),
    (-2 * far * near) / (far - near),
    0,
    0,
    -1,
    0
  );

  view.interactive = false;
  view.manualProjectionMatrix = true;
  view.controls.enabled = false;
  view.controls.enableRotate = false;
  view.controls.enablePan = false;
  view.controls.enableZoom = false;
  view.controls.target.copy(cameraPosition);
  view.camera.near = near;
  view.camera.far = far;
  view.camera.zoom = 1;
  view.camera.position.copy(cameraPosition);
  view.camera.quaternion.copy(cameraRotation);
  view.camera.projectionMatrix.copy(projectionMatrix);
  view.camera.projectionMatrixInverse.copy(projectionMatrix).invert();
  view.camera.updateMatrixWorld(true);
}

function TrackerList({ trackers }: { trackers: BodyPart[] }) {
  const { l10n } = useLocalization();

  if (!trackers.length) {
    return <Typography id="video-calibration-none" color="secondary" />;
  }

  return (
    <div className="flex flex-col gap-1">
      {trackers.map((tracker) => (
        <Typography key={tracker}>
          {l10n.getString('body_part-' + BodyPart[tracker])}
        </Typography>
      ))}
    </div>
  );
}

function CameraDetails({
  camera,
}: {
  camera: VideoTrackerCalibrationCameraT | null;
}) {
  if (!camera) {
    return (
      <Typography id="video-calibration-camera-unavailable" color="secondary" />
    );
  }

  return (
    <div className="flex flex-col gap-2">
      <Typography id="video-calibration-camera-available" />
      <div className="flex flex-col gap-1">
        <Typography
          color="secondary"
          id="video-calibration-camera-resolution"
        />
        <Typography>
          {camera.width} x {camera.height}
        </Typography>
      </div>
      <div className="flex flex-col gap-1">
        <Typography
          color="secondary"
          id="video-calibration-camera-focal_length"
        />
        <Typography>
          {camera.fx.toFixed(1)} / {camera.fy.toFixed(1)}
        </Typography>
      </div>
      <div className="flex flex-col gap-1">
        <Typography
          color="secondary"
          id="video-calibration-camera-principal_point"
        />
        <Typography>
          {camera.tx.toFixed(1)} / {camera.ty.toFixed(1)}
        </Typography>
      </div>
    </div>
  );
}

function VideoCalibrationSidebar({
  progress,
  startRequested,
  onStartCalibration,
  showVideo,
  onToggleVideo,
}: {
  progress: VideoTrackerCalibrationProgressResponseT | null;
  startRequested: boolean;
  onStartCalibration: () => void;
  showVideo: boolean;
  onToggleVideo: () => void;
}) {
  const error = asText(progress?.error);

  return (
    <div className="my-2 flex h-[calc(100%-16px)] flex-col gap-4 rounded-lg bg-background-70 p-4">
      <div className="flex flex-col gap-1">
        <Typography
          variant="section-title"
          id="video-calibration-sidebar-title"
        />
        <Typography
          color="secondary"
          id={
            progress == null
              ? 'video-calibration-sidebar-description'
              : 'video-calibration-sidebar-description-active'
          }
        />
      </div>

      <div className="flex flex-col gap-1">
        <Typography color="secondary" id="video-calibration-status-label" />
        <Typography
          id={getCalibrationStatusId(progress?.status ?? null, startRequested)}
        />
      </div>

      <div className="flex flex-col gap-2">
        <Typography color="secondary" id="video-calibration-camera-label" />
        <CameraDetails camera={progress?.camera ?? null} />
      </div>

      <div className="flex flex-col gap-2">
        <Typography color="secondary" id="video-calibration-done-trackers" />
        <TrackerList trackers={progress?.trackersDone ?? []} />
      </div>

      <div className="flex flex-col gap-2">
        <Typography color="secondary" id="video-calibration-pending-trackers" />
        <TrackerList trackers={progress?.trackersPending ?? []} />
      </div>

      {!!error && (
        <div className="flex flex-col gap-1">
          <Typography color="secondary" id="video-calibration-error-label" />
          <Typography color="text-status-critical">{error}</Typography>
        </div>
      )}

      <Button
        variant="primary"
        className="mt-auto w-full"
        onClick={onStartCalibration}
        loading={startRequested}
        id="video-calibration-start"
      />
      <Button variant="secondary" className="w-full" onClick={onToggleVideo}>
        {showVideo ? 'Hide Video' : 'Show Video'}
      </Button>
    </div>
  );
}

function VideoCalibrationContent({
  videoRef,
  skeletonViewRef,
  calibrationCamera,
  calibrationStatus,
  showVideo,
  status,
  errorMessage,
}: {
  videoRef: RefObject<HTMLVideoElement>;
  skeletonViewRef: React.MutableRefObject<SkeletonPreviewView | null>;
  calibrationCamera: VideoTrackerCalibrationCameraT | null;
  calibrationStatus: VideoTrackerCalibrationStatus | null;
  showVideo: boolean;
  status: VideoStreamStatus;
  errorMessage: string;
}) {
  const showSkeleton =
    calibrationStatus != null &&
    calibrationStatus !== VideoTrackerCalibrationStatus.CALIBRATE_CAMERA;

  const instructionId = getCalibrationInstructionId(calibrationStatus);

  return (
    <div className="flex h-full w-full items-center justify-center overflow-hidden p-4">
      <div className="flex h-full w-full items-center justify-center">
        <div className="relative h-full max-h-[1280px] w-auto max-w-[720px] aspect-[720/1280] overflow-hidden rounded-lg bg-black">
          {!!instructionId && (
            <div className="pointer-events-none absolute left-0 top-0 z-20 w-full p-4">
              <div className="mx-auto w-full rounded-md bg-black/60 px-4 py-3 text-center backdrop-blur-sm">
                <Typography
                  id={instructionId}
                  variant="section-title"
                  whitespace="whitespace-pre-line"
                />
              </div>
            </div>
          )}
          <video
            ref={videoRef}
            autoPlay
            playsInline
            muted
            className={classNames(
              // Above dim overlay (z-5) so the camera feed stays visible; skeleton stays on top (z-10).
              // Horizontal mirror on video + skeleton (same transform) so overlay matches the feed.
              'absolute left-0 top-0 z-[6] h-full w-full object-contain transition-opacity [transform:scaleX(-1)]',
              showVideo ? 'opacity-100' : 'opacity-0'
            )}
          />
          {showVideo && (
            <div className="pointer-events-none absolute left-0 top-0 z-[5] h-full w-full bg-black/40" />
          )}
          {showSkeleton && (
            <SkeletonVisualizerWidget
              className="pointer-events-none absolute left-0 top-0 z-10 h-full w-full [filter:drop-shadow(0_0_8px_rgba(255,255,255,0.55))] [transform:scaleX(-1)]"
              showGrid={false}
              stabilizeSkeleton={false}
              anchorToHmdPosition
              onInit={(context) => {
                skeletonViewRef.current =
                  context.addView({
                    left: 0,
                    bottom: 0,
                    width: 1,
                    height: 1,
                    position: new Vector3(3, 2.5, -3),
                    interactive: false,
                    manualProjectionMatrix: false,
                    onHeightChange(v, newHeight) {
                      if (v.manualProjectionMatrix) return;

                      v.controls.target.set(0, newHeight / 2, 0);
                      const scale = Math.max(1, newHeight) / 1.5;
                      v.camera.zoom = 1 / scale;
                      v.controls.update();
                      v.camera.updateProjectionMatrix();
                    },
                  }) ?? null;

                if (skeletonViewRef.current) {
                  skeletonViewRef.current.controls.target.set(0, 1, 0);
                  skeletonViewRef.current.controls.update();
                }

                if (calibrationCamera && skeletonViewRef.current) {
                  applyCalibrationCameraToView(
                    skeletonViewRef.current,
                    calibrationCamera
                  );
                }
              }}
            />
          )}
        </div>
        {status !== 'ready' && (
          <div className="flex max-w-xl flex-col items-center gap-1 text-center">
            {status === 'loading' && (
              <Typography id="video-calibration-loading" />
            )}
            {status === 'connecting' && (
              <Typography id="video-calibration-connecting" />
            )}
            {status === 'error' && (
              <>
                <Typography id="video-calibration-error" />
                {!!errorMessage && (
                  <Typography color="text-status-critical">
                    {errorMessage}
                  </Typography>
                )}
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export function VideoCalibrationPage({ isMobile }: { isMobile?: boolean }) {
  const videoRef = useRef<HTMLVideoElement>(null);
  const skeletonViewRef = useRef<SkeletonPreviewView | null>(null);
  const peerConnectionRef = useRef<RTCPeerConnection | null>(null);
  const remoteMediaStreamRef = useRef<MediaStream | null>(null);
  const attemptRef = useRef(0);
  const webrtcConnectTxRef = useRef(0);
  const webrtcPendingTxIdRef = useRef<number | null>(null);
  const videoPlayRafRef = useRef<number | null>(null);
  const videoFrameLogHandleRef = useRef<number | null>(null);
  const videoFrameLogStreamRef = useRef<MediaStream | null>(null);
  const videoDiagPollRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const videoDiagLastKeyRef = useRef<string | null>(null);
  const videoTrackDebugIdsRef = useRef<Set<string>>(new Set());
  const [status, setStatus] = useState<VideoStreamStatus>('loading');
  const [errorMessage, setErrorMessage] = useState('');
  const [startRequested, setStartRequested] = useState(false);
  const [showVideo, setShowVideo] = useState(true);
  const [progress, setProgress] =
    useState<VideoTrackerCalibrationProgressResponseT | null>(null);
  const { sendRPCPacket, useRPCPacket, isConnected } = useWebsocketAPI();
  const { config } = useConfig();
  const lastProgressStatusRef = useRef<VideoTrackerCalibrationStatus | null>(
    null
  );

  const scheduleVideoPlay = useCallback(() => {
    if (videoPlayRafRef.current != null) {
      cancelAnimationFrame(videoPlayRafRef.current);
    }
    videoPlayRafRef.current = requestAnimationFrame(() => {
      videoPlayRafRef.current = null;
      const el = videoRef.current;
      if (!el?.srcObject) return;
      void el.play().catch((e) => {
        console.warn(VC_WEBRTC_LOG, 'video.play() rejected', e);
      });
    });
  }, []);

  const stopVideoFrameLogging = useCallback(() => {
    const el = videoRef.current;
    const handle = videoFrameLogHandleRef.current;
    if (
      el != null &&
      handle != null &&
      typeof el.cancelVideoFrameCallback === 'function'
    ) {
      try {
        el.cancelVideoFrameCallback(handle);
      } catch {
        /* ignore */
      }
    }
    videoFrameLogHandleRef.current = null;
    videoFrameLogStreamRef.current = null;
    if (videoDiagPollRef.current != null) {
      clearInterval(videoDiagPollRef.current);
      videoDiagPollRef.current = null;
    }
    videoDiagLastKeyRef.current = null;
  }, []);

  const ensureVideoFrameLogging = useCallback(() => {
    const el = videoRef.current;
    if (!el) return;

    const src = el.srcObject;
    if (
      !(src instanceof MediaStream) ||
      videoFrameLogHandleRef.current != null
    ) {
      return;
    }

    if (typeof el.requestVideoFrameCallback !== 'function') {
      el.addEventListener(
        'loadeddata',
        () => {
          console.log(
            VC_WEBRTC_LOG,
            'video decoded data ready (first frame path; no requestVideoFrameCallback)'
          );
        },
        { once: true }
      );
      return;
    }

    videoFrameLogStreamRef.current = src;

    // requestVideoFrameCallback only runs when the compositor presents a NEW frame — not on a timer.
    // A single 2×2 "frame" usually means one placeholder decode (often track.muted / waiting for keyframe).
    if (import.meta.env.DEV && videoDiagPollRef.current == null) {
      videoDiagLastKeyRef.current = null;
      videoDiagPollRef.current = setInterval(() => {
        const v = videoRef.current;
        const expected = videoFrameLogStreamRef.current;
        if (!v?.srcObject || v.srcObject !== expected) {
          if (videoDiagPollRef.current != null) {
            clearInterval(videoDiagPollRef.current);
            videoDiagPollRef.current = null;
          }
          return;
        }
        const vt = v.srcObject.getVideoTracks()[0];
        const settings = vt?.getSettings?.();
        const key = [
          v.videoWidth,
          v.videoHeight,
          v.readyState,
          v.paused,
          vt?.muted,
          vt?.readyState,
          settings?.width ?? '',
          settings?.height ?? '',
          settings?.frameRate ?? '',
        ].join('|');

        if (key === videoDiagLastKeyRef.current) return;
        videoDiagLastKeyRef.current = key;
      }, 2000);
    }

    const onFrame: VideoFrameRequestCallback = (_now, _metadata) => {
      const v = videoRef.current;
      const expectedStream = videoFrameLogStreamRef.current;

      if (v?.srcObject === expectedStream && expectedStream) {
        videoFrameLogHandleRef.current = v.requestVideoFrameCallback(onFrame);
      } else {
        videoFrameLogHandleRef.current = null;
      }
    };

    videoFrameLogHandleRef.current = el.requestVideoFrameCallback(onFrame);
  }, []);

  const syncRemoteVideoFromPeer = useCallback(() => {
    const peerConnection = peerConnectionRef.current;
    const remoteStream = remoteMediaStreamRef.current;
    if (!peerConnection || !remoteStream) {
      console.log(
        VC_WEBRTC_LOG,
        'syncRemoteVideoFromPeer: skip (no peer or stream)',
        {
          hasPeer: !!peerConnection,
          hasStream: !!remoteStream,
        }
      );
      return;
    }

    const receivers = peerConnection.getReceivers();
    let added = 0;
    for (const receiver of receivers) {
      const track = receiver.track;
      if (!track || track.kind !== 'video') continue;
      if (!remoteStream.getTracks().some(({ id }) => id === track.id)) {
        remoteStream.addTrack(track);
        added += 1;
      }
    }

    const videoTracks = remoteStream.getVideoTracks();
    console.log(VC_WEBRTC_LOG, 'syncRemoteVideoFromPeer', {
      receiverCount: receivers.length,
      videoReceiversWithTrack: receivers.filter(
        (r) => r.track?.kind === 'video'
      ).length,
      tracksAddedThisCall: added,
      streamVideoTrackCount: videoTracks.length,
      signalingState: peerConnection.signalingState,
      connectionState: peerConnection.connectionState,
      iceConnectionState: peerConnection.iceConnectionState,
    });

    if (videoTracks.length > 0 && videoRef.current) {
      const el = videoRef.current;
      // Re-assigning the same stream still counts as a "new load" and aborts an in-flight play().
      if (el.srcObject !== remoteStream) {
        stopVideoFrameLogging();
        el.srcObject = remoteStream;
      }
      scheduleVideoPlay();
      ensureVideoFrameLogging();

      for (const track of videoTracks) {
        if (videoTrackDebugIdsRef.current.has(track.id)) continue;
        videoTrackDebugIdsRef.current.add(track.id);
        console.log(VC_WEBRTC_LOG, 'MediaStreamTrack (video)', {
          id: track.id,
          label: track.label,
          muted: track.muted,
          enabled: track.enabled,
          readyState: track.readyState,
          settings: track.getSettings(),
        });
        track.addEventListener('unmute', () => {
          console.log(VC_WEBRTC_LOG, 'video track unmute', {
            id: track.id,
            settings: track.getSettings(),
          });
        });
        track.addEventListener('mute', () => {
          console.log(VC_WEBRTC_LOG, 'video track mute', { id: track.id });
        });
      }

      setStatus('ready');
      // console.log(VC_WEBRTC_LOG, 'Video element bound; UI status -> ready');
    }
  }, [ensureVideoFrameLogging, scheduleVideoPlay, stopVideoFrameLogging]);

  const cleanupConnection = useCallback(() => {
    console.log(VC_WEBRTC_LOG, 'cleanupConnection');
    if (videoPlayRafRef.current != null) {
      cancelAnimationFrame(videoPlayRafRef.current);
      videoPlayRafRef.current = null;
    }
    stopVideoFrameLogging();
    webrtcPendingTxIdRef.current = null;
    remoteMediaStreamRef.current = null;

    const peerConnection = peerConnectionRef.current;
    if (peerConnection) {
      peerConnection.ontrack = null;
      peerConnection.onconnectionstatechange = null;
      peerConnection.onicegatheringstatechange = null;
      peerConnection.oniceconnectionstatechange = null;
      peerConnection.onsignalingstatechange = null;
      peerConnection
        .getReceivers()
        .forEach((receiver) => receiver.track?.stop());
      peerConnection.close();
      peerConnectionRef.current = null;
    }

    const currentStream = videoRef.current?.srcObject;
    if (currentStream instanceof MediaStream) {
      currentStream.getTracks().forEach((track) => track.stop());
    }

    if (videoRef.current) {
      videoRef.current.srcObject = null;
    }

    videoTrackDebugIdsRef.current.clear();
  }, [stopVideoFrameLogging]);

  const connectVideoViaWebRTC = useCallback(async () => {
    const attempt = ++attemptRef.current;
    console.log(VC_WEBRTC_LOG, 'connectVideoViaWebRTC start', { attempt });
    const peerConnection = new RTCPeerConnection();
    const remoteStream = new MediaStream();

    cleanupConnection();
    peerConnectionRef.current = peerConnection;
    remoteMediaStreamRef.current = remoteStream;
    setStatus('connecting');
    setErrorMessage('');

    peerConnection.onicegatheringstatechange = () => {
      console.log(
        VC_WEBRTC_LOG,
        'peer iceGatheringState:',
        peerConnection.iceGatheringState
      );
    };
    peerConnection.oniceconnectionstatechange = () => {
      console.log(
        VC_WEBRTC_LOG,
        'peer iceConnectionState:',
        peerConnection.iceConnectionState
      );
    };
    peerConnection.onsignalingstatechange = () => {
      console.log(
        VC_WEBRTC_LOG,
        'peer signalingState:',
        peerConnection.signalingState
      );
    };

    peerConnection.ontrack = (event) => {
      const incomingTracks = event.streams[0]?.getTracks() ?? [event.track];
      console.log(VC_WEBRTC_LOG, 'ontrack', {
        streams: event.streams.length,
        trackCount: incomingTracks.length,
        kinds: incomingTracks.map((t) => t.kind),
      });

      incomingTracks.forEach((track) => {
        if (track.kind !== 'video') return;
        if (!remoteStream.getTracks().some(({ id }) => id === track.id)) {
          remoteStream.addTrack(track);
        }
      });

      syncRemoteVideoFromPeer();
    };

    peerConnection.onconnectionstatechange = () => {
      console.log(
        VC_WEBRTC_LOG,
        'peer connectionState:',
        peerConnection.connectionState
      );
      if (peerConnection.connectionState === 'connected') {
        syncRemoteVideoFromPeer();
      }
    };

    try {
      peerConnection.addTransceiver('video', { direction: 'recvonly' });
      console.log(VC_WEBRTC_LOG, 'recvonly video transceiver added');

      const offer = await peerConnection.createOffer();
      console.log(VC_WEBRTC_LOG, 'createOffer done', {
        type: offer.type,
        sdpChars: offer.sdp?.length ?? 0,
      });
      await peerConnection.setLocalDescription(offer);
      console.log(VC_WEBRTC_LOG, 'setLocalDescription done');
      await waitForIceGatheringComplete(peerConnection);

      const localSdp = peerConnection.localDescription?.sdp;
      if (!localSdp) {
        throw new Error('Peer connection did not produce a local SDP offer');
      }

      if (attempt !== attemptRef.current) {
        console.log(
          VC_WEBRTC_LOG,
          'aborted before send (stale attempt)',
          attempt,
          'current',
          attemptRef.current
        );
        return;
      }

      const txId = ++webrtcConnectTxRef.current >>> 0;
      webrtcPendingTxIdRef.current = txId;

      console.log(VC_WEBRTC_LOG, 'sending ConnectToWebRTCRequest', {
        txId,
        offerSdpChars: localSdp.length,
        provider: 'VIDEO_CALIBRATION',
      });
      sendRPCPacket(
        RpcMessage.ConnectToWebRTCRequest,
        new ConnectToWebRTCRequestT(
          WebRTCVideoProvider.VIDEO_CALIBRATION,
          localSdp
        ),
        txId
      );
      console.log(
        VC_WEBRTC_LOG,
        'ConnectToWebRTCRequest dispatched; awaiting ConnectToWebRTCResponse'
      );
    } catch (error) {
      if (attempt !== attemptRef.current) {
        console.log(
          VC_WEBRTC_LOG,
          'connect error ignored (stale attempt)',
          attempt
        );
        return;
      }

      console.error(VC_WEBRTC_LOG, 'connectVideoViaWebRTC failed', error);
      cleanupConnection();
      setStatus('error');
      setErrorMessage(
        error instanceof Error ? error.message : 'Unknown WebRTC error'
      );
    }
  }, [cleanupConnection, sendRPCPacket, syncRemoteVideoFromPeer]);

  const connectVideoViaWebRTCRef = useRef(connectVideoViaWebRTC);
  connectVideoViaWebRTCRef.current = connectVideoViaWebRTC;

  const onConnectToWebRTCResponse = useCallback(
    (detail: ConnectToWebRTCRpcDetail) => {
      void (async () => {
        const response = detail.message;

        console.log(VC_WEBRTC_LOG, 'ConnectToWebRTCResponse received', {
          responseTxId: detail.txId,
          pendingTxId: webrtcPendingTxIdRef.current,
        });

        if (
          detail.txId != null &&
          detail.txId !== webrtcPendingTxIdRef.current
        ) {
          console.log(
            VC_WEBRTC_LOG,
            'ConnectToWebRTCResponse ignored (txId mismatch)'
          );
          return;
        }

        const err = asText(response.error);
        const answerSdp = asText(response.answerSdp);

        console.log(VC_WEBRTC_LOG, 'ConnectToWebRTCResponse payload', {
          hasError: !!err,
          errorPreview: err ? err.slice(0, 120) : '',
          answerSdpChars: answerSdp.length,
        });

        if (err) {
          console.error(VC_WEBRTC_LOG, 'server error on WebRTC connect', err);
          cleanupConnection();
          setStatus('error');
          setErrorMessage(err);
          return;
        }

        if (!answerSdp) {
          console.error(VC_WEBRTC_LOG, 'no answer SDP in response');
          cleanupConnection();
          setStatus('error');
          setErrorMessage('Server did not return an SDP answer');
          return;
        }

        const peerConnection = peerConnectionRef.current;
        if (!peerConnection) {
          console.warn(
            VC_WEBRTC_LOG,
            'no peerConnection when applying answer (already cleaned up?)'
          );
          return;
        }

        try {
          console.log(VC_WEBRTC_LOG, 'setRemoteDescription(answer) …', {
            sdpChars: answerSdp.length,
          });
          await peerConnection.setRemoteDescription({
            type: 'answer',
            sdp: answerSdp,
          });
          console.log(VC_WEBRTC_LOG, 'setRemoteDescription(answer) OK');
          webrtcPendingTxIdRef.current = null;
          syncRemoteVideoFromPeer();
        } catch (error) {
          console.error(
            VC_WEBRTC_LOG,
            'setRemoteDescription(answer) failed',
            error
          );
          cleanupConnection();
          setStatus('error');
          setErrorMessage(
            error instanceof Error
              ? error.message
              : 'Failed to apply SDP answer'
          );
        }
      })();
    },
    [cleanupConnection, syncRemoteVideoFromPeer]
  );

  useRPCPacket(RpcMessage.ConnectToWebRTCResponse, onConnectToWebRTCResponse);

  const onVideoTrackerCalibrationProgress = useCallback(
    (response: VideoTrackerCalibrationProgressResponseT) => {
      console.log(VC_CALIB_LOG, 'VideoTrackerCalibrationProgressResponse', {
        status: response.status,
        error: asText(response.error)?.slice(0, 200) ?? '',
        camera: response.camera
          ? `${response.camera.width}x${response.camera.height}`
          : null,
        trackersDone: response.trackersDone?.length ?? 0,
        trackersPending: response.trackersPending?.length ?? 0,
      });
      setStartRequested(false);
      // New object so React state updates even if the wire protocol reuses instances,
      // and so useRPCPacket's listener stays stable (see useCallback) instead of
      // resubscribing every render (which can drop messages between remove/add).
      setProgress(
        new VideoTrackerCalibrationProgressResponseT(
          response.status,
          response.camera,
          response.trackersDone != null ? [...response.trackersDone] : [],
          response.trackersPending != null ? [...response.trackersPending] : [],
          response.error
        )
      );
    },
    []
  );

  useRPCPacket(
    RpcMessage.VideoTrackerCalibrationProgressResponse,
    onVideoTrackerCalibrationProgress
  );

  useEffect(() => {
    const next = progress?.status ?? null;
    if (next == null) return;

    const prev = lastProgressStatusRef.current;
    if (prev === next) return;

    lastProgressStatusRef.current = next;
    if (!config?.feedbackSound) return;
    restartAndPlay(resetChimeSound, config.feedbackSoundVolume ?? 1);
  }, [progress?.status, config?.feedbackSound, config?.feedbackSoundVolume]);

  const startCalibration = useCallback(() => {
    console.log(VC_CALIB_LOG, 'StartVideoTrackerCalibrationRequest');
    setStartRequested(true);
    setProgress(null);
    sendRPCPacket(
      RpcMessage.StartVideoTrackerCalibrationRequest,
      new StartVideoTrackerCalibrationRequestT()
    );
  }, [sendRPCPacket]);

  // Only re-run when the socket connects/disconnects — not when connectVideoViaWebRTC’s
  // identity changes (parent re-renders would otherwise cleanup the peer connection mid-session).
  useEffect(() => {
    if (!isConnected) {
      console.log(VC_WEBRTC_LOG, 'WebSocket not connected; skip WebRTC start');
      return;
    }

    console.log(
      VC_WEBRTC_LOG,
      'WebSocket connected; starting WebRTC handshake'
    );
    void connectVideoViaWebRTCRef.current();

    return () => {
      console.log(
        VC_WEBRTC_LOG,
        'effect cleanup (WebSocket disconnected or page unmount)'
      );
      attemptRef.current += 1;
      skeletonViewRef.current = null;
      cleanupConnection();
    };
  }, [isConnected, cleanupConnection]);

  useEffect(() => {
    if (!progress?.camera || !skeletonViewRef.current) return;

    applyCalibrationCameraToView(skeletonViewRef.current, progress.camera);
  }, [progress?.camera]);

  return (
    <MainLayout
      isMobile={isMobile}
      full
      showToolbar={false}
      scrollContent={false}
      rightSidebar={
        <VideoCalibrationSidebar
          progress={progress}
          startRequested={startRequested}
          onStartCalibration={startCalibration}
          showVideo={showVideo}
          onToggleVideo={() => setShowVideo((value) => !value)}
        />
      }
    >
      <VideoCalibrationContent
        videoRef={videoRef}
        skeletonViewRef={skeletonViewRef}
        calibrationCamera={progress?.camera ?? null}
        calibrationStatus={progress?.status ?? null}
        showVideo={showVideo}
        status={status}
        errorMessage={errorMessage}
      />
    </MainLayout>
  );
}
