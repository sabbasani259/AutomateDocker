package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.MasterServiceScheduleRequestContract;
import remote.wise.service.datacontract.MasterServiceScheduleResponseContract;
import remote.wise.service.implementation.MasterServiceScheduleImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;
/** WebService class to set and get the serviceName,scheduleName,durationSchedule,engineHoursSchedule,assetTypeId,engineTypeId,assetGroupId for the specified assetTypeId,engineTypeId,assetGroupId
 * @author jgupta41
 *
 */

@WebService(name = "MasterServiceSchedule")
public class MasterServiceSchedule {
	
	/** This method gets serviceName,scheduleName,durationSchedule,engineHoursSchedule for the specified assetTypeId,engineTypeId,assetGroupId
	 * @param reqObj Get the details of  assetTypeId,engineTypeId,assetGroupId,serviceName by passing the same to this request Object
	 * @return Returns respObj for the specified assetTypeId,engineTypeId and assetGroupId or serviceName 
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetMasterServiceSchedule", action = "GetMasterServiceSchedule")

	public List<MasterServiceScheduleResponseContract> getMasterServiceScheduleDetails(@WebParam(name="reqObj") MasterServiceScheduleRequestContract  reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MasterServiceSchedule:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("assetTypeId:"+reqObj.getAssetTypeId()+","+"engineTypeId:"+reqObj.getEngineTypeId()+","+"assetGroupId:"+reqObj.getAssetGroupId()+","+"scheduleName:"+reqObj.getScheduleName()+"");
		
		//DF20181015 - KO369761 - Extracting CSRF Token from login field.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		String loginId = null;
		boolean isValidCSRF = false;
		if(reqObj.getScheduleName().split("\\|").length > 2){
			loginId=reqObj.getScheduleName().split("\\|")[1];
			csrfToken=reqObj.getScheduleName().split("\\|")[2];
			reqObj.setScheduleName(reqObj.getScheduleName().split("\\|")[0]);

			//DF20181011 - KO369761 - Validating the CSRF Token against login id.
			if(csrfToken != null)
				isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
			if(!isValidCSRF){
				iLogger.info("getMasterServiceScheduleDetails ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
		}
		
		//DF20180803:KO369761-InputField Validation
		String isValidinput=null;

		isValidinput = util.inputFieldValidation(reqObj.getScheduleName());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		List<MasterServiceScheduleResponseContract> respobj=new MasterServiceScheduleImpl().getMasterServiceScheduleDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respobj.size(); i++){
			iLogger.info("serviceName:"+respobj.get(i).getServiceName()+","+"scheduleName:"+respobj.get(i).getScheduleName()+","+"durationSchedule:"+respobj.get(i).getDurationSchedule()+","+"engineHoursSchedule:"+respobj.get(i).getEngineHoursSchedule()+","+"dbmsPartCode:"+respobj.get(i).getDbmsPartCode()+","+"assetTypeId:"+respobj.get(i).getAssetTypeId()+","+"engineTypeId:"+respobj.get(i).getEngineTypeId()+","+"assetGroupId:"+respobj.get(i).getAssetGroupId()+","+"engineTypeName:"+respobj.get(i).getEngineTypeName()+","+"asset_group_name:"+respobj.get(i).getAsset_group_name()+","+"asset_type_name:"+respobj.get(i).getAsset_type_name()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MasterServiceSchedule~executionTime:"+(endTime-startTime)+"~"+""+"~");	
		
		return respobj;
	}	
	/** Webservice method to set the Service Schedule Details for a product - Modified by Rajani Nagaraju
	 * @param servicePlan - Standard Warranty, Extended Warranty, or annual service contract , or 2000 hrs
	 * @param serviceName Name of the service like  First Free Service
	 * @param scheduleName Name of the service schedule such as  Model - Backhoe Loader - JCB Engine
	 * @param dbmsPartCode dbmsPartCode associated with the schedule
	 * @param assetGroupCode
	 * @param assetTypeCode
	 * @param engineTypeCode
	 * @param engineHours Scheduled EngineHous for Service
	 * @param days service schedule in No. of days
	 * @param messageId
	 * @param fileRef
	 * @param process
	 * @param reprocessJobCode
	 * @return Returns the status String
	 */
	@WebMethod(operationName = "setMasterServiceScheduleDetails", action = "setMasterServiceScheduleDetails")
	public String setMasterServiceScheduleDetails(@WebParam(name="servicePlan" ) String servicePlan, 
			  	 @WebParam(name="serviceName" ) String serviceName,
	 			 @WebParam(name="scheduleName" ) String scheduleName,
	 			 @WebParam(name="dbmsPartCode" ) String dbmsPartCode,
	 			 @WebParam(name="assetGroupCode" ) String assetGroupCode,
	 			 @WebParam(name="assetTypeCode" ) String assetTypeCode,
	 			 @WebParam(name="engineTypeCode" ) String engineTypeCode,
	 			 @WebParam(name = "engineHours") String engineHours,
	 			 @WebParam(name="days" ) String days,
	 			 @WebParam(name="messageId" ) String messageId,
	 			 @WebParam(name="fileRef" ) String fileRef,
	 			 @WebParam(name="process" ) String process,
	 			 @WebParam(name="reprocessJobCode" ) String reprocessJobCode)
	{
		String response = "SUCCESS";
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLoggeiLoggerer = WiseLogger.getLogger("MasterServiceSchedule:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger iLogger = Logger.getLogger("iLogger"); 
		iLogger.info("---- Webservice Input ------");
		iLogger.info("servicePlan:"+servicePlan+",  "+"serviceName:"+serviceName+",  " +
					"scheduleName:"+scheduleName+",  "+"dbmsPartCode:"+dbmsPartCode+",  " +
					"assetGroupCode:"+assetGroupCode+",  "+"assetTypeCode:"+assetTypeCode+",  " +
					"engineTypeCode:"+engineTypeCode+",  "+"engineHours:"+engineHours+",  " +"days:"+days);
		
		long startTime = System.currentTimeMillis();
		response = new MasterServiceScheduleImpl().setMasterServiceScheduleDetails(servicePlan,serviceName, scheduleName, 
																	dbmsPartCode, assetGroupCode, assetTypeCode, engineTypeCode, engineHours, days);
		long endTime = System.currentTimeMillis();
		iLogger.info("Webservice Execution Time in ms:"+(endTime-startTime));
		
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response+"");
		
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
			if(servicePlan==null)
				servicePlan=""; 
			if(serviceName==null)
				 serviceName="";
			 if(scheduleName==null)
				 scheduleName="";
			 if(dbmsPartCode==null)
				 dbmsPartCode="";
			 if(assetGroupCode==null)
				 assetGroupCode="";
			 if(assetTypeCode==null)
				 assetTypeCode="";
			 if(engineTypeCode==null)
				 engineTypeCode="";
			 if(engineHours==null)
				 engineHours="";
			 if(days==null)
				 days="";
			 
			String messageString = servicePlan+"|"+serviceName+"|"+scheduleName+"|"+dbmsPartCode+"|"+assetGroupCode+"|"+assetTypeCode+"|"+engineTypeCode+"|"+engineHours+"|"+days;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode,faultCause);
		}
		
		return response;
	}}
