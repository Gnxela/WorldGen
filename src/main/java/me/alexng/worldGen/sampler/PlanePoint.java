package me.alexng.worldGen.sampler;

import me.alexng.worldGen.FastNoiseLite;

public class PlanePoint implements Point {

	private final int x, y, index;

	public PlanePoint(int x, int y, int index) {
		this.x = x;
		this.y = y;
		this.index = index;
	}

	@Override
	public float sample(FastNoiseLite noise) {
		return noise.GetNoise(x, y);
	}

	@Override
	public int getIndex() {
		return index;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return index;
	}
}
