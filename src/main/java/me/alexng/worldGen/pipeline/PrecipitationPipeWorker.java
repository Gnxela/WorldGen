package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.Biome;
import me.alexng.worldGen.NoiseHelper;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

/**
 * A map that outputs precipitation in a range [-1, 1], 1 being more rainy than -1.
 */
public class PrecipitationPipeWorker implements PipeWorker {

	@Override
	public void setup(int seed, Sampler sampler) {
	}

	@Override
	public float process(Point point, float... data) {
		float height = data[0];
		if (height > 0) { // Land
			return 0;
		}
		return 0;
	}
}
