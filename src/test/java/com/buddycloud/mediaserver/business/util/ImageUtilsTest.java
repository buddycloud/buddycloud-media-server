package com.buddycloud.mediaserver.business.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ImageUtilsTest {

	private ImageUtils imageUtils;

	@Before
	public void setUp() throws Exception {
		imageUtils = new ImageUtils();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void isImageReturnsFalseIfFileHasNoExtension() {
		assertFalse(imageUtils.isImage(null));
	}
	
	@Test
	public void isImageReturnsFalseForNonImage() {
		assertFalse(imageUtils.isImage("buddycloud"));
	}
	
	@Test
	public void isImageReturnsTrueForImage() {
		assertTrue(imageUtils.isImage("jpeg"));
	}

}
