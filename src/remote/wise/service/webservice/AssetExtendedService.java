package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetExtendedReqContract;
import remote.wise.service.datacontract.AssetExtendedRespContract;
import remote.wise.service.datacontract.FotaUpdateReqContract;
import remote.wise.service.datacontract.FotaUpdateRespContract;
import remote.wise.service.implementation.AssetExtendedImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "AssetExtendedService")
public class AssetExtendedService {

	@WebMethod(operationName = "GetAssetExtendedDetails", action = "GetAssetExtendedDetails")
	public AssetExtendedRespContract getAssetExtendedDetails(@WebParam(name="reqObj" ) AssetExtendedReqContract reqObj) throws CustomFault
	{ 

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetExtendedService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("SerialNumber:"+reqObj.getSerialNumber()+"");
		
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		//DF20180924 :: MA369757 :: security check for fields.
		isValidinput = util.inputFieldValidation(reqObj.getSerialNumber());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		AssetExtendedRespContract response = new AssetExtendedImpl().getAssetExtendedDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("DriverContactNumber:"+response.getDriverContactNumber()+" , "+" DriverName:"+response.getDriverName()+" , "+" , Notes:"+response.getNotes()+" , "+" , Offset:"+response.getOffset()+" , "+"  OperatingEndTime:"+response.getOperatingEndTime()+" , "+" OperatingStartTime:"+response.getOperatingStartTime()+" , "+" PrimaryOwnerId:"+response.getPrimaryOwnerId()+" , "+" SerialNumber():"+response.getSerialNumber()+" , "+" UsageCategory():"+response.getUsageCategory()+" ,  " +"FWVersionNumber:" +response.getFWVersionNumber()+",");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetExtendedService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;

	}	

	@WebMethod(operationName = "SetAssetExtendedDetails", action = "SetAssetExtendedDetails")
	public String setAssetExtendedDetails(@WebParam(name="reqObj" ) AssetExtendedRespContract reqObj) throws CustomFault
	{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetExtendedService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("DriverContactNumber:"+reqObj.getDriverContactNumber()+" , "+" DriverName:"+reqObj.getDriverName()+" , "+" , " +
				" Notes:"+reqObj.getNotes()+" , "+" , Offset:"+reqObj.getOffset()+" , "+"  OperatingEndTime:"+reqObj.getOperatingEndTime()+" , "+" " +
						"OperatingStartTime:"+reqObj.getOperatingStartTime()+" , "+" PrimaryOwnerId:"+reqObj.getPrimaryOwnerId()+" , "+" " +
						//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on RollOff with status of machine as Normal
						"SerialNumber():"+reqObj.getSerialNumber()+" , "+" UsageCategory():"+reqObj.getUsageCategory()+" , "+" cmhLoginId():"+reqObj.getCmhLoginId()+"  "+"Device Status:"+reqObj.getDeviceStatus());
		
		//DF20181015 - KO369761 - Extracting CSRF Token from driver name field.
		CommonUtil util = new CommonUtil();
		String loginId = null;
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getDriverName().split("\\|").length > 1){
			csrfToken=reqObj.getDriverName().split("\\|")[1];
			reqObj.setDriverName(reqObj.getDriverName().split("\\|")[0]);
		}

		//DF20181015 - KO369761 - Extracting login id from contact number field.
		if(reqObj.getDriverContactNumber().split("\\|").length > 1){
			loginId=reqObj.getDriverContactNumber().split("\\|")[1];
			reqObj.setDriverContactNumber(reqObj.getDriverContactNumber().split("\\|")[0]);
		}

		//DF20181015 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
		if(!isValidCSRF){
			iLogger.info("setAssetExtendedDetails ::  Invalid request.");
			throw new CustomFault("Invalid request.");

		}else{
			//delete the validated token
			util.deleteANTICSRFTOKENS(loginId,csrfToken,"one");
		}
		
		//DF20180713: KO369761 - Security Check added for input text fields.
		String isValidinput=null;

		isValidinput = util.inputFieldValidation(reqObj.getDriverName());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		isValidinput = util.inputFieldValidation(reqObj.getDriverContactNumber());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		isValidinput = util.inputFieldValidation(reqObj.getOffset());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		isValidinput = util.inputFieldValidation(reqObj.getNotes());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
	
		//DF20180925 :: MA369757 :: security checks for all input fields.
		if(reqObj.getOperatingEndTime()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getOperatingEndTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getOperatingStartTime()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getOperatingStartTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		isValidinput = util.inputFieldValidation(reqObj.getPrimaryOwnerId()+"");
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		if(reqObj.getSerialNumber()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getSerialNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getUsageCategory()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getUsageCategory());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}	
		if(reqObj.getDeviceStatus()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getDeviceStatus());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getCmhLoginId());
		reqObj.setCmhLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getCmhLoginId());
		
		String response_msg= new AssetExtendedImpl().setAssetExtendedDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("status:"+response_msg+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetExtendedService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		
		//new changes by santosh
		new AssetExtendedImpl().setAssetMoolDAService(reqObj);
		return response_msg;		
	}
	
	@WebMethod(operationName = "UpdateCHMRDetails", action = "UpdateCHMRDetails")
	public String UpdateCHMRDetails(@WebParam(name="reqObj") AssetExtendedRespContract reqObj ) throws CustomFault
	{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		Logger businessError = BusinessErrorLoggerClass.logger;
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UpdateCHMRDetails:","info");
		
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		infoLogger.info("Entered : UpdateCHMRDetails ");
		
		//DF20180713: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;

		isValidinput = util.inputFieldValidation(reqObj.getDriverName());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		isValidinput = util.inputFieldValidation(reqObj.getDriverContactNumber());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		isValidinput = util.inputFieldValidation(reqObj.getOffset());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		isValidinput = util.inputFieldValidation(reqObj.getNotes());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20180925 :: MA369757 :: security checks for all input fields.
		if(reqObj.getOperatingEndTime()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getOperatingEndTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getOperatingStartTime()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getOperatingStartTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		isValidinput = util.inputFieldValidation(reqObj.getPrimaryOwnerId()+"");
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		if(reqObj.getSerialNumber()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getSerialNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getUsageCategory()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getUsageCategory());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}	
		if(reqObj.getDeviceStatus()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getDeviceStatus());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getCmhLoginId());
		reqObj.setCmhLoginId(UserID);
		infoLogger.info("Decoded userId::"+reqObj.getCmhLoginId());
		
		String response_msg = new AssetExtendedImpl().UpdateCHMRDetails(reqObj);
		
		infoLogger.info("****** OUTPUTS **************\n");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		infoLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		infoLogger.info("serviceName:AssetExtendedService~executionTime:"+(endTime-startTime)+"~"+UserID+"~"+response_msg);
		return response_msg;
	}
}
