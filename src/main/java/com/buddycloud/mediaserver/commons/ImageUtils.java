package com.buddycloud.mediaserver.commons;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;

public class ImageUtils {

	private static final String[] imageFormats = ImageIO.getReaderFormatNames();
	
	
	private ImageUtils() {}
	
	
	public static File storeImageIntoFile(BufferedImage image, String imageFormat,
			String pathToStore) throws IOException {
		
		// Store into provided path
		File output = new File(pathToStore);
		ImageIO.write(image, imageFormat, FileUtils.openOutputStream(output));
		
		return output;
	}
	
	public static byte[] imageToBytes(BufferedImage image, String imageFormat) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, imageFormat, baos);
		baos.flush();
		
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		
		return imageInByte;
	}
	
	public static BufferedImage createImagePreview(File image, int size) throws IOException {
		final BufferedImage img = ImageIO.read(image);
		final BufferedImage thumbnail = Scalr.resize(img, size);
		img.flush();
		
		return thumbnail;
	}
	
	public static BufferedImage createImagePreview(File image, int width, int height) throws IOException {
		final BufferedImage img = ImageIO.read(image);
		final BufferedImage thumbnail = Scalr.resize(img, width, height);
		img.flush();
		
		return thumbnail;
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
