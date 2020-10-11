package me.alexng.untitled.generate.pipeline;

import me.alexng.untitled.generate.Point;
import me.alexng.untitled.generate.Sampler;

public interface PipeWorker {
	void setup(int seed, Sampler sampler);

	float process(Point point, float... data);
}
