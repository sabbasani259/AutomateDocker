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
import remote.wise.service.datacontract.LandmarkCategoryReqContract;
import remote.wise.service.datacontract.LandmarkCategoryRespContract;

import remote.wise.service.implementation.LandmarkCategoryImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 * WebService class to get and set Landmark Category
 * @author jgupta41
 *
 */


@WebService(name = "LandmarkCategoryService")
public class LandmarkCategoryService {
	
	
	/**
	 * This method gets all landmark category that belongs specified Tenancy ID and Login Id
	 * @param reqObj : Get all landmark category for a given Tenancy ID
	 * @return respObj :Returns list of landmark categories attached for the Tenancy ID  defined. 
	 * @throws CustomFault 
	 */
	@WebMethod(operationName = "GetLandmarkCategory", action = "GetLandmarkCategory")
	public List<LandmarkCategoryRespContract> getLandmarkCategory(@WebParam(name="reqObj") LandmarkCategoryReqContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("LandmarkCategoryService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("login_id:"+reqObj.getLogin_id()+","+"Tenancy_ID:"+reqObj.getTenancy_ID()+"");
		
		//DF20181008 - XSS validation of input for Security Fixes.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		
		// DF20181023 Avinash Xavier : CSRF Token Validation ---Start---.
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;

		if (reqObj.getLogin_id() != null) {
			if (reqObj.getLogin_id().split("\\|").length > 1) {
				csrfToken = reqObj.getLogin_id().split("\\|")[1];
				loginId = reqObj.getLogin_id().split("\\|")[0];
				reqObj.setLogin_id(reqObj.getLogin_id().split("\\|")[0]);
			}
		}

		if (csrfToken != null) {
			isValidCSRF = util.validateANTICSRFTOKEN(loginId, csrfToken);
			iLogger.info("getLandmarkCategory ::   csrftoken isValidCSRF :: "
					+ isValidCSRF);
			if (!isValidCSRF) {
				iLogger.info("getLandmarkCategory ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
		}

		// DF20181023 Avinash Xavier CsrfToken Validation ---End----.

		isValidinput = util.inputFieldValidation(String.valueOf(reqObj.getTenancy_ID()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLogin_id());
		reqObj.setLogin_id(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLogin_id());
		
		List<LandmarkCategoryRespContract> respObj = new LandmarkCategoryImpl().getLandmarkCategory(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respObj.size(); i++){
			iLogger.info("login_id:"+respObj.get(i).getLogin_id()+","+"Landmark_Category_ID:"+respObj.get(i).getLandmark_Category_ID()+","+"Landmark_Category_Name:"+respObj.get(i).getLandmark_Category_Name()+","+"Color_Code:"+respObj.get(i).getColor_Code()+","+"Landmark_Category_Color_Code:"+respObj.get(i).getLandmark_Category_Color_Code()+"");
			
			//DF20181008-KO369761-XSS validation of output response contract
			isValidinput = util.inputFieldValidation(respObj.get(i).getColor_Code());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(respObj.get(i).getLandmark_Category_Color_Code());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(respObj.get(i).getLandmark_Category_Name());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(respObj.get(i).getLandmark_Category_ID()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(respObj.get(i).getTenancy_ID()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:LandmarkCategoryService~executionTime:"+(endTime-startTime)+"~"+loginId+"~");
		return respObj;
	}


	/**
	 * This method sets Color Code that belongs specified Landmark Category Name and Tenancy ID
	 * @param setLandmarkCategory:Get Landmark Category and set Color Code to the corresponding to it
	 * @return response_msg:Return the status String as either Success/Failure.
	 * @throws CustomFault
	 */

	@WebMethod(operationName = "SetLandmarkCategory", action = "SetLandmarkCategory")
	public String setLandmarkCategory(@WebParam(name = "reqObj") LandmarkCategoryRespContract reqObj)  throws CustomFault
	{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("LandmarkCategoryService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("login_id:"+reqObj.getLogin_id()+","+"Landmark_Category_ID:"+reqObj.getLandmark_Category_ID()+","+"Landmark_Category_Name:"+reqObj.getLandmark_Category_Name()+","+"Color_Code:"+reqObj.getColor_Code()+","+"Landmark_Category_Color_Code:"+reqObj.getLandmark_Category_Color_Code()+","+"Tenancy_ID:"+reqObj.getTenancy_ID()+"");
		
		//DF20181008-KO369761-XSS validation of input request contract
		CommonUtil util = new CommonUtil();
		String isValidinput=null;

		// DF20181023 Avinash Xavier : CSRF Token Validation ---Start---.
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;

		if (reqObj.getLogin_id() != null) {
			if (reqObj.getLogin_id().split("\\|").length > 1) {
				csrfToken = reqObj.getLogin_id().split("\\|")[1];
				loginId = reqObj.getLogin_id().split("\\|")[0];
				reqObj.setLogin_id(reqObj.getLogin_id().split("\\|")[0]);
			}
		}

		if (csrfToken != null) {
			isValidCSRF = util.validateANTICSRFTOKEN(loginId, csrfToken);
			iLogger.info("setLandmarkCategory ::   csrftoken isValidCSRF :: "
					+ isValidCSRF);
			if (!isValidCSRF) {
				iLogger.info("setLandmarkCategory ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			} else {

				util.deleteANTICSRFTOKENS(loginId, csrfToken, "one");
			}

		}

		// DF20181023 Avinash Xavier CsrfToken Validation ---End----.

		isValidinput = util.inputFieldValidation(reqObj.getColor_Code());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(reqObj.getLandmark_Category_Color_Code());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(reqObj.getLandmark_Category_Name());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(String.valueOf(reqObj.getLandmark_Category_ID()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(String.valueOf(reqObj.getTenancy_ID()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLogin_id());
		reqObj.setLogin_id(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLogin_id());
		
		String response_msg= new LandmarkCategoryImpl().setLandmarkCategory(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response_msg+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:LandmarkCategoryService~executionTime:"+(endTime-startTime)+"~"+loginId+"~"+response_msg);
		return response_msg;
	}

}
