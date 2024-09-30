package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONObject;

import com.wipro.mda.AssetProfileDetails;



import remote.wise.EAintegration.Qhandler.InterfaceDataProducer;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.businessentity.FileWiseTracking;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetPersonalityImpl;
import remote.wise.service.implementation.OassetPersonalityImpl;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.InterfaceProducerCaller;

/** Webservice method to set the Asset personality details
 * @author Rajani Nagaraju
 * ME100030804 : Dhiraj Kumar : 20220826 : Incorrect SAP data (Roll off, Personality, Gate out Sale and D2C sale) 
 * 		   		 being sent to Mobile App
 */
@WebService(name = "AssetPersonalityService")
public class AssetPersonalityService 
{

	/*
	/** Webmethod to set the AssetPersonality details
	 * @param machineName MachineNickName
	 * @param assetGroupName Machine Profile
	 * @param assetTypeName Model
	 * @param installDate installation Date
	 * @param serialNumber VIN
	 * @param description Machine description
	 * @param purchaseDate Asset purchase date
	 * @param assetClassName AssetClass
	 * @param make Machine Make
	 * @return Returns the status String
	 * @throws CustomFault
	 */

	/*
	@WebMethod(operationName = "SetAssetPersonalityDetails", action = "SetAssetPersonalityDetails")
	public String setAssetPersonalityDetails(@WebParam(name="machineName" ) String machineName, 
			@WebParam(name="assetGroupName" ) String assetGroupName,
			@WebParam(name="assetTypeName" ) String assetTypeName,
			@WebParam(name="installDate" ) String installDate,
			@WebParam(name="serialNumber" ) String serialNumber,
			@WebParam(name="description" ) String description,
			@WebParam(name="purchaseDate" ) String purchaseDate,
			@WebParam(name="assetClassName" ) String assetClassName,
			@WebParam(name="engineTypeName" ) String engineTypeName,
			@WebParam(name="make" ) int make) throws CustomFault
			{
		Logger infoLogger = Logger.getLogger("infoLogger");
		AssetPersonalityImpl assetPersonalityImpl = new AssetPersonalityImpl();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: "+startDate);
		infoLogger.info("---- Webservice Input ------");
		infoLogger.info("machineName:"+machineName+",  "+"assetGroupName:"+assetGroupName+",   " +
				"assetTypeName:"+assetTypeName+",  "+"installDate:"+installDate+",  "+"serialNumber:"+serialNumber+",  " +
				"description: "+description+",  "+"purchaseDate:"+purchaseDate+",  "+"assetClassName:"+assetClassName+",  "+"make:"+make);
		long startTime = System.currentTimeMillis();
		String response = assetPersonalityImpl.setAssetPersonalityDetails(machineName, assetGroupName, assetTypeName, installDate, serialNumber, description, purchaseDate,assetClassName, engineTypeName,make);
		infoLogger.info("----- Webservice Output-----");
		infoLogger.info("Status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		infoLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		infoLogger.info("Webservice Execution Time in ms:"+(endTime-startTime));
		return response;
			}
	 */


