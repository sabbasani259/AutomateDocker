package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetServiceScheduleEntity;
import remote.wise.businessentity.ServiceScheduleEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

public class ExtendedWarrantyServiceBo {

	public String setWarrantyDetails(String vinNumber, String callTypeId,
			String monthlyVisitFlag, String extendedWarantyFlag,
			String messageId, String fileRef, String process,
			String reprocessJobCode, int assetTypeId, int assetGroupId,
			String installationDate, int primaryOwnerId,AssetEntity ae) {
		

		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		try{
				LinkedHashMap<Integer, ServiceScheduleEntity> servSchDurationMap = new LinkedHashMap<Integer, ServiceScheduleEntity>();
				
				iLogger.info("Inside setWarrantyDetails()");
				
				if(callTypeId!=null&&assetTypeId!=0&&assetGroupId!=0){
					
					String query="";
					if(monthlyVisitFlag.equals("0")){
						query = "from ServiceScheduleEntity ss where ss.assetTypeId='"+ assetTypeId + "' and ss.assetGroupId='"+ assetGroupId + "' and ss.callTypeId='"+callTypeId+"' and ss.ServiceType='Extended Warranty'"; 
					}
					else if(monthlyVisitFlag.equals("1")){
						
						query = "from ServiceScheduleEntity ss where ss.assetTypeId='"+ assetTypeId + "' and ss.assetGroupId='"+ assetGroupId + "' and ss.callTypeId='"+callTypeId+"' and ss.ServiceType='Extended Warranty' and ss.MonthlyVisit=1";
					}
					iLogger.info("Query :"+query+" monthlyVisitFlag :"+monthlyVisitFlag);
					Iterator itrquery = session.createQuery(query).list().iterator();
					
					while (itrquery.hasNext()) {
						ServiceScheduleEntity serviceSchedule = (ServiceScheduleEntity)itrquery.next();
						ae.setExtendedWarrantyType(serviceSchedule.getCallType());
						servSchDurationMap.put(serviceSchedule.getServiceScheduleId(), serviceSchedule);
					}
					
				}
				
				else{
					fLogger.fatal("Mandatory parameter callTypeId or assetTypeId or assetGroupId missing for Vin: "+vinNumber+" in ServiceSchedule table.");
					return "Failure";
				}
				
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date vinInstallationDate=null;
				try
				{
					vinInstallationDate = dateFormat.parse(installationDate);
				}
				catch(Exception e)
				{
					
					fLogger.fatal("EA Processing: AssetInstallation: : Date Parse Exception for Installation Date"+e);
					return "Failure";
				}
				
				//to get dealer entity.
				AccountEntity dealerEntity = null;
				Query accountQuery = session.createQuery("select a.parent_account_id from AccountEntity a where a.status=true and a.account_id='"+primaryOwnerId+"'");
				Iterator accountItr = accountQuery.list().iterator();
				while(accountItr.hasNext())
				{
					dealerEntity = (AccountEntity)accountItr.next();
				}
				
				
				/*Query servSch = session.createQuery(" from AssetServiceScheduleEntity ass inner join ass.serviceScheduleId ss where ass.serialNumber ='"+vinNumber+"' and lower(ss.serviceType)='free' order by ss.serviceScheduleId desc");
				servSch.setMaxResults(1);
				Iterator servSchItr = servSch.list().iterator();
				int freeServiceEngHrs=-1;
				while(servSchItr.hasNext())
				{
					ServiceScheduleEntity ss = (ServiceScheduleEntity) servSchItr.next();
					freeServiceEngHrs=(int)ss.getEngineHoursSchedule();
				}*/
				Query servSch = session.createQuery(" from ServiceScheduleEntity where assetGroupId="+ae.getProductId().getAssetGroupId().getAsset_group_id()+" and " +
						" assetTypeId="+ae.getProductId().getAssetTypeId().getAsset_type_id()+" and engineTypeId="+ae.getProductId().getEngineTypeId().getEngineTypeId()+" " +
						" and ServiceType='Free'  order by serviceScheduleId desc");
					servSch.setMaxResults(1);
					Iterator servSchItr = servSch.list().iterator();
					int freeServiceEngHrs=-1;
					while(servSchItr.hasNext())
					{
						ServiceScheduleEntity ss = (ServiceScheduleEntity) servSchItr.next();
						freeServiceEngHrs=(int)ss.getEngineHoursSchedule();
					}
				
				Query assetServSch = session.createQuery(" from AssetServiceScheduleEntity where serialNumber ='"+vinNumber+"'");
				Iterator assetServSchItr = assetServSch.list().iterator();
				while(assetServSchItr.hasNext()){
					AssetServiceScheduleEntity assetService = (AssetServiceScheduleEntity) assetServSchItr.next();
					int ssId = assetService.getServiceScheduleId().getServiceScheduleId();
					if(servSchDurationMap.containsKey(ssId))
						servSchDurationMap.remove(ssId);
				}
					
				//Insert the records into AssetService Schedule
				for(int j=0; j<servSchDurationMap.size(); j++)
				{
		
					iLogger.info("ExtentedWarrantyLog: "+vinNumber+":AssetServiceScheduleEntity  inserting the record");
		
					ServiceScheduleEntity serSch = (ServiceScheduleEntity)servSchDurationMap.values().toArray()[j];
					int duration = serSch.getDurationSchedule();
					
					//get the scheduledDate
					Calendar c = Calendar.getInstance();
					c.setTime(vinInstallationDate);
					c.add(Calendar.DATE, duration);
					Timestamp scheduledDate = new Timestamp(c.
							getTime().getTime());
		
					AssetServiceScheduleEntity assetServSchEnt = new AssetServiceScheduleEntity();
					assetServSchEnt.setDealerId(dealerEntity);
					assetServSchEnt.setScheduledDate(scheduledDate);
					assetServSchEnt.setSerialNumber(ae);
					assetServSchEnt.setServiceScheduleId(serSch);
		
					assetServSchEnt.setEngineHoursSchedule(serSch.getEngineHoursSchedule());
					
					//CR212 - 20210713 - Rajani Nagaraju - Extended Warranty Changes
					/*if(freeServiceEngHrs==0){
						assetServSchEnt.setAlertGenFlag(0);
					}
					else{
						assetServSchEnt.setAlertGenFlag(1);
						
					}*/
					assetServSchEnt.setAlertGenFlag(0);
					
					session.save(assetServSchEnt);
				}
				/*if(freeServiceEngHrs!=0){
					String updateStmnt = "UPDATE AssetServiceScheduleEntity  set alertGenFlag = 0 "  + 
				             "WHERE serviceScheduleId.ServiceType = 'paid'";
					Query query = session.createQuery(updateStmnt);
					int result = query.executeUpdate();
				}*/
				
				//CR212 - 20210713 - Rajani Nagaraju - Extended Warranty Changes - Commented out the below logic as it will be taken care in batch service
				/*if(freeServiceEngHrs!=0){
					String assQuery="from AssetServiceScheduleEntity a where a.serialNumber= '"+ vinNumber + "'";
					Iterator itrassQuery = session.createQuery(assQuery).list().iterator();
					while(itrassQuery.hasNext()){
						AssetServiceScheduleEntity asse=(AssetServiceScheduleEntity)itrassQuery.next();
						if("paid".equalsIgnoreCase(asse.getServiceScheduleId().getServiceType())){
							asse.setAlertGenFlag(0);
							session.save(asse);
						}
					}
				}*/
		}
		catch(Exception e){
			fLogger.fatal("ExtendedWarrantyServiceBo - : Exception :"+e,e);
			
			//update fault details table.
			
			String messageString = vinNumber+"|"+callTypeId+"|"+monthlyVisitFlag+"|"+
					extendedWarantyFlag+"|"+messageId+"|"+fileRef+"|"+reprocessJobCode;

			fLogger.fatal("Extended Warranty - "+messageString+" Exception:: Put the record into fault details");

			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			
			errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, 
					reprocessJobCode,"Error while proccessing ExtendedWarrantyServiceBo. Please check fatal log.","0002","Service Invokation");
			
			if(messageId!=null && messageId.split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}
		}

