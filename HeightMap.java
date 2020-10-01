package me.alexng.untitled.generate;

import me.alexng.untitled.render.Texture;
import org.joml.Vector3f;
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
			Vector3f color = HeightMap.heightToColor(normalizedValue);
			buffer.put((byte) color.x);
			buffer.put((byte) color.y);
			buffer.put((byte) color.z);
		}
		buffer.flip();
		texture.load(getWidth(), getHeight(), buffer);
		buffer.clear();
		return texture;
	}

	public static Vector3f heightToColor(float normalizedHeight) {
		if (normalizedHeight < 0.2) {
			return new Vector3f(0, 0, 122);
		} else if (normalizedHeight < 0.4) {
			return new Vector3f(25, 25, 150);
		} else if (normalizedHeight < 0.5) {
			return new Vector3f(240, 240, 64);
		} else if (normalizedHeight < 0.7) {
			return new Vector3f(50, 220, 20);
		} else if (normalizedHeight < 0.8) {
			return new Vector3f(16, 160, 0);
		} else if (normalizedHeight < 0.9) {
			return new Vector3f(122, 122, 122);
		} else {
			return new Vector3f(255, 255, 255);
		}
	}
}
