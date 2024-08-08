package remote.wise.service.webservice;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AdminAlertPrefReqContract;
import remote.wise.service.datacontract.AdminAlertPrefRespContract;
import remote.wise.service.datacontract.TenancyCreationReqContract;
import remote.wise.service.datacontract.TenancyDetailsReqContract;
import remote.wise.service.datacontract.TenancyDetailsRespContract;
import remote.wise.service.implementation.AdminAlertPrefImpl;
import remote.wise.service.implementation.TenancyDetailsImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;


/** WebService to handle TenancyDetails
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "TenancyDetailsService")
public class TenancyDetailsService 
{
	/** Webmethod to set the tenancy details
	 * @param reqObj Data to create a tenancy and set the details
	 * @return Returns the status String
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "setTenancyDetails", action = "setTenancyDetails")
	public String setTenancyDetails(@WebParam(name="reqObj" ) TenancyCreationReqContract reqObj) throws CustomFault, ParseException, IOException
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("TenancyDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+reqObj.getLoginId()+",  "+"accountId:"+reqObj.getAccountId()+",   " +
				"parentTenancyId:"+reqObj.getParentTenancyId()+",  "+"childTenancyId:"+reqObj.getChildTenancyId()+",  "+
				"childTenancyName: "+reqObj.getChildTenancyName()+",  "+"operatingStartTime:"+reqObj.getOperatingStartTime()+",  "+
				"operatingEndTime: "+reqObj.getOperatingEndTime()+",  "+
				"tenancyAdminFirstName: "+reqObj.getTenancyAdminFirstName()+",  "+"tenancyAdminLastName:"+reqObj.getTenancyAdminLastName()+",  "+
				"tenancyAdminEmailId: "+reqObj.getTenancyAdminEmailId()+",  "+"tenancyAdminPhoneNumber:"+reqObj.getTenancyAdminPhoneNumber()+",  "+
				"tenancyAdminRoleId: "+reqObj.getTenancyAdminRoleId()+",  "+"parentTenancyUserIdList:"+reqObj.getParentTenancyUserIdList()+",  "+
				"overrideMachineOperatingHours: "+reqObj.isOverrideMachineOperatingHours()+",  "+"countryCode:"+reqObj.getCountryCode());

		//DF20181029 -MA369757  - Extracting CSRF Token from login field.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}
		//DF20181029 -  - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("TenancyDetailsService :: setTenancyDetails ::  Invalid request.");
			throw new CustomFault("Invalid request.");

		}else
		{
			util.deleteANTICSRFTOKENS(reqObj.getLoginId(),csrfToken,"one");
		}

		//DF20170919 @Roopa getting decoded UserId
		CommonUtil utilObj = new CommonUtil();
		String UserID= utilObj.getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());

		//text field validation to avoid SQL Injection
		String isUserValid = utilObj.inputFieldValidation(reqObj.getChildTenancyName());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		isUserValid = utilObj.inputFieldValidation(reqObj.getOperatingEndTime());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		isUserValid = utilObj.inputFieldValidation(reqObj.getOperatingStartTime());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		isUserValid = utilObj.inputFieldValidation(reqObj.getTenancyAdminEmailId());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		isUserValid = utilObj.inputFieldValidation(reqObj.getTenancyAdminFirstName());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		isUserValid = utilObj.inputFieldValidation(reqObj.getTenancyAdminLastName());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		isUserValid = utilObj.inputFieldValidation(reqObj.getTenancyAdminPhoneNumber());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		isUserValid = utilObj.inputFieldValidation(String.valueOf(reqObj.getAccountId()));
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		isUserValid = utilObj.inputFieldValidation(String.valueOf(reqObj.getChildTenancyId()));
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		isUserValid = utilObj.inputFieldValidation(String.valueOf(reqObj.getParentTenancyId()));
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		ListToStringConversion convObj = new ListToStringConversion();
		String parentTenancyList = convObj.getStringList(reqObj.getParentTenancyUserIdList()).toString();
		isUserValid = utilObj.inputFieldValidation(parentTenancyList);
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		isUserValid = utilObj.inputFieldValidation(String.valueOf(reqObj.getTenancyAdminRoleId()));
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}



		/*//DF20180809:KO369761-Role validation against login id
		if(!utilObj.roleValidationAgainstLoginId(UserID, reqObj.getTenancyAdminRoleId())){
			throw new CustomFault("Unauthorized request.");
		}*/
				
		String response = new TenancyDetailsImpl().createTenancy(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:TenancyDetailsService~executionTime:"+(endTime-startTime)+"~"+UserID+"~"+response);
		return response;
	}


	/** Webmethod to get the tenancy details
	 * DefectId: To be provided by testing team
	 * Rajani Nagaraju - 20130704 - AccountName to be returned from TenancyDetailsService 
	 * @param reqObj Input filters based on which tenancy details are returned
	 * @return Returns the List of tenancy with their details
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetTenancyDetails", action = "GetTenancyDetails")
	public List<TenancyDetailsRespContract> getTenancyDetails(@WebParam(name="reqObj" ) TenancyDetailsReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("TenancyDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+reqObj.getLoginId()+",  "+
				"parentTenancyId:"+reqObj.getParentTenancyIdList()+",  "+"childTenancyId:"+reqObj.getChildTenancyIdList());
		System.out.println("---- Webservice Input ------");
		System.out.println("loginId:"+reqObj.getLoginId()+",  "+
				"parentTenancyId:"+reqObj.getParentTenancyIdList()+",  "+"childTenancyId:"+reqObj.getChildTenancyIdList());
		
		CommonUtil util = new CommonUtil();
		//DF20181025 -  - Extracting CSRF Token and page number from login field.
		String csrfToken = null;
		boolean isValidCSRF = false;
		//DF20190509: Anudeep Immidisetty adding page number to acheive pagination for faster response
		int pageNumber = 1;
		String page = null;
		if(reqObj.getLoginId().split("\\|").length == 2){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			iLogger.info("csrf Token is " + csrfToken);
			System.out.println("csrf Token is " + csrfToken);
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}
		if(reqObj.getLoginId().split("\\|").length > 2){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			page = reqObj.getLoginId().split("\\|")[2];
			iLogger.info("csrf Token is " + csrfToken + "," + " Page Number is " + page);
			System.out.println("csrf Token is " + csrfToken);
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}
		
		//DF20181025 -  - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("TenancyDetailsService :: GetTenancyDetails ::  Invalid request. CSRF Validation failure " + csrfToken);
			throw new CustomFault("Invalid request.");
		}
		//DF20190509: Anudeep Immidisetty adding page number to acheive pagination for faster response
		//DF20190509: Anudeep Immidisetty Validate Page Number
		String isValidinput = null;
		isValidinput = util.inputFieldValidation(page);
		if(!isValidinput.equals("SUCCESS")){
			iLogger.info("TenancyDetailsService :: GetTenancyDetails ::  Invalid request because page number is invalid " + page);
			throw new CustomFault("Invalid Request.");
		}
		
		try{
			if(page!=null)
				pageNumber = Integer.parseInt(page);
		}
		catch(Exception e){
			e.printStackTrace();
			iLogger.info("Exception occured while converting page number " + page);
		}
		
		//DF20181008-KO369761-XSS validation of input request contract
		ListToStringConversion convObj = new ListToStringConversion();
		String childTenancyList = convObj.getIntegerListString(reqObj.getChildTenancyIdList()).toString();
		isValidinput = util.inputFieldValidation(childTenancyList);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		String parentTenancyList = convObj.getIntegerListString(reqObj.getParentTenancyIdList()).toString();
		isValidinput = util.inputFieldValidation(parentTenancyList);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
		
		List<TenancyDetailsRespContract> response = new TenancyDetailsImpl().getTenancyDetails(reqObj,pageNumber);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Output object size " + response.size());
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("tenancyId:"+response.get(i).getTenancyId()+",  "+"tenancyName:"+response.get(i).getTenancyName()+",   " +
					"parentTenancyId:"+response.get(i).getParentTenancyId()+",  "+"parentTenancyName:"+response.get(i).getParentTenancyName()+",  "+
					"tenancyAdminList:"+response.get(i).getTenancyAdminList()+",  " +
					"parentTenancyUserIdEmailId:"+response.get(i).getParentTenancyUserIdMailIdList()+",  "+
					"createdBy: "+response.get(i).getCreatedBy()+",  "+"createdDate:"+response.get(i).getCreatedDate()+",  "+
					"operatingStartTime: "+response.get(i).getOperatingStartTime()+",  "+"operatingEndTime:"+response.get(i).getOperatingEndTime()+",  " +
					//DefectId: To be provided by testing team
					//Rajani Nagaraju - 20130704 - AccountName to be returned from TenancyDetailsService 
					"AccountName:"+response.get(i).getAccountName() +
					"TenancyCode:"+response.get(i).getTenancyCode() +
					",  " + "Size:"+response.get(i).getSize());
			System.out.println("tenancyId:"+response.get(i).getTenancyId()+",  "+"tenancyName:"+response.get(i).getTenancyName()+",   " +
					"parentTenancyId:"+response.get(i).getParentTenancyId()+",  "+"parentTenancyName:"+response.get(i).getParentTenancyName()+",  "+
					"tenancyAdminList:"+response.get(i).getTenancyAdminList()+",  " +
					"parentTenancyUserIdEmailId:"+response.get(i).getParentTenancyUserIdMailIdList()+",  "+
					"createdBy: "+response.get(i).getCreatedBy()+",  "+"createdDate:"+response.get(i).getCreatedDate()+",  "+
					"operatingStartTime: "+response.get(i).getOperatingStartTime()+",  "+"operatingEndTime:"+response.get(i).getOperatingEndTime()+",  " +
					//DefectId: To be provided by testing team
					//Rajani Nagaraju - 20130704 - AccountName to be returned from TenancyDetailsService 
					"AccountName:"+response.get(i).getAccountName() +
					"TenancyCode:"+response.get(i).getTenancyCode() +
					",  " + "Size:"+response.get(i).getSize());
			
			isValidinput = util.inputFieldValidation(response.get(i).getAccountName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(response.get(i).getCreatedBy());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(response.get(i).getCreatedDate());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(response.get(i).getOperatingEndTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(response.get(i).getOperatingStartTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(response.get(i).getParentTenancyName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(response.get(i).getTenancyCode());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(response.get(i).getTenancyName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(response.get(i).getParentTenancyId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			String tenancyAdminList = convObj.getStringList(response.get(i).getTenancyAdminList()).toString();
			isValidinput = util.inputFieldValidation(tenancyAdminList);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(response.get(i).getTenancyId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:TenancyDetailsService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response;
	}

}
