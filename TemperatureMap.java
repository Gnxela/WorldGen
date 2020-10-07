package me.alexng.untitled.generate;

import me.alexng.untitled.render.Texture;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * A map that outputs temperature in the range (-1, 1). -1 being colder than 1.
 */
public class TemperatureMap extends MapData {

	private final HeightMap heightMap;

	public TemperatureMap(HeightMap heightMap) {
		super(heightMap.getSampler());
		this.heightMap = heightMap;
	}

	@Override
	public void generate(int seed) {
		FastNoiseLite noise = NoiseHelper.getTemperatureNoise(seed);
		for (Sampler.Point point : getSampler().generatePoints()) {
			// TODO: Store the calculated latitude temp in a map to avoid expensive recalculations
			float latitudeTemp = calculateHeatGradient(point.getY());
			float sample = noise.GetNoise(point.getX(), point.getY());
			float tempWithNoise = latitudeTemp * 0.8f + sample * 0.2f;
			float height = Math.max(0, heightMap.getData(point));
			float scaledHeight = (float) Math.pow(height, 1.5);
			float tempWithNoiseLessHeight = NoiseHelper.clamp(tempWithNoise - scaledHeight * 0.5f - (NoiseHelper.normalize(tempWithNoise) * scaledHeight * 0.5f));
			setData(tempWithNoiseLessHeight, point);
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
		float temp = getDataNormalized(i);
		return new Vector3f(temp * 255, 0, (1 - temp) * 255);
	}

	private float calculateHeatGradient(int y) {
		float normalizedY = y / (float) getSampler().getTotalHeight();
		float temp = (float) (Math.pow(normalizedY, 4) * 14.955 + Math.pow(normalizedY, 3) * -29.911 + Math.pow(normalizedY, 2) * 14.6948 + normalizedY * 0.26);
		if (temp > 1) {
			temp = 1;
		}
		if (temp < 0) {
			temp = 0;
		}
		temp = (float) Math.pow(temp, 1.1);
		// Changing from (0, 1) to (-1, 1)
		return temp * 2 - 1;
	}
}
