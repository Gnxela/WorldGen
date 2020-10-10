package me.alexng.untitled.generate;

import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A map that outputs biome type. Data for each cell is the biome ID, see {@link Biome}.
 * DO NOT NORMALIZE MAP OUTPUT.
 */
public class BiomeMap extends MapData {

	public static final int MICRO_BIOME_THRESHOLD = 110;

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
	public void setupGeneration(int seed) {
	}

	@Override
	public void generatePoint(Point point) {
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

	private void floodSearch(List<Point> points) {
		HashMap<Integer, Point> unvisitedPoints = new HashMap<>(points.size());
		points.forEach(p -> unvisitedPoints.put(p.hashCode(), p));
		while (unvisitedPoints.size() > 0) {
			Point point = unvisitedPoints.values().iterator().next();
			int biomeId = (int) getData(point.getIndexX(), point.getIndexY());
			int num = floodSearchPoint(point, biomeId, unvisitedPoints);
			if (num < MICRO_BIOME_THRESHOLD) {
				floodSearchSurroundedPoint(point, biomeId);
			}
		}
	}

	private int floodSearchPoint(Point startingPoint, int originalBiomeId, HashMap<Integer, Point> unvisitedPoints) {
		Queue<Point> fringe = new LinkedList<>();
		fringe.add(startingPoint);
		int n = 0;
		while (fringe.size() > 0) {
			Point point = fringe.remove();

			if (!unvisitedPoints.containsKey(point.hashCode())) {
				continue;
			}

			// TODO: To get biome border remove from unvisitedPoints here

			if (!getSampler().containsIndex(point)) {
				continue;
			}
			int biomeId = (int) getData(point);
			if (biomeId != originalBiomeId) {
				continue;
			}
			unvisitedPoints.remove(point.hashCode());
			n++;
			fringe.add(point.up());
			fringe.add(point.down());
			fringe.add(point.left());
			fringe.add(point.right());
		}
		return n;
	}

	private void floodSearchSurroundedPoint(Point startingPoint, int originalBiomeId) {
		Set<Point> visitedPoints = new HashSet<>();
		Queue<Point> fringe = new LinkedList<>();
		fringe.add(startingPoint);
		while (fringe.size() > 0) {
			Point point = fringe.remove();
			if (visitedPoints.contains(point) || !getSampler().containsIndex(point)) {
				continue;
			}
			visitedPoints.add(point);
			int biomeId = (int) getData(point);
			if (biomeId != originalBiomeId) {
				continue;
			}
			setData(Biome.UNKNOWN.id, point);
			fringe.add(point.up());
			fringe.add(point.down());
			fringe.add(point.left());
			fringe.add(point.right());
		}
	}

	@Override
	public Vector3f toColor(int i) {
		int biomeId = (int) getData(i);
		if (biomeId == Biome.OCEAN.id) {
			return heightMap.toColor(i);
		}
		return Biome.getBiomeFromId(biomeId).color;
	}

	@Override
	public float getDataNormalized(int i) {
		throw new RuntimeException("Unimplemented. See javadoc");
	}
}
