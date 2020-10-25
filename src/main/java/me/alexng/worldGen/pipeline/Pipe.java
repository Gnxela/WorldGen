package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.MapData;
import me.alexng.worldGen.Point;
import me.alexng.worldGen.Sampler;

public class Pipe {

	private final PipeWorker pipeWorker;
	private final boolean stored;

	private MapData storedData;

	public Pipe(PipeWorker pipeWorker, boolean stored) {
		this.pipeWorker = pipeWorker;
		this.stored = stored;
	}

	public void setup(int seed, Sampler sampler) {
		if (stored) {
			storedData = new MapData(sampler);
		}
		pipeWorker.setup(seed, sampler);
	}

	public float process(Point point, float... data) {
		float value = pipeWorker.process(point, data);
		if (stored) {
			storedData.setData(value, point);
		}
		return value;
	}

	public MapData getStoredData() {
		return storedData;
	}
}