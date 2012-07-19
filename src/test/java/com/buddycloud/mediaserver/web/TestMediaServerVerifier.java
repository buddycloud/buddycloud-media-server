package com.buddycloud.mediaserver.web;


import org.restlet.security.LocalVerifier;

public class TestMediaServerVerifier extends LocalVerifier {
	
	@Override
	public int verify(String identifier, char[] secret) {
		return RESULT_VALID;
	}

	@Override
	public char[] getLocalSecret(String identifier) {
		return null;
	}
}
