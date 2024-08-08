package remote.wise.service.implementation;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Logger;
////import org.apache.log4j.Logger;
import org.hibernate.Session;

import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.HibernateUtil;
import remote.wise.util.StaticProperties;
//import remote.wise.util.WiseLogger;

/** Implementation Class to read the XML Data Packets from Unprocessed folder, Process the data packets and move the same to AEMP output folder.
 * Since the edge proxy is modified to drop the files into a folder rather than calling the Webservice directly - This change is to avoid the loss of data packets if the WISE service is down
 * @author Rajani Nagaraju
 *
 */
public class FileProcessAssetDiagnosticImpl 
{
	/*public static WiseLogger fatalError = WiseLogger.getLogger("FileProcessAssetDiagnosticImpl:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("FileProcessAssetDiagnosticImpl:","info");*/
	//DF20140219 - Rajani Nagaraju - This Global variable is maintained to ensure that no two threads try to access the same file
	public static int fileProcessingFlag = 0;
	
	public void handleDataPackets()
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	//DF20150611 - Rajani Nagaraju - Declaration of varaiables at the start for better logging
    	List<List<String>> unprocessFileArray = new LinkedList<List<String>>();
		int totalFilesInArray=0;
		//DF20150611 - Rajani Nagaraju - For Better Logging mechanism
		long startTime = System.currentTimeMillis();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String startDate = dateFormat.format(new Date());
		
        try
        {
        	if(fileProcessingFlag==0)
        	{
        		fileProcessingFlag=1;
        		//DF20150409 - Rajani Nagaraju - Commenting Loggers 
        		iLogger.info("FADS: File Processing thread started");
        		
        		
        		
        		try
        		{
        			//------------------- get the required folder paths from properties file
        			Properties prop = new Properties();
        			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
    			
	        		String DataPackets_UnprocessedFolderPath = null;
	        		String DataPackets_InProcessFolderPath = null;
        		
	        		if (prop.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
	        			DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath_SIT");
	        			DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath_SIT");
					} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
						DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath_DEV");
	        			DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath_DEV");
					} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
						DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath_QA");
	        			DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath_QA");
					} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
						DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath_PROD");
	        			DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath_PROD");
					} else {
						DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath");
	        			DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath");
					}
    			
        		
        		
	        		String pattern = "*.xml";
	        		//infoLogger.info("File Pattern:"+pattern);
	    			
	        		//------------------------- Get the files related to Asset Type
	        		FileFilter fileFilter = new WildcardFileFilter(pattern);
	    			        		
	        		//DF20140521 - Rajani Nagaraju - To reduce the delay when processing the piled up files in unprocessed folder. Instead of waiting for the cron to spawn a new thread
	        		// after 3 min, existing thread would continue scanning the unprocessed folder again and again after the current pass until the folder becomes empty

	        		File unProcessedFolder = new File(DataPackets_UnprocessedFolderPath);
	        		File[] processingFiles = unProcessedFolder.listFiles(fileFilter);
	        		int unprocessedFolderCount=0;
				
	        		//DF20150409 - Rajani Nagaraju - Commenting Loggers 	        		
	        		//iLogger.info("Timstamp before array Sorting:"+ (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS")).format(new Date()));
	        		//STEP1: Sort the files in Unprocessed folder in the ascending order sequence of VIN followed by ascending order sequence of snapshot time
	        		Arrays.sort( processingFiles, new Comparator<File>() 
	        		{
	        		    public int compare( File a, File b ) {
	        		    	return (a.getName().split("_")[1] + a.getName().split("_")[0] + a.getName().split("_")[2])
	                        .compareTo( b.getName().split("_")[1] + b.getName().split("_")[0] + a.getName().split("_")[2]);
	
	
	        		    }
	        		});
	        		//DF20150409 - Rajani Nagaraju - Commenting Loggers 	
	        		//iLogger.info("Timstamp after array Sorting:"+ (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS")).format(new Date()));
        		
        		
	        		//STEP2: Create a two dimensional array of fixed length - 200X200 with Y axis running for unique VINs and X axis running for different 
	        		//packets for a single VIN in sequence of snapshot time
	        		String prevVIN="";
	        		String skipVIN="";
	        		
