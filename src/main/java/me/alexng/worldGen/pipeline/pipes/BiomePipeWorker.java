package me.alexng.worldGen.pipeline.pipes;

import me.alexng.worldGen.Biome;
import me.alexng.worldGen.NoiseHelper;
import me.alexng.worldGen.pipeline.Consume;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

/**
 * A map that outputs biome type. Data for each cell is the biome ID, see {@link Biome}.
 * DO NOT NORMALIZE MAP OUTPUT.
 */
public class BiomePipeWorker implements PipeWorker {

	@Override
	public void setup(int seed, Sampler sampler) {
	}

	@Producer(name = "biome", stored = true)
	public float process(Point point, @Consume(name = "height") float height, @Consume(name = "temperature") float temperature, @Consume(name = "moisture") float moisture) {
		if (height <= 0) {
			return Biome.OCEAN.getId();
		} else {
			float temperatureNormalized = NoiseHelper.normalize(temperature);
			float moistureNormalized = NoiseHelper.normalize(moisture);
			int degrees = (int) (45 * temperatureNormalized - 10);
			int precipitation = (int) (400 * moistureNormalized);
			return classify(degrees, precipitation).getId();
		}
	}

	private Biome classify(int degrees, int precipitation) {
		if (degrees < 0) {
			if (precipitation < 100) {
				return Biome.TUNDRA;
			}
			return Biome.ICE;
		} else if (degrees < 7) {
			if (precipitation < 25) {
				return Biome.TEMPERATE_GRASSLAND;
			} else if (precipitation < 50) {
				return Biome.WOODLAND;
			} else if (precipitation < 200) {
				return Biome.BOREAL_FOREST;
			}
			return Biome.BOREAL_FOREST;
		} else if (degrees < 22) {
			if (precipitation < 50) {
				return Biome.TEMPERATE_GRASSLAND;
			} else if (precipitation < 100) {
				return Biome.WOODLAND;
			} else if (precipitation < 200) {
				return Biome.TEMPERATE_SEASONAL_RAINFOREST;
			} else if (precipitation < 300) {
				return Biome.TEMPERATE_RAINFOREST;
			}
			return Biome.TEMPERATE_RAINFOREST;
		} else {
			if (precipitation < 75) {
				return Biome.SUBTROPICAL_DESERT;
			} else if (precipitation < 250) {
				return Biome.SAVANNA;
			} else {
				return Biome.TROPICAL_RAINFOREST;
			}
		}
	}
}
