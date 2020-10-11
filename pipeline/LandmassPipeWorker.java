package me.alexng.untitled.generate.pipeline;

import me.alexng.untitled.generate.FastNoiseLite;
import me.alexng.untitled.generate.NoiseHelper;
import me.alexng.untitled.generate.Point;
import me.alexng.untitled.generate.Sampler;

/**
 * A map that outputs -1 for water, 1 for land and [-1, 1] in between (the 'shore').
 */
public class LandmassPipeWorker implements PipeWorker {

	// x < SHORE_START -> biome(x) == OCEAN. x > SHORE_END -> biome(x) != OCEAN
	private static final float SHORE_START = -0.2f;
	private static final float SHORE_WIDTH = 0.6f;
	private static final float SHORE_END = SHORE_START + SHORE_WIDTH;

	private FastNoiseLite noise;

	@Override
	public void setup(int seed, Sampler sampler) {
		noise = NoiseHelper.getLandmassNoise(seed);
	}

	@Override
	public float process(Point point, float... data) {
		float height = noise.GetNoise(point.getX(), point.getY());
		if (height > SHORE_END) {
			return 1;
		} else if (height < SHORE_START) {
			return -1;
		} else {
			return (height - SHORE_START) / SHORE_WIDTH * 2 - 1;
		}
	}
}
