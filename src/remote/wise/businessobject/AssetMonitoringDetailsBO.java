/*
 * JCB6370 : 20230328 : Dhiraj K : Event Map not getting loaded
 */
package remote.wise.businessobject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetMonitoringDetailEntity;
import remote.wise.businessentity.AssetMonitoringDetailExtended;
import remote.wise.businessentity.AssetMonitoringHeaderEntity;
import remote.wise.businessentity.AssetMonitoringSnapshotEntity;
import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.ExtendedMonitoringParameters;
import remote.wise.businessentity.IndustryEntity;
import remote.wise.businessentity.IndustryStandardEntity;
import remote.wise.businessentity.MonitoringParameters;
import remote.wise.businessentity.ParameterTypeEntity;
import remote.wise.businessentity.RecordTypeEntity;
import remote.wise.businessentity.RejectedPacketDetailsEntity;
import remote.wise.businessentity.UomMasterEntity;
import remote.wise.dal.DynamicAMD_DAL;
import remote.wise.dal.DynamicAMH_DAL;
import remote.wise.dal.DynamicAMS_DAL;
import remote.wise.dal.DynamicAMS_Doc_DAL;
import remote.wise.dal.DynamicAssetEvent_DAL;
import remote.wise.dal.DynamicTAssetMonData_DAL;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.pojo.AMSDoc_DAO;
import remote.wise.pojo.AmdDAO;
import remote.wise.pojo.AmsDAO;
import remote.wise.pojo.TAssetMonDataDAO;
import remote.wise.service.datacontract.AssetControlUnitRespContract;
import remote.wise.service.implementation.AssetEventLogImpl;
import remote.wise.service.implementation.AssetProvisioningImpl;
import remote.wise.service.implementation.DomainServiceImpl;
import remote.wise.service.implementation.MachineHealthDetailsImpl;

import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
import remote.wise.util.StaticProperties;
//import remote.wise.util.WiseLogger;
import remote.wise.util.XmlParser;
import remote.wise.util.XsdParser;

