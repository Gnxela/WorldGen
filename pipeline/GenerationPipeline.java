package me.alexng.untitled.generate.pipeline;

import me.alexng.untitled.generate.Point;
import me.alexng.untitled.generate.Sampler;

public class GenerationPipeline {

	private final Pipe landmassPipe = new Pipe(new LandmassPipeWorker(), true);
	private final Pipe heightPipe = new Pipe(new HeightPipeWorker(), true);
	private final Pipe temperaturePipe = new Pipe(new TemperaturePipeWorker(), true);
	private final Pipe moisturePipe = new Pipe(new MoisturePipeWorker(), true);
	private final Pipe biomePipe = new Pipe(new BiomePipeWorker(), true);
	private final Pipe mountainPipe = new Pipe(new MountainPipeWorker(), true);

	public void setup(int seed, Sampler sampler) {
		landmassPipe.setup(seed, sampler);
		heightPipe.setup(seed, sampler);
		temperaturePipe.setup(seed, sampler);
		moisturePipe.setup(seed, sampler);
		biomePipe.setup(seed, sampler);
		mountainPipe.setup(seed, sampler);
	}

	public void process(Point point) {
		float landmass = landmassPipe.process(point);
		float mountain = mountainPipe.process(point, landmass);
		float height = heightPipe.process(point, landmass, mountain);
		float temperature = temperaturePipe.process(point, height);
		float moisture = moisturePipe.process(point, height);
		biomePipe.process(point, height, temperature, moisture);
	}

	public Pipe getLandmassPipe() {
		return landmassPipe;
	}

	public Pipe getHeightPipe() {
		return heightPipe;
	}

	public Pipe getTemperaturePipe() {
		return temperaturePipe;
	}

	public Pipe getMoisturePipe() {
		return moisturePipe;
	}

	public Pipe getBiomePipe() {
		return biomePipe;
	}

	public Pipe getMountainPipe() {
		return mountainPipe;
	}
}
