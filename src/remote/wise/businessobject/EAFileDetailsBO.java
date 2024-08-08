/**
 * 
 */
package remote.wise.businessobject;

import java.util.Iterator;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.EAFileDetailsEntity;
import remote.wise.businessentity.ServiceHistoryFilesEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

/**
 * @author sunayak
 *
 */
public class EAFileDetailsBO {


	public String setEAFileDetailsEntity(List<String> serialNumberList,String fileName,String process)
	{

		String status ="SUCCESS-Record Processed";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		  Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	      session.beginTransaction();

		try
		{
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            } 

			if(serialNumberList!=null && (!serialNumberList.isEmpty())){

				int i=0;
				
				
				iLogger.info("EAFiles - setting EA  received file for every machine in the file::"+fileName);

				for(i=0;i<serialNumberList.size();i++){
					EAFileDetailsEntity eaFileDetailsEntity = null;
					String serialNumber = serialNumberList.get(i);
					if(serialNumber.length()>7 && serialNumber.length()==18){
						serialNumber=serialNumber.substring(11);
					}
					else if(serialNumber.length()>7 && serialNumber.length()==17){
						serialNumber=serialNumber.substring(10);
					}
					else{
						serialNumber=serialNumber;
					}
					Query query = session.createQuery("from EAFileDetailsEntity where Serial_Number like '%"+serialNumber+"'");
					Iterator itr = query.list().iterator();

					while(itr.hasNext())
					{
						eaFileDetailsEntity = (EAFileDetailsEntity) itr.next();
					}
					if(! (session.isOpen() ))
	                  {
	                              session = HibernateUtil.getSessionFactory().getCurrentSession();
	                              session.getTransaction().begin();
	                  } 

					if(eaFileDetailsEntity!=null && eaFileDetailsEntity.getSerial_Number()!=null)
					{
						if(process!=null && (!process.isEmpty())){
							
							iLogger.info("EAFiles - updating EA File data for the machine::"+serialNumber+" with the file name::"+fileName);
							
							if(process.contains("RollOff")){
								eaFileDetailsEntity.setJCBRollOff(fileName);
							}
							else if(process.contains("AssetPersonality")){
								eaFileDetailsEntity.setAssetPersonality(fileName);
							}
							else if(process.contains("AssetGateOut")){
								eaFileDetailsEntity.setAssetGateOut(fileName);
							}
							else if(process.contains("SaleFromD2C")){
								eaFileDetailsEntity.setSaleFromD2C(fileName);
							}
							else if(process.contains("ServiceHistory")){
								//DF20180420-KO369761-Modified for storing multiple service history files for a single VIN.
								if(eaFileDetailsEntity.getServiceHistory() == null){
									eaFileDetailsEntity.setServiceHistory(fileName);
								}
								else{
									String findQ = ("from ServiceHistoryFilesEntity where fileName = '"+fileName+"'");
									Query query2 = session.createQuery(findQ);
									Iterator iterator = query2.list().iterator();
									if(iterator.hasNext()){
										if(!eaFileDetailsEntity.getServiceHistory().contains(fileName)){
											eaFileDetailsEntity.setServiceHistory(eaFileDetailsEntity.getServiceHistory()+","+fileName);
										}
									}
								}
							}
							else if(process.contains("PrimaryDealerTransfer")){
								eaFileDetailsEntity.setPrimaryDealerTransfer(fileName);
							}
							else if(process.contains("AssetInstallationDetails")){
								eaFileDetailsEntity.setAssetInstallationDetails(fileName);
							}
						}
						session.update(eaFileDetailsEntity);
					}

					else
					{
						EAFileDetailsEntity neweaFileDetailsEntity = new EAFileDetailsEntity();
						neweaFileDetailsEntity.setSerial_Number(serialNumber);
						if(process!=null && (!process.isEmpty())){
							
							iLogger.info("EAFiles - Inserting a new row for setting EA File data for the machine::"+serialNumber+" with the file name::"+fileName);
							
							if(process.contains("RollOff")){
								neweaFileDetailsEntity.setJCBRollOff(fileName);
							}
							else if(process.contains("AssetPersonality")){
								neweaFileDetailsEntity.setAssetPersonality(fileName);
							}
							else if(process.contains("AssetGateOut")){
								neweaFileDetailsEntity.setAssetGateOut(fileName);
							}
							else if(process.contains("SaleFromD2C")){
								neweaFileDetailsEntity.setSaleFromD2C(fileName);
							}
							else if(process.contains("ServiceHistory")){
								neweaFileDetailsEntity.setServiceHistory(fileName);
							}
							else if(process.contains("PrimaryDealerTransfer")){
								neweaFileDetailsEntity.setPrimaryDealerTransfer(fileName);
							}
							else if(process.contains("AssetInstallationDetails")){
								neweaFileDetailsEntity.setAssetInstallationDetails(fileName);
							}
						}
						session.save(neweaFileDetailsEntity);
					}
				}
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}

		finally
		{
			try
			{
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}
			}
			catch(Exception e)
			{
				fLogger.fatal("Exception in commiting the record:"+e);
				e.printStackTrace();
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}


		return status;
	}
	
	
	public void storeServiceHistoryFilesData(String fileName, String serviceTktNumber, String machineNumber){
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = null;
		try{
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			if(machineNumber != null){
				machineNumber=machineNumber.replaceFirst("^0+(?!$)", "");

				if(machineNumber.length()>7)
				{
					machineNumber=machineNumber.substring(machineNumber.length()-7);
				}
			}
			
			//DF20180420-ko369761: Inserting filename & JobNumber into a new table called service_history_files for interface tracking.
			if(serviceTktNumber !=null && !serviceTktNumber.isEmpty()){
				iLogger.info("Service History - inserting filename & service ticket number in service_history_files table for tracking -"+serviceTktNumber);
				Query query = session.createQuery("from ServiceHistoryFilesEntity where serviceTktNumber = '"+serviceTktNumber+"'");
				Iterator iterator = query.list().iterator();
				if(!iterator.hasNext()){
					ServiceHistoryFilesEntity fileObj = new ServiceHistoryFilesEntity();
					fileObj.setFileName(fileName);
					fileObj.setServiceTktNumber(serviceTktNumber);
					fileObj.setMachineNumber(machineNumber);
					session.save(fileObj);
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			fLogger.fatal("Exception in storing the service history files:"+serviceTktNumber+" from file:"+e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(session!=null && session.isOpen())
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
						session.flush();
						session.close();
					}
			}
			catch(Exception e2)
			{
				fLogger.fatal("Service History - "+"Exception in commiting the record:"+e2);
			}
		}
	}
}
