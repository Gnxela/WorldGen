package me.alexng.worldGen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		CombinedMap worldMap = new CombinedMap(new Sampler(10000, 10000));
		CombinedMap sampledWorldMap = worldMap.sample(1000, 1000);
		sampledWorldMap.generate(0);
		writeMapDataToPng(sampledWorldMap.getGenerationPipeline().getLandmassPipe().getStoredData(), ColorMaps.GREY_SCALE, "maps/landmass.png");
		writeMapDataToPng(sampledWorldMap.getGenerationPipeline().getMountainPipe().getStoredData(), ColorMaps.GREY_SCALE, "maps/mountain.png");
		writeMapDataToPng(sampledWorldMap.getGenerationPipeline().getHeightPipe().getStoredData(), ColorMaps.HEIGHT_MAP, "maps/height.png");
		writeMapDataToPng(sampledWorldMap.getGenerationPipeline().getMoisturePipe().getStoredData(), ColorMaps.MOISTURE_MAP, "maps/moisture.png");
		writeMapDataToPng(sampledWorldMap.getGenerationPipeline().getTemperaturePipe().getStoredData(), ColorMaps.TEMPERATURE_MAP, "maps/temperature.png");
		writeMapDataToPng(sampledWorldMap.getGenerationPipeline().getBiomePipe().getStoredData(), ColorMaps.BIOME_MAP, "maps/biome.png");
	}

	private static void writeMapDataToPng(MapData mapData, ColorMaps.ColorMap colorMap, String outputPath) throws IOException {
		int width = mapData.getWidth();
		int height = mapData.getHeight();
		MemoryImageSource imageSource = new MemoryImageSource(width, height, colorMap.packToPixels(mapData.getRawData()), 0, width);
		Image image = Toolkit.getDefaultToolkit().createImage(imageSource);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferedImage.getGraphics().drawImage(image, 0, 0, null);
		ImageIO.write(bufferedImage, "png", new File(outputPath));
	}
}
