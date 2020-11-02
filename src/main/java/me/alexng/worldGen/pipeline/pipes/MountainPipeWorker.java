package me.alexng.worldGen.pipeline.pipes;

import me.alexng.worldGen.FastNoiseLite;
import me.alexng.worldGen.NoiseHelper;
import me.alexng.worldGen.pipeline.Consumer;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

public class MountainPipeWorker implements PipeWorker {

	private static final float MOUNTAIN_NOISE_AMP = 0.57f;
	private static final float MOUNTAIN_FILTER_START = 0.6f;
	private static final float MOUNTAIN_FILTER_WIDTH = 0.2f;
	private static final float MOUNTAIN_FILTER_END = MOUNTAIN_FILTER_START + MOUNTAIN_FILTER_WIDTH;

	private FastNoiseLite mountainNoise, mountainFilterNoise;

	@Override
	public void setup(int seed, Sampler sampler) {
		mountainNoise = NoiseHelper.getMountainNoise(seed);
		mountainFilterNoise = NoiseHelper.getMountainFilterNoise(seed);
	}

	@Producer(name = "mountain")
	public float process(Point point, @Consumer(name = "landmass") float landmass) {
		float landmassNormalized = NoiseHelper.normalize(landmass); // [0, 1]
		float sampleNormalized = NoiseHelper.normalize(point.sample(mountainNoise)); // [0, 1]
		float filterSampleNormalized = NoiseHelper.normalize(point.sample(mountainFilterNoise)); // [0, 1]
		if (filterSampleNormalized > MOUNTAIN_FILTER_END) {
			filterSampleNormalized = 1;
		} else if (filterSampleNormalized < MOUNTAIN_FILTER_START) {
			filterSampleNormalized = 0;
		} else {
			filterSampleNormalized = (filterSampleNormalized - MOUNTAIN_FILTER_START) / MOUNTAIN_FILTER_WIDTH;
		}
		return NoiseHelper.stretch(((float) Math.pow(Math.E, sampleNormalized) - 1) * MOUNTAIN_NOISE_AMP * filterSampleNormalized * landmassNormalized); // [-1, 1]
	}
}
