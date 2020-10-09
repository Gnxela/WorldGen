package me.alexng.untitled.generate;

import org.joml.Vector3f;

/**
 * A map that outputs the base height. 0 is sea level. (-1, 0) is water, [0, 1) is land
 */
public class HeightMap extends MapData {

	private final LandmassMap landmassMap;

	public HeightMap(LandmassMap landmassMap) {
		super(landmassMap.getSampler());
		this.landmassMap = landmassMap;
	}

	@Override
	public void generate(int seed) {
		FastNoiseLite landNoise = NoiseHelper.getLandHeightMapNoise(seed);
		FastNoiseLite oceanNoise = NoiseHelper.getOceanHeightMapNoise(seed);
		for (Point point : getSampler().generatePoints()) {
			float landmass = landmassMap.getDataNormalized(point);
			if (landmass == 0) { // Ocean
				float oceanDepth = NoiseHelper.normalize(oceanNoise.GetNoise(point.getX(), point.getY()));
				setData(-oceanDepth, point.getIndexX(), point.getIndexY());
			} else if (landmass == 1) { // Land
				float height = NoiseHelper.normalize(landNoise.GetNoise(point.getX(), point.getY()));
				setData(height, point.getIndexX(), point.getIndexY());
			} else { // 'Shore'
				float height = NoiseHelper.normalize(landNoise.GetNoise(point.getX(), point.getY()));
				float oceanDepth = NoiseHelper.normalize(oceanNoise.GetNoise(point.getX(), point.getY()));
				setData(landmass * height - (1 - landmass) * oceanDepth, point.getIndexX(), point.getIndexY());
			}
		}
	}

	@Override
	public MapData sample(Sampler sampler) {
		return null;
	}

	@Override
	public Vector3f toColor(int i) {
		float height = getData(i);
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
	}
}
