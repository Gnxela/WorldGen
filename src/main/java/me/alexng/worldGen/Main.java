package me.alexng.worldGen;

import me.alexng.worldGen.sampler.PlaneSampler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		WorldMap<PlaneSampler> worldMap = new WorldMap<>(new PlaneSampler(10000, 10000));
		WorldMap<PlaneSampler> sampledWorldMap = new WorldMap<>(worldMap.getSampler().sample(1000, 1000));
		long generationStart = System.nanoTime();
		sampledWorldMap.generate(0);
		System.out.println("Generation: " + (System.nanoTime() - generationStart) / 1000000000f + "s");
		int width = sampledWorldMap.getSampler().getNumPointsX();
		int height = sampledWorldMap.getSampler().getNumPointsX();
		long writingStart = System.nanoTime();
		/*
		writeMapDataToPng(width, height, sampledWorldMap.getGenerationPipeline().getLandmassPipe().getStoredData(), ColorMaps.GREY_SCALE, "maps/landmass.png");
		writeMapDataToPng(width, height, sampledWorldMap.getGenerationPipeline().getMountainPipe().getStoredData(), ColorMaps.GREY_SCALE, "maps/mountain.png");
		writeMapDataToPng(width, height, sampledWorldMap.getGenerationPipeline().getHeightPipe().getStoredData(), ColorMaps.HEIGHT_MAP, "maps/height.png");
		writeMapDataToPng(width, height, sampledWorldMap.getGenerationPipeline().getMoisturePipe().getStoredData(), ColorMaps.MOISTURE_MAP, "maps/moisture.png");
		writeMapDataToPng(width, height, sampledWorldMap.getGenerationPipeline().getTemperaturePipe().getStoredData(), ColorMaps.TEMPERATURE_MAP, "maps/temperature.png");
		writeMapDataToPng(width, height, sampledWorldMap.getGenerationPipeline().getBiomePipe().getStoredData(), ColorMaps.BIOME_MAP, "maps/biome.png");
		*/
		System.out.println("Writing: " + (System.nanoTime() - writingStart) / 1000000000f + "s");
	}

	private static void writeMapDataToPng(int width, int height, MapData mapData, ColorMaps.ColorMap colorMap, String outputPath) throws IOException {
		MemoryImageSource imageSource = new MemoryImageSource(width, height, colorMap.packToPixels(mapData.getRawData()), 0, width);
		Image image = Toolkit.getDefaultToolkit().createImage(imageSource);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferedImage.getGraphics().drawImage(image, 0, 0, null);
		ImageIO.write(bufferedImage, "png", new File(outputPath));
	}
}
