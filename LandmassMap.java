package me.alexng.untitled.generate;

import org.joml.Vector3f;

/**
 * A map that outputs -1 for water, 1 for land and [-1, 1] in between (the 'shore').
 */
public class LandmassMap extends MapData {

	public LandmassMap(Sampler sampler) {
		super(sampler);
	}

	@Override
	public void generate(int seed) {
		final float maxHeight = 0.2f, minHeight = 0.0f;
		FastNoiseLite noise1 = NoiseHelper.getLandmassNoise(seed);
		for (Point point : getSampler().generatePoints()) {
			float height = noise1.GetNoise(point.getX(), point.getY());
			if (height > maxHeight) {
				setData(1, point.getIndexX(), point.getIndexY());
			} else if (height < minHeight) {
				setData(-1, point.getIndexX(), point.getIndexY());
			} else {
				setData((height - minHeight) / (maxHeight - minHeight) * 2 - 1, point.getIndexX(), point.getIndexY());
			}
		}
	}

	@Override
	public MapData sample(Sampler sampler) {
		return null;
	}

	@Override
	public Vector3f toColor(int i) {
		float normalizedHeight = getDataNormalized(i);
		return new Vector3f(255 * normalizedHeight);
	}
}
