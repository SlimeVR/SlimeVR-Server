package dev.slimevr.util

/**
 * Returns a sub-list containing last n elements (similar to [takeLast]).
 * @throws IllegalArgumentException if [n] is negative.
 */
fun <T> List<T>.subListOfLast(
	n: Int,
): List<T> {
	require(n >= 0) { "Requested element count $n is less than zero." }
	if (n == 0) return emptyList()
	val size = size
	if (n >= size) return this
	return subList(size - n, size)
}
