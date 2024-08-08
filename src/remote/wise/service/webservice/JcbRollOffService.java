/*
 * CR334 : 20220830 : Dhiraj K : Billability Module Integration changes
 * ME100030804 : 20220830 : Dhiraj K : Real time SAP data push To Mobile App
 * JCB6332 : 20230206 : Dhiraj K : Re-rolloff handling for Billing
 * JCB6371 : 20230404 : Dhiraj K : Subscription history not getting update for Billing for Rolloff
 * CR395 : 20230425 : Dhiraj K : Rolloff date change
 * CR428 : 20230830 : Dhiraj Kumar : Sea Ports (Landmark) Configurations
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONObject;

import com.wipro.mda.AssetOwnershipDetails;
import com.wipro.mda.AssetProfileDetails;

import remote.wise.EAintegration.Qhandler.InterfaceDataProducer;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.businessentity.FileWiseTracking;
import remote.wise.dao.GeofenceDao;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.JcbRollOffImpl;
import remote.wise.util.BillingSubscriptionHistory;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.InterfaceProducerCaller;


/** Webservice to handle JCB RollOff process
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "JcbRollOffService")
public class JcbRollOffService {
	/** WebMethod that stores the JCB roll of data received from JCB SAP system
	 * @param serialNumber VIN as String input
	 * @param nickName EngineNumber as String input
	 * @param chasisNumber chasisNumber as String input
	 * @return Returns the status String
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "setVinMachineNameMapping", action = "setVinMachineNameMapping")
	/*public String setVinMachineNameMapping(@WebParam(name="serialNumber" ) String serialNumber, 
			 			  @WebParam(name="nickName" ) String nickName,
			 			  @WebParam(name="chasisNumber" ) String chasisNumber) throws CustomFault
	  {

		Logger infoLogger = Logger.getLogger("infoLogger"); 
		infoLogger.info("---- Webservice Input ------");
		infoLogger.info("Serial Number:"+serialNumber+",  "+"Engine Number:"+nickName+",  "+"chasisNumber:"+chasisNumber);

		long startTime = System.currentTimeMillis();
		String response = new JcbRollOffImpl().vinMachineNameMapping(serialNumber,nickName,chasisNumber);
		long endTime = System.currentTimeMillis();
		infoLogger.info("Webservice Execution Time in ms:"+(endTime-startTime));

		infoLogger.info("----- Webservice Output-----");
		infoLogger.info("Status:"+response);

		return response;

	  }*/
	public String setVinMachineNameMapping(@WebParam(name="serialNumber" ) String serialNumber, 
			@WebParam(name="engineNumber" ) String engineNumber,
			@WebParam(name="chasisNumber" ) String chasisNumber,
			@WebParam(name="messageId" ) String messageId,
			@WebParam(name="fileRef" ) String fileRef,
			@WebParam(name="process" ) String process,
			@WebParam(name="reprocessJobCode" ) String reprocessJobCode,
			@WebParam(name="make" ) String make,
			@WebParam(name="builtDate" ) String builtDate,
			@WebParam(name="machineNumber" ) String machineNumber	
			
			/*@WebParam(name="rollOffDate" ) String rollOffDate,
			@WebParam(name="profile" ) String profile,
			@WebParam(name="model" ) String model,
			@WebParam(name="llPlusFlag" ) String llPlusFlag,
			@WebParam(name="dealerCode" ) String dealerCode,
			@WebParam(name="customerCode" ) String customerCode,
			@WebParam(name="customerName" ) String customerName,
			@WebParam(name="zonalCode" ) String zonalCode*/) throws CustomFault
			{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("JcbRollOffService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("EA Processing: AssetRolloffFromJCB: "+messageId+": ---- Actual Webservice Input ------");
		iLogger.info("EA Processing: AssetRolloffFromJCB: "+messageId+":Serial Number:"+serialNumber+",  "+"Engine Number:"+engineNumber+",  "+"chasisNumber:"+chasisNumber+
				",  "+"Message Id:"+messageId+",  "+"File Reference:"+fileRef+
				",  "+"Process:"+process+",  "+"Reprocess JobCode:"+reprocessJobCode+
				",  "+"Make:"+make+","+"BuiltDate:"+builtDate+","+"machineNumber:"+machineNumber);

		long startTime = System.currentTimeMillis();
		//CR395.sn
		String rollOffDate = null;
		String rolloffDateFlag = null;
		//If RollOffDate is not true, system date is rolloff date
		if (System.getProperty("RollOffDate") !=null ) {
			rolloffDateFlag = System.getProperty("RollOffDate");
		}
		if(rolloffDateFlag == null || !rolloffDateFlag.equalsIgnoreCase("True")) {
			//Use current date date as Built date or Rolloff date
			Date currentDate = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			rollOffDate = dateFormat.format(currentDate);
		}else {
			rollOffDate = builtDate;
		}
		//CR395.en
		//String response = new JcbRollOffImpl().vinMachineNameMapping(serialNumber,engineNumber,chasisNumber,make,builtDate,machineNumber, messageId);
		String response = new JcbRollOffImpl().vinMachineNameMapping(serialNumber,engineNumber,chasisNumber,make,builtDate,machineNumber, messageId, rollOffDate);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:JcbRollOffService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		

		iLogger.info("EA Processing: AssetRolloffFromJCB: "+messageId+":----- Actual Webservice Output-----");
		iLogger.info("EA Processing: AssetRolloffFromJCB: "+messageId+":Status:"+response);

		//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
		String faultCause =null;
		//CR334.so
		/*if(response.split("-").length>1){
				faultCause=response.split("-")[1];
				response = response.split("-")[0].trim();
		}*/
		//CR334.eo
		//JCB6332.so
//		//CR334.sn
//		String rollOffDate = null;
//		if(response.split("-").length>1){
//			faultCause=response.split("-")[1].split("\\|")[0];
//			rollOffDate = response.split("\\|")[1].split("\\.")[0];
//			response = response.split("-")[0].trim();
//			iLogger.info(faultCause + "|" + rollOffDate + "|" + response);
//		}
//		//CR334.en
		//JCB6332.so

		//JCB6332.sn
		//String rollOffDate = null;//CR395.o
		boolean reRollOff = false;
		if(response.contains("|")) {
			if(response.split("-").length>1){
				faultCause=response.split("-")[1].split("\\|")[0];
				//rollOffDate = response.split("\\|")[1].split("\\.")[0];//JCB6371.o
				//reRollOff = Boolean.parseBoolean(response.split("\\|")[2]);
				reRollOff = Boolean.parseBoolean(response.split("\\|")[1]);
				response = response.split("-")[0].trim();
				//iLogger.info(faultCause + "|" + rollOffDate + "|" + response);//JCB6371.o
				iLogger.info(faultCause + "|" + response);//JCB6371.n
			}
		}else {
			//In case of failure, for few scenario rolloffdate and re-rolloff flag will not come
			if(response.split("-").length>1){
				faultCause=response.split("-")[1];
				response = response.split("-")[0].trim();
				iLogger.info(faultCause + "|" + response);
			}
		}
		//JCB6332.en

		//If Failure in insertion, put the message to fault_details table
		if(response.equalsIgnoreCase("FAILURE")){
			if(serialNumber==null)
				serialNumber="";
			if(engineNumber==null)
				engineNumber="";
			if(chasisNumber==null)
				chasisNumber="";
			if(make==null)
				make="";
			if(builtDate==null)
				builtDate="";
			if(machineNumber==null)
				machineNumber="";
			String messageString = serialNumber+"|"+engineNumber+"|"+chasisNumber+"|"+make+"|"+builtDate+"|"+machineNumber;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
			//errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode,faultCause);
			errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, reprocessJobCode,faultCause, "0003", "Service Layer");
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(messageId!=null && messageId.split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}
		}else{
			//CR334.sn
			// Invoke Billability module service to update billing.billing_subsHistory Table
			if(reRollOff) {//JCB6332.sn
				iLogger.info("No need to invoke Billing service as it is re-rolloff for machine "+ serialNumber);
			}else {//JCB6332.en
				//rollOffDate = new CommonUtil().getRolloffDate(serialNumber);//JCB6371.n//CR395.o
				iLogger.info("Now Invoking Billing service:" + response +":"+ rollOffDate);
				new BillingSubscriptionHistory().updateSubsHistory(serialNumber, rollOffDate + " 00:00:00", "2050-12-31 23:59:59");
			}
			//CR334.en
			//ME100030804.so
			//SH20011298-20211216 : CR264-20211118-LLPlusAPI-MachinesAPI_Changes_v00_00
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
			/*InterfacePoducerDataPayloadMap.put("TXN_KEY","InterfaceProducerData"+"_"+serialNumber);
			InterfacePoducerDataPayloadMap.put("serialNumber",serialNumber);
			InterfacePoducerDataPayloadMap.put("engineNumber",engineNumber);
			InterfacePoducerDataPayloadMap.put("chasisNumber",chasisNumber);
			InterfacePoducerDataPayloadMap.put("make",make);
			InterfacePoducerDataPayloadMap.put("builtDate",builtDate);
			InterfacePoducerDataPayloadMap.put("machineNumber",machineNumber);
			
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
			
			
			//DF20180105: @SU334449 - Invoking MOOL DA Layer from WISE post successful persistence of machine ownership details in AOS table
			//new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber);//20220921.o.Additional log added
			new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber, "RollOff");//20220921.n.Additional log added
			//DF20180108: @SU334449 - Invoking MOOL DA Layer from WISE post successful persistence of machine profile details in MySQL database
			new AssetProfileDetails().setAssetProfileRollOffDetails(serialNumber, engineNumber);
			
			//ME100030804.so
			//SH20011298-20211216 : CR264-20211118-LLPlusAPI-MachinesAPI_Changes_v00_00
			//iLogger.info("Calling InterfaceDataProducer for publishing the data to kafka topic -------> START");
			//new InterfaceDataProducer(InterfacePoducerDataPayloadMap.get("TXN_KEY"), InterfacePoducerDataPayloadMap);
			//iLogger.info("Calling InterfaceDataProducer for publishing the data to kafka topic -------> END");
			//ME100030804.eo
			//new InterfaceDataProducer().publishMachineData(InterfacePoducerDataPayloadMap.get("TXN_KEY"), InterfacePoducerDataPayloadMap);

			//ME100030804.sn
			// Invoke Interface Producer to publish data on kafka topic
			new InterfaceProducerCaller().invokeInterfaceProducer(serialNumber, "ROLLOFF");
			//ME100030804.en
			
			//CR428.sn
			// Update landmark for the machine
			String landmarkAssetStatus = new GeofenceDao().updateLandmarkAssetMap("GeoFenceForPortArea", serialNumber);
			iLogger.info("landmarkAsset update status : "+landmarkAssetStatus);
			//CR428.en
			
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "sucessCount", 1);
			iLogger.info("Status on updating data into interface log details table :"+uStatus);
			if(messageId!=null && messageId.split("\\|").length>1){
				CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", -1);
			}
			//DF20180207:KO369761 - deleting message from fault details table if it is there.
			ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
			messageHandlerObj.deleteErrorMessage(messageId);
		}

		//DF20180208 - KO369761: Inserting file and response into interaface tracking table.
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		FileWiseTracking trackingtable=null;
		HashMap<String, String> data=new HashMap<String, String>();
		data.put(fileRef, response);

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
				trackingtable.setRollOff(jsondata);
				session.update(trackingtable);
				iLogger.info("RollOff:Updated the file wise tracking table");
			}
			else{
				trackingtable = new FileWiseTracking();
				trackingtable.setSerialNumber(serialNumber);
				String jsondata= new JSONObject(data).toString();
				trackingtable.setRollOff(jsondata);
				session.save(trackingtable);
				iLogger.info("RollOff:Inserted into the file wise tracking table");

			}

		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception occured in inserting/updating the data into File wise tracking table"+e.getMessage());

		}finally{
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
