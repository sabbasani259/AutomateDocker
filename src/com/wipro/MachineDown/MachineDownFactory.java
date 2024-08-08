package com.wipro.MachineDown;

public class MachineDownFactory {

	
	public MachineDownInterface getInstance(){
		return new MachineDownInterfaceImpl();
	}
	
	
}
