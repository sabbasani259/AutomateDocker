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
import remote.wise.service.datacontract.AssetEventLogReqContract;
import remote.wise.service.datacontract.AssetEventRespContract;
import remote.wise.service.implementation.AssetEventLogImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "AssetEventLogService")
public class AssetEventLogService 
{
	
	/** This method returns the Machine Activity log report for a given day
	 * @param reqObj Specifies the machine and the time period
	 * @return Returns the Machine Activity Log details for the given serialNumber for the given period
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetAssetEventLog", action = "GetAssetEventLog")
	public List<AssetEventRespContract> getAssetEventDetails(@WebParam(name="reqObj" )AssetEventLogReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetEventLogService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("SerialNumber:"+reqObj.getSerialNumber()+",  "+"Period:"+reqObj.getPeriod()+",  "+"LoginTenancyId:"+reqObj.getLoginTenancyId());
		
		//DF20181015 - KO369761 - Extracting CSRF Token from driver name field.
		CommonUtil utilObj = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;
		if(reqObj.getSerialNumber().split("\\|").length > 1){
			csrfToken=reqObj.getSerialNumber().split("\\|")[1];
			reqObj.setSerialNumber(reqObj.getSerialNumber().split("\\|")[0]);
		}

		//DF20181015 - KO369761 - Extracting login id from contact number field.
		if(reqObj.getPeriod().split("\\|").length > 1){
			loginId=reqObj.getPeriod().split("\\|")[1];
			reqObj.setPeriod(reqObj.getPeriod().split("\\|")[0]);
		}

		//DF20181015 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=utilObj.validateANTICSRFTOKEN(loginId,csrfToken);
		if(!isValidCSRF){
			iLogger.info("AssetEventLogService ::  Invalid request.");
			throw new CustomFault("Invalid request.");

		}
		
		//DF20181005 ::: MA369757 :: Security checks for all input fields	
		String isValidinput=null;
		if(reqObj.getSerialNumber()!=null){
			isValidinput = utilObj.inputFieldValidation(reqObj.getSerialNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getPeriod()!=null){
			isValidinput = utilObj.inputFieldValidation(reqObj.getPeriod());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		
		//DF20180806:KO369761 - Validating VIN hierarchy against login id/tenancy id
		String serialNum = utilObj.validateVIN(reqObj.getLoginTenancyId(), reqObj.getSerialNumber());
		if(serialNum == null || serialNum.equalsIgnoreCase("FAILURE")){
			throw new CustomFault("Invalid VIN Number");
		}
		
		List<AssetEventRespContract> respObj = new AssetEventLogImpl().getAssetEvents(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respObj.size(); i++)
		{
			/*iLogger.info("SequenceId:"+respObj.get(i).getSequenceId()+",  "+"TransactionTime:"+respObj.get(i).getTransactionTime()+",   " +
					"ParameterName:"+respObj.get(i).getParameterName()+",  "+"ParameterValue:"+respObj.get(i).getParameterValue()+",  "+
					"latitude:"+respObj.get(i).getLatitude()+",  "+"longitude:"+respObj.get(i).getLongitude()+",  "+
					"alertSeverity:"+respObj.get(i).getAlertSeverity());*/
		
			// DF20181005 ::: MA369757 :: Security checks for all input fields
			if (respObj.get(i).getTransactionTime() != null) {
				isValidinput = utilObj.responseValidation(respObj.get(i)
						.getTransactionTime());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			}
			if (respObj.get(i).getParameterName() != null) {
				isValidinput = utilObj.responseValidation(respObj.get(i)
						.getParameterName());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			}
			if (respObj.get(i).getParameterValue() != null) {
				isValidinput = utilObj.responseValidation(respObj.get(i)
						.getParameterValue());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			}
			if (respObj.get(i).getLatitude() != null) {
				isValidinput = utilObj.responseValidation(respObj.get(i)
						.getLatitude());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			}
			if (respObj.get(i).getLongitude() != null) {
				isValidinput = utilObj.responseValidation(respObj.get(i)
						.getLongitude());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
			}
			if (respObj.get(i).getAlertSeverity() != null) {
				isValidinput = utilObj.responseValidation(respObj.get(i)
						.getAlertSeverity());
				if (!isValidinput.equals("SUCCESS")) {
					throw new CustomFault(isValidinput);
				}
		}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetEventLogService~executionTime:"+(endTime-startTime)+"~"+loginId+"~");
		return respObj;
	}
}
