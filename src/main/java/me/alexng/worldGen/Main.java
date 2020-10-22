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
		writeMapDataToJpg(sampledWorldMap.getGenerationPipeline().getLandmassPipe().getStoredData(), ColorMaps.GREY_SCALE, "maps/landmass.jpg");
		writeMapDataToJpg(sampledWorldMap.getGenerationPipeline().getMountainPipe().getStoredData(), ColorMaps.GREY_SCALE, "maps/mountain.jpg");
		writeMapDataToJpg(sampledWorldMap.getGenerationPipeline().getHeightPipe().getStoredData(), ColorMaps.HEIGHT_MAP, "maps/height.jpg");
		writeMapDataToJpg(sampledWorldMap.getGenerationPipeline().getMoisturePipe().getStoredData(), ColorMaps.MOISTURE_MAP, "maps/moisture.jpg");
		writeMapDataToJpg(sampledWorldMap.getGenerationPipeline().getTemperaturePipe().getStoredData(), ColorMaps.TEMPERATURE_MAP, "maps/temperature.jpg");
		writeMapDataToJpg(sampledWorldMap.getGenerationPipeline().getBiomePipe().getStoredData(), ColorMaps.BIOME_MAP, "maps/biome.jpg");
	}

	private static void writeMapDataToJpg(MapData mapData, ColorMaps.ColorMap colorMap, String outputPath) throws IOException {
		int width = mapData.getWidth();
		int height = mapData.getHeight();
		MemoryImageSource mis = new MemoryImageSource(width, height, colorMap.packToPixels(mapData.getRawData()), 0, width);
		Image im = Toolkit.getDefaultToolkit().createImage(mis);

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferedImage.getGraphics().drawImage(im, 0, 0, null);
		ImageIO.write(bufferedImage, "jpg", new File(outputPath));
	}
}
