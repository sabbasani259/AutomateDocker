package remote.wise.service.implementation;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;


import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AlertPacketDetailsEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;
import remote.wise.util.XmlParser;

public class AlertReprocessImpl 
{
	public String reprocessAlerts(String serialNumber, String createdTimeStartDate, String createdTimeEndDate)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		try
		{
			String alertPktDetails = "from AlertPacketDetailsEntity where serialNumber is not null ";
			if(serialNumber!=null)
				alertPktDetails=alertPktDetails+" and serialNumber='"+serialNumber+"'";
			if(createdTimeStartDate!=null && createdTimeEndDate!=null)
			{
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date createdStartDate= sdf.parse(createdTimeStartDate);
				Date createdEndDate= sdf.parse(createdTimeEndDate);
				alertPktDetails=alertPktDetails+" and createdTimestamp > '"+sdf.format(createdStartDate)+"' " +
						" and createdTimestamp < '"+sdf.format(createdEndDate)+"'";
			}
			alertPktDetails=alertPktDetails+" order by serialNumber,transactionTime ";
			Query alertPacketDetailsQ = session.createQuery(alertPktDetails);
			Iterator alertPacketDetailsItr = alertPacketDetailsQ.list().iterator();
			int loopCounter=0;
			int commitCounter=0;
			while(alertPacketDetailsItr.hasNext())
			{
				/*if(session==null || ! (session.isOpen() ))
	            {
	                        session = HibernateUtil.getSessionFactory().openSession();
	                        session.beginTransaction();
	            }*/
				
				AlertPacketDetailsEntity alertPkt = (AlertPacketDetailsEntity)alertPacketDetailsItr.next();
				
				int alertPacketDetailsId=alertPkt.getPacketDetailsId();
				String otherInfo = alertPkt.getOtherInfo();
				String[] otherInfoArr = otherInfo.split("\\|");
				
				//DF20160822 @Roopa taking prev log fuel level from the table if otherInfoArr length is 10(Bcoz prev log fuel is added later, should not give error for prev records
				
				if(otherInfoArr.length==9 || otherInfoArr.length==10)
				{
					ByteArrayInputStream in = null;
					ObjectInputStream is =null;
					
					try
					{
						//Deserialize Byte array to class object
						byte[] xmlByteArray = alertPkt.getParserObject();
						in = new ByteArrayInputStream(xmlByteArray);
						is = new ObjectInputStream(in);
						XmlParser parser  = ( XmlParser)is.readObject();
						
						iLogger.info("Alert Reprocess:"+alertPkt.getSerialNumber()+":"+alertPkt.getTransactionTime()+" Call AGS START");
						
						String alertGenerationStatus =null;
						if(otherInfoArr.length==9){
						 alertGenerationStatus = new AlertGenerationImpl().generateAlertsNew(parser, alertPkt.getSerialNumber(), alertPkt.getTransactionTime(), otherInfoArr[0], otherInfoArr[1], 
								otherInfoArr[2], otherInfoArr[3], otherInfoArr[4], otherInfoArr[5],
								otherInfoArr[6], otherInfoArr[7], Integer.parseInt(otherInfoArr[8]), null);
						}
						else if(otherInfoArr.length==10){
						 alertGenerationStatus = new AlertGenerationImpl().generateAlertsNew(parser, alertPkt.getSerialNumber(), alertPkt.getTransactionTime(), otherInfoArr[0], otherInfoArr[1], 
									otherInfoArr[2], otherInfoArr[3], otherInfoArr[4], otherInfoArr[5],
									otherInfoArr[6], otherInfoArr[7], Integer.parseInt(otherInfoArr[8]), otherInfoArr[9]);	
						}
						
						iLogger.info("Alert Reprocess:"+alertPkt.getSerialNumber()+":"+alertPkt.getTransactionTime()+" Call AGS END");
						
						
						//--------------- If the alert is generated successfully, delete the record from AlertPacketDetails table
						/*if(! (session.isOpen() ))
			            {
			                        session = HibernateUtil.getSessionFactory().getCurrentSession();
			                        session.getTransaction().begin();
			            }*/
						if(alertGenerationStatus.equalsIgnoreCase("SUCCESS"))
						{
							if(session==null || ! (session.isOpen() ))
				            {
				                        session = HibernateUtil.getSessionFactory().openSession();
				                        session.beginTransaction();
				                        
				                        iLogger.info("Alert Reprocess:"+"Opening new session");       
				            }
							
							iLogger.info("Alert Reprocess:"+alertPkt.getSerialNumber()+":"+alertPkt.getTransactionTime()+":Inside AlertPacketDetail deletion::");
							
							Query deletePktDetailsQ = session.createQuery(" delete from AlertPacketDetailsEntity where packetDetailsId="+alertPacketDetailsId);
							int rowsAffected = deletePktDetailsQ.executeUpdate();
							if(rowsAffected>0)
							{
								iLogger.info("Alert Reprocess:"+alertPkt.getSerialNumber()+":"+alertPkt.getTransactionTime()+": Record deleted from alert packet details with ID: "+alertPacketDetailsId);
							}
						}
						
					}
					
					catch(Exception e)
					{
						fLogger.fatal("Alert Reprocess:"+alertPkt.getSerialNumber()+":"+alertPkt.getTransactionTime()+": Exception :"+e);
					}
					
					finally
					{
						if (in != null) 
						      in.close();
						if(is !=null) 
							is.close();
					}
				}
				
				loopCounter=loopCounter+1;
				
				//Commit the records for each 200 transactions
				if(((loopCounter)%200 == 0)){
					commitCounter++;
					iLogger.info("Alert Reprocess:Committing set of 200 Records::"+commitCounter+" ");
					
					try
					{
						if(session!=null && session.isOpen())  {
							session.flush();
							session.getTransaction().commit();
							if(session!=null && session.isOpen()){
								iLogger.info("Alert Reprocess:"+"Closing session");  
								session.close();
							}
						}
							
						
					}
					catch(Exception e)
					{
						fLogger.fatal("Alert Reprocess: committing Exception :"+e);
						iLogger.info("Alert Reprocess: committing Exception :"+e);
					}
					/*if(session !=null && session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}*/
					loopCounter=0;
				}
				
			}
			if(session!=null && session.isOpen())  {
				session.flush();
			}
			if(session !=null && session.getTransaction().isActive())
			{
				session.getTransaction().commit();
			}
		}
		
		catch(Exception e)
		{
			status="FAILURE";
			fLogger.fatal(" Alert Reprocess: Exception: "+e);
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
				fLogger.fatal(" Alert Reprocess: Exception :"+e);
			}

			if(session!=null && session.isOpen())
			{
				if(session.getTransaction().isActive())
					session.flush();
				session.close();
			}
		}
		
		return status;
	}
}
