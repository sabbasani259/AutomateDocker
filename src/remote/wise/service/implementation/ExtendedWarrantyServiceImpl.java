package remote.wise.service.implementation;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetServiceScheduleEntity;
import remote.wise.businessobject.ExtendedWarrantyServiceBo;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;

public class ExtendedWarrantyServiceImpl {

	public String updateWarrantyDetails(String vinNumber, String callTypeId,
			String monthlyVisitFlag, String extendedWarantyFlag,
			String messageId, String fileRef, String process,
			String reprocessJobCode) {
		
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response=null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		vinNumber=vinNumber.replaceFirst("^0+(?!$)", "");
		
		try{
				AssetEntity ae=null;	
				AssetServiceScheduleEntity asse=null;
				
				int assetTypeId = 0;
				int assetGroupId = 0;
				String installationDate=null;
				int primaryOwnerId=0;
				
				
				int cancallationFlag=Integer.valueOf(extendedWarantyFlag);
				
				session.beginTransaction();
				String query="from AssetEntity a where a.serial_number like '%"+ vinNumber + "%'";
				iLogger.info("Creating Asset Query hibernate object");
				Iterator itrquery = session.createQuery(query).list().iterator();
				iLogger.info("Created Asset Query hibernate object");
				while(itrquery.hasNext()){
					ae=(AssetEntity)itrquery.next();
					vinNumber = ae.getSerial_number().getSerialNumber();
					assetTypeId = ae.getProductId().getAssetTypeId().getAsset_type_id();
					assetGroupId = ae.getProductId().getAssetGroupId().getAsset_group_id();
					installationDate = ae.getInstall_date().toString();
					primaryOwnerId = ae.getPrimary_owner_id();
					
						if(installationDate!=null)
						{
							if(cancallationFlag==0){
								ae.setExtendedWarrantyFlag(1);
								
								ExtendedWarrantyServiceBo serviceBO = new ExtendedWarrantyServiceBo();
								iLogger.info("calling setWarrantyDetails in serviceBO:"+vinNumber+  callTypeId+
										monthlyVisitFlag+ extendedWarantyFlag+
										messageId+ fileRef+ process+
										reprocessJobCode+assetTypeId+assetGroupId+installationDate+primaryOwnerId+ae);
								response = serviceBO.setWarrantyDetails(vinNumber,  callTypeId,
										monthlyVisitFlag, extendedWarantyFlag,
										messageId, fileRef, process,
										reprocessJobCode,assetTypeId,assetGroupId,installationDate,primaryOwnerId,ae);
								
							}
							else if(cancallationFlag==1){
								ae.setExtendedWarrantyFlag(0);
								
								//Update AssetSerSchd with AlertGen Flag for EW schedules as 0 and BW schedules as 1
								String assQuery="from AssetServiceScheduleEntity a where a.serialNumber= '"+ vinNumber + "'";
								Iterator itrassQuery = session.createQuery(assQuery).list().iterator();
								while(itrassQuery.hasNext()){
									asse=(AssetServiceScheduleEntity)itrassQuery.next();
									if("Extended Warranty".equalsIgnoreCase(asse.getServiceScheduleId().getServiceType())){
										//asse.setAlertGenFlag(0);
										session.delete(asse);
									}
									else if("Paid".equalsIgnoreCase(asse.getServiceScheduleId().getServiceType())){
										asse.setAlertGenFlag(1);
										session.save(asse);
									}
									
									
								}
								
								
							}
						}
						
				}
				if(installationDate==null){
					
					//update fault details table.
					
					//Defect ID: Ex: DF20200908-Shajesh- handling the incorrect messageString :Remove attribute messageId+"|"+fileRef+"|"+reprocessJobCode from the messageString
					String messageString = vinNumber+"|"+callTypeId+"|"+monthlyVisitFlag+"|"+extendedWarantyFlag;
		
					fLogger.fatal("Extended Warranty - "+messageString+" Exception:: Put the record into fault details");
		
					ErrorMessageHandler errorHandler = new ErrorMessageHandler();
					
					errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, 
							reprocessJobCode,"Installation Date or Vin details not available.Please check vin no.","0002","Service Invokation");
		
					
					if(messageId!=null && messageId.split("\\|").length<2){
						String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", 1);
						iLogger.info("Status on updating data into interface log details table :"+uStatus);
					}
				}
		}
		catch(Exception e){
			fLogger.error("Exception:"+e.getMessage(),e);
			

			
			//update fault details table.
			//Defect ID: Ex: DF20200908-Shajesh- handling the incorrect messageString :Remove attribute messageId+"|"+fileRef+"|"+reprocessJobCode from the messageString
			String messageString = vinNumber+"|"+callTypeId+"|"+monthlyVisitFlag+"|"+extendedWarantyFlag;
			fLogger.fatal("Extended Warranty - "+messageString+" Exception:: Put the record into fault details");

			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			
			errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, 
					reprocessJobCode,"Installation File or assetTypeId or assetGroupId not available","0002","Service Invokation");

			
			if(messageId!=null && messageId.split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}

		}
		finally
		{
			try
			{
				if(session.getTransaction().isActive()){
					session.getTransaction().commit();
				}
				if(session.isOpen())
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}
			}
			catch(Exception e2)
			{
				fLogger.fatal("ExtendedWarrantyServiceImpl - "+"Exception in commiting the record:"+e2);

			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
		}
		return response;
	}

	
	
}
