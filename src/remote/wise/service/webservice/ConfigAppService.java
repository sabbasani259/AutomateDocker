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
import remote.wise.service.datacontract.ConfigAppReqContract;
import remote.wise.service.datacontract.ConfigAppRespContract;
import remote.wise.service.implementation.ConfigAppImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;


/**
 * 
 * @author Smitha  
 */
@WebService(name = "ConfigAppService")
public class ConfigAppService {

	/**
	 * This method will get details whether SMS and Map Service are been enabled for a user or not
	 * @param reqObj
	 * @return respobj
	 * @throws CustomFault
	 */	
	@WebMethod(operationName = "GetConfigAppService", action = "GetConfigAppService")

	public List<ConfigAppRespContract> getConfigAppService(@WebParam(name="reqObj") ConfigAppReqContract  reqObj) throws CustomFault{
		

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ConfigAppService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("configuration_id:"+reqObj.getConfiguration_id()+"");
		
		//DF20181008-KO369761-XSS validation of input request contract
		CommonUtil util = new CommonUtil();
		String isValidinput=null;

		isValidinput = util.inputFieldValidation(String.valueOf(reqObj.getConfiguration_id()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		List<ConfigAppRespContract> respobj=new ConfigAppImpl().getConfigAppSettings(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respobj.size(); i++){
			iLogger.info("configuration_id:"+respobj.get(i).getConfiguration_id()+","+"services:"+respobj.get(i).getServices()+","+"isStatus:"+respobj.get(i).isStatus()+","+"modifiedBy:"+respobj.get(i).getModifiedBy()+","+"modifiedOn:"+respobj.get(i).getModifiedOn()+"");
			
			isValidinput = util.inputFieldValidation(String.valueOf(respobj.get(i).getConfiguration_id()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(respobj.get(i).getModifiedBy());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(respobj.get(i).getModifiedOn());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(respobj.get(i).getServices());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(respobj.get(i).isStatus()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ConfigAppService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respobj;
	}	
	/**
	 * This method sets the SMS and Map Service for a user 
	 * @param reqObj
	 * @return respobj
	 * @throws CustomFault
	 */	
	@WebMethod(operationName = "SetConfigAppService", action = "SetConfigAppService")
	public String setConfigAppService(@WebParam(name="reqObj")List<ConfigAppRespContract> reqObjList)  throws CustomFault
	{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ConfigAppService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//DF20181008-KO369761-XSS validation of input request contract
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		
		//DF20181015 - KO369761 - Extracting CSRF Token & login id from edited by field.
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;

		if(reqObjList.size() > 0 ){
			ConfigAppRespContract respObj = reqObjList.get(0);

			if(respObj.getModifiedBy().split("\\|").length > 2){
				loginId = respObj.getModifiedBy().split("\\|")[1];
				csrfToken = respObj.getModifiedBy().split("\\|")[2];
			}

			//DF20181015 - KO369761 - Validating the CSRF Token against login id.
			if(csrfToken != null)
				isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
			if(!isValidCSRF){
				iLogger.info("setConfigAppService ::  Invalid request.");
				throw new CustomFault("Invalid request.");

			}else{
				//delete the validated token
				util.deleteANTICSRFTOKENS(loginId,csrfToken,"one");
			}
		}
		
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		for(int i=0; i<reqObjList.size();i++){
			iLogger.info("configuration_id:"+reqObjList.get(i).getConfiguration_id()+","+"services:"+reqObjList.get(i).getServices()+","+"isStatus:"+reqObjList.get(i).isStatus()+","+"modifiedBy:"+reqObjList.get(i).getModifiedBy()+","+"modifiedOn:"+reqObjList.get(i).getModifiedOn()+"");
			
			if(reqObjList.get(i).getModifiedBy().split("\\|").length > 1){
				reqObjList.get(i).setModifiedBy(reqObjList.get(i).getModifiedBy().split("\\|")[0]);
			}
			
			isValidinput = util.inputFieldValidation(reqObjList.get(i).getModifiedBy());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(reqObjList.get(i).getModifiedOn());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(reqObjList.get(i).getServices());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(reqObjList.get(i).getConfiguration_id()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(reqObjList.get(i).isStatus()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		String response_msg= new ConfigAppImpl().setConfigAppSettings(reqObjList);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response_msg+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ConfigAppService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response_msg);
		return response_msg;
	}	
}
