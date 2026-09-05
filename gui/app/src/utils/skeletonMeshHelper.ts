import { Matrix4, Mesh, Object3D, Quaternion, Vector3 } from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { BoneKind, getBoneList } from './skeletonHelper';
import {
  BoneShapeConfig,
  CYLINDER_GEOMETRY,
  JOINT_GEOMETRY,
  JOINT_MATERIAL,
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
const boneDir = new Vector3();
const localOffset = new Vector3();

const DOWN = new Vector3(0, -1, 0);

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
      .catch(() => null); // no file yet -> keep the primitive
    modelCache.set(url, p);
  }
  return p;
}

interface AttachedShape {
  config: BoneShapeConfig;
  node: Object3D;
}

interface BonePart {
  bone: BoneKind;
  jointMesh: Mesh | null;
  jointRatio: number;
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

      let jointMesh: Mesh | null = null;
      if (config.joint) {
        jointMesh = new Mesh(JOINT_GEOMETRY, JOINT_MATERIAL);
        jointMesh.matrixAutoUpdate = false;
        jointMesh.frustumCulled = false;
        this.add(jointMesh);
      }

      const shapes: AttachedShape[] = [];
      for (const shapeConfig of config.shapes) {
        const node = new Object3D();
        node.matrixAutoUpdate = false;
        const primitive = new Mesh(shapeConfig.geometry ?? CYLINDER_GEOMETRY, material);
        primitive.frustumCulled = false;
        node.add(primitive);
        this.add(node);

        if (shapeConfig.modelUrl) {
          loadModel(shapeConfig.modelUrl).then((scene) => {
            if (!scene || this.disposed) return;
            const model = scene.clone(true);
            model.traverse((o) => {
              if (o instanceof Mesh) {
                o.material = material;
                o.frustumCulled = false;
              }
            });
            node.clear();
            node.add(model);
          });
        }

        shapes.push({ config: shapeConfig, node });
      }

      this.parts.push({
        bone,
        jointMesh,
        jointRatio: config.joint ?? 0.45,
        shapes,
      });
    }
  }

  setProportions(proportions: SkeletonProportions) {
    this.proportions = proportions;
  }

  updateMatrixWorld(force: boolean) {
    matrixWorldInv.copy(this.root.matrixWorld).invert();

    for (const part of this.parts) {
      const { bone, jointMesh, jointRatio, shapes } = part;

      boneMatrix.multiplyMatrices(matrixWorldInv, bone.matrixWorld);
      position.setFromMatrixPosition(boneMatrix); // head joint position

      const boneLength = Math.max(bone.boneT.boneLength, 1e-4);
      const o = bone.boneT.orientation;
      if (o) quat.set(o.x, o.y, o.z, o.w).normalize();
      else quat.identity();

      boneDir.copy(DOWN).applyQuaternion(quat);

      let maxGirth = 0;

      for (const attached of shapes) {
        const { config, node } = attached;
        const { scaleX, scaleY, scaleZ } = computeShapeScale(
          config,
          this.proportions,
          boneLength
        );
        maxGirth = Math.max(maxGirth, scaleX, scaleZ);

        shapePos.copy(position);
        // Default midpoint + optional along-bone offset
        shapePos.addScaledVector(
          boneDir,
          boneLength / 2 + (config.offset ?? 0) * boneLength
        );

        if (config.localOffset) {
          localOffset
            .copy(config.localOffset)
            .multiplyScalar(this.proportions.bodyScale)
            .applyQuaternion(quat);
          shapePos.add(localOffset);
        }

        node.position.copy(shapePos);
        node.quaternion.copy(quat);
        node.scale.set(scaleX, scaleY, scaleZ);
        node.updateMatrix();
      }

      if (jointMesh) {
        jointMesh.position.copy(position);
        jointMesh.scale.setScalar(jointRatio * maxGirth);
        jointMesh.updateMatrix();
      }
    }

    super.updateMatrixWorld(force);
  }

  dispose() {
    this.disposed = true;
    for (const part of this.parts) {
      if (part.jointMesh) this.remove(part.jointMesh);
      for (const attached of part.shapes) {
        this.remove(attached.node);
      }
    }
    this.parts = [];
  }
}
