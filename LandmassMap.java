package me.alexng.untitled.generate;

import org.joml.Vector3f;

/**
 * A map that outputs -1 for water, 1 for land and [-1, 1] in between (the 'shore').
 */
public class LandmassMap extends MapData {

	// x < SHORE_START -> biome(x) == OCEAN. x > SHORE_END -> biome(x) != OCEAN
	private static final float SHORE_START = -0.2f;
	private static final float SHORE_WIDTH = 0.6f;
	private static final float SHORE_END = SHORE_START + SHORE_WIDTH;

	private FastNoiseLite noise;

	public LandmassMap(Sampler sampler) {
		super(sampler);
	}

	@Override
	public void setupGeneration(int seed) {
		noise = NoiseHelper.getLandmassNoise(seed);
	}

	@Override
	public void generatePoint(Point point) {
		float height = noise.GetNoise(point.getX(), point.getY());
		if (height > SHORE_END) {
			setData(1, point.getIndexX(), point.getIndexY());
		} else if (height < SHORE_START) {
			setData(-1, point.getIndexX(), point.getIndexY());
		} else {
			setData((height - SHORE_START) / SHORE_WIDTH * 2 - 1, point.getIndexX(), point.getIndexY());
		}
	}

	@Override
	public Vector3f toColor(int i) {
		float normalizedHeight = getDataNormalized(i);
		return new Vector3f(255 * normalizedHeight);
	}
}
