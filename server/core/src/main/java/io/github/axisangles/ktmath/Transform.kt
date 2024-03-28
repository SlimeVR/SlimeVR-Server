package io.github.axisangles.ktmath

data class Transform(
	var rotation: Quaternion = Quaternion.IDENTITY,
	var translation: Vector3 = Vector3.NULL,
	var scale: Vector3 = Vector3(1f, 1f, 1f),
) {
	companion object {
		val IDENTITY = Transform()
	}

	fun set(transform: Transform): Transform {
		// Quaternions and vectors are immutable, so we're copying links
		this.rotation = transform.rotation
		this.translation = transform.translation
		this.scale = transform.scale
		return this
	}

	/**
	 * Changes the values of this matrix according to its parent. Very similar
	 * to the concept of Node/Spatial transforms.
	 *
	 * @param parent The parent matrix.
	 * @return This matrix, after combining.
	 */
	fun combineWithParent(parent: Transform): Transform {
		this.scale = this.scale hadamard parent.scale

		this.rotation = parent.rotation * this.rotation

		val scaledTranslation = this.translation hadamard parent.scale
		this.translation = parent.rotation.sandwich(scaledTranslation) + parent.translation

		return this
	}
}
