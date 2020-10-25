package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.FastNoiseLite;
import me.alexng.worldGen.NoiseHelper;
import me.alexng.worldGen.Point;
import me.alexng.worldGen.Sampler;

/**
 * A map that outputs moisture in the range (-1, 1). Where -1 is dry and 1 is wet.
 */
public class MoisturePipeWorker implements PipeWorker {

	public static final float HEIGHT_POWER = 2;

	private FastNoiseLite noise;

	@Override
	public void setup(int seed, Sampler sampler) {
		noise = NoiseHelper.getMoistureNoise(seed);
	}

	@Override
	public float process(Point point, float... data) {
		float sample = noise.GetNoise(point.getX(), point.getY());
		float height = data[0];
		if (height <= 0) {
			sample += height * -8;
		} else {
			sample += sample * Math.pow(height, HEIGHT_POWER) + sample * Math.pow(1 - height, HEIGHT_POWER);
		}
		return NoiseHelper.clamp(sample);
	}
}