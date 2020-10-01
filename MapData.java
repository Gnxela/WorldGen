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

	public float getData(int i) {
		return data[i];
	}

	public float getData(int x, int y) {
		return data[y * width + x];
	}

	public float getDataNormalized(int i) {
		return (getData(i) - min) / (max - min);
	}

	public float getDataNormalized(int x, int y) {
		return (getData(x, y) - min) / (max - min);
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
