package com.buddycloud.mediaserver.commons;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;

public class PreviewUtils {

	private PreviewUtils() {}

	/**
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @param imageFormat
	 * @param pathToStore
	 * @return
	 * @throws IOException
	 */
	public static File getImagePreview(File image, int width, int height, String imageFormat,
			String pathToStore) throws IOException {
		// Resize image
		BufferedImage resize = Scalr.resize(ImageIO.read(image), width, height);

		// Store into provided path
		File output = new File(pathToStore);
		ImageIO.write(resize, imageFormat, FileUtils.openOutputStream(output));
		
		return output;
	}
	
}
