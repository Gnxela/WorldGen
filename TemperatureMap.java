package me.alexng.untitled.generate;

import org.joml.Vector3f;

import java.util.HashMap;

/**
 * A map that outputs temperature in the range (-1, 1). -1 being colder than 1.
 */
public class TemperatureMap extends MapData {

	// (0, 1). Percent of output that is noise before height pass.
	public static final float NOISE_STRENGTH = 0.2f;
	public static final float HEIGHT_POWER = 2;

	private final HeightMap heightMap;

	private FastNoiseLite noise;
	private HashMap<Integer, Float> gradientCache;

	public TemperatureMap(HeightMap heightMap) {
		super(heightMap.getSampler());
		this.heightMap = heightMap;
	}

	@Override
	public void setupGeneration(int seed) {
		noise = NoiseHelper.getTemperatureNoise(seed);
		gradientCache = new HashMap<>();
	}

	@Override
	public void generatePoint(Point point) {
		Float cachedGradient = gradientCache.get(point.getY());
		if (cachedGradient == null) {
			cachedGradient = calculateHeatGradient(point.getY());
			gradientCache.put(point.getY(), cachedGradient);
		}
		float latitudeTemp = cachedGradient; // (-1, 1)
		float sample = noise.GetNoise(point.getX(), point.getY()); // (-1, 1)
		float tempWithNoise = latitudeTemp * (1 - NOISE_STRENGTH) + sample * NOISE_STRENGTH; // (-1, 1)
		float height = Math.max(0, heightMap.getData(point)); // (0, 1)
		float scaledHeight = (float) Math.pow(height, HEIGHT_POWER); // (0, 1)
		float tempWithNoiseLessHeight = NoiseHelper.clamp(tempWithNoise - scaledHeight - (NoiseHelper.normalize(tempWithNoise) * scaledHeight * 0.5f));
		setData(tempWithNoiseLessHeight, point);
	}

	@Override
	public Vector3f toColor(int i) {
		float temp = getDataNormalized(i);
		return new Vector3f(temp * 255, 0, (1 - temp) * 255);
	}

	private float calculateHeatGradient(int y) {
		float normalizedY = y / (float) getSampler().getTotalHeight();
		float normalizedY2 = normalizedY * normalizedY;
		float normalizedY3 = normalizedY2 * normalizedY;
		float normalizedY4 = normalizedY3 * normalizedY;
		float temp = (float) (normalizedY4 * 14.955 + normalizedY3 * -29.911 + normalizedY2 * 14.6948 + normalizedY * 0.26);
		if (temp > 1) {
			temp = 1;
		}
		if (temp < 0) {
			temp = 0;
		}
		temp = (float) Math.pow(temp, 1.1);
		// Changing from (0, 1) to (-1, 1)
		return temp * 2 - 1;
	}
}
