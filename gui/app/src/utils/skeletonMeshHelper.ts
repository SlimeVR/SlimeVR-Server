import {
  Box3,
  BoxGeometry,
  Color,
  Mesh,
  MeshStandardMaterial,
  Object3D,
  Quaternion,
  Raycaster,
  Vector3,
} from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import {
  getTrackerMarkerScale,
  SkeletonRenderPart,
  TrackerPreviewData,
} from './skeletonHelper';
import { BodyPart, BoneT } from 'solarxr-protocol';
import { QuaternionFromQuatT } from '@/maths/quaternion';
import { Vector3FromVec3fT } from '@/maths/vector3';
import {
  BoneShapeConfig,
  ModelDimensions,
  SKELETON_PART_PRESETS,
  computeShapeScale,
  getPartMaterial,
} from './skeletonParts';
import { SkeletonProportions, deriveSkeletonProportions } from './skeletonProportions';

const position = new Vector3();
const shapePos = new Vector3();
const quat = new Quaternion();
const localOffset = new Vector3();

const modelBox = new Box3();
const mountingNormal = new Vector3();
const rayOrigin = new Vector3();
const rayDirection = new Vector3();
const surfaceRaycaster = new Raycaster();

// Shared loader + cache: each model URL is fetched and parsed once, then cloned
// per instance. Same pattern as the tracker preview (IMUVisualizerWidget).
const gltfLoader = new GLTFLoader();
const modelCache = new Map<string, Promise<Object3D | null>>();
function loadModel(url: string): Promise<Object3D | null> {
  let p = modelCache.get(url);
  if (!p) {
    p = gltfLoader
      .loadAsync(url)
      .then((gltf) => gltf.scene)
      .catch((err) => {
        console.error('MODEL LOAD FAIL', url, err);
        return null;
      }); // nothing to draw for this bone yet
    modelCache.set(url, p);
  }
  return p;
}

interface AttachedShape {
  config: BoneShapeConfig;
  node: Object3D;
  model: ModelDimensions | null;
}

interface BonePart extends SkeletonRenderPart {
  shapes: AttachedShape[];
  marker?: Mesh;
  surfaceDistance: number;
  surfaceDirty: boolean;
}

/**
 * Renders a skeleton with modular 3D models or primitives attached to each bone.
 * Every bone holds a list of rigid shapes (e.g. spine column, chestplate armor, etc.)
 * that transform and scale dynamically with bone kinematics and user proportions.
 */
export class BasedSkeletonMeshHelper extends Object3D {
  readonly type = 'SkeletonMeshHelper';
  private parts: BonePart[] = [];
  private proportions: SkeletonProportions = deriveSkeletonProportions(new Map());
  private disposed = false;
  private trackerMarkerGeometry = new BoxGeometry(0.03, 0.03, 0.02);
  private trackerMarkerMaterial = new MeshStandardMaterial({
    color: 0x49e5ff,
    emissive: 0x0c5966,
    emissiveIntensity: 0.8,
    roughness: 0.45,
  });

  constructor(bones: Map<BodyPart, BoneT>) {
    super();

    const m0 = getPartMaterial(new Color('#cccccc'));
    const m1 = getPartMaterial(new Color('#ad0ccc'));

    for (const bone of bones.values()) {
      if (bone.bodyPart === BodyPart.NONE) continue;
      const config = SKELETON_PART_PRESETS[bone.bodyPart];
      if (!config.visible) continue;

      const part: BonePart = {
        bone,
        shapes: [],
        surfaceDistance: 0,
        surfaceDirty: true,
      };
      for (const shapeConfig of config.shapes) {
        const node = new Object3D();
        node.matrixAutoUpdate = false;
        this.add(node);

        const attached: AttachedShape = {
          config: shapeConfig,
          node,
          model: null,
        };

        if (shapeConfig.modelUrl) {
          loadModel(shapeConfig.modelUrl).then((scene) => {
            if (!scene || this.disposed) return;
            const model = scene.clone(true);
            model.traverse((o) => {
              if (o !== model) {
                // The exporter parks the model at its bone's rest pose; the
                // part config says how it sits on the bone instead.
                o.position.set(0, 0, 0);
                o.quaternion.identity();
                o.scale.set(1, 1, 1);
              }
              if (o instanceof Mesh) {
                if (o.material !== undefined && o.material.color.r < 0.7) {
                  o.material = m1;
                } else {
                  o.material = m0;
                }

                o.frustumCulled = false;
              }
            });
            modelBox.setFromObject(model);
            // The export writes each model in its bone's frame, running down
            // -Y off the bone's head, so it needs no orienting here.
            attached.model = {
              width: modelBox.max.x - modelBox.min.x,
              depth: modelBox.max.z - modelBox.min.z,
              length: Math.abs(modelBox.min.y),
            };
            node.clear();
            node.add(model);
            part.surfaceDirty = true;
          });
        }

        part.shapes.push(attached);
      }

      this.parts.push(part);
    }
  }

