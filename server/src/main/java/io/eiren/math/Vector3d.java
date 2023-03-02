package io.eiren.math;


import io.github.axisangles.ktmath.Vector3;


public class Vector3d implements Cloneable {

	public double x;
	public double y;
	public double z;

	public Vector3d() {
	}

	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.x = x2 - x1;
		this.y = y2 - y1;
		this.z = z2 - z1;
	}

	public Vector3d(Vector3 src) {
		this(src.getX(), src.getY(), src.getZ());
	}

	public Vector3d set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vector3d set(Vector3d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		return this;
	}

	public Vector3d add(double addX, double addY, double addZ) {
		return new Vector3d(this.x + addX, this.y + addY, this.z + addZ);
	}

	public Vector3d addLocal(Vector3d vec) {
		return addLocal(vec.x, vec.y, vec.z);
	}

	public Vector3d addLocal(double addX, double addY, double addZ) {
		x += addX;
		y += addY;
		z += addZ;
		return this;
	}

	public Vector3d substract(double subX, double subY, double subZ) {
		return new Vector3d(this.x - subX, this.y - subY, this.z - subZ);
	}

	public Vector3d substractLocal(Vector3d vec) {
		if (null == vec) {
			return null;
		}
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
		return this;
	}

	public Vector3d substractLocal(double subX, double subY, double subZ) {
		x -= subX;
		y -= subY;
		z -= subZ;
		return this;
	}

	public Vector3d negate() {
		return new Vector3d(-x, -y, -z);
	}

	public Vector3d negateLocal() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	public Vector3d mult(double scalar) {
		return new Vector3d(x * scalar, y * scalar, z * scalar);
	}

	public Vector3d multLocal(double scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
		return this;
	}

	public Vector3d divide(double scalar) {
		return new Vector3d(x / scalar, y / scalar, z / scalar);
	}

	public Vector3d divideLocal(double scalar) {
		x /= scalar;
		y /= scalar;
		z /= scalar;
		return this;
	}

	public double dot(Vector3d v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public double dot(double vx, double vy, double vz) {
		return x * vx + y * vy + z * vz;
	}

	public Vector3d cross(Vector3d other, Vector3d result) {
		if (result == null)
			result = new Vector3d();
		double resX = ((y * other.z) - (z * other.y));
		double resY = ((z * other.x) - (x * other.z));
		double resZ = ((x * other.y) - (y * other.x));
		result.set(resX, resY, resZ);
		return result;
	}

	@Override
	public Vector3d clone() {
		return new Vector3d(this.x, this.y, this.z);
	}

	public Vector3d normalize() {
		double length = x * x + y * y + z * z;
		if (length != 1.0 && length != 0.0) {
			double invLength = 1.0 / Math.sqrt(length);
			return mult(invLength);
		}
		return clone();
	}

	public Vector3d normalizeLocal() {
		double length = x * x + y * y + z * z;
		if (length != 1.0 && length != 0.0) {
			length = Math.sqrt(length);
			double invLength = 1.0 / length;
			x *= invLength;
			z *= invLength;
			y *= invLength;
		}
		return this;
	}

	public Vector3 toVector3f() {
		return new Vector3((float) x, (float) y, (float) z);
	}

	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public double lengthSquared() {
		return x * x + y * y + z * z;
	}

	@Override
	public String toString() {
		return new StringBuilder("Vector3D{")
			.append(x)
			.append(',')
			.append(y)
			.append(',')
			.append(z)
			.append('}')
			.toString();
	}

	public void rotateAroundX(float f) {
		double f1 = Math.cos(f);
		double f2 = Math.sin(f);
		double d = x;
		double d1 = y * f1 + z * f2;
		double d2 = z * f1 - y * f2;
		x = (float) d;
		y = (float) d1;
		z = (float) d2;
	}

	public void rotateAroundY(float f) {
		double f1 = Math.cos(f);
		double f2 = Math.sin(f);
		double d = x * f1 + z * f2;
		double d1 = y;
		double d2 = z * f1 - x * f2;
		x = (float) d;
		y = (float) d1;
		z = (float) d2;
	}

	public double distanceTo(Vector3d vec3d) {
		return Math.sqrt(squaredDistance(vec3d));
	}

	public double squaredDistance(Vector3d point) {
		return squaredDistance(point.x, point.y, point.z);
	}

	public double squaredDistance(double toX, double toY, double toZ) {
		return (this.x - toX) * (this.x - toX)
			+ (this.y - toY) * (this.y - toY)
			+ (this.z - toZ) * (this.z - toZ);
	}

	public Vector3d add(Vector3d dir) {
		return add(dir.x, dir.y, dir.z);
	}

	public Vector3d substract(Vector3d dir) {
		return substract(dir.x, dir.y, dir.z);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector3d other = (Vector3d) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return Double.doubleToLongBits(z) == Double.doubleToLongBits(other.z);
	}
}