		//DF20150619 - Rajani Nagaraju - WISE Down issue - Adding finally block and closing the session
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
				fLogger.fatal("ExtendedWarrantyService - "+"Exception in commiting the record:"+e2);

			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
		} 
		return "Success";
	
		
	}
	
	//**********************************************************************************************************************************************
	//CR212 - 20210713 - Rajani Nagaraju - Extended Warranty Changes
	public String updateExtendedWarrantyDetails(String serialNumber)
	{
		String status="SUCCESS";
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		
		List<String> updateStatements = new LinkedList<String>();
		
		
		try
		{
			int updateAlertGenFlag=0, scheduleDateUpdated=0, engHoursUpdated=0;
			
			//Connection to read data from DB
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
		
			//Get the ExtendedWarranty type defined for the VIN
			Timestamp scheduledDate=null;
			String scheduledDateAsString = null;
			double engineHoursSchedule=0, currentCMH=0;
			int EngineHoursTrigger=0,DateTrigger=0,UpdateEngineHours=0,UpdateScheduledDates=0,serviceScheduleId=0,EW_Type_ID=0;
			String query = "select a.Scheduled_Date as scheduledDate,a.EngineHours_Schedule as engineHoursSchedule," +
					" b.Duration_Schedule as ss_durationSchedule,b.EngineHours_Schedule as ss_engineHoursSchedule," +
					" c.EngineHoursTrigger,c.DateTrigger,c.UpdateEngineHours,c.UpdateScheduledDates, JSON_EXTRACT(TxnData, '$.CMH') as currentCMH," +
					" b.serviceScheduleId, b.EW_Type_ID " +
					" from asset_service_schedule a, service_schedule b, Extended_Warranty_Type c, asset_monitoring_snapshot d" +
					" where a.Service_Schedule_Id=b.servicescheduleId and b.EW_Type_ID=c.EW_Type_ID and a.SerialNumber=d.serial_number " +
					" and a.SerialNumber='"+serialNumber+"' and b.service_type='Extended Warranty' order by a.EngineHours_Schedule limit 1";
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				scheduledDate = rs.getTimestamp("scheduledDate");
				scheduledDateAsString= new SimpleDateFormat("yyyy-MM-dd").format(scheduledDate);
				engineHoursSchedule = rs.getDouble("engineHoursSchedule");
				EngineHoursTrigger=rs.getInt("EngineHoursTrigger");
				DateTrigger=rs.getInt("DateTrigger");
				UpdateEngineHours=rs.getInt("UpdateEngineHours");
				UpdateScheduledDates=rs.getInt("UpdateScheduledDates");
				serviceScheduleId=rs.getInt("serviceScheduleId");
				EW_Type_ID=rs.getInt("EW_Type_ID");
				
				String currentCMHFromAMS = rs.getString("currentCMH");
				if(currentCMHFromAMS!=null)
				{
					currentCMHFromAMS=currentCMHFromAMS.replaceAll("\"", "");
					currentCMH = Double.parseDouble(currentCMHFromAMS);
				}
			}
			
			//Taking currentDate - 7 days for comparison, as the alert generation flag should be enabled 7 days before since due alert has to be generated
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.DATE, 7);
			Date compareDate = cal.getTime();
			Timestamp dateToCompare= new Timestamp(compareDate.getTime());
			
			if(DateTrigger==1)//check if scheduled date for the first service under extended warranty <= current date
			{
				iLogger.info("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyServiceBo:updateExtendedWarrantyDetails:scheduledDate:"+scheduledDate+";" +
						"dateToCompare:"+dateToCompare);
				if(scheduledDate!=null && ( scheduledDate.before(dateToCompare) || scheduledDate.equals(dateToCompare) ) )
				{
					updateAlertGenFlag=1;
				}
			}
			
			if(EngineHoursTrigger==1 && updateAlertGenFlag==0)
			{
				//We need to enable alert generation 50 hours before the scheduled hours so that due alert will be generated
				if(currentCMH >= (engineHoursSchedule-50) )
				{
					updateAlertGenFlag=1;
				}
			}
			
			if(updateAlertGenFlag==0)
			{
				iLogger.info("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyServiceBo:updateExtendedWarrantyDetails:No update required for the VIN:"+serialNumber);
				return status;
			}
			
			//STEP1: Update asset table and set extended warranty date = current date
			String formattedCurrentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String assetUpdateQ = "update asset set ExtendedWarrantyStartDate='"+formattedCurrentDate+"' where serial_number='"+serialNumber+"'";
			updateStatements.add(assetUpdateQ);
			
			//STEP2: Update asset_service_schedule table set alert_gen_flag=0 for beyond warranty services
			String beyondWarrantySchedules = "select a.Asset_Svc_Schd_Id as assetServiceScheduleId,a.Service_Schedule_Id as serviceScheduleId" +
					" from asset_service_schedule a, service_schedule b" +
					" where a.Service_Schedule_Id=b.servicescheduleId" +
					" and a.SerialNumber='"+serialNumber+"' and b.service_type='Paid'";
			rs=stmt.executeQuery(beyondWarrantySchedules);
			while(rs.next())
			{
				String beyondWarrantyupdateQuery = "update asset_service_schedule set alert_gen_flag=0 where Asset_Svc_Schd_Id="+rs.getInt("assetServiceScheduleId");
				updateStatements.add(beyondWarrantyupdateQuery);
			}
			
			//STEP3: Update asset_service_schedule table set alert_gen_flag=1 for the extended warranty services
			String serviceScheduleDetails = "select a.Asset_Svc_Schd_Id as assetServiceScheduleId,a.Service_Schedule_Id as serviceScheduleId," +
					" a.Scheduled_Date as scheduledDate,a.EngineHours_Schedule as engineHoursSchedule," +
					" b.Duration_Schedule as ss_durationSchedule,b.EngineHours_Schedule as ss_engineHoursSchedule" +
					" from asset_service_schedule a, service_schedule b" +
					" where a.Service_Schedule_Id=b.servicescheduleId " +
					" and a.SerialNumber='"+serialNumber+"'" +
					" and b.service_type='Extended Warranty' order by a.EngineHours_Schedule";
			rs = stmt.executeQuery(serviceScheduleDetails);
			
			int recordCount=0;
			Timestamp newScheduleStartDate = null;
			Timestamp firstScheduleDate=null;
			
			while(rs.next())
			{
				recordCount++;
				if(recordCount==1)
				{
					firstScheduleDate=rs.getTimestamp("scheduledDate");
				}
				
				if(UpdateScheduledDates==1 && (currentCMH >= (engineHoursSchedule -50 )))
				{
					if(recordCount==1)
					{
						int durationSchedule = rs.getInt("ss_durationSchedule");
						Calendar c = Calendar.getInstance();
						c.setTime(new Date());
						c.add(Calendar.DATE, -durationSchedule);
						newScheduleStartDate = new Timestamp(c.getTime().getTime());
					}
					
					Calendar c1 = Calendar.getInstance();
					c1.setTime(newScheduleStartDate);
					c1.add(Calendar.DATE,rs.getInt("ss_durationSchedule"));
					Timestamp newScheduledDate = new Timestamp(c1.getTime().getTime());
					
					if(recordCount==1)
					{
						firstScheduleDate= new Timestamp(newScheduledDate.getTime());
					}
					
					String newScheduledDateAsString = new SimpleDateFormat("yyyy-MM-dd").format(newScheduledDate);
					String assetServSchUpdateQ = "update asset_service_schedule set alert_gen_flag=1, Scheduled_Date='"+newScheduledDateAsString+"'" +
							" where Asset_Svc_Schd_Id="+rs.getInt("assetServiceScheduleId");
					updateStatements.add(assetServSchUpdateQ);
					
					scheduleDateUpdated=1;
				}
				
				else
				{
					String assetServSchUpdateQ = "update asset_service_schedule set alert_gen_flag=1 " +
							" where Asset_Svc_Schd_Id="+rs.getInt("assetServiceScheduleId");
					updateStatements.add(assetServSchUpdateQ);
				}
			}
			
			//STEP4: Insert details to AssetExtendedWarrantyLogDetails table
			String firstScheduleDateAsString = new SimpleDateFormat("yyyy-MM-dd").format(firstScheduleDate);
			String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			
			String insertQ = "INSERT INTO AssetExtendedWarrantyLogDetails (Serial_Number,EW_Type_ID,Service_Schedule_ID,Scheduled_Date,EngineHours_Schedule," +
					"ScheduleStartDate,StartEngHours,CurrentTimestamp) values ('"+serialNumber+"', "+EW_Type_ID+", "+serviceScheduleId+", " +
					"'"+scheduledDateAsString+"', "+engineHoursSchedule+", '"+firstScheduleDateAsString+"', "+currentCMH+", '"+currentTimestamp+"')";
			updateStatements.add(insertQ);
			
			
		}
		
		catch(Exception e)
		{
			status="FAILURE";
			fLogger.fatal("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyServiceBo:updateExtendedWarrantyDetails:Exception:"+e.getMessage(),e);
			return status;
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
				fLogger.fatal("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyServiceBo:updateExtendedWarrantyDetails:Exception in closing MySQL connection:"+e.getMessage(),e);
			}
		}
		
		
		//******************************************** Batch update with roll back functionality****************************
		if(updateStatements!=null && updateStatements.size()>0)
		{
			Connection con1=null;
			Statement stmt1 = null;
			
			try
			{
				//Connection to perform batch update with rollback on failures
				con1 = new ConnectMySQL().getConnection();
				con1.setAutoCommit(false);
				stmt1 = con1.createStatement();
				
				for(int i=0; i<updateStatements.size(); i++)
				{
					stmt1.addBatch(updateStatements.get(i));
				}
				
				stmt1.executeBatch();
				
				con1.commit();
				
				iLogger.info("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyServiceBo:updateExtendedWarrantyDetails:"+serialNumber+"; Extended Warranty enabled");
			}
			catch(SQLException sqlEx)
			{
				status="FAILURE";
				fLogger.fatal("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyServiceBo:updateExtendedWarrantyDetails:Batch commit: SQLException:"+sqlEx.getMessage()+"; Rolling back.");
				try
				{
					con1.rollback();
				}
				catch(Exception e)
				{
					fLogger.fatal("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyServiceBo:updateExtendedWarrantyDetails:Batch commit:Exception in roll back:"+e.getMessage());
				}
			}
			catch(Exception e)
			{
				status="FAILURE";
				fLogger.fatal("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyServiceBo:updateExtendedWarrantyDetails:Batch commit:Exception:"+e.getMessage());
			}
			finally
			{
				try
				{
					if(stmt1!=null)
						stmt1.close();
					if(con1!=null)
						con1.close();
					
				}
				catch(Exception e)
				{
					fLogger.fatal("ExtendedWarrantyEnablerBatchService:ExtendedWarrantyServiceBo:updateExtendedWarrantyDetails:Exception in closing MySQL connection during batch commit:"+e.getMessage(),e);
				}
			}
		}
		else
		{
			iLogger.info("xtendedWarrantyEnablerBatchService:ExtendedWarrantyServiceBo:updateExtendedWarrantyDetails:"+serialNumber+"; Extended Warranty not enabled");
		}
		
		return status;
	}

}