  setProportions(proportions: SkeletonProportions) {
    if (
      proportions.bodyScale !== this.proportions.bodyScale ||
      proportions.shoulderWidth !== this.proportions.shoulderWidth ||
      proportions.hipWidth !== this.proportions.hipWidth
    ) {
      for (const part of this.parts) part.surfaceDirty = true;
    }
    this.proportions = proportions;
  }

  setBones(bones: Map<BodyPart, BoneT>) {
    for (const part of this.parts) {
      const bone = bones.get(part.bone.bodyPart);
      if (!bone) continue;
      if (bone.boneLength !== part.bone.boneLength) part.surfaceDirty = true;
      part.bone = bone;
    }
  }

  setTrackers(trackers: Map<BodyPart, TrackerPreviewData>) {
    for (const part of this.parts) {
      const tracker = trackers.get(part.bone.bodyPart);
      if (
        tracker?.trackerId !== part.tracker?.trackerId ||
        tracker?.boneOffset !== part.tracker?.boneOffset ||
        (tracker &&
          part.tracker &&
          tracker.mountingOrientation.angleTo(part.tracker.mountingOrientation) > 1e-6)
      ) {
        part.surfaceDirty = true;
      }
      part.tracker = tracker;
      if (!part.tracker && part.marker) part.marker.visible = false;
    }
  }

  updateMatrixWorld(force: boolean) {
    for (const part of this.parts) {
      const { bone, shapes } = part;

      position.copy(Vector3FromVec3fT(bone.headPosition));
      quat.copy(QuaternionFromQuatT(bone.orientation)).normalize();
      const boneLength = Math.max(bone.boneLength, 1e-4);

      for (const attached of shapes) {
        const { config, node, model } = attached;
        const size = computeShapeScale(
          config,
          this.proportions,
          boneLength,
          model ?? undefined
        );

        shapePos.copy(position);
        if (config.offset && model) {
          localOffset
            .copy(config.offset({ model, proportions: this.proportions, boneLength }))
            .applyQuaternion(quat);
          shapePos.add(localOffset);
        }

        node.position.copy(shapePos);
        node.quaternion.copy(quat);
        if (config.rotation) node.quaternion.multiply(config.rotation);
        node.scale.set(size.width, size.length, size.depth);
        node.updateMatrix();
      }

      if (part.tracker) {
        if (!part.marker) {
          part.marker = new Mesh(
            this.trackerMarkerGeometry,
            this.trackerMarkerMaterial
          );
          this.add(part.marker);
        }

        part.marker.visible = true;
        part.marker.scale.setScalar(getTrackerMarkerScale(bone.bodyPart));
        part.marker.quaternion.copy(quat).multiply(part.tracker.mountingOrientation);
        part.marker.position.copy(position);
        localOffset
          .set(0, -boneLength * part.tracker.boneOffset, 0)
          .applyQuaternion(quat);
        part.marker.position.add(localOffset);
        mountingNormal.set(0, 0, 1).applyQuaternion(part.marker.quaternion);
        if (part.surfaceDirty) {
          // The mesh and marker move rigidly with the bone, so the surface
          // distance only changes when mounting or mesh-shape inputs change.
          for (const { node } of shapes) node.updateMatrixWorld(true);
          rayOrigin.copy(part.marker.position).applyMatrix4(this.matrixWorld);
          rayDirection.copy(mountingNormal).transformDirection(this.matrixWorld);
          surfaceRaycaster.set(rayOrigin, rayDirection);
          part.surfaceDistance =
            surfaceRaycaster.intersectObjects(
              shapes.map(({ node }) => node),
              true
            )[0]?.distance ?? 0;
          part.surfaceDirty = false;
        }
        part.marker.position.addScaledVector(mountingNormal, part.surfaceDistance);
      }
    }

    super.updateMatrixWorld(force);
  }

  dispose() {
    this.disposed = true;
    this.trackerMarkerGeometry.dispose();
    this.trackerMarkerMaterial.dispose();
    for (const part of this.parts) {
      for (const attached of part.shapes) {
        this.remove(attached.node);
      }
    }
    this.parts = [];
  }
}
