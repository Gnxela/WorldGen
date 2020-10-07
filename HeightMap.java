package me.alexng.untitled.generate;

import me.alexng.untitled.render.Texture;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class HeightMap extends MapData {

	private final LandmassMap landmassMap;

	public HeightMap(LandmassMap landmassMap) {
		super(landmassMap.getSampler());
		this.landmassMap = landmassMap;
	}

	@Override
	public void generate(int seed) {
		FastNoiseLite noise = NoiseHelper.getHeightMapNoise(seed);
		for (Sampler.Point point : getSampler().generatePoints()) {
			float landmass = landmassMap.getData(point);
			float height = noise.GetNoise(point.getX(), point.getY());
			// TODO: Maybe change definition to 0 is sea level.
			if (landmass == -1) {
				setData(landmass, point.getIndexX(), point.getIndexY());
			} else if (landmass == 1) {
				setData(height, point.getIndexX(), point.getIndexY());
			} else {
				setData(landmass * height, point.getIndexX(), point.getIndexY());
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
		if (normalizedHeight < 0.06) { // Deep water
			return new Vector3f(0, 0, 122);
		} else if (normalizedHeight < 0.15) { // Water
			return new Vector3f(25, 25, 150);
		} else if (normalizedHeight < 0.3) { // Sand
			return new Vector3f(240, 240, 64);
		} else if (normalizedHeight < 0.5) { // Grass
			return new Vector3f(50, 220, 20);
		} else if (normalizedHeight < 0.68) { // Dark grass
			return new Vector3f(16, 160, 0);
		} else if (normalizedHeight < 0.8) { // Stone
			return new Vector3f(122, 122, 122);
		} else { // Snow
			return new Vector3f(255, 255, 255);
		}
	}
}
