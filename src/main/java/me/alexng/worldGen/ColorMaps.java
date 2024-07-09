package me.alexng.worldGen;

import org.joml.Vector3f;

import me.alexng.worldGen.pipeline.pipes.dto.Velocity;

/**
 * A helper class that contains methods that map a particular
 * {@link me.alexng.worldGen.pipeline.Pipe}'s output to color values.
 */
public class ColorMaps {

	public static final ColorMap GREY_SCALE = data -> new Vector3f(255 * NoiseHelper.normalize(toFloat(data)));
	public static final ColorMap WIND_SCALE = data -> hslColor(
			NoiseHelper.normalize(
					(float) Math.tanh(((Velocity) data).direction.y / ((Velocity) data).direction.x)),
			0.5f,
			// 0.5f);
			((Velocity) data).magnitude);
	public static final ColorMap HSL_SCALE = data -> hslColor(NoiseHelper.normalize(toFloat(data)), 0.5f, 0.5f);

	static public Vector3f hslColor(float h, float s, float l) {
		float q, p, r, g, b;

		if (s == 0) {
			r = g = b = l; // achromatic
		} else {
			q = l < 0.5 ? (l * (1 + s)) : (l + s - l * s);
			p = 2 * l - q;
			r = hue2rgb(p, q, h + 1.0f / 3);
			g = hue2rgb(p, q, h);
			b = hue2rgb(p, q, h - 1.0f / 3);
		}
		return new Vector3f(r * 255, g * 255, b * 255);
	}

	private static float hue2rgb(float p, float q, float h) {
		if (h < 0) {
			h += 1;
		}

		if (h > 1) {
			h -= 1;
		}

		if (6 * h < 1) {
			return p + ((q - p) * 6 * h);
		}

		if (2 * h < 1) {
			return q;
		}

		if (3 * h < 2) {
			return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
		}

		return p;
	}

	public static final ColorMap LANDMASS_MAP = GREY_SCALE;

	public static final ColorMap HEIGHT_MAP = heightO -> {
		float height = toFloat(heightO);
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

	public static final ColorMap TEMPERATURE_MAP = temperatureO -> {
		float temperature = toFloat(temperatureO);
		float temperatureNormalized = NoiseHelper.normalize(temperature);
		return new Vector3f(temperatureNormalized * 255, 0, (1 - temperatureNormalized) * 255);
	};

	public static final ColorMap MOISTURE_MAP = moistureO -> {
		float moisture = toFloat(moistureO);
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
		 * 
		 * @param value the value. DO NOT NORMALIZE
		 */
		Vector3f toColor(Object value);

		/**
		 * Converts floats to ARGB ints.
		 */
		default int[] packToPixels(Object[] rawData) {
			int[] array = new int[rawData.length];
			for (int i = 0; i < rawData.length; i++) {
				Vector3f color = toColor(rawData[i]);
				array[i] = (255 << 24) | ((int) color.x << 16) | ((int) color.y << 8) | (int) color.z;
			}
			return array;
		}
	}

	private static float toFloat(Object o) {
		return (float) o;
	}
}
