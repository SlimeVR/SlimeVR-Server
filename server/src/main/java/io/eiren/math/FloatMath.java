package io.eiren.math;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;


public class FloatMath {

	public static final float PI = (float) Math.PI;
	public static final float TWO_PI = (float) (Math.PI * 2);
	public static final float ANGLE_EPSILON = 0.028f; // in degrees (float
														// epsilon for sin/cos)
	public static final float ANGLE_EPSILON_RAD = toRad(ANGLE_EPSILON);

	public static final float ZERO_TOLERANCE_F = FastMath.ZERO_TOLERANCE;
	public static final double ZERO_TOLERANCE_D = 0.0001d;

	public static final float SQRT_TWO = (float) Math.sqrt(2f);
	public static final float INV_SQRT_TWO = 1f / SQRT_TWO;
	public static final float SQRT_THREE = (float) Math.sqrt(3f);
	public static final float INV_SQRT_THREE = 1f / SQRT_THREE;
	public static final float TWO_FPI = PI * 2;

	public static final float SIN_75_DEG = 0.965926f;
	public static final float SIN_60_DEG = 0.866025f;
	public static final float SIN_45_DEG = 0.707107f;
	public static final float SIN_30_DEG = 0.5f;
	public static final float SIN_15_DEG = 0.258819f;

	public static final float COS_75_DEG = 0.258819f;
	public static final float COS_60_DEG = 0.5f;
	public static final float COS_45_DEG = 0.707107f;
	public static final float COS_30_DEG = 0.866025f;
	public static final float COS_15_DEG = 0.965926f;

	public static final int TEN_BITS = ~(~0 << 10);
	public static final int TENTH_BIT = 1 << 10;
	public static final int TEN_BITS_MAX = ~(~0 << 9);
	public static final int TEN_BITS_MAX_UNSIGNED = ~(~0 << 10);
	public static final int TWO_BITS = ~(~0 << 2);
	public static final int SECOND_BIT = 1 << 2;
	public static final int TWO_BITS_MAX = ~(~0 << 1);
	public static final int TWO_BITS_MAX_UNSIGNED = ~(~0 << 2);

	public static float roundIfZero(float x) {
		return Math.abs(x) < ZERO_TOLERANCE_F ? 0.0f : x;
	}

	public static boolean equalsToZero(float x) {
		return Math.abs(x) < ZERO_TOLERANCE_F;
	}

	public static boolean lessThanZero(float x) {
		return (x < -ZERO_TOLERANCE_F);
	}

	public static boolean lessOrEqualsToZero(float x) {
		return (x < ZERO_TOLERANCE_F);
	}

	public static boolean greaterThanZero(float x) {
		return (x > ZERO_TOLERANCE_F);
	}

	public static boolean greaterOrEqualsToZero(float x) {
		return (x > -ZERO_TOLERANCE_F);
	}

	public static boolean equalsToZero(float x, float epsilon) {
		return Math.abs(x) < epsilon;
	}

	public static boolean equalsWithEpsilon(float x, float y) {
		return Math.abs(x - y) < ZERO_TOLERANCE_F;
	}

	public static boolean equalsWithEpsilon(float x, float y, float epsilon) {
		return Math.abs(x - y) < epsilon;
	}

	public static boolean lessWithEpsilon(float x, float y) {
		return (x < y - ZERO_TOLERANCE_F);
	}

	public static boolean lessOrEqualsWithEpsilon(float x, float y) {
		return (x < y + ZERO_TOLERANCE_F);
	}

	public static boolean lessWithEpsilon(float x, float y, float epsilon) {
		return (x < y - epsilon);
	}

	public static boolean lessOrEqualsWithEpsilon(float x, float y, float epsilon) {
		return (x < y + epsilon);
	}

	public static boolean greaterWithEpsilon(float x, float y) {
		return (x > y + ZERO_TOLERANCE_F);
	}

	public static boolean greaterOrEqualsWithEpsilon(float x, float y) {
		return (x > y - ZERO_TOLERANCE_F);
	}

	public static boolean greaterWithEpsilon(float x, float y, float epsilon) {
		return (x > y + epsilon);
	}

	public static boolean greaterOrEqualsWithEpsilon(float x, float y, float epsilon) {
		return (x > y - epsilon);
	}

	public static double roundIfZero(double x) {
		return Math.abs(x) < ZERO_TOLERANCE_D ? 0.0d : x;
	}

	public static boolean equalsToZero(double x) {
		return Math.abs(x) < ZERO_TOLERANCE_D;
	}

