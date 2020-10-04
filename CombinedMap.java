package me.alexng.untitled.generate;

public class CombinedMap {

	private Sampler sampler;
	private HeightMap heightMap;
	private TemperatureMap temperatureMap;

	public CombinedMap(Sampler sampler) {
		this.sampler = sampler;
		this.heightMap = new HeightMap(sampler);
		this.temperatureMap = new TemperatureMap(heightMap);
	}

	private CombinedMap(HeightMap heightMap, TemperatureMap temperatureMap) {
		this.sampler = heightMap.getSampler();
		this.heightMap = heightMap;
		this.temperatureMap = temperatureMap;
	}

	public void generate() {
		heightMap.generate();
		temperatureMap.generate();
	}

	public CombinedMap sample(int numPointsX, int numPointsY) {
		return new CombinedMap(new Sampler(0, 0, getWidth(), getHeight(), numPointsX, numPointsY));
	}

	public Sampler getSampler() {
		return sampler;
	}

	public HeightMap getHeightMap() {
		return heightMap;
	}

	public TemperatureMap getTemperatureMap() {
		return temperatureMap;
	}

	public int getWidth() {
		return sampler.getNumPointsX();
	}

	public int getHeight() {
		return sampler.getNumPointsY();
	}
}
