package me.alexng.untitled.generate.pipeline;

import me.alexng.untitled.generate.FastNoiseLite;
import me.alexng.untitled.generate.NoiseHelper;
import me.alexng.untitled.generate.Point;
import me.alexng.untitled.generate.Sampler;

/**
 * A map that outputs the base height. 0 is sea level. (-1, 0) is water, [0, 1) is land
 */
public class HeightPipeWorker implements PipeWorker {

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
		return NoiseHelper.normalize(oceanNoise.GetNoise(point.getX(), point.getY()));
	}

	/**
	 * Returns [0, 1], 1 being higher.
	 */
	private float getLandHeight(Point point, float mountainNormalized) {
		float sampleNormalized = NoiseHelper.normalize(landNoise.GetNoise(point.getX(), point.getY()));
		return sampleNormalized * 0.5f + mountainNormalized * 0.5f;
	}
}
