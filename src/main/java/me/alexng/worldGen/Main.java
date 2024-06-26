package me.alexng.worldGen;

import me.alexng.worldGen.sampler.PlaneSampler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws IOException {
		// Create a large map
		WorldMap<PlaneSampler> worldMap = new WorldMap<>(new PlaneSampler(10000, 10000));
		// Sample 1000 points
		WorldMap<PlaneSampler> sampledWorldMap = new WorldMap<>(worldMap.getSampler().sample(1000, 1000));
		/// Take the botom right quad
		// WorldMap<PlaneSampler> sampledWorldMap = new WorldMap<>(childSampledWorldMap.getSampler().sample(500, 500, 500, 500));
		long generationStart = System.nanoTime();
		Map<String, float[]> resultMap = sampledWorldMap.generate(0);
		System.out.println("Generation: " + (System.nanoTime() - generationStart) / 1000000000f + "s");
		int width = sampledWorldMap.getSampler().getNumPointsX();
		int height = sampledWorldMap.getSampler().getNumPointsY();
		long writingStart = System.nanoTime();

		writeMapDataToPng(width, height, resultMap.get("biome"), ColorMaps.BIOME_MAP, "maps/biome.png");
		writeMapDataToPng(width, height, resultMap.get("coriolis"), ColorMaps.HSL_SCALE, "maps/coriolis.png");
		writeMapDataToPng(width, height, resultMap.get("coriolis"), ColorMaps.GREY_SCALE, "maps/coriolis_grey.png");

		float[] c = resultMap.get("coriolis");
		int numPoints = 100;
		int d = 1000 / numPoints;
		for (int i = 0; i < numPoints; i++) {
			System.out.println(i * d + ":\n\t" + c[i * d * 1000] + "\n\t" + angularDist(0f, ((float) Math.toDegrees(c[i * d * 1000] * Math.PI)) + 180f));
		}

		System.out.println("Writing: " + (System.nanoTime() - writingStart) / 1000000000f + "s");
	}

	/*
     * Given 2 angles between 0, 360
     */
    private static float angularDist(float x, float y) {
        float a = Math.abs(x - y);
		if (a < 180) {
			return a;
		}
		return 360 - a;
    }

	private static void writeMapDataToPng(int width, int height, float[] rawData, ColorMaps.ColorMap colorMap, String outputPath) throws IOException {
		MemoryImageSource imageSource = new MemoryImageSource(width, height, colorMap.packToPixels(rawData), 0, width);
		Image image = Toolkit.getDefaultToolkit().createImage(imageSource);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferedImage.getGraphics().drawImage(image, 0, 0, null);
		ImageIO.write(bufferedImage, "png", new File(outputPath));
	}
}
