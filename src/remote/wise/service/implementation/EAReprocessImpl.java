/**
 * 
 */
package remote.wise.service.implementation;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.FaultDetails;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.service.webservice.AssetGateoutService;
import remote.wise.service.webservice.AssetPersonalityService;
import remote.wise.service.webservice.AssetSaleFromD2Cservice;
import remote.wise.service.webservice.InstallationDateDetailsService;
import remote.wise.service.webservice.JcbRollOffService;
import remote.wise.service.webservice.PrimaryDealerTransferService;
import remote.wise.service.webservice.ServiceHistoryDetailsRESTService;
import remote.wise.service.webservice.ServiceHistoryDetailsService;
import remote.wise.util.HibernateUtil;

/**
 * @author KO369761
 *
 */
public class EAReprocessImpl {
	
	public String reprocessRollOffInterface(String serialNumber, String fileName){
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		String messgId=null;
		String engineNumber = null;
		String chasisNumber =null;
		String response = null;
		String make=null;
		String buildDate=null;
		String machineNumber=null;
		String reprocessJobCode = null;
		String process = null;
		String fileRef = null;
		String[] messageSplit = null;
		String messageString = null;
		FaultDetails faultDetailsObj = null;

		serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");
		
		try{
			session.beginTransaction();
			Query query = session.createQuery(" from FaultDetails where fileName = '"+fileName+"' and messageString like '%"+serialNumber+"%'");
			Iterator queryItr = query.list().iterator();
			if(queryItr.hasNext())
			{
				faultDetailsObj = (FaultDetails) queryItr.next();
				messageString = faultDetailsObj.getMessageString();
				messgId = faultDetailsObj.getMessageId();
				reprocessJobCode = faultDetailsObj.getReprocessJobCode();
				fileRef = faultDetailsObj.getFileName();
				process = faultDetailsObj.getProcess();
				messageSplit = messageString.split("\\|");
        		int paramSize =messageSplit.length;
        		
        		if(paramSize>0)
        			serialNumber = messageSplit[0];
        		if(paramSize>1)
        			engineNumber = messageSplit[1];
        		if(paramSize>2)
        			chasisNumber =  messageSplit[2];
        		if(paramSize>3)
        			make=messageSplit[3];
		      	if(paramSize>4)
		      		buildDate=messageSplit[4];
		      	if(paramSize>5)
		      		machineNumber=messageSplit[5];
			}
			if(messgId !=null)
				messgId = messgId+"|R";
			if(faultDetailsObj != null){
				JcbRollOffService serviceObj = new JcbRollOffService();
				response = serviceObj.setVinMachineNameMapping(serialNumber, engineNumber, chasisNumber, messgId, fileRef, process, reprocessJobCode, make, buildDate, machineNumber);
			}
			else{
				response = "FAILURE";
			}
		}catch(Exception e){
			fLogger.error("Exception Caught: "+e.getMessage());

		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return response;
	}
	
	public String reprocessPersonalityInterface(String serialNumber, String fileName){
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		String response = null;
		String messageString = null;
		String messgId=null;
		String engineNumber = null;
		String assetGroupCode = null;
    	String assetTypeCode = null;
    	String engineTypeCode = null;
    	String assetBuiltDate = null;
    	String make = null;
    	String fuelCapacity = null;
		String[] messageSplit = null;
		String process = null;
		String reprocessJobCode = null;
		String fileRef = null;
		FaultDetails faultDetailsObj = null;
		
		serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");

		try{
			session.beginTransaction();
			Query query = session.createQuery(" from FaultDetails where fileName = '"+fileName+"' and messageString like '%"+serialNumber+"%'");
			Iterator queryItr = query.list().iterator();

			if(queryItr.hasNext()){
				faultDetailsObj = (FaultDetails) queryItr.next();
				messageString = faultDetailsObj.getMessageString();
				messgId = faultDetailsObj.getMessageId();
				reprocessJobCode = faultDetailsObj.getReprocessJobCode();
				fileRef = faultDetailsObj.getFileName();
				process = faultDetailsObj.getProcess();
				messageSplit = messageString.split("\\|");
        		int paramSize =messageSplit.length;
        		
        		if(paramSize>0)
        			engineNumber = messageSplit[0];
        		if(paramSize>1)
        			assetGroupCode = messageSplit[1];
        		if(paramSize>2)
        			assetTypeCode = messageSplit[2];
        		if(paramSize>3)
        			engineTypeCode = messageSplit[3];
        		if(paramSize>4)
        			assetBuiltDate = messageSplit[4];
        		if(paramSize>5)
        			make = messageSplit[5];
        		if(paramSize>6)
        			fuelCapacity = messageSplit[6];
        		if(paramSize>7)
        			serialNumber = messageSplit[7];
			}
			if(messgId !=null)
				messgId = messgId+"|R";
			if(faultDetailsObj != null){
			AssetPersonalityService serviceObj = new AssetPersonalityService();
			response = serviceObj.setAssetPersonalityDetails(engineNumber, assetGroupCode, assetTypeCode, engineTypeCode, assetBuiltDate, make, fuelCapacity, serialNumber, messgId, fileRef, process, reprocessJobCode);
			}
			else{
				response = "FAILURE";
			}
		}catch(Exception e){
			fLogger.error("Exception Caught: "+e.getMessage());

		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return response;
	}

	public String reprocessGateOutInterface(String serialNumber, String fileName){
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		String response = null;
		String messageString = null;
		String messgId=null;
		String dealerCode = null;
		String customerCode = null;
		String engineNumber = null;
		String[] messageSplit = null;
		String process = null;
		String reprocessJobCode = null;
		String fileRef = null;
		FaultDetails faultDetailsObj = null;
		
		serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");

		try{
			session.beginTransaction();
			Query query = session.createQuery(" from FaultDetails where fileName = '"+fileName+"' and messageString like '%"+serialNumber+"%'");
			Iterator queryItr = query.list().iterator();

			if(queryItr.hasNext()){
				faultDetailsObj = (FaultDetails) queryItr.next();
				messageString = faultDetailsObj.getMessageString();
				messgId = faultDetailsObj.getMessageId();
				reprocessJobCode = faultDetailsObj.getReprocessJobCode();
				fileRef = faultDetailsObj.getFileName();
				process = faultDetailsObj.getProcess();
				messageSplit = messageString.split("\\|");
				int paramSize =messageSplit.length;

				if(paramSize>0)
					dealerCode = messageSplit[0];
				if(paramSize>1)
					customerCode = messageSplit[1];
				if(paramSize>2)
					engineNumber = messageSplit[2];
				if(paramSize>3)
					serialNumber = messageSplit[3];
			}
			if(messgId !=null)
				messgId = messgId+"|R";

			if(faultDetailsObj != null){
			AssetGateoutService serviceObj = new AssetGateoutService();
			response = serviceObj.setAssetGateoutService(dealerCode, customerCode, engineNumber, serialNumber, messgId, fileRef, process, reprocessJobCode);
			}
			else{
				response = "FAILURE";
			}
		}catch(Exception e){
			fLogger.error("Exception Caught: "+e.getMessage());

		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return response;
	}
	
	public String reprocessSaleFromD2CInterface(String serialNumber, String fileName){
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		String messgId=null;
        String sellerCode =null;
    	String buyerCode=null;
    	String dealerCode = null;
    	String transferDate = null;
    	String messageString = null;
    	String response = null;
		String[] messageSplit = null;
		String process = null;
		String reprocessJobCode = null;
		String fileRef = null;
		FaultDetails faultDetailsObj = null;
		
		serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");

		try{
			session.beginTransaction();
			Query query = session.createQuery(" from FaultDetails where fileName = '"+fileName+"' and messageString like '%"+serialNumber+"%'");
			Iterator queryItr = query.list().iterator();

			if(queryItr.hasNext()){
				faultDetailsObj = (FaultDetails) queryItr.next();
				messageString = faultDetailsObj.getMessageString();
				messgId = faultDetailsObj.getMessageId();
				reprocessJobCode = faultDetailsObj.getReprocessJobCode();
				fileRef = faultDetailsObj.getFileName();
				process = faultDetailsObj.getProcess();
				messageSplit = messageString.split("\\|");
				int paramSize =messageSplit.length;

				if(paramSize>0)
        			sellerCode = messageSplit[0];
        		if(paramSize>1)
        			buyerCode = messageSplit[1];
        		if(paramSize>2)
        			dealerCode = messageSplit[2];
        		if(paramSize>3)
        			serialNumber = messageSplit[3];
        		if(paramSize>4)
        			transferDate = messageSplit[4];
			}
			if(messgId !=null)
				messgId = messgId+"|R";
			
			if(faultDetailsObj != null){
			AssetSaleFromD2Cservice serviceObj = new AssetSaleFromD2Cservice();
			response = serviceObj.assetSaleFromDealerToCust(sellerCode, buyerCode, dealerCode, serialNumber, transferDate, messgId, fileRef, process, reprocessJobCode);
			}
			else{
				response = "FAILURE";
			}
		}catch(Exception e){
			fLogger.error("Exception Caught: "+e.getMessage());

		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return response;
	}
	
	public String reprocessInstallationInterface(String serialNumber, String fileName){
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		String messgId=null;
		String installationDate = null;
		String dealerCode = null;
		String customerCode = null;
    	String messageString = null;
    	String response = null;
		String[] messageSplit = null;
		String process = null;
		String reprocessJobCode = null;
		String fileRef = null;
		FaultDetails faultDetailsObj = null;
		
		serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");

		try{
			session.beginTransaction();
			Query query = session.createQuery(" from FaultDetails where fileName = '"+fileName+"' and messageString like '%"+serialNumber+"%'");
			Iterator queryItr = query.list().iterator();

			if(queryItr.hasNext()){
				faultDetailsObj = (FaultDetails) queryItr.next();
				messageString = faultDetailsObj.getMessageString();
				messgId = faultDetailsObj.getMessageId();
				reprocessJobCode = faultDetailsObj.getReprocessJobCode();
				fileRef = faultDetailsObj.getFileName();
				process = faultDetailsObj.getProcess();
				messageSplit = messageString.split("\\|");
				int paramSize =messageSplit.length;

				if (paramSize > 0)
					serialNumber = messageSplit[0];
				if (paramSize > 1)
					installationDate = messageSplit[1];
				if (paramSize > 2)
					dealerCode = messageSplit[2];
				if (paramSize > 3)
					customerCode = messageSplit[3];
			}
			if(messgId !=null)
				messgId = messgId+"|R";
			
			if(faultDetailsObj != null){
			InstallationDateDetailsService serviceObj = new InstallationDateDetailsService();
			response = serviceObj.setAssetServiceSchedule(serialNumber, installationDate, dealerCode, customerCode, messgId, fileRef, process, reprocessJobCode);
			}
			else{
				response = "FAILURE";
			}
		}catch(Exception e){
			fLogger.error("Exception Caught: "+e.getMessage());

		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return response;
	}
	
	public String reprocessServiceHistoryInterface(String serialNumber, String fileName){
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		String messgId=null;
		String dealerCode = null;
    	String jobCardNumber = null;
    	String dbmsPartCode = null;
    	//DF20191220:Abhishek::Added new field for Extended Warranty
    	String callTypeId = null;
    	String servicedDate = null;
		//DF20190423:IM20018382-Adding additonal field jobCardDetails
    	String jobCardDetails = "NA";
    	String messageString = null;
    	String response = null;
		String[] messageSplit = null;
		String process = null;
		String reprocessJobCode = null;
		String fileRef = null;
		FaultDetails faultDetailsObj = null;
		
		serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");

		try{
			session.beginTransaction();
			Query query = session.createQuery(" from FaultDetails where fileName = '"+fileName+"' and messageString like '%"+serialNumber+"%'");
			Iterator queryItr = query.list().iterator();

			if(queryItr.hasNext()){
				faultDetailsObj = (FaultDetails) queryItr.next();
				messageString = faultDetailsObj.getMessageString();
				messgId = faultDetailsObj.getMessageId();
				reprocessJobCode = faultDetailsObj.getReprocessJobCode();
				fileRef = faultDetailsObj.getFileName();
				process = faultDetailsObj.getProcess();
				messageSplit = messageString.split("\\|");
				int paramSize =messageSplit.length;

				if(paramSize>0)
        			serialNumber = messageSplit[0];
        		if(paramSize>1)
        			dealerCode = messageSplit[1];
        		if(paramSize>2)
        			jobCardNumber = messageSplit[2];
        		if(paramSize>3)
        			dbmsPartCode = messageSplit[3];
        		//DF20191220:Abhishek::Added new field for Extended Warranty
        		if(paramSize>4)
        			callTypeId = messageSplit[4];
        		if(paramSize>5)
        			servicedDate = messageSplit[5];
    			//DF20190423:IM20018382-Adding additonal field jobCardDetails
        		if(paramSize>6)
        			jobCardDetails = messageSplit[6];

			}
			if(messgId !=null)
				messgId = messgId+"|R";
			
			if(faultDetailsObj != null){
				//ServiceHistoryDetailsService serviceObj = new ServiceHistoryDetailsService();
				//response = serviceObj.setServiceHistory(serialNumber, dealerCode, jobCardNumber, dbmsPartCode, servicedDate, messgId, fileRef, process, reprocessJobCode,jobCardDetails);
				//Changing the call from ServiceHistoryDetails Service to ServiceHistortyDetailsRestService 
				ServiceHistoryDetailsRESTService serviceObj = new ServiceHistoryDetailsRESTService();
				//DF20190423:IM20018382-Adding additonal field jobCardDetails
				response = serviceObj.setServiceHistoryDetails(serialNumber, dealerCode, jobCardNumber, dbmsPartCode, callTypeId, servicedDate, messgId, fileRef, process, reprocessJobCode,jobCardDetails);
			}
			else{
				response = "FAILURE";
			}
		}catch(Exception e){
			fLogger.error("Exception Caught: "+e.getMessage());

		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return response;
	}
	
	public String reprocessPrimaryDealerTransferInterface(String serialNumber, String fileName){
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		String messgId=null;
		String customerCode = null;
    	String dealerCode = null;
    	String transferDate = null;
    	String messageString = null;
    	String response = null;
		String[] messageSplit = null;
		String process = null;
		String reprocessJobCode = null;
		String fileRef = null;
		FaultDetails faultDetailsObj = null;
		
		serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");

		try{
			session.beginTransaction();
			Query query = session.createQuery(" from FaultDetails where fileName = '"+fileName+"' and messageString like '%"+serialNumber+"%'");
			Iterator queryItr = query.list().iterator();

			if(queryItr.hasNext()){
				faultDetailsObj = (FaultDetails) queryItr.next();
				messageString = faultDetailsObj.getMessageString();
				messgId = faultDetailsObj.getMessageId();
				reprocessJobCode = faultDetailsObj.getReprocessJobCode();
				fileRef = faultDetailsObj.getFileName();
				process = faultDetailsObj.getProcess();
				messageSplit = messageString.split("\\|");
				int paramSize =messageSplit.length;

				if(paramSize>0)
        			customerCode = messageSplit[0];
        		if(paramSize>1)
        			dealerCode = messageSplit[1];
        		if(paramSize>2)
        			transferDate = messageSplit[2];
        		if(paramSize>3)
        			serialNumber =  messageSplit[3];
			}
			if(messgId !=null)
				messgId = messgId+"|R";
			
			if(faultDetailsObj != null){
			PrimaryDealerTransferService serviceObj = new PrimaryDealerTransferService();
			response = serviceObj.primaryDealerTransfer(customerCode, dealerCode, transferDate, serialNumber, messgId, fileRef, process, reprocessJobCode);
			}
			else{
				response = "FAILURE";
			}
		}catch(Exception e){
			fLogger.error("Exception Caught: "+e.getMessage());

		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return response;
	}
}
