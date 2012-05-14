package com.buddycloud.mediaserver;

public class AThread extends Thread {
	private static AThread instance = new AThread();
	
	private AThread() {
		System.out.println("STARTED!!!");
	}
	
	
	public static AThread getInstance() {
		return instance;
	}
	
	
	public void hello() {
		System.out.println("Hello World!");
	}
}
