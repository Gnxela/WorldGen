package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.Biome;
import me.alexng.worldGen.NoiseHelper;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;
import org.joml.Vector3f;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A map that outputs biome type. Data for each cell is the biome ID, see {@link Biome}.
 * DO NOT NORMALIZE MAP OUTPUT.
 */
public class BiomePipeWorker implements PipeWorker {

	@Override
	public void setup(int seed, Sampler sampler) {
	}

	@Override
	public float process(Point point, float... data) {
		if (data[0] <= 0) {
			return Biome.OCEAN.getId();
		} else {
			float temperature = NoiseHelper.normalize(data[1]);
			float moisture = NoiseHelper.normalize(data[2]);
			int degrees = (int) (45 * temperature - 10);
			int precipitation = (int) (400 * moisture);
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
