package com.wipro.MachineDown;

import java.io.File;
import java.text.ParseException;

import remote.wise.util.ConnectMySQL;

public interface MachineDownInterface {

	public MachineDownEntity fieldValidation(String[] msgString) throws MachineDownCustomFault;

	public String processMachineDownData(File processingFile, String archivedFolderPath, String process, String errorFolderPath);
	
	public String WriteMachineDownData(MachineDownEntity machineDownEntity,ConnectMySQL connFactory) throws ParseException;
	
	public String insertInterfaceLogDetails(String fName,int totalCount);

}
