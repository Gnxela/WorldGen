package me.alexng.worldGen.pipeline.pipes;

import me.alexng.worldGen.FastNoiseLite;
import me.alexng.worldGen.NoiseHelper;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

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

	@Producer(name = "landmass")
	public float process(Point point) {
		float height = point.sample(noise);
		if (height > SHORE_END) {
			return 1;
		} else if (height < SHORE_START) {
			return -1;
		} else {
			return (height - SHORE_START) / SHORE_WIDTH * 2 - 1;
		}
	}
}