	public static boolean equalsWithEpsilon(double x, double y) {
		return Math.abs(x - y) < ZERO_TOLERANCE_D;
	}

	public static boolean lessWithEpsilon(double x, double y) {
		return (x < y - ZERO_TOLERANCE_D);
	}

	public static boolean lessOrEqualsWithEpsilon(double x, double y) {
		return (x < y + ZERO_TOLERANCE_D);
	}

	public static boolean greaterWithEpsilon(double x, double y) {
		return (x > y + ZERO_TOLERANCE_D);
	}

	public static boolean greaterOrEqualsWithEpsilon(double x, double y) {
		return (x > y - ZERO_TOLERANCE_D);
	}

	public static float toDegrees(float angrad) {
		return angrad * 180.0f / PI;
	}

	public static float toRad(float deg) {
		return deg / 180.0f * PI;
	}

	public static boolean radEqual(float angle1, float angle2) {
		float diff = clampRad(angle1 - angle2);
		return Math.abs(diff) < ANGLE_EPSILON_RAD;
	}

	public static boolean degreesEqual(float angle1, float angle2) {
		float diff = clampDegrees(angle1 - angle2);
		return Math.abs(diff) < ANGLE_EPSILON;
	}

	/**
	 * @deprecated use {@link #normalizeRad(float)}
	 */
	@Deprecated
	public static float clampRad(float angle) {
		return normalizeRad(angle);
	}

	public static float normalizeRad(float angle) {
		return FastMath.normalize(angle, -FastMath.PI, FastMath.PI);
	}

	/**
	 * @deprecated use {@link #normalizeDegrees(float)}
	 */
	@Deprecated
	public static float clampDegrees(float angle) {
		return normalizeDegrees(angle);
	}

	public static float normalizeDegrees(float angle) {
		return FastMath.normalize(angle, -180f, 180f);
	}

	public static float animateEase(float t) {
		// Special case of Bezier interpolation (p0 = p1 = 0, p2 = p3 = 1)
		return (3.0f - 2.0f * t) * t * t;
	}

	public static float animateEaseIn(float t) {
		return t * t;
	}

	/**
	 * Lineary remaps value from the source interval to the target interval.
	 * <a href="https://en.wikipedia.org/wiki/Linear_interpolation">details</a>
	 */
	public static float mapValue(
		float value,
		float sourceStart,
		float sourceEnd,
		float targetStart,
		float targetEnd
	) {
		return targetStart
			+ (value - sourceStart) * (targetEnd - targetStart) / (sourceEnd - sourceStart);
	}

	/**
	 * Clamps the given value and remaps to the target interval.
	 * <p>
	 * Note the source interval values should be sorted.
	 */
	public static float mapValueWithClampBefore(
		float value,
		float sourceBottom,
		float sourceTop,
		float targetBottom,
		float targetTop
	) {
		return mapValue(
			clamp(value, sourceBottom, sourceTop),
			sourceBottom,
			sourceTop,
			targetBottom,
			targetTop
		);
	}

	/**
	 * Remaps the given value to the target interval and clamps.
	 * <p>
	 * Note the target interval values should be sorted.
	 */
	public static float mapValueWithClampAfter(
		float value,
		float sourceBottom,
		float sourceTop,
		float targetBottom,
		float targetTop
	) {
		return clamp(
			mapValue(value, sourceBottom, sourceTop, targetBottom, targetTop),
			targetBottom,
			targetTop
		);
	}

	public static float smoothstep(float edge0, float edge1, float x) {
		// Scale, bias and saturate x to 0..1 range
		x = FastMath.clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
		// Evaluate polynomial
		return x * x * (3f - 2f * x);
	}

	public static float smootherstep(float edge0, float edge1, float x) {
		// Scale, and clamp x to 0..1 range
		x = FastMath.clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
		// Evaluate polynomial
		return x * x * x * (x * (x * 6f - 15f) + 10f);
	}

	/**
	 * Applies linear contrast (with clamping).
	 * 
	 * @param t - input value in range (0..1)
	 * @param k - contrast factor in range (-1..1):
	 * <ul>
	 * <li>1.0 - maximal contrast</li>
	 * <li><b>0.0</b> - bypass (returns input value)</li>
	 * <li>-1.0 - minimal contrast (returns 0.5f for any input)</li>
	 * </ul>
	 * @return contrasted value in range (0..1)
	 */
	public static float contrastLinear(float t, float k) {
		float x = 2f * t - 1f; // -1..1
		float gamma = (1f + k) / (1f - k);
		float f = FastMath.clamp(gamma * x, -1f, 1f); // -1..1
		return 0.5f * (f + 1f); // 0..1
	}

