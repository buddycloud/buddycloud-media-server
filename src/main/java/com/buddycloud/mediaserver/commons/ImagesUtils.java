package com.buddycloud.mediaserver.commons;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;

public class ImagesUtils {

	private static final String[] imageFormats = ImageIO.getReaderFormatNames();
	
	private ImagesUtils() {}

	
	public static File getImagePreview(File image, int width, int height, String imageFormat,
			String pathToStore) throws IOException {
		// Resize image
		BufferedImage resize = Scalr.resize(ImageIO.read(image), width, height);
		
		// Store into provided path
		File output = new File(pathToStore);
		ImageIO.write(resize, imageFormat, FileUtils.openOutputStream(output));
		
		return output;
	}
	
	public static boolean isImage(String extension) {
		for (String format : imageFormats) {
			if (format.equals(extension)) {
				return true;
			}
		}
		
		return false;
	}
}
