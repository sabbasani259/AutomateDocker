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
import remote.wise.service.datacontract.FuelUtilizationDetailReqContract;
import remote.wise.service.datacontract.FuelUtilizationDetailRespContract;
import remote.wise.service.implementation.FuelUtilizationDetailImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 *  WebService class to get Fuel Utilization Details
 * @author jgupta41
 *
 */

@WebService(name = "FuelUtilizationDetailService")
public class FuelUtilizationDetailService {
	
	/**
	 * 	This method gets Fuel Utilization Details that belongs specified LoginId and SerialNumber
	 * @param reqObj:Get Fuel Utilization Details for specified LoginId and SerialNumber
	 * @return respObj:Returns List of Fuel Utilization Details 
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetFuelUtilizationDetail", action = "GetFuelUtilizationDetail")
	public List<FuelUtilizationDetailRespContract> getFuelUtilizationDetail(@WebParam(name="ReqObj") FuelUtilizationDetailReqContract reqObj ) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("FuelUtilizationDetailService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+reqObj.getLoginId()+","+"serialNumber:"+reqObj.getSerialNumber()+","+"period:"+reqObj.getPeriod()+"");
		
		//DF20170919 @Roopa getting decoded UserId
		CommonUtil utilObj = new CommonUtil();
		//DF20170919 @Roopa getting decoded UserId
		String UserID= utilObj.getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());

		//DF20180806:KO369761 - Validating VIN hierarchy against login id/tenancy id
		int tenancyId = utilObj.getTenancyIdFromLoginId(UserID);
		String serialNumber = utilObj.validateVIN(tenancyId, reqObj.getSerialNumber());
		if(serialNumber == null || serialNumber.equalsIgnoreCase("FAILURE")){
			throw new CustomFault("Invalid VIN Number");
		}
		
		//DF20180927 :: adding security checks for all fields.
		String isValidinput=null;
		isValidinput = utilObj.inputFieldValidation(reqObj.getSerialNumber());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(reqObj.getPeriod());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
				
		List<FuelUtilizationDetailRespContract> respObj = new FuelUtilizationDetailImpl().getFuelUtilizationDetail(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respObj.size(); i++){
//			iLogger.info("serialNumber:"+respObj.get(i).getSerialNumber()+","+"period:"+respObj.get(i).getPeriod()+","+"timeDuration:"+respObj.get(i).getTimeDuration()+","+"fuelLevel:"+respObj.get(i).getFuelLevel()+",");
			iLogger.info("serialNumber:"+respObj.get(i).getSerialNumber()+","+"period:"+respObj.get(i).getPeriod()+","+"hourFuelLevelMap:"+respObj.get(i).getHourFuelLevelMap()+",");
			
			isValidinput = utilObj.inputFieldValidation(respObj.get(i).getSerialNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.inputFieldValidation(respObj.get(i).getPeriod());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:FuelUtilizationDetailService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return respObj;
	}

}
