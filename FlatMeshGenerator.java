package me.alexng.untitled.generate;

import me.alexng.untitled.render.Mesh;
import me.alexng.untitled.render.Texture;
import me.alexng.untitled.render.util.AttributeBuilder;
import org.joml.Vector3f;

import static me.alexng.untitled.render.UntitledConstants.FLOAT_WIDTH;
import static org.lwjgl.opengl.GL11.GL_FLOAT;

public class FlatMeshGenerator {

	private static final AttributeBuilder ATTRIBUTE_BUILDER = new AttributeBuilder(
			new AttributeBuilder.Attribute(0, 3, GL_FLOAT, false, 6 * FLOAT_WIDTH, 0),
			new AttributeBuilder.Attribute(1, 3, GL_FLOAT, false, 6 * FLOAT_WIDTH, 3 * FLOAT_WIDTH)
	);

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
			float h = 1f;
			for (int z = 0; z < numHeight; z++) {
				Vector3f vector = new Vector3f(x * dx, h, z * dz);
				h += dx / 10;
				h *= 1 + dx / 20;
				vectors[x * numWidth + z] = vector;
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
				indices[currentIndex++] = x * numWidth + z;
				indices[currentIndex++] = x * numWidth + (z + 1);
				indices[currentIndex++] = (x + 1) * numWidth + z;

				indices[currentIndex++] = x * numWidth + (z + 1);
				indices[currentIndex++] = (x + 1) * numWidth + (z + 1);
				indices[currentIndex++] = (x + 1) * numWidth + z;
			}
		}
		return new Mesh(indices, vertices, new Texture[]{}, ATTRIBUTE_BUILDER);
	}
}
