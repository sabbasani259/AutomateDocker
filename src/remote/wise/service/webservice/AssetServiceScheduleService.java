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
import remote.wise.service.datacontract.AssetServiceScheduleGetReqContract;
import remote.wise.service.datacontract.AssetServiceScheduleGetRespContract;
import remote.wise.service.datacontract.AssetServiceScheduleRespContract;
import remote.wise.service.implementation.AssetServiceScheduleImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;



/**
 * 
 * @author Smitha
 * This method will get details on Scheduling services on machine
 */

@WebService(name = "AssetServiceScheduleService")
public class AssetServiceScheduleService {
	
	
	/**
	 * 
	 * @param reqObj
	 * @return serviceSchedule
	 * @throws CustomFault
	 */	
	@WebMethod(operationName = "GetAssetServiceScheduleService", action = "GetAssetServiceScheduleService")

	public List<AssetServiceScheduleGetRespContract> getAssetServiceScheduleDetails(@WebParam(name="reqObj") AssetServiceScheduleGetReqContract  reqObj) throws CustomFault{
		//Logger infoLogger = Logger.getLogger("infoLogger");
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetServiceScheduleService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("serialNumber:"+reqObj.getSerialNumber()+"");
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;
		
		//Validating VIN Hierarchy
		CommonUtil util = new CommonUtil();
		if(reqObj.getSerialNumber().split("\\|").length > 1){
			int tenancyId= Integer.parseInt(reqObj.getSerialNumber().split("\\|")[1]);

			if(reqObj.getSerialNumber().split("\\|").length > 3){
				loginId = reqObj.getSerialNumber().split("\\|")[2];
				csrfToken = reqObj.getSerialNumber().split("\\|")[3];

				//DF20181015 - KO369761 - Validating the CSRF Token against login id.
				if(csrfToken != null)
					isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
				if(!isValidCSRF){
					iLogger.info("AssetServiceScheduleService ::  Invalid request.");
					throw new CustomFault("Invalid request.");
				}
			}
			
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

		
		List<AssetServiceScheduleGetRespContract> respobj=new AssetServiceScheduleImpl().getAssetserviceSchedule(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respobj.size(); i++)
		{
			iLogger.info("serialNumber:"+respobj.get(i).getSerialNumber()+",  "+"dealerId:"+respobj.get(i).getDealerId()+",   " +
					"serviceScheduleId:"+respobj.get(i).getServiceScheduleId()+",  "+"EventId:"+respobj.get(i).getEventId()+" ,"+"scheduledDate:"+respobj.get(i).getScheduledDate()+",  "+"hoursToNextService:"+respobj.get(i).getHoursToNextService()+", "+" dealerName:"+respobj.get(i).getDealerName()+","+"serviceName:"+respobj.get(i).getServiceName()+","+"scheduleName:"+respobj.get(i).getScheduleName()+","+"engineHoursSchedule:"+respobj.get(i).getEngineHoursSchedule()+","+respobj.get(i).getExtendedWarrantyType());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetServiceScheduleService...respobj "+respobj);
		iLogger.info("serviceName:AssetServiceScheduleService~executionTime:"+(endTime-startTime)+"~"+loginId+"~");
		return respobj;
	}	
	/**
	 * 
	 * @param reqObj
	 * @return serviceSchedule
	 * @throws CustomFault
	 */		
	/*@WebMethod(operationName = "SetAssetServiceScheduleService", action = "SetAssetServiceScheduleService")
	public String setAssetScheduleService(@WebParam(name="setAssetServiceSchedule")AssetServiceScheduleRespContract setAssetServiceSchedule)throws CustomFault{
		Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		infoLogger.info("---- Webservice Input ------");
		infoLogger.info("installationDate:"+setAssetServiceSchedule.getInstallationDate()+","+"serialNumber:"+setAssetServiceSchedule.getSerialNumber()+"");
		String response_msg=new AssetServiceScheduleImpl().setAssetserviceSchedule(setAssetServiceSchedule);
		infoLogger.info("----- Webservice Output-----");
		infoLogger.info("status:"+response_msg+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		infoLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		infoLogger.info("Webservice Execution Time in ms:"+(endTime-startTime));
		return response_msg;
	}*/

}