public class AssetMonitoringDetailsBO {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("AssetMonitoringDetailsBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("AssetMonitoringDetailsBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("AssetMonitoringDetailsBO:","info");*/
	//DefectId: - 1435-  Rajani Nagaraju - 20131010 - Update rejected packets logger if the packet is rejected for any reason
	/*public static WiseLogger rejectedPacketsInfo = WiseLogger.getLogger("AssetMonitoringDetailsBO:","rejectedPackets");*/
	
	String serialNumber;
	int transactionNumber;
	Timestamp transactionTimeStamp;
	HashMap<String, String> parameterNameValueMap;
	//Logger infoLogger = Logger.getLogger("infoLogger");
	String profileName;
	
	String AMHAMDQuery;
	String TAssetMonQuery=null;
	String stratTAssetMonQuery=null;
	String endTAssetMonQuery=null;
	List<AssetEventLogImpl> implListFromAMHAMD;

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public int getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(int transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public Timestamp getTransactionTimeStamp() {
		return transactionTimeStamp;
	}

	public void setTransactionTimeStamp(Timestamp transactionTimeStamp) {
		this.transactionTimeStamp = transactionTimeStamp;
	}

	public HashMap<String, String> getParameterNameValueMap() {
		return parameterNameValueMap;
	}

	public void setParameterNameValueMap(
			HashMap<String, String> parameterNameValueMap) {
		this.parameterNameValueMap = parameterNameValueMap;
	}

	public AssetMonitoringDetailsBO() {
		serialNumber = null;
		transactionNumber = 0;
		transactionTimeStamp = null;
		parameterNameValueMap = new HashMap<String, String>();
		profileName = null;
	}

	// *************************************** get Asset Monitoring details for
	// the given Serial Number *************************************
	/**
	 * This method returns snapshot details for a given serial Number
	 * 
	 * @param serialNumber
	 * @return snapshot details
	 */
	public AssetMonitoringDetailsBO getAssetMonitoringDetails(
			String serialNumber) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		//session.beginTransaction();

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger fLogger = FatalLoggerClass.logger;
		
		try {
		
			
			//DF20160718 @Roopa Dynamic AMD changes
			

			
		AssetEntity asset=null;
			
			int seg_ID=0;
			
			try{
				
				Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
				Iterator assetItr = assetQ.list().iterator();
				if(assetItr.hasNext())
				{
					asset = (AssetEntity)assetItr.next();
				}
				
			}
			catch(Exception e)
			{
				fLogger.fatal("DownLoadAEMPService::getDownloadAemp::Exception::"+e.getMessage());
			}
			
			if(asset!=null){
				
				this.profileName=asset.getProductId().getAssetGroupId().getAsset_group_name();
				seg_ID=asset.getSegmentId();
			}
			
			//DF20161222 @Roopa changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column and TAssetMonData changes
			
			String txnKey="AssetMonitoringDetailsBO::getAssetMonitoringDetails";
			
			List<AMSDoc_DAO> snapshotObj=new ArrayList<AMSDoc_DAO> ();

			DynamicAMS_Doc_DAL amsDaoObj=new DynamicAMS_Doc_DAL();

			snapshotObj=amsDaoObj.getAMSData(txnKey, serialNumber);
			
			
			HashMap<String,String> txnDataMap=new HashMap<String, String>();
		
			if(snapshotObj!=null && snapshotObj.size()>0){
				
		
				
				txnDataMap=snapshotObj.get(0).getTxnData();
				
				if(txnDataMap.size()>0){
				
				this.parameterNameValueMap.put("LAT",txnDataMap.get("LAT"));
				this.parameterNameValueMap.put("LONG",txnDataMap.get("LONG"));
				this.parameterNameValueMap.put("CMH",txnDataMap.get("CMH"));
				}
			}
		
			

		} catch (Exception e) {
			//e.printStackTrace();
			fLogger.fatal("Exception :" + e);
		}

		finally {
			
          if (session!=null && session.isOpen()) {
				session.close();
			}

		}

		return this;
	}

	// ***************************************END of get Asset Monitoring
	// details for the given Serial Number *************************************

	// *************************************************** FROM FIRMWARE
	// *******************************************************************

	// get the monitoring parameters entity for the given parameter Id
	public MonitoringParameters getMonitoringParameters(int parameterId) {
		MonitoringParameters monitoringObj = new MonitoringParameters(
				parameterId);
		if (monitoringObj.getParameterId() == 0)
			return null;
		else
			return monitoringObj;
	}

	/**
	 * Parse the XSD placed in specific location and insert the data into
	 * database
	 * 
	 * @return Returns the status String
	 * @throws IOException
	 * @throws CustomFault
	 */
	@SuppressWarnings("unchecked")
	public String setMonitoringParameters() {
		String status = "SUCCESS";

		// Parse the XSD placed in predefined location
		XsdParser xsdParser = new XsdParser();
		xsdParser = xsdParser.parseXsd();

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;
    	
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try {
			// get the version number of XSD
			String xsdVersion = xsdParser.getVersion();

			Properties prop = new Properties();
			String Record = null;
			String Event = null;
			String Info = null;
			String EquipmentHeader = null;

			prop.load(getClass().getClassLoader().getResourceAsStream(
					"remote/wise/resource/properties/configuration.properties"));
			Record = prop.getProperty("Record");
			Event = prop.getProperty("Event");
			Info = prop.getProperty("Info");
			EquipmentHeader = prop.getProperty("EquipmentHeader");

			// get the parameter metadata to be inserted for new xsd version
			HashMap<String, List<String>> parameterMetadataMap = xsdParser
					.getParametersMasterMap();

			// check whether the version already exists
			Query query = session
					.createQuery("from IndustryStandardEntity where versionNumber="
							+ xsdVersion);
			Iterator itr = query.list().iterator();

			while (itr.hasNext()) {
				IndustryStandardEntity industryStandard = (IndustryStandardEntity) itr
						.next();
				if (industryStandard.isIncompatibleVersion() == false)
					return "FAILURE";
				else {
					List<String> existingParamType = new LinkedList<String>();

					// get the existing parameter types for the incompatible
					// version
					Query query4 = session
							.createQuery("from ParameterTypeEntity where standardId="
									+ industryStandard.getStandardId());
					Iterator itr4 = query4.list().iterator();
					while (itr4.hasNext()) {
						ParameterTypeEntity paramTypeEnt = (ParameterTypeEntity) itr4
								.next();
						existingParamType.add(paramTypeEnt
								.getParameterTypeName());
					}

					// get the remaining parameter types from the XSD parser and
					// insert into metadata table
					for (int g = 0; g < parameterMetadataMap.size(); g++) {
						String xsdParamType = (String) parameterMetadataMap
								.keySet().toArray()[g];
						if (xsdParamType.equalsIgnoreCase(EquipmentHeader)) {
							continue;
						}

						if (!(existingParamType.contains(xsdParamType))) {
							// Insert into parameter type
							ParameterTypeEntity paramTypeEnt = new ParameterTypeEntity();
							paramTypeEnt.setStandardId(industryStandard);
							paramTypeEnt.setParameterTypeName(xsdParamType);
							paramTypeEnt.save();

							// get the parameter type id for the newly inserted
							// parameter type
							ParameterTypeEntity paramType = null;

							Query query5 = session
									.createQuery("from ParameterTypeEntity where standardId="
											+ industryStandard.getStandardId()
											+ " and parameterTypeName='"
											+ xsdParamType + "'");
							Iterator itr5 = query5.list().iterator();
							while (itr5.hasNext()) {
								paramType = (ParameterTypeEntity) itr5.next();
							}

							// Insert the corresponding monitoring parameters
							List<String> xsdParams = (List<String>) parameterMetadataMap
									.values().toArray()[g];
							for (int e = 0; e < xsdParams.size(); e++) {
								MonitoringParameters monitParams = new MonitoringParameters();
								monitParams.setParameterTypeId(paramType);
								monitParams.setParameterName(xsdParams.get(e)
										.substring(4));
								if (xsdParams.get(e).startsWith(Record)) {
									monitParams.setRecordType("Record");
								} else if (xsdParams.get(e).startsWith(Event)) {
									monitParams.setRecordType("Event");
								} else if (xsdParams.get(e).startsWith(Info)) {
									monitParams.setRecordType("Info");
								}
								monitParams.save();
							}

						}
					}
					// Make Industry standard compatible version
					industryStandard.setIncompatibleVersion(false);
					session.update(industryStandard);

					return "SUCCESS";
				}
			}

			// get the Industry entity for JCB
			DomainServiceImpl domainService = new DomainServiceImpl();
			IndustryEntity industryEntity = domainService.getIndustryDetails(1);

			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			// create a new row for Industry standard for the new Xsd version
			// number
			IndustryStandardEntity industryStandard = new IndustryStandardEntity();
			industryStandard.setIndustryId(industryEntity);
			industryStandard.setStandardName("AEMP");
			industryStandard.setVersionNumber(xsdVersion);
			industryStandard.setIncompatibleVersion(false);
			industryStandard.save();

			// get the Industry Standard Id for the newly inserted row
			IndustryStandardEntity industryStandardEntity = null;
			Query industryStandardQuery = session
					.createQuery("from IndustryStandardEntity where industryId=1 and standardName = 'AEMP' and versionNumber="
							+ xsdVersion);
			Iterator strdItr = industryStandardQuery.list().iterator();

			while (strdItr.hasNext()) {
				industryStandardEntity = (IndustryStandardEntity) strdItr
						.next();
			}
			if (industryStandardEntity == null) {
				throw new CustomFault("Industry Standard not created properly");
			}

			for (int i = 0; i < parameterMetadataMap.size(); i++) {
				String parameterType = (String) parameterMetadataMap.keySet()
						.toArray()[i];

				if (parameterType.equalsIgnoreCase("EquipmentHeader")) {
					continue;
				}

				// create the parameter types specified in xsd for its new
				// version
				ParameterTypeEntity parameterTypeEntity = new ParameterTypeEntity();
				parameterTypeEntity.setStandardId(industryStandardEntity);
				parameterTypeEntity.setParameterTypeName(parameterType);
				parameterTypeEntity.save();

				// get the primary key for the Parameter Type created above for
				// the new xsd version

				Query paramTypeQuery = session
						.createQuery("from ParameterTypeEntity where standardId="
								+ industryStandardEntity.getStandardId()
								+ " and "
								+ "parameterTypeName='"
								+ parameterType + "'");
				Iterator paramTypeItr = paramTypeQuery.list().iterator();
				ParameterTypeEntity paramTypeEntity = null;
				while (paramTypeItr.hasNext()) {
					paramTypeEntity = (ParameterTypeEntity) paramTypeItr.next();
				}

				// create monitoring parameters for the given parameter type
				List<String> parameters = (List<String>) parameterMetadataMap
						.values().toArray()[i];

				for (int j = 0; j < parameters.size(); j++) {
					String monitoringParameterName = parameters.get(j);
					MonitoringParameters monitoringParameters = new MonitoringParameters();
					monitoringParameters.setParameterTypeId(paramTypeEntity);
					monitoringParameters
							.setParameterName(monitoringParameterName
									.substring(4));
					if (monitoringParameterName.startsWith(Record)) {
						monitoringParameters.setRecordType("Record");
					} else if (monitoringParameterName.startsWith(Event)) {
						monitoringParameters.setRecordType("Event");
					} else if (monitoringParameterName.startsWith(Info)) {
						monitoringParameters.setRecordType("Info");
					}
					monitoringParameters.save();
				}

			}

		}

		catch (CustomFault e) {
			status = "FAILURE";
			bLogger.error("Custom Fault: " + e.getFaultInfo());
		}

		catch (Exception e) {
			status = "FAILURE";
			fLogger.fatal("Exception :" + e.getMessage());
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
		return status;
	}

	
	
	// ***********************************set the remote monitoring data received from the pricol device****************************************

	/**
	 * This method sets the remote monitoring data received from device 
	 * Defect ID: 873 - Rajani Nagaraju - Handle Multiple packets with same snapshotTime 
	 * DefectID 955 - Rajani Nagaraju - 20130715 - To handle backdated Log packets
	 * Defect Id: 1017 - Rajani Nagaraju - 20130730 - To handle Hello Packets
	 * DefectId: - 1435 - Rajani Nagaraju - 20131010 - Update rejected packets logger if the packet is rejected for any reason
	 * @param parser  Parsed result of XML input
	 * @return Returns the status String
	 */
	public String setRemoteMonitoringData(XmlParser parser, String processingFileName,int  lastTransNumber ) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Inside AMH Thread");
		/*try {
			Thread.currentThread().sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		infoLogger.info(" Resume AMH Thread");*/
		
		String status = "SUCCESS";

		int existingVersion = 0;
		boolean incompatibleVersion = true;

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		//Logger businessError = Logger.getLogger("businessErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;
    	Logger rLogger = RejectedPktLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		 //DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors 
		String DataPackets_UnprocessedFolderPath=null;
		String DataPackets_ProcessedFolderPath=null;
		String DataPackets_InProcessFolderPath =null;
		//DefectID: 20140408 - Rajani Nagaraju - Introducing the Intermediate Folder Path to avoid re-processing the same file from different threads
		try
		{
			//Properties prop1 = new Properties();
			//prop1.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
		
//			DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath");
//			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath");
//			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath");
//			
			
			
			/*if (prop1.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
    			DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath_SIT");
    			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath_SIT");
    			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath_SIT");
			} else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
				DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath_DEV");
    			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath_DEV");
    			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath_DEV");
			} else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
				DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath_QA");
    			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath_QA");
    			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath_QA");
			} else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
				DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath_PROD");
    			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath_PROD");
    			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath_PROD");
			} else {
				DataPackets_UnprocessedFolderPath = prop1.getProperty("DataPackets_UnprocessedFolderPath");
    			DataPackets_ProcessedFolderPath = prop1.getProperty("DataPackets_ProcessedFolderPath");
    			DataPackets_InProcessFolderPath = prop1.getProperty("DataPackets_InProcessFolderPath");
			}*/
			
			DataPackets_UnprocessedFolderPath = "/user/JCBLiveLink/EdgeProxy/data/M2M/conf/WISE_Unprocessed_XMLs";
			DataPackets_ProcessedFolderPath = "/user/JCBLiveLink/EdgeProxy/data/M2M/conf/AEMP_OUTPUT";
			DataPackets_InProcessFolderPath = "/user/JCBLiveLink/EdgeProxy/data/M2M/conf/InProcess_XMLs";
			
		}
		
		catch(Exception e)
		{
			fLogger.fatal(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Exception :"+e);
			Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    String err = result.toString();
    	    fLogger.fatal(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Exception trace: "+err);
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
		
		//DefectID: 20140408 - Rajani Nagaraju - Introducing the Intermediate Folder Path to avoid re-processing the same file from different threads
		//File processingFile = new File(DataPackets_UnprocessedFolderPath,processingFileName);
		File processingFile = new File(DataPackets_InProcessFolderPath,processingFileName);
		File archivedFile = null;
		
		
		try 
		{
			// get Client Details
			//Properties prop = new Properties();
			String clientName = null;
			String eventParameter = null;
			String Ignition_ON=null;
			String ToAwayParameterName=null;
			String LowFuelParameterName =null;
			
			//prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName = "JCB";
			eventParameter = "EventParameters";
			Ignition_ON= "IGNITION_ON";
			ToAwayParameterName = "TO_AWAY";
			
//			DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath");
//			DataPackets_ProcessedFolderPath = prop.getProperty("DataPackets_ProcessedFolderPath");
//			DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath");
			
			/*if (prop.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
    			DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath_SIT");
    			DataPackets_ProcessedFolderPath = prop.getProperty("DataPackets_ProcessedFolderPath_SIT");
    			DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath_SIT");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
				DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath_DEV");
    			DataPackets_ProcessedFolderPath = prop.getProperty("DataPackets_ProcessedFolderPath_DEV");
    			DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath_DEV");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
				DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath_QA");
    			DataPackets_ProcessedFolderPath = prop.getProperty("DataPackets_ProcessedFolderPath_QA");
    			DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath_QA");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
				DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath_PROD");
    			DataPackets_ProcessedFolderPath = prop.getProperty("DataPackets_ProcessedFolderPath_PROD");
    			DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath_PROD");
			} else {
				DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath");
    			DataPackets_ProcessedFolderPath = prop.getProperty("DataPackets_ProcessedFolderPath");
    			DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath");
			}*/
			DataPackets_UnprocessedFolderPath = "/user/JCBLiveLink/EdgeProxy/data/M2M/conf/WISE_Unprocessed_XMLs";
			DataPackets_ProcessedFolderPath = "/user/JCBLiveLink/EdgeProxy/data/M2M/conf/AEMP_OUTPUT";
			DataPackets_InProcessFolderPath = "/user/JCBLiveLink/EdgeProxy/data/M2M/conf/InProcess_XMLs";
			
			LowFuelParameterName ="LOW_FUEL";
			
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			// END of get Client Details
			
			if (!(session.isOpen())) 
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			// get the asset monitoring header details
			String version = parser.getVersion();
			String serialNumber = parser.getSerialNumber().substring(3);
			iLogger.info("Serial_Number:"+parser.getSerialNumber().substring(3)+"TransactionTime:"+parser.getTransactionTime()+" ************ :");
			Timestamp transactionTime = parser.getTransactionTime();
			// Change Made by Rajani - 20130626 - New Column CreatedDate added
			// to AMH to populate the data correctly to OLAP
			Timestamp currentTime = new Timestamp(new Date().getTime());

			// Added by Rajani Nagaraju - 20130715 - DefectID 955 - To handle the log packets with backdated Timestamp - Event Packets are received 
			// in sequence and Log packets are received in Sequence. These two might not be in sequence when considered together -
			// Because of the FW requirement, if the GSM is 'OFF' all the logs/event generated.... will be in Queue and as soon as the 
			// GSN is 'ON', first all the event packets are sent in sequence since Events are of high priority.... and then the log packets in sequence
			String receivedMessgeId = parser.getMessageId();
//			Keerthi : 12/02/14 : setting fota version number to table AMH
			String fwVersionNumber = parser.getFwVersionNumber();
		//	infoLogger.info("PIN : "+serialNumber+" FW VERSION NUMBER :"+fwVersionNumber);
			// get the asset monitoring data
			HashMap<String, HashMap<String, String>> hashMap = parser.getParamType_parametersMap();
			
			//DefectID: DF20131105 - Rajani Nagaraju - To Close Tow away Alert with next IgnitionON packet - To reflect the same in AMD
			if(hashMap.containsKey(eventParameter))
			{
				HashMap<String, String> paramValues = (HashMap<String, String>) hashMap.get(eventParameter);
				if(paramValues.containsKey(Ignition_ON))
				{
					String ignitionStatus = paramValues.get(Ignition_ON);
					if(ignitionStatus.equalsIgnoreCase("1"))
					{
						//Create an entry for Closure of Tow-away
						paramValues.put(ToAwayParameterName, "0");
						hashMap.put(eventParameter, paramValues);
					}
				}
				
			}

			// ---------------------------check whether this is the XML received
			// for Authentication
			String authenticationMsg = null;
			String manufacturingIndustryId = null;
			String fuelLevel = null;
			String fuelConsumedInLitres = null;
			String latitude = null;
			String longitude = null;
			// Added by Rajani Nagaraju - 20130715
			String eventRecordType = null;
			String logRecordType = null;
			String EngineRunningBand = null;
			// Added by Rajani Nagaraju - 20130730 - To Handle Hello Packet - DefectID:1017
			String HelloPacketParamName = null;
			// prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			
			authenticationMsg = "AuthenticationParameters";
			manufacturingIndustryId = "1";
			fuelLevel = "FuelLevel";
			fuelConsumedInLitres = "FuelConsumedInLitres";
			latitude = "Latitude";
			longitude = "Longitude";
			// Added by Rajani Nagaraju - 20130715
			eventRecordType = "Event Packet";
			logRecordType = "Log Packet";
			EngineRunningBand = "EngineRunningBand";
			// Added by Rajani Nagaraju - 20130730 - To Handle Hello Packet - 1017
			HelloPacketParamName = "HELLO";

			HashMap<Integer, String> engineRunningParamIdValue = new HashMap<Integer, String>();

			if (hashMap.containsKey(authenticationMsg)) 
			{
				//DefectId: - 1435-  Rajani Nagaraju - 20131010 - Update rejected packets logger if the packet is rejected for any reason
				//DefectID: DF20131230 - Rajani Nagaraju - Fixing Robustness of Loggers. RejectedPackets Log level changed from 'DEBUG' to 'WARN'
				rLogger.warn("Invalid XML - Contains Authentication Parameters");
				
				 //DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
				archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
				
				if(archivedFile.exists())
					archivedFile.delete();
		        boolean moveStatus = processingFile.renameTo(archivedFile);
		      
		       if(moveStatus)
		        {
		        	iLogger.info("Data Packet is Rejected - Invalid XML - Contains Authentication Parameters");
		        	iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
		        }
		       
				return "FAILURE";
			}

			// Validate SerialNumber - Authentication packet should have been received earlier for the serialNumber
			AssetDetailsBO assetDetails = new AssetDetailsBO();
			AssetEntity assetEnt = assetDetails.getAssetEntity(serialNumber);
			/*if (assetEnt == null || assetEnt.getSerial_number() == null) 
			{
				//DefectId: - 1435-  Rajani Nagaraju - 20131010 - Update rejected packets logger if the packet is rejected for any reason
				//DefectID: DF20131230 - Rajani Nagaraju - Fixing Robustness of Loggers. RejectedPackets Log level changed from 'DEBUG' to 'WARN'
				rejectedPacketsInfo.warn("Invalid VIN - "+serialNumber+", JCBRollOff data is not yet received for this VIN");
				
				 //DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
				archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
				
				if(archivedFile.exists())
					archivedFile.delete();
		        boolean moveStatus = processingFile.renameTo(archivedFile);
		      
		       if(moveStatus)
		        {
		        	infoLogger.info("Data Packet is Rejected - JCBRollOff data is not yet received for this VIN");
		        	infoLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
		        }
		       
				return "FAILURE";
			}*/

			if (!(session.isOpen())) 
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			// check for the duplication of composite primary key (SerialNumber-TrnsactionTime combination)
			/*
			 * Query qry = session.createQuery(
			 * "from AssetMonitoringHeaderEntity where serialNumber ='"
			 * +serialNumber+"' and transactionTime = '"+transactionTime+"'");
			 * Iterator itrtr = qry.list().iterator(); while(itrtr.hasNext()) {
			 * return "FAILURE"; }
			 */

			IndustryStandardEntity industryStandard = null;

			Query q = session.createQuery("from IndustryStandardEntity where versionNumber="+ version);
			Iterator itr = q.list().iterator();
			while (itr.hasNext()) 
			{
				industryStandard = (IndustryStandardEntity) itr.next();
				if (industryStandard.isIncompatibleVersion() == false)
					incompatibleVersion = false;
				existingVersion = 1;
			}

			// get the Industry entity for Manufacturing Industry
			DomainServiceImpl domainService = new DomainServiceImpl();
			IndustryEntity industryEntity = domainService.getIndustryDetails(Integer.parseInt(manufacturingIndustryId));

			if (!(session.isOpen())) 
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			// ************************ If the XSD is not received for the specified version **********************

			// Insert the parameter types and then the monitoring parameters
			if (existingVersion == 0) 
			{
				industryStandard = new IndustryStandardEntity();
				industryStandard.setIndustryId(industryEntity);
				industryStandard.setStandardName("AEMP");
				industryStandard.setVersionNumber(version);
				industryStandard.setIncompatibleVersion(true);
				session.save(industryStandard);

			}

			if (incompatibleVersion == true) 
			{
				List<String> existingParamTypes = new LinkedList<String>();

				if (!(session.isOpen())) 
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}

				// get the monitoring parameters already assigned for this incompatible version - if exists
				Query q2 = session.createQuery("from ParameterTypeEntity where standardId = "+ industryStandard.getStandardId());
				Iterator paramTypeItr = q2.list().iterator();
				while (paramTypeItr.hasNext()) 
				{
					ParameterTypeEntity paramType = (ParameterTypeEntity) paramTypeItr.next();
					existingParamTypes.add(paramType.getParameterTypeName());
				}

				// find the remaining parameter types to be inserted for incompatible version
				for (int i = 0; i < hashMap.size(); i++) 
				{
					String parameterType = (String) hashMap.keySet().toArray()[i];
					
					if (!(existingParamTypes.contains(parameterType))) 
					{
						// Insert into parameter Type Entity
						ParameterTypeEntity parameterTypeEntity = new ParameterTypeEntity();
						parameterTypeEntity.setParameterTypeName(parameterType);
						parameterTypeEntity.setStandardId(industryStandard);
						session.save(parameterTypeEntity);

						// Insert into monitoring parameters
						HashMap<String, String> monitoringParamMap = (HashMap<String, String>) hashMap.values().toArray()[i];
						for (int j = 0; j < monitoringParamMap.size(); j++) 
						{
							String monitoringParamName = (String) monitoringParamMap.keySet().toArray()[j];

							MonitoringParameters monParamObj = new MonitoringParameters();
							monParamObj.setParameterTypeId(parameterTypeEntity);
							monParamObj.setParameterName(monitoringParamName);
							session.save(monParamObj);
						}
					}

					session.getTransaction().commit();

				}

			}

			// *************************Insert the data into asset monitoring header*******************
			//AssetEntity assetEntity = domainService.getAssetDetails(serialNumber);
			AssetEntity assetEntity=null;
			
			if (!(session.isOpen())) 
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			Query assetQ = session.createQuery("from AssetEntity where serial_number='"+serialNumber+"'");
			Iterator assetItr = assetQ.list().iterator();
			while(assetItr.hasNext())
			{
				assetEntity = (AssetEntity)assetItr.next();
			}

			if(assetEntity==null)
			{
				rLogger.warn("Invalid VIN - "+serialNumber+", JCBRollOff data is not yet received for this VIN");
				//rejectedPacketsInfo.warn(xmlInput);
				
				if (!(session.isOpen())) 
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				
				//DF20141117 - Rajani Nagaraju - Log Rejected Record into DB
				RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
				rejectedPkt.setCreatedTime(currentTime);
				rejectedPkt.setSerialNumber(parser.getSerialNumber().substring(3));
				rejectedPkt.setTransactionTime(parser.getTransactionTime());
				rejectedPkt.setRejectionCause("JCBRollOff data is not yet received for this VIN ");
				session.save(rejectedPkt);
				
				
				//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
				archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
				
				if(archivedFile.exists())
					archivedFile.delete();
		        boolean moveStatus = processingFile.renameTo(archivedFile);
		      
		       if(moveStatus)
		        {
		        	iLogger.info("Data Packet is Rejected - JCBRollOff data is not yet received for this VIN");
		        	iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
		        }
		       
				return "FAILURE";
			}
			
			if (!(session.isOpen())) 
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			// DefectID 955 - Get the record type of the received packet
						RecordTypeEntity receivedRecordType = null;
						Query messageTypeQuery = session.createQuery(" from RecordTypeEntity where messageId='"+receivedMessgeId+"'");
						Iterator messageTypeItr = messageTypeQuery.list().iterator();
						while (messageTypeItr.hasNext()) 
						{
							receivedRecordType = (RecordTypeEntity) messageTypeItr.next();
						}
						
			// Defect ID: 873 Added by Rajani 20130627 - Logic modified to update the record if multiple packets with same snapshotTime 
			// is received instead of recording it two transactions in the application
			Query updateQuery = session.createQuery("from AssetMonitoringHeaderEntity where serialNumber='"+serialNumber+"' " +
							 						" and transactionTime='"+transactionTime+"' ");
			Iterator itratr = updateQuery.list().iterator();
			AssetMonitoringHeaderEntity assetMonitoringHeaderObj = null;
			while (itratr.hasNext()) 
			{
				assetMonitoringHeaderObj = (AssetMonitoringHeaderEntity) itratr.next();
				int updateCount = assetMonitoringHeaderObj.getUpdateCount();
				//DF20140616 - Rajani Nagaraju - If Log and Event packet are received with same transaction timestamp, record type of the transaction would be considered as event
				if(receivedRecordType.getRecordTypeName().equalsIgnoreCase(eventRecordType))
				{
					assetMonitoringHeaderObj.setRecordTypeId(receivedRecordType);
				}
				

				//DF20141110 - START - Rajani Nagaraju - To Monitor the same packets being created again and again
				assetMonitoringHeaderObj.setLastUpdatedTime(currentTime);
				assetMonitoringHeaderObj.setUpdateCount(updateCount+1);
				//DF20141110 - END - Rajani Nagaraju - To Monitor the same packets being created again and again
				session.update(assetMonitoringHeaderObj);
			}

			
			

			
			// New Record Insertion
			AssetMonitoringHeaderEntity assetMonitoringHeader = null;
			if (assetMonitoringHeaderObj == null) 
			{
				assetMonitoringHeader = new AssetMonitoringHeaderEntity();
				assetMonitoringHeader.setSerialNumber(assetEntity);
				assetMonitoringHeader.setTransactionTime(transactionTime);
				// Change Made by Rajani - 20130626 - New Column CreatedDate added to AMH to populate the data correctly to OLAP
				assetMonitoringHeader.setCreatedTimestamp(currentTime);
				assetMonitoringHeader.setRecordTypeId(receivedRecordType);
				assetMonitoringHeader.setFwVersionNumber(fwVersionNumber);
				
				//DF20141110 - START - Rajani Nagaraju - To Monitor the same packets being created again and again
				assetMonitoringHeader.setLastUpdatedTime(currentTime);
				assetMonitoringHeader.setUpdateCount(0);
				//DF20141110 - END - Rajani Nagaraju - To Monitor the same packets being created again and again
				
				session.save(assetMonitoringHeader);
				iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Insert to AMH");
			}

			// get the parameterId for the given parameter names
			HashMap<String, String> parameterValues = new HashMap<String, String>();
			List<String> parameterNames = new LinkedList<String>();

			for (int y = 0; y < hashMap.size(); y++) 
			{
				HashMap<String, String> valuesMap = (HashMap<String, String>) hashMap.values().toArray()[y];
				for (int z = 0; z < valuesMap.size(); z++) 
				{
					String name = (String) valuesMap.keySet().toArray()[z];
					String value = (String) valuesMap.values().toArray()[z];

					parameterValues.put(name, value);
					parameterNames.add(name);
				}
			}

			// get parameter Ids for the given parameter names
			HashMap<String, MonitoringParameters> nameValueMap = new HashMap<String, MonitoringParameters>();
			Query query5 = session.createQuery("from MonitoringParameters where parameterName in (:list)").setParameterList("list", parameterNames);
			Iterator itr5 = query5.list().iterator();
			while (itr5.hasNext()) 
			{
				MonitoringParameters parameters = (MonitoringParameters) itr5.next();
				nameValueMap.put(parameters.getParameterName(), parameters);
			}

			if (!(session.isOpen())) 
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			List<MonitoringParameters> currentParamsReceived = new LinkedList<MonitoringParameters>();
			List<Integer> currentParamsId = new LinkedList<Integer>();

			// Get the fuel left percentage -fuelLevel
			String fuelLevelInPerct = null;

			//DefectID: DF20131030 - Rajani Nagaraju - Tow away alert changes implementation - Based on new parameter GPS Fix
			List<Integer> totalParamList= new LinkedList<Integer>();
			// for each parameter insert the data into asset monitoring details
			for (int r = 0; r < parameterValues.size(); r++) 
			{
				String parName = (String) parameterValues.keySet().toArray()[r];
				String parValues = (String) parameterValues.values().toArray()[r];

				MonitoringParameters monitoringParamEntity = nameValueMap.get(parName);
				String convertedValueString = null;
				double convertedValue = 0;

				// check if UOM is specified in param values
				if (parValues.contains("unit")) 
				{
					String[] parValuesArray = parValues.split(" unit ");
					String[] splitparValue = null;
					String[] splitparUnit = null;

					if (parValuesArray[0].contains("-")) 
					{
						splitparValue = parValuesArray[0].split("-");
						splitparUnit = parValuesArray[1].split("-");
					} 
					
					else 
					{
						splitparValue = new String[] { parValuesArray[0] };
						splitparUnit = new String[] { parValuesArray[1] };
					}
					
					
					//DF20140313 - Rajani Nagaraju - To handle the packets with junk (or _) in Altitude or any other parameters
					String receivedUnit =null;
					int junkValue=0;
					String juckParamValue =""; 
					
					for (int y = 0; y < splitparValue.length; y++) 
					{
						try
						{
							double receivedValue = Double.parseDouble(splitparValue[y].trim());
						}
						catch(NumberFormatException e)
						{
							junkValue=1;
							juckParamValue=juckParamValue+splitparValue[y];
						}
					}
					
					if(junkValue==1)
					{
						convertedValueString = juckParamValue;
					}
					
					else
					{
						for (int y = 0; y < splitparValue.length; y++) 
						{
							double receivedValue = Double.parseDouble(splitparValue[y].trim());
							
							if(splitparUnit.length>y)
								receivedUnit = splitparUnit[y].trim();
							
							Query q6 = session.createQuery("from UomMasterEntity where receivedUnit='"+receivedUnit+"'");
							Iterator itr6 = q6.list().iterator();
							double conversionFactor = 0;
							while (itr6.hasNext()) 
							{
								UomMasterEntity uomMaster = (UomMasterEntity) itr6.next();
								conversionFactor = uomMaster.getConversionFactor();
							}
							
							convertedValue = convertedValue + (receivedValue * conversionFactor);
						}
						convertedValueString = Double.toString(convertedValue);
					}

				}

				
				else 
				{
					if ((parName.equalsIgnoreCase(latitude)) || (parName.equalsIgnoreCase(longitude))) 
					{
						double paramValueInDouble = Double.valueOf(parValues.replace('N', ' ').replace('E', ' ').trim());
						Double convertedValueInDouble = (((paramValueInDouble / 100) % 1) * 2 + (paramValueInDouble / 100) * 3) / 3;
						convertedValueString = convertedValueInDouble.toString();
					}

					else
						convertedValueString = parValues;
				}

				// Added by Rajani Nagaraju - 20130715 - DefectID 955
				if (receivedRecordType.getRecordTypeName().equalsIgnoreCase(logRecordType)) 
				{
					if (monitoringParamEntity.getParameterName().contains(EngineRunningBand)) 
					{
						engineRunningParamIdValue.put(monitoringParamEntity.getParameterId(),convertedValueString);
					}

				}

				// Insert the data into Asset monitoring detail entity
				if (assetMonitoringHeaderObj == null) 
				{
					AssetMonitoringDetailEntity assetMonitoringDetailObj = new AssetMonitoringDetailEntity();
					assetMonitoringDetailObj.setTransactionNumber(assetMonitoringHeader);
					assetMonitoringDetailObj.setParameterId(monitoringParamEntity);
					assetMonitoringDetailObj.setParameterValue(convertedValueString);
					session.save(assetMonitoringDetailObj);
					
					//DefectID: DF20131030 - Rajani Nagaraju - Tow away alert changes implementation - Based on new parameter GPS Fix
					totalParamList.add(monitoringParamEntity.getParameterId());
				}

				// Defect ID: 873 - Addition done By Rajani - 20130627 - To update the txns when  multiple packets received with same snapshotTime
				// Update the corresponding parameter to the existing txn with the same transaction timestap
				
				//DefectID: DF20140522 - Rajani Nagaraju -
				//If multiple transactions are received with same snapshot time, update all the parameters received in current txn.
				//Update Fuel Level Parameter only if it is not equal to 110. This is because, FW will send correct Fuel Level only in log and low fuel event
				//In all other packets it would be 110. For ex. If two packets are received a)Low fuel Event with fuel level 40 and b) Ignition on with fuel level 110
				//Dont update the transaction with fuel level as 110
				
				//else if (!(monitoringParamEntity.getParameterName().equalsIgnoreCase(fuelLevel)))
				else
				{

					/*
					 * if(receivedRecordType.getRecordTypeName().equalsIgnoreCase
					 * (logRecordType) ) {
					 */
					//get the fuel level received in current packet
					//DF20140616 - Rajani Nagaraju - Fuel Level String comparision updated from 110 to 110.0 - Since Converted value String will be in double
					if( (!(monitoringParamEntity.getParameterName().equalsIgnoreCase(fuelLevel))) ||
						((monitoringParamEntity.getParameterName().equalsIgnoreCase(fuelLevel))&&(!(convertedValueString.equalsIgnoreCase("110.0")))) )	
					{
						
						String updateStatus = updateExistingTransaction(assetMonitoringHeaderObj.getTransactionNumber(),
															monitoringParamEntity.getParameterId(),convertedValueString);
						if (updateStatus.equalsIgnoreCase("FAILURE"))
						{
							 //DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
							archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
							
							if(archivedFile.exists())
								archivedFile.delete();
					        boolean moveStatus = processingFile.renameTo(archivedFile);
					      
					       if(moveStatus)
					        {
					        	iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Data Packet with same transaction timestamp - but unable to update the transaction");
					        	iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
					        }
					       
					       return "FAILURE";
						}
					}	
					
				}

				if (!(monitoringParamEntity.getParameterName().equalsIgnoreCase(fuelLevel))) 
				{
					/*
					 * if(receivedRecordType.getRecordTypeName().equalsIgnoreCase
					 * (logRecordType) ) {
					 */
					currentParamsReceived.add(monitoringParamEntity);
					currentParamsId.add(monitoringParamEntity.getParameterId());
					
				}

				else 
				{
					fuelLevelInPerct = convertedValueString;
				}

				if (!(session.isOpen())) 
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
			}

			if (!(session.isOpen())) 
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			// Defect ID: 873 - Addition By Rajani 20130627 - Update the txns when multiple packets with same snapshot time is received
			// Insertion of remaining parameters + Insertion of FuelLevel happens only this is the new txn inserted for the VIN
			if (assetMonitoringHeaderObj == null) 
			{
				// if fuelLevelParameter itself is not received in the packet
				if (fuelLevelInPerct == null)
					fuelLevelInPerct = "0";

				// get the details of last received transaction - This is to insert all parameters for a transaction
				HashMap<MonitoringParameters, String> oldTxnData = new HashMap<MonitoringParameters, String>();
				String previousFuelLevel = null;

				// Change Made by Rajani - 20130626 - New Column CreatedDate added to AMH to populate the data correctly to OLAP
				/*
				 * Query query = session.createQuery(
				 * "from AssetMonitoringDetailEntity where transactionNumber = "
				 * +
				 * " (select max(transactionNumber) from AssetMonitoringHeaderEntity where serialNumber = '"
				 * +serialNumber+"' and " +
				 * " transactionNumber != '"+assetMonitoringHeader
				 * .getTransactionNumber
				 * ()+"' ) and parameterId not in (:list)").
				 * setParameterList("list", currentParamsReceived);
				 */

				// Modified by Rajani Nagaraju - 20130715 - DefectID 955
				iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Before executing prev AMD Query ***" +lastTransNumber);

				/*Query query = session.createQuery("select a from AssetMonitoringDetailEntity a, AssetMonitoringHeaderEntity b "
										+ " where b.transactionTime = (select max(transactionTime) from AssetMonitoringHeaderEntity where serialNumber = '"
										+ serialNumber + "' and transactionNumber != '"+ assetMonitoringHeader.getTransactionNumber()+"' and transactionTime < '"
										+ transactionTime+ "' ) and a.transactionNumber=b.transactionNumber and "
										+ " b.serialNumber='" + serialNumber+ "' and a.parameterId not in (:list)").setParameterList("list", currentParamsReceived);
*/
				
				Query query = session.createQuery("select a from AssetMonitoringDetailEntity a  "
						+ " where a.transactionNumber='"+lastTransNumber+"' "
						+ " and a.parameterId not in (:list)").setParameterList("list", currentParamsReceived);

				Iterator previousTxnDataItr = query.list().iterator();
				
				iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"After executing prev AMD Query ***");

				int lastDataReceived = 0;
				while (previousTxnDataItr.hasNext()) 
				{
					lastDataReceived = 1;
					AssetMonitoringDetailEntity lastData = (AssetMonitoringDetailEntity) previousTxnDataItr.next();

					if (!(lastData.getParameterId().getParameterName().equalsIgnoreCase(fuelLevel)))
						oldTxnData.put(lastData.getParameterId(),lastData.getParameterValue());
					else
						previousFuelLevel = lastData.getParameterValue();
				}

				// DefectID 955 - Added by Rajani Nagaraju - 20130716
				// Check for the last received Event/Log packet
				iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Before executing AMD Query ***");
				
				
				if (lastDataReceived == 0) 
				{
					/*Query qu = session.createQuery(" select a from AssetMonitoringDetailEntity a, AssetMonitoringHeaderEntity b "
											+ " where b.transactionTime = (select max(transactionTime) from AssetMonitoringHeaderEntity " 
											+ " where serialNumber = '"+serialNumber+"' and transactionNumber != '"+assetMonitoringHeader.getTransactionNumber()
											+ "' and transactionTime < '"+transactionTime+"' ) and a.transactionNumber=b.transactionNumber "
											+ " and  b.serialNumber= '"+serialNumber+ "'"
											+ " and a.parameterId not in (:list)").setParameterList("list", currentParamsReceived);*/
					
					Query qu = session.createQuery(" select a from AssetMonitoringDetailEntity a   "							
							+ " where a.transactionNumber= "+lastTransNumber+" "						
							+ " and a.parameterId not in (:list)").setParameterList("list", currentParamsReceived);
					Iterator quItr = qu.list().iterator();
					iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"After executing AMD *** ");
					while (quItr.hasNext()) 
					{
						lastDataReceived = 1;
						AssetMonitoringDetailEntity lastData = (AssetMonitoringDetailEntity) quItr.next();

						if (!(lastData.getParameterId().getParameterName().equalsIgnoreCase(fuelLevel)))
							oldTxnData.put(lastData.getParameterId(),lastData.getParameterValue());
						else
							previousFuelLevel = lastData.getParameterValue();
					}

				}

				if (lastDataReceived == 0) 
				{
					// get the monitoring parameters - If this is the first record getting inserted for the given serial number
					Query query2 = session.createQuery(" select a from MonitoringParameters a, ParameterTypeEntity b, IndustryStandardEntity c "
											+ " where a.parameterTypeId = b.parameterTypeId and b.standardId= c.standardId and c.versionNumber = '"
											+ version+"' and a.parameterId not in (:list1)").setParameterList("list1", currentParamsId);
					Iterator itr2 = query2.list().iterator();
					while (itr2.hasNext()) 
					{
						MonitoringParameters params = (MonitoringParameters) itr2.next();
						if (!(params.getParameterName().equalsIgnoreCase(fuelLevel)))
							oldTxnData.put(params, "0");
						else
							previousFuelLevel = "0";
					}
				}

				// Insert the previous data for parameters that are not received in the current transaction
				for (int k = 0; k < oldTxnData.size(); k++) 
				{
					MonitoringParameters parameterId = (MonitoringParameters) oldTxnData.keySet().toArray()[k];
					String parameterValue = (String) oldTxnData.values().toArray()[k];

					AssetMonitoringDetailEntity assetMonitoringDetailObj = new AssetMonitoringDetailEntity();
					assetMonitoringDetailObj.setTransactionNumber(assetMonitoringHeader);
					assetMonitoringDetailObj.setParameterId(parameterId);

					// Defect Id: 1017 - Rajani Nagaraju - 20130730 - To handle Hello Packets
					if (parameterId.getParameterName().equalsIgnoreCase(HelloPacketParamName)) 
					{
						assetMonitoringDetailObj.setParameterValue("0");
					}

					else 
					{
						//DF20140409 - Rajani Nagaraju - If it is Event Packet, Engine Running Bands will not be received and it should be zero
						if(  (receivedRecordType.getRecordTypeName().equalsIgnoreCase(eventRecordType)) 
								&& (parameterId.getParameterName().contains(EngineRunningBand)) )
						{
							assetMonitoringDetailObj.setParameterValue("0");
						}
						else
						{
							assetMonitoringDetailObj.setParameterValue(parameterValue);
						}
					}
					
					session.save(assetMonitoringDetailObj);
					//DefectID: DF20131030 - Rajani Nagaraju - Tow away alert changes implementation - Based on new parameter GPS Fix
					totalParamList.add(parameterId.getParameterId());
					

				}
				
				//DefectID: DF20131030 - Rajani Nagaraju - Tow away alert changes implementation - Based on new parameter GPS Fix
				//Insert the values if new monitoring parameters are added
				Query newParamQuery = session.createQuery(" from MonitoringParameters where parameterId not in (:list)").setParameterList("list", totalParamList);
				Iterator newParmItr = newParamQuery.list().iterator();
				while(newParmItr.hasNext())
				{
					MonitoringParameters monitParm = (MonitoringParameters)newParmItr.next();
					
					AssetMonitoringDetailEntity assetMonitoringDetailObj = new AssetMonitoringDetailEntity();
					assetMonitoringDetailObj.setTransactionNumber(assetMonitoringHeader);
					assetMonitoringDetailObj.setParameterId(monitParm);
					assetMonitoringDetailObj.setParameterValue("0");
					iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Before saving to AMD");
					session.save(assetMonitoringDetailObj);
					iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Insert to AMD");
				}
				
				// *************calculate the fuelConsumed In Litres***********
				// Added by Rajani Nagaraju - 20130715 - DefectID 955 - To handle the packets with backdated timestamp 
				// Fule Consumed In Litres will be calculated only when the log packets are received
				
				if (receivedRecordType.getRecordTypeName().equalsIgnoreCase(logRecordType)) 
				{
					double previousFuelLevelInDouble = 0;
					if (previousFuelLevel != null)
						previousFuelLevelInDouble = Double.parseDouble(previousFuelLevel);

					double currentFuelLevelInDouble = 0;
					if (fuelLevelInPerct != null)
						currentFuelLevelInDouble = Double.parseDouble(fuelLevelInPerct);

					// get the parameterId for fuelConsumptionInLitres
					ExtendedMonitoringParameters extendedParam = null;
					Query q3 = session.createQuery("from ExtendedMonitoringParameters where extendedParameterName='"+fuelConsumedInLitres+"'");
					Iterator itr3 = q3.list().iterator();
					while (itr3.hasNext()) 
					{
						extendedParam = (ExtendedMonitoringParameters) itr3.next();
					}

					if (extendedParam == null) 
					{
						throw new CustomFault("Metadata not defined: FuelConsumedInLitres");
					}

					if (previousFuelLevelInDouble > currentFuelLevelInDouble)
					{
						// get the fuel consumed percentage
						Double fuelConsumedPercentage = previousFuelLevelInDouble - currentFuelLevelInDouble;

						// get the capacity of the Fuel Tank in Litres
						Query q1 = session.createQuery(" select a. fuelCapacityInLitres from ProductProfileEntity a, AssetEntity b "
										+ " where a.productId = b.productId and b.serial_number = '"+serialNumber+"' and " +
										" b.active_status=true and b.client_id="+clientEntity.getClient_id()+ " ");
						List fuelCapacityList = q1.list();

						if (!(fuelCapacityList == null || fuelCapacityList.isEmpty())) 
						{
							Iterator itr1 = q1.list().iterator();
							while (itr1.hasNext()) 
							{
								double fuelTankCapacityInLitres = (Double) itr1.next();

								// calculate fuel consumed in litres
								Double fuelConsumptionInLitres = (fuelConsumedPercentage / 100) * fuelTankCapacityInLitres;

								// Insert data into assetMonitoringDetailsExtened
								AssetMonitoringDetailExtended extendedParams = new AssetMonitoringDetailExtended();
								extendedParams.setTransactionNumber(assetMonitoringHeader);
								extendedParams.setExtendedParameterId(extendedParam);
								extendedParams.setExtendedParameterValue(fuelConsumptionInLitres.toString());
								session.save(extendedParams);
							}
						}

						else 
						{
							// Insert data into assetMonitoringDetailsExtened
							AssetMonitoringDetailExtended extendedParams = new AssetMonitoringDetailExtended();
							extendedParams.setTransactionNumber(assetMonitoringHeader);
							extendedParams.setExtendedParameterId(extendedParam);
							extendedParams.setExtendedParameterValue("0");
							iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Before saving to AMD Extended");
							session.save(extendedParams);
							iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Insert to AMD Extended");
						}

					}

					else 
					{
						AssetMonitoringDetailExtended extendedParams = new AssetMonitoringDetailExtended();
						extendedParams.setTransactionNumber(assetMonitoringHeader);
						extendedParams.setExtendedParameterId(extendedParam);
						extendedParams.setExtendedParameterValue("0");
						iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Before Saving to AMD Extended");
						session.save(extendedParams);
						iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Insert to AMD Extended");
					}
				}

			}

			// Added by Rajani Nagaraju - 20130715 - DefectID 955 - Change Requirement - Event Packets are received in sequence and
			// Log packets are received in Sequence. These two might not be in sequence when considered together - Because of the FW requirement, 
			// if the GSM is 'OFF' all the logs/event generated... will be in Queue and as soon as the GSN is 'ON', first all the
			// event packets are sent in sequence since Events are of high priority.... and then the log packets in sequence

			// When the Log packet is received and if there are any Event Packets which is greater than this transaction timestamp,...
			// ..then propagate the Engine Running Bands to all those transactions
			
			//DF20140409 - Rajani Nagaraju - No need to update the sunbsequent EventPackets, because EngineRunningBands are not cummulative values
			//and hence in Event Packet it should be zero
			/*if (receivedRecordType.getRecordTypeName().equalsIgnoreCase(logRecordType)) 
			{
				int update = 0;
				List<AssetMonitoringHeaderEntity> amhObj = new LinkedList<AssetMonitoringHeaderEntity>();

				// Check for any transactions with the timestamp > this received Time
				Query checkTxn = session.createQuery(" from AssetMonitoringHeaderEntity where serialNumber ='"+ serialNumber+ "' and "
								+ " transactionTime > '"+ transactionTime+ "'");
				Iterator checkItr = checkTxn.list().iterator();
				while (checkItr.hasNext()) 
				{
					AssetMonitoringHeaderEntity header = (AssetMonitoringHeaderEntity) checkItr.next();
					amhObj.add(header);
					update = 1;
				}

				if (update == 1) 
				{
					for (int i = 0; i < engineRunningParamIdValue.size(); i++) 
					{
						Integer paramId = (Integer) engineRunningParamIdValue.keySet().toArray()[i];
						String paramValue = (String) engineRunningParamIdValue.values().toArray()[i];

						Query txnUpdateQuery = session.createQuery(" update AssetMonitoringDetailEntity set parameterValue = '"
										+ paramValue + "' where parameterId=" + paramId+ " and transactionNumber in (:list) ").setParameterList("list", amhObj);
						int updated = txnUpdateQuery.executeUpdate();
					}
				}
			}*/

			
			// Added by Rajani Nagaraju 20130701. Resolution to the defectID-  821: Discrepency in the parameter data getting logged into DB.
			/*if (session.getTransaction().isActive()) 
			{
				session.getTransaction().commit();
			}*/
			
			//DF20140409 - Rajani Nagaraju - Adding data to SnapshotTable
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			Query snapshotQuery = session.createQuery(" from AssetMonitoringSnapshotEntity where serialNumber='"+serialNumber+"'");
			Iterator snapshotItr = snapshotQuery.list().iterator();
			AssetMonitoringSnapshotEntity snapshotObj = null;
			while(snapshotItr.hasNext())
			{
				snapshotObj = (AssetMonitoringSnapshotEntity)snapshotItr.next();
			}
			
			//Insertion of new VIN into snapshot table
			if(snapshotObj==null)
			{
				AssetMonitoringSnapshotEntity newSnapshotObj = new AssetMonitoringSnapshotEntity();
				newSnapshotObj.setSerialNumber(assetEntity);
				
				if(assetMonitoringHeaderObj==null){
					newSnapshotObj.setTransactionNumber(assetMonitoringHeader);
				}
				else{
					newSnapshotObj.setTransactionNumber(assetMonitoringHeaderObj);
				}
				newSnapshotObj.setTransactionTime(transactionTime);
				
				
				if( (receivedRecordType.getRecordTypeName().equalsIgnoreCase(logRecordType))  || ( parameterNames.contains(LowFuelParameterName)) )
				{
				//if( ! (fuelLevelInPerct==null || fuelLevelInPerct.equalsIgnoreCase("110")) )
					newSnapshotObj.setFuelLevel(fuelLevelInPerct);
				}
				
				session.save(newSnapshotObj);
			}
			//Update snapshot table for the VIN
			else
			{
				if( (snapshotObj.getTransactionTime().before(transactionTime)) || (snapshotObj.getTransactionTime().equals(transactionTime)) )
				{
					
					if(assetMonitoringHeaderObj==null){
						snapshotObj.setTransactionNumber(assetMonitoringHeader);
					}
					else{
						snapshotObj.setTransactionNumber(assetMonitoringHeaderObj);
					}
					snapshotObj.setTransactionTime(transactionTime);
					
					if( (receivedRecordType.getRecordTypeName().equalsIgnoreCase(logRecordType))  || ( parameterNames.contains(LowFuelParameterName)) )
					{
					//if( ! (fuelLevelInPerct==null || fuelLevelInPerct.equalsIgnoreCase("110")) )
						snapshotObj.setFuelLevel(fuelLevelInPerct);
					}
					session.update(snapshotObj);
				}
			}
			iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Insert to AMH Snapshot");
			
			if (session.getTransaction().isActive()) 
			{
				session.getTransaction().commit();
			}
			
			 //DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors 
			/*//Once the Data is succesfully inserted into DB, Move the XML Data Packet from Unprocessed_XML to AEMP_OUTPUT folder
			archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
			
			if(archivedFile.exists())
				archivedFile.delete();
	        boolean moveStatus = processingFile.renameTo(archivedFile);
	      
	       if(moveStatus)
	        {
	        	infoLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
	        }*/

		}

		catch (CustomFault e) 
		{
			// Added by Rajani Nagaraju 20130701. Resolution to the defectID 821: Discrepency in the parameter data getting logged into DB.
			if (session.getTransaction().isActive()) 
			{
				session.getTransaction().rollback();
			}

			status = "FAILURE";
			bLogger.error(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Custom Fault: " + e.getFaultInfo());
			
			 //DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If there is any business errors, move the data packet from Unprocessed_Xml to AEMP_Output folder
			archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
			
			if(archivedFile.exists())
				archivedFile.delete();
	        boolean moveStatus = processingFile.renameTo(archivedFile);
	      
	       if(moveStatus)
	        {
	        	iLogger.info("Business Error in Data Packet"+e.getFaultInfo());
	        	iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
	        }
		}

		catch (Exception e) 
		{
			// Added by Rajani Nagaraju 20130701. Resolution to the defectID 821: Discrepency in the parameter data getting logged into DB.
			if (session.getTransaction().isActive()) 
			{
				session.getTransaction().rollback();
			}
			e.printStackTrace();
			status = "FAILURE";
			fLogger.fatal(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Exception :" + e.getMessage());
			Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    String err = result.toString();
    	    fLogger.fatal(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Exception trace: "+err);
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
			
			
			 //DefectID: 20140408 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If there is any business errors, move the data packet from Unprocessed_Xml to AEMP_Output folder
			archivedFile = new File(DataPackets_UnprocessedFolderPath,processingFileName);
			
			if(archivedFile.exists())
				archivedFile.delete();
	        boolean moveStatus = processingFile.renameTo(archivedFile);
	      
	       if(moveStatus)
	        {
	        	iLogger.info("Fatal Error in Data Packet"+e);
	        	iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to UnProcessed folder");
	        }
		}

		finally 
		{
			// Commented by Rajani Nagaraju 20130701. Resolution to the defectID 821: Discrepency in the parameter data getting logged into DB.

			/*
			 * if(session.getTransaction().isActive()) {
			 * session.getTransaction().commit(); }
			 */

			if (session.isOpen()) 
			{
				session.flush();
				session.close();
			}
			
			iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"AMH Thread Completed");

		}
		return status;

	}

	// ***********************************END of set the remote monitoring data received from the pricol device****************************************
	
	// *********************************** Update transaction Details for the existing transactions ***************************************8
	// Defect ID: 873 Addition By Rajani - 20130627 - Update the txns when multiple packets with same snapshot time is received
	public String updateExistingTransaction(int transactionNumber,int parameterId, String parameterValue) 
	{
		String status = "SUCCESS";
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		//Logger infoLogger = Logger.getLogger("infoLogger");

		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try 
		{
			Query updateTxn = session.createQuery(" update AssetMonitoringDetailEntity set parameterValue='"+parameterValue+"' where "
							+ " transactionNumber="+ transactionNumber+ " and parameterId=" + parameterId);
			int rowCount = updateTxn.executeUpdate();
			if (rowCount > 0)
				iLogger.info("Transaction " + transactionNumber	+ " updated sccessfully for the parameterId "+ parameterId + " with value " + parameterValue);
		}

		catch (Exception e) 
		{
			status = "FAILURE";
			fLogger.fatal("Exception :" + e.getMessage());
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

		return status;
	}

	// ************************** END of Update transaction Details for the existing transactions ******************************

	

	
// IST TO GMT Conversion
	
	private static String stringISTtoGMTConversion(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
       
      
        Date newDate;
        try {
               newDate = sdf.parse(date);
        } catch (Exception e) {
        	e.printStackTrace();
               return date;
        }

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
      
        sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        
        return sdf1.format(newDate);
        // 	// Defect ID 1442: Settings-Audit Log - Number of records displayed are varying for particular date when there is a change in start date selection.- End
 }
	// ******************************************************Get Asset Event Log Details ********************************************
	/******************************************************* Asset Event Log Details ******************************************** /**
	 * This method returns the Machine Activity Details for the given date 
	 * Added by Rajani Nagaraju - DefectId: 685, 20130703 - To display event Log for LandmarkAlerts properly 
	 * Defect Id: 1017 -  Rajani Nagaraju - 20130730 - To handle Hello Packets
	 * DefectId: 1263 - 20130916 - Rajani Nagaraju - 10 min report is displayed for Engine_ON/Ignition_ON
	 * DefectID:1308 - Rajani Nagaraju - 20130918 - Not to display Application closed alerts 
	 * DefectID:1308 - Rajani Nagaraju - 20130925 - Query Tweaking as it was taking much time
	 * @param SerialNumber VIN as string input
	 * @param period Timestamp of the required date
	 * @return Returns the list of Machine Activity details 
	 */
	
	//DF20160715 @Roopa Fetching EventMap details for the given VIN from Dynamic AMH and AMD Tables
	
	public List<AssetEventLogImpl> getAssetEventLog(String SerialNumber, String period, int loginTenancyId) 
	{
		final String assetId=SerialNumber;
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	
    	implListFromAMHAMD = new LinkedList<AssetEventLogImpl>();
		
		List<AssetEventLogImpl> activeAlertsList=new LinkedList<AssetEventLogImpl>();
		
		List<AssetEventLogImpl> closedAlertsList=new LinkedList<AssetEventLogImpl>();
		
		List<AssetEventLogImpl> FinalAlertsList=new LinkedList<AssetEventLogImpl>();
		
		//DF20170130 @Roopa for Role based alert implementation
				DateUtil utilObj=new DateUtil();
				
				List<String> alertCodeList= utilObj.roleAlertMapDetails(null,loginTenancyId, "Display");
				
				ListToStringConversion conversion = new ListToStringConversion();

				StringBuilder alertCodeListAsString=conversion.getStringList(alertCodeList);
	
	/*	String tAssetMonTable=null;
		
		SimpleDateFormat dateTimeFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		// Validate the time period - Required format: yyyy-MM-dd
					SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
					Date inputDate = null;
					try {
						inputDate = dateFormatter.parse(period);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						fLogger.fatal("AssetEventLogService::getAssetEventLog::Exception::"+e1.getMessage());
					}
					String DateString = dateFormatter.format(inputDate);
					
					String txnTimestamp= DateString + " 00:00:00";
					
					


		Date txnDate = null;
		try {
			txnDate = dateTimeFormat.parse(txnTimestamp);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			fLogger.fatal("AssetEventLogService::getAssetEventLog::Exception::"+e.getMessage());
		}

	
		
		tAssetMonTable=new DateUtil().getDynamicTable("AssetEventLogService::getAssetEventLog::", txnDate);*/
		
		//******************************* STEP3: Get the AssetEntityObj for the given VIN
		AssetEntity asset=null;
		 MonitoringParameters monitorParam=null;
		 List<String> paramKeyList=new ArrayList<String>();
		Session session = HibernateUtil.getSessionFactory().openSession();

		try{
			// Shajesh : 2021-02-17 : Node Traverse issue in Hibernate call, So convert to native SQL call
			/*Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+SerialNumber+"'");*/
			Query assetQ = session.createSQLQuery("select * from asset where serial_number='" + SerialNumber + "'");
		    ((SQLQuery) assetQ).addEntity(AssetEntity.class);
			Iterator assetItr = assetQ.list().iterator();
			if(assetItr.hasNext())
			{
				asset = (AssetEntity)assetItr.next();
			}
		//	iLogger.info("AssetMonitoringDetailsBO :: getAssetEventLog()-----> assetQ " + assetQ);
			//DF20170130 @Roopa for Role based alert implementation, added paramId column in business_event table to match alert code and get the parameter key list from the monitoring prameters table. 
			// Shajesh : 2021-02-17 : Node Traverse issue in Hibernate call, So convert to native SQL call
			//Query monQ = session.createQuery(" from MonitoringParameters where RecordType='Event' ");
			/*Query monQ = session.createQuery("SELECT a from MonitoringParameters a, EventEntity b where a.parameterId=b.parameterID and b.eventCode in ("+alertCodeListAsString+") and a.RecordType='Event' ");*/
			Query monQ = session.createSQLQuery("SELECT * FROM monitoring_parameters a, business_event b WHERE a.Parameter_ID = b.Parameter_ID AND b.Alert_Code IN (" + alertCodeListAsString + ") AND a.Record_Type = 'Event'");
		    ((SQLQuery)monQ).addEntity(MonitoringParameters.class);
		    iLogger.info("AssetMonitoringDetailsBO :: getAssetEventLog()-----> monQ " + monQ);
			Iterator monItr = monQ.list().iterator();
			while(monItr.hasNext())
			{
				monitorParam = (MonitoringParameters)monItr.next();
				paramKeyList.add(monitorParam.getParameterKey());
			}
			
			paramKeyList.add("EVT_ENG");
			paramKeyList.add("EVT_IGN");
			paramKeyList.add("EVT_HELLO");
			
		}
		catch(Exception e)
		{
			fLogger.fatal("AssetEventLogService::getAssetEventLog::Exception::"+e.getMessage());
		}
		finally{
			if(session!=null && session.isOpen())  
			{
				session.close();
			}	
			
		}
		
		final int seg_ID=asset.getSegmentId();
		
		//DF20170809: SU334449 - Implementing timeZone implementations for SAARC changes
		final String timeZone=asset.getTimeZone();
		
		//String jsonObjQuery=null;//JCB6370.o
		StringBuilder jsonObjQuery=new StringBuilder();//JCB6370.n
		
		
	/*	for(int i=0;i<paramKeyList.size();i++){
			if(i==0)
			jsonObjQuery="JSON_OBJECT('"+paramKeyList.get(i)+"','1') ,JSON_OBJECT('"+paramKeyList.get(i)+"','0')"+",";
			else
				jsonObjQuery=jsonObjQuery+"JSON_OBJECT('"+paramKeyList.get(i)+"','1') ,JSON_OBJECT('"+paramKeyList.get(i)+"','0')"+",";	
		}
		jsonObjQuery=jsonObjQuery.substring(0, jsonObjQuery.length()-1);*/
		
		//DF20170525 @Roopa fix for not picking the events if multiple events are there in the event column row
		
		for(int i=0;i<paramKeyList.size();i++){
//			if(i==0)
//				jsonObjQuery="Events -> '$."+paramKeyList.get(i)+"' is not null OR";
//			else
//				jsonObjQuery=jsonObjQuery+" Events -> '$."+paramKeyList.get(i)+"' is not null OR";	
			//JCB6370.sn
			jsonObjQuery.append(" Events -> '$.\"");
			jsonObjQuery.append(paramKeyList.get(i));
			jsonObjQuery.append("\"' is not null OR");	 
			//JCB6370.en
		}
		//JCB6370.so
//		if(jsonObjQuery!=null)
//	     jsonObjQuery=jsonObjQuery+" Events -> '$.Unknown_ErrorCode' is not null";
//		else
//			jsonObjQuery=" Events -> '$.Unknown_ErrorCode' is not null";
		//JCB6370.eo
		jsonObjQuery=jsonObjQuery.append(" Events -> '$.Unknown_ErrorCode' is not null");//JCB6370.n
		
		
		/*TAssetMonQuery=" select t.Transaction_Timestamp, t.TxnData, t.Message_ID, t.Events"
                + " from "+tAssetMonTable+" t"
                + " where t.Segment_ID_TxnDate = "+seg_ID+" "
                + " and t.Transaction_Timestamp >= '"
                + stringISTtoGMTConversion(DateString + " 00:00:00.0") 
                + "' and t.Transaction_Timestamp <= '"
                + stringISTtoGMTConversion(DateString + " 23:59:59.0")
                + "' and t.Serial_Number='"
                + SerialNumber
                + "' "
                + " and (Message_ID -> '$.LOG'='1' OR "+jsonObjQuery+")"
                + " order by t.Transaction_Timestamp";

		  
		iLogger.info("AssetEventLogService AMHAMDQuery :: "+TAssetMonQuery);*/
		
		//System.out.println("AssetEventLogService AMHAMDQuery :: "+TAssetMonQuery);
		
		//start
		
		DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd");
		
		Date TxnTS=null;
		try {
			TxnTS = dateStr.parse(period);
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		String endTS=period+" 18:30:00";
		
		String startTAssetMonTable=null;
		String endTAssetMonTable=null;
		
		 String dynamicTable=new DateUtil().getDynamicTable("Eventmap", TxnTS);
		 
		 if(dynamicTable!=null){
			 endTAssetMonTable=dynamicTable;
			 
		 }
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(TxnTS);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		
		String startTS = dateStr.format(cal.getTime());
		// data will be take from previous day 6:30 to today 6:30
		startTS = startTS + " 18:30:00";
		
		Timestamp starttxnTimestamp = new Timestamp(cal.getTimeInMillis());
		
		  String startDynamicTable=new DateUtil().getDynamicTable("Eventmap", starttxnTimestamp);
			 
			 if(startDynamicTable!=null){
				 startTAssetMonTable=startDynamicTable;
			 }
			 else{
				 startTAssetMonTable =endTAssetMonTable; //Df20170102 @Roopa if strat table is not available than picking the data only from emd table.
			 }
			
			
			 if(startTAssetMonTable.equals(endTAssetMonTable)){
				 
				 TAssetMonQuery=" select t.Transaction_Timestamp, t.TxnData, t.Message_ID, t.Events"
			                + " from "+startTAssetMonTable+" t"
			                + " where t.Segment_ID_TxnDate = "+seg_ID+" "
			                + " and t.Transaction_Timestamp >= '"
			                + startTS +"'"
			                + " and t.Transaction_Timestamp <= '"
			                + endTS +"'"
			                + " and t.Serial_Number='"
			                + SerialNumber
			                + "' "
			                //+ " and (Message_ID -> '$.LOG'='1' OR Message_ID -> '$.LOG_PT'='1' OR "+jsonObjQuery+")"//JCB6370.o
			                + " and (Message_ID -> '$.LOG'='1' OR Message_ID -> '$.LOG_PT'='1' OR "+jsonObjQuery.toString()+")"//JCB6370.n
			                //aj20119610: DM4 events exclusion change
			                + " and ((Txndata -> '$.DTC_ID_2' is null or Txndata ->'$.DTC_ID_2'='########') and (Txndata -> '$.DTC_ID_4' is  null or Txndata ->'$.DTC_ID_4'='########') )"
			               // + " OR Events in ("+jsonObjQuery+") " //Role based alert fetch @Roopa
			               // + " OR Events -> '$.Unknown_ErrorCode' is not null)" //Fetching unknown dtc codes also if any
			                + " order by t.Transaction_Timestamp";
				// System.out.println("TAssetMonQuery:"+TAssetMonQuery);
				 
			 }
			 else{
				 
				 
				 stratTAssetMonQuery=" select t.Transaction_Timestamp, t.TxnData, t.Message_ID, t.Events"
			                + " from "+startTAssetMonTable+" t"
			                + " where t.Segment_ID_TxnDate = "+seg_ID+" "
			                + " and t.Transaction_Timestamp >= '"
			                + startTS +"'"
			                + " and t.Serial_Number='"
			                + SerialNumber
			                + "' "
			                //+ " and (Message_ID -> '$.LOG'='1' OR Message_ID -> '$.LOG_PT'='1' OR "+jsonObjQuery+")"//JCB6370.o
			                + " and (Message_ID -> '$.LOG'='1' OR Message_ID -> '$.LOG_PT'='1' OR "+jsonObjQuery.toString()+")"//JCB6370.n
			                //aj20119610: DM4 events exclusion change
			                + " and ((Txndata -> '$.DTC_ID_2' is null or Txndata ->'$.DTC_ID_2'='########') and (Txndata -> '$.DTC_ID_4' is  null or Txndata ->'$.DTC_ID_4'='########') )"
			               // + " OR Events in ("+jsonObjQuery+") " //Role based alert fetch @Roopa
			               // + " OR Events -> '$.Unknown_ErrorCode' is not null)" //Fetching unknown dtc codes also if any
			                + " order by t.Transaction_Timestamp";
				 
				 endTAssetMonQuery=" select t.Transaction_Timestamp, t.TxnData, t.Message_ID, t.Events"
			                + " from "+endTAssetMonTable+" t"
			                + " where t.Segment_ID_TxnDate = "+seg_ID+" "
			                + " and t.Transaction_Timestamp <= '"
			                + endTS +"'"
			                + " and t.Serial_Number='"
			                + SerialNumber
			                + "' "
			                //+ " and (Message_ID -> '$.LOG'='1' OR Message_ID -> '$.LOG_PT'='1' OR "+jsonObjQuery+")"//JCB6370.o
			                + " and (Message_ID -> '$.LOG'='1' OR Message_ID -> '$.LOG_PT'='1' OR "+jsonObjQuery.toString()+")"//JCB6370.n
			                //aj20119610: DM4 events exclusion change
			                + " and ((Txndata -> '$.DTC_ID_2' is null or Txndata ->'$.DTC_ID_2'='########') and (Txndata -> '$.DTC_ID_4' is  null or Txndata ->'$.DTC_ID_4'='########') )"
			               // + " OR Events in ("+jsonObjQuery+") " //Role based alert fetch @Roopa
			               // + " OR Events -> '$.Unknown_ErrorCode' is not null)" //Fetching unknown dtc codes also if any
			                + " order by t.Transaction_Timestamp";
				 
			 }
		
		//end
		
		
		   Thread thread1 = new Thread() {


			public void run() {
				
				try{
				//DF20170809: SU334449 - Passing timeZone for SAARC changes
				implListFromAMHAMD= new DynamicAMH_DAL().getAMHAMDListForEventMap(TAssetMonQuery,seg_ID,assetId,stratTAssetMonQuery,endTAssetMonQuery,timeZone);
				//System.out.println("Check1"+implListFromAMHAMD);
				}
				catch(Exception e){
					
					e.printStackTrace();
				}
				
			}
		};
		
		thread1.start();
		
		try {
			thread1.join();
		} catch (InterruptedException e1) {
			fLogger.fatal("AssetEventLogService::getAssetEventLog::Exception::"+e1.getMessage());
		}
		
		//Strat fetching Events from AssetEvent table
	
		//Open alerts
		
		//DF20170814 @Roopa appending error code corresponding to dtc code
		
		String openAlertQuery=" select a.Location,a.Event_ID, a.Active_Status, a.Event_Severity, a.Event_Generated_Time, a.Event_Type_ID, a.Asset_Event_ID, b.Event_Name, m.DTC_code, m.Error_Code"
				+ " from asset_event a, business_event b left outer join monitoring_parameters m on  b.Parameter_ID = m.Parameter_ID"
				+ " where a.Serial_Number='" + SerialNumber + "'"
				+ " and a.Event_Generated_Time >= '"+ stringISTtoGMTConversion(period + " 00:00:00.0") +"' and a.Event_Generated_Time <= '"+ stringISTtoGMTConversion(period + " 23:59:59.0") +"' "
				+ " and a.Event_Type_ID in(2, 4) "
				+ " and a.Event_ID = b.Event_ID "
				//+ " and b.Parameter_ID = m.Parameter_ID "
				+ " and b.Alert_Code in("+alertCodeListAsString+") " //Df20180129 Role based alert fetch @Roopa
				//+ " and a.Active_Status=1 " //DF20160809 @Roopa to get all the historical data(if the event has been closed after some time of generation, even that generated event should come for that particulat TS)
				+ " order by a.Event_Generated_Time";
		
		
		
		//System.out.println("AssetEventLogService Open Alerts Query:: "+openAlertQuery);
		
		DynamicAssetEvent_DAL dynamicAssetEvent_DALObj=new DynamicAssetEvent_DAL();
		try{
		//DF20170809: SU334449 - Passing timeZone for SAARC changes
		activeAlertsList = dynamicAssetEvent_DALObj.getActiveAlertsListtForEventMap(openAlertQuery, "open", timeZone);
		}
		catch(Exception e){
			fLogger.fatal("AssetEventLogService::getAssetEventLog::Exception::"+e.getMessage());
		}
		
		//iLogger.info("AssetEventLogService Final activeAlertsList size:: "+activeAlertsList.size());
		
		//closed alerts
		
		//DF20170814 @Roopa appending error code corresponding to dtc code
		
		//taking as a.Event_Closed_Time as Event_Generated_Time(Bcoz Using same generic method for active and closed alerts)
			//DF20190802:Abhishek::changed location to EventclosedLocation
		
		//DF20200116:Abhishek: fetcing location from location if EventClosedLocation is null.
				String closedAlertsQuery = " select ifnull(a.EventClosedLocation,location) as Location,a.UpdateSource,a.Event_ID, a.Active_Status, a.Event_Severity, a.Event_Closed_Time as Event_Generated_Time, a.Event_Type_ID, a.Asset_Event_ID, b.Event_Name, m.DTC_code, m.Error_Code"
						+ " from asset_event a, business_event b left outer join monitoring_parameters m on  b.Parameter_ID = m.Parameter_ID"
						+ " where a.Serial_Number='" + SerialNumber + "'"
						+ " and a.Event_Closed_Time >= '"+ stringISTtoGMTConversion(period + " 00:00:00.0") +"' and a.Event_Closed_Time <= '"+ stringISTtoGMTConversion(period + " 23:59:59.0") +"' "
						+ " and a.Event_Type_ID in(2, 4) "
						+ " and a.Event_ID = b.Event_ID "
						//+ " and b.Parameter_ID = m.Parameter_ID "
						+ " and b.Alert_Code in("+alertCodeListAsString+") " //Df20180129 Role based alert fetch @Roopa
						+ " and a.Active_Status=0 "
						+ " order by a.Event_Closed_Time";
				
				//System.out.println("AssetEventLogService Closed Alerts Query:: "+closedAlertsQuery);
				
				try{
					//DF20170809: SU334449 - Passing timeZone for SAARC changes		
					closedAlertsList = dynamicAssetEvent_DALObj.getActiveAlertsListtForEventMap(closedAlertsQuery, "close", timeZone);
				}
				catch(Exception e){
					fLogger.fatal("AssetEventLogService::getAssetEventLog::Exception::"+e.getMessage());
				}
		
//				iLogger.info("AssetEventLogService Final openAlertQuery :: "+openAlertQuery);
//				iLogger.info("AssetEventLogService Final closedAlertsQuery :: "+closedAlertsQuery);
		
		FinalAlertsList.addAll(activeAlertsList);
		FinalAlertsList.addAll(closedAlertsList);
		iLogger.info("activeAlertsList:"+activeAlertsList);
		iLogger.info("closedAlertsList:"+closedAlertsList);
		
		iLogger.info("implListFromAMHAMD :: "+implListFromAMHAMD);
		
		Collections.sort(FinalAlertsList, new transactionComparator());
		
		//iLogger.info("AssetEventLogService Final FinalAlertsList size:: "+FinalAlertsList.size());
		
		//iLogger.info("AssetEventLogService AMHAMDList size:: "+implListFromAMHAMD.size());
		
		List<AssetEventLogImpl> tempList=new LinkedList<AssetEventLogImpl>();
		//LL-198 : Sai Divya : 20240731 : EventMapBugFix.en
//		for(int i=0;i<implListFromAMHAMD.size();i++){
//			String isRemoveAlert = "0";
//			if(implListFromAMHAMD.get(i).getRecord_Type_Id()==2){
//				int k =0;
//				for(int j=0;j<FinalAlertsList.size();j++){
//					k++;
//				
//			
//				
//				
////					System.out.println("FinalAlertsList.size():"+FinalAlertsList.size());
//					if(implListFromAMHAMD.get(i).getEventGeneratedTime().equalsIgnoreCase(FinalAlertsList.get(j).getEventGeneratedTime())){
////						//Sai Divya : 20250724 : Engine Off status missing in event map.sn
////						if ((implListFromAMHAMD.get(i).getParamName()
////								.equalsIgnoreCase(FinalAlertsList.get(j).getParamName()))
////								&& implListFromAMHAMD.get(i).getParameterValue()
////										.equalsIgnoreCase(FinalAlertsList.get(j).getParameterValue()) && 
////										implListFromAMHAMD.get(i).getEventGeneratedTime().equalsIgnoreCase(FinalAlertsList.get(j).getEventGeneratedTime())) {
////							System.out.print("Check1 :" + FinalAlertsList.get(j));
////							FinalAlertsList.remove(j);
////						}
////						//Sai Divya : 20250724 : Engine Off status missing in event map.en
//						implListFromAMHAMD.get(i).setParamName(FinalAlertsList.get(j).getParamName());
//						implListFromAMHAMD.get(i).setParameterValue(FinalAlertsList.get(j).getParameterValue());
//						implListFromAMHAMD.get(i).setAlertSeverity(FinalAlertsList.get(j).getAlertSeverity());
//						//implListFromAMHAMD.get(i).setDtcCode(FinalAlertsList.get(j).getDtcCode());
//						
//						System.out.println("removing   /n"+FinalAlertsList.get(j));
//						FinalAlertsList.remove(j);
//					}
//					//DF20210524 Avinash Xavier A Events Map :Delete the alert that is not in asset event from the list instead of showing as engine on
//					else {
//						if (k==FinalAlertsList.size() && !implListFromAMHAMD.get(i).getEventGeneratedTime().equalsIgnoreCase(FinalAlertsList.get(j).getEventGeneratedTime()) 
//								&& implListFromAMHAMD.get(i).getIsengineOn().equalsIgnoreCase("0") && implListFromAMHAMD.get(i).getParamName().equalsIgnoreCase("ENGINE_ON")){
//							isRemoveAlert="1";
//						
//						}}
//					
//				}
//			
//			}
//			if(isRemoveAlert!=null && isRemoveAlert.equalsIgnoreCase("1")){
//				
//				implListFromAMHAMD.remove(i);
//				i--;
//				
//			}
//		}
		//LL-198 : Sai Divya : 20240731 : EventMapBugFix.eo
		//LL-198 : Sai Divya : 20240731 : EventMapBugFix.sn
		for (int i = 0; i < implListFromAMHAMD.size(); i++) {
			String isRemoveAlert = "0";
			if (implListFromAMHAMD.get(i).getRecord_Type_Id() == 2) {
				int k = 0;
				AssetEventLogImpl implAlert = implListFromAMHAMD.get(i);

				for (int j = 0; j < FinalAlertsList.size(); j++) {
					k++;
					AssetEventLogImpl finalAlert = FinalAlertsList.get(j);

					boolean isSameEvent = implAlert.getEventGeneratedTime()
							.equalsIgnoreCase(finalAlert.getEventGeneratedTime())
							&& implAlert.getParamName().equalsIgnoreCase(finalAlert.getParamName())
							&& implAlert.getParameterValue().equalsIgnoreCase(finalAlert.getParameterValue());

					if (isSameEvent) {
						// It's a duplicate, remove from FinalAlertsList
						FinalAlertsList.remove(j);
						break;
					} else {
						if (k == FinalAlertsList.size()
								&& !implListFromAMHAMD.get(i).getEventGeneratedTime()
										.equalsIgnoreCase(FinalAlertsList.get(j).getEventGeneratedTime())
								&& implListFromAMHAMD.get(i).getIsengineOn().equalsIgnoreCase("0")
								&& implListFromAMHAMD.get(i).getParamName().equalsIgnoreCase("ENGINE_ON")) {
							isRemoveAlert = "1";

						}
					}
				}
			}
			if (isRemoveAlert != null && isRemoveAlert.equalsIgnoreCase("1")) {

				implListFromAMHAMD.remove(i);
				i--;

			}
		}
		//LL-198 : Sai Divya : 20240731 : EventMapBugFix.en
	
		//iLogger.info("AssetEventLogService Final return list size:: "+implListFromAMHAMD.size());
//		System.out.println("FinalAlertsList"+FinalAlertsList);
//		for(int j=0;j<FinalAlertsList.size();j++){
//			//Df20170103 @Roopa Taking the application generated alerts from asset event table
//			AssetEventLogImpl impl=new AssetEventLogImpl();
//			
//			impl.setAlertSeverity(FinalAlertsList.get(j).getAlertSeverity());
//			impl.setEventGeneratedTime(FinalAlertsList.get(j).getEventGeneratedTime());
//			impl.setLatitude(FinalAlertsList.get(j).getLatitude());
//			impl.setLongitude(FinalAlertsList.get(j).getLongitude());
//			impl.setParamName(FinalAlertsList.get(j).getParamName());
//			impl.setParameterValue(FinalAlertsList.get(j).getParameterValue());
//			impl.setRecord_Type_Id(2);
//			
//			
//			
//			tempList.add(impl);
//		}
	//	System.out.println("tempList"+tempList);
		
		implListFromAMHAMD.addAll(FinalAlertsList);
		System.out.println("implListFromAMHAMD"+implListFromAMHAMD);
		return implListFromAMHAMD;
		
	}
	
/*	
	public List<AssetEventLogImpl> getAssetEventLog(String SerialNumber, String period, int loginTenancyId) 
	{
		List<AssetEventLogImpl> implList = new LinkedList<AssetEventLogImpl>();

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		DomainServiceImpl domainService = new DomainServiceImpl();

		try 
		{
			// Validate the time period - Required format: yyyy-MM-dd
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			Date inputDate = dateFormatter.parse(period);
			String DateString = dateFormatter.format(inputDate);

			// Validate the serialNumber
			AssetEntity asset = domainService.getAssetEntity(SerialNumber);
			if (asset == null || asset.getSerial_number() == null) {
				throw new CustomFault("Invalid SerialNumber");
			}

			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			List<Integer> alertTransactionNumList = new LinkedList<Integer>();
			List<String> parameterNames = new LinkedList<String>();
			List<String> parameterValues = new LinkedList<String>();

			// ------------------- get the required parameters from properties
			// file
			String longitude = null;
			String latitude = null;
			String healthEventTypeId = null;
			String securityEventTypeId = null;
			String internalBatteryLowEventId = null;
			String serviceEventTypeId = null;
			String usageOutsideOHEventId = null;
			String engineOn = null;
			String ignitionOn = null;
			String LandmarkEventTypeId = null;
			// Added by Rajani Nagaraju - 20130730 - To Handle Hello Packet -DefectID:1017
			String HelloPacketParamName = null;
			//DF20131105 - Rajani Nagaraju - To Display Ingnition ON in addition to Tow-away closure
			String TowAwayEventId=null;

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			longitude = prop.getProperty("Longitude");
			latitude = prop.getProperty("Latitude");
			healthEventTypeId = prop.getProperty("HealthEventTypeId");
			securityEventTypeId = prop.getProperty("SecurityEventTypeId");
			internalBatteryLowEventId = prop.getProperty("InternalBatteryLowEventId");
			serviceEventTypeId = prop.getProperty("ServiceEventTypeId");
			usageOutsideOHEventId = prop.getProperty("UsageOutsideOHEventId");
			engineOn = prop.getProperty("EngineON");
			ignitionOn = prop.getProperty("Ignition_ON");
			LandmarkEventTypeId = prop.getProperty("LandmarkEventTypeId");
			// Added by Rajani Nagaraju - 20130730 - To Handle Hello Packet - 1017
			HelloPacketParamName = prop.getProperty("HelloPacketParamName");
			//DF20131105 - Rajani Nagaraju - To Display Ingnition ON in addition to Tow-away closure
			TowAwayEventId=prop.getProperty("TowAwayEventId");

			// STEP1: Get the list of Alerts generated/closed on the given date
			List<Integer> assetEventIdList = new LinkedList<Integer>();

			// DefectID:1308 - Rajani Nagaraju - 20130918 - Not to display Application closed alerts
			// Get the list of events that got generated for the given date
			Query assetEventQuery = session.createQuery(" select a.eventId, a.eventSeverity, b.transactionNumber,"
							+ " b.transactionTime, CAST(GROUP_CONCAT(c.parameterValue) As string ) as paramValue,"
							+ " CAST(GROUP_CONCAT(d.parameterName) As string ) as paramId, "
							+ " a.eventGeneratedTime, a.eventClosedTime, a.eventTypeId, a.assetEventId "
							+ " from AssetEventEntity a, AssetMonitoringHeaderEntity b, AssetMonitoringDetailEntity c, "
							+ " MonitoringParameters d where a.serialNumber=b.serialNumber and "
							+ " a.eventGeneratedTime=b.transactionTime "
							+ " and b.transactionNumber=c.transactionNumber and c.parameterId=d.parameterId "
							+ " and b.transactionTime >= '"
							+ stringISTtoGMTConversion(DateString + " 00:00:00.0") 
							+ "' and b.transactionTime <= '"
							+ stringISTtoGMTConversion(DateString + " 23:59:59.0")
							+ "' and d.parameterName in ('"
							+ latitude
							+ "', '"
							+ longitude + "' ) " +
							" and a.serialNumber='" + SerialNumber + "'" +
							" group by b.transactionNumber, a.assetEventId ");

			Iterator itr = assetEventQuery.list().iterator();
			Object[] result = null;

			while (itr.hasNext()) 
			{
				result = (Object[]) itr.next();

				EventTypeEntity eventType = (EventTypeEntity) result[8];
				int assetEventId = (Integer) result[9];

				// Added by Rajani Nagaraju - DefectId: 685, 20130703 - To display event Log for LandmarkAlerts properly
				LandmarkLogDetailsEntity logDetails = null;
				if (String.valueOf(eventType.getEventTypeId()).equalsIgnoreCase(LandmarkEventTypeId)) 
				{
					Query landmarkLogQuery = session.createQuery(" from LandmarkLogDetailsEntity where assetEventId="+ assetEventId);
					Iterator landmarkLogItr = landmarkLogQuery.list().iterator();
					while (landmarkLogItr.hasNext()) 
					{
						logDetails = (LandmarkLogDetailsEntity) landmarkLogItr.next();

					}
				}

				if ((logDetails != null)&& (logDetails.getLandmarkId().getLandmark_Category_ID().getTenancy_ID().getTenancy_id() != loginTenancyId)) 
				{
					continue;
				}

				if (assetEventIdList.contains((Integer) result[9])) 
				{
					// continue;
					assetEventIdList.add((Integer) result[9]);

				}

				// assetEventIdList.add((Integer)result[9]);

				AssetEventLogImpl implObj = new AssetEventLogImpl();
				if (result[6] != null)
					implObj.setEventGeneratedTime(result[6].toString());

				EventEntity event = (EventEntity) result[0];
				if (event != null) 
				{
					implObj.setParamName(event.getEventName());
				}

				if (result[6] != null) 
				{
					if (result[3].toString().equals(result[6].toString())) 
					{
						implObj.setParameterValue("1");
					}
				}

				if (result[1] != null)
					implObj.setAlertSeverity(result[1].toString());

				implObj.setTransactionNumber((Integer) result[2]);

				if ((String.valueOf(eventType.getEventTypeId()).equalsIgnoreCase(healthEventTypeId))|| (String.valueOf(eventType.getEventTypeId()).equalsIgnoreCase(securityEventTypeId))) 
				{
					if (!(result[0].toString().equalsIgnoreCase(internalBatteryLowEventId)))
						alertTransactionNumList.add((Integer) result[2]);
				}

				if (result[5] != null)
					parameterNames = Arrays.asList(result[5].toString().split(","));

				if (result[4] != null)
					parameterValues = Arrays.asList(result[4].toString().split(","));

				implObj.setLatitude(parameterValues.get(parameterNames.indexOf(latitude)));

				implObj.setLongitude(parameterValues.get(parameterNames.indexOf(longitude)));

				implList.add(implObj);

			}

			// DefectID:1308 - Rajani Nagaraju - 20130918 - Not to display Application closed alerts
			// Get the List of events that got closed for the given date - For Service alert
			// DefectID:1308 - Rajani Nagaraju - 20130925 - Query Tweaking as it was taking much time
			Query assetClosedQuery = session.createQuery(" select a.eventId, a.eventSeverity, "
					+ " a.eventGeneratedTime, a.eventClosedTime, a.eventTypeId, a.assetEventId "
					+ " from AssetEventEntity a where "
					+ " a.eventClosedTime >=  '"
					+ stringISTtoGMTConversion(DateString + " 00:00:00.0") 
					+ "' and a.eventClosedTime <= '"
					+ stringISTtoGMTConversion(DateString + " 23:59:59.0")
					+ "'  AND "
					+ "( a.eventId != "
					+ usageOutsideOHEventId
					+ ") AND "
					+ "( a.eventTypeId != "
					+ LandmarkEventTypeId
					+ ") AND "
					+ "( a.eventClosedTime not in (select w.eventGeneratedTime from  AssetEventEntity w where w.serialNumber='"+SerialNumber+"'"
					+ " and w.eventTypeId=1 and w.eventGeneratedTime = a.eventClosedTime) )"
					+ " and a.eventTypeId = "
					+ serviceEventTypeId
					+ " and a.serialNumber='"
					+ SerialNumber + "'" +
					" group by a.eventClosedTime, a.eventId ");
			
			Iterator assetClosureItr = assetClosedQuery.list().iterator();
			Object[] result3 = null;
			while (assetClosureItr.hasNext()) 
			{
				result3 = (Object[]) assetClosureItr.next();

				EventTypeEntity eventType = (EventTypeEntity) result3[4];
				int assetEventId = (Integer) result3[5];

				if (assetEventIdList.contains((Integer) result3[5])) 
				{
					// continue;
					assetEventIdList.add((Integer) result3[5]);

				}

				// assetEventIdList.add((Integer)result[9]);

				AssetEventLogImpl implObj = new AssetEventLogImpl();
				if (result3[3] != null)
					implObj.setEventGeneratedTime(result3[3].toString());

				EventEntity event = (EventEntity) result3[0];
				if (event != null) 
				{
					implObj.setParamName(event.getEventName());
				}

				implObj.setParameterValue("0");

				if (result3[1] != null)
					implObj.setAlertSeverity(result3[1].toString());

				implObj.setTransactionNumber(0);
				implObj.setLatitude(null);
				implObj.setLongitude(null);

				implList.add(implObj);
			}

			// DefectID:1308 - Rajani Nagaraju - 20130921 - Not to display  Application closed alerts
			// Get the List of events that got closed for the given date - For the alerts other than Service alert
			// DefectID:1308 - Rajani Nagaraju - 20130925 - Query tweaking
			//DF20131105 - Rajani Nagaraju - Not to display Tow- away Closure since it is closed by the application on IgnitionON
			Query assetClosedQuery1 = session.createQuery(" select a.eventId, a.eventSeverity, b.transactionNumber,"
							+ " b.transactionTime, CAST(GROUP_CONCAT(c.parameterValue) As string ) as paramValue,"
							+ " CAST(GROUP_CONCAT(d.parameterName) As string ) as paramId, "
							+ " a.eventGeneratedTime, a.eventClosedTime, a.eventTypeId, a.assetEventId "
							+ " from AssetEventEntity a, AssetMonitoringHeaderEntity b, AssetMonitoringDetailEntity c, "
							+ " MonitoringParameters d, EventEntity e where a.serialNumber=b.serialNumber and "
							+ " a.eventId = e.eventId and "
							+ " ( b.transactionTime = a.eventClosedTime ) AND a.eventClosedTime >= '"							
							+ stringISTtoGMTConversion(DateString + " 00:00:00.0") 
							+ "' and a.eventClosedTime <= '"
							+ stringISTtoGMTConversion(DateString + " 23:59:59.0")
							+ "' AND "
							+ "( a.eventId NOT IN ("
							+ usageOutsideOHEventId +","+ TowAwayEventId
							+ ") ) AND "
							+ "( a.eventTypeId != "
							+ LandmarkEventTypeId
							+ ") AND "
							+ "( a.eventTypeId != "
							+ LandmarkEventTypeId
							+ ") AND "
							+ "( a.eventClosedTime not in (select x.eventGeneratedTime from  AssetEventEntity x, EventEntity y where x.serialNumber=b.serialNumber "
							+ " and x.eventId = y.eventId and y.eventName = e.eventName and x.eventGeneratedTime = a.eventClosedTime)) "
							+ " and b.transactionNumber=c.transactionNumber and c.parameterId=d.parameterId "
							+ " and b.transactionTime >= '"
							+ stringISTtoGMTConversion(DateString + " 00:00:00.0") 
							+ "' and b.transactionTime <= '"
							+ stringISTtoGMTConversion(DateString + " 23:59:59.0")
							+  "' and d.parameterName in ('"
							+ latitude
							+ "', '"
							+ longitude
							+ "' ) "
							+ " and a.eventTypeId != "
							+ serviceEventTypeId
							+ " and a.serialNumber='"
							+ SerialNumber + "'" +
							// " group by b.transactionNumber, a.assetEventId ");
							" group by a.eventClosedTime, a.eventId ");
			Iterator assetClosureItr1 = assetClosedQuery1.list().iterator();
			Object[] result4 = null;
			while (assetClosureItr1.hasNext()) 
			{
				result4 = (Object[]) assetClosureItr1.next();

				EventTypeEntity eventType = (EventTypeEntity) result4[8];
				int assetEventId = (Integer) result4[9];

				if (assetEventIdList.contains((Integer) result4[9])) 
				{
					// continue;
					assetEventIdList.add((Integer) result4[9]);

				}

				// assetEventIdList.add((Integer)result[9]);

				AssetEventLogImpl implObj = new AssetEventLogImpl();
				if (result4[6] != null)
					implObj.setEventGeneratedTime(result4[7].toString());

				EventEntity event = (EventEntity) result4[0];
				if (event != null) 
				{
					implObj.setParamName(event.getEventName());
				}

				if (result4[7] != null) 
				{
					implObj.setParameterValue("0");

				}

				if (result4[1] != null)
					implObj.setAlertSeverity(result4[1].toString());

				implObj.setTransactionNumber((Integer) result4[2]);

				if ((String.valueOf(eventType.getEventTypeId()).equalsIgnoreCase(healthEventTypeId))|| (String.valueOf(eventType.getEventTypeId()).equalsIgnoreCase(securityEventTypeId))) 
				{
					if(!(result4[0].toString().equalsIgnoreCase(internalBatteryLowEventId)))
						alertTransactionNumList.add((Integer) result4[2]);
				}

				if (result4[5] != null)
					parameterNames = Arrays.asList(result4[5].toString().split(","));

				if (result4[4] != null)
					parameterValues = Arrays.asList(result4[4].toString().split(","));

				implObj.setLatitude(parameterValues.get(parameterNames.indexOf(latitude)));

				implObj.setLongitude(parameterValues.get(parameterNames.indexOf(longitude)));

				implList.add(implObj);
			}

			String ignition_ON = null;
			String engine_ON = null;

			// STEP 2: Get the log/event packets received for the day and correspondingly fill the Machine Activity Log
			ListToStringConversion conversion = new ListToStringConversion();
			String alertTxnNumList = null;

			if (alertTransactionNumList != null)
				alertTxnNumList = conversion.getIntegerListString(alertTransactionNumList).toString();
			List<String> paramNameList = new LinkedList<String>();
			List<String> paramValueList = new LinkedList<String>();
			//int firstpacket = 0;

			Query query2 = null;

			if (!(alertTransactionNumList == null || alertTransactionNumList.isEmpty())) 
			{
				query2 = session.createQuery(" select a.transactionTime, CAST(GROUP_CONCAT(c.parameterName) As string ) as paramName, "
								+ " CAST(GROUP_CONCAT(b.parameterValue) As string ) as paramValue , a. transactionNumber, a.recordTypeId "
								+ " from AssetMonitoringHeaderEntity a, AssetMonitoringDetailEntity b, MonitoringParameters c "
								+ " where a.transactionNumber = b.transactionNumber and b.parameterId = c.parameterId and a.transactionNumber not in "
								+ " ("
								+ alertTxnNumList
								+ ") and c.parameterName in ('"
								+ latitude
								+ "', '"
								+ longitude
								+ "', '"
								+ engineOn
								+ "', '"
								+ ignitionOn
								+ "', '"
								+ HelloPacketParamName
								+ "' )"
								+ " and a.transactionTime >= '"
								+ stringISTtoGMTConversion(DateString + " 00:00:00.0") 
								+ "' and a.transactionTime <= '"
								+ stringISTtoGMTConversion(DateString + " 23:59:59.0")
								+ "' and a.serialNumber='"
								+ SerialNumber
								+ "' "
								+ " group by a.transactionNumber order by a.transactionTime");
			}

			else 
			{
				query2 = session.createQuery(" select a.transactionTime, CAST(GROUP_CONCAT(c.parameterName) As string ) as paramName, "
								+ " CAST(GROUP_CONCAT(b.parameterValue) As string ) as paramValue , a. transactionNumber, a.recordTypeId "
								+ " from AssetMonitoringHeaderEntity a, AssetMonitoringDetailEntity b, MonitoringParameters c "
								+ " where a.transactionNumber = b.transactionNumber and b.parameterId = c.parameterId and "
								+ " c.parameterName in ('"
								+ latitude
								+ "', '"
								+ longitude
								+ "', '"
								+ engineOn
								+ "', '"
								+ ignitionOn
								+ "', '"
								+ HelloPacketParamName
								+ "' )"
								+ " and a.transactionTime >= '"
								+ stringISTtoGMTConversion(DateString + " 00:00:00.0") 
								+ "' and a.transactionTime <= '"
								+ stringISTtoGMTConversion(DateString + " 23:59:59.0")
								+ "' and a.serialNumber='"
								+ SerialNumber
								+ "' "
								+ " group by a.transactionNumber order by a.transactionTime");
			}

			Iterator itr2 = query2.list().iterator();
			Object[] result2 = null;
			//DF20150320 - Rajani Nagaraju - Using Record type id to determine the log/event Packet. If it is event packet and not an hello packet, --
			// -- then it should be the Engine status change packet since only ENGINE_STATUS will be sent from FW from which app will infer the same as IGNITION STATUS-- 
			// -- and hence it should be Engine status packet
			while(itr2.hasNext())
			{
				result2 = (Object[]) itr2.next();
				
				if (result2[1] != null)
					paramNameList = Arrays.asList(result2[1].toString().split(","));
				
				if (result2[2] != null)
					paramValueList = Arrays.asList(result2[2].toString().split(","));
				
				AssetEventLogImpl implObj = new AssetEventLogImpl();
				implObj.setEventGeneratedTime(result2[0].toString());
				implObj.setLatitude(paramValueList.get(paramNameList.indexOf(latitude)));
				implObj.setLongitude(paramValueList.get(paramNameList.indexOf(longitude)));
				implObj.setTransactionNumber((Integer) result2[3]);
				implObj.setAlertSeverity("NA");
				
				//----------------- Set Parameter Name and Parameter Value
				//Check for the Record type
				RecordTypeEntity recordType=null;
				if(result2[4]!=null)
				{
					recordType = (RecordTypeEntity) result2[4];
				}
				
				if(recordType==null)
					continue;
				
				else
				{
					if(recordType.getMessageId().equalsIgnoreCase("010"))
					{
						implObj.setParamName("10 Minute Report");
						implObj.setParameterValue("1");
					}
					
					else if (recordType.getMessageId().equalsIgnoreCase("001"))
					{
						if( (paramNameList.contains(HelloPacketParamName)) && (paramValueList.get(paramNameList.indexOf(HelloPacketParamName)).equalsIgnoreCase("1")) ) 
						{
							implObj.setParamName("Hello");
							implObj.setParameterValue("1");
						}
						else
						{
							//DF09022015 @ Deepthi for param name and value changes
							
							if((paramNameList.contains(engineOn)) && (paramValueList.get(paramNameList.indexOf(engineOn)).equalsIgnoreCase("1")) )
							{
								if((paramNameList.contains(ignitionOn)) && (paramValueList.get(paramNameList.indexOf(ignitionOn)).equalsIgnoreCase("1")) )
								{
									implObj.setParamName(engineOn);
									implObj.setParameterValue("1");
								}
								else if((paramNameList.contains(ignitionOn)) && (paramValueList.get(paramNameList.indexOf(ignitionOn)).equalsIgnoreCase("0")) ){
									implObj.setParamName(engineOn);
									implObj.setParameterValue("1");
								}
								
							}
							else if((paramNameList.contains(engineOn)) && (paramValueList.get(paramNameList.indexOf(engineOn)).equalsIgnoreCase("0")) )
							{
								
								//DF20160419 @ Roopa for egnition on changes
								ENG ON	IGN ON ==>	Engine ON - Green
								ENF OFF	IGN ON ==>  Ignition ON – Green
								ENG ON	IGN OFF ==> Engine ON - Green
								ENG OFF	IGN OFF	==> Engine OFF - Grey

								
								if((paramNameList.contains(ignitionOn)) && (paramValueList.get(paramNameList.indexOf(ignitionOn)).equalsIgnoreCase("1")) )
								{
									implObj.setParamName(ignitionOn);
									implObj.setParameterValue("1");
								}
								
								else if((paramNameList.contains(ignitionOn)) && (paramValueList.get(paramNameList.indexOf(ignitionOn)).equalsIgnoreCase("0")) ){
									implObj.setParamName(engineOn); 
									implObj.setParameterValue("0");
								}
								
									
								
								
							}
							
						}
					}
				}
				
				implList.add(implObj);
			}
			
			while (itr2.hasNext()) 
			{
				int logPacket = 1;

				result2 = (Object[]) itr2.next();
				if (firstpacket == 0)
				{
					Timestamp time = (Timestamp) result2[0];
					String txnTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(time);
					int txnNum = (Integer) result2[3];
					//DefectId:1398 - Rajani Nagaraju - 20131003 - While taking previous txn for Engine_ON/Ignition_ON comparision, take max txn_time < current txn time which is under supervision and not, !=
					Query query1 = session.createQuery(" select c.parameterName, a.parameterValue,  a.transactionNumber  from AssetMonitoringDetailEntity a, "
									+ " AssetMonitoringHeaderEntity b, MonitoringParameters c "
									+ " where a.transactionNumber=b.transactionNumber "
									+ " and a.parameterId=c.parameterId and c.parameterName in ('"
									+ engineOn
									+ "' , '"
									+ ignitionOn
									+ "') "
									+ " and b.transactionTime = ( select max(d.transactionTime) from "
									+ " AssetMonitoringHeaderEntity d where d.serialNumber='"
									+ SerialNumber
									+ "' and d.transactionTime < '"
									+ txnTime
									+ "') and b.serialNumber='"+SerialNumber+"'");
		
					
					Iterator itr1 = query1.list().iterator();
					Object[] result1 = null;

					while (itr1.hasNext()) 
					{
						result1 = (Object[]) itr1.next();
						if (result1[0].toString().equalsIgnoreCase(ignitionOn)) 
						{
							ignition_ON = result1[1].toString();
						}

						if (result1[0].toString().equalsIgnoreCase(engineOn)) 
						{
							engine_ON = result1[1].toString();
						}
					}
				}

				firstpacket = 1;

				// AssetEventLogImpl implObj = new AssetEventLogImpl();
				// implObj.setEventGeneratedTime(result2[0].toString());
				if (result2[1] != null)
					paramNameList = Arrays.asList(result2[1].toString().split(","));

				if (result2[2] != null)
					paramValueList = Arrays.asList(result2[2].toString().split(","));

				//String engineOnValue = null;
				//String ignitionOnValue = null;
				// String majorReportValue = null;

				// check for the first received packet
				// DefectID: 984 - Rajani Nagaraju - AssetEventLog fix to handle
				// update of packet information into the same transaction in
				// Application
				// DefectId: 1263 - 20130916 - Rajani Nagaraju - 10 min report
				// is displayed for Engine_ON/Ignition_ON
				if ((ignition_ON == null && engine_ON == null)&& ((paramValueList.get(paramNameList.indexOf(engineOn)).equalsIgnoreCase("1")) 
						|| (paramValueList.get(paramNameList.indexOf(ignitionOn)).equalsIgnoreCase("1")))) 
				{

					logPacket = 0;
					if (paramValueList.get(paramNameList.indexOf(engineOn)).equalsIgnoreCase("1")) 
					{
						// implObj.setParamName(engineOn);
						// implObj.setParameterValue(paramValueList.get(paramNameList.indexOf(engineOn)));
						// engineOnValue =
						// paramValueList.get(paramNameList.indexOf(engineOn));
						// engine_ON =
						// paramValueList.get(paramNameList.indexOf(engineOn));
						engine_ON = "0";
					} 
					else 
					{
						engine_ON = "0";
					}
					if (paramValueList.get(paramNameList.indexOf(ignitionOn)).equalsIgnoreCase("1")) 
					{
						// implObj.setParamName(ignitionOn);
						// implObj.setParameterValue(paramValueList.get(paramNameList.indexOf(ignitionOn)));
						// ignitionOnValue =
						// paramValueList.get(paramNameList.indexOf(ignitionOn));
						// ignition_ON =
						// paramValueList.get(paramNameList.indexOf(ignitionOn));
						ignition_ON = "0";
					} 
					else 
					{
						ignition_ON = "0";

					}
				}


				// Check for change EngineOn event
				// DefectId: 1263 - 20130916 - Rajani Nagaraju - 10 min report is displayed for Engine_ON/Ignition_ON
				if ((!(paramValueList.get(paramNameList.indexOf(engineOn)).equalsIgnoreCase(engine_ON))) && engine_ON != null) 
				{
					logPacket = 0;
					AssetEventLogImpl implObj = new AssetEventLogImpl();
					implObj.setEventGeneratedTime(result2[0].toString());
					implObj.setParamName(engineOn);
					implObj.setParameterValue(paramValueList.get(paramNameList.indexOf(engineOn)));

					engine_ON = paramValueList.get(paramNameList.indexOf(engineOn));

					implObj.setLatitude(paramValueList.get(paramNameList.indexOf(latitude)));
					implObj.setLongitude(paramValueList.get(paramNameList.indexOf(longitude)));

					implObj.setTransactionNumber((Integer) result2[3]);
					//DefectId:20140812 @ mobile app change /Added NA for 10 min packet
					implObj.setAlertSeverity("NA");

					//End DefectId:20140812
					implList.add(implObj);
				}

				// Check for IgnitionOn event

				// DefectId: 1263 - 20130916 - Rajani Nagaraju - 10 min report is displayed for Engine_ON/Ignition_ON
				if ((!(paramValueList.get(paramNameList.indexOf(ignitionOn)).equalsIgnoreCase(ignition_ON))) && ignition_ON != null) 
				{
					AssetEventLogImpl implObj = new AssetEventLogImpl();
					implObj.setEventGeneratedTime(result2[0].toString());

					logPacket = 0;
					implObj.setParamName(ignitionOn);
					implObj.setParameterValue(paramValueList.get(paramNameList.indexOf(ignitionOn)));

					ignition_ON = paramValueList.get(paramNameList.indexOf(ignitionOn));

					implObj.setLatitude(paramValueList.get(paramNameList.indexOf(latitude)));
					implObj.setLongitude(paramValueList.get(paramNameList.indexOf(longitude)));

					implObj.setTransactionNumber((Integer) result2[3]);
					//DefectId:20140812 @ mobile app change /Added NA for 10 min packet
					implObj.setAlertSeverity("NA");
					//End DefectId:20140812

					implList.add(implObj);
				}

				// Major Timed Report
				if (logPacket == 1) 
				{
					AssetEventLogImpl implObj = new AssetEventLogImpl();
					implObj.setEventGeneratedTime(result2[0].toString());

					// Defect Id: 1017 - Rajani Nagaraju - 20130730 - To handle Hello Packets
					if (paramValueList.get(paramNameList.indexOf(HelloPacketParamName)).equalsIgnoreCase("1")) 
					{
						implObj.setParamName("Hello");
					}

					else 
					{
						implObj.setParamName("10 Minute Report");
					}
					
					implObj.setParameterValue("1");
					implObj.setLatitude(paramValueList.get(paramNameList.indexOf(latitude)));
					implObj.setLongitude(paramValueList.get(paramNameList.indexOf(longitude)));

					implObj.setTransactionNumber((Integer) result2[3]);
					//DefectId:20140812 @ mobile app change /Added NA for 10 min packet
					implObj.setAlertSeverity("NA");
					//End DefectId:20140812

					implList.add(implObj);
				}

				
				 * implObj.setLatitude(paramValueList.get(paramNameList.indexOf(
				 * latitude)));
				 * implObj.setLongitude(paramValueList.get(paramNameList
				 * .indexOf(longitude)));
				 * 
				 * implObj.setTransactionNumber((Integer)result2[3]);
				 * 
				 * implList.add(implObj);
				 
			}
			
			
			

		}

		catch (CustomFault e) 
		{
			bLogger.error("Custom Fault: " + e.getFaultInfo());
		}

		catch (Exception e) 
		{
			fLogger.fatal("Exception :" + e.getMessage());

			//e.printStackTrace();
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

		return implList;
	}*/

	// ******************************************************END OF Asset Event Log Details ********************************************

	
	// *************************************************** FROM FIRMWARE
	// *******************************************************************

	// *************************************************** Get Machine Health
	// Details *************************************************************

	/**
	 * This method returns the current details of various health parameters for
	 * a given VIN
	 * 
	 * @param loginId
	 *            userloginId as String input
	 * @param serialNumber
	 *            VIN as String input
	 * @return
	 */
	
	//DF20161021 @Roopa Machine health detail changes Fetching from new AMD dynamic Table
	public List<MachineHealthDetailsImpl> getMachineHealthDetails(
			String loginId, String serialNumber) {
		List<MachineHealthDetailsImpl> implRespList = new LinkedList<MachineHealthDetailsImpl>();
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	
       
		//String amdTable=null;
	//	String txnTimestamp=null;
		
		//int latestEvtTransactionNumber=0;
    	
    	//DF20170130 @Roopa for Role based alert implementation
		DateUtil utilObj=new DateUtil();
		
		List<String> alertCodeList= utilObj.roleAlertMapDetailsForHealthTab(loginId,0, "Display");
		
		ListToStringConversion conversion = new ListToStringConversion();

		StringBuilder alertCodeListAsString=conversion.getStringList(alertCodeList);
		
		  MonitoringParameters monitorParam=null;
		  
			HashMap<String,String> transactionDataMap = new HashMap<String, String>();
      	
      	HashMap<String,String> MastermonitorParamNameMap = new HashMap<String, String>();
		
      //DF20181129 - KO369761 - Commented because fetching data from asset_event instead of ams.
		/*String txnKey = "AssetMonitoringDetailsBO:getMachineHealthDetails ";
		DynamicAMS_Doc_DAL dalObj= new DynamicAMS_Doc_DAL();
		
		List<AMSDoc_DAO> amsDAOList = dalObj.getAMSData(txnKey, serialNumber);
		
		if(amsDAOList!=null && amsDAOList.size()>0){
		
		
			// txnTimestamp= amsDAOList.get(0).getLatest_Transaction_Timestamp();
			// latestEvtTransactionNumber= amsDAOList.get(0).getTransaction_Number();
			
			if(amsDAOList.get(0).getEvents()!=null)
			 transactionDataMap=amsDAOList.get(0).getEvents();
			else
			return implRespList;
		
		}
		else{
			return implRespList;
		}*/
      	
      	/**
		 * DF20181129 - KO369761
		 * Fetching events data from asset_event instead of asset monitoring snapshot.
		 * because of that above code block commented.
		 */
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			
			//DF20190114-KO369761-Query channged to fix discrepancy between alerts and health tabs.
			String eventQ = "select event_id from asset_event where serial_number='"+serialNumber+"' and active_status = 1";
				
			rs = statement.executeQuery(eventQ);
			iLogger.info("Event Query for health details ::"+eventQ);

			while(rs.next()){
				transactionDataMap.put(String.valueOf(rs.getInt("event_id")), "1");
			}
			
			if(transactionDataMap.size() == 0)
				return implRespList;
			
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Serial_Number::"+serialNumber+":::Exception Caught :"+e.getMessage());
		}
		finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		/*	SimpleDateFormat dateTimeFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date txnDate = null;
		try {
			txnDate = dateTimeFormat.parse(txnTimestamp);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			fLogger.fatal("AssetMonitoringDetailsBO:getMachineHealthDetails ::Exception::"+e.getMessage());
		}

		HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(txnDate);
		
		if(dynamicTables!=null && dynamicTables.size()>0){

		amdTable = dynamicTables.get("AMD");
		
		}*/
		
		//******************************* STEP3: Get the AssetEntityObj for the given VIN
		//AssetEntity asset=null;
		Session session = HibernateUtil.getSessionFactory().openSession();

		try{
			
		/*	Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
			Iterator assetItr = assetQ.list().iterator();
			if(assetItr.hasNext())
			{
				asset = (AssetEntity)assetItr.next();
			}*/
			
			//DF20170130 @Roopa for Role based alert implementation, added paramId column in business_event table to match alert code and get the parameter key list from the monitoring prameters table. 
			
			//Query monQ = session.createQuery(" from MonitoringParameters where RecordType='Event' ");
			/**
			 * DF20181129 - KO369761
			 * Fetching events data from asset_event instead of asset monitoring snapshot.
			 * events comparison based on event id instead of monitoring_parameter_key
			 * query changed for that
			 */
			/*Query monQ = session.createQuery("SELECT a from MonitoringParameters a, EventEntity b where a.parameterId=b.parameterID and b.eventCode in ("+alertCodeListAsString+") and a.RecordType='Event' ");
			Iterator monItr = monQ.list().iterator();
			while(monItr.hasNext())
			{
				monitorParam = (MonitoringParameters)monItr.next();
				MastermonitorParamNameMap.put(monitorParam.getParameterKey(), monitorParam.getParameterName());
			}*/
			
			Query monQ = session.createQuery("SELECT b.eventId, a.parameterId, a.parameterName from MonitoringParameters a, EventEntity b where a.parameterId=b.parameterID and b.eventCode in ("+alertCodeListAsString+") and a.RecordType='Event' ");
			Iterator monItr = monQ.list().iterator();
			Object[] resultSet = null;
			while(monItr.hasNext())
			{
				resultSet = (Object[]) monItr.next();
				MastermonitorParamNameMap.put(String.valueOf(resultSet[0]),String.valueOf(resultSet[2]));
			}
			
		}
		catch(Exception e)
		{
			fLogger.fatal("AssetMonitoringDetailsBO:getMachineHealthDetails::Exception::"+e.getMessage());
		}
		finally{
			if(session!=null && session.isOpen())  
			{
				session.close();
			}	
			
		}
		
		/*int seg_ID=asset.getSegmentId();
		
		String machinehealthQuery="select GROUP_CONCAT(Parameter_ID) as Parameter_ID, GROUP_CONCAT(Parameter_Value) as Parameter_Value "
				+"from "+amdTable+" where Segment_ID_TxnDate="+seg_ID+" and Transaction_Number="+latestEvtTransactionNumber+" ";
		
		iLogger.info("machinehealthQuery ::"+machinehealthQuery);
		//System.out.println("machinehealthQuery ::"+machinehealthQuery);
		
		DynamicAMD_DAL amdObj=new DynamicAMD_DAL();
		
		 transactionDataMap=amdObj.getMachineHealthData(machinehealthQuery);*/
		String eventvalue=null;
		 
		 for(Entry<String, String> entry:MastermonitorParamNameMap.entrySet()){
			 
			 MachineHealthDetailsImpl implObj =new MachineHealthDetailsImpl();
			 
			// implObj.setParameterId(entry.getKey());
			 implObj.setParameterName(entry.getValue());
			 eventvalue=transactionDataMap.get(String.valueOf(entry.getKey()));
			 
			 if(eventvalue!=null)
				 implObj.setParameterValue(eventvalue);
			 else
				 implObj.setParameterValue("0");
			 
			 implObj.setRecordType("Event");
			 implRespList.add(implObj);
 		}
		
		
		return implRespList;
	}
/*	public List<MachineHealthDetailsImpl> getMachineHealthDetails(
			String loginId, String serialNumber) {

		List<MachineHealthDetailsImpl> implRespList = new LinkedList<MachineHealthDetailsImpl>();

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try {
			String parameterName = null;
			String eventParameters = null;

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream(
					"remote/wise/resource/properties/configuration.properties"));
			parameterName = prop.getProperty("EngineON");
			eventParameters = prop.getProperty("EventParameters");

			// get the transaction number of the latest time when the Engine
			// status has changed
			int transactionNumber = 0;

			Query query = session
					.createQuery(" select b.transactionNumber from AssetMonitoringDetailEntity a JOIN a.transactionNumber b"
							+ " JOIN a.parameterId c  where b.serialNumber ='"
							+ serialNumber
							+ "' "
							+ "and c.parameterId = (select max(parameterId) from MonitoringParameters where parameterName = '"
							+ parameterName
							+ "' ) order by b.transactionNumber desc");
			query.setMaxResults(1);
			Iterator itr = query.list().iterator();

			while (itr.hasNext()) {
				transactionNumber = (Integer) itr.next();
			}

			if (transactionNumber == 0) {
				return implRespList;
			}

			Query query1 = session
					.createQuery(" select c.parameterId, c.parameterName, a.parameterValue, c.RecordType from AssetMonitoringDetailEntity a "
							+ " JOIN a.transactionNumber b JOIN a.parameterId c  where b.serialNumber ='"
							+ serialNumber
							+ "' "
							+ " and b.transactionNumber >= "
							+ transactionNumber
							+ "  and c.parameterTypeId = (select max(parameterTypeId) from "
							+ " ParameterTypeEntity where parameterTypeName = '"
							+ eventParameters
							+ "') order by b.transactionNumber desc");

			Iterator itr1 = query1.list().iterator();
			Object[] result1 = null;
			List<Integer> paramIdList = new LinkedList<Integer>();

			while (itr1.hasNext()) {
				result1 = (Object[]) itr1.next();
				int parameterid = (Integer) result1[0];
				if (paramIdList.contains(parameterid)) {

				}

				else {
					MachineHealthDetailsImpl implResp = new MachineHealthDetailsImpl();
					implResp.setParameterId(parameterid);
					implResp.setParameterName(result1[1].toString());
					implResp.setParameterValue(result1[2].toString());
					implResp.setRecordType(result1[3].toString());

					implRespList.add(implResp);

					paramIdList.add(parameterid);
				}
			}
		}

		catch (Exception e) {
			fLogger.fatal("Exception :" + e.getMessage());
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

		return implRespList;
	}*/

	// ***************************************************END of Get Machine
	// Health Details
	// *************************************************************
	// ********************************************** Set Vin Registration
	// ******************************************************************
	
	//DF20141107 - Rajani Nagaraju - START - Accepting registration data as "|" seperated String instead of XML
	//public String setVinRegistration(XmlParser parser)
	public String setVinRegistration(String serialNumber, String registrationTime, String imei, String sim) throws ParseException
	//DF20141107 - Rajani Nagaraju - END - Accepting registration data as "|" seperated String instead of XML
	{
		String status = "FAILURE";

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;
		
		//int existingVersion = 0;
		//boolean incompatibleVersion = true;

		// get the asset monitoring header details
		serialNumber = serialNumber.substring(3);
		Timestamp transactionTime= new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(registrationTime).getTime());
		
		// get the asset monitoring data
		//HashMap<String, HashMap<String, String>> hashMap = parser.getParamType_parametersMap();

		// ---------------------------check whether this is the XML received for
		// Authentication
	/*	String authenticationMsg = null;
		String imeiNumber = null;
		String simNumber = null;*/

		try {
			
			//infoLogger.info("Inside AssetMonitoringDetailsBO: setVinRegistration()");
		/*	Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream(
					"remote/wise/resource/properties/configuration.properties"));
			authenticationMsg = prop.getProperty("AuthenticationParameters");
			imeiNumber = prop.getProperty("LLECU_IMEI_Number");
			simNumber = prop.getProperty("LLECU_SIM_Number");

			if (hashMap.containsKey(authenticationMsg)) {
				HashMap<String, String> paramValues = (HashMap<String, String>) hashMap
						.get(authenticationMsg);
				String imei = null, sim = null;

				for (int y = 0; y < paramValues.size(); y++) {
					String param = (String) paramValues.keySet().toArray()[y];
					String values = (String) paramValues.values().toArray()[y];

					if (param.equalsIgnoreCase(imeiNumber))
						imei = values;
					else if (param.equalsIgnoreCase(simNumber))
						sim = values;
				}*/

				// call the implementation class of AssetProvisioning service
				// passing serialNumber, imei and sim
				AssetControlUnitRespContract reqObj = new AssetControlUnitRespContract();
				reqObj.setIMEI(imei);
				reqObj.setSerialNumber(serialNumber);
				reqObj.setSIM_NO(sim);
				reqObj.setRegistrationDate(transactionTime);
				
				//infoLogger.info("Inside AssetMonitoringDetailsBO: setVinRegistration() - Call setAssetProvisioningDetails");
				status = new AssetProvisioningImpl().setAssetProvisioningDetails(reqObj);

			
		}

				
		catch (Exception e) {
			status = "FAILURE";
			fLogger.fatal("Exception :" + e.getMessage());
		}

		return status;

	}
	// **********************************************END of Set Vin Registration ******************************************************************
	
	
	
	//************************************************** Set Remote Monitoring Data into database *************************************
	public String setRemoteMonitoringDataNew(XmlParser parser, String processingFileName, String serialNumber, String transactionTime, 
							int prevPktTxnNumber, String latitude, String longitude ) 
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger rLogger = RejectedPktLoggerClass.logger;
    	
		String status="SUCCESS";
		
		iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"ADS Processing Logic - START");
		
		iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Read from property file - START");
		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal("ADS:"+serialNumber+":"+transactionTime+":"+" -Error in intializing property File :"+e);
			iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"ADS Processing Logic - END - Error in reading property file");
			return "FAILURE";
		}
		
