package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.FastNoiseLite;
import me.alexng.worldGen.NoiseHelper;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

/**
 * A map that outputs the base height. 0 is sea level. (-1, 0) is water, [0, 1) is land
 */
public class HeightPipeWorker implements PipeWorker {

	private static final float MOUNTAIN_WEIGHT = 0.5f;

	private FastNoiseLite landNoise, oceanNoise;

	@Override
	public void setup(int seed, Sampler sampler) {
		landNoise = NoiseHelper.getLandHeightMapNoise(seed);
		oceanNoise = NoiseHelper.getOceanHeightMapNoise(seed);
	}

	@Override
	public float process(Point point, float... data) {
		float landmassNormalized = NoiseHelper.normalize(data[0]);
		float mountainNormalized = NoiseHelper.normalize(data[1]);
		if (landmassNormalized == 0) { // Ocean
			return -getOceanDepth(point);
		} else if (landmassNormalized == 1) { // Land
			return getLandHeight(point, mountainNormalized);
		} else { // 'Shore'
			float height = getLandHeight(point, mountainNormalized);
			float oceanDepth = getOceanDepth(point);
			return landmassNormalized * height - (1 - landmassNormalized) * oceanDepth;
		}
	}

	/**
	 * Returns [0, 1]. 1 being deeper
	 */
	private float getOceanDepth(Point point) {
		return NoiseHelper.normalize(point.sample(oceanNoise));
	}

	/**
	 * Returns [0, 1], 1 being higher.
	 */
	private float getLandHeight(Point point, float mountainNormalized) {
		float sampleNormalized = NoiseHelper.normalize(point.sample(landNoise));
		return sampleNormalized * (1 - MOUNTAIN_WEIGHT) + mountainNormalized * MOUNTAIN_WEIGHT;
	}
}
