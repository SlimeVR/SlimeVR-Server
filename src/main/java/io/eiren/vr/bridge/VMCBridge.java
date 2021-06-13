package io.eiren.vr.bridge;

import java.net.InetAddress;

public class VMCBridge extends Thread implements VRBridge {
	
	public final int readPort;
	public final int writePort;
	public final InetAddress writeAddr;
	
	public VMCBridge(int readPort, int writePort, InetAddress writeAddr) {
		super("Virtual Motion Capture bridge");
		if(readPort == writePort)
			throw new IllegalArgumentException("Read and write port shouldn't be the same!");
		this.readPort = readPort;
		this.writePort = writePort;
		this.writeAddr = writeAddr;
	}
	
	@Override
	public void dataRead() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dataWrite() {
		// TODO Auto-generated method stub
		
	}
	
}
