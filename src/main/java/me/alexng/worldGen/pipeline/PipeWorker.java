package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.sampler.Sampler;

public interface PipeWorker {
	void setup(int seed, Sampler sampler);
}
