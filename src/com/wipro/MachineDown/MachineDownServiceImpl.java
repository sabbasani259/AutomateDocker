package com.wipro.MachineDown;

import java.io.File;
import java.io.FileFilter;
import java.util.Properties;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class MachineDownServiceImpl {

	public void invokeProcessing() {

		String inputfolderPath = "";
		String processingFolderPath = "";
		String archivedFolderPath = "";
		String errorFolderPath = "";
		String machineDownFileNamePattern = "";
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		
		MachineDownFactory factory = new MachineDownFactory();
		MachineDownInterface machineDownFactoryObj = factory.getInstance();
		
		try{
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
		
		
		errorFolderPath = prop.getProperty("EA_errorFolderPath");
		machineDownFileNamePattern=prop.getProperty("machineDownFileNamePattern");
//		if (prop.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
//			inputfolderPath = prop.getProperty("EA_inputFolderPath_SIT");
//			processingFolderPath = prop.getProperty("EA_processingFolderPath_SIT");
//			archivedFolderPath = prop.getProperty("EA_archiveFolderPath_SIT");
//		} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
//			inputfolderPath = prop.getProperty("EA_inputFolderPath_DEV");
//			processingFolderPath = prop.getProperty("EA_processingFolderPath_DEV");
//			archivedFolderPath = prop.getProperty("EA_archiveFolderPath_DEV");
//		} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
//			inputfolderPath = prop.getProperty("EA_inputFolderPath_QA");
//			processingFolderPath = prop.getProperty("EA_processingFolderPath_QA");
//			archivedFolderPath = prop.getProperty("EA_archiveFolderPath_QA");
//		} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
//			inputfolderPath = prop.getProperty("EA_inputFolderPath_PROD");
//			processingFolderPath = prop.getProperty("EA_processingFolderPath_PROD");
//			archivedFolderPath = prop.getProperty("EA_archiveFolderPath_PROD");
//		} else {
//			inputfolderPath = prop.getProperty("EA_inputFolderPath");
//			processingFolderPath = prop.getProperty("EA_processingFolderPath");
//			archivedFolderPath = prop.getProperty("EA_archiveFolderPath");
//		}
		
		inputfolderPath = "/user/JCBLiveLink/EAIntegration/input";
		processingFolderPath = "/user/JCBLiveLink/EAIntegration/processing";
		archivedFolderPath = "/user/JCBLiveLink/EAIntegration/archived";
		errorFolderPath = "/user/JCBLiveLink/EAIntegration/error";
		machineDownFileNamePattern=prop.getProperty("machineDownFileNamePattern");
//		
		// File name Patter to be read
		String pattern = machineDownFileNamePattern+"*.txt";
		
		FileFilter fileFilter = new WildcardFileFilter(pattern);
		File inputFolder = new File(inputfolderPath);
		File processingFolder = new File(processingFolderPath);
		File errorFolder = new File(errorFolderPath);
		File[] errorFiles = errorFolder.listFiles(fileFilter);
		File[] processingFiles = processingFolder.listFiles(fileFilter);
		File[] inputFiles = inputFolder.listFiles(fileFilter);
		
		try
		{
			
			//First Process the files in processing folder
			for(int i=0; i<errorFiles.length; i++)
			{
				File errorFile = errorFiles[i];
				iLogger.info("WISE:MachineDownInterface - "+"File to be processed:"+errorFile.getName());
				
				
				machineDownFactoryObj.processMachineDownData(errorFile,archivedFolderPath, "MachineDown", errorFolderPath);
				
				//fileObj.processJcbRollOffData(processFile,archivedFolderPath, "MachineDown", errorFolderPath);
			}

			
			//First Process the files in processing folder
			for(int i=0; i<processingFiles.length; i++)
			{
				File processFile = processingFiles[i];
				iLogger.info("WISE:MachineDownInterface - "+"File to be processed:"+processFile.getName());
				
				
				machineDownFactoryObj.processMachineDownData(processFile,archivedFolderPath, "MachineDown", errorFolderPath);
				
				//fileObj.processJcbRollOffData(processFile,archivedFolderPath, "MachineDown", errorFolderPath);
			}

			//Next Process the files in input folder
			for (int j = 0; j < inputFiles.length; j++) 
			{
				//get each file
				File inputFile = inputFiles[j];

				//move the file to processing folder
				File processingFile = new File(processingFolderPath,inputFile.getName());
				if(processingFile.exists())
					processingFile.delete();
				boolean fileMoveStatus = inputFile.renameTo(processingFile);

				if(fileMoveStatus)
					iLogger.info("WISE:MachineDownInterface - "+"File "+inputFile.getName()+" is moved successfully from Input to processing folder");

				machineDownFactoryObj.processMachineDownData(processingFile,archivedFolderPath, "MachineDown", errorFolderPath);
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}
		
		
		}
		catch(Exception e){
			
		}
	}
}
