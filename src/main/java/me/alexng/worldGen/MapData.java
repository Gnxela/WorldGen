package me.alexng.worldGen;

import me.alexng.worldGen.sampler.Point;

public class MapData {

	private final float[] data;
	private final Sampler sampler;

	public MapData(Sampler sampler) {
		this.sampler = sampler;
		this.data = new float[getSize()];
	}

	public void setData(float value, Point point) {
		setData(value, point.getIndexX(), point.getIndexY());
	}

	private void setData(float value, int x, int y) {
		data[getIndex(x, y)] = value;
	}

	int getIndex(int x, int y) {
		return y * getWidth() + x;
	}

	public float[] getRawData() {
		return data;
	}

	public float getData(int i) {
		return data[i];
	}

	public float getData(int x, int y) {
		return getData(getIndex(x, y));
	}

	public float getData(Point point) {
		return getData(getIndex(point.getIndexX(), point.getIndexY()));
	}

	/**
	 * Normalized to range (0, 1) from range (-1, 1).
	 */
	public float getDataNormalized(int i) {
		return NoiseHelper.normalize(getData(i));
	}

	public float getDataNormalized(int x, int y) {
		return getDataNormalized(getIndex(x, y));
	}

	public float getDataNormalized(Point point) {
		return getDataNormalized(getIndex(point.getIndexX(), point.getIndexY()));
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

	public int getSize() {
		return sampler.getSize();
	}
}
