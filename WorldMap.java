package me.alexng.untitled.generate;

public class WorldMap {

	// Width = x. Length = y/z.
	private int mapWidth, mapHeight;

	public WorldMap(int mapWidth, int mapHeight) {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
	}

	public void sample(int numPointsX, int numPointsY) {
		float sampleDistanceX = mapWidth / (float) numPointsX;
		float sampleDistanceY = mapHeight / (float) numPointsY;
		float currentY = 0;
		while (currentY < mapHeight) {
			float currentX = 0;
			while (currentX < mapWidth) {
				currentX += sampleDistanceX;
			}
			currentY += sampleDistanceY;
		}
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}
}
