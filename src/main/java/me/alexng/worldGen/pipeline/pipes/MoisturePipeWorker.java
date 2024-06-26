package me.alexng.worldGen.pipeline.pipes;

import me.alexng.worldGen.FastNoiseLite;
import me.alexng.worldGen.NoiseHelper;
import me.alexng.worldGen.pipeline.Consume;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

/**
 * A map that outputs moisture in the range [-1, 1]. Where -1 is dry and 1 is wet.
 */
public class MoisturePipeWorker implements PipeWorker {

	public static final float HEIGHT_POWER = 2;

	private FastNoiseLite noise;

	@Override
	public void setup(int seed, Sampler sampler) {
		noise = NoiseHelper.getMoistureNoise(seed);
	}

	@Producer(name = "moisture")
	public float process(Point point, @Consume(name = "height") float height) {
		float sample = point.sample(noise);
		if (height <= 0) {
			sample += height * -8;
		} else {
			sample += sample * Math.pow(height, HEIGHT_POWER) + sample * Math.pow(1 - height, HEIGHT_POWER);
		}
		return NoiseHelper.clamp(sample);
	}
}
