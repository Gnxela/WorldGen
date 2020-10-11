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
		float landmass = NoiseHelper.normalize(data[0]);
		if (landmass == 0) { // Ocean
			return -NoiseHelper.normalize(oceanNoise.GetNoise(point.getX(), point.getY()));
		} else if (landmass == 1) { // Land
			return NoiseHelper.normalize(landNoise.GetNoise(point.getX(), point.getY()));
		} else { // 'Shore'
			float height = NoiseHelper.normalize(landNoise.GetNoise(point.getX(), point.getY()));
			float oceanDepth = NoiseHelper.normalize(oceanNoise.GetNoise(point.getX(), point.getY()));
			return landmass * height - (1 - landmass) * oceanDepth;
		}
	}
}