		//------------------- File Movement - get the required folder paths from properties file - START
		String deploymentEnv = prop.getProperty("deployenvironment");
		String DataPackets_UnprocessedFolderPath = prop.getProperty("DataPackets_UnprocessedFolderPath_"+deploymentEnv);

		//DF20150319 - Rajani Nagaraju - VIN based file storage in AEMP output folder
		//String DataPackets_ProcessedFolderPath = prop.getProperty("DataPackets_ProcessedFolderPath_"+deploymentEnv);
		String DataPackets_ProcessedFolderPath = prop.getProperty("DataPackets_ProcessedFolderPath_"+deploymentEnv)+"/"+serialNumber;
		
		String DataPackets_InProcessFolderPath = prop.getProperty("DataPackets_InProcessFolderPath_"+deploymentEnv);
		//------------------- File Movement - get the required folder paths from properties file - END
		iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Read from property file - END");
		
		File processingFile = new File(DataPackets_InProcessFolderPath,processingFileName);
		File archivedFile = null;
		
		try
		{
			//File processingFile = new File(DataPackets_InProcessFolderPath,processingFileName);
			//File archivedFile = null;
			
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			//DF20150812 - Rajani Nagaraju -  Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists). This will prevent from getting session that are stale
	        if(session.getTransaction().isActive() && session.isDirty())
	        {
	           	iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Opening a new session");
	           	session = HibernateUtil.getSessionFactory().openSession();
	        }
			session.beginTransaction();

			try
			{
				Timestamp currentTime = new Timestamp(new Date().getTime());
				HashMap<String, HashMap<String, String>> hashMap = parser.getParamType_parametersMap();
				HashMap<String, String> eventParamValues = (HashMap<String, String>) hashMap.get("EventParameters");
				
				//************************ STEP0:NULL Check for Latitude and Longitude 
				if(latitude==null || longitude==null)
				{
					HashMap<String, String> locparamValuesMap = (HashMap<String, String>) hashMap.get("Location");
					
					String currentLat=locparamValuesMap.get("Latitude");
					double latValueInDouble = Double.valueOf(currentLat.replace('N', ' ').replace('E', ' ').trim());
					Double convertedLatValueInDouble = (((latValueInDouble/100)%1)*2+(latValueInDouble/100)*3)/3;
					latitude = convertedLatValueInDouble.toString();
					
					String currentLong=locparamValuesMap.get("Longitude");
					double longValueInDouble = Double.valueOf(currentLong.replace('N', ' ').replace('E', ' ').trim());
					Double convertedLongValueInDouble = (((longValueInDouble/100)%1)*2+(longValueInDouble/100)*3)/3;
					longitude = convertedLongValueInDouble.toString();
				}
				
				
				
				//****************************** STEP1: Close the Tow-away alert with next Ignition on packet - Same should be reflected in AMD for Tow-away parameter
				if( (eventParamValues!=null) && (eventParamValues.containsKey("IGNITION_ON")) && (eventParamValues.get("IGNITION_ON").equalsIgnoreCase("1")) )
				{
					//Create an entry for Closure of Tow-away
					eventParamValues.put("TO_AWAY", "0");
					hashMap.put("EventParameters", eventParamValues);
				}
				
				//****************************** STEP2: Get the Record Type of the received packet
				RecordTypeEntity receivedRecordType = null;
				Query recordTypeQ = session.createQuery(" from RecordTypeEntity where messageId='"+parser.getMessageId()+"'");
				Iterator recordTypeItr = recordTypeQ.list().iterator();
				if(recordTypeItr.hasNext())
				{
					receivedRecordType = (RecordTypeEntity)recordTypeItr.next();
				}
				
				
				//******************************* STEP3: Get the AssetEntityObj for the given VIN
				AssetEntity asset=null;
				Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
				Iterator assetItr = assetQ.list().iterator();
				if(assetItr.hasNext())
				{
					asset = (AssetEntity)assetItr.next();
				}
				//DF20150330 - Rajani Nagaraju - Check for invalid VIN in XML
				if(asset==null)
				{
					iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Invalid VIN");
					return "REJECTED";
				}
				
				
				//******************************* STEP4: If the packet already exists with same composite key combination (VIN+TT), then update the details --
				// -- Such packets are still valid for ex. IgnitionON, EngineON and the following log packet
				iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Insert/Update AMH - START");
				AssetMonitoringHeaderEntity amhObj = null;
				//Df20150330 - Rajani nagaraju - Adding SegmentId in where clause will pick the VIN from the specific partition and hence improves performance
				Query chkPktUpdateQ = session.createQuery(" from AssetMonitoringHeaderEntity where serialNumber='"+serialNumber+"' and " +
															"transactionTime='"+transactionTime+"' and segmentId="+asset.getSegmentId());
				Iterator chkPktUpdateItr = chkPktUpdateQ.list().iterator();
				if(chkPktUpdateItr.hasNext())
				{
					amhObj = (AssetMonitoringHeaderEntity)chkPktUpdateItr.next();
					
					//If Log and Event packet are received with same transaction timestamp, record type of the transaction would be considered as event
					if(receivedRecordType.getRecordTypeName().equalsIgnoreCase("Event Packet"))
						amhObj.setRecordTypeId(receivedRecordType);
					amhObj.setLastUpdatedTime(currentTime);
					amhObj.setUpdateCount(amhObj.getUpdateCount()+1);
					session.update(amhObj);
				}
				
				
				//******************************* STEP5: Insert the new record into AMH
				AssetMonitoringHeaderEntity newAmhObj = null;
				if(amhObj==null)
				{
					newAmhObj = new AssetMonitoringHeaderEntity();
					newAmhObj.setSerialNumber(asset);
					newAmhObj.setTransactionTime(parser.getTransactionTime());
					newAmhObj.setCreatedTimestamp(currentTime);
					newAmhObj.setRecordTypeId(receivedRecordType);
					newAmhObj.setFwVersionNumber(parser.getFwVersionNumber());
					newAmhObj.setLastUpdatedTime(currentTime);
					newAmhObj.setUpdateCount(0);
					//DF20150330 - Rajani Nagaraju - To Facilitate DB Partitioning
					newAmhObj.setSegmentId(asset.getSegmentId());
					session.save(newAmhObj);
				}
				iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Insert/Update AMH - END");
				
				
				//******************************* STEP6: Get the ParameterIds corresponding to Parameter names in hashmap
				HashMap<String, String> parameterNameValueMap = new HashMap<String, String>();
				List<String> parameterNames = new LinkedList<String>();
				for (int y = 0; y < hashMap.size(); y++) 
				{
					HashMap<String, String> nameValuesMap = (HashMap<String, String>) hashMap.values().toArray()[y];
					for (int z = 0; z < nameValuesMap.size(); z++) 
					{
						String name = (String) nameValuesMap.keySet().toArray()[z];
						String value = (String) nameValuesMap.values().toArray()[z];
						parameterNameValueMap.put(name, value);
						parameterNames.add(name);
					}
				}
				
				HashMap<String, MonitoringParameters> paramNameObjMap = new HashMap<String, MonitoringParameters>();
				Query parametersQ = session.createQuery("from MonitoringParameters where parameterName in (:list)").setParameterList("list", parameterNames);
				Iterator parametersItr = parametersQ.list().iterator();
				while (parametersItr.hasNext()) 
				{
					MonitoringParameters parameters = (MonitoringParameters) parametersItr.next();
					paramNameObjMap.put(parameters.getParameterName(), parameters);
				}
				
				//Reject the packet if edge proxy sends wrong paremeters
				if(paramNameObjMap.size()!=parameterNames.size())
				{
					rLogger.warn("ADI:"+serialNumber+":"+transactionTime+":"+" :Packet received with invalid parameters");
					
					if (session.getTransaction().isActive()) 
					{
						session.getTransaction().rollback();
					}
					
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.beginTransaction();
					}
					
					
					RejectedPacketDetailsEntity rejectedPkt = new RejectedPacketDetailsEntity();
					rejectedPkt.setCreatedTime(currentTime);
					rejectedPkt.setSerialNumber(serialNumber);
					rejectedPkt.setTransactionTime(parser.getTransactionTime());
					rejectedPkt.setRejectionCause("Packet received with invalid parameter in XML");
					session.save(rejectedPkt);
					
					/* <!-- DF20160316 @Roopa for session flush changes -->*/
					
					session.flush();

					if(session.getTransaction().isActive())
					{
							session.getTransaction().commit();
					}
					
					
					
					//DefectID: 20140318 - Rajani Nagaraju - In order to avoid data loss on Deployment Errors - If the packet is rejected, move the data packet from Unprocessed_Xml to AEMP_Output folder
					archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
					archivedFile.mkdirs();
					if(archivedFile.exists())
						archivedFile.delete();
					boolean moveStatus = processingFile.renameTo(archivedFile);
					if(moveStatus)
					{
						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"Packet received with invalid parameter");
						iLogger.info("ADI:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
					}
	
					return "REJECTED";
				}
				
				
				//******************************** STEP7: For each parameter received, insert the record into AMD
				iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Insert Received parameters into AMD - START");
				List<Integer> receivedParamList= new LinkedList<Integer>();
				String currentFuelLevel=null;
				
				for (int r = 0; r < parameterNameValueMap.size(); r++) 
				{
					String parName = (String) parameterNameValueMap.keySet().toArray()[r];
					String parValues = (String) parameterNameValueMap.values().toArray()[r];

					MonitoringParameters monitoringParamEntity = paramNameObjMap.get(parName);
					String convertedValueString = null;
					double convertedValue = 0;

					//-------------------- STEP 7.1 Extract the actual value from paramValue with the defined generic unit 
					if (parValues.contains("unit")) 
					{
						String[] parValuesArray = parValues.split(" unit ");
						String[] splitparValue = null;
						String[] splitparUnit = null;

						if (parValuesArray[0].contains("-")) 
						{
							splitparValue = parValuesArray[0].split("-");
							splitparUnit = parValuesArray[1].split("-");
						} 
						
						else 
						{
							splitparValue = new String[] { parValuesArray[0] };
							splitparUnit = new String[] { parValuesArray[1] };
						}
						
						
						//To handle the packets with junk (or _) in Altitude or any other parameters
						String receivedUnit =null;
						int junkValue=0;
						String juckParamValue =""; 
						
						for (int y = 0; y < splitparValue.length; y++) 
						{
							try
							{
								double receivedValue = Double.parseDouble(splitparValue[y].trim());
							}
							catch(NumberFormatException e)
							{
								junkValue=1;
								juckParamValue=juckParamValue+splitparValue[y];
							}
						}
						
						if(junkValue==1)
						{
							convertedValueString = juckParamValue;
						}
						
						else
						{
							for (int y = 0; y < splitparValue.length; y++) 
							{
								double receivedValue = Double.parseDouble(splitparValue[y].trim());
								
								if(splitparUnit.length>y)
									receivedUnit = splitparUnit[y].trim();
								
								Query q6 = session.createQuery("from UomMasterEntity where receivedUnit='"+receivedUnit+"'");
								Iterator itr6 = q6.list().iterator();
								double conversionFactor = 0;
								while (itr6.hasNext()) 
								{
									UomMasterEntity uomMaster = (UomMasterEntity) itr6.next();
									conversionFactor = uomMaster.getConversionFactor();
								}
								
								convertedValue = convertedValue + (receivedValue * conversionFactor);
							}
							convertedValueString = Double.toString(convertedValue);
						}

					}
					else if (parName.equalsIgnoreCase("Latitude"))
							convertedValueString = latitude;
								
					else if (parName.equalsIgnoreCase("Longitude"))
							convertedValueString = longitude;
						
					else
							convertedValueString = parValues;
					
					if(monitoringParamEntity.getParameterId()==5)
						currentFuelLevel=convertedValueString;

					//-------------------- STEP 7.2  Insert the data into Asset monitoring detail entity
					if (amhObj == null) 
					{
						AssetMonitoringDetailEntity assetMonitoringDetailObj = new AssetMonitoringDetailEntity();
						assetMonitoringDetailObj.setTransactionNumber(newAmhObj);
						assetMonitoringDetailObj.setParameterId(monitoringParamEntity);
						assetMonitoringDetailObj.setParameterValue(convertedValueString);
						session.save(assetMonitoringDetailObj);
						
						receivedParamList.add(monitoringParamEntity.getParameterId());
					}

					//-------------------- STEP 7.2  Update the data into Asset monitoring detail entity if it is a duplicate txn
					
					//If multiple transactions are received with same snapshot time, update all the parameters received in current txn.
					else
					{
						receivedParamList.add(monitoringParamEntity.getParameterId());
						//Update Fuel Level Parameter only if it is not equal to 110. This is because, FW will send correct Fuel Level only in log and low fuel event
						//In all other packets it would be 110. For ex. If two packets are received a)Low fuel Event with fuel level 40 and b) Ignition on with fuel level 110
						//Dont update the transaction with fuel level as 110
						
						if( (!(monitoringParamEntity.getParameterName().equalsIgnoreCase("FuelLevel"))) ||
							( (monitoringParamEntity.getParameterName().equalsIgnoreCase("FuelLevel")) && (receivedRecordType.getMessageId().equalsIgnoreCase("010")) ) ||
							( (monitoringParamEntity.getParameterName().equalsIgnoreCase("FuelLevel")) && (eventParamValues!=null) && (eventParamValues.containsKey("LOW_FUEL")) )
						  ) 
						
						{
							
							Query updateTxnQ = session.createQuery(" update AssetMonitoringDetailEntity set parameterValue='"+convertedValueString+"' where "
									+ " transactionNumber="+ amhObj.getTransactionNumber()+ " and parameterId=" + monitoringParamEntity.getParameterId());
							int rowCount = updateTxnQ.executeUpdate();
							if (rowCount > 0)
								iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+ "Transaction " + amhObj.getTransactionNumber()+ " " +
										"updated sccessfully for the parameterId "+ monitoringParamEntity.getParameterId() + " with value " + convertedValueString);
						}	
						
					}
					

				}
				iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Insert Received parameters into AMD - END");
				
				
				//******************************** STEP8: If it is a new transaction packet, insert the remaining parameters from prev txn
				if(amhObj==null)
				{
					iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Insert remaining parameters from prev transaction into AMD - START");
					AssetMonitoringDetailEntity prevAmdObj =null;
					
					if(prevPktTxnNumber!=0)
					{
						//------------------- Step8.1 If this is not the first record received for the VIN
						Query prevTxnDetailsQ = session.createQuery(" from AssetMonitoringDetailEntity where transactionNumber="+prevPktTxnNumber);
						Iterator prevTxnDetailsItr = prevTxnDetailsQ.list().iterator();
						while(prevTxnDetailsItr.hasNext())
						{
							prevAmdObj = (AssetMonitoringDetailEntity)prevTxnDetailsItr.next();
							if( ! (receivedParamList.contains(prevAmdObj.getParameterId().getParameterId())) )
							{
								AssetMonitoringDetailEntity amdCarryForwardData = new AssetMonitoringDetailEntity();
								amdCarryForwardData.setTransactionNumber(newAmhObj);
								amdCarryForwardData.setParameterId(prevAmdObj.getParameterId());
								
								if(prevAmdObj.getParameterId().getParameterName().equalsIgnoreCase("HELLO"))
									amdCarryForwardData.setParameterValue("0");
								else if (  (receivedRecordType.getRecordTypeName().equalsIgnoreCase("Event Packet")) 
										&& (prevAmdObj.getParameterId().getParameterName().contains("EngineRunningBand")) )
									amdCarryForwardData.setParameterValue("0");
								else
									amdCarryForwardData.setParameterValue(prevAmdObj.getParameterValue());
								
								session.save(amdCarryForwardData);
							}
						}
					}
					
					//------------------- Step8.2 If this is the first record received for the VIN - Insert the remaining parameters with value 0
					if(prevAmdObj==null)
					{
						ListToStringConversion conversionObj = new ListToStringConversion();
						String receivedParamListAsString = conversionObj.getIntegerListString(receivedParamList).toString();
						Query newParamQuery = session.createQuery(" from MonitoringParameters where parameterId not in ("+receivedParamListAsString+")");
						Iterator newParmItr = newParamQuery.list().iterator();
						while(newParmItr.hasNext())
						{
							MonitoringParameters monitParm = (MonitoringParameters)newParmItr.next();
							
							AssetMonitoringDetailEntity amdCarryForwardData = new AssetMonitoringDetailEntity();
							amdCarryForwardData.setTransactionNumber(newAmhObj);
							amdCarryForwardData.setParameterId(monitParm);
							amdCarryForwardData.setParameterValue("0");
							session.save(amdCarryForwardData);
						}
					}
					
					iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Insert remaining parameters from prev transaction into AMD - END");
				}
				
				
				
