package me.alexng.worldGen;

import org.joml.Vector3f;

/**
 * A helper class that contains methods that map a particular {@link me.alexng.worldGen.pipeline.Pipe}'s output to color values.
 */
public class ColorMaps {

	public static final ColorMap GREY_SCALE = data -> new Vector3f(255 * NoiseHelper.normalize(data));
	public static final ColorMap LANDMASS_MAP = GREY_SCALE;

	public static final ColorMap HEIGHT_MAP = height -> {
		if (height <= 0) {
			return new Vector3f(0, 0, 255 * (1 + height));
		} else {
			final Vector3f lowColor = new Vector3f(60, 209, 90);
			final Vector3f middleColor = new Vector3f(251, 248, 80);
			final Vector3f highColor = new Vector3f(250, 49, 74);
			if (height < 0.5) {
				return middleColor.mul(height).mul(2).add(lowColor.mul(0.5f - height).mul(2));
			} else {
				return highColor.mul(height - 0.5f).mul(2).add(middleColor.mul(1 - height).mul(2));
			}
		}
	};

	public static final ColorMap TEMPERATURE_MAP = temperature -> {
		float temperatureNormalized = NoiseHelper.normalize(temperature);
		return new Vector3f(temperatureNormalized * 255, 0, (1 - temperatureNormalized) * 255);
	};

	public static final ColorMap MOISTURE_MAP = moisture -> {
		float moistureNormalized = NoiseHelper.normalize(moisture);
		return new Vector3f((1 - moistureNormalized) * 255, 0, moistureNormalized * 255);
	};

	public static final ColorMap BIOME_MAP = biomeIdFloat -> {
		int biomeId = (int) biomeIdFloat;
		if (biomeId == Biome.OCEAN.getId()) {
			return new Vector3f(10, 20, 130);
		}
		return Biome.getBiomeFromId(biomeId).getColor();
	};

	public interface ColorMap {

		// TODO: Why are we returning floats from toColor()?
		/**
		 * Maps a pipes output to a color.
		 * @param value the value. DO NOT NORMALIZE
		 */
		Vector3f toColor(float value);

		/**
		 * Converts floats to ARGB ints.
		 */
		default int[] packToPixels(float[] rawData) {
			int[] array = new int[rawData.length];
			for (int i = 0; i < rawData.length; i++) {
				Vector3f color = toColor(rawData[i]);
				array[i] = (255 << 24) | ((int) color.x << 16) | ((int) color.y << 8) | (int) color.z;
			}
			return array;
		}
	}
}
