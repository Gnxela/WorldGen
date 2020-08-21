package me.alexng.untitled.generate;

import me.alexng.untitled.render.Mesh;
import me.alexng.untitled.render.Texture;
import me.alexng.untitled.render.util.AttributeStore;
import org.joml.Vector3f;

public class FlatMeshGenerator {

	/**
	 * Generates a mesh that is  flat on the y axis.
	 *
	 * @param width     The width of the generated mesh
	 * @param height    The height of the generated mesh
	 * @param numWidth  The number of vertices across the width of the mesh
	 * @param numHeight The number of vertices across the height of the mesh
	 */
	public static Mesh generateFlatMesh(float width, float height, int numWidth, int numHeight) {
		if (width <= 0 || height <= 0 || numWidth < 2 || numHeight < 2) {
			throw new IllegalArgumentException("Invalid arguments generateFlatMesh()");
		}
		final int stride = 6;
		final float dx = width / (numWidth - 1);
		final float dz = height / (numHeight - 1);

		Vector3f[] vectors = new Vector3f[numWidth * numHeight];
		for (int x = 0; x < numWidth; x++) {
			for (int z = 0; z < numHeight; z++) {
				Vector3f vector = new Vector3f(x * dx, 0, z * dz);
				vectors[x * numHeight + z] = vector;
			}
		}

		float[] vertices = new float[vectors.length * stride];
		int offset = 0;
		for (Vector3f vector : vectors) {
			offset = PrimitiveGenerator.insertTriangleVertexData(offset, vertices, vector);
		}

		int numTriangles = (numWidth - 1) * (numHeight - 1) * 2;
		int[] indices = new int[numTriangles * 3];
		int currentIndex = 0;
		for (int x = 0; x < numWidth - 1; x++) {
			for (int z = 0; z < numHeight - 1; z++) {
				indices[currentIndex++] = x * numHeight + z;
				indices[currentIndex++] = x * numHeight + (z + 1);
				indices[currentIndex++] = (x + 1) * numHeight + z;

				indices[currentIndex++] = x * numHeight + (z + 1);
				indices[currentIndex++] = (x + 1) * numHeight + (z + 1);
				indices[currentIndex++] = (x + 1) * numHeight + z;
			}
		}
		return new Mesh(indices, vertices, new Texture[]{}, AttributeStore.VEC3F_VEC3F);
	}
}
