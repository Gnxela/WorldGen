package me.alexng.untitled.generate;

import org.joml.Vector3f;

public class PrimitiveGenerator {

	/**
	 * Returns new offset
	 * Stride 6. Vec3 position, Vec3 normal
	 */
	public static int insertTriangleVertexData(int offset, float[] vertexData, Vector3f a) {
		addAll(offset, vertexData,
				a.x,
				a.y,
				a.z,
				// TODO: Generate normals
				0f,
				0f,
				0f
		);
		return offset + 6;
	}

	private static void addAll(int offset, float[] vertexData, Float... data) {
		for (Float f : data) {
			vertexData[offset++] = f;
		}
	}
}
