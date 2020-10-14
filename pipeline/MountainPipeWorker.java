package me.alexng.untitled.generate.pipeline;

import me.alexng.untitled.generate.FastNoiseLite;
import me.alexng.untitled.generate.NoiseHelper;
import me.alexng.untitled.generate.Point;
import me.alexng.untitled.generate.Sampler;

public class MountainPipeWorker implements PipeWorker {

	private static final float MOUNTAIN_NOISE_AMP = 2;

	private FastNoiseLite noise;

	@Override
	public void setup(int seed, Sampler sampler) {
		noise = NoiseHelper.getMountainNoise(seed);
	}

	@Override
	public float process(Point point, float... data) {
		float landmassNormalized = NoiseHelper.normalize(data[0]); // [0, 1]
		float sampleNormalized = NoiseHelper.normalize(noise.GetNoise(point.getX(), point.getY())); // [0, 1]
		return NoiseHelper.stretch(sampleNormalized * sampleNormalized * sampleNormalized * MOUNTAIN_NOISE_AMP); // [-1, 1]
	}
}
