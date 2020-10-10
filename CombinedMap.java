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

		Stopwatch total = new Stopwatch(), landmass = new Stopwatch(), height = new Stopwatch(), temp = new Stopwatch(), moisture = new Stopwatch(), biome = new Stopwatch();

		total.start();
		Iterator<Point> iterator = sampler.getPoints();
		while (iterator.hasNext()) {
			Point point = iterator.next();
			landmass.start();
			landmassMap.generatePoint(point);
			landmass.stop();
			height.start();
			heightMap.generatePoint(point);
			height.stop();
			temp.start();
			temperatureMap.generatePoint(point);
			temp.stop();
			moisture.start();
			moistureMap.generatePoint(point);
			moisture.stop();
			biome.start();
			biomeMap.generatePoint(point);
			biome.stop();
		}
		total.stop();
		System.out.println("Total points generated: " + getSampler().getSize());
		System.out.println("ns per point: " + total.getElapsedTime() / (float) sampler.getSize());
		System.out.println("Total: " + total.getElapsedSeconds() + "(100%)");
		System.out.println("Landmass: " + landmass.getElapsedSeconds() + "(" + landmass.getElapsedSeconds() / total.getElapsedSeconds() * 100 + "%)");
		System.out.println("Height: " + height.getElapsedSeconds() + "(" + height.getElapsedSeconds() / total.getElapsedSeconds() * 100 + "%)");
		System.out.println("Temperature: " + temp.getElapsedSeconds() + "(" + temp.getElapsedSeconds() / total.getElapsedSeconds() * 100 + "%)");
		System.out.println("Moisture: " + moisture.getElapsedSeconds() + "(" + moisture.getElapsedSeconds() / total.getElapsedSeconds() * 100 + "%)");
		System.out.println("Biome: " + biome.getElapsedSeconds() + "(" + biome.getElapsedSeconds() / total.getElapsedSeconds() * 100 + "%)");
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
