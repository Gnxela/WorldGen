package me.alexng.untitled.generate;

public class NoiseHelper {

	// TODO: If we only need one instance of a particular noise setup, then make these variables.

	public static FastNoiseLite getHeightMapNoise() {
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
		noise.SetRotationType3D(FastNoiseLite.RotationType3D.None);
		noise.SetSeed((int) System.currentTimeMillis() % 936523);
		noise.SetFrequency(0.00125f);

		noise.SetFractalType(FastNoiseLite.FractalType.FBm);
		noise.SetFractalOctaves(6);
		noise.SetFractalLacunarity(2);
		noise.SetFractalGain(0.5f);
		noise.SetFractalWeightedStrength(0);
		noise.SetFractalPingPongStrength(2);

		noise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq);
		noise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance);
		noise.SetCellularJitter(1);

		return noise;
	}

	public static FastNoiseLite getTemperatureNoise() {
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
		noise.SetRotationType3D(FastNoiseLite.RotationType3D.None);
		noise.SetSeed((int) System.currentTimeMillis() % 936523);
		noise.SetFrequency(0.0013f);

		noise.SetFractalType(FastNoiseLite.FractalType.FBm);
		noise.SetFractalOctaves(4);
		noise.SetFractalLacunarity(2);
		noise.SetFractalGain(0.5f);
		noise.SetFractalWeightedStrength(0);
		noise.SetFractalPingPongStrength(2);

		noise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq);
		noise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance);
		noise.SetCellularJitter(1);

		return noise;
	}
}
