package io.github.axisangles.ktmath

import kotlin.math.sqrt

/**
 * guarantees last bit floating point accuracy
 */
fun atan(co: Float, si: Float) = atan(co.toDouble(), si.toDouble()).toFloat()
fun atan(ta: Float) = atan(ta.toDouble()).toFloat()
fun acos(co: Float) = acos(co.toDouble()).toFloat()
fun asin(si: Float) = asin(si.toDouble()).toFloat()


fun atan(co: Double, si: Double) =
	if (co == 0.0 && si == 0.0) 0.0
	else atanFold1(co, si)

fun atan(ta: Double) = when (ta) {
	1.0/0.0 -> 1.570796326794897
	-1.0/0.0 -> -1.570796326794897
	else -> atanFold1(1.0, ta)
}
fun acos(co: Double) = atanFold1(co, sqrt(1.0 - co*co))
fun asin(si: Double) = atanFold1(sqrt(1.0 - si*si), si)

// The goal was to make use of as much FMA and parallel instruction execution as possible
// make a viktor version to replace this in the future
private fun atanFold1(x: Double, y: Double) =
	if (y < 0.0) -atanFold2(x, -y)
	else atanFold2(x, y)

private fun atanFold2(x: Double, y: Double) =
	if (x < 0.0) 3.141592653589793 - atanFold3(-x, y)
	else atanFold3(x, y)

private fun atanFold3(x: Double, y: Double) =
	if (y > x) 1.570796326794897 - atanFold4(y, x)
	else atanFold4(x, y)

private fun atanFold4(x: Double, y: Double) =
	if (y > 0.4142135623730950*x) 0.7853981633974483 - atanFold5(
		x + y, x - y)
	else atanFold5(x, y)

private fun atanFold5(x: Double, y: Double) =
	if (y > 0.1989123673796580*x) 0.3926990816987242 - atanFold6(
		x + 0.4142135623730950*y, 0.4142135623730950*x - y)
	else atanFold6(x, y)

private fun atanFold6(x: Double, y: Double) =
	if (y > 0.09849140335716425*x) 0.1963495408493621 - atanFold7(
		x + 0.1989123673796580*y, 0.1989123673796580*x - y)
	else atanFold7(x, y)

private fun atanFold7(x: Double, y: Double) =
	if (y > 0.04912684976946725*x) 0.09817477042468103 - atanFinal(
		x + 0.09849140335716425*y, 0.09849140335716425*x - y)
	else atanFinal(x, y)

private fun atanFinal(x: Double, y: Double): Double {
	val s = y/x
	val t = s*s
	return s*(1.0 - t*(1.0/3.0 - t*(1.0/5.0)))// - t*(1.0/7.0))))// - t*(1.0/9.0)))))// - t*(1.0/11.0 - t*(1.0/13.0 - t*(1.0/15.0))))))))
//    val r = y/x
//    val s = r*r
//    val t = s*s
//    // help the CPU take advantage of parallel instruction execution
//    val odd = s*(1.0/3.0 + t*(1.0/7.0))
//    val evn = 1.0 + t*(1.0/5.0 + t*(1.0/9.0))
//    return r*(evn - odd)
}