	/**
	 * Applies non-linear contrast by power function.
	 * 
	 * @param t - input value in range (0..1)
	 * @param k - contrast factor in range (-1..1) exclusive:
	 * <ul>
	 * <li>0.999 - maximal contrast</li>
	 * <li>0.0 - bypass (returns input value)</li>
	 * <li>-0.999 - minimal contrast</li>
	 * </ul>
	 * @return contrasted value in range (0..1)
	 */
	public static float contrastPower(float t, float k) {
		float x = 2f * t - 1f; // -1..1
		float gamma = (1f - k) / (1f + k);
		float f = FastMath.sign(x) * FastMath.pow(FastMath.abs(x), gamma); // -1..1
		return 0.5f * (f + 1f); // 0..1
	}

	/**
	 * Applies non-linear contrast by square splines.
	 * 
	 * @param t - input value in range (0..1)
	 * @param k - contrast factor in range (-1..1):
	 * <ul>
	 * <li>1.0 - maximal contrast</li>
	 * <li>0.0 - bypass (returns input value)</li>
	 * <li>-1.0 - minimal contrast</li>
	 * </ul>
	 * @return contrasted value in range (0..1)
	 */
	public static float contrastQuadricSpline(float t, float k) {
		float x = 2f * t - 1f; // -1..1
		float f = x * (1f + k * (1f - FastMath.abs(x))); // -1..1
		return 0.5f * (f + 1f); // 0..1
	}

	/**
	 * Applies non-linear contrast by square splines inverted function.
	 * 
	 * @param t - input value in range (0..1)
	 * @param k - contrast factor in range (-2..2):
	 * <ul>
	 * <li>2.0 - maximal contrast</li>
	 * <li>0.0 - bypass (returns input value)</li>
	 * <li>-2.0 - minimal contrast</li>
	 * </ul>
	 * @return contrasted value in range (0..1)
	 */
	public static float contrastInvertQuadricSpline(float t, float k) {
		float x = 2f * t - 1f; // -1..1
		float g;
		if (k > 0) {
			g = FastMath.sign(x) * FastMath.sqrt(FastMath.abs(x)) - 2f * x;
		} else {
			g = FastMath.sign(x) * (FastMath.sqrt(1f - FastMath.abs(x)) - 1f);
		}
		float f = (1f + k) * x + k * g; // -1..1
		return 0.5f * (f + 1f); // 0..1
	}

	/**
	 * Applies non-linear contrast by cubic splines.
	 * 
	 * @param t - input value in range (0..1)
	 * @param k - contrast factor in range (-1..1):
	 * <ul>
	 * <li>1.0 - maximal contrast</li>
	 * <li>0.0 - bypass (returns input value)</li>
	 * <li>-1.0 - minimal contrast</li>
	 * </ul>
	 * @return contrasted value in range (0..1)
	 */
	public static float contrastCubicSpline(float t, float k) {
		float x = 2f * t - 1f; // -1..1
		float f = x * (1f + FastMath.abs(k) * (x * x - 1f));
		if (k < 0)
			f -= x * 3f * k * (1f - FastMath.abs(x));
		return 0.5f * (f + 1f); // 0..1
	}

	public static float fraction(float f) {
		return f - (int) f;
	}

	public static double fraction(double d) {
		return d - (long) d;
	}

	/**
	 * @deprecated Do not copy {@link Math} methods.
	 */
	@Deprecated
	public static float min(float a, float b) {
		return a > b ? b : a;
	}

	public static float min(float a, float b, float c) {
		return Math.min(Math.min(a, b), c);
	}

	public static float min(float a, float b, float c, float d) {
		return Math.min(Math.min(a, b), Math.min(c, d));
	}

	/**
	 * @deprecated Do not copy {@link Math} methods.
	 */
	@Deprecated
	public static float max(float a, float b) {
		return a > b ? a : b;
	}

	public static float max(float a, float b, float c) {
		return Math.max(Math.max(a, b), c);
	}

	public static float max(float a, float b, float c, float d) {
		return Math.max(Math.max(a, b), Math.max(c, d));
	}

	public static float cos(float value) {
		return (float) Math.cos(value);
	}

	public static float sin(float value) {
		return (float) Math.sin(value);
	}

	public static float ceil(float value) {
		return (float) Math.ceil(value);
	}

	public static float floor(float value) {
		return (float) Math.floor(value);
	}

	public static float pow(float value, float power) {
		return (float) Math.pow(value, power);
	}

	/**
	 * @deprecated Do not copy {@link Math} methods.
	 */
	@Deprecated
	public static float abs(float value) {
		return Math.abs(value);
	}

	/**
	 * @deprecated Do not copy {@link Math} methods.
	 */
	@Deprecated
	public static float round(float value) {
		return (float) Math.round(value);
	}

	public static float sqrt(float value) {
		return (float) Math.sqrt(value);
	}

	public static float distance(float x0, float y0, float z0, float x1, float y1, float z1) {
		return distance(x1 - x0, y1 - y0, z1 - z0);
	}

	public static float distance(float x, float y, float z) {
		return sqrt(sqrDistance(x, y, z));
	}

	public static float sqrDistance(float x, float y, float z) {
		return x * x + y * y + z * z;
	}

	public static float distance(float x, float y) {
		return sqrt(sqrDistance(x, y));
	}

	public static float sqrDistance(float x, float y) {
		return x * x + y * y;
	}

	public static float sqrDistance(Vector3f v, float x1, float y1, float z1) {
		return sqrDistance(x1 - v.x, y1 - v.y, z1 - v.z);
	}

	public static float sqrDistance(float x0, float y0, float z0, float x1, float y1, float z1) {
		return sqrDistance(x1 - x0, y1 - y0, z1 - z0);
	}

	public static float hypot(float x, float y) {
		return FastMath.sqrt(x * x + y * y);
	}

	public static float hypot(float x, float y, float z) {
		return FastMath.sqrt(x * x + y * y + z * z);
	}

	/**
	 * The same as FastMath.clamp
	 */
	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	public static Vector3f int2101010RevToFloats(int packedValue, Vector3f store) {
		if (store == null)
			store = new Vector3f();
		store.x = packedValue & TEN_BITS_MAX;
		if ((packedValue & TENTH_BIT) != 0)
			store.x *= -1;
		store.y = (packedValue >>> 10) & TEN_BITS_MAX;
		if ((packedValue & (TENTH_BIT << 10)) != 0)
			store.y *= -1;
		store.z = (packedValue >>> 20) & TEN_BITS_MAX;
		if ((packedValue & (TENTH_BIT << 20)) != 0)
			store.z *= -1;
		return store;
	}

	public static int floatToInt210101Rev(Vector3f values) {
		int store = 0;
		store |= ((int) values.x) & TEN_BITS_MAX;
		if (values.x < 0)
			store |= TENTH_BIT;
		store |= (((int) values.y) & TEN_BITS_MAX) << 10;
		if (values.y < 0)
			store |= TENTH_BIT << 10;
		store |= (((int) values.z) & TEN_BITS_MAX) << 20;
		if (values.z < 0)
			store |= TENTH_BIT << 20;
		return store;
	}

	public static int floatToInt210101RevNormalized(Vector3f values) {
		int store = 0;
		store |= ((int) (values.x * TEN_BITS)) & TEN_BITS_MAX;
		if (values.x < 0)
			store |= TENTH_BIT;
		store |= (((int) (values.y * TEN_BITS)) & TEN_BITS_MAX) << 10;
		if (values.y < 0)
			store |= TENTH_BIT << 10;
		store |= (((int) (values.z * TEN_BITS)) & TEN_BITS_MAX) << 20;
		if (values.z < 0)
			store |= TENTH_BIT << 20;
		return store;
	}

	public static int floatToUnsignedInt210101Rev(Vector3f values) {
		int store = 0;
		store |= ((int) values.x) & TEN_BITS;
		store |= (((int) values.y) & TEN_BITS) << 10;
		store |= (((int) values.z) & TEN_BITS) << 20;
		return store;
	}

	public static int floatToUnsignedInt210101RevNormalized(Vector3f values) {
		int store = 0;
		store |= ((int) (values.x * TEN_BITS)) & TEN_BITS;
		store |= (((int) (values.y * TEN_BITS)) & TEN_BITS) << 10;
		store |= (((int) (values.z * TEN_BITS)) & TEN_BITS) << 20;
		return store;
	}

	public static int floatToInt210101Rev(float x, float y, float z) {
		int store = 0;
		store |= ((int) x) & TEN_BITS_MAX;
		if (x < 0)
			store |= TENTH_BIT;
		store |= (((int) y) & TEN_BITS_MAX) << 10;
		if (y < 0)
			store |= TENTH_BIT << 10;
		store |= (((int) z) & TEN_BITS_MAX) << 20;
		if (z < 0)
			store |= TENTH_BIT << 20;
		return store;
	}

