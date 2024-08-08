package remote.wise.businessobject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.ServiceScheduleEntity;
import remote.wise.dal.DynamicAMS_Doc_DAL;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AMSDoc_DAO;
import remote.wise.service.implementation.AssetServiceScheduleImpl;
import remote.wise.util.HibernateUtil;

public class ServiceScheduleNewBO2 {
	AssetServiceScheduleImpl assetimplObj = null;

	public int getServiceTicketNumber(Session session, String serialNumber, String dbmsPartCode){
		int serviceTicketNumber = 0;

		Query query = session.createQuery("SELECT serviceTicketNumber FROM ServiceHistoryEntity she "+
				" WHERE she.serialNumber='"+serialNumber+"' AND she.dbmsPartCode='"+dbmsPartCode+"'");
		List resultList = query.list();
	//	System.out.println("size +"+resultList.size());
		Iterator iterator = resultList.iterator();
		String param[] = null;
		String strEngineHours = null;

		while (iterator.hasNext()) {
			strEngineHours = iterator.next().toString();
			if (strEngineHours.contains(".")) {
				int totalLength = strEngineHours.length();
				int len = strEngineHours.substring(strEngineHours.indexOf(".")).length() - 1;
				int index = (totalLength - len) - 1;
				strEngineHours = strEngineHours.substring(0, index);
			}
		}
		return serviceTicketNumber;
	}

	/*public int getEngineHours(Session session, String serialNumber){
		int intEngineHours =0;

		//		get engine hours for the serial number

		Query query = session.createQuery("SELECT c.parameterValue FROM AssetMonitoringDetailEntity c "+
				" WHERE c.transactionNumber=( SELECT b.transactionNumber FROM "+
				" AssetMonitoringSnapshotEntity b WHERE b.serialNumber='"+ serialNumber+"')"+
				" AND c.parameterId=(SELECT MAX(a.parameterId) FROM MonitoringParameters a WHERE a.parameterName LIKE 'Hour')");
		List resultList = query.list();
		//System.out.println("size +"+resultList.size());
		Iterator iterator = resultList.iterator();
		String param[] = null;
		String strEngineHours = null;

		while (iterator.hasNext()) {

			strEngineHours = iterator.next().toString();
			if (strEngineHours.contains(".")) {
				int totalLength = strEngineHours.length();
				int len = strEngineHours.substring(strEngineHours.indexOf(".")).length() - 1;
				int index = (totalLength - len) - 1;
				strEngineHours = strEngineHours.substring(0, index);
			}
		}
		//	removing zeroes from the beginning
		//strEngineHours = "212";
		if((strEngineHours!=null)&&(Double.parseDouble(strEngineHours)!=0)){
			strEngineHours = strEngineHours.replaceFirst("^0+(?!$)", "");
		}
		//DefectId:201411 To Handle Null Check
		if(strEngineHours!=null && strEngineHours.length()>0)
			intEngineHours = Integer.parseInt(strEngineHours);
		return intEngineHours;
	}*/
	
	
	public int getEngineHours(Session session, String serialNumber){
		int intEngineHours =0;

		//		get engine hours for the serial number

		/*Query query = session.createQuery("SELECT c.parameterValue FROM AssetMonitoringDetailEntity c "+
				" WHERE c.transactionNumber=( SELECT b.transactionNumber FROM "+
				" AssetMonitoringSnapshotEntity b WHERE b.serialNumber='"+ serialNumber+"')"+
				" AND c.parameterId=(SELECT MAX(a.parameterId) FROM MonitoringParameters a WHERE a.parameterName LIKE 'Hour')");
		List resultList = query.list();
		//System.out.println("size +"+resultList.size());
		Iterator iterator = resultList.iterator();
		String param[] = null;*/
		String strEngineHours = null;

		/*while (iterator.hasNext()) {

			strEngineHours = iterator.next().toString();
			if (strEngineHours.contains(".")) {
				int totalLength = strEngineHours.length();
				int len = strEngineHours.substring(strEngineHours.indexOf(".")).length() - 1;
				int index = (totalLength - len) - 1;
				strEngineHours = strEngineHours.substring(0, index);
			}
		}
		//	removing zeroes from the beginning
		//strEngineHours = "212";
		if((strEngineHours!=null)&&(Double.parseDouble(strEngineHours)!=0)){
			strEngineHours = strEngineHours.replaceFirst("^0+(?!$)", "");
		}
		//DefectId:201411 To Handle Null Check
		if(strEngineHours!=null && strEngineHours.length()>0)
			intEngineHours = Integer.parseInt(strEngineHours);*/
		
		  //DF20161223 @Supriya changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column	
		
		String txnKey = "ServiceScheduleNewBO2:getEngineHours";
		
		/*List<AmsDAO> snapshotObj=new ArrayList<AmsDAO> ();

		DynamicAMS_DAL amsDaoObj=new DynamicAMS_DAL();

		snapshotObj=amsDaoObj.getAMSData(txnKey, serialNumber);*/
		List<AMSDoc_DAO> snapshotObj=new ArrayList<AMSDoc_DAO> ();

		DynamicAMS_Doc_DAL amsDaoObj=new DynamicAMS_Doc_DAL();
		
		snapshotObj=amsDaoObj.getAMSData(txnKey, serialNumber);
		HashMap<String,String> txnDataMap=new HashMap<String, String>();

		if(snapshotObj!=null && snapshotObj.size()>0){
			txnDataMap=snapshotObj.get(0).getTxnData();
			
			if(txnDataMap!=null && txnDataMap.size()>0)
			strEngineHours = txnDataMap.get("CMH");
			}

		//iLogger.debug(txnKey+"::"+"AMS:persistDetailsToDynamicMySql::AMS DAL::getAMSData Size:"+snapshotObj.size());
		
		/*if(snapshotObj.size()>0){
			
			//parameters format in AMS
			//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow
			//temp = false;
		String parameters=snapshotObj.get(0).getParameters();
		String [] currParamList=parameters.split("\\|", -1);
		 //DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 
		 //CMH
		strEngineHours = currParamList[3];*/
		if (strEngineHours!=null && strEngineHours.contains(".")) {
			int totalLength = strEngineHours.length();
			int len = strEngineHours.substring(strEngineHours.indexOf(".")).length() - 1;
			int index = (totalLength - len) - 1;
			strEngineHours = strEngineHours.substring(0, index);
		}
		 
		
		if((strEngineHours!=null)&&(Double.parseDouble(strEngineHours)!=0)){
			strEngineHours = strEngineHours.replaceFirst("^0+(?!$)", "");
		}
		//DefectId:201411 To Handle Null Check
		if(strEngineHours!=null && strEngineHours.length()>0)
			intEngineHours = Integer.parseInt(strEngineHours);
		return intEngineHours;
	}
	
