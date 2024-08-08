package remote.wise.service.implementation;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.EPStage2InstancesEntity;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class VinLoadDistributionImpl 
{
	/*public static WiseLogger fatalError = WiseLogger.getLogger("VinLoadDistributionImpl:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("VinLoadDistributionImpl:","info");*/
	
	public String distributeLoad()
	{
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		try
		{
			List<String> activeInstances = new LinkedList<String>();
			HashMap<String,String> machineServerNodeMap = new HashMap<String,String>();
			Calendar cal = Calendar.getInstance();
			
			//Get the List of Active EP Stage2 instances
			Query activeInstancesQ = session.createQuery(" from EPStage2InstancesEntity where status=1 ");
			Iterator activeInstanceItr = activeInstancesQ.list().iterator();
			while(activeInstanceItr.hasNext())
			{
				EPStage2InstancesEntity instanceEntity = (EPStage2InstancesEntity)activeInstanceItr.next();
				activeInstances.add(instanceEntity.getNodeName());
			}
			
			
			if(activeInstances.size()>0)
			{
				int i=0;
				//get the current date -1
				cal.add(Calendar.DATE, -1);
				String yestDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
				
				//Get the List of active VINs in the order of maximum communication in previous day
				Query communicatedMachinesQ = session.createQuery(" select a.serialNumber, count(*) as count " +
						" from AssetMonitoringHeaderEntity a where a.createdTimestamp like '"+yestDate+"%' " +
								" group by a.serialNumber order by count(*) desc ");
				Iterator communicationMachinesItr = communicatedMachinesQ.list().iterator();
				Object[] result=null;
				while(communicationMachinesItr.hasNext())
				{
					result = (Object[]) communicationMachinesItr.next();
					String serialNumber =( (AssetEntity) result[0]).getSerial_number().getSerialNumber();
					
					String serverNodeName = activeInstances.get(i);
					i++;
					if(i>activeInstances.size()-1)
					{
						i=0;
					}
					
					machineServerNodeMap.put(serialNumber,serverNodeName);
				}
				
				//Get the list of VINS which has not communicated previous day
				Query nonCommMachinesQ = session.createQuery(" select b.serial_number from AssetEntity b where b.serial_number not in " +
						"( select distinct a.serialNumber from AssetMonitoringHeaderEntity a where a.createdTimestamp like '"+yestDate+"%'  )");
				Iterator nonCommMachinesItr = nonCommMachinesQ.list().iterator();
				while(nonCommMachinesItr.hasNext())
				{
					AssetControlUnitEntity assetControl = (AssetControlUnitEntity)nonCommMachinesItr.next();
					String serverNodeName = activeInstances.get(i);
					i++;
					if(i>activeInstances.size()-1)
					{
						i=0;
					}
					
					machineServerNodeMap.put(assetControl.getSerialNumber(),serverNodeName);
				}
					
				//Iterate to the Hashmap to update device info status table
				//Make a connection to smart_systems DB
				Connection con = null;
				Statement statement = null;
				try
				{
					ConnectMySQL connectionObj = new ConnectMySQL();
					con = connectionObj.getEdgeProxyConnection();
					
					Iterator it = machineServerNodeMap.entrySet().iterator();
					 while (it.hasNext()) 
					 {
						 statement = con.createStatement(); 
						 Map.Entry pairs = (Map.Entry)it.next();
					     String updateQuery = " UPDATE device_status_info SET node_name='"+pairs.getValue()+"' where vin_no='"+pairs.getKey()+"'";
					     int rowsUpdated = statement.executeUpdate(updateQuery);
					     
					     if(rowsUpdated>0)
					    	 iLogger.info("VIN Node Distribution: "+pairs.getKey()+" - "+pairs.getValue());
					 }
					 
					 //Update DistributionRunDate.txt to current date
					 String issueCodeFile = "/user/JCBLiveLink/LoadDistribution/DistributionRunDate.txt";
					 FileWriter fw = new FileWriter(issueCodeFile);     
					 BufferedWriter bw = new BufferedWriter(fw);
					 String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					 bw.write(currentDate); 
					 bw.close();
					 
				}
				catch(SQLException e)
				{
					status="FAILURE";
					fLogger.error("VIN Node Distribution failed: "+e);
				}
				
				finally
				{
					 try
					 {
						 if (statement != null) 
							 statement.close(); 
						 				 
						 if(con!=null)
							 con.close();
					 
					 }
					 
					 catch(SQLException e)
					 {
						fLogger.fatal("SQLException :"+e);
					 }
				}
				
			}
			
			
		}
		
		catch(Exception e)
		{
			status="FAILURE";
			fLogger.fatal("Exception :"+e);
			e.printStackTrace();
		}
		
		finally
		{
			if(session.getTransaction().isActive())
			{
				session.getTransaction().commit();
			}
		
			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
		
		}
		
		return status;
	}
}
