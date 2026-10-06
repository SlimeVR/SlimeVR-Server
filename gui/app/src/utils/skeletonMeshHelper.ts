import { Box3, Object3D, Quaternion, Vector3 } from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { SkeletonRenderPart } from './skeletonHelper';
import { BodyPart, BoneT } from 'solarxr-protocol';
import { FlatDeviceTracker } from '@/store/app-store';
import { QuaternionFromQuatT } from '@/maths/quaternion';
import { Vector3FromVec3fT } from '@/maths/vector3';
import {
  BoneShapeConfig,
  DefaultBoneModelUrl,
  ModelDimensions,
  SKELETON_PART_PRESETS,
  computeShapeScale,
} from './skeletonParts';
import { SkeletonProportions, deriveSkeletonProportions } from './skeletonProportions';

const position = new Vector3();
const quat = new Quaternion();

const modelBox = new Box3();

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
  /** On the bone: its orientation, and the shape's offset and size along its axes. */
  node: Object3D;
  /** Under the node: the turn that puts the model onto the bone's axes. */
  tilt: Object3D;
  /** From the bone's head to the node, in the bone's axes. */
  offset: Vector3;
  model: ModelDimensions | null;
  /** The model's box in its own axes. */
  bounds: Box3 | null;
}

interface BonePart extends SkeletonRenderPart {
  shapes: AttachedShape[];
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

  constructor(bones: Map<BodyPart, BoneT>) {
    super();

    const addrObject = (modelUrl: string, partName: string) =>
      modelUrl + ':' + partName;
    const modelUrlsToFetch = new Set<string>();
    const partsByAddr: Record<string, Array<[BonePart, AttachedShape]>> = {};

    for (const bone of bones.values()) {
      if (bone.bodyPart === BodyPart.NONE) continue;
      const config = SKELETON_PART_PRESETS[bone.bodyPart];
      if (config === undefined || !config.visible) continue;

      const part: BonePart = {
        bone,
        shapes: [],
      };
      for (const shapeConfig of config.shapes) {
        const node = new Object3D();
        node.matrixAutoUpdate = false;
        this.add(node);

        const tilt = new Object3D();
        node.add(tilt);

        const attached: AttachedShape = {
          config: shapeConfig,
          node,
          tilt,
          offset: new Vector3(),
          model: null,
          bounds: null,
        };

        const modelUrl = shapeConfig.modelUrl ?? DefaultBoneModelUrl;
        const partName = shapeConfig.objectName;
        if (partName && modelUrl) {
          modelUrlsToFetch.add(modelUrl);
          const addr = addrObject(modelUrl, partName);
          (partsByAddr[addr] ??= []).push([part, attached]);
        }

        part.shapes.push(attached);
      }

      this.parts.push(part);
    }

    modelUrlsToFetch.forEach((url) =>
      loadModel(url).then((scene) => {
        if (!scene || this.disposed) return;

        scene.traverse((og) => {
          if (og === scene) return;

          const addr = addrObject(url, og.name);
          for (const [_, attached] of partsByAddr[addr] ?? []) {
            const o = og.clone(true);

            o.position.set(0, 0, 0);
            o.quaternion.identity();
            o.scale.set(1, 1, 1);

            modelBox.setFromObject(o);

            attached.model = {
              width: modelBox.max.x - modelBox.min.x,
              depth: modelBox.max.z - modelBox.min.z,
              length: Math.abs(modelBox.min.y),
            };

            attached.bounds = modelBox.clone();
            attached.tilt.clear();
            attached.tilt.add(o);
          }
        });
      })
    );
  }

  setProportions(proportions: SkeletonProportions) {
    this.proportions = proportions;
  }

  setTrackers(trackers: Partial<Record<BodyPart, FlatDeviceTracker>>) {
    for (const part of this.parts) {
      part.tracker = trackers[part.bone.bodyPart];
    }
  }

  setBones(bones: Map<BodyPart, BoneT>) {
    for (const part of this.parts) {
      const bone = bones.get(part.bone.bodyPart);
      if (!bone) continue;
      part.bone = bone;
    }
  }

  updateMatrixWorld(force: boolean) {
    const hasBustTracker = this.parts.some(
      (part) =>
        (part.bone.bodyPart === BodyPart.LEFT_BUST ||
          part.bone.bodyPart === BodyPart.RIGHT_BUST) &&
        part.tracker
    );
    const hasPosteriorTracker = this.parts.some(
      (part) =>
        (part.bone.bodyPart === BodyPart.LEFT_POSTERIOR ||
          part.bone.bodyPart === BodyPart.RIGHT_POSTERIOR) &&
        part.tracker
    );
    const hasTailTracker = this.parts.some(
      (part) =>
        (part.bone.bodyPart === BodyPart.TAIL ||
          part.bone.bodyPart === BodyPart.TAIL_1 ||
          part.bone.bodyPart === BodyPart.TAIL_2 ||
          part.bone.bodyPart === BodyPart.TAIL_3 ||
          part.bone.bodyPart === BodyPart.TAIL_4 ||
          part.bone.bodyPart === BodyPart.TAIL_5 ||
          part.bone.bodyPart === BodyPart.TAIL_6) &&
        part.tracker
    );

    for (const part of this.parts) {
      const { bone, shapes } = part;
      const isBustPart =
        bone.bodyPart === BodyPart.LEFT_BUST || bone.bodyPart === BodyPart.RIGHT_BUST;
      const isPosteriorPart =
        bone.bodyPart === BodyPart.LEFT_POSTERIOR ||
        bone.bodyPart === BodyPart.RIGHT_POSTERIOR;
      const isTailPart =
        bone.bodyPart === BodyPart.TAIL ||
        bone.bodyPart === BodyPart.TAIL_1 ||
        bone.bodyPart === BodyPart.TAIL_2 ||
        bone.bodyPart === BodyPart.TAIL_3 ||
        bone.bodyPart === BodyPart.TAIL_4 ||
        bone.bodyPart === BodyPart.TAIL_5 ||
        bone.bodyPart === BodyPart.TAIL_6;

      const hidden =
        (isBustPart && !hasBustTracker) ||
        (isTailPart && !hasTailTracker) ||
        (isPosteriorPart && !hasPosteriorTracker);

      for (const attached of shapes) {
        attached.node.visible = !hidden;
      }
      if (hidden) {
        continue;
      }

      position.copy(Vector3FromVec3fT(bone.headPosition));
      quat.copy(QuaternionFromQuatT(bone.orientation)).normalize();
      const boneLength = Math.max(bone.boneLength, 1e-4);

      for (const attached of shapes) {
        const { config, node, tilt, model } = attached;
        const size = computeShapeScale(
          config,
          this.proportions,
          boneLength,
          model ?? undefined
        );

        attached.offset.set(0, 0, 0);
        if (config.offset && model) {
          attached.offset.copy(
            config.offset({ model, proportions: this.proportions, boneLength })
          );
        }

        node.position.copy(attached.offset).applyQuaternion(quat).add(position);
        node.quaternion.copy(quat);
        node.scale.set(size.width, size.length, size.depth);
        node.updateMatrix();

        if (config.rotation) tilt.quaternion.copy(config.rotation);
      }
    }

    super.updateMatrixWorld(force);
  }

  dispose() {
    this.disposed = true;
    for (const part of this.parts) {
      for (const attached of part.shapes) {
        this.remove(attached.node);
      }
    }
    this.parts = [];
  }
}