	public List<AssetServiceScheduleImpl> getServiceScheduleNew(String serialNumber){
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		List<AssetServiceScheduleImpl> implList = new LinkedList<AssetServiceScheduleImpl>();		
		

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		AccountEntity dealerId=null;

		int eventIdHrs = 0;
		int eventIdDays= 0;
		int servSchHrs=0;
		int servSchDays=0;
		
		int eventId = 0;
		int schIdForNxtService = 0; 
		
		int schEnginehoursForNxtService = 0;
		Timestamp schDateForNxtService = null;
		HashMap<Integer, String> svcTktsMap = new HashMap<Integer, String>();

		try{
			Object[] resultSet = null; 
			Object[] resultSet2 = null;
			String serviceTktNum = null, scheduleName = null, serviceName = null;
			int serviceScheduleId = 0, engineHoursSchInt = 0, firstRecord = 1;
			Date scheduledDate = null, currentDate = new Date();
			Timestamp scheduledTimestamp= null;
			String extendedWarrantyType=null;
			int intEngineHours = getEngineHours( session, serialNumber);

			//DF20150923 - Rajani Nagaraju - If the service completion record is received for the given VIN, dont check for AlertGeneration
			//for the latest received schedule and also all other schedules before that
			//Eg: Third service alert generated though fifth service completion record is received (May be becoz of HMR decrease. However since 5th service
			//completion is received alert should not be generated for any schedules <= 5th service)
			//------------------ STEP1: Get the list of Service Schedules for which alert should not be generated
		
			
			/*	Query maxServiceCompQ = session.createQuery(" select b from ServiceHistoryEntity a, ServiceScheduleEntity b , AssetServiceScheduleEntity c " +
					" where a.serialNumber='"+serialNumber+"' and a.dbmsPartCode=b.dbmsPartCode and c.serialNumber='"+serialNumber+"' " +
							" and b.serviceScheduleId=c.serviceScheduleId order by b.engineHoursSchedule desc");*/
			
			//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - START
			//Include the schedules only where alertGeneration Flag is 1
			/*Query maxServiceCompQ = session.createQuery(" select b from ServiceHistoryEntity a, ServiceScheduleEntity b , AssetServiceScheduleEntity c " +
					" where a.serialNumber='"+serialNumber+"' and a.serviceScheduleId=b.serviceScheduleId and c.serialNumber='"+serialNumber+"' " +
							" and b.serviceScheduleId = c.serviceScheduleId order by b.engineHoursSchedule desc");*/
			Query maxServiceCompQ = session.createQuery(" select b from ServiceHistoryEntity a, ServiceScheduleEntity b , AssetServiceScheduleEntity c " +
					" where a.serialNumber='"+serialNumber+"' and a.serviceScheduleId=b.serviceScheduleId and c.serialNumber='"+serialNumber+"' " +
							" and b.serviceScheduleId = c.serviceScheduleId and c.alertGenFlag =1" +
							" order by b.engineHoursSchedule desc");
			//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - END
			
			maxServiceCompQ.setMaxResults(1);
			Iterator maxServiceCompItr = maxServiceCompQ.list().iterator();
			long maxEngineHrsSch = 0L;
			if(maxServiceCompItr.hasNext())
			{
				ServiceScheduleEntity servSch = (ServiceScheduleEntity)maxServiceCompItr.next();
				maxEngineHrsSch = servSch.getEngineHoursSchedule();
			}
			
			List<Integer> servicedSchList = new LinkedList<Integer>();
			//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - START
			//For unlimited standard warranty, engineHoursSchedule will be 0 for all the standard warranty services. Hence we need to consider even the 
			//-- schedules with engineHours as 0. Hence commenting the IF condition here. Also consider only the schedules with alertGenFlag =1
			/*if(maxEngineHrsSch!=0)
			{
				Query servicedSchListQ = session.createQuery("select a from ServiceScheduleEntity a, AssetServiceScheduleEntity b " +
						" where a.serviceScheduleId=b.serviceScheduleId and b.serialNumber='"+serialNumber+"' " +
						" and a.engineHoursSchedule <= "+maxEngineHrsSch);
				Iterator servicedSchListItr = servicedSchListQ.list().iterator();
				while(servicedSchListItr.hasNext())
				{
					ServiceScheduleEntity servSch = (ServiceScheduleEntity)servicedSchListItr.next();
					servicedSchList.add(servSch.getServiceScheduleId());
				}
			}*/
			Query servicedSchListQ = session.createQuery("select a from ServiceScheduleEntity a, AssetServiceScheduleEntity b " +
					" where a.serviceScheduleId=b.serviceScheduleId and b.serialNumber='"+serialNumber+"' " +
					" and b.alertGenFlag=1 and a.engineHoursSchedule <= "+maxEngineHrsSch);
			Iterator servicedSchListItr = servicedSchListQ.list().iterator();
			while(servicedSchListItr.hasNext())
			{
				ServiceScheduleEntity servSch = (ServiceScheduleEntity)servicedSchListItr.next();
				servicedSchList.add(servSch.getServiceScheduleId());
			}
			//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - END
			
			// Get DBMS part codes and service completion ticket numbers for given serial number from service history 
			Query serviceTkts = session.createQuery("select serviceScheduleId, serviceTicketNumber " +
					"from ServiceHistoryEntity " +
					"where serialNumber ='" + serialNumber + "' "
					);

			Iterator svcItr1 = serviceTkts.list().iterator();

			while(svcItr1.hasNext()){
				
				resultSet2 = (Object[]) svcItr1.next();

				if(resultSet2[0]!=null){
					if(resultSet2[1]!=null){
						svcTktsMap.put( (Integer)resultSet2[0], String.valueOf(resultSet2[1]));
					} else {
						svcTktsMap.put( (Integer)resultSet2[0], "");
					}
				}
			}
			
			//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - START
			//Consider only the services with AlertGenFlag =1
			/*Query query = session.createQuery(
					" SELECT ae.serial_number, sse.serviceScheduleId, sse.engineHoursSchedule," +
							" ant.scheduledDate, sse.dbmsPartCode, ant.dealerId, " +
							" sse.scheduleName, sse.serviceName " +
							" FROM AssetEntity ae, ServiceScheduleEntity sse, AssetServiceScheduleEntity ant" +
							" WHERE ae.serial_number='" +serialNumber+"' AND ae.serial_number=ant.serialNumber" +
							" AND ant.serviceScheduleId = sse.serviceScheduleId "+
					" ORDER BY sse.engineHoursSchedule ASC");*/
			Query query = session.createQuery(
					" SELECT ae.serial_number, sse.serviceScheduleId, sse.engineHoursSchedule," +
							" ant.scheduledDate, sse.dbmsPartCode, ant.dealerId, " +
							" sse.scheduleName, sse.serviceName, sse.CallType " +
							" FROM AssetEntity ae, ServiceScheduleEntity sse, AssetServiceScheduleEntity ant" +
							" WHERE ae.serial_number='" +serialNumber+"' AND ae.serial_number=ant.serialNumber" +
							" AND ant.serviceScheduleId = sse.serviceScheduleId and ant.alertGenFlag=1 "+
					" ORDER BY sse.engineHoursSchedule ASC");
			//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - END
			
			Iterator iterator = query.list().iterator();
			int loopCounter=0;
			if(session!=null && session.isOpen()){
				session.close();
			}
			while ( iterator.hasNext() ) {
				//System.out.println("Inside While***");
				// Initialize for each row of ressult set
				
				loopCounter++;
				
				assetimplObj = new AssetServiceScheduleImpl();
				resultSet = null; 
				resultSet2 = null;
				serviceTktNum = null; 
				scheduleName = null; 
				serviceName = null;
				serviceScheduleId = 0; 
				engineHoursSchInt = 0; 
				
				scheduledDate = null;
				scheduledTimestamp= null;
				extendedWarrantyType=null;
				resultSet = ( Object[] ) iterator.next();

				if(resultSet[1]!=null){
					serviceScheduleId = (Integer) resultSet[1];
					serviceTktNum = svcTktsMap.get(serviceScheduleId );
				}

				if(resultSet[2]!=null){
					engineHoursSchInt = Integer.valueOf( ( (Long) resultSet[2] ).intValue() );
				}

				if(resultSet[3]!=null){
					scheduledTimestamp = (Timestamp) resultSet[3];
					scheduledDate = new Date(scheduledTimestamp.getTime());
				}

			/*	if(resultSet[4]!=null){
					// For selected DBMS part code get the service completion ticket from hash map
					serviceTktNum = svcTktsMap.get( (String)resultSet[4] );
				}*/

				if(resultSet[5]!=null){
					dealerId = (AccountEntity) resultSet[5];
				}

				if(resultSet[6]!=null){
					scheduleName = (String) resultSet[6].toString();
				}

				if(resultSet[7]!=null){
					serviceName = (String) resultSet[7].toString();
				}
				if(resultSet[8]!=null){
					extendedWarrantyType = (String) resultSet[8].toString();
				}


				// Set serviceTktNum
				if(serviceTktNum!=null)
				{
					firstRecord=1;
					//System.out.println("serviceTktNum " + serviceTktNum);
					//System.out.println("Inside If***");
					
					//set impl obj 
					//DefectId:20140703 @Suprava  sn
					assetimplObj.setServiceScheduleId(serviceScheduleId);
					assetimplObj.setDealerId(dealerId.getAccount_id());
					assetimplObj.setScheduledDate(scheduledTimestamp.toString());
					assetimplObj.setEngineHoursSchedule( Long.valueOf(engineHoursSchInt) );
					assetimplObj.setScheduleName(scheduleName);
					assetimplObj.setServiceName(serviceName);
					assetimplObj.setDealerName(dealerId.getAccount_name());
					//ramu b added on 20200511
					assetimplObj.setExtendedWarrantyType(extendedWarrantyType);
					
					implList.add(assetimplObj);
                    //20140703 en
					
					continue;
				} 
				
				else if (serviceScheduleId!=0 && servicedSchList.contains(serviceScheduleId))
				{
					firstRecord=1;
					//System.out.println("serviceTktNum " + serviceTktNum);
					//System.out.println("Inside If***");
					
					//set impl obj 
					//DefectId:20140703 @Suprava  sn
					assetimplObj.setServiceScheduleId(serviceScheduleId);
					assetimplObj.setDealerId(dealerId.getAccount_id());
					assetimplObj.setScheduledDate(scheduledTimestamp.toString());
					assetimplObj.setEngineHoursSchedule( Long.valueOf(engineHoursSchInt) );
					assetimplObj.setScheduleName(scheduleName);
					assetimplObj.setServiceName(serviceName);
					assetimplObj.setDealerName(dealerId.getAccount_name());
					//ramu b added on 20200511
					assetimplObj.setExtendedWarrantyType(extendedWarrantyType);
					
					implList.add(assetimplObj);
					
					continue;
				}
					
				else 
				{
					if(firstRecord==1){
						firstRecord=0;

						schEnginehoursForNxtService = engineHoursSchInt;
						schDateForNxtService = scheduledTimestamp;
						schIdForNxtService = serviceScheduleId;

						//Service Schedule based on Engine Hours
						//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - START
						//If engineHoursSchedule is 0, then consider only based on date, Hence adding the IF condition if(engineHoursSchInt > 0) 
						if(engineHoursSchInt > 0) 
						{
							if ( (engineHoursSchInt - intEngineHours) < 50 ) 
							{
								servSchHrs = loopCounter;
								
								eventIdHrs = 1;			// YELLOW
								if ( ( engineHoursSchInt - intEngineHours ) < 10 ) {                	
									eventIdHrs = 2;		// YELLOW
									if ( ( engineHoursSchInt - intEngineHours ) < 0 ) {
										eventIdHrs = 3; // RED
									}
								}
							} else {
								eventIdHrs = 0;			// GREEN
							}
						}
						else
						{
							eventIdHrs = 0;			// GREEN based on Engine Hours for Unlimited hours schedule
						}//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - END
						
						
						//Service Schedule based on Days
						Calendar FormatcurrentDate = Calendar.getInstance();
						SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
						String dateNow = formatter1.format(FormatcurrentDate.getTime());
						Date date1 = formatter1.parse(dateNow);
						
						
						int differenceInDays = (int) ((scheduledDate.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
						
						//System.out.println("scheduledDate -- Days" + scheduledDate);
						//System.out.println("currentDate - Days:" + currentDate);
						//System.out.println("scheduledDate.getTime() " + scheduledDate.getTime());
						//System.out.println("currentDate.getTime() " + currentDate.getTime());
						//System.out.println("differenceInDays:" + differenceInDays);
						if(differenceInDays<=7)
						{
							servSchDays=loopCounter;
							
							eventIdDays = 1;		// YELLOW
							if(differenceInDays<=1){                	
								eventIdDays = 2 ;	// YELLOW
								if(differenceInDays<=0){
									eventIdDays = 3;// RED
								}
							}
						} else {
							eventIdDays = 0;		// GREEN	
						}	


					} else {
						firstRecord=0;

						//Service Schedule based on Engine Hours
						//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - START
						//If engineHoursSchedule is 0, then consider only based on date, Hence adding the IF condition if(engineHoursSchInt > 0) 
						if(engineHoursSchInt > 0)
						{
							if((engineHoursSchInt-intEngineHours)<50)
							{
								servSchHrs = loopCounter;
								schEnginehoursForNxtService=engineHoursSchInt;
								schDateForNxtService=scheduledTimestamp;
								schIdForNxtService = serviceScheduleId;
	
								eventIdHrs = 1;			// YELLOW
								if((engineHoursSchInt-intEngineHours)<10){                	
									eventIdHrs = 2;		// YELLOW
									if((engineHoursSchInt-intEngineHours)<0){
										eventIdHrs = 3;	// RED
									}
								}
							}
						}
						else
						{
							eventIdHrs = 0;			// GREEN based on Engine Hours for Unlimited hours schedule
						}//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - END
						
						
						//Service Schedule based on Days
						int differenceInDays = (int) ((scheduledDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24));
						//System.out.println("scheduledDate***" + scheduledDate);
						//System.out.println("currentDate ***:" + currentDate);
						//System.out.println("scheduledDate.*** " + scheduledDate.getTime());
						//System.out.println("currentDate.*** " + currentDate.getTime());
						//System.out.println("differenceInDays:***" + differenceInDays);
						//System.out.println("differenceInDays:***" + differenceInDays);
						if (differenceInDays <= 7 ) 
						{
							servSchDays = loopCounter;
							schEnginehoursForNxtService=engineHoursSchInt;
							schDateForNxtService=scheduledTimestamp;
							schIdForNxtService = serviceScheduleId;

							eventIdDays = 1;		// YELLOW

							if ( differenceInDays <= 1 ) {                	
								eventIdDays = 2;	// YELLOW
								if( differenceInDays <= 0){
									eventIdDays = 3;// GREEN
								}
							}
						}
					}
				}
				//System.out.println("Setting to ImplObj");
				assetimplObj.setServiceScheduleId(serviceScheduleId);
				assetimplObj.setDealerId(dealerId.getAccount_id());
				assetimplObj.setScheduledDate(scheduledTimestamp.toString());
				assetimplObj.setEngineHoursSchedule( Long.valueOf(engineHoursSchInt) );
				assetimplObj.setScheduleName(scheduleName);
				assetimplObj.setServiceName(serviceName);
				assetimplObj.setDealerName(dealerId.getAccount_name());
				//ramu b added on 20200511
				assetimplObj.setExtendedWarrantyType(extendedWarrantyType);
				
				implList.add(assetimplObj);
			}

			// Final severity (Green / Yellow / Red) must be maximum of that computed using Hours and Days across entire schedule for an asset 
			//System.out.println(servSchHrs+": "+servSchDays);
			
			if(servSchHrs>servSchDays)
				eventId = eventIdHrs; 
			
			else if(servSchHrs<servSchDays)
				eventId = eventIdDays;
			
			else if(servSchHrs==servSchDays)
			{
				if (eventIdHrs > eventIdDays) {
					eventId = eventIdHrs; 
				} else {
					eventId = eventIdDays;
				}
			}

			//System.out.println("Engine hours to next service  : "+schEnginehoursForNxtService);	
			//System.out.println("ScheduleDate for next service : "+schDateForNxtService);
			//System.out.println("ScheduleId for next service   : "+schIdForNxtService);

			for (int i=0; i < implList.size(); i++) {				
				if(implList.get(i).getServiceScheduleId() == schIdForNxtService){
					implList.get(i).setHoursToNextService( Long.valueOf(schEnginehoursForNxtService - intEngineHours) );
					implList.get(i).setEventId( eventId );
					implList.get(i).setEngineHoursSchedule( Long.valueOf(schEnginehoursForNxtService) );
					implList.get(i).setServiceScheduleId(schIdForNxtService);
					implList.get(i).setScheduledDate(schDateForNxtService.toString());
					
				//	System.out.println(eventId+": "+schIdForNxtService+" : "+Long.valueOf(schEnginehoursForNxtService - intEngineHours));
				}
			}
		}

		catch(Exception e)
		{
			//DF20160401 - Rajani Nagaraju - Adding stacktrace for better logging
			e.printStackTrace();
			fLogger.fatal("AssetServiceSchedule:"+serialNumber+":"+"Exception :"+e);
			
			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal("AssetServiceSchedule:"+serialNumber+":"+"Exception trace: "+err);
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		finally 
		{
			/*if (session!=null && session.getTransaction().isActive())
				session.getTransaction().commit();
*/
			if(session!=null &&  (session.isOpen()) ) 
			{
				//DF20160401- Rajani Nagaraju - To Handle the Exception: org.hibernate.HibernateException: flush is not valid without active transaction
				/*if( (session.getTransaction()!=null ) &&  (session.getTransaction().isActive()) )
					session.flush();*/
				
				session.close();

			}
		}
		iLogger.info("ServiceScheduleNewBO2 .... "+implList);
		return implList;
	
}

	
	
	public List<AssetServiceScheduleImpl> getServiceSchedule(String serialNumber){

		Logger fLogger = FatalLoggerClass.logger;
		List<AssetServiceScheduleImpl> implList = new LinkedList<AssetServiceScheduleImpl>();		
		

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		AccountEntity dealerId=null;

		int eventIdHrs = 0;
		int eventIdDays= 0;
		int servSchHrs=0;
		int servSchDays=0;
		
		int eventId = 0;
		int schIdForNxtService = 0; 
		
		int schEnginehoursForNxtService = 0;
		Timestamp schDateForNxtService = null;
		HashMap<String, String> svcTktsMap = new HashMap<String, String>();

		try{
			Object[] resultSet = null; 
			Object[] resultSet2 = null;
			String serviceTktNum = null, scheduleName = null, serviceName = null;
			int serviceScheduleId = 0, engineHoursSchInt = 0, firstRecord = 1;
			Date scheduledDate = null, currentDate = new Date();
			Timestamp scheduledTimestamp= null;

			int intEngineHours = getEngineHours( session, serialNumber);

			//DF20150923 - Rajani Nagaraju - If the service completion record is received for the given VIN, dont check for AlertGeneration
			//for the latest received schedule and also all other schedules before that
			//Eg: Third service alert generated though fifth service completion record is received (May be becoz of HMR decrease. However since 5th service
			//completion is received alert should not be generated for any schedules <= 5th service)
			//------------------ STEP1: Get the list of Service Schedules for which alert should not be generated
			Query maxServiceCompQ = session.createQuery(" select b from ServiceHistoryEntity a, ServiceScheduleEntity b , AssetServiceScheduleEntity c " +
					" where a.serialNumber='"+serialNumber+"' and a.dbmsPartCode=b.dbmsPartCode and c.serialNumber='"+serialNumber+"' " +
							" and b.serviceScheduleId=c.serviceScheduleId order by b.engineHoursSchedule desc");
			maxServiceCompQ.setMaxResults(1);
			Iterator maxServiceCompItr = maxServiceCompQ.list().iterator();
			long maxEngineHrsSch = 0L;
			if(maxServiceCompItr.hasNext())
			{
				ServiceScheduleEntity servSch = (ServiceScheduleEntity)maxServiceCompItr.next();
				maxEngineHrsSch = servSch.getEngineHoursSchedule();
			}
			
			List<Integer> servicedSchList = new LinkedList<Integer>();
			if(maxEngineHrsSch!=0)
			{
				Query servicedSchListQ = session.createQuery("select a from ServiceScheduleEntity a, AssetServiceScheduleEntity b " +
						" where a.serviceScheduleId=b.serviceScheduleId and b.serialNumber='"+serialNumber+"' " +
						" and a.engineHoursSchedule <= "+maxEngineHrsSch);
				Iterator servicedSchListItr = servicedSchListQ.list().iterator();
				while(servicedSchListItr.hasNext())
				{
					ServiceScheduleEntity servSch = (ServiceScheduleEntity)servicedSchListItr.next();
					servicedSchList.add(servSch.getServiceScheduleId());
				}
			}
			
			// Get DBMS part codes and service completion ticket numbers for given serial number from service history 
			Query serviceTkts = session.createQuery("select dbmsPartCode, serviceTicketNumber " +
					"from ServiceHistoryEntity " +
					"where serialNumber ='" + serialNumber + "' "
					);

			Iterator svcItr1 = serviceTkts.list().iterator();

			while(svcItr1.hasNext()){
				
				resultSet2 = (Object[]) svcItr1.next();

				if(resultSet2[0]!=null){
					if(resultSet2[1]!=null){
						svcTktsMap.put((String) resultSet2[0], (String) resultSet2[1]);
					} else {
						svcTktsMap.put((String) resultSet2[0], "");
					}
				}
			}

			Query query = session.createQuery(
					" SELECT ae.serial_number, sse.serviceScheduleId, sse.engineHoursSchedule," +
							" ant.scheduledDate, sse.dbmsPartCode, ant.dealerId, " +
							" sse.scheduleName, sse.serviceName " +
							" FROM AssetEntity ae, ServiceScheduleEntity sse, AssetServiceScheduleEntity ant" +
							" WHERE ae.serial_number='" +serialNumber+"' AND ae.serial_number=ant.serialNumber" +
							" AND ant.serviceScheduleId = sse.serviceScheduleId "+
					" ORDER BY sse.engineHoursSchedule ASC");

			Iterator iterator = query.list().iterator();
			int loopCounter=0;
			
			while ( iterator.hasNext() ) {
				//System.out.println("Inside While***");
				// Initialize for each row of ressult set
				
				loopCounter++;
				
				assetimplObj = new AssetServiceScheduleImpl();
				resultSet = null; 
				resultSet2 = null;
				serviceTktNum = null; 
				scheduleName = null; 
				serviceName = null;
				serviceScheduleId = 0; 
				engineHoursSchInt = 0; 
				
				scheduledDate = null;
				scheduledTimestamp= null;

				resultSet = ( Object[] ) iterator.next();

				if(resultSet[1]!=null){
					serviceScheduleId = (Integer) resultSet[1];
				}

				if(resultSet[2]!=null){
					engineHoursSchInt = Integer.valueOf( ( (Long) resultSet[2] ).intValue() );
				}

				if(resultSet[3]!=null){
					scheduledTimestamp = (Timestamp) resultSet[3];
					scheduledDate = new Date(scheduledTimestamp.getTime());
				}

				if(resultSet[4]!=null){
					// For selected DBMS part code get the service completion ticket from hash map
					serviceTktNum = svcTktsMap.get( (String)resultSet[4] );
				}

				if(resultSet[5]!=null){
					dealerId = (AccountEntity) resultSet[5];
				}

				if(resultSet[6]!=null){
					scheduleName = (String) resultSet[6].toString();
				}

				if(resultSet[7]!=null){
					serviceName = (String) resultSet[7].toString();
				}

				// Set serviceTktNum
				if(serviceTktNum!=null)
				{
					firstRecord=1;
					//System.out.println("serviceTktNum " + serviceTktNum);
					//System.out.println("Inside If***");
					
					//set impl obj 
					//DefectId:20140703 @Suprava  sn
					assetimplObj.setServiceScheduleId(serviceScheduleId);
					assetimplObj.setDealerId(dealerId.getAccount_id());
					assetimplObj.setScheduledDate(scheduledTimestamp.toString());
					assetimplObj.setEngineHoursSchedule( Long.valueOf(engineHoursSchInt) );
					assetimplObj.setScheduleName(scheduleName);
					assetimplObj.setServiceName(serviceName);
					assetimplObj.setDealerName(dealerId.getAccount_name());

					implList.add(assetimplObj);
                    //20140703 en
					
					continue;
				} 
				
				else if (serviceScheduleId!=0 && servicedSchList.contains(serviceScheduleId))
				{
					firstRecord=1;
					//System.out.println("serviceTktNum " + serviceTktNum);
					//System.out.println("Inside If***");
					
					//set impl obj 
					//DefectId:20140703 @Suprava  sn
					assetimplObj.setServiceScheduleId(serviceScheduleId);
					assetimplObj.setDealerId(dealerId.getAccount_id());
					assetimplObj.setScheduledDate(scheduledTimestamp.toString());
					assetimplObj.setEngineHoursSchedule( Long.valueOf(engineHoursSchInt) );
					assetimplObj.setScheduleName(scheduleName);
					assetimplObj.setServiceName(serviceName);
					assetimplObj.setDealerName(dealerId.getAccount_name());

					implList.add(assetimplObj);
					
					continue;
				}
					
				else 
				{
					if(firstRecord==1){
						firstRecord=0;

						schEnginehoursForNxtService = engineHoursSchInt;
						schDateForNxtService = scheduledTimestamp;
						schIdForNxtService = serviceScheduleId;

						//Service Schedule based on Engine Hours
						if ( (engineHoursSchInt - intEngineHours) < 50 ) 
						{
							servSchHrs = loopCounter;
							
							eventIdHrs = 1;			// YELLOW
							if ( ( engineHoursSchInt - intEngineHours ) < 10 ) {                	
								eventIdHrs = 2;		// YELLOW
								if ( ( engineHoursSchInt - intEngineHours ) < 0 ) {
									eventIdHrs = 3; // RED
								}
							}
						} else {
							eventIdHrs = 0;			// GREEN
						}

						//Service Schedule based on Days
						
						Calendar FormatcurrentDate = Calendar.getInstance();
						SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
						String dateNow = formatter1.format(FormatcurrentDate.getTime());
						Date date1 = formatter1.parse(dateNow);
						
						
						int differenceInDays = (int) ((scheduledDate.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
						
						//System.out.println("scheduledDate -- Days" + scheduledDate);
						//System.out.println("currentDate - Days:" + currentDate);
						//System.out.println("scheduledDate.getTime() " + scheduledDate.getTime());
						//System.out.println("currentDate.getTime() " + currentDate.getTime());
						//System.out.println("differenceInDays:" + differenceInDays);
						if(differenceInDays<=7)
						{
							servSchDays=loopCounter;
							
							eventIdDays = 1;		// YELLOW
							if(differenceInDays<=1){                	
								eventIdDays = 2 ;	// YELLOW
								if(differenceInDays<=0){
									eventIdDays = 3;// RED
								}
							}
						} else {
							eventIdDays = 0;		// GREEN	
						}	


					} else {
						firstRecord=0;

						//Service Schedule based on Engine Hours
						if((engineHoursSchInt-intEngineHours)<50)
						{
							servSchHrs = loopCounter;
							schEnginehoursForNxtService=engineHoursSchInt;
							schDateForNxtService=scheduledTimestamp;
							schIdForNxtService = serviceScheduleId;

							eventIdHrs = 1;			// YELLOW
							if((engineHoursSchInt-intEngineHours)<10){                	
								eventIdHrs = 2;		// YELLOW
								if((engineHoursSchInt-intEngineHours)<0){
									eventIdHrs = 3;	// RED
								}
							}
						}

						//Service Schedule based on Days
						
						int differenceInDays = (int) ((scheduledDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24));
						//System.out.println("scheduledDate***" + scheduledDate);
						//System.out.println("currentDate ***:" + currentDate);
						//System.out.println("scheduledDate.*** " + scheduledDate.getTime());
						//System.out.println("currentDate.*** " + currentDate.getTime());
						//System.out.println("differenceInDays:***" + differenceInDays);
						//System.out.println("differenceInDays:***" + differenceInDays);
						if (differenceInDays <= 7 ) 
						{
							servSchDays = loopCounter;
							schEnginehoursForNxtService=engineHoursSchInt;
							schDateForNxtService=scheduledTimestamp;
							schIdForNxtService = serviceScheduleId;

							eventIdDays = 1;		// YELLOW

							if ( differenceInDays <= 1 ) {                	
								eventIdDays = 2;	// YELLOW
								if( differenceInDays <= 0){
									eventIdDays = 3;// GREEN
								}
							}
						}
					}
				}
				//System.out.println("Setting to ImplObj");
				assetimplObj.setServiceScheduleId(serviceScheduleId);
				assetimplObj.setDealerId(dealerId.getAccount_id());
				assetimplObj.setScheduledDate(scheduledTimestamp.toString());
				assetimplObj.setEngineHoursSchedule( Long.valueOf(engineHoursSchInt) );
				assetimplObj.setScheduleName(scheduleName);
				assetimplObj.setServiceName(serviceName);
				assetimplObj.setDealerName(dealerId.getAccount_name());

				implList.add(assetimplObj);
			}

			// Final severity (Green / Yellow / Red) must be maximum of that computed using Hours and Days across entire schedule for an asset 
			//System.out.println(servSchHrs+": "+servSchDays);
			
			if(servSchHrs>servSchDays)
				eventId = eventIdHrs; 
			
			else if(servSchHrs<servSchDays)
				eventId = eventIdDays;
			
			else if(servSchHrs==servSchDays)
			{
				if (eventIdHrs > eventIdDays) {
					eventId = eventIdHrs; 
				} else {
					eventId = eventIdDays;
				}
			}

			//System.out.println("Engine hours to next service  : "+schEnginehoursForNxtService);	
			//System.out.println("ScheduleDate for next service : "+schDateForNxtService);
			//System.out.println("ScheduleId for next service   : "+schIdForNxtService);

			for (int i=0; i < implList.size(); i++) {				
				if(implList.get(i).getServiceScheduleId() == schIdForNxtService){
					implList.get(i).setHoursToNextService( Long.valueOf(schEnginehoursForNxtService - intEngineHours) );
					implList.get(i).setEventId( eventId );
					implList.get(i).setEngineHoursSchedule( Long.valueOf(schEnginehoursForNxtService) );
					implList.get(i).setServiceScheduleId(schIdForNxtService);
					implList.get(i).setScheduledDate(schDateForNxtService.toString());
					
				//	System.out.println(eventId+": "+schIdForNxtService+" : "+Long.valueOf(schEnginehoursForNxtService - intEngineHours));
				}
			}
		}

		catch(Exception e)
		{
			//DF20160401 - Rajani Nagaraju - Adding stacktrace for better logging
			e.printStackTrace();
			fLogger.fatal("AssetServiceSchedule:"+serialNumber+":"+"Exception :"+e);
			
			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal("AssetServiceSchedule:"+serialNumber+":"+"Exception trace: "+err);
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		finally 
		{
			if (session.getTransaction().isActive())
				session.getTransaction().commit();

			if(session!=null &&  (session.isOpen()) ) 
			{
				//DF20160401- Rajani Nagaraju - To Handle the Exception: org.hibernate.HibernateException: flush is not valid without active transaction
				if( (session.getTransaction()!=null ) &&  (session.getTransaction().isActive()) )
					session.flush();
				
				session.close();

			}
		}

		return implList;
	
	}
}



/*package remote.wise.businessobject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Query;

import remote.wise.businessentity.AccountEntity;
import remote.wise.service.implementation.AssetServiceScheduleImpl;
import remote.wise.util.HibernateUtil;

public class ServiceScheduleNewBO2 {
	AssetServiceScheduleImpl assetimplObj = null;
	
	public int getServiceTicketNumber(Session session, String serialNumber, String dbmsPartCode){
		int serviceTicketNumber = 0;
		
		Query query = session.createQuery("SELECT serviceTicketNumber FROM ServiceHistoryEntity she "+
				" WHERE she.serialNumber='"+serialNumber+"' AND she.dbmsPartCode='"+dbmsPartCode+"'");
		List resultList = query.list();
		System.out.println("size +"+resultList.size());
		Iterator iterator = resultList.iterator();
		String param[] = null;
		String strEngineHours = null;
		
		while (iterator.hasNext()) {
			strEngineHours = iterator.next().toString();
		if (strEngineHours.contains(".")) {
			int totalLength = strEngineHours.length();
			int len = strEngineHours.substring(strEngineHours.indexOf(".")).length() - 1;
			int index = (totalLength - len) - 1;
			strEngineHours = strEngineHours.substring(0, index);
		}
	}
		return serviceTicketNumber;
	}

	public int getEngineHours(Session session, String serialNumber){
		int intEngineHours =0;
		
//		get engine hours for the serial number
		
		Query query = session.createQuery("SELECT c.parameterValue FROM AssetMonitoringDetailEntity c "+
				" WHERE c.transactionNumber=( SELECT MAX(b.transactionNumber) FROM"+
				" AssetMonitoringHeaderEntity b WHERE b.serialNumber='"+ serialNumber+"')"+
				" AND c.parameterId=(SELECT MAX(a.parameterId) FROM MonitoringParameters a WHERE a.parameterName LIKE 'Hour')");
		List resultList = query.list();
		System.out.println("size +"+resultList.size());
		Iterator iterator = resultList.iterator();
		String param[] = null;
		String strEngineHours = null;
		
		while (iterator.hasNext()) {
			
			strEngineHours = iterator.next().toString();
		if (strEngineHours.contains(".")) {
			int totalLength = strEngineHours.length();
			int len = strEngineHours.substring(strEngineHours.indexOf(".")).length() - 1;
			int index = (totalLength - len) - 1;
			strEngineHours = strEngineHours.substring(0, index);
		}
	}
//	removing zeroes from the beginning
		strEngineHours = "212";
	if((strEngineHours!=null)&&(Double.parseDouble(strEngineHours)!=0)){
		strEngineHours = strEngineHours.replaceFirst("^0+(?!$)", "");
	}
	
	 intEngineHours = Integer.parseInt(strEngineHours);
		return intEngineHours;
	}
	public List<AssetServiceScheduleImpl> getServiceSchedule(String serialNumber){

		List<AssetServiceScheduleImpl> implList = new LinkedList<AssetServiceScheduleImpl>();		

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		try{

			int intEngineHours = getEngineHours(session,serialNumber);
			Query query = session.createQuery("SELECT ae.serial_number, sse.serviceScheduleId, sse.engineHoursSchedule," +
					" ant.scheduledDate, sse.dbmsPartCode , ant.dealerId, sse.scheduleName,sse.serviceName" +
					" FROM AssetEntity ae, ServiceScheduleEntity sse, AssetServiceScheduleEntity ant" +
					" WHERE ae.serial_number='" +serialNumber+"' AND ae.serial_number=ant.serialNumber" +
					" AND ant.serviceScheduleId = sse.serviceScheduleId "+
			" ORDER BY engineHoursSchedule ASC");
			Iterator iterator = query.list().iterator();
			Object[] resultSet = null;
			int serviceScheduleId = 0,engineHoursSchInt=0,intEngineHoursSchedule=0,firstRecord=0;
			int schIdForNxtService=0, schEnginehoursForNxtService = 0;
			long engineHoursSchedule=0l;
			Timestamp scheduledTimestamp= null,schDateForNxtService = null;
			Date scheduledDate = null, currentDate = new Date();
			String serviceTktNum = null;
			String dbmsPartCode=null;
			int EventIdhrs =0;
			int EventIdDays=0;
			AccountEntity dealerId=null;
			String ScheduleName="";
			String serviceName="";
			while(iterator.hasNext()){
				assetimplObj = new AssetServiceScheduleImpl();
				resultSet = (Object[])iterator.next();

				serviceScheduleId = 0;engineHoursSchInt=0;engineHoursSchedule=0l;
				scheduledTimestamp= null; scheduledDate = null;serviceTktNum = null;

				if(resultSet[1]!=null){
					serviceScheduleId = (Integer)resultSet[1];
				}
				if(resultSet[2]!=null){
					engineHoursSchedule = (Long)resultSet[2];
					intEngineHoursSchedule = (int)engineHoursSchedule;
				}
				if(resultSet[3]!=null){
					scheduledTimestamp = (Timestamp)resultSet[3];
					scheduledDate = new Date(scheduledTimestamp.getTime());
				}
				if(resultSet[4]!=null){
					dbmsPartCode =(String)resultSet[4];
					Query serviceTkt = session.createQuery("select serviceTicketNumber from ServiceHistoryEntity where serialNumber ='"+serialNumber+"' and dbmsPartCode='"+dbmsPartCode+"'" );
					Iterator svcItr = serviceTkt.list().iterator();
					while(svcItr.hasNext()){
						if(resultSet[0]!=null){
							serviceTktNum = (String)resultSet[0];
						}
					}
				}
				if(resultSet[5]!=null){
					dealerId =((AccountEntity)resultSet[5]);
				}
				if(resultSet[6]!=null){
					ScheduleName=(String)resultSet[6].toString();
				}

				if(resultSet[7]!=null){
					serviceName=(String)resultSet[7].toString();
				}

				Query EventIdQry =  session.createQuery("select assetEventId from AssetEventEntity where serialNumber ='"+serialNumber+"' and eventTypeId= 1 and activeStatus =1" );
				Iterator eventItr =EventIdQry.list().iterator();
				while(eventItr.hasNext()){
					if(resultSet[0]!=null){
						assetEventId = (Integer)resultSet[0];
					}
				}
				// Set serviceTktNum
				if(serviceTktNum!=null){
					firstRecord=1;
					continue;
				}
				else{
					if(firstRecord==1){
						firstRecord=0;
						schEnginehoursForNxtService=engineHoursSchInt;
						schDateForNxtService=scheduledTimestamp;
						schIdForNxtService = serviceScheduleId;
						                    
	                    if((engineHoursSchInt-intEngineHours)<50){
	                    	EventIdhrs = 1 ;//- YELLOW
	                        if((engineHoursSchInt-intEngineHours)<10){                	
	                        	EventIdhrs = 2;// - YELLOW
	                        	if((engineHoursSchInt-intEngineHours)<0){
	                        		EventIdhrs = 3; //- RED
	                        	}
	                        }
	                    } else {
	                    	EventIdhrs = 0;//Green
	                	}

	                	int differenceInDays = (int) ((scheduledDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24));
	                	if(differenceInDays<=7){
	                		EventIdDays = 1 ;//- YELLOW
	                        if(differenceInDays<=1){                	
	                        	EventIdDays = 2 ;//- YELLOW
	                        	if(differenceInDays<=0){
	                        		EventIdDays = 3;// - RED
	                        	}
	                        }
	                    } else {
	                    	EventIdDays = 0;//Green	
	                    }	
						               
						continue;
					}
					else{
						firstRecord=0;
						//Service Schedule based on Engine Hours
						if((engineHoursSchInt-intEngineHours)<50){
							schEnginehoursForNxtService=engineHoursSchInt;
							schDateForNxtService=scheduledTimestamp;
							schIdForNxtService = serviceScheduleId;
							//	                        severityHrs = 1 - YELLOW
							if((engineHoursSchInt-intEngineHours)<10){                	
								//	                        	 severityHrs = 2 - YELLOW
								if((engineHoursSchInt-intEngineHours)<0){
									//	                        		 severityHrs = 3 - RED
								}
							}
						} else {
							//	HrsBasedNotProc = true
						}

						//Service Schedule based on Date
						int differenceInDays = (int) ((scheduledDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24));
						if(differenceInDays<=7){
							schEnginehoursForNxtService=engineHoursSchInt;
							schDateForNxtService=scheduledTimestamp;
							schIdForNxtService = serviceScheduleId;
							// severityDys = 1 - YELLOW
							if(differenceInDays<=1){                	
								//	                        	 severityDys = 2 - YELLOW
								if(differenceInDays<=0){
									//	                        		 severityDys = 3 - RED
								}
							}
						} else {
							//	                    	DysBasedNotProc = true	
						}
					}

					// If HrsBasedNotProc && DysBasedNotProc
					//		break
				}

				assetimplObj.setServiceScheduleId(serviceScheduleId);
				assetimplObj.setDealerId(dealerId.getAccount_id());
				assetimplObj.setScheduledDate(scheduledTimestamp.toString());
				long svc = (long)engineHoursSchInt;
				assetimplObj.setEngineHoursSchedule(svc);
				assetimplObj.setScheduleName(ScheduleName);
				assetimplObj.setServiceName(serviceName);
				assetimplObj.setDealerName(dealerId.getAccount_name());

				implList.add(assetimplObj);
			}
			// Max of severityDys and severityHrs
			System.out.println("Engine hours to next service  : "+schEnginehoursForNxtService);	
			System.out.println("ScheduleDate for next service : "+schDateForNxtService);
			System.out.println("ScheduleId for next service   : "+schIdForNxtService);
			
			for (int i=0; i < implList.size(); i++) {
				
				if(implList.get(i).getServiceScheduleId()==schIdForNxtService){
					long hrsToNextSvc =  schEnginehoursForNxtService-intEngineHours;
					implList.get(i).setHoursToNextService(hrsToNextSvc);
					implList.get(i).setEventId(assetEventId);
				}
				
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			if (session.getTransaction().isActive())
				session.getTransaction().commit();

			if (session.isOpen()) {
				session.flush();
				session.close();

			}
		}


		return implList;


	}
	
	
}
*/