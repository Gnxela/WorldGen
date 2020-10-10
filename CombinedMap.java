package me.alexng.untitled.generate;

import java.util.Iterator;

public class CombinedMap {

	private final Sampler sampler;
	private final LandmassMap landmassMap;
	private final HeightMap heightMap;
	private final TemperatureMap temperatureMap;
	private final MoistureMap moistureMap;
	private final BiomeMap biomeMap;

	public CombinedMap(Sampler sampler) {
		this.sampler = sampler;
		this.landmassMap = new LandmassMap(sampler);
		this.heightMap = new HeightMap(landmassMap);
		this.temperatureMap = new TemperatureMap(heightMap);
		this.moistureMap = new MoistureMap(heightMap);
		this.biomeMap = new BiomeMap(heightMap, temperatureMap, moistureMap);
	}

	public void generate(int seed) {
		landmassMap.setupGeneration(seed);
		heightMap.setupGeneration(seed);
		temperatureMap.setupGeneration(seed);
		moistureMap.setupGeneration(seed);
		biomeMap.setupGeneration(seed);

		Iterator<Point> iterator = sampler.getPoints();
		while (iterator.hasNext()) {
			Point point = iterator.next();
			landmassMap.generatePoint(point);
			heightMap.generatePoint(point);
			temperatureMap.generatePoint(point);
			moistureMap.generatePoint(point);
			biomeMap.generatePoint(point);
		}
	}

	public CombinedMap sample(int x, int y, int width, int height, int numPointsX, int numPointsY) {
		return new CombinedMap(new Sampler(x, y, width, height, numPointsX, numPointsY, getSampler().getTotalWidth(), getSampler().getTotalHeight()));
	}

	public CombinedMap sample(int x, int y, int width, int height) {
		return new CombinedMap(new Sampler(x, y, width, height, width, height, getSampler().getTotalWidth(), getSampler().getTotalHeight()));
	}

	public CombinedMap sample(int numPointsX, int numPointsY) {
		return new CombinedMap(new Sampler(0, 0, getWidth(), getHeight(), numPointsX, numPointsY, getSampler().getTotalWidth(), getSampler().getTotalHeight()));
	}

	public Sampler getSampler() {
		return sampler;
	}

	public LandmassMap getLandmassMap() {
		return landmassMap;
	}

	public HeightMap getHeightMap() {
		return heightMap;
	}

	public TemperatureMap getTemperatureMap() {
		return temperatureMap;
	}

	public MoistureMap getMoistureMap() {
		return moistureMap;
	}

	public BiomeMap getBiomeMap() {
		return biomeMap;
	}

	public int getWidth() {
		return sampler.getNumPointsX();
	}

	public int getHeight() {
		return sampler.getNumPointsY();
	}
}
