package remote.wise.businessobject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DetailMachineImpl;
import remote.wise.service.implementation.DomainServiceImpl;
import remote.wise.service.implementation.EAInterfaceImpl;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

import java.util.HashMap;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetAccountMapping;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEventEntity;
import remote.wise.businessentity.AssetServiceScheduleEntity;
import remote.wise.businessentity.EAFileDetailsEntity;
import remote.wise.businessentity.EALogDetailsEntity;
import remote.wise.businessentity.FaultDetails;
import remote.wise.businessentity.InterfaceLogDetails;
import remote.wise.businessentity.ServiceHistoryEntity;
import remote.wise.businessentity.ServiceHistoryFilesEntity;

public class EAIntegrationBO 
{
	/*public static WiseLogger infoLogger = WiseLogger.getLogger("EAIntegrationBO:","info");
	public static WiseLogger businessError = WiseLogger.getLogger("EAIntegrationBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("EAIntegrationBO:","fatalError");*/

	//DF20140813 - Rajani Nagaraju - Web based search for EA File Processing Status
	public List<EAInterfaceImpl> getEAVinSummaryDetails(String serialNumber, String interfaceName)
	{

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		List<EAInterfaceImpl> eaVinSummList = new LinkedList<EAInterfaceImpl>();

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		AssetEntity asset=null;
		int productId=0;
		int assetOwnerCount=0;
		String JCBRollOff =null;
		String AssetPersonality =null;
		String AssetGateOut =null;
		String SaleFromD2C =null;
		String ServiceHistory =null;
		String PrimaryDealerTransfer =null;
		String AssetInstallationDetails =null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try
		{
			//Store the processes to be analyzed for - in Sequence
			//DF20190220 : Z1007653 - Added condition to select a "specific interface" or "all interface" 
			HashMap<Integer,String> eaProcessMap = new HashMap<Integer, String>(); 
			if (interfaceName.equalsIgnoreCase("All")) {
				/*eaProcessMap = new HashMap<Integer, String>();			DF20190220 : Z1007653 - Commented*/
				eaProcessMap.put(1, "RollOff");
				eaProcessMap.put(2, "AssetPersonality");
				eaProcessMap.put(3, "AssetGateOut");
				eaProcessMap.put(4, "SaleFromD2C");
				eaProcessMap.put(5, "AssetInstallationDetails");
				eaProcessMap.put(6, "ServiceHistory");
				eaProcessMap.put(7, "PrimaryDealerTransfer");
			}
			else {
				eaProcessMap.put(1, interfaceName);
			}
			//------- DF20190220 : Z1007653 Logic ends --------

			for(int i=1; i<=eaProcessMap.size(); i++)
			{
				String process = eaProcessMap.get(i);

				int processFailed=0;
				int present=0;

				/*//Check whether any rejections has happened for the given VIN and the current process
				Query eaFileRejectionQ = session.createQuery(" from FaultDetails where process='"+process+"' and messageString like '%"+serialNumber+"'");
				Iterator eaFileRejectionItr = eaFileRejectionQ.list().iterator();
				while(eaFileRejectionItr.hasNext())
				{
					processFailed=1;

					FaultDetails faultDetailsObj = (FaultDetails)eaFileRejectionItr.next();
					EAInterfaceImpl implObj = new EAInterfaceImpl();
					implObj.setFileName(faultDetailsObj.getFileName());
					implObj.setInterfaceName(faultDetailsObj.getProcess());
					implObj.setReasonForRejection(faultDetailsObj.getFailureCause());
					implObj.setStatus("FAILURE");

					eaVinSummList.add(implObj);
				}

				if(processFailed==0)
				{
				 */	//DefectId:20150617 @Suprava Check whether the Files are received or not for the given process from EAfileDetails Table
				EAFileDetailsEntity eaFileDetailsEntity =new EAFileDetailsEntity();
				Query eaFileQ = session.createQuery("from EAFileDetailsEntity where Serial_Number like '%"+serialNumber+"%'");
				Iterator eaFileItr = eaFileQ.list().iterator();
				while(eaFileItr.hasNext())
				{
					eaFileDetailsEntity = (EAFileDetailsEntity)eaFileItr.next();
					JCBRollOff =eaFileDetailsEntity.getJCBRollOff();
					AssetPersonality = eaFileDetailsEntity.getAssetPersonality();
					AssetGateOut = eaFileDetailsEntity.getAssetGateOut();
					SaleFromD2C = eaFileDetailsEntity.getSaleFromD2C();
					ServiceHistory = eaFileDetailsEntity.getServiceHistory();
					PrimaryDealerTransfer = eaFileDetailsEntity.getPrimaryDealerTransfer();
					AssetInstallationDetails = eaFileDetailsEntity.getAssetInstallationDetails();
				}

				if(process.equalsIgnoreCase("RollOff"))
				{

					if(JCBRollOff==null)
					{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setInterfaceName("RollOff");
						implObj.setStatus("File not Received");

						eaVinSummList.add(implObj);
					}
					else{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setFileName(JCBRollOff);
						
						//DF20180404 - KO369761 - file processed time field was added for interface backtraking.
						Query fileQuery = session.createQuery(" from EALogDetailsEntity where fileName = '"+JCBRollOff+"'");
						Iterator fileItr = fileQuery.list().iterator();
						if(fileItr.hasNext()){
							EALogDetailsEntity eaLogObj = (EALogDetailsEntity) fileItr.next();
							implObj.setFileProcessedTime(sdf.format(eaLogObj.getProcessedTimestamp()));
						}

						Query eaFileRejectionQ = session.createQuery(" from FaultDetails where process='"+process+"' and messageString like '%"+serialNumber+"%'");
						Iterator eaFileRejectionItr = eaFileRejectionQ.list().iterator();
						while(eaFileRejectionItr.hasNext())
						{
							processFailed=1;

							FaultDetails faultDetailsObj = (FaultDetails)eaFileRejectionItr.next();
							implObj.setInterfaceName(faultDetailsObj.getProcess());
							implObj.setReasonForRejection(faultDetailsObj.getFailureCause());
							implObj.setStatus("FAILURE");

							eaVinSummList.add(implObj);
						}
						if(processFailed==0){

							//get the Asset details for the given VIN
							Query assetQ = session.createQuery("from AssetEntity where serial_number like '%"+serialNumber+"'");
							Iterator assetItr = assetQ.list().iterator();
							while(assetItr.hasNext())
							{
								present=1;
								asset = (AssetEntity)assetItr.next();
								if(asset.getProductId()!=null){
									productId = asset.getProductId().getProductId();
								}
								implObj.setInterfaceName("RollOff");
								implObj.setStatus("SUCCESS");

								eaVinSummList.add(implObj);

							}
						}
						if(present==0 && processFailed==0)
						{
							implObj.setInterfaceName("RollOff");
							implObj.setStatus("FAILURE");

							eaVinSummList.add(implObj);	
						}


					}
				}

				else if(process.equalsIgnoreCase("AssetPersonality"))
				{
					if(AssetPersonality==null)
					{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setInterfaceName("AssetPersonality");
						implObj.setStatus("File not Received");

						eaVinSummList.add(implObj);
					}
					else{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setFileName(AssetPersonality);
						
						//DF20180404 - KO369761 - file processed time field was added for interface backtraking.
						Query fileQuery = session.createQuery(" from EALogDetailsEntity where fileName = '"+AssetPersonality+"'");
						Iterator fileItr = fileQuery.list().iterator();
						if(fileItr.hasNext()){
							EALogDetailsEntity eaLogObj = (EALogDetailsEntity) fileItr.next();
							implObj.setFileProcessedTime(sdf.format(eaLogObj.getProcessedTimestamp()));
						}

						Query eaFileRejectionQ = session.createQuery(" from FaultDetails where process='"+process+"' and messageString like '%"+serialNumber+"%'");
						Iterator eaFileRejectionItr = eaFileRejectionQ.list().iterator();
						while(eaFileRejectionItr.hasNext())
						{
							processFailed=1;

							FaultDetails faultDetailsObj = (FaultDetails)eaFileRejectionItr.next();
							implObj.setInterfaceName(faultDetailsObj.getProcess());
							implObj.setReasonForRejection(faultDetailsObj.getFailureCause());
							implObj.setStatus("FAILURE");

							eaVinSummList.add(implObj);
						}

						if(processFailed==0){
							if(productId>0)
							{
								implObj.setInterfaceName("AssetPersonality");
								implObj.setStatus("SUCCESS");

								eaVinSummList.add(implObj);
							}
							else
							{
								implObj.setInterfaceName("AssetPersonality");
								implObj.setStatus("FAILURE");

								eaVinSummList.add(implObj);
							}
						}
					}
				}

				else if(process.equalsIgnoreCase("AssetGateOut"))
				{
					if(AssetGateOut==null)
					{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setInterfaceName("AssetGateOut");
						implObj.setStatus("File not Received");

						eaVinSummList.add(implObj);
					}
					else{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setFileName(AssetGateOut);
						
						//DF20180404 - KO369761 - file processed time field was added for interface backtraking.
						Query fileQuery = session.createQuery(" from EALogDetailsEntity where fileName = '"+AssetGateOut+"'");
						Iterator fileItr = fileQuery.list().iterator();
						if(fileItr.hasNext()){
							EALogDetailsEntity eaLogObj = (EALogDetailsEntity) fileItr.next();
							implObj.setFileProcessedTime(sdf.format(eaLogObj.getProcessedTimestamp()));
						}

						Query eaFileRejectionQ = session.createQuery(" from FaultDetails where process='"+process+"' and messageString like '%"+serialNumber+"%'");
						Iterator eaFileRejectionItr = eaFileRejectionQ.list().iterator();
						while(eaFileRejectionItr.hasNext())
						{
							processFailed=1;

							FaultDetails faultDetailsObj = (FaultDetails)eaFileRejectionItr.next();
							implObj.setInterfaceName(faultDetailsObj.getProcess());
							implObj.setReasonForRejection(faultDetailsObj.getFailureCause());
							implObj.setStatus("FAILURE");

							eaVinSummList.add(implObj);
						}

						if(processFailed==0){
							//Get the count of asset_owners for the given machine
							Query assetOwnerQ = session.createQuery(" from AssetAccountMapping where serialNumber like '%"+serialNumber+"'");
							Iterator assetOwnerItr = assetOwnerQ.list().iterator();
							while(assetOwnerItr.hasNext())
							{
								AssetAccountMapping assetAccount= (AssetAccountMapping)assetOwnerItr.next();
								assetOwnerCount=assetOwnerCount+1;
							}

							if(assetOwnerCount>1)
							{
								implObj.setInterfaceName("AssetGateOut");
								implObj.setStatus("SUCCESS");

								eaVinSummList.add(implObj);
							}

							else
							{
								implObj.setInterfaceName("AssetGateOut");
								implObj.setStatus("FAILURE");

								eaVinSummList.add(implObj);
							}
						}
					}

				}

				else if(process.equalsIgnoreCase("SaleFromD2C"))
				{
					if(SaleFromD2C==null)
					{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setInterfaceName("SaleFromD2C");
						implObj.setStatus("File not Received");

						eaVinSummList.add(implObj);
					}
					else{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setFileName(SaleFromD2C);
						
						//DF20180404 - KO369761 - file processed time field was added for interface backtraking.
						Query fileQuery = session.createQuery(" from EALogDetailsEntity where fileName = '"+SaleFromD2C+"'");
						Iterator fileItr = fileQuery.list().iterator();
						if(fileItr.hasNext()){
							EALogDetailsEntity eaLogObj = (EALogDetailsEntity) fileItr.next();
							implObj.setFileProcessedTime(sdf.format(eaLogObj.getProcessedTimestamp()));
						}

						Query eaFileRejectionQ = session.createQuery(" from FaultDetails where process='"+process+"' and messageString like '%"+serialNumber+"%'");
						Iterator eaFileRejectionItr = eaFileRejectionQ.list().iterator();
						while(eaFileRejectionItr.hasNext())
						{
							processFailed=1;

							FaultDetails faultDetailsObj = (FaultDetails)eaFileRejectionItr.next();
							implObj.setInterfaceName(faultDetailsObj.getProcess());
							implObj.setReasonForRejection(faultDetailsObj.getFailureCause());
							implObj.setStatus("FAILURE");

							eaVinSummList.add(implObj);
						}

						if(processFailed==0){

							if(assetOwnerCount>2)
							{
								implObj.setInterfaceName("SaleFromD2C");
								implObj.setStatus("SUCCESS");

								eaVinSummList.add(implObj);
							}
							else
							{
								implObj.setInterfaceName("SaleFromD2C");
								implObj.setStatus("FAILURE");

								eaVinSummList.add(implObj);
							}

						}
					}
				}

				else if(process.equalsIgnoreCase("AssetInstallationDetails"))
				{
					if(AssetInstallationDetails==null)
					{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setInterfaceName("AssetInstallationDetails");
						implObj.setStatus("File not Received");

						eaVinSummList.add(implObj);
					}
					else{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setFileName(AssetInstallationDetails);
						
						//DF20180404 - KO369761 - file processed time field was added for interface backtraking.
						Query fileQuery = session.createQuery(" from EALogDetailsEntity where fileName = '"+AssetInstallationDetails+"'");
						Iterator fileItr = fileQuery.list().iterator();
						if(fileItr.hasNext()){
							EALogDetailsEntity eaLogObj = (EALogDetailsEntity) fileItr.next();
							implObj.setFileProcessedTime(sdf.format(eaLogObj.getProcessedTimestamp()));
						}

						Query eaFileRejectionQ = session.createQuery(" from FaultDetails where process='"+process+"' and messageString like '%"+serialNumber+"%'");
						Iterator eaFileRejectionItr = eaFileRejectionQ.list().iterator();
						while(eaFileRejectionItr.hasNext())
						{
							processFailed=1;

							FaultDetails faultDetailsObj = (FaultDetails)eaFileRejectionItr.next();
							implObj.setInterfaceName(faultDetailsObj.getProcess());
							implObj.setReasonForRejection(faultDetailsObj.getFailureCause());
							implObj.setStatus("FAILURE");

							eaVinSummList.add(implObj);
						}

						if(processFailed==0){

							AssetServiceScheduleEntity assetServSch =null;
							Query assetServSchQ = session.createQuery(" from AssetServiceScheduleEntity where serialNumber like '%"+serialNumber+"'");
							Iterator assetSerSchItr = assetServSchQ.list().iterator();
							if(assetSerSchItr.hasNext())
							{
								assetServSch = (AssetServiceScheduleEntity)assetSerSchItr.next();
								implObj.setInterfaceName("AssetInstallationDetails");
								implObj.setStatus("SUCCESS");

								eaVinSummList.add(implObj);
							}

							if(assetServSch==null)
							{
								implObj.setInterfaceName("AssetInstallationDetails");
								implObj.setStatus("FAILURE");

								eaVinSummList.add(implObj);
							}

						}

					}


				}

				else if(process.equalsIgnoreCase("ServiceHistory"))
				{
					if(ServiceHistory==null)
					{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setInterfaceName("ServiceHistory");
						implObj.setStatus("File not Received");

						eaVinSummList.add(implObj);
					}
					else{
						
						//DF20180423 - KO369761 - Logic changed, because a VIN can have multiple service history files.
						String serviceHistoryFiles[] = ServiceHistory.split(",");
						for(String fileName : serviceHistoryFiles){
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setFileName(fileName);

							int failureFlag = 1;
							//DF20180404 - KO369761 - file processed time field was added for interface backtraking.
							Query fileQuery = session.createQuery(" from EALogDetailsEntity where fileName = '"+fileName+"'");
							Iterator fileItr = fileQuery.list().iterator();
							if(fileItr.hasNext()){
								EALogDetailsEntity eaLogObj = (EALogDetailsEntity) fileItr.next();
								implObj.setFileProcessedTime(sdf.format(eaLogObj.getProcessedTimestamp()));
							}

							Query query2 = session.createQuery(" from ServiceHistoryFilesEntity where fileName = '"+fileName+"' and machineNumber = '"+serialNumber+"'");
							Iterator itr2 = query2.list().iterator();
							if(itr2.hasNext()){
								ServiceHistoryFilesEntity serviceHistoryFileObj = (ServiceHistoryFilesEntity) itr2.next();
								Query query3 = session.createQuery(" from ServiceHistoryEntity where serviceTicketNumber = '"+serviceHistoryFileObj.getServiceTktNumber()+"'");
								Iterator itr3 = query3.list().iterator();
								if(itr3.hasNext()){
									failureFlag = 0;
									implObj.setStatus("SUCCESS");
									implObj.setInterfaceName("ServiceHistory");
									eaVinSummList.add(implObj);
								}
							}
							if(failureFlag == 1){
								Query eaFileRejectionQ = session.createQuery(" from FaultDetails where process='"+process+"' and messageString like '%"+serialNumber+"%' and fileName='"+fileName+"'");
								Iterator eaFileRejectionItr = eaFileRejectionQ.list().iterator();
								if(eaFileRejectionItr.hasNext())
								{
									FaultDetails faultDetailsObj = (FaultDetails)eaFileRejectionItr.next();
									implObj.setInterfaceName(faultDetailsObj.getProcess());
									implObj.setReasonForRejection(faultDetailsObj.getFailureCause());
									implObj.setStatus("FAILURE");
									eaVinSummList.add(implObj);
								}
								else{
									implObj.setInterfaceName("ServiceHistory");
									implObj.setStatus("FAILURE");
									eaVinSummList.add(implObj);
								}
							}
						}

						
						/*EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setFileName(ServiceHistory);
						
						//DF20180404 - KO369761 - file processed time field was added for interface backtraking.
						Query fileQuery = session.createQuery(" from EALogDetailsEntity where fileName = '"+ServiceHistory+"'");
						Iterator fileItr = fileQuery.list().iterator();
						if(fileItr.hasNext()){
							EALogDetailsEntity eaLogObj = (EALogDetailsEntity) fileItr.next();
							implObj.setFileProcessedTime(sdf.format(eaLogObj.getProcessedTimestamp()));
						}

						Query eaFileRejectionQ = session.createQuery(" from FaultDetails where process='"+process+"' and messageString like '%"+serialNumber+"%'");
						Iterator eaFileRejectionItr = eaFileRejectionQ.list().iterator();
						while(eaFileRejectionItr.hasNext())
						{
							processFailed=1;

							FaultDetails faultDetailsObj = (FaultDetails)eaFileRejectionItr.next();
							implObj.setInterfaceName(faultDetailsObj.getProcess());
							implObj.setReasonForRejection(faultDetailsObj.getFailureCause());
							implObj.setStatus("FAILURE");

							eaVinSummList.add(implObj);
						}

						if(processFailed==0){
							int openServiceAlert=0;
							Query assetEventQ = session.createQuery(" from AssetEventEntity where serialNumber like '%"+serialNumber+"%' and eventTypeId=1" +
							" and activeStatus=1");
							Iterator assetEventItr = assetEventQ.list().iterator();
							while(assetEventItr.hasNext())
							{
								AssetEventEntity assetEvent = (AssetEventEntity)assetEventItr.next();
								openServiceAlert=1;
							}

							if(openServiceAlert==0)
							{
								implObj.setInterfaceName("ServiceHistory");
								implObj.setStatus("FAILURE");

								eaVinSummList.add(implObj);
							}

							else
							{
								Query serviceRecordQ = session.createQuery(" from ServiceHistoryEntity where serialNumber like '%"+serialNumber+"'");
								Iterator serviceRecordItr = serviceRecordQ.list().iterator();
								if(serviceRecordItr.hasNext())
								{
									ServiceHistoryEntity servHis = (ServiceHistoryEntity)serviceRecordItr.next();
									implObj.setInterfaceName("ServiceHistory");
									implObj.setStatus("SUCCESS");

									eaVinSummList.add(implObj);
								}
							}

						}*/
 					}
				}
				else if(process.equalsIgnoreCase("PrimaryDealerTransfer")){

					if(PrimaryDealerTransfer==null)
					{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setInterfaceName("PrimaryDealerTransfer");
						implObj.setStatus("File not Received");

						eaVinSummList.add(implObj);
					}
					else{
						EAInterfaceImpl implObj = new EAInterfaceImpl();
						implObj.setFileName(PrimaryDealerTransfer);
						
						//DF20180404 - KO369761 - file processed time field was added for interface backtraking.
						Query fileQuery = session.createQuery(" from EALogDetailsEntity where fileName = '"+PrimaryDealerTransfer+"'");
						Iterator fileItr = fileQuery.list().iterator();
						if(fileItr.hasNext()){
							EALogDetailsEntity eaLogObj = (EALogDetailsEntity) fileItr.next();
							implObj.setFileProcessedTime(sdf.format(eaLogObj.getProcessedTimestamp()));
						}

						Query eaFileRejectionQ = session.createQuery(" from FaultDetails where process='"+process+"' and messageString like '%"+serialNumber+"%'");
						Iterator eaFileRejectionItr = eaFileRejectionQ.list().iterator();
						while(eaFileRejectionItr.hasNext())
						{
							processFailed=1;

							FaultDetails faultDetailsObj = (FaultDetails)eaFileRejectionItr.next();
							implObj.setInterfaceName(faultDetailsObj.getProcess());
							implObj.setReasonForRejection(faultDetailsObj.getFailureCause());
							implObj.setStatus("FAILURE");

							eaVinSummList.add(implObj);
						}

						if(processFailed==0){
								implObj.setInterfaceName("PrimaryDealerTransfer");
								implObj.setStatus("SUCCESS");

								eaVinSummList.add(implObj);
						}

					}


					
				}
				//End DefectId:20150617
				//Check whether the Files are received or not for the given process
				/*if(process.equalsIgnoreCase("RollOff"))
					{

						//get the Asset details for the given VIN
						Query assetQ = session.createQuery("from AssetEntity where serial_number like '%"+serialNumber+"'");
						Iterator assetItr = assetQ.list().iterator();
						while(assetItr.hasNext())
						{
							asset = (AssetEntity)assetItr.next();
							if(asset.getProductId()!=null)
								productId = asset.getProductId().getProductId();
						}

						if(asset==null)
						{
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setInterfaceName("RollOff");
							implObj.setStatus("File not Received");

							eaVinSummList.add(implObj);
						}

						else
						{
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setInterfaceName("RollOff");
							implObj.setStatus("SUCCESS");

							eaVinSummList.add(implObj);
						}
					}

					else if(process.equalsIgnoreCase("AssetPersonality"))
					{
						if(productId>0)
						{
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setInterfaceName("AssetPersonality");
							implObj.setStatus("SUCCESS");

							eaVinSummList.add(implObj);
						}
						else
						{
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setInterfaceName("AssetPersonality");
							implObj.setStatus("File Not Received");

							eaVinSummList.add(implObj);
						}
					}

					else if(process.equalsIgnoreCase("AssetGateOut"))
					{
						//Get the count of asset_owners for the given machine
						Query assetOwnerQ = session.createQuery(" from AssetAccountMapping where serialNumber like '%"+serialNumber+"'");
						Iterator assetOwnerItr = assetOwnerQ.list().iterator();
						while(assetOwnerItr.hasNext())
						{
							AssetAccountMapping assetAccount= (AssetAccountMapping)assetOwnerItr.next();
							assetOwnerCount=assetOwnerCount+1;
						}

						if(assetOwnerCount>1)
						{
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setInterfaceName("AssetGateOut");
							implObj.setStatus("SUCCESS");

							eaVinSummList.add(implObj);
						}

						else
						{
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setInterfaceName("AssetGateOut");
							implObj.setStatus("File Not Received");

							eaVinSummList.add(implObj);
						}
					}

					else if(process.equalsIgnoreCase("SaleFromD2C"))
					{
						if(assetOwnerCount>2)
						{
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setInterfaceName("SaleFromD2C");
							implObj.setStatus("SUCCESS");

							eaVinSummList.add(implObj);
						}
						else
						{
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setInterfaceName("SaleFromD2C");
							implObj.setStatus("File Not Received");

							eaVinSummList.add(implObj);
						}
					}

					else if(process.equalsIgnoreCase("AssetInstallationDetails"))
					{
						AssetServiceScheduleEntity assetServSch =null;
						Query assetServSchQ = session.createQuery(" from AssetServiceScheduleEntity where serialNumber like '%"+serialNumber+"'");
						Iterator assetSerSchItr = assetServSchQ.list().iterator();
						if(assetSerSchItr.hasNext())
						{
							assetServSch = (AssetServiceScheduleEntity)assetSerSchItr.next();
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setInterfaceName("AssetInstallationDetails");
							implObj.setStatus("SUCCESS");

							eaVinSummList.add(implObj);
						}

						if(assetServSch==null)
						{
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setInterfaceName("AssetInstallationDetails");
							implObj.setStatus("File Not Received");

							eaVinSummList.add(implObj);
						}

					}

					else if(process.equalsIgnoreCase("ServiceHistory"))
					{
						int openServiceAlert=0;
						Query assetEventQ = session.createQuery(" from AssetEventEntity where serialNumber like '%"+serialNumber+"%' and eventTypeId=1" +
								" and activeStatus=1");
						Iterator assetEventItr = assetEventQ.list().iterator();
						while(assetEventItr.hasNext())
						{
							AssetEventEntity assetEvent = (AssetEventEntity)assetEventItr.next();
							openServiceAlert=1;
						}

						if(openServiceAlert==0)
						{
							EAInterfaceImpl implObj = new EAInterfaceImpl();
							implObj.setInterfaceName("ServiceHistory");
							implObj.setStatus("File Not Received");

							eaVinSummList.add(implObj);
						}

						else
						{
							Query serviceRecordQ = session.createQuery(" from ServiceHistoryEntity where serialNumber like '%"+serialNumber+"'");
							Iterator serviceRecordItr = serviceRecordQ.list().iterator();
							if(serviceRecordItr.hasNext())
							{
								ServiceHistoryEntity servHis = (ServiceHistoryEntity)serviceRecordItr.next();
								EAInterfaceImpl implObj = new EAInterfaceImpl();
								implObj.setInterfaceName("ServiceHistory");
								implObj.setStatus("SUCCESS");

								eaVinSummList.add(implObj);
							}
						}

					}*/


				//	}
			}

		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
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
		return eaVinSummList;
	}

	//DF20190220: Z1007653 - Adding one more parameter as interfaceName 
	public List<EAInterfaceImpl> getEADateSummaryDetails(String searchDate, String interfaceName) { 
		// TODO Auto-generated method stub

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		List<EAInterfaceImpl> eaDateSummList = new LinkedList<EAInterfaceImpl>();
		List<String> tempFileList=new ArrayList<String>();
		DomainServiceImpl domainService = new DomainServiceImpl();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		//DF20190220: Z1007653 - added condition to get file name for further "file_name search"
		String fileName = interfaceName;
		if ("AssetInstallationDetails".equalsIgnoreCase(fileName)) {
			fileName = "AssetInstallation";
		}
		if ("ServiceHistory".equalsIgnoreCase(fileName)) {
			fileName = "AssetServiceDetails";
		}
		try{
			// Validate the time period - Required format: yyyy-MM-dd
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			Date inputDate = dateFormatter.parse(searchDate);
			String DateString = dateFormatter.format(inputDate);
			String searchDateString = DateString.replaceAll("-", "");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			
			/*//DF20180411:KO369761 - taking file wise data from new table for interface tracing.
			Query fileQuery = session.createQuery(" from InterfaceLogDetails where filename like '%"+searchDateString+"%'");*/
			
			//DF20190220: Z1007653 - Extended sql query to fetch InterfaceLogDetails for a specific file_name and date
			String hql = " from InterfaceLogDetails where filename like '%"+searchDateString+"%' ";
			if(!"All".equalsIgnoreCase(fileName)){
				hql=hql+"and filename like '%"+fileName+"%'";
			}
			Query fileQuery = session.createQuery(hql);
			
			Iterator fileItr = fileQuery.list().iterator();
			while(fileItr.hasNext()){
				InterfaceLogDetails detailsObj = (InterfaceLogDetails) fileItr.next();
				if(detailsObj.getFailureCount()>= 0){
					EAInterfaceImpl implResp = new EAInterfaceImpl();
					implResp.setInterfaceFileName(detailsObj.getFilename());
					implResp.setRejectedRecord(detailsObj.getFailureCount());
					implResp.setProcessedRecord(detailsObj.getSucessCount());
					
					Query processedTimeQ = session.createQuery(" from EALogDetailsEntity where fileName = '"+detailsObj.getFilename()+"'");
					Iterator processedTimeItr = processedTimeQ.list().iterator();
					if(processedTimeItr.hasNext()){
						EALogDetailsEntity eaLogObj = (EALogDetailsEntity) processedTimeItr.next();
						implResp.setFileProcessedTime(sdf.format(eaLogObj.getProcessedTimestamp()));
					}
					
					eaDateSummList.add(implResp);
					tempFileList.add(detailsObj.getFilename());
				}
				
			}
			
			//DF20180411 :KO369761 - Query modified for fetching file processed time from the table.
			/*Query query1 = session.createQuery(" select count(b.fileName), a.fileName, a.noOfRecords from EALogDetailsEntity a, FaultDetails b "
					+ " where a.processedTimestamp like '"+DateString+"%' and a.fileName=b.fileName and a.process=b.process group by b.fileName");*/
			
			iLogger.info("Process name before query is "+interfaceName);
			String query1 = " select count(b.fileName), a.fileName, a.noOfRecords, a.processedTimestamp from EALogDetailsEntity a, FaultDetails b "
					+ " where a.fileName like '%" + searchDateString +"%' ";
			
			//DF20190220: Z1007653 - Extended selecting records from EALogDetailsEntity & FaultDetails for specific process = interfaceName
			if (!("All".equalsIgnoreCase(interfaceName))) {
				query1 = query1 + "and a.process = '" + interfaceName + "' ";
			}
			query1 = query1+ "and a.fileName=b.fileName and a.process=b.process group by b.fileName";
			iLogger.info("Date Query is"+query1);
			Query query2 = session.createQuery(query1);
			
			/*Query query2 = session.createQuery(" select count(b.fileName), a.fileName, a.noOfRecords, a.processedTimestamp from EALogDetailsEntity a, FaultDetails b "
					+ " where a.fileName like '%"+searchDateString+"%' and a.fileName=b.fileName and a.process=b.process group by b.fileName");*/
			
			Iterator itr1 = query2.list().iterator();
			Object[] result1 = null;
			
			
			//List<String> tempFileList=new ArrayList<String>();
			while (itr1.hasNext()) {
				result1 = (Object[]) itr1.next();
				EAInterfaceImpl implResp = new EAInterfaceImpl();
				int failRecord = 0;
				int processRecord = 0;
				int totalRecord = 0;
				if(result1[0]!=null){
					Long count=(Long) result1[0];
					failRecord=count.intValue();
					implResp.setRejectedRecord(failRecord);
				}
				if(result1[1]!=null){
					implResp.setInterfaceFileName(result1[1].toString());
					tempFileList.add(result1[1].toString());
				}
				if(result1[2]!=null){
					totalRecord=((Integer)result1[2]);
				}
				if(result1[3]!=null){
					implResp.setFileProcessedTime(sdf.format(result1[3]));
				}
				processRecord=totalRecord-failRecord;
				implResp.setProcessedRecord(processRecord);

				eaDateSummList.add(implResp);

			}
			int totalRecord=0;
			String selectQuery ="SELECT a.fileName,a.noOfRecords,a.processedTimestamp";
			String fromQuery =" FROM EALogDetailsEntity a ";
			String whereQuery =" WHERE a.processedTimestamp like '"+DateString+"%' ";
			
			//DF20190220: Z1007653 - Extended query to select records for a specific process = interfaceName
			if (!("All".equalsIgnoreCase(interfaceName))) {
				whereQuery = whereQuery + "and a.process = '" + interfaceName + "' ";
			}
			/*String whereQuery =" WHERE a.fileName NOT IN "
				+ " (SELECT distinct(b.fileName) FROM FaultDetails b) and a.processedTimestamp like '"+DateString+"%' ";*/
			String finalQuery= selectQuery+fromQuery+whereQuery;
			iLogger.info("Final Date Query : "+finalQuery);
			Query query4 = session.createQuery(finalQuery);
			Iterator query4Itr = query4.list().iterator();
			while (query4Itr.hasNext())
			{
				//iLogger.info("Inside 5th while");
				Object[] result = (Object[]) query4Itr.next();
				//DF20160224 @Roopa for fixing EAInterface query taking long time to execute(3 min)
				if(! tempFileList.contains(result[0].toString())){
				EAInterfaceImpl implResp1 = new EAInterfaceImpl();
				if (result[0] != null) {
					implResp1.setInterfaceFileName(result[0].toString());
				}
				if (result[1] != null) {
					totalRecord=((Integer)result[1]);
					implResp1.setProcessedRecord(totalRecord);
				}
				if(result[2]!=null){
					implResp1.setFileProcessedTime(sdf.format(result[2]));
				}
				implResp1.setRejectedRecord(0);
				eaDateSummList.add(implResp1);
				}
			}

		}
		catch (Exception e) 
		{
			fLogger.fatal("Exception :" + e.getMessage());

			e.printStackTrace();
		}

		finally 
		{
			if (session.getTransaction().isActive()) 
			{
				session.getTransaction().commit();
			}

			if (session.isOpen()) 
			{
				session.flush();
				session.close();
			}
		}
		return eaDateSummList;
	}

	//DF20190220: Z1007653 - Introducing method getEAWeekSummaryDetails to give week wise (current day - 7) interface summary details	
	public List<EAInterfaceImpl> getEAWeekSummaryDetails(String searchDate, String interfaceName) {
		List<EAInterfaceImpl> eaWeekSummList = new LinkedList<EAInterfaceImpl>();
		List<String> tempFileList = new ArrayList<String>();
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query fileQuery = null;
		String fileName = interfaceName;
		if ("AssetInstallationDetails".equalsIgnoreCase(fileName)) {
			fileName = "AssetInstallation";
		}
		if ("ServiceHistory".equalsIgnoreCase(fileName)) {
			fileName = "AssetServiceDetails";
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			String weekDays[] = new String[7];
			String weekDaysFormatted[] = new String[7];
			Date currentDate = new Date();
			weekDays[0] = dateFormat.format(currentDate);
			weekDaysFormatted[0] = dateFormat1.format(currentDate);
			int i;
			for (i = 1; i <= 6; i++) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -i);
				Date todate1 = cal.getTime();
				String fromdate = dateFormat.format(todate1);
				String formatteddate = dateFormat1.format(todate1);
				weekDays[i] = fromdate;
				weekDaysFormatted[i]=formatteddate;
			}
			if (weekDays.length == 7) {
				String hql = " from InterfaceLogDetails where (filename like '%"
						+ weekDays[0] + "%' " + "or filename like '%" 
						+ weekDays[1] + "%' " + "or filename like '%" 
						+ weekDays[2] + "%' " + "or filename like '%" 
						+ weekDays[3] + "%' " + "or filename like '%"
						+ weekDays[4] + "%' " + "or filename like '%" 
						+ weekDays[5] + "%' " + "or filename like '%" 
						+ weekDays[6] + "%') ";
				if(!"All".equalsIgnoreCase(fileName)){
					hql=hql+"and filename like '%"+fileName+"%'";
				}

				fileQuery = session.createQuery(hql);
			}
			iLogger.info(fileQuery);
			Iterator fileItr = fileQuery.list().iterator();

			while (fileItr.hasNext()) {
				InterfaceLogDetails detailsObj = (InterfaceLogDetails) fileItr
						.next();
				if (detailsObj.getFailureCount() >= 0) {
					EAInterfaceImpl implResp = new EAInterfaceImpl();
					implResp.setInterfaceFileName(detailsObj.getFilename());
					implResp.setRejectedRecord(detailsObj.getFailureCount());
					implResp.setProcessedRecord(detailsObj.getSucessCount());

					Query processedTimeQ = session
							.createQuery(" from EALogDetailsEntity where fileName = '"
									+ detailsObj.getFilename() + "'");
					Iterator processedTimeItr = processedTimeQ.list()
							.iterator();
					if (processedTimeItr.hasNext()) {
						EALogDetailsEntity eaLogObj = (EALogDetailsEntity) processedTimeItr
								.next();
						implResp.setFileProcessedTime(sdf.format(eaLogObj
								.getProcessedTimestamp()));
					}

					eaWeekSummList.add(implResp);
					tempFileList.add(detailsObj.getFilename());
				}
			}
			// Query for fetching file processed time from the table EA_LogDetails_Entity & Fault_Details
			String hql2 = "select count(b.fileName), a.fileName, a.noOfRecords, a.processedTimestamp from EALogDetailsEntity a, FaultDetails b where (a.fileName ";
			hql2 = hql2 + "like '%" + weekDays[0]+ "%' "
						+ "or a.fileName like '%" + weekDays[1] + "%' "
						+ "or a.fileName like '%" + weekDays[2] + "%' "
						+ "or a.fileName like '%" + weekDays[3] + "%' "
						+ "or a.fileName like '%" + weekDays[4] + "%' "
						+ "or a.fileName like '%" + weekDays[5] + "%' "
						+ "or a.fileName like '%" + weekDays[6] + "%') ";
			if (!("All".equalsIgnoreCase(interfaceName))) {
				hql2 = hql2 + "and a.process = '" + interfaceName + "' ";
			}
			hql2 = hql2	+ "and a.fileName=b.fileName and a.process=b.process group by b.fileName";

			Query query1 = session.createQuery(hql2);
			Iterator itr1 = query1.list().iterator();
			Object[] result1 = null;

			// Iterate result
			while (itr1.hasNext()) {
				result1 = (Object[]) itr1.next();
				EAInterfaceImpl implResp = new EAInterfaceImpl();
				int failRecord = 0;
				int processRecord = 0;
				int totalRecord = 0;
				if (result1[0] != null) {
					Long count = (Long) result1[0];
					failRecord = count.intValue();
					implResp.setRejectedRecord(failRecord);
				}
				if (result1[1] != null) {
					implResp.setInterfaceFileName(result1[1].toString());
					tempFileList.add(result1[1].toString());
				}
				if (result1[2] != null) {
					totalRecord = ((Integer) result1[2]);
				}
				if (result1[3] != null) {
					implResp.setFileProcessedTime(sdf.format(result1[3]));
				}
				processRecord = totalRecord - failRecord;
				implResp.setProcessedRecord(processRecord);

				eaWeekSummList.add(implResp);
			}
			
			int totalRecord=0;
			String selectQuery ="SELECT a.fileName,a.noOfRecords,a.processedTimestamp";
			String fromQuery =" FROM EALogDetailsEntity a ";
			String whereQuery =" WHERE (a.processedTimestamp like '" + weekDaysFormatted[0]+ "%' "
					+ "or a.processedTimestamp like '%" + weekDaysFormatted[1] + "%' "
					+ "or a.processedTimestamp like '%" + weekDaysFormatted[2] + "%' "
					+ "or a.processedTimestamp like '%" + weekDaysFormatted[3] + "%' "
					+ "or a.processedTimestamp like '%" + weekDaysFormatted[4] + "%' "
					+ "or a.processedTimestamp like '%" + weekDaysFormatted[5] + "%' "
					+ "or a.processedTimestamp like '%" + weekDaysFormatted[6] + "%') ";
			if (!("All".equalsIgnoreCase(interfaceName))) {
				whereQuery = whereQuery + "and a.process = '" + interfaceName + "' ";
			}
			String finalQuery= selectQuery+fromQuery+whereQuery;
			iLogger.info("Final query for Week: "+finalQuery);
			Query query4 = session.createQuery(finalQuery);
			Iterator query4Itr = query4.list().iterator();
			while (query4Itr.hasNext())
			{
				Object[] result = (Object[]) query4Itr.next();
				if(! tempFileList.contains(result[0].toString())){
				EAInterfaceImpl implResp1 = new EAInterfaceImpl();
				if (result[0] != null) {
					implResp1.setInterfaceFileName(result[0].toString());
				}
				if (result[1] != null) {
					totalRecord=((Integer)result[1]);
					implResp1.setProcessedRecord(totalRecord);
				}
				if(result[2]!=null){
					implResp1.setFileProcessedTime(sdf.format(result[2]));
				}
				implResp1.setRejectedRecord(0);
				eaWeekSummList.add(implResp1);
				}
			}
			
			

		} catch (Exception e) {
			fLogger.fatal("Exception :" + e.getMessage());
			e.printStackTrace();
		}
		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}
			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}
		return eaWeekSummList;
	}
	
	//Ends here
	
	
	public List<EAInterfaceImpl> getEADetailData(String fileName) {
		// TODO Auto-generated method stub

		Logger fLogger = FatalLoggerClass.logger;

		List<EAInterfaceImpl> eaDetailslist = new LinkedList<EAInterfaceImpl>();
		DomainServiceImpl domainService = new DomainServiceImpl();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try{

			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			Query query2 = session.createQuery(" select a.failureCounter, a.messageString, a.failureCause from FaultDetails a "
					+ " where a.fileName like '"+fileName+"%' group by a.fileName,a.messageString ");
			Iterator itr2 = query2.list().iterator();
			Object[] result2 = null;
			while (itr2.hasNext()) {
				result2 = (Object[]) itr2.next();
				EAInterfaceImpl implResp = new EAInterfaceImpl();
				if(result2[0]!=null){
					implResp.setReProcessCount((Integer)result2[0]);
				}
				if(result2[1]!=null){
					implResp.setRecord(result2[1].toString());
				}
				if(result2[2]!=null){
					implResp.setFailureForRejection(result2[2].toString());
				}

				eaDetailslist.add(implResp);

			}

		}
		catch (Exception e) 
		{
			fLogger.fatal("Exception :" + e.getMessage());

			e.printStackTrace();
		}

		finally 
		{
			if (session.getTransaction().isActive()) 
			{
				session.getTransaction().commit();
			}

			if (session.isOpen()) 
			{
				session.flush();
				session.close();
			}
		}
		return eaDetailslist;

	}
}
