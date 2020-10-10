package me.alexng.untitled.generate;

import org.joml.Vector3f;

/**
 * A map that outputs the base height. 0 is sea level. (-1, 0) is water, [0, 1) is land
 */
public class HeightMap extends MapData {

	private final LandmassMap landmassMap;

	private FastNoiseLite landNoise, oceanNoise;

	public HeightMap(LandmassMap landmassMap) {
		super(landmassMap.getSampler());
		this.landmassMap = landmassMap;
	}

	@Override
	public void setupGeneration(int seed) {
		landNoise = NoiseHelper.getLandHeightMapNoise(seed);
		oceanNoise = NoiseHelper.getOceanHeightMapNoise(seed);
	}

	@Override
	public void generatePoint(Point point) {
		float landmass = landmassMap.getDataNormalized(point);
		if (landmass == 0) { // Ocean
			float oceanDepth = NoiseHelper.normalize(oceanNoise.GetNoise(point.getX(), point.getY()));
			setData(-oceanDepth, point);
		} else if (landmass == 1) { // Land
			float height = NoiseHelper.normalize(landNoise.GetNoise(point.getX(), point.getY()));
			setData(height, point);
		} else { // 'Shore'
			float height = NoiseHelper.normalize(landNoise.GetNoise(point.getX(), point.getY()));
			float oceanDepth = NoiseHelper.normalize(oceanNoise.GetNoise(point.getX(), point.getY()));
			setData(landmass * height - (1 - landmass) * oceanDepth, point);
		}
	}

	@Override
	public Vector3f toColor(int i) {
		float height = getData(i);
		if (height <= 0) {
			return new Vector3f(0, 0, 255 * (1 + height));
		} else {
			final Vector3f lowColor = new Vector3f(60, 209, 90);
			final Vector3f middleColor = new Vector3f(251, 248, 80);
			final Vector3f highColor = new Vector3f(250, 49, 74);
			if (height < 0.5) {
				return middleColor.mul(height).mul(2).add(lowColor.mul(0.5f - height).mul(2));
			} else {
				return highColor.mul(height - 0.5f).mul(2).add(middleColor.mul(1 - height).mul(2));
			}
		}
		/*
		if (height <= 0) {
			return new Vector3f(0, 0, 255 * (1 + height));
		} else if (height < 0.15) { // Sand
			return new Vector3f(240, 240, 64);
		} else if (height < 0.3) { // Grass
			return new Vector3f(50, 220, 20);
		} else if (height < 0.65) { // Dark grass
			return new Vector3f(16, 160, 0);
		} else if (height < 0.8) { // Stone
			return new Vector3f(122, 122, 122);
		} else { // Snow
			return new Vector3f(255, 255, 255);
		}
		 */
	}
}
