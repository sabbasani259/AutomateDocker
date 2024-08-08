package remote.wise.service.webservice;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetServiceScheduleEntity;
import remote.wise.businessentity.ServiceScheduleEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

@Path("/ExtendedWarantyDailyCheckService")
public class ExtendedWarantyDailyCheckService {

	@Path("/updateTable")
	public void updateTable(){
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		try 
		{
			Query query = session.createQuery("from AssetEntity a where  extendedWarrantyFlag=1");
			Iterator itr= query.list().iterator();
			while(itr.hasNext()){
				AssetEntity asset = (AssetEntity)itr.next();
				String vinNumber=asset.getSerial_number().getSerialNumber();
				Timestamp installDate= asset.getInstall_date();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(installDate.getTime());
			
				Query servSch = session.createQuery(" from ServiceScheduleEntity where assetGroupId="+asset.getProductId().getAssetGroupId().getAsset_group_id()+" and " +
					" assetTypeId="+asset.getProductId().getAssetTypeId().getAsset_type_id()+" and engineTypeId="+asset.getProductId().getEngineTypeId().getEngineTypeId()+" " +
					" and ServiceType='Free'  order by serviceScheduleId desc");
				servSch.setMaxResults(1);
				Iterator servSchItr = servSch.list().iterator();
				int freeServiceEngHrs=-1;
				int durationSchedule=-1;
				while(servSchItr.hasNext())
				{
					ServiceScheduleEntity ss = (ServiceScheduleEntity) servSchItr.next();
					freeServiceEngHrs=(int)ss.getEngineHoursSchedule();
					durationSchedule= ss.getDurationSchedule();
				}
				if(durationSchedule!=-1)
					cal.add(Calendar.DAY_OF_MONTH, durationSchedule);
				Date updatedDate= cal.getTime();
				Date today = Calendar.getInstance().getTime();
				if(freeServiceEngHrs==0 && today.compareTo(updatedDate)>=0)
				{
					Query assetServSch = session.createQuery(" from AssetServiceScheduleEntity ass where ass.serialNumber ='"+vinNumber+"'");
					Iterator assetServSchItr = assetServSch.list().iterator();
					while(assetServSchItr.hasNext())
					{
						AssetServiceScheduleEntity ass=(AssetServiceScheduleEntity)assetServSchItr.next();
						System.out.println(ass.getAlertGenFlag()+" "+ ass.getServiceScheduleId().getServiceType());
						if(ass.getAlertGenFlag()==1 && "Paid".equalsIgnoreCase(ass.getServiceScheduleId().getServiceType())){
							ass.setAlertGenFlag(0);
						}
						else if(ass.getAlertGenFlag()==0 && "Extended Warranty".equalsIgnoreCase(ass.getServiceScheduleId().getServiceType())){
							ass.setAlertGenFlag(1);
						}
						session.save(ass);
					
					}
				
				}
				
		}
		

		
	}


	catch(Exception e)
	{
		//status = "FAILURE-"+e.getMessage();
		fLogger.fatal("EA Processing: AssetInstallation:  Fatal Exception :"+e,e);
	}


	finally
	{
		try
		{
			
			if(session.isOpen())
			{
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}
			}
		}
		catch(Exception e)
		{
			fLogger.fatal("Exception in commiting the record:"+e);
		}
		if(session.isOpen())
		{
			session.flush();
			session.close();
		}
	}
	}
}
