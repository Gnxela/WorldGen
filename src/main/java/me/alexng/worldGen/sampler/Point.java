package me.alexng.worldGen.sampler;

import me.alexng.worldGen.FastNoiseLite;

public interface Point {
	float sample(FastNoiseLite noise);
	int getIndex();
}
