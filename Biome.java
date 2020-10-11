package me.alexng.untitled.generate;

import org.joml.Vector3f;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public int getId() {
		return id;
	}

	public Vector3f getColor() {
		return color;
	}

	public static Biome getBiomeFromId(int id) {
		return biomeMap.getOrDefault(id, Biome.UNKNOWN);
	}
}
