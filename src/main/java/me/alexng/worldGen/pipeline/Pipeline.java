package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.pipeline.exec.Graph;
import me.alexng.worldGen.sampler.Sampler;

public class Pipeline {

	private final PipeWorker[] workers;
	private final Graph graph;

	public Pipeline(PipeWorker[] workers) {
		this.workers = workers;
		this.graph = new Graph();
		graph.createGraph(workers);
	}

	public void setup(int seed, Sampler sampler) {
		for (PipeWorker worker : workers) {
			worker.setup(seed, sampler);
		}
	}

	public Graph getGraph() {
		return graph;
	}
}
