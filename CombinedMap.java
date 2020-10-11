package me.alexng.untitled.generate;

import me.alexng.untitled.generate.pipeline.GenerationPipeline;

import java.util.Iterator;

public class CombinedMap {

	private final Sampler sampler;
	private final GenerationPipeline generationPipeline;

	public CombinedMap(Sampler sampler) {
		this.sampler = sampler;
		this.generationPipeline = new GenerationPipeline();
	}

	public void generate(int seed) {
		generationPipeline.setup(seed, sampler);
		Iterator<Point> iterator = sampler.getPoints();
		while (iterator.hasNext()) {
			generationPipeline.process(iterator.next());
		}
	}

	public CombinedMap sample(int x, int y, int width, int height, int numPointsX, int numPointsY) {
		return new CombinedMap(new Sampler(x, y, width, height, numPointsX, numPointsY, getSampler().getTotalWidth(), getSampler().getTotalHeight()));
	}

	public CombinedMap sample(int x, int y, int width, int height) {
		return new CombinedMap(new Sampler(x, y, width, height, width, height, getSampler().getTotalWidth(), getSampler().getTotalHeight()));
	}

	public CombinedMap sample(int numPointsX, int numPointsY) {
		return new CombinedMap(new Sampler(0, 0, getWidth(), getHeight(), numPointsX, numPointsY, getSampler().getTotalWidth(), getSampler().getTotalHeight()));
	}

	public GenerationPipeline getGenerationPipeline() {
		return generationPipeline;
	}

	public Sampler getSampler() {
		return sampler;
	}

	public int getWidth() {
		return sampler.getNumPointsX();
	}

	public int getHeight() {
		return sampler.getNumPointsY();
	}
}
