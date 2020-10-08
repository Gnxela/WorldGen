package me.alexng.untitled.generate;

import org.joml.Vector3f;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A map that outputs biome type. Data for each cell is the biome ID, see {@link Biome}.
 * DO NOT NORMALIZE MAP OUTPUT.
 */
public class BiomeMap extends MapData {

	public enum Biome {
		OCEAN(-2, new Vector3f()),
		UNKNOWN(-1, new Vector3f(204, 0, 204)),
		NONE(0, new Vector3f(0, 0, 0)),
		TUNDRA(1, new Vector3f(96, 131, 112)),
		BOREAL_FOREST(2, new Vector3f(95, 115, 62)),
		TEMPERATE_RAINFOREST(3, new Vector3f(29, 73, 40)),
		TEMPERATE_SEASONAL_RAINFOREST(4, new Vector3f(73, 100, 35)),
		WOODLAND(5, new Vector3f(139, 175, 90)), // SHRUBLAND
		TEMPERATE_GRASSLAND(6, new Vector3f(164, 225, 99)), // COLD_DESERT
		TROPICAL_RAINFOREST(7, new Vector3f(66, 123, 25)),
		SAVANNA(8, new Vector3f(177, 209, 110)), // TROPICAL_SEASONAL_RAINFOREST
		SUBTROPICAL_DESERT(9, new Vector3f(238, 218, 130)),
		ICE(10, new Vector3f(255, 255, 255));

		private final int id;
		private final Vector3f color;

		private static final Map<Integer, Biome> biomeMap = Stream.of(Biome.values()).collect(Collectors.toMap(biome -> biome.id, biome -> biome));

		Biome(int id, Vector3f color) {
			this.id = id;
			this.color = color;
		}

		public static Biome getBiomeFromId(int id) {
			return biomeMap.getOrDefault(id, Biome.UNKNOWN);
		}
	}

	private final HeightMap heightMap;
	private final TemperatureMap temperatureMap;
	private final MoistureMap moistureMap;

	public BiomeMap(HeightMap heightMap, TemperatureMap temperatureMap, MoistureMap moistureMap) {
		super(temperatureMap.getSampler());
		this.heightMap = heightMap;
		this.temperatureMap = temperatureMap;
		this.moistureMap = moistureMap;
	}

	@Override
	public void generate(int seed) {
		for (Sampler.Point point : getSampler().generatePoints()) {
			if (heightMap.getData(point) <= 0) {
				setData(Biome.OCEAN.id, point);
			} else {
				float temperature = temperatureMap.getDataNormalized(point);
				float moisture = moistureMap.getDataNormalized(point);
				int degrees = (int) (45 * temperature - 10);
				int precipitation = (int) (400 * moisture);
				Biome biome = classify(degrees, precipitation);
				setData(biome.id, point);
			}
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

	@Override
	public float getDataNormalized(int i) {
		throw new RuntimeException("Unimplemented. See javadoc");
	}

	@Override
	public MapData sample(Sampler sampler) {
		return null;
	}

	@Override
	public Vector3f toColor(int i) {
		int biomeId = (int) getData(i);
		if (biomeId == Biome.OCEAN.id) {
			return heightMap.toColor(i);
		}
		return Biome.getBiomeFromId(biomeId).color;
	}
}
