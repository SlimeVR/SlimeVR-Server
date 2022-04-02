import { QuatT } from "slimevr-protocol/dist/server";


export class Quaternion {

    constructor(public x: number, public y: number, public z: number, public w: number) {}

    public inverse(): Quaternion {
        const norm = this.norm();
        if(norm > 0.0) {
            const invNorm = 1.0 / norm;
            return new Quaternion(-this.x * invNorm, -this.y * invNorm, -this.z * invNorm, this.w * invNorm);
        }
        // return an invalid result to flag the error
        return new Quaternion(0,0, 0, 1);
    }


    norm(): number {
        return this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public mult(q: Quaternion): Quaternion {
        const res = new Quaternion(0, 0, 0, 1);

        const qw = q.w, qx = q.x, qy = q.y, qz = q.z;
        res.x = this.x * qw + this.y * qz - this.z * qy + this.w * qx;
        res.y = -this.x * qz + this.y * qw + this.z * qx + this.w * qy;
        res.z = this.x * qy - this.y * qx + this.z * qw + this.w * qz;
        res.w = -this.x * qx - this.y * qy - this.z * qz + this.w * qw;
        return res;
    }


    static from(q: { x: number, y: number, z: number, w: number }): Quaternion {
        return new Quaternion(q.x, q.y, q.z, q.w)
    }

}