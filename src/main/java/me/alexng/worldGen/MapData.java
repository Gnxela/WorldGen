package me.alexng.worldGen;

import me.alexng.worldGen.sampler.Point;

/**
 * A class that stores map data. The stored data can be from any {@link me.alexng.worldGen.pipeline.Pipe}'s output.
 */
public class MapData {

	private final float[] data;

	public MapData(int size) {
		this.data = new float[size];
	}

	public float[] getRawData() {
		return data;
	}

	public void setData(float value, Point point) {
		setData(value, point.getIndex());
	}

	public void setData(float value, int i) {
		data[i] = value;
	}

	public float getData(Point point) {
		return getData(point.getIndex());
	}

	public float getData(int i) {
		return data[i];
	}
}
