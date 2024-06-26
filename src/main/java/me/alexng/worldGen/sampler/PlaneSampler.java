package me.alexng.worldGen.sampler;

import java.util.Iterator;

/**
 * A class that samples points from a plane. All coordinates are positive
 * (subject to change).
 */
public class PlaneSampler implements Sampler {

	private final int x, y, width, height, numPointsX, numPointsY, totalWidth, totalHeight;

	public PlaneSampler(int x, int y, int width, int height, int numPointsX, int numPointsY, int totalWidth,
			int totalHeight) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.numPointsX = numPointsX;
		this.numPointsY = numPointsY;
		this.totalWidth = totalWidth;
		this.totalHeight = totalHeight;
		System.out.println(
				"x: " + x + " y: " + y + " width: " + width + " height: " + height + " numPointsX: " + numPointsX
						+ " numPointsY: " + numPointsY + " totalWidth: " + totalWidth + " totalHeight: " + totalHeight);
	}

	public PlaneSampler(int width, int height) {
		this(0, 0, width, height, width, height, width, height);
	}

	public PlaneSampler sample(int x, int y, int width, int height, int numPointsX, int numPointsY) {
		float sampleDistanceX = getWidth() / (float) getNumPointsX(),
				sampleDistanceY = getHeight() / (float) getNumPointsY();
		return new PlaneSampler((int) (x * getWidth() / (float) getNumPointsX()),
				(int) (y * getHeight() / (float) getNumPointsY()),
				(int) (width * sampleDistanceX),
				(int) (height * sampleDistanceY), numPointsX, numPointsY, totalWidth, totalHeight);
	}

	public PlaneSampler sample(int x, int y, int width, int height) {
		return sample(x, y, width, height, width, height);
	}

	public PlaneSampler sample(int numPointsX, int numPointsY) {
		return new PlaneSampler(0, 0, getWidth(), getHeight(), numPointsX, numPointsY, totalWidth, totalHeight);
	}

	public Iterator<Point> getPoints() {
		return new Iterator<Point>() {

			private final float sampleDistanceX = width / (float) numPointsX,
					sampleDistanceY = height / (float) numPointsY;
			private int sampleX = 0, sampleY = 0, index = 0;

			@Override
			public boolean hasNext() {
				return index < getSize();
			}

			@Override
			public Point next() {
				Point point = new PlanePoint((int) (x + sampleX * sampleDistanceX),
						(int) (y + sampleY * sampleDistanceY), index++);
				if (++sampleX >= numPointsX) {
					sampleY++;
					sampleX = 0;
				}
				return point;
			}
		};
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getNumPointsX() {
		return numPointsX;
	}

	public int getNumPointsY() {
		return numPointsY;
	}

	public int getTotalHeight() {
		return totalHeight;
	}

	/**
	 * Returns the number of points that will be sampled by this Sampler.
	 */
	@Override
	public int getSize() {
		return numPointsX * numPointsY;
	}
}