	/** Webmethod to set the AssetPersonality details
	 * @param engineNumber engine Number of the Machine
	 * @param assetGroupCode Machine Profile Code
	 * @param assetTypeCode Model Code
	 * @param engineTypeCode Code of Engine Type
	 * @param assetBuiltDate Machine Built Date
	 * @param make Machine Make
	 * @param fuelCapacity Fuel Capacity of fuel Tank
	 * @param serialNumber VIN
	 * @param messageId
	 * @param fileRef
	 * @param process
	 * @param reprocessJobCode
	 * @return Returns the status String
	 */
	@WebMethod(operationName = "SetAssetPersonalityDetails", action = "SetAssetPersonalityDetails")
	public String setAssetPersonalityDetails(@WebParam(name="engineNumber" ) String engineNumber, 
			@WebParam(name="assetGroupCode" ) String assetGroupCode,
			@WebParam(name="assetTypeCode" ) String assetTypeCode,
			@WebParam(name="engineTypeCode" ) String engineTypeCode,
			@WebParam(name="assetBuiltDate" ) String assetBuiltDate,
			@WebParam(name="make" ) String make,
			@WebParam(name="fuelCapacity" ) String fuelCapacity,
			@WebParam(name="serialNumber" ) String serialNumber,
			@WebParam(name="messageId" ) String messageId,
			@WebParam(name="fileRef" ) String fileRef,
			@WebParam(name="process" ) String process,
			@WebParam(name="reprocessJobCode" ) String reprocessJobCode
			
			/*@WebParam(name="rollOffDate" ) String rollOffDate,
			@WebParam(name="profile" ) String profile,
			@WebParam(name="model" ) String model,
			@WebParam(name="llPlusFlag" ) String llPlusFlag,
			@WebParam(name="dealerCode" ) String dealerCode,
			@WebParam(name="customerCode" ) String customerCode,
			@WebParam(name="customerName" ) String customerName,
			@WebParam(name="zonalCode" ) String zonalCode*/)
	{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetPersonalityService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		AssetPersonalityImpl assetPersonalityImpl = new AssetPersonalityImpl();

		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
		iLogger.info("EA Processing: AssetPersonality: "+messageId+": ---- Webservice Input ------");
		iLogger.info("EA Processing: AssetPersonality: "+messageId+": Engine Number:"+engineNumber+",  "+"assetGroupCode:"+assetGroupCode+",   " +
				"assetTypeCode:"+assetTypeCode+",  "+"engineTypeCode:"+engineTypeCode+",  "+"assetBuiltDate:"+assetBuiltDate+",  " +
				"make: "+make+",  "+"fuelCapacity:"+fuelCapacity+",  "+"serialNumber:"+serialNumber);

		long startTime = System.currentTimeMillis();
		//String response = assetPersonalityImpl.setAssetPersonalityDetails(engineNumber, assetGroupCode, assetTypeCode, engineTypeCode, assetBuiltDate, make, fuelCapacity,serialNumber, messageId);

		//DF20160115 @Roopa for Set AssetPersonality Details into OrientDB

		/*Properties dtabaseInsertion = new Properties();
				dtabaseInsertion = CommonUtil.getDepEnvProperties();

				String assetOwnerSnapMysqlInsertionCheck = dtabaseInsertion.getProperty("InsertMySQL");

				String assetOwnerSnapOrientDBInsertionCheck = dtabaseInsertion.getProperty("InsertOrientDB");

				String response = "";

				String orientResponse="";



				if (assetOwnerSnapMysqlInsertionCheck.equalsIgnoreCase("true")) {*/
		String response = "";
		response = assetPersonalityImpl.setAssetPersonalityDetails(engineNumber, assetGroupCode, assetTypeCode, engineTypeCode, assetBuiltDate, make, fuelCapacity,serialNumber, messageId);

		//}

		/*	if (assetOwnerSnapOrientDBInsertionCheck.equalsIgnoreCase("true")) {

					OassetPersonalityImpl OassetPersonalityImplObj=new OassetPersonalityImpl();
					iLogger.info("setOAssetPersonalityDetails input :"+"serialNumber :"+serialNumber+","+"assetGroupCode :"+assetGroupCode+","+"assetTypeCode :"+assetTypeCode);
					orientResponse= OassetPersonalityImplObj.setOAssetPersonalityDetails(serialNumber,assetGroupCode,assetTypeCode);
				}

				iLogger.info("EA Processing: AssetPersonality setting into orientDB: "+messageId+": Status:"+orientResponse);*/
		//END

		long endTime = System.currentTimeMillis();
		iLogger.info("EA Processing: AssetPersonality: "+messageId+": Webservice Execution Time in ms:"+(endTime-startTime));

		iLogger.info("EA Processing: AssetPersonality: "+messageId+": ----- Webservice Output-----");
		iLogger.info("EA Processing: AssetPersonality: "+messageId+": Status:"+response);

		//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
		String faultCause =null;
		if(response.split("-").length>1)
		{
			faultCause=response.split("-")[1];
			response = response.split("-")[0].trim();

		}

		//If Failure in insertion, put the message to fault_details table
		if(response.toUpperCase().contains("FAILURE"))
		{
			if(engineNumber==null)
				engineNumber="";
			if(assetGroupCode==null)
				assetGroupCode="";
			if(assetTypeCode==null)
				assetTypeCode="";
			if(engineTypeCode==null)
				engineTypeCode="";
			if(fuelCapacity==null)
				fuelCapacity="";
			if(assetBuiltDate==null)
				assetBuiltDate="";
			if(serialNumber==null)
				serialNumber="";

			String messageString = engineNumber+"|"+assetGroupCode+"|"+assetTypeCode+"|"+engineTypeCode+"|"+assetBuiltDate+"|"+make+"|"+fuelCapacity+"|"+serialNumber;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
			//errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode,faultCause);
			//DF20180214 :@Maniratnam ## Interfaces backTracking--inserting into fault details with new parameters
			errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, reprocessJobCode,faultCause,"0003","Service Layer");
			if(messageId!=null && messageId.split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount",1);
				iLogger.info("AssetPersonality::Status on Updating data into interface log details table :"+uStatus);
			}
		}else{
			/*HashMap<String,String> InterfacePoducerDataPayloadMap = new HashMap<String,String>();
			InterfacePoducerDataPayloadMap.put("TXN_KEY","InterfaceProducerData"+"_"+serialNumber);
			InterfacePoducerDataPayloadMap.put("rollOffDate",rollOffDate);
			InterfacePoducerDataPayloadMap.put("profile",profile);
			InterfacePoducerDataPayloadMap.put("model",model);
			InterfacePoducerDataPayloadMap.put("llPlusFlag",llPlusFlag);
			InterfacePoducerDataPayloadMap.put("dealerCode",dealerCode);
			InterfacePoducerDataPayloadMap.put("customerCode",customerCode);
			InterfacePoducerDataPayloadMap.put("customerName",customerName);
			InterfacePoducerDataPayloadMap.put("zonalCode",zonalCode);*/
			
			//ME100030804.so
			//SH20011298-20211216 : CR264-20211118-LLPlusAPI-MachinesAPI_Changes_v00_00
			/*HashMap<String,String> InterfacePoducerDataPayloadMap = new HashMap<String,String>();
			InterfacePoducerDataPayloadMap.put("TXN_KEY","InterfaceProducerData"+"_"+serialNumber);
			InterfacePoducerDataPayloadMap.put("engineNumber",engineNumber);
			InterfacePoducerDataPayloadMap.put("assetGroupCode",assetGroupCode);
			InterfacePoducerDataPayloadMap.put("assetTypeCode",assetTypeCode);
			InterfacePoducerDataPayloadMap.put("engineTypeCode",engineTypeCode);
			InterfacePoducerDataPayloadMap.put("assetBuiltDate",assetBuiltDate);
			InterfacePoducerDataPayloadMap.put("make",make);
			InterfacePoducerDataPayloadMap.put("fuelCapacity",fuelCapacity);
			InterfacePoducerDataPayloadMap.put("serialNumber",serialNumber);
			
			InterfacePoducerDataPayloadMap.put("rollOffDate","NA");
			InterfacePoducerDataPayloadMap.put("profile","NA");
			InterfacePoducerDataPayloadMap.put("model","NA");
			InterfacePoducerDataPayloadMap.put("llPlusFlag","NA");
			InterfacePoducerDataPayloadMap.put("dealerCode","NA");
			InterfacePoducerDataPayloadMap.put("dealerName","NA");
			InterfacePoducerDataPayloadMap.put("customerCode","NA");
			InterfacePoducerDataPayloadMap.put("customerName","NA");
			InterfacePoducerDataPayloadMap.put("zonalCode","NA");*/
			//ME100030804.eo
			
			//DF20180108: @SU334449 - Invoking MOOL DA Layer from WISE post successful persistence of machine profile details in MySQL database
			new AssetProfileDetails().setAssetProfilePersonalityDetails(serialNumber, assetGroupCode, assetTypeCode);
			
			//ME100030804.so
			//SH20011298-20211216 : CR264-20211118-LLPlusAPI-MachinesAPI_Changes_v00_00
			//new InterfaceDataProducer(InterfacePoducerDataPayloadMap.get("TXN_KEY"), InterfacePoducerDataPayloadMap);
			//new InterfaceDataProducer().publishMachineData(InterfacePoducerDataPayloadMap.get("TXN_KEY"), InterfacePoducerDataPayloadMap);
			//ME100030804.eo

			//ME100030804.sn
			// Invoke Interface Producer to publish data on kafka topic
			new InterfaceProducerCaller().invokeInterfaceProducer(serialNumber, "PERSONALITY");
			//ME100030804.so
			
			String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "sucessCount",1);
			iLogger.info("AssetPersonality::Status on Updating data into interface log details table :"+uStatus);
			if(messageId!=null && messageId.split("\\|").length>1){
				CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", -1);
			} 
			//DF20180207:KO369761 - deleting message from fault details table if it is there.
			ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
			messageHandlerObj.deleteErrorMessage(messageId);
		}

		//DF20180212 :@Maniratnam ## Interfaces backTracking--to insert into the filewisetracking table
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		FileWiseTracking trackingtable=null;
		HashMap<String, String> data=new HashMap<String,String>();
		data.put(fileRef,response);

		try{
			serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");
			if(serialNumber.length()>7)
				serialNumber = serialNumber.substring(serialNumber.length()-7);

			Query query=session
					.createQuery("from FileWiseTracking where serialNumber ='"+serialNumber +"'");
			Iterator itr = query.list().iterator();
			if(itr.hasNext()) {
				trackingtable = (FileWiseTracking) itr.next();
			}

			if(trackingtable != null) {
				String jsondata= new JSONObject(data).toString();
				trackingtable.setPersonality(jsondata);
				session.update(trackingtable);
				iLogger.info("AssetPersonality:Updated the file wise tracking table");
			}
			else{
				trackingtable=new FileWiseTracking();
				trackingtable.setSerialNumber(serialNumber);
				String jsondata= new JSONObject(data).toString();
				trackingtable.setPersonality(jsondata);
				session.save(trackingtable);
				iLogger.info("AssetPersonality::Inserted into the file wise tracking table");

			}

		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("AssetPersonality:: Exception occured in inserting/updating the data into File wise tracking table"+e.getMessage());
		}
		finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 
		}

		return response;
	}

}
