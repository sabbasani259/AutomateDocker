package com.wipro.mcoreapp.businessobject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.MoolFaultDetailsEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class MoolErrorMsgHandlerBO 
{
	public String handleErrorMessages(String messageId, String fileName, String process, String messageString, String failureCause)
	{
		String errorMsgInsStatus = "SUCCESS";
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			try
			{
				MoolFaultDetailsEntity faultDetails = new MoolFaultDetailsEntity();
				faultDetails.setFailureCause(failureCause);
				faultDetails.setFileName(fileName);
				faultDetails.setMessageId(messageId);
				faultDetails.setMessageString(messageString);
				faultDetails.setProcess(process);
				session.save(faultDetails);
			}
			
			catch(Exception e)
			{
				errorMsgInsStatus="FAILURE";
				fLogger.fatal("MOOL:EADataPopulation:MoolErrorMsgHandler: process:"+process+":messageString:"+messageString+": Exception in Storing details into mool_fault_details:"+e.getMessage());
			}
			
			finally
			{
				try
				{
					if(session!=null && session.isOpen())  
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e)
				{
					errorMsgInsStatus="FAILURE";
					fLogger.fatal("MOOL:EADataPopulation:MoolErrorMsgHandler: process:"+process+": messageString:"+messageString+": Exception in commiting details into mool_fault_details:"+e.getMessage());
					
				}
	
				if(session.isOpen())
				{
					if(session.getTransaction().isActive())
						session.flush();
					session.close();
				}
	
			}
		}
		
		catch(Exception e)
		{
			errorMsgInsStatus="FAILURE";
			fLogger.fatal("MOOL:EADataPopulation:MoolErrorMsgHandler: process:"+process+": messageString:"+messageString+": Exception in getting Hibernate Session:"+e.getMessage());
		}
		
		return errorMsgInsStatus;
	}
	
	
	public void fileHandler(String messageId, String fileRef, String process, String messageString, String failureCause)
	{
		FileWriter fileWritter=null;
    	BufferedWriter bufferWritter=null;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		try
		{
			String insertStatement = messageId+"MOOLSEP"+fileRef+"MOOLSEP"+process+"MOOLSEP"+messageString+"MOOLSEP"+failureCause;
			
			//Put the record into a file in EAFiles folder if the record cannot be inserted to fault_details table
    		int newFile=0;
    		//----- Create file name
            String fileName=process;
        	String currentDate= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        	fileName=fileName+"_"+currentDate.replaceAll("-", "")+"_1000000"+".txt";
        	File file = new File("/user/JCBLiveLink/EAIntegration/MOOL/FailedRecords/"+fileName);
        		
        	//if file doesnt exists, then create it
        	if(!file.exists())
        	{
        		newFile=1;
        		file.createNewFile();
        	}
        		
        	fileWritter = new FileWriter(file,true);
    	    bufferWritter = new BufferedWriter(fileWritter);
    	    if(newFile==0)
    	       	bufferWritter.write("\n");
    	    bufferWritter.write(insertStatement);
    	}
		
		catch(Exception e)
		{
			fLogger.fatal("MOOL:EADataPopulation:RED ALERT:MoolErrorMsgHandler: FileHandler: process:"+process+": messageString:"+messageString+":Error in storing the details");
		}
		
		finally
		{
			try
			{
				if(bufferWritter!=null)
	    			bufferWritter.close();
		        if(fileWritter!=null)
		        	fileWritter.close();
			}
			
			catch(IOException e)
			{
				fLogger.fatal("MOOL:EADataPopulation:MoolErrorMsgHandler: FileHandler: process:"+process+": messageString:"+messageString+":Error in closing FileWriter");
			}
		}
	}
	
	
	public void fileToDBHandler(String fileName)
	{
		BufferedReader br = null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		try
		{
			String fileStoragePath = "/user/JCBLiveLink/EAIntegration/MOOL/FailedRecords/";
			File[] rejectedFiles=null;
			
			if(fileName==null)
			{
				File folderPath = new File(fileStoragePath);
			}
			else
			{
				rejectedFiles[0]=new File(fileStoragePath+"\\"+fileName);
			}
			
			if(rejectedFiles!=null && rejectedFiles.length>0)
    		{
				for(int i=0; i<rejectedFiles.length; i++)
				{
					File processingFile = rejectedFiles[i];
					
					br = new BufferedReader(new FileReader(processingFile));
					String line = br.readLine();
					
					while(line != null)
					{
						line = br.readLine();
						String[] msgDetails = line.split("MOOLSEP");
						String status = handleErrorMessages(msgDetails[0], msgDetails[1], msgDetails[2], msgDetails[3], msgDetails[4]);
						if(status.equalsIgnoreCase("FAILURE"))
							return;
					}
					
					//ToDo: Delete the processing file
					boolean fileDelStatus = processingFile.delete();
					iLogger.info("MOOL:EADataPopulation:MoolErrorMsgHandler: fileToDBHandler:"+fileName+": Processing File:"+processingFile+": Deletion Status:"+fileDelStatus);
	
				}
    		}
		}
		
		catch(Exception e)
    	{
    		fLogger.fatal("MOOL:EADataPopulation:MoolErrorMsgHandler: fileToDBHandler:"+"Error in reading data from File:"+e.getMessage());
    		
    	}
    	
    	finally
    	{
    		if(br!=null)
				try 
    			{
					br.close();
				} 
    			catch (IOException e) 
    			{
					fLogger.fatal("MOOL:EADataPopulation:MoolErrorMsgHandler: fileToDBHandler:"+"Error in closing Buffered reader:"+e.getMessage());
				}	 
    	}
		
	}
	
	
	public void deleteErrorMessage(String messageId)
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
      //DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
        if(session.getTransaction().isActive() && session.isDirty())
        {
           	iLogger.info("MOOL:EADataPopulation:MoolErrorMsgHandler:deleteErrorMessage:Opening a new session");
           	session = HibernateUtil.getSessionFactory().openSession();
        }
        session.beginTransaction();
        
        try
        {
        	Query query = session.createQuery("delete from MoolFaultDetailsEntity where messageId='"+messageId+"'");
        	query.executeUpdate();
        }
        
        catch(Exception e)
		{
        	fLogger.fatal("MOOL:EADataPopulation:MoolErrorMsgHandler: deleteErrorMessage: "+messageId+":Exception :"+e.getMessage());
		}
        
        finally
        {
        	try
			{
				if(session.isOpen())
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}
			}
			catch(Exception e2)
			{
				fLogger.fatal("MOOL:EADataPopulation:MoolErrorMsgHandler: deleteErrorMessage: Exception in comitting the record:"+e2.getMessage());
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
              
        }
	}
}
