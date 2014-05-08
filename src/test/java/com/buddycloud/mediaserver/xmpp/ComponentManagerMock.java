package com.buddycloud.mediaserver.xmpp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.component.IQResultListener;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;

public class ComponentManagerMock implements ComponentManager {

	private BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>();
	
	@Override
	public void addComponent(String subdomain, Component component)
			throws ComponentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeComponent(String subdomain) throws ComponentException {
		// TODO Auto-generated method stub
		
	}

	public Packet collectPacket() {
		try {
			return queue.poll(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	@Override
	public void sendPacket(Component component, Packet packet)
			throws ComponentException {
		try {
			queue.put(packet);
		} catch (InterruptedException e) {
			throw new ComponentException(e);
		}
	}

	@Override
	public IQ query(Component component, IQ packet, long timeout)
			throws ComponentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void query(Component component, IQ packet, IQResultListener listener)
			throws ComponentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getProperty(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(String name, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isExternalMode() {
		// TODO Auto-generated method stub
		return false;
	}

}
