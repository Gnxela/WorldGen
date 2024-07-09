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
	public Float process(Point point, @Consume(name = "height", blocked = true) Float[] height) {
		if (height[point.getIndex()] > 0) { // Land
			return 0f;
		}
		return 0f;
	}
}
