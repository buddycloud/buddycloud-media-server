package com.buddycloud.mediaserver;

import java.util.Properties;

public interface TextExtension {
	public void start(Properties configuration) throws Exception;

	public void shutdown() throws Exception;
}
