package me.alexng.untitled.generate;

import org.joml.Vector3f;

public class PrimitiveGenerator {

	/**
	 * Returns the number of floats written
	 * Stride 6. Vec3 position, Vec3 normal
	 */
	public static int insertTriangleVertexData(int offset, float[] vertexData, Vector3f vector, Vector3f normal) {
		writeAll(offset, vertexData,
				vector.x,
				vector.y,
				vector.z,
				normal.x,
				normal.y,
				normal.z
		);
		return 6;
	}

	private static void writeAll(int offset, float[] vertexData, Float... data) {
		for (Float f : data) {
			vertexData[offset++] = f;
		}
	}
}