	        		//DF20150611 - Rajani Nagaraju - Moving the declaration to outside try - For better logging mechanism
	        		//List<List<String>> unprocessFileArray = new LinkedList<List<String>>();
	        		List<String> innerList = null;
	        		
	        		//DF20150409 - Rajani Nagaraju - Commenting Loggers 
	        		//iLogger.info("Start of Creating two-dimensional array :"+ (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS")).format(new Date()));
	        		
	        		//DF20150611 - Rajani Nagaraju - Moving the declaration to outside try - For better logging mechanism
	        		//int totalFilesInArray=0;
	        		
	        		for(int x=0; x<processingFiles.length; x++)
	        		{
	        			String currentVIN = processingFiles[x].getName().split("_")[1];
	        			if(currentVIN.equalsIgnoreCase(skipVIN))
	        			{
	        				/*if(innerList!=null)
	        				{
	        					unprocessFileArray.add(innerList);
	        					innerList=null;
	        				}*/
	        				continue;
	        			}
	        			
	        			if(!(currentVIN.equalsIgnoreCase(prevVIN)))
	        			{
	        				if(unprocessFileArray.size()>199)
	        				{
	        					break;
	        				}
        				
	        				if( (x!=0) && innerList!=null)
	        				{
	        					unprocessFileArray.add(innerList);
	        					totalFilesInArray=totalFilesInArray+innerList.size();
	        				}
	        				
	        				innerList = new LinkedList<String>();
	        				innerList.add(processingFiles[x].getName());
	        				prevVIN=currentVIN;
	        			}
	        			else
	        			{
	        				//DF20150611 - Rajani Nagaraju - Redefining Horizontal Matrix length from 200 to 18 - Logic behind defining 18 is: -- 
	        				//If there is a 3 sec pause for every columnar iteration for 18 columns it would be 18*3=54 + 3sec pause for reprocess index -- 
	        				//which comes to 57 sec + 3sec buffer - So if the XMLListenerScript runs for every 1 min, all elements of the array should have been processed --
	        				//making room for other VINs in the next pass, so that huge backlog on one VIN doesn't slows down the processing of other VINs
	        				if(innerList.size()>18)
	        				{
	        					skipVIN=processingFiles[x].getName().split("_")[1];
	        					
	        			    }
	        				
	        				else
	        				{
	        					innerList.add(processingFiles[x].getName());
	            				
	        				}
	        			}
	        			
	        			
	        			
	        		}
	        		
	        		
	        		if(innerList!=null)
        			{
        				unprocessFileArray.add(innerList);
        				totalFilesInArray=totalFilesInArray+innerList.size();
        			}
	        		
	        		//DF20150409 - Rajani Nagaraju - Commenting Loggers 
	        		//iLogger.info("End of Creating two-dimensional array :"+ (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS")).format(new Date()));
	        		
	        		//DF20150611 - Rajani Nagaraju - Commenting loggers here and placing the same under finally block
	        		//iLogger.info("FADS: Total Vins to be processed: "+unprocessFileArray.size());
	        		//iLogger.info("FADS: Number of records in two-dimesional array:"+totalFilesInArray);
	        		
	        		//iLogger.info("Two Dimensional Array:");
	        		/*for(int i=0; i<unprocessFileArray.size(); i++)
	        		{
	        			iLogger.info("Row :"+i);
	        			for(int j=0; j<unprocessFileArray.get(i).size(); j++)
	        			{
	        				iLogger.info(unprocessFileArray.get(i).get(j));
	        			}
	        		}*/
        		
	        		//Step 3: Iterate the List Vertically and for each record find whether the file exists in Inprocess Folder
	        		//Initializing unprocessIndexList
	        		List<Integer> indexList = new LinkedList<Integer>();
	        		for(int i=0; i<unprocessFileArray.size(); i++)
	        		{
	        			indexList.add(i);
	        		}
	        		//DF20150409 - Rajani Nagaraju - Commenting Loggers 
	        		//iLogger.info("indexList:"+indexList);
        		
