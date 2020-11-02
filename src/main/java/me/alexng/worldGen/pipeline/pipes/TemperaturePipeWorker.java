package me.alexng.worldGen.pipeline.pipes;

import me.alexng.worldGen.FastNoiseLite;
import me.alexng.worldGen.NoiseHelper;
import me.alexng.worldGen.pipeline.Consumer;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
import me.alexng.worldGen.sampler.PlanePoint;
import me.alexng.worldGen.sampler.PlaneSampler;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

import java.util.HashMap;

/**
 * A map that outputs temperature in the range [-1, 1]. -1 being colder than 1.
 */
public class TemperaturePipeWorker implements PipeWorker {

	// [0, 1]. Percent of output that is noise before height pass.
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
		totalHeight = getTotalHeight(sampler);
	}

	@Producer(name = "temperature")
	public float process(Point point, @Consumer(name = "height") float height) {
		int y = getY(point);
		float latitudeTemp = gradientCache.computeIfAbsent(y, k -> calculateHeatGradient(y)); // [-1, 1]
		float sample = point.sample(noise); // [-1, 1]
		float tempWithNoise = latitudeTemp * (1 - NOISE_STRENGTH) + sample * NOISE_STRENGTH; // [-1, 1]
		float positiveHeight = Math.max(0, height); // [0, 1]
		float scaledHeight = (float) Math.pow(positiveHeight, HEIGHT_POWER) * HEIGHT_EXPONENT; // [0, 1]
		return NoiseHelper.clamp(tempWithNoise - scaledHeight - (NoiseHelper.normalize(tempWithNoise) * scaledHeight * 0.5f));
	}

	private float getTotalHeight(Sampler sampler) {
		if (sampler instanceof PlaneSampler) {
			return ((PlaneSampler) sampler).getTotalHeight();
		}
		throw new RuntimeException("Unknown sampler type");
	}

	private int getY(Point point) {
		if (point instanceof PlanePoint) {
			return ((PlanePoint) point).getY();
		}
		throw new RuntimeException("Unknown point type");
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
		return NoiseHelper.stretch(temp);
	}
}
