package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.ExtendedWarrantyServiceBo;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class ExtendedWarrantyBatchServiceImpl implements Runnable
{
	Thread t;
	String serialNumber=null;
	
	public ExtendedWarrantyBatchServiceImpl()
	{
		
	}
	
	public ExtendedWarrantyBatchServiceImpl(String serialNumber)
	{
		this.serialNumber=serialNumber;
		
		t = new Thread(this, "ExtendedWarrantyBatchServiceImpl");
		t.start();
	}
	
	public void run()
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		
		List<String> vinList = new LinkedList<String>();
		
		//STEP1: Get the list of VINs for which Extended warranty file is received, but not yet enabled in the system
		try
		{
			if(this.serialNumber==null)
			{
				String query = "select Serial_Number from asset where extended_warranty_flag=1 and ExtendedWarrantyStartDate is null";
				con = new ConnectMySQL().getConnection();
				stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next())
				{
					vinList.add(rs.getString("Serial_Number"));
				}
			}
			else
			{
				vinList.add(this.serialNumber);
			}
			
		}
		catch(Exception e)
		{
			fLogger.fatal("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyBatchServiceImpl:Exception:"+e.getMessage());
		}
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(con!=null)
					con.close();
				
			}
			catch(Exception e)
			{
				fLogger.fatal("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyBatchServiceImpl:Exception in closing MySQL connection:"+e.getMessage(),e);
			}
		}
		
		iLogger.info("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyBatchServiceImpl:Number of VINs considered in the current run:"+vinList.size());
	
	
		//STEP2: Update the extended warranty deatails for each VIN
		long startTime = System.currentTimeMillis();
		for(int i=0; i<vinList.size(); i++ )
		{
			new ExtendedWarrantyServiceBo().updateExtendedWarrantyDetails(vinList.get(i));
		}
		long endTime = System.currentTimeMillis();
		iLogger.info("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyBatchServiceImpl:Number of VINs:"+vinList.size()+"; Total Time in ms:"+(endTime-startTime));
	}
}