	        		int jCounter=0;
	        		int iCounter=0;
	        		int indexCounter=0;
	        		FileProcessAssetDiagnosticImpl fileObj = new FileProcessAssetDiagnosticImpl();
	        		
	        		Iterator<Integer> outerindexItr = indexList.iterator();
	        		while(outerindexItr.hasNext())
	        		{
	        			int outerIndex = outerindexItr.next();
	        			indexCounter=0;
	        			//iCounter=indexList.get(indexCounter);
	        			String[] InprocessfileList = new File(DataPackets_InProcessFolderPath).list();
	        			String inProcessfileNames =null;
		    			if(InprocessfileList!=null)
		    			{
		    				inProcessfileNames = Arrays.toString( InprocessfileList );
		    			}
	        			
	        			
		    			List<Integer> reprocessIndexList = new LinkedList<Integer>();
		    			Iterator<Integer> indexItr = indexList.iterator();
		    			
		    			while( indexItr.hasNext() )
	        			{
		    				iCounter=indexItr.next();
		    				//DF20150409 - Rajani Nagaraju - Commenting Loggers 
		    				//iLogger.info("iCounter: "+iCounter);
		    				//iLogger.info("jCounter: "+jCounter);
	        				if (jCounter>=(unprocessFileArray.get(iCounter).size()))
	        				{
	        					indexItr.remove();
	        				}
	        				else
	        				{
	        					//Find whether the file exists in InProcess folder for that VIN
	        					File processFile = new File(DataPackets_UnprocessedFolderPath+"/"+unprocessFileArray.get(iCounter).get(jCounter));
	        					//DF20150409 - Rajani Nagaraju - Commenting Loggers 
	        					//iLogger.info("File to be processed:"+processFile.getName());
	        					
	        					if((inProcessfileNames!=null && inProcessfileNames.contains( processFile.getName().split("_")[1])))
	        					{
	        						reprocessIndexList.add(iCounter);
	        						//iCounter++;
	        						continue;
	        					}
	        					
	        					else
	        					{
	        						//Move the files to InprocessFolder
	        						fileObj.processAssetTypeData(processFile,DataPackets_InProcessFolderPath);
	        					}
	        					
	        				}
	        				//indexCounter++;
	        			}
		    			
		    			//Scan reprocessIndex List for 5 times
		    			if(reprocessIndexList.size()>0)
		    			{	
		    				// sleep for 3 sec, so that VINs would get processed from Inprocess folder
		    				//DF20150409 - Rajani Nagaraju - Commenting Loggers 
		    				//iLogger.info("Sleep for 3 sec");
			        		Thread.currentThread().sleep(3000);
		    				
		    				for(int l=0; l<3; l++)
			    			{
			    				InprocessfileList = new File(DataPackets_InProcessFolderPath).list();
			        			inProcessfileNames =null;
				    			if(InprocessfileList!=null)
				    			{
				    				inProcessfileNames = Arrays.toString( InprocessfileList );
				    			}
			    				Iterator<Integer> iter = reprocessIndexList.iterator();
			    				
			    				while(iter.hasNext())
			    				{
			    					int indexing = iter.next();
			    					//Find whether the file exists in InProcess folder for that VIN
		        					File processFile = new File(DataPackets_UnprocessedFolderPath+"/"+unprocessFileArray.get(indexing).get(jCounter));
		        					//DF20150409 - Rajani Nagaraju - Commenting Loggers 
		        					//iLogger.info("Reprocessing Counter:+"+l+", File to be processed:"+processFile.getName());
		        					
		        					if((inProcessfileNames!=null && inProcessfileNames.contains( processFile.getName().split("_")[1])))
		        					{
		        						continue;
		        					}
		        					
		        					else
		        					{
		        						//Move the files to InprocessFolder
		        						fileObj.processAssetTypeData(processFile,DataPackets_InProcessFolderPath);
		        						//reprocessIndexList.remove(indexing);
		        						iter.remove();
		        					}
		        					
		        					
		        					
			    				}
			    			}
		    			}
	        			//Even after five attempts if the file is not processed, dont process the VIN in the current pass
		    			for(int i=0; i<reprocessIndexList.size(); i++)
		    			{
		    				//DF20150409 - Rajani Nagaraju - Commenting Loggers 
		    				//iLogger.info("Skip VIN in the current pass:"+reprocessIndexList.get(i));
		    				indexList.remove(reprocessIndexList.get(i));
		    			}
		    			
	        			jCounter++;
	        			
	        			outerindexItr = indexList.iterator();
	        		}
        		
        		
        		}
        			    
		    	catch(IOException e)
		    	{
		    		fLogger.fatal("Exception in IO for the XML data file : "+e);
		    	}
		    		
		    		
		    	finally
		    	{
		    		fileProcessingFlag=0;
		    	}
        	}
        	
        	        	
        	else
        	{
        		iLogger.info("FADS: File Processing is already in progress by other thread");
        		return;
        	}
    			
        }	
        
        catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
        
        //DF20150611 - Rajani Nagaraju - Adding finally block for better logging
        finally
        {
        	long endTime = System.currentTimeMillis();
        	iLogger.info("FADS:"+startDate+"|Total VIN:"+unprocessFileArray.size()+"|Total Files:"+totalFilesInArray+"|Total Time(ms):"+(endTime-startTime));
		
        }
	}
	
	
	

	/** This method processes XML Data Packets from Unprocessed Folder and place the file into Processed Folder
	 * @param processingFile File to be processed
	 * @param inProcessFolderPath intermediate folder path to which the file has to be moved to, once AssetDiagnosticService is called
	 * @throws IOException
	 */
	public void processAssetTypeData(File processingFile, String inProcessFolderPath) throws IOException
	{
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    
		BufferedReader br = null;
		File archivedFile = null;
		
		try
		{
			br = new BufferedReader(new FileReader(processingFile));
			//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors
			String xmlDataPacket = processingFile.getName()+"|";
			//String xmlDataPacket = "";
			String line = br.readLine();
	
			while (line != null) 
			{
				xmlDataPacket = xmlDataPacket+ line; 	
				line = br.readLine();
			}
			
			 //Make an Asynchronous call to AssetDiagnosticImpl to start processing the read XML file
			ExecutorService executor = Executors.newSingleThreadExecutor();
			AssetDiagnosticImpl implObj = new AssetDiagnosticImpl();
			implObj.inputXmlPacket=xmlDataPacket;
			
			//infoLogger.info(" From File Processing Impl: "+xmlDataPacket);
			Callable<String> worker = implObj;
			Future<String> submit =executor.submit(worker);
			//infoLogger.info(" End of File Processing Impl: "+xmlDataPacket);
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception in reading the XML file :"+processingFile.getName()+": " +e);
			
		}
		
		finally
		{
			if(br!=null)
				br.close();	 
		}
		
		
		//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - Commenting the below code and moving it to AssetMonitoringDetailsBO
		 //move the file to archived folder
		//DefectID20140408 - Rajani Nagaraju - To avoid the packets being picked up by two different threads, introducing an intermediate Folder Path:
		//This method will call AssetDiagnosticService and immediately move the file to InProcess_XMLs folder
		/*archivedFile = new File(processedFolderPath,processingFile.getName());
		
		if(archivedFile.exists())
			archivedFile.delete();
        boolean moveStatus = processingFile.renameTo(archivedFile);
      
        if(moveStatus)
        	infoLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
        	
       */
		archivedFile = new File(inProcessFolderPath,processingFile.getName());
		
		/*if(archivedFile.exists())
			archivedFile.delete();*/
        boolean moveStatus = processingFile.renameTo(archivedFile);
      
        if(moveStatus)
        	iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to InProcess folder");
	}
}
