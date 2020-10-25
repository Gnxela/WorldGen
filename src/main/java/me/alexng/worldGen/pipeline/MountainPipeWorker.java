package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.FastNoiseLite;
import me.alexng.worldGen.NoiseHelper;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

public class MountainPipeWorker implements PipeWorker {

	private static final float MOUNTAIN_NOISE_AMP = 0.57f;
	private static final float MOUNTAIN_FILTER_START = 0.6f;
	private static final float MOUNTAIN_FILTER_WIDTH = 0.2f;
	private static final float MOUNTAIN_FILTER_END = MOUNTAIN_FILTER_START + MOUNTAIN_FILTER_WIDTH;

	private FastNoiseLite mountainNoise, mountainNoise2, mountainFilterNoise;

	@Override
	public void setup(int seed, Sampler sampler) {
		mountainNoise = NoiseHelper.getMountainNoise(seed);
		mountainFilterNoise = NoiseHelper.getMountainFilterNoise(seed);
	}

	@Override
	public float process(Point point, float... data) {
		float landmassNormalized = NoiseHelper.normalize(data[0]); // [0, 1]
		float sampleNormalized = NoiseHelper.normalize(mountainNoise.GetNoise(point.getX(), point.getY())); // [0, 1]
		float filterSampleNormalized = NoiseHelper.normalize(mountainFilterNoise.GetNoise(point.getX(), point.getY())); // [0, 1]
		if (filterSampleNormalized > MOUNTAIN_FILTER_END) {
			filterSampleNormalized = 1;
		} else if (filterSampleNormalized < MOUNTAIN_FILTER_START) {
			filterSampleNormalized = 0;
		} else {
			filterSampleNormalized = (filterSampleNormalized - MOUNTAIN_FILTER_START) / MOUNTAIN_FILTER_WIDTH ;
		}
		return NoiseHelper.stretch(((float) Math.pow(Math.E, sampleNormalized) - 1) * MOUNTAIN_NOISE_AMP * filterSampleNormalized * landmassNormalized); // [-1, 1]
	}
}