	public static int floatToUnsignedInt210101Rev(float x, float y, float z) {
		int store = 0;
		store |= ((int) x) & TEN_BITS;
		store |= (((int) y) & TEN_BITS) << 10;
		store |= (((int) z) & TEN_BITS) << 20;
		return store;
	}

	public static Vector4f int2101010RevToFloats(int packedValue, Vector4f store) {
		if (store == null)
			store = new Vector4f();
		store.x = packedValue & TEN_BITS_MAX;
		if ((packedValue & TENTH_BIT) != 0)
			store.x *= -1;
		store.y = (packedValue >>> 10) & TEN_BITS_MAX;
		if ((packedValue & (TENTH_BIT << 10)) != 0)
			store.y *= -1;
		store.z = (packedValue >>> 20) & TEN_BITS_MAX;
		if ((packedValue & (TENTH_BIT << 20)) != 0)
			store.z *= -1;
		store.w = (packedValue >>> 30) & TWO_BITS_MAX;
		if ((packedValue & (SECOND_BIT << 30)) != 0)
			store.w *= -1;
		return store;
	}

	public static int floatToInt210101Rev(Vector4f values) {
		int store = 0;
		store |= ((int) values.x) & TEN_BITS_MAX;
		if (values.x < 0)
			store |= TENTH_BIT;
		store |= (((int) values.y) & TEN_BITS_MAX) << 10;
		if (values.y < 0)
			store |= TENTH_BIT << 10;
		store |= (((int) values.z) & TEN_BITS_MAX) << 20;
		if (values.z < 0)
			store |= TENTH_BIT << 20;
		store |= (((int) values.z) & TWO_BITS_MAX) << 30;
		if (values.w < 0)
			store |= SECOND_BIT << 30;
		return store;
	}

	public static int floatToUnsignedInt210101Rev(Vector4f values) {
		int store = 0;
		store |= ((int) values.x) & TEN_BITS;
		store |= (((int) values.y) & TEN_BITS) << 10;
		store |= (((int) values.z) & TEN_BITS) << 20;
		store |= (((int) values.z) & TWO_BITS) << 30;
		return store;
	}

	public static Vector3f unsignedInt2101010RevToFloats(int packedValue, Vector3f store) {
		if (store == null)
			store = new Vector3f();
		store.x = packedValue & TEN_BITS;
		store.y = (packedValue >>> 10) & TEN_BITS;
		store.z = (packedValue >>> 20) & TEN_BITS;
		return store;
	}

	public static Vector4f unsignedInt2101010RevToFloats(int packedValue, Vector4f store) {
		if (store == null)
			store = new Vector4f();
		store.x = packedValue & TEN_BITS;
		store.y = (packedValue >>> 10) & TEN_BITS;
		store.z = (packedValue >>> 20) & TEN_BITS;
		store.w = (packedValue >>> 30) & TWO_BITS;
		return store;
	}

	public static Vector3f int2101010RevNormalizedToFloats(int packedValue, Vector3f store) {
		store = int2101010RevToFloats(packedValue, store);
		store.x /= TEN_BITS_MAX;
		store.y /= TEN_BITS_MAX;
		store.z /= TEN_BITS_MAX;
		return store;
	}

	public static Vector4f int2101010RevNormalizedToFloats(int packedValue, Vector4f store) {
		store = int2101010RevToFloats(packedValue, store);
		store.x /= TEN_BITS_MAX;
		store.y /= TEN_BITS_MAX;
		store.z /= TEN_BITS_MAX;
		store.w /= TWO_BITS_MAX;
		return store;
	}

	public static Vector3f unsignedInt2101010RevNormalizedToFloats(
		int packedValue,
		Vector3f store
	) {
		store = unsignedInt2101010RevToFloats(packedValue, store);
		store.x /= TEN_BITS;
		store.y /= TEN_BITS;
		store.z /= TEN_BITS;
		return store;
	}

	public static Vector4f unsignedInt2101010RevNormalizedToFloats(
		int packedValue,
		Vector4f store
	) {
		store = unsignedInt2101010RevToFloats(packedValue, store);
		store.x /= TEN_BITS;
		store.y /= TEN_BITS;
		store.z /= TEN_BITS;
		store.w /= TWO_BITS;
		return store;
	}
}
