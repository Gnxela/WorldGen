package me.alexng.untitled.generate;

import me.alexng.untitled.render.Texture;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class MapData {

	private final float[] data;
	private final int width, height;
	private float max = Float.MIN_VALUE, min = Float.MAX_VALUE;

	public MapData(int width, int height) {
		this.width = width;
		this.height = height;
		this.data = new float[getSize()];
	}

	public Texture toTextureRGB(Texture.Type type) {
		final int PIXEL_WIDTH = 3;
		MemoryStack stack = MemoryStack.stackGet();
		Texture texture = new Texture(type);
		ByteBuffer buffer = stack.malloc(data.length * PIXEL_WIDTH);
		for (int i = 0; i < data.length; i++) {
			buffer.put((byte) 255);
			buffer.put((byte) 0);
			buffer.put((byte) 0);
		}
		buffer.flip();
		texture.load(width, height, buffer);
		buffer.clear();
		return texture;
	}

	public void setData(float value, int x, int y) {
		if (value > max) {
			max = value;
		}
		if (value < min) {
			min = value;
		}
		data[y * width + x] = value;
	}

	int getIndex(int x, int y) {
		return y * width + x;
	}

	public float getData(int i) {
		return data[i];
	}

	public float getData(int x, int y) {
		return getData(getIndex(x, y));
	}

	/**
	 * Normalized to range (0, 1) from range (min, max).
	 */
	public float getDataNormalized(int i) {
		return (getData(i) - min) / (max - min);
	}

	public float getDataNormalized(int x, int y) {
		return getDataNormalized(getIndex(x, y));
	}

	/**
	 * Normalized to range (0, 1) from range (-1, 1).
	 */
	public float getDataNormalizedRange(int i) {
		return (getData(i) + 1) / 2;
	}

	public float getDataNormalizedRange(int x, int y) {
		return getDataNormalizedRange(getIndex(x, y));
	}

	public float getMax() {
		return max;
	}

	public float getMin() {
		return min;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getSize() {
		return width * height;
	}
}
