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
import remote.wise.service.datacontract.MachineProfileReqContract;
import remote.wise.service.datacontract.MachineProfileRespContract;
import remote.wise.service.implementation.MachineProfileImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 * 
 * This Service gets and set's the MachineProfile
 *
 */
@WebService(name = "MachineProfileService")
public class MachineProfileService {
	
	/**
	 * This service gets the details based on the machine
	 * @param reqObj is passed to optain the machine profile
	 * @return listOfResponse gets the list as a machine's profile
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetMachineProfileService", action = "GetMachineProfileService")
	public List<MachineProfileRespContract> getMachineProfileService(@WebParam(name = "reqObj") MachineProfileReqContract reqObj)throws CustomFault {

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineProfileService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginID:"+reqObj.getLoginId()+","+"assetGroupId:"+reqObj.getAssetGroupId()+"");
		
		//DF20181015 - KO369761 - Extracting CSRF Token from login field.
		CommonUtil utilObj = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);

			//DF20181011 - KO369761 - Validating the CSRF Token against login id.
			if(csrfToken != null)
				isValidCSRF=utilObj.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
			if(!isValidCSRF){
				iLogger.info("Machine Profile Service :: Details ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
		}
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
				reqObj.setLoginId(UserID);
				iLogger.info("Decoded userId::"+reqObj.getLoginId());
				
		List<MachineProfileRespContract> listOfResponse = new MachineProfileImpl().getMachineProfile(reqObj);
		iLogger.info("----- Webservice Output-----");
		String isValidinput=null;
		CommonUtil util = new CommonUtil();
		for(int i=0; i<listOfResponse.size(); i++){
			iLogger.info("assetGroupId:"+listOfResponse.get(i).getAssetGroupId()+","+"asseetOperatingStartTime:"+listOfResponse.get(i).getAsseetOperatingStartTime()+","+"asseetOperatingEndTime:"+listOfResponse.get(i).getAsseetOperatingEndTime()+","+"profileName:"+listOfResponse.get(i).getProfileName()+","+"assetGroupCode:"+listOfResponse.get(i).getAssetGroupCode()+"");

			//DF20180925 ::: MA369757 :: Security checks for all input fields		
			if(listOfResponse.get(i).getAsseetOperatingStartTime()!=null){
				isValidinput = util.inputFieldValidation(listOfResponse.get(i).getAsseetOperatingStartTime());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(listOfResponse.get(i).getAsseetOperatingEndTime()!=null){
				isValidinput = util.inputFieldValidation(listOfResponse.get(i).getAsseetOperatingEndTime());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(listOfResponse.get(i).getProfileName()!=null){
				isValidinput = util.inputFieldValidation(listOfResponse.get(i).getProfileName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(listOfResponse.get(i).getAssetGroupCode()!=null){
				isValidinput = util.inputFieldValidation(listOfResponse.get(i).getAssetGroupCode());
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
		iLogger.info("serviceName:MachineProfileService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return listOfResponse;
	}

	/**
	 * 
	 * @param respObj provided as input to set a profile of the asset
	 * @return machineProfileResponse sets the machines profile based on the respObj that was passed
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetMachineProfileService", action = "SetMachineProfileService")
	public String setMachineProfileService(@WebParam(name = "respObj") MachineProfileRespContract respObj)throws CustomFault {

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineProfileService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("assetGroupId:"+respObj.getAssetGroupId()+","+"asseetOperatingStartTime:"+respObj.getAsseetOperatingStartTime()+","+"asseetOperatingEndTime:"+respObj.getAsseetOperatingEndTime()+","+"profileName:"+respObj.getProfileName()+","+"assetGroupCode:"+respObj.getAssetGroupCode()+"");
		
		CommonUtil utilObj = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;
		//DF20181015 - KO369761 - Extracting CSRF Token from profile name field.
		if(respObj.getProfileName().split("\\|").length > 2){
			loginId = respObj.getProfileName().split("\\|")[1];
			csrfToken = respObj.getProfileName().split("\\|")[2];
			respObj.setProfileName(respObj.getProfileName().split("\\|")[0]);

			//DF20181011 - KO369761 - Validating the CSRF Token against login id.
			if(csrfToken != null)
				isValidCSRF=utilObj.validateANTICSRFTOKEN(loginId,csrfToken);
			if(!isValidCSRF){
				iLogger.info("Machine Profile Service :: Details ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}else{
				utilObj.deleteANTICSRFTOKENS(loginId, csrfToken, "one");
			}
		}
		
		//text field validation to avoid SQL Injection
		String isValidinput = utilObj.inputFieldValidation(respObj.getAssetGroupCode());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		isValidinput = utilObj.inputFieldValidation(respObj.getProfileName());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20180925 ::: MA369757 :: Security checks for all input fields	
		if(respObj.getAsseetOperatingStartTime()!=null){
			isValidinput = utilObj.inputFieldValidation(respObj.getAsseetOperatingStartTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(respObj.getAsseetOperatingEndTime()!=null){
			isValidinput = utilObj.inputFieldValidation(respObj.getAsseetOperatingEndTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		
		String machineProfileResponse = new MachineProfileImpl().setMachineProfileService(respObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+machineProfileResponse+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineProfileService~executionTime:"+(endTime-startTime)+"~"+loginId+"~"+machineProfileResponse);
		return machineProfileResponse;
	}
}
