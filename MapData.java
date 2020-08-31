package me.alexng.untitled.generate;

import me.alexng.untitled.render.Texture;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class MapData {

	private final float[] data;
	private final int width, height;
	private float max = Float.MAX_VALUE, min = Float.MIN_VALUE;

	public MapData(int width, int height) {
		this.data = new float[width * height];
		this.width = width;
		this.height = height;
	}

	public Texture toTextureRGB(Texture.Type type) {
		MemoryStack stack = MemoryStack.stackGet();
		Texture texture = new Texture(type);
		ByteBuffer buffer = stack.malloc(data.length * 3);
		for (int i = 0; i < data.length; i++) {
			buffer.put((byte) 0xf);
			buffer.put((byte) 0xf);
			buffer.put((byte) 0xf);
		}
		buffer.flip();
		texture.load(width, height, buffer);
		return texture;
	}

	public void setData(float value, int x, int y) {
		data[y * width + x] = value;
	}

	public float getData(int x, int y) {
		return data[y * width + x];
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
