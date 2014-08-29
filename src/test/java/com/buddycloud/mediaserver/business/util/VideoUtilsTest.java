package com.buddycloud.mediaserver.business.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VideoUtilsTest {

	@Test
	public void isImageReturnsFalseIfFileHasNoExtension() {
		assertFalse(VideoUtils.isVideo(null));
	}
	
	@Test
	public void isImageReturnsFalseForNonImage() {
		assertFalse(VideoUtils.isVideo("buddycloud"));
	}
	
	@Test
	public void isImageReturnsTrueForImage() {
		assertTrue(VideoUtils.isVideo("mov"));
	}

}
