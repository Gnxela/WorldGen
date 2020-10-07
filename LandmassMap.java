package me.alexng.untitled.generate;

import me.alexng.untitled.render.Texture;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class LandmassMap extends MapData {

	public LandmassMap(Sampler sampler) {
		super(sampler);
	}

	@Override
	public void generate(int seed) {
		final float maxHeight = 0.1f, minHeight = 0.0f;
		FastNoiseLite noise1 = NoiseHelper.getLandmassNoise(seed);
		for (Sampler.Point point : getSampler().generatePoints()) {
			float height = noise1.GetNoise(point.getX(), point.getY());
			if (height > maxHeight) {
				setData(1, point.getIndexX(), point.getIndexY());
			} else if (height < minHeight) {
				setData(-1, point.getIndexX(), point.getIndexY());
			} else {
				setData((height - minHeight) / (maxHeight - minHeight) * 2 - 1, point.getIndexX(), point.getIndexY());
			}
		}
	}

	@Override
	public MapData sample(Sampler sampler) {
		return null;
	}

	@Override
	public Texture toTextureRGB(Texture texture) {
		final int PIXEL_WIDTH = 3;
		ByteBuffer buffer = MemoryUtil.memAlloc(getSize() * PIXEL_WIDTH);
		for (int i = 0; i < getSize(); i++) {
			Vector3f color = toColor(i);
			buffer.put((byte) color.x);
			buffer.put((byte) color.y);
			buffer.put((byte) color.z);
		}
		buffer.flip();
		texture.load(getWidth(), getHeight(), buffer);
		buffer.clear();
		MemoryUtil.memFree(buffer);
		return texture;
	}

	@Override
	public Vector3f toColor(int i) {
		float normalizedHeight = getDataNormalized(i);
		return new Vector3f(255 * normalizedHeight);
	}
}
