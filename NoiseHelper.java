package me.alexng.untitled.generate;

public class NoiseHelper {

	// TODO: If we only need one instance of a particular noise setup, then make these variables.

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

	/**
	 * @param value Large value. Not (0, 1) or (-1. 1). Something like (0, 255)
	 * @return
	 */
	public static float step(float value, int step) {
		return (int) (value / step) * step;
	}

	public static float clamp(float value) {
		return Math.max(-1, Math.min(1, value));
	}

	public static float normalize(float data) {
		return (data + 1) / 2f;
	}

	public static int getSeed() {
		return (int) System.currentTimeMillis() % 5177351;
	}
}
