package me.alexng.untitled.generate;

import me.alexng.untitled.render.Mesh;
import me.alexng.untitled.render.Texture;
import me.alexng.untitled.render.util.AttributeStore;
import org.joml.Vector3f;

public class TerrainGenerator {

	/**
	 * Generates a mesh that is  flat on the y axis.
	 *
	 * @param width     The width of the generated mesh
	 * @param length    The height of the generated mesh
	 * @param numWidth  The number of vertices across the width of the mesh
	 * @param numHeight The number of vertices across the height of the mesh
	 */
	public static Mesh generateFlatMesh(float width, float length, int numWidth, int numHeight) {
		return generateMesh(width, length, numWidth, numHeight, (x, y) -> 0, (x, y) -> new Vector3f(25, 150, 25));
	}

	/**
	 * Generates a mesh that is  flat on the y axis.
	 *
	 * @param width     The width of the generated mesh
	 * @param length    The height of the generated mesh
	 * @param numWidth  The number of vertices across the width of the mesh
	 * @param numHeight The number of vertices across the height of the mesh
	 */
	public static Mesh generateMeshFromHeightMap(float width, float length, int numWidth, int numHeight, float amplification, HeightMap heightMap) {
		return generateMesh(width, length, numWidth, numHeight, (x, y) -> amplification * heightMap.getDataNormalized(x, y), (x1, y1) -> HeightMap.heightToColor(heightMap.getDataNormalized(x1, y1)));
	}

	private static Mesh generateMesh(float width, float length, int numWidth, int numHeight, FloatMapper heightMapper, Vec3fMapper colorMapper) {
		if (width <= 0 || length <= 0 || numWidth < 2 || numHeight < 2 || heightMapper == null || colorMapper == null) {
			throw new IllegalArgumentException("Invalid arguments generateMesh()");
		}
		final int stride = 9;
		final float dx = width / (numWidth - 1);
		final float dz = length / (numHeight - 1);

		Vector3f[] vectors = new Vector3f[numWidth * numHeight];
		Vector3f[] colors = new Vector3f[numWidth * numHeight];
		for (int x = 0; x < numWidth; x++) {
			for (int z = 0; z < numHeight; z++) {
				Vector3f vector = new Vector3f(x * dx, heightMapper.getValue(x, z), z * dz);
				vectors[x * numHeight + z] = vector;
				Vector3f color = colorMapper.getValue(x, z);
				colors[x * numHeight + z] = color;
			}
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

		float[] vertices = new float[vectors.length * stride];
		int offset = 0;
		Vector3f normal = new Vector3f();
		for (int i = 0, vectorsLength = vectors.length; i < vectorsLength; i++) {
			Vector3f vector = vectors[i];
			Vector3f color = colors[i];
			vertices[offset + 0] = vector.x;
			vertices[offset + 1] = vector.y;
			vertices[offset + 2] = vector.z;
			vertices[offset + 3] = normal.x;
			vertices[offset + 4] = normal.y;
			vertices[offset + 5] = normal.z;
			vertices[offset + 6] = color.x / 255;
			vertices[offset + 7] = color.y / 255;
			vertices[offset + 8] = color.z / 255;
			offset += stride;
		}
		return new Mesh(indices, vertices, new Texture[]{}, AttributeStore.VEC3F_VEC3F_VEC3F);
	}

	interface FloatMapper {
		float getValue(int x, int y);
	}

	interface Vec3fMapper {
		Vector3f getValue(int x, int y);
	}
}