				//************************************ STEP9: Insert the Data into AssetMonitoringDetailsExtended - FuelCapacity - For new packet
				//As of now FuelCapacity is not available for product as master data, FuelConsumedInLiters cannot be computed from percentage data, however this can act as a placeholder for the same
				if(amhObj==null)
				{
					iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Insert record into AMD Extended - START");
					// get the parameter Object for fuelConsumptionInLitres
					ExtendedMonitoringParameters extendedParam = null;
					Query fuelConsumedParamQ = session.createQuery("from ExtendedMonitoringParameters where extendedParameterName='FuelConsumedInLitres'");
					Iterator fuelConsumedParamItr = fuelConsumedParamQ.list().iterator();
					while (fuelConsumedParamItr.hasNext()) 
					{
						extendedParam = (ExtendedMonitoringParameters) fuelConsumedParamItr.next();
					}
					
					
					AssetMonitoringDetailExtended extendedParams = new AssetMonitoringDetailExtended();
					extendedParams.setTransactionNumber(newAmhObj);
					extendedParams.setExtendedParameterId(extendedParam);
					extendedParams.setExtendedParameterValue("0.0");
					session.save(extendedParams);
					iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Insert record into AMD Extended - END");
				}
				
				
				//************************************* STEP 10: Insert/Update AssetMonitoringSnapshot
				iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Insert/Update AssetMonitoringSnapshot - START");
				Query snapshotQuery = session.createQuery(" from AssetMonitoringSnapshotEntity where serialNumber='"+serialNumber+"'");
				Iterator snapshotItr = snapshotQuery.list().iterator();
				AssetMonitoringSnapshotEntity snapshotObj = null;
				while(snapshotItr.hasNext())
				{
					snapshotObj = (AssetMonitoringSnapshotEntity)snapshotItr.next();
				}
				
