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
import remote.wise.service.datacontract.UtilizationDetailServiceReqContract;
import remote.wise.service.datacontract.UtilizationDetailServiceRespContract;
import remote.wise.service.implementation.UtilizationDetailServiceImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;


/** Webservice that provides the utilization details of machine
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "UtilizationDetailService")
public class UtilizationDetailService 
{
	/** Webservice method that returns the list of period with the machine utilization details
	 * @param reqObj serialNumber for which utilization details to be returned
	 * @return Returns the list of periods and the machine utilization details for that period
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetUtilizationDetailService", action = "GetUtilizationDetailService")
	public List<UtilizationDetailServiceRespContract> getUtilizationDetailService(@WebParam(name="reqObj" ) UtilizationDetailServiceReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UtilizationDetailService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("<---- Webservice Input ------>");
		iLogger.info("Serial Number:"+reqObj.getSerialNumber()+",  "+"loginId:"+reqObj.getLoginId()+",  "+"period:"+reqObj.getPeriod());
		
		//DF20181015 - KO369761 - Extracting CSRF Token from login field.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}

		//DF20181015 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("getUtilizationDetailService ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		//DF20170919 @Roopa getting decoded UserId
		CommonUtil utilObj = new CommonUtil();
		//DF20170919 @Roopa getting decoded UserId
		String UserID=utilObj.getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
		
		//DF20180806:KO369761 - Validating VIN hierarchy against login id/tenancy id
		int tenancyId = utilObj.getTenancyIdFromLoginId(UserID);
		String serialNumber = utilObj.validateVIN(tenancyId, reqObj.getSerialNumber());
		if(serialNumber == null || serialNumber.equalsIgnoreCase("FAILURE")){
			throw new CustomFault("Invalid VIN Number");
		}
				
		List<UtilizationDetailServiceRespContract> response = new UtilizationDetailServiceImpl().getUtilizationDetailService(reqObj);
		iLogger.info("<----- Webservice Output----->");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info(i+"   ROW");
			iLogger.info("Serial Number:"+response.get(i).getSerialNumber()+",  "+"period:"+response.get(i).getPeriod()+",   " +
					"timeDuration:"+response.get(i).getTimeDuration()+",  "+"workingHourPerct:"+response.get(i).getWorkingHourPerct()+",  "+
					"timeMachineStatusMap:"+response.get(i).getTimeMachineStatusMap());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UtilizationDetailService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response;
	}
}
