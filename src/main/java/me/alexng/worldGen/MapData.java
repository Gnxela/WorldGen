package me.alexng.worldGen;

import me.alexng.worldGen.sampler.Point;

public class MapData {

	private final float[] data;

	public MapData(int size) {
		this.data = new float[size];
	}

	public void setData(float value, Point point) {
		data[point.getIndex()] = value;
	}

	public float[] getRawData() {
		return data;
	}

	public float getData(int i) {
		return data[i];
	}

	public float getData(Point point) {
		return getData(point.getIndex());
	}
}
