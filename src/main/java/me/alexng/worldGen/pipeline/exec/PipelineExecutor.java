package me.alexng.worldGen.pipeline.exec;

import me.alexng.worldGen.sampler.Sampler;

import java.util.Map;

public interface PipelineExecutor {
	// TODO: Do we want a specialised object?
	Map<String, float[]> execute(Sampler sampler);
}
