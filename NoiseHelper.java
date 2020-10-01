package me.alexng.untitled.generate;

public class NoiseHelper {

	public static FastNoiseLite getHeightMapNoise() {
		FastNoiseLite noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
		noise.SetRotationType3D(FastNoiseLite.RotationType3D.None);
		noise.SetSeed((int) System.currentTimeMillis() % 936523);
		noise.SetFrequency(0.001f);

		noise.SetFractalType(FastNoiseLite.FractalType.FBm);
		noise.SetFractalOctaves(5);
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
