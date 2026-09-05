import { Box3, Matrix4, Mesh, Object3D, Quaternion, Vector3 } from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { BoneKind, getBoneList } from './skeletonHelper';
import {
  BoneShapeConfig,
  ModelDimensions,
  SKELETON_PART_PRESETS,
  computeShapeScale,
  getPartMaterial,
} from './skeletonParts';
import { SkeletonProportions, deriveSkeletonProportions } from './skeletonProportions';

const matrixWorldInv = new Matrix4();
const boneMatrix = new Matrix4();
const position = new Vector3();
const shapePos = new Vector3();
const quat = new Quaternion();
const localOffset = new Vector3();

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
  node: Object3D;
  model: ModelDimensions | null;
}

interface BonePart {
  bone: BoneKind;
  shapes: AttachedShape[];
}

/**
 * Renders a skeleton with modular 3D models or primitives attached to each bone.
 * Every bone holds a list of rigid shapes (e.g. spine column, chestplate armor, etc.)
 * that transform and scale dynamically with bone kinematics and user proportions.
 */
export class BasedSkeletonMeshHelper extends Object3D {
  readonly type = 'SkeletonMeshHelper';
  private root: Object3D;
  private parts: BonePart[] = [];
  private proportions: SkeletonProportions = deriveSkeletonProportions(new Map());
  private disposed = false;

  constructor(root: Object3D) {
    super();

    this.root = root;
    this.matrix = root.matrixWorld;
    this.matrixAutoUpdate = false;

    for (const bone of getBoneList(root)) {
      if (!(bone instanceof BoneKind) || bone.tail) continue;
      const config = SKELETON_PART_PRESETS[bone.boneT.bodyPart];
      if (!config.visible) continue;

      const material = getPartMaterial(bone.boneColor);

      const shapes: AttachedShape[] = [];
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
            modelBox.makeEmpty();
            model.traverse((o) => {
              if (o !== model) {
                // The exporter parks the model at its bone's rest pose; the
                // part config says how it sits on the bone instead.
                o.position.set(0, 0, 0);
                o.quaternion.identity();
                o.scale.set(1, 1, 1);
              }
              if (o instanceof Mesh) {
                o.material = material;
                o.frustumCulled = false;
                o.geometry.computeBoundingBox();
                if (o.geometry.boundingBox) modelBox.union(o.geometry.boundingBox);
              }
            });
            // The export writes each model in its bone's frame, running down
            // -Y off the bone's head, so it needs no orienting here.
            attached.model = {
              width: modelBox.max.x - modelBox.min.x,
              depth: modelBox.max.z - modelBox.min.z,
              length: Math.abs(modelBox.min.y),
            };

            node.clear();
            node.add(model);
          });
        }

        shapes.push(attached);
      }

      this.parts.push({ bone, shapes });
    }
  }

  setProportions(proportions: SkeletonProportions) {
    this.proportions = proportions;
  }

  updateMatrixWorld(force: boolean) {
    matrixWorldInv.copy(this.root.matrixWorld).invert();

    for (const part of this.parts) {
      const { bone, shapes } = part;

      boneMatrix.multiplyMatrices(matrixWorldInv, bone.matrixWorld);
      position.setFromMatrixPosition(boneMatrix);
      quat.copy(bone.orientation);
      const boneLength = Math.max(bone.boneT.boneLength, 1e-4);

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
