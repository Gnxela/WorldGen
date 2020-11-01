package me.alexng.worldGen;

import me.alexng.worldGen.pipeline.GenerationPipeline;
import me.alexng.worldGen.sampler.Sampler;


/**
 * @param <T> The type of sampler that is used in this class. Determines how our 3d noise is sampled.
 */
public class WorldMap<T extends Sampler> {

	private final T sampler;
	private final GenerationPipeline generationPipeline;

	public WorldMap(T sampler) {
		this.sampler = sampler;
		this.generationPipeline = new GenerationPipeline();
	}

	public void generate(int seed) {
		generationPipeline.setup(seed, sampler);
		generationPipeline.generatePoints(sampler.getPoints());
	}

	public GenerationPipeline getGenerationPipeline() {
		return generationPipeline;
	}

	public T getSampler() {
		return sampler;
	}
}
