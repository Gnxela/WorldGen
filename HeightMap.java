package me.alexng.untitled.generate;

import me.alexng.untitled.render.Texture;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class HeightMap extends MapData {

	public HeightMap(int width, int height) {
		super(width, height);
		init();
	}

	private void init() {
		FastNoiseLite noise = NoiseHelper.getHeightMapNoise();
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				float height = noise.GetNoise(x, y);
				setData(height, x, y);
			}
		}
	}

	@Override
	public Texture toTextureRGB(Texture.Type type) {
		final int PIXEL_WIDTH = 3;
		Texture texture = new Texture(type);
		ByteBuffer buffer = MemoryUtil.memAlloc(getSize() * PIXEL_WIDTH);
		for (int i = 0; i < getSize(); i++) {
			float normalizedValue = getDataNormalized(i);
			if (normalizedValue < 0.2) {
				buffer.put((byte) 0);
				buffer.put((byte) 0);
				buffer.put((byte) 122);
			} else if (normalizedValue < 0.4) {
				buffer.put((byte) 25);
				buffer.put((byte) 25);
				buffer.put((byte) 150);
			} else if (normalizedValue < 0.5) {
				buffer.put((byte) 240);
				buffer.put((byte) 240);
				buffer.put((byte) 64);
			} else if (normalizedValue < 0.7) {
				buffer.put((byte) 50);
				buffer.put((byte) 220);
				buffer.put((byte) 20);
			} else if (normalizedValue < 0.8) {
				buffer.put((byte) 16);
				buffer.put((byte) 160);
				buffer.put((byte) 0);
			} else if (normalizedValue < 0.9) {
				buffer.put((byte) 122);
				buffer.put((byte) 122);
				buffer.put((byte) 122);
			} else {
				buffer.put((byte) 255);
				buffer.put((byte) 255);
				buffer.put((byte) 255);
			}
		}
		buffer.flip();
		texture.load(getWidth(), getHeight(), buffer);
		buffer.clear();
		return texture;
	}
}
