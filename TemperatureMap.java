package me.alexng.untitled.generate;

import me.alexng.untitled.render.Texture;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class TemperatureMap extends MapData {

	private final HeightMap heightMap;

	public TemperatureMap(HeightMap heightMap) {
		super(heightMap.getSampler());
		this.heightMap = heightMap;
	}

	@Override
	public void generate() {
		FastNoiseLite noise = NoiseHelper.getTemperatureNoise();
		for (Sampler.Point point : getSampler().generatePoints()) {
			// TODO: Store the calculated temp in a map to avoid expensive recalculations
			final int y = point.getY();
			final int x = point.getX();
			final int indexX = point.getIndexX();
			final int indexY = point.getIndexY();
			// TODO: This doesn't work as intended for sub maps of the global map. 
			float temp = calculateHeatGradient(indexY);
			float sample = noise.GetNoise(x, y);
			float normalized2Sample = (sample + 1) / 2;
			float weightedTemp = temp * 0.65f + normalized2Sample * 0.35f;
			float weightedTempLessHeight = Math.max(0, weightedTemp - (float) (Math.pow(heightMap.getDataNormalized(indexX, indexY) + 1, 1.3) - 1) * 0.4f);
			setData(weightedTempLessHeight, indexX, indexY);
		}
	}

	@Override
	public MapData sample(Sampler sampler) {
		return null;
	}

	@Override
	public Texture toTextureRGB(Texture.Type type) {
		final int PIXEL_WIDTH = 3;
		Texture texture = new Texture(type);
		ByteBuffer buffer = MemoryUtil.memAlloc(getSize() * PIXEL_WIDTH);
		for (int i = 0; i < getSize(); i++) {
			Vector3f color = toColor(i);
			buffer.put((byte) color.x);
			buffer.put((byte) color.y);
			buffer.put((byte) color.z);
		}
		buffer.flip();
		texture.load(getWidth(), getHeight(), buffer);
		buffer.clear();
		MemoryUtil.memFree(buffer);
		return texture;
	}

	@Override
	public Vector3f toColor(int i) {
		float temp = getData(i);
		return new Vector3f(temp * 255, 0, (1 - temp) * 255);
	}

	private float calculateHeatGradient(int y) {
		float normalizedY = y / (float) getHeight();
		float temp = (float) (Math.pow(normalizedY, 4) * 14.955 + Math.pow(normalizedY, 3) * -29.911 + Math.pow(normalizedY, 2) * 14.6948 + normalizedY * 0.26);
		if (temp > 1) {
			temp = 1;
		}
		if (temp < 0) {
			temp = 0;
		}
		return temp;
	}
}
