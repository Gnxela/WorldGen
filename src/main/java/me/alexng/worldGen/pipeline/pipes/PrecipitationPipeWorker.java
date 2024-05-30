package me.alexng.worldGen.pipeline.pipes;

import me.alexng.worldGen.pipeline.Consume;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

/**
 * A map that outputs precipitation in a range [-1, 1], 1 being more rainy than -1.
 */
public class PrecipitationPipeWorker implements PipeWorker {

	@Override
	public void setup(int seed, Sampler sampler) {
	}

	@Producer(name = "precipitation")
	public float process(Point point, @Consume(name = "height", blocked = true) float[] height) {
		if (height[point.getIndex()] > 0) { // Land
			return 0;
		}
		return 0;
	}
}
