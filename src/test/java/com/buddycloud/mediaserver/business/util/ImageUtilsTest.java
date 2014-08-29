package com.buddycloud.mediaserver.business.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ImageUtilsTest {

	@Test
	public void isImageReturnsFalseIfFileHasNoExtension() {
		assertFalse(ImageUtils.isImage(null));
	}
	
	@Test
	public void isImageReturnsFalseForNonImage() {
		assertFalse(ImageUtils.isImage("buddycloud"));
	}
	
	@Test
	public void isImageReturnsTrueForImage() {
		assertTrue(ImageUtils.isImage("jpeg"));
	}

}
