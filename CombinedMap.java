package me.alexng.untitled.generate;

public class CombinedMap {

	private final Sampler sampler;
	private final LandmassMap landmassMap;
	private final HeightMap heightMap;
	private final TemperatureMap temperatureMap;
	private final MoistureMap moistureMap;

	public CombinedMap(Sampler sampler) {
		this.sampler = sampler;
		this.landmassMap = new LandmassMap(sampler);
		this.heightMap = new HeightMap(landmassMap);
		this.temperatureMap = new TemperatureMap(heightMap);
		this.moistureMap = new MoistureMap(heightMap);
	}

	public void generate(int seed) {
		landmassMap.generate(seed);
		heightMap.generate(seed);
		temperatureMap.generate(seed);
		moistureMap.generate(seed);
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

	public int getWidth() {
		return sampler.getNumPointsX();
	}

	public int getHeight() {
		return sampler.getNumPointsY();
	}
}
