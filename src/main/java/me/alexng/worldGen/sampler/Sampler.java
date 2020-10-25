package me.alexng.worldGen.sampler;

import java.util.Iterator;

/**
 * A class that samples points from a field. All coordinates are positive (subject to change).
 */
public interface Sampler {
	Iterator<Point> getPoints();
	int getSize();
}
