package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetGroupTypeRespContract;
import remote.wise.service.datacontract.AssetGroupUsersRespContract;
import remote.wise.service.datacontract.ServiceHistoryReqContract;
import remote.wise.service.datacontract.ServiceHistoryRespContract;
import remote.wise.service.datacontract.ServiceScheduleRespContract;
import remote.wise.service.implementation.AssetGroupTypeImpl;
import remote.wise.service.implementation.ServiceHistoryImpl;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;

@WebService(name = "ServiceHistoryDetailsService")
public class ServiceHistoryDetailsService {

	@WebMethod(operationName = "GetServiceHistoryDetails", action = "GetServiceHistoryDetails")
	public List<ServiceHistoryRespContract> getServiceHistoryDetails(ServiceHistoryReqContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ServiceHistoryDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("<-----Webservice Input----->");
		iLogger.info("SerialNumber:"+reqObj.getSerialNumber());
		reqObj.setSerialNumber(reqObj.getSerialNumber());
		
		//Validating VIN Hierarchy
		CommonUtil util = new CommonUtil();
		if(reqObj.getSerialNumber().split("\\|").length > 1){
			int tenancyId= Integer.parseInt(reqObj.getSerialNumber().split("\\|")[1]);
			reqObj.setSerialNumber(reqObj.getSerialNumber().split("\\|")[0]);
			String serialNumber = util.validateVIN(tenancyId,reqObj.getSerialNumber());
			if(serialNumber == null || serialNumber.equalsIgnoreCase("FAILURE")){
				throw new CustomFault("Invalid VIN Number");
			}
		}
		
		//DF20180803:KO369761-InputField Validation
		String isValidinput=null;
		isValidinput = util.inputFieldValidation(reqObj.getSerialNumber());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		List<ServiceHistoryRespContract> respObj = new ServiceHistoryImpl().getServiceHistory(reqObj);
		iLogger.info("<-----Webservice Output----->");
		for(int i=0;i<respObj.size();i++){
			iLogger.info(i+" ROW");
			//DF20180423:IM20018382 - An additional field jobCardDetails.
			iLogger.info("JobCardNumber:"+respObj.get(i).getJobCardNumber()+" , ScheduleName:"+respObj.get(i).getJobCardDetails()+" , ServiceDate:"+respObj.get(i).getServiceDate()+" , Job Card Details:"+respObj.get(i).getServiceDate()+" , ServiceName:"+respObj.get(i).getServiceName()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ServiceHistoryDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj; 
	}


	/** This method sets the Service History details received from EA system
	 * @param serialNumber VIN as String input
	 * @param dealerCode Code of the Service dealer
	 * @param jobCardNumber jobCard Number of the service done
	 * @param dbmsPartCode dbms partCode of the service being done
	 * @param servicedDate Date of when the service is done for the VIN
	 * @param messageId
	 * @param fileRef
	 * @param process
	 * @param reprocessJobCode
	 * @return Returns the status String
	 */
	@WebMethod(operationName = "SetServiceHistoryDetails", action = "SetServiceHistoryDetails")
	public String setServiceHistory(@WebParam(name="serialNumber" ) String serialNumber,
			@WebParam(name="dealerCode" ) String dealerCode,
			@WebParam(name="jobCardNumber" ) String jobCardNumber,
			@WebParam(name="dbmsPartCode" ) String dbmsPartCode,
			@WebParam(name="callTypeId" ) String callTypeId,
			@WebParam(name="servicedDate" ) String servicedDate,
			@WebParam(name="messageId" ) String messageId,
			@WebParam(name="fileRef" ) String fileRef,
			@WebParam(name="process" ) String process,
			@WebParam(name="reprocessJobCode" ) String reprocessJobCode,
			@WebParam(name="jobCardDetails" ) String jobCardDetails) 

	{

		String responseStatus = "SUCCESS-Record Processed";

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ServiceHistoryDetailsService:","info");

		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		Logger businessError = BusinessErrorLoggerClass.logger;

		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		infoLogger.info("EA Processing: AssetServiceDetails: "+messageId+": ---- Webservice Input ------");
		//DF20180423:IM20018382 - An additional field jobCardDetails.
		infoLogger.info("EA Processing: AssetServiceDetails: "+messageId+": Serial Number:"+serialNumber+",  "+"DealerCode:"+dealerCode+",  "+"JobCardNumber:"+jobCardNumber+
				",  "+"Dbms PartCode:"+dbmsPartCode+",  "+"callTypeId:"+callTypeId+", "+"Serviced Date:"+servicedDate+",  "+"Job Card Details:"+jobCardDetails);

		long startTime = System.currentTimeMillis();
		//DF20190423: IM20018382 adding new field jobCardDetails
		responseStatus= new ServiceHistoryImpl().setServiceHistoryDetails(serialNumber,dealerCode,jobCardNumber,dbmsPartCode,servicedDate,jobCardDetails,callTypeId, messageId);
		long endTime = System.currentTimeMillis();
		infoLogger.info("EA Processing: AssetServiceDetails: "+messageId+": Webservice Execution Time in ms:"+(endTime-startTime));

		infoLogger.info("EA Processing: AssetServiceDetails: "+messageId+": ----- Webservice Output-----");
		infoLogger.info("EA Processing: AssetServiceDetails: "+messageId+": Status: "+responseStatus);


		//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
		String faultCause =null;
		if(responseStatus.split("-").length>1)
		{
			faultCause=responseStatus.split("-")[1];
			responseStatus = responseStatus.split("-")[0].trim();

		}


		//If Failure in insertion, put the message to fault_details table
		if(responseStatus.equalsIgnoreCase("FAILURE"))
		{
			if(serialNumber==null)
				serialNumber="";
			if(dealerCode==null)
				dealerCode="";
			if(jobCardNumber==null)
				jobCardNumber="";
			if(dbmsPartCode==null)
				dbmsPartCode="";
			if(servicedDate==null)
				servicedDate="";
			//DF20180423:IM20018382 - Validating additional field jobCardDetails.
			if(jobCardDetails==null)
				jobCardDetails="";

			//DF20180423:IM20018382 - Appending additional field jobCardDetails.
			String messageString = serialNumber+"|"+dealerCode+"|"+jobCardNumber+"|"+dbmsPartCode+"|"+servicedDate+"|"+jobCardDetails;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode,faultCause);
			errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, reprocessJobCode,faultCause,"0003","Service Layer");

			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(messageId!=null && messageId.split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", 1);
				infoLogger.info("Status on updating data into interface log details table :"+serialNumber+"::"+uStatus);
			}
		}else{
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "sucessCount", 1);
			infoLogger.info("Status on updating data into interface log details table :"+serialNumber+"::"+uStatus);
			if(messageId!=null && messageId.split("\\|").length>1){
				CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", -1);
			}
			ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
			messageHandlerObj.deleteErrorMessage(messageId);
		}

		return responseStatus;
	}
}