				//--------------------- Step 10.1: Insertion of new VIN into snapshot table
				if(snapshotObj==null)
				{
					AssetMonitoringSnapshotEntity newSnapshotObj = new AssetMonitoringSnapshotEntity();
					newSnapshotObj.setSerialNumber(asset);
					
					AssetMonitoringHeaderEntity transactionNum=null;
					if(amhObj==null)
						transactionNum=newAmhObj;
					else
						transactionNum=amhObj;
					newSnapshotObj.setTransactionNumber(transactionNum);
					newSnapshotObj.setTransactionTime(parser.getTransactionTime());
					
					if( (receivedRecordType.getRecordTypeName().equalsIgnoreCase("Log Packet"))  || ( (eventParamValues!=null) && (eventParamValues.containsKey("LOW_FUEL")) ) )
					{
						newSnapshotObj.setFuelLevel(currentFuelLevel);
						newSnapshotObj.setLatestFuelTxn(transactionNum);
					}
					
					if(receivedRecordType.getRecordTypeName().equalsIgnoreCase("Log Packet"))
						newSnapshotObj.setLatestLogTxn(transactionNum);
					else
						newSnapshotObj.setLatestEventTxn(transactionNum);
					
					session.save(newSnapshotObj);
				}
				
				//------------------------ Step 10.2: Update Snapshot table
				else
				{
									
					AssetMonitoringHeaderEntity transactionNum=null;
						
					if(amhObj==null)
						transactionNum=newAmhObj;
					else
						transactionNum=amhObj;
					
					if( (snapshotObj.getTransactionTime().before(parser.getTransactionTime())) || (snapshotObj.getTransactionTime().equals(parser.getTransactionTime())) )
					{
						snapshotObj.setTransactionNumber(transactionNum);
						snapshotObj.setTransactionTime(parser.getTransactionTime());
					}	
					
					if( (receivedRecordType.getRecordTypeName().equalsIgnoreCase("Log Packet"))  || ( (eventParamValues!=null) &&(eventParamValues.containsKey("LOW_FUEL")) ))
					{
						if(snapshotObj.getLatestFuelTxn()==null)
						{
							snapshotObj.setFuelLevel(currentFuelLevel);
							snapshotObj.setLatestFuelTxn(transactionNum);
						}
						else if(snapshotObj.getLatestFuelTxn().getTransactionTime().before(parser.getTransactionTime())) 
						{
							snapshotObj.setFuelLevel(currentFuelLevel);
							snapshotObj.setLatestFuelTxn(transactionNum);
						}
					}
					
					if(receivedRecordType.getRecordTypeName().equalsIgnoreCase("Log Packet"))
						snapshotObj.setLatestLogTxn(transactionNum);
					else
						snapshotObj.setLatestEventTxn(transactionNum);
						
					session.update(snapshotObj);
					
				}
				iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Insert/Update AssetMonitoringSnapshot - END");
				
				iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Commit the Records into DB - START");
				long startTime = System.currentTimeMillis();
				
				/* <!-- DF20160316 @Roopa for session flush changes -->*/
				
				session.flush();
				
				if (session.getTransaction().isActive()) 
				{
					session.getTransaction().commit();
				}
				long endTime=System.currentTimeMillis();
				iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"Commit the Records into DB - END");
				iLogger.info(currentTime+": ADS:"+serialNumber+":"+transactionTime+":"+" Total Time taken to Commit_"+(endTime-startTime));
				
				//Once the Data is succesfully inserted into DB, Move the XML Data Packet from Inprocess_XML to AEMP_OUTPUT folder
				archivedFile = new File(DataPackets_ProcessedFolderPath,processingFileName);
				archivedFile.mkdirs();
				if(archivedFile.exists())
					archivedFile.delete();
		        boolean moveStatus = processingFile.renameTo(archivedFile);
		      
		        if(moveStatus)
		        {
		        	iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
		        }
		        
		        iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"ADS Processing Logic - END");
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				// Added by Rajani Nagaraju 20130701. Resolution to the defectID 821: Discrepency in the parameter data getting logged into DB.
				if (session.getTransaction().isActive()) 
				{
					session.getTransaction().rollback();
				}
				
				status = "FAILURE";
				
							
				fLogger.fatal("ADS:"+serialNumber+":"+transactionTime+":"+"  :"+"Exception :" + e.getMessage());
				Writer result = new StringWriter();
	    	    PrintWriter printWriter = new PrintWriter(result);
	    	    e.printStackTrace(printWriter);
	    	    String err = result.toString();
	    	    fLogger.fatal("ADS:"+serialNumber+":"+transactionTime+":"+"  :"+"Exception trace: "+err);
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
	    	   
	    	    //In case of Exception, Move the XML Data Packet from Inprocess_XML back to WISE_UnprocessXMLs folder
				archivedFile = new File(DataPackets_UnprocessedFolderPath,processingFileName);
				archivedFile.mkdirs();
				if(archivedFile.exists())
					archivedFile.delete();
		        boolean moveStatus = processingFile.renameTo(archivedFile);
		      
		        if(moveStatus)
		        {
		        	iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from inprocess to wiseunprocess folder");
		        }
		        
		        iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"ADS Processing Logic - END");
				
			}
			
			finally
			{
				if(session!=null &&  (session.isOpen())) 
				{
					/* <!-- DF20160316 @Roopa for session flush changes -->*/
					/*session.flush();*/
					session.close();
				}
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			
			fLogger.fatal("ADS:"+serialNumber+":"+transactionTime+":"+" :Exception :"+e);
			
			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal("ADS:"+serialNumber+":"+transactionTime+":"+" :Exception trace: "+err);
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
			
			
			status= "FAILURE";
			
			//In case of Exception, Move the XML Data Packet from Inprocess_XML back to WISE_UnprocessXMLs folder
			archivedFile = new File(DataPackets_UnprocessedFolderPath,processingFileName);
			archivedFile.mkdirs();
			if(archivedFile.exists())
				archivedFile.delete();
	        boolean moveStatus = processingFile.renameTo(archivedFile);
	      
	        if(moveStatus)
	        {
	        	iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"File "+processingFile.getName()+" is moved successfully from inprocess to wiseunprocess folder");
	        }
	        
	        iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"ADS Processing Logic - END");
		}
		
		iLogger.info("ADS:"+serialNumber+":"+transactionTime+":"+"ADS Processing Logic - END");
		
		return status;
	}
	//************************************************** END of Set Remote Monitoring Data into database ******************************
	
}

//to Sort the Final alert list based on the event generaeted time

class transactionComparator implements Comparator<AssetEventLogImpl>
{

	 Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		Logger businessError = BusinessErrorLoggerClass.logger;
	@Override
	
	public int compare(AssetEventLogImpl arg0, AssetEventLogImpl arg1) 
	{
		// TODO Auto-generated method stub
		long t1=0;
		long t2=0;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(arg0.getEventGeneratedTime());
			Date date2 = format.parse(arg1.getEventGeneratedTime());
			t1 = date1.getTime();
			t2 = date2.getTime();
			
		}
		
		catch(ParseException e)
		{
			infoLogger.info(e);
		}
		return (int) (t2 - t1);
	}
	
	
}