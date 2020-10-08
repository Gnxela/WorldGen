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
		SUBTROPICAL_DESERT(9, new Vector3f(238, 218, 130));

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
				setData(Biome.NONE.id, point);
			} else {
				setData(Biome.UNKNOWN.id, point);
			}
		}
	}

	@Override
	public MapData sample(Sampler sampler) {
		return null;
	}

	@Override
	public Vector3f toColor(int i) {
		int biomeId = (int) getData(i);
		return Biome.getBiomeFromId(biomeId).color;
	}
}
