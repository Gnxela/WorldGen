package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.Point;
import me.alexng.worldGen.Sampler;

public interface PipeWorker {
	void setup(int seed, Sampler sampler);

	float process(Point point, float... data);
}
