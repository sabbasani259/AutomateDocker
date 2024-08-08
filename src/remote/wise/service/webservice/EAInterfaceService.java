/**
 * 
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.EAInterfaceDetailReqContract;
import remote.wise.service.datacontract.EAInterfaceDetailRespContract;
import remote.wise.service.datacontract.EAInterfaceReqContract;
import remote.wise.service.datacontract.EAInterfaceRespContract;
import remote.wise.service.implementation.EAInterfaceImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
@WebService(name = "EAInterfaceService")
public class EAInterfaceService {
	/** This method returns InterfaceFileName details for a given day
	 * @param reqObj Specifies the searchDate and SerialNumber
	 * @return Returns the InterfaceFileName details for the given serialNumber for the given period
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetEASummaryData", action = "GetEASummaryData")
	public List<EAInterfaceRespContract> getSummaryData(@WebParam(name="reqObj" )EAInterfaceReqContract reqObj) throws CustomFault
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("EAInterfaceService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("SerialNumber:"+reqObj.getSerialNumber()+",  "+"Search Date:"+reqObj.getSearchDate());
		
		//DF20181015 Avinash Xavier : CSRF Token Validation ---Start---.
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;
		CommonUtil util = new CommonUtil();

		if(reqObj.getSearchDate() != null){
			if(reqObj.getSearchDate().split("\\|").length >1){
				csrfToken = reqObj.getSearchDate().split("\\|")[1];
				reqObj.setSearchDate(reqObj.getSearchDate().split("\\|")[0]);
			}
		}

		if(reqObj.getSerialNumber() != null){
			if(reqObj.getSerialNumber().split("\\|").length >1){
				loginId = reqObj.getSerialNumber().split("\\|")[1];
				reqObj.setSerialNumber(reqObj.getSerialNumber().split("\\|")[0]);
			}
		}

		// DF20190305 : Z1007653 - Adding a condition, if request is not from an
		// InternalSource - do validation and checks
		if (!csrfToken.equals("InternalSource")) {
			if (csrfToken != null) {
				isValidCSRF = util.validateANTICSRFTOKEN(loginId, csrfToken);
			}
			iLogger.info("EAInterfaceService :: getSummaryData ::   csrftoken isValidCSRF :: "
					+ isValidCSRF);
			if (!isValidCSRF) {
				iLogger.info("EAInterfaceService :: getSummaryData ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
			// DF20181015 Avinash Xavier CsrfToken Validation ---End----.
		}
		
		//DF20180713: KO369761 - Security Check added for input text fields.
		String isValidinput=null;
		isValidinput = util.inputFieldValidation(reqObj.getSerialNumber());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(reqObj.getSearchDate());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		List<EAInterfaceRespContract> respObj = new EAInterfaceImpl().getEASummaryData(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respObj.size(); i++)
		{
			iLogger.info("Interface File Name: "+respObj.get(i).getInterfaceFileName()+",  "+"ProcessedRecord: "+respObj.get(i).getProcessedRecord()+",   " +
					" RejectedRecord: "+respObj.get(i).getRejectedRecord()+", Interface Name: "+respObj.get(i).getInterfaceName()+",  " +
					" Status: "+respObj.get(i).getStatus()+", Reason for Rejection: "+respObj.get(i).getReasonForRejection()+", " +
					" fileName: "+respObj.get(i).getFileName());
			
			//DF20181008-KO369761-XSS validation of output response contract
			isValidinput = util.inputFieldValidation(respObj.get(i).getFileName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(respObj.get(i).getInterfaceFileName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(respObj.get(i).getInterfaceName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(respObj.get(i).getReasonForRejection());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(respObj.get(i).getStatus());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(respObj.get(i).getProcessedRecord()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(respObj.get(i).getRejectedRecord()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:EAInterfaceService~executionTime:"+(endTime-startTime)+"~"+loginId+"~");
		return respObj;
	}
	
	//DF20140813 - Suprava Nayak - Web based search for EA File Processing Status
	/** This method returns InterfaceFileName details for a given fileName
	 * @param reqObj Specifies the fileName
	 * @return Returns the InterfaceFileName details for the given filename
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetEADetailData", action = "GetEADetailData")
	public List<EAInterfaceDetailRespContract> getDetailData(@WebParam(name="reqObj" )EAInterfaceDetailReqContract reqObj) throws CustomFault
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("EAInterfaceService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("FileName:"+reqObj.getFileName());
		
		//DF20181008 - XSS validation for Security Fixes.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;

		isValidinput = util.inputFieldValidation(reqObj.getFileName());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		List<EAInterfaceDetailRespContract> respObj = new EAInterfaceImpl().getEADetailData(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respObj.size(); i++)
		{
			iLogger.info("Record: "+respObj.get(i).getRecord()+",  "+"ReProcessCount: "+respObj.get(i).getReProcessCount()+",   " +
					" Failure for Rejection: "+respObj.get(i).getFailureForRejection());
			
			//DF20181008-KO369761-XSS validation of output response contract
			isValidinput = util.inputFieldValidation(respObj.get(i).getRecord());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(respObj.get(i).getFailureForRejection());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(respObj.get(i).getReProcessCount()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:EAInterfaceService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}
}
