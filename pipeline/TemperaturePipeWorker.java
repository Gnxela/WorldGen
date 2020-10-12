package me.alexng.untitled.generate.pipeline;

import me.alexng.untitled.generate.FastNoiseLite;
import me.alexng.untitled.generate.NoiseHelper;
import me.alexng.untitled.generate.Point;
import me.alexng.untitled.generate.Sampler;

import java.util.HashMap;

/**
 * A map that outputs temperature in the range (-1, 1). -1 being colder than 1.
 */
public class TemperaturePipeWorker implements PipeWorker {

	// (0, 1). Percent of output that is noise before height pass.
	public static final float NOISE_STRENGTH = 0.2f;
	public static final float HEIGHT_POWER = 1.8f;
	public static final float HEIGHT_EXPONENT = 1.35f;

	private HashMap<Integer, Float> gradientCache;
	private FastNoiseLite noise;
	private float totalHeight;

	@Override
	public void setup(int seed, Sampler sampler) {
		gradientCache = new HashMap<>();
		noise = NoiseHelper.getTemperatureNoise(seed);
		totalHeight = sampler.getTotalHeight();
	}

	@Override
	public float process(Point point, float... data) {
		float latitudeTemp = gradientCache.computeIfAbsent(point.getY(), k -> calculateHeatGradient(point.getY())); // (-1, 1)
		float sample = noise.GetNoise(point.getX(), point.getY()); // (-1, 1)
		float tempWithNoise = latitudeTemp * (1 - NOISE_STRENGTH) + sample * NOISE_STRENGTH; // (-1, 1)
		float height = Math.max(0, data[0]); // (0, 1)
		float scaledHeight = (float) Math.pow(height, HEIGHT_POWER) * HEIGHT_EXPONENT; // (0, 1)
		return NoiseHelper.clamp(tempWithNoise - scaledHeight - (NoiseHelper.normalize(tempWithNoise) * scaledHeight * 0.5f));
	}

	private float calculateHeatGradient(int y) {
		float normalizedY = y / totalHeight;
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
