package me.alexng.worldGen;

public class NoiseHelper {

	public static FastNoiseLite getLandmassNoise(int seed) {
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
		noise.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXYPlanes);
		noise.SetSeed(seed);
		noise.SetFrequency(0.0003f);

		noise.SetFractalType(FastNoiseLite.FractalType.FBm);
		noise.SetFractalOctaves(6);
		noise.SetFractalLacunarity(2);
		noise.SetFractalGain(0.5f);
		noise.SetFractalWeightedStrength(0);
		noise.SetFractalPingPongStrength(2);
		return noise;
	}

	public static FastNoiseLite getLandHeightMapNoise(int seed) {
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
		noise.SetRotationType3D(FastNoiseLite.RotationType3D.None);
		noise.SetSeed(seed);
		noise.SetFrequency(0.00125f);

		noise.SetFractalType(FastNoiseLite.FractalType.FBm);
		noise.SetFractalOctaves(6);
		noise.SetFractalLacunarity(2);
		noise.SetFractalGain(0.5f);
		noise.SetFractalWeightedStrength(0);
		noise.SetFractalPingPongStrength(2);
		return noise;
	}

	public static FastNoiseLite getOceanHeightMapNoise(int seed) {
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
		noise.SetRotationType3D(FastNoiseLite.RotationType3D.None);
		noise.SetSeed(seed);
		noise.SetFrequency(0.0002f);

		noise.SetFractalType(FastNoiseLite.FractalType.FBm);
		noise.SetFractalOctaves(4);
		noise.SetFractalLacunarity(2);
		noise.SetFractalGain(0.5f);
		noise.SetFractalWeightedStrength(0);
		noise.SetFractalPingPongStrength(2);
		return noise;
	}

	public static FastNoiseLite getTemperatureNoise(int seed) {
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
		noise.SetRotationType3D(FastNoiseLite.RotationType3D.None);
		noise.SetSeed(seed);
		noise.SetFrequency(0.0009f);

		noise.SetFractalType(FastNoiseLite.FractalType.FBm);
		noise.SetFractalOctaves(4);
		noise.SetFractalLacunarity(2);
		noise.SetFractalGain(0.5f);
		noise.SetFractalWeightedStrength(0);
		noise.SetFractalPingPongStrength(2);
		return noise;
	}

	public static FastNoiseLite getMoistureNoise(int seed) {
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
		noise.SetRotationType3D(FastNoiseLite.RotationType3D.None);
		noise.SetSeed(seed);
		noise.SetFrequency(0.0005f);

		noise.SetFractalType(FastNoiseLite.FractalType.FBm);
		noise.SetFractalOctaves(4);
		noise.SetFractalLacunarity(2);
		noise.SetFractalGain(0.5f);
		noise.SetFractalWeightedStrength(0);
		noise.SetFractalPingPongStrength(2);
		return noise;
	}

	public static FastNoiseLite getMountainFilterNoise(int seed) {
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
		noise.SetRotationType3D(FastNoiseLite.RotationType3D.None);
		noise.SetSeed(seed);
		noise.SetFrequency(0.0008f);

		noise.SetFractalType(FastNoiseLite.FractalType.FBm);
		noise.SetFractalOctaves(4);
		noise.SetFractalLacunarity(2);
		noise.SetFractalGain(0.5f);
		noise.SetFractalWeightedStrength(0);
		noise.SetFractalPingPongStrength(2);
		return noise;
	}

	public static FastNoiseLite getMountainNoise(int seed) {
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
		noise.SetRotationType3D(FastNoiseLite.RotationType3D.None);
		noise.SetSeed(seed);
		noise.SetFrequency(0.003f);

		noise.SetFractalType(FastNoiseLite.FractalType.Ridged);
		noise.SetFractalOctaves(2);
		noise.SetFractalLacunarity(2f);
		noise.SetFractalGain(0.5f);
		noise.SetFractalWeightedStrength(1f);
		noise.SetFractalPingPongStrength(0);

		noise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
		noise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance);

		noise.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2Reduced);
		noise.SetDomainWarpAmp(40);
		return noise;
	}

	/**
	 * Returns the global wind direction in degrees [0, 360).
	 */
	public static int getWindDirection(int seed) {
		return seed % 360;
	}

	/**
	 * @param value Large value. Not [0, 1] or [-1. 1]. Something like [0, 255]
	 */
	public static float step(float value, int step) {
		return (int) (value / step) * step;
	}

	/**
	 * Clamps a value to the range [-1, 1].
	 */
	public static float clamp(float value) {
		return Math.max(-1, Math.min(1, value));
	}

	/**
	 * Normalizes a value from the range [-1, 1] to [0, 1]
	 */
	public static float normalize(float data) {
		return (data + 1) / 2f;
	}

	/**
	 * Stretches [0, 1] to [-1, 1].
	 */
	public static float stretch(float value) {
		return clamp(value * 2 - 1);
	}

	public static int getSeed() {
		return (int) System.currentTimeMillis() % 5177351;
	}
}
