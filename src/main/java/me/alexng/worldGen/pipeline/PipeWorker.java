package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.sampler.Sampler;

public interface PipeWorker {
	// TODO: Now that this is a general pipeline library, we should have a more generalised setup method.
	void setup(int seed, Sampler sampler);
}
