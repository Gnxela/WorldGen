package me.alexng.untitled.generate;

import org.joml.Vector3f;

/**
 * A map that outputs moisture in the range (-1, 1). Where -1 is dry and 1 is wet.
 */
public class MoistureMap extends MapData {

	public static final float HEIGHT_POWER = 2;

	private final HeightMap heightMap;

	private FastNoiseLite noise;

	public MoistureMap(HeightMap heightMap) {
		super(heightMap.getSampler());
		this.heightMap = heightMap;
	}

	@Override
	public void setupGeneration(int seed) {
		noise = NoiseHelper.getMoistureNoise(seed);
	}

	@Override
	public void generatePoint(Point point) {
		float sample = noise.GetNoise(point.getX(), point.getY());
		float height = heightMap.getData(point);
		if (height <= 0) {
			sample += height * -8;
		} else {
			sample += sample * Math.pow(height, HEIGHT_POWER) + sample * Math.pow(1 - height, HEIGHT_POWER);
		}
		float clampedSample = NoiseHelper.clamp(sample);
		setData(clampedSample, point);
	}

	@Override
	public Vector3f toColor(int i) {
		float moisture = getDataNormalized(i);
		/*if (moisture < 0.27) {
			return new Vector3f(255, 139, 17);
		} else if (moisture < 0.4) {
			return new Vector3f(245, 245, 24);
		} else if (moisture < 0.6) {
			return new Vector3f(80, 255, 0);
		} else if (moisture < 0.8) {
			return new Vector3f(85, 255, 255);
		} else if (moisture < 0.9) {
			return new Vector3f(20, 70, 255);
		} else {
			return new Vector3f(0, 0, 100);
		} */
		return new Vector3f((1 - moisture) * 255, 0, moisture * 255);
	}
}
