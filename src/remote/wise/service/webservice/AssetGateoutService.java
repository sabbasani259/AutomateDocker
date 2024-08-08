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

import remote.wise.EAintegration.Qhandler.InterfaceDataProducer;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.businessentity.FileWiseTracking;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetGateoutIml;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.InterfaceProducerCaller;
//import remote.wise.util.WiseLogger;
/** Webservice to record the GateOut Process
 * @author Rajani Nagaraju
 * ME100030804 : Dhiraj Kumar : 20220826 : Incorrect SAP data (Roll off, Personality, Gate out Sale and D2C sale) 
 * 		   		 being sent to Mobile App
 * JCB6341 : 20230306 : Dhiraj K : Sale date is not getting updated as part of Direct Gateout sale 
 */
@WebService(name = "AssetGateoutService")
public class AssetGateoutService 
{
	/** Webservice method to record the asset sale to dealer/ Direct sale to Customer
	 * @param dealerCode DealerCode
	 * @param customerCode CustomerCode if it is a direct sale to Customer
	 * @param engineNumber engineNumber of the Asset
	 * @param serialNumber VIN - Optional parameter
	 * @param messageId
	 * @param fileRef
	 * @param process
	 * @param reprocessJobCode
	 * @return Returns the process result status
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "setAssetGateoutService", action = "setAssetGateoutService")
	public String setAssetGateoutService(@WebParam(name="dealerCode") String dealerCode,
			@WebParam(name="customerCode") String customerCode,
			@WebParam(name="engineNumber") String engineNumber,
			@WebParam(name="serialNumber") String serialNumber,
			@WebParam(name="messageId" ) String messageId,
			@WebParam(name="fileRef" ) String fileRef,
			@WebParam(name="process" ) String process,
			@WebParam(name="reprocessJobCode" ) String reprocessJobCode
			
			/*@WebParam(name="rollOffDate" ) String rollOffDate,
			@WebParam(name="profile" ) String profile,
			@WebParam(name="model" ) String model,
			@WebParam(name="llPlusFlag" ) String llPlusFlag,			
			@WebParam(name="customerName" ) String customerName,
			@WebParam(name="zonalCode" ) String zonalCode*/)
	{

		String response = "SUCCESS";


		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetGateoutService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		iLogger.info("EA Processing: AssetGateOut: "+messageId+":---- Webservice Input ------");
		iLogger.info("EA Processing: AssetGateOut: "+messageId+": dealerCode: "+dealerCode+",  "+"customerCode: "+customerCode+",   " +
				"engineNumber: "+engineNumber+",  "+"serialNumber: "+serialNumber+",  messageId:"+messageId);

		long startTime = System.currentTimeMillis();

		iLogger.info("AssetGateOutLog: "+serialNumber+": Before calling IMPL");

		//JCB6341.sn
		//Current date will be gateout date(Gateout to Dealer) or sale date(Gateout to Customer)
		Date currentDate = new Date();
		String gateoutDateString = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
		//JCB6341.en
		//response = new AssetGateoutIml().setAssetGateoutDetails(dealerCode, customerCode, engineNumber, serialNumber, messageId, gateoutDateString);//JCB6341.o
		response = new AssetGateoutIml().setAssetGateoutDetails(dealerCode, customerCode, engineNumber, serialNumber, messageId, gateoutDateString);//JCB6341.n

		iLogger.info("AssetGateOutLog: "+serialNumber+": After calling IMPL:"+response);


		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetGateoutService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		iLogger.info("EA Processing: AssetGateOut: "+messageId+": ----- Webservice Output-----");
		iLogger.info("EA Processing: AssetGateOut: "+messageId+": Status:"+response);

		//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
		String faultCause =null;
		if(response.split("-").length>1)
		{
			faultCause=response.split("-")[1];
			response = response.split("-")[0].trim();

		}

		//If Failure in insertion, put the message to fault_details table
		if(response.equalsIgnoreCase("FAILURE"))
		{
			if(dealerCode==null)
				dealerCode="";
			if(customerCode==null)
				customerCode="";
			if(engineNumber==null)
				engineNumber="";
			if(serialNumber==null)
				serialNumber="";

			String messageString = dealerCode+"|"+customerCode+"|"+engineNumber+"|"+serialNumber;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode, faultCause);
			errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, reprocessJobCode, faultCause,"0003","Service Layer");

			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(messageId!=null && messageId.split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
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
			InterfacePoducerDataPayloadMap.put("zonalCode",zonalCode);	*/	
			//SH20011298-20211216 : CR264-20211118-LLPlusAPI-MachinesAPI_Changes_v00_00
			
			//ME100030804.so
			/*HashMap<String,String> InterfacePoducerDataPayloadMap = new HashMap<String,String>();
			InterfacePoducerDataPayloadMap.put("TXN_KEY","InterfaceProducerData"+"_"+serialNumber);			
			InterfacePoducerDataPayloadMap.put("dealerCode",dealerCode);
			InterfacePoducerDataPayloadMap.put("customerCode",customerCode);
			InterfacePoducerDataPayloadMap.put("engineNumber",engineNumber);
			InterfacePoducerDataPayloadMap.put("serialNumber",serialNumber);
			
			InterfacePoducerDataPayloadMap.put("rollOffDate","NA");
			InterfacePoducerDataPayloadMap.put("profile","NA");
			InterfacePoducerDataPayloadMap.put("model","NA");
			InterfacePoducerDataPayloadMap.put("llPlusFlag","NA");	
			InterfacePoducerDataPayloadMap.put("dealerName","NA");
			InterfacePoducerDataPayloadMap.put("customerName","NA");
			InterfacePoducerDataPayloadMap.put("zonalCode","NA");*/			
			//ME100030804.eo

			//DF20180108: @SU334449 - Invoking MOOLDA Layer from WISE post successful persistence of machine ownership details in AOS table
			//new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber);//20220921.o.Additional log added
			new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber, "GateoutSale");//20220921.n.Additional log added

			//ME100030804.so
			//SH20011298-20211216 : CR264-20211118-LLPlusAPI-MachinesAPI_Changes_v00_00
			//new InterfaceDataProducer(InterfacePoducerDataPayloadMap.get("TXN_KEY"), InterfacePoducerDataPayloadMap);
			//new InterfaceDataProducer().publishMachineData(InterfacePoducerDataPayloadMap.get("TXN_KEY"), InterfacePoducerDataPayloadMap);
			//ME100030804.eo

			//ME100030804.sn
			// Invoke Interface Producer to publish data on kafka topic
			new InterfaceProducerCaller().invokeInterfaceProducer(serialNumber, "GATEOUT");
			//ME100030804.en
						
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
				trackingtable.setGateOut(jsondata);
				session.update(trackingtable);
				iLogger.info("AssetGateout:Updated the file wise tracking table");
			}
			else{
				trackingtable = new FileWiseTracking();
				trackingtable.setSerialNumber(serialNumber);
				String jsondata= new JSONObject(data).toString();
				trackingtable.setGateOut(jsondata);
				session.save(trackingtable);
				iLogger.info("AssetGateout:Inserted into the file wise tracking table");

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
