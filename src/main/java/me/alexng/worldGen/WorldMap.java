package me.alexng.worldGen;

import me.alexng.worldGen.pipeline.exec.NaivePipelineExecutor;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Pipeline;
import me.alexng.worldGen.pipeline.exec.PipelineExecutor;
import me.alexng.worldGen.pipeline.pipes.*;
import me.alexng.worldGen.sampler.Sampler;

import java.util.Map;


/**
 * @param <T> The type of sampler that is used in this class. Determines how our 3d noise is sampled.
 */
public class WorldMap<T extends Sampler> {

	private static final PipeWorker[] workers = new PipeWorker[]{
		new TemperaturePipeWorker(),
			new LandmassPipeWorker(),
			new CoriolisPipeWorder(),
			new MountainPipeWorker(),
			new HeightPipeWorker(),
			new MoisturePipeWorker(),
			new PrecipitationPipeWorker(),
			new BiomePipeWorker()
	};

	private final T sampler;
	private final Pipeline generationPipeline;
	private final PipelineExecutor pipelineExecutor;

	public WorldMap(T sampler) {
		this.sampler = sampler;
		// TODO: Allow out of order PipeWorkers? Construct DAG automatically.
		this.generationPipeline = new Pipeline(workers);
		// pipelineExecutor = new ThreadedPipelineExecutor(10, generationPipeline);
		pipelineExecutor = new NaivePipelineExecutor(generationPipeline);
	}

	public Map<String, float[]> generate(int seed) {
		generationPipeline.setup(seed, sampler);
		return pipelineExecutor.execute(sampler);
	}

	public T getSampler() {
		return sampler;
	}
}
