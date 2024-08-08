package remote.wise.service.webservice;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.DownloadAempReqContract;
import remote.wise.service.datacontract.DownloadAempRespContract;
import remote.wise.service.implementation.DownloadAempImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;
/**
 *  WebService class to get Download details in Aemp Format for given Serial Number and Period
 * @author jgupta41
 *
 */



@WebService(name = "DownloadAempService")

public class DownloadAempService {
	
	/**
	 * This method Download details in Aemp Format for given Serial Number and Period
	 * @param reqObj:Get and Download details in Aemp Format for given Serial Number and Period
	 * @return respObj:Download details in Aemp Format for given Serial Number and Period
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetDownloadAemp", action = "GetDownloadAemp")
	public List<DownloadAempRespContract> getDownloadAemp(@WebParam(name="reqObj") DownloadAempReqContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("DownloadAempService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//	Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Period:"+reqObj.getPeriod()+","+"serialNumber:"+reqObj.getSerialNumber()+"");
		
		//DF20181015 - KO369761 - Extracting CSRF Token from getPeriod field.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		String loginId = null;
		boolean isValidCSRF = false;
		if(reqObj.getPeriod().split("\\|").length > 1){
			csrfToken=reqObj.getPeriod().split("\\|")[1];
			reqObj.setPeriod(reqObj.getPeriod().split("\\|")[0]);
		}
		
		//Validating VIN Hierarchy
		if(reqObj.getSerialNumber().split("\\|").length > 1){
			int tenancyId= Integer.parseInt(reqObj.getSerialNumber().split("\\|")[1]);
			
			if(reqObj.getSerialNumber().split("\\|").length > 2){
				loginId = reqObj.getSerialNumber().split("\\|")[2];
			}
			
			reqObj.setSerialNumber(reqObj.getSerialNumber().split("\\|")[0]);
			String serialNumber = util.validateVIN(tenancyId,reqObj.getSerialNumber());
			if(serialNumber == null || serialNumber.equalsIgnoreCase("FAILURE")){
				throw new CustomFault("Invalid VIN Number");
			}
		}
		
		//DF20181015 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
		if(!isValidCSRF){
			iLogger.info("getDownloadAemp ::  Invalid request.");
			throw new CustomFault("Invalid request.");

		}
		
		List<DownloadAempRespContract> respObj = new DownloadAempImpl().getDownloadAemp(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0;i<respObj.size();i++){
			iLogger.info("Date:"+respObj.get(i).getDate()+","+"PIN:"+respObj.get(i).getPIN()+"");	
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:DownloadAempService~executionTime:"+(endTime - startTime)+"~"+loginId+"~");
		return respObj;
	}
}
