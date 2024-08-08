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
import remote.wise.service.datacontract.MachineHealthDetailsReqContract;
import remote.wise.service.datacontract.MachineHealthDetailsRespContract;
import remote.wise.service.implementation.MachineHealthDetailsImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;


/** WebService that returns the current health parameters of the machine
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "MachineHealthDetailsService")
public class MachineHealthDetailsService 
{
	/** WebMethod to return the current health status of a VIN
	 * @param reqObj VIN is specified through this reqObj
	 * @return Returns the machine health details
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetMachineHealthDetails", action = "GetMachineHealthDetails")
	public List<MachineHealthDetailsRespContract> getMachineHealthDetails(@WebParam(name="reqObj" ) MachineHealthDetailsReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineHealthDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Serial Number:"+reqObj.getSerialNumber()+",  "+"loginId:"+reqObj.getLoginId());
		
		//DF20181015 - KO369761 - Extracting CSRF Token from login field.
		CommonUtil utilObj = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}

		//DF20181015 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=utilObj.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("getMachineHealthDetails ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}

		//DF20170919 @Roopa getting decoded UserId
		String UserID=utilObj.getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
		
		//DF20181005 ::: MA369757 :: Security checks for all input fields		
		String isValidinput=null;
		isValidinput = utilObj.inputFieldValidation(reqObj.getSerialNumber());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		//DF20180806:KO369761 - Validating VIN hierarchy against login id/tenancy id
		int tenancyId = utilObj.getTenancyIdFromLoginId(UserID);
		String serialNumber = utilObj.validateVIN(tenancyId, reqObj.getSerialNumber());
		if(serialNumber == null || serialNumber.equalsIgnoreCase("FAILURE")){
			throw new CustomFault("Invalid VIN Number");
		}
				
		List<MachineHealthDetailsRespContract> response = new MachineHealthDetailsImpl().getMachineHealthDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("parameterId:"+response.get(i).getParameterId()+",  "+"parameterName:"+response.get(i).getParameterName()+",   " +
					"parameterValue:"+response.get(i).getParameterValue()+",  "+"recordType:"+response.get(i).getRecordType());
			
			//DF20181005 ::: MA369757 :: Security checks for all input fields		
			if(response.get(i).getParameterName()!=null){
				isValidinput = utilObj.responseValidation(response.get(i).getParameterName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getParameterValue()!=null){
				isValidinput = utilObj.responseValidation(response.get(i).getParameterValue());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getRecordType()!=null){
				isValidinput = utilObj.responseValidation(response.get(i).getRecordType());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineHealthDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}
}
