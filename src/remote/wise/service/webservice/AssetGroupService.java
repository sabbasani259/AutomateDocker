package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
//import javax.servlet.http.HttpServletRequest;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetGroupReqContract;
import remote.wise.service.datacontract.AssetGroupRespContract;
import remote.wise.service.implementation.AssetGroupImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

/** Webservice class to handle Custom Machine Group
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "AssetGroupService")
public class AssetGroupService 
{
	
	/** This method sets the details of Custom Machine Group
	 * @param reqObj details of custom Machine group is specified through reqObj
	 * @return Returns the status message of new machine group creation/update
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetCustomAssetGroup", action = "SetCustomAssetGroup")
	public AssetGroupReqContract setCustomAssetGroup(@WebParam(name="reqObj" ) AssetGroupRespContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetGroupService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId Id:"+reqObj.getLoginId()+",  "+"AssetGroupId:"+reqObj.getAssetGroupId()+",  " +
				""+"AssetGroupName:"+reqObj.getAssetGroupName()+",  "+"Description: "+reqObj.getAssetGroupDescription()+",  " +
				"AssetGroupTypeId:"+reqObj.getAssetGroupTypeId()+",  "+"SerialNumberList: "+reqObj.getSerialNumberList()+",  " +
				"TenancyId:"+reqObj.getTenancyId()+",  "+"ClientId: "+reqObj.getClientId());
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
		
		CommonUtil util = new CommonUtil();
		//text field validation to avoid SQL Injection
		String isUserValid = util.responseValidation(reqObj.getAssetGroupDescription());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		
		isUserValid = util.responseValidation(reqObj.getAssetGroupName());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		
		isUserValid = util.responseValidation(reqObj.getAssetGroupTypeName());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		
		//DF20180925 ::: MA369757 :: Security checks for all input fields		
		String isValidinput=null;
		ListToStringConversion convert=new ListToStringConversion();
		String serialNumberList="";
		if(reqObj.getSerialNumberList()!=null && reqObj.getSerialNumberList().size()>0)
		{
			serialNumberList=convert.getStringWithoutQuoteList(reqObj.getSerialNumberList()).toString();
		}
		if(reqObj.getSerialNumberList()!=null){
			isValidinput = util.responseValidation(serialNumberList);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getClientId()!=0){
			isValidinput = util.responseValidation(reqObj.getClientId()+"");
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}if(reqObj.getTenancyId()!=0){
			isValidinput = util.responseValidation(reqObj.getTenancyId()+"");
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		
		AssetGroupReqContract response = new AssetGroupImpl().setAssetGroup(reqObj);
		iLogger.info("----- Webservice Output-----");
		if(response!=null)
			iLogger.info("AssetGroupId:"+response.getAssetGroupId());
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetGroupService~executionTime:"+(endTime-startTime)+"~"+UserID+"~"+response);
		return response;

	}


	/** This method returns the details of Custom Asset Group Type
	 * @param reqObj AssetGroupType/Tenancy is specified for which details are to be returned
	 * @return details of required AssetGroupType
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetCustomAssetGroup", action = "GetCustomAssetGroup")
	public List<AssetGroupRespContract> getCustomAssetGroup(@WebParam(name="reqObj" ) AssetGroupReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetGroupService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Login Id:"+reqObj.getLoginId()+",  "+"TenancyIdList:"+reqObj.getTenancyIdList()+",  " +
				""+"AssetGroupId:"+reqObj.getAssetGroupId()+",  "+"AssetGroupTypeId:"+reqObj.getAssetGroupTypeId()+",  "+
				"SerialNumberList:"+reqObj.getSerialNumberList()+",  "+"IsOtherTenancy: "+reqObj.isOtherTenancy());
		
		//DF20181015 - KO369761 - Extracting CSRF Token from login name field.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}

		//DF20181015 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=new CommonUtil().validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("AssetGroupService:GetCustomAssetGroup ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		//DF20180925 ::: MA369757 :: Security checks for all input fields	
		String isValidinput=null;
		ListToStringConversion convert=new ListToStringConversion();
		String serialNumberList="",tenancyListString="";
		if(reqObj.getSerialNumberList()!=null && reqObj.getSerialNumberList().size()>0)
		{
			serialNumberList=convert.getStringWithoutQuoteList(reqObj.getSerialNumberList()).toString();
		}
		if(reqObj.getSerialNumberList()!=null){
			isValidinput = util.inputFieldValidation(serialNumberList);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0)
		{
			tenancyListString=convert.getStringWithoutQuoteList(reqObj.getSerialNumberList()).toString();
		}
		if(reqObj.getSerialNumberList()!=null){
			isValidinput = util.inputFieldValidation(tenancyListString);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
				
		List<AssetGroupRespContract> response = new AssetGroupImpl().getAssetGroup(reqObj);
		
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("loginId Id:"+response.get(i).getLoginId()+",  "+"AssetGroupId:"+response.get(i).getAssetGroupId()+",  " +
					""+"AssetGroupName:"+response.get(i).getAssetGroupName()+",  "+"Description: "+response.get(i).getAssetGroupDescription()+",  " +
					"AssetGroupTypeId:"+response.get(i).getAssetGroupTypeId()+",  "+"SerialNumberList: "+response.get(i).getSerialNumberList()+",  " +
					"TenancyId:"+response.get(i).getTenancyId()+",  "+"ClientId: "+response.get(i).getClientId()+",  "+"AssetGroupTypeName: "+response.get(i).getAssetGroupTypeName());

			//DF20180925 ::: MA369757 :: Security checks for all input fields	
			if(response.get(i).getSerialNumberList()!=null && response.get(i).getSerialNumberList().size()>0)
			{
				serialNumberList=convert.getStringWithoutQuoteList(reqObj.getSerialNumberList()).toString();
			}
			if(response.get(i).getSerialNumberList()!=null){
				isValidinput = util.responseValidation(serialNumberList);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getClientId()!=0){
				isValidinput = util.responseValidation(response.get(i).getClientId()+"");
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}if(response.get(i).getTenancyId()!=0){
				isValidinput = util.responseValidation(response.get(i).getTenancyId()+"");
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			isValidinput = util.responseValidation(response.get(i).getAssetGroupDescription());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			if(response.get(i).getAssetGroupName()!=null){
				isValidinput = util.responseValidation(response.get(i).getAssetGroupName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getAssetGroupTypeName()!=null){
				isValidinput = util.responseValidation(response.get(i).getAssetGroupTypeName());
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
		iLogger.info("serviceName:AssetGroupService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response;

	}


	/**This method deletes the Custom Asset group
	 * @param reqObj CustomAssetGroupId that has to be deleted 
	 * @return status of AssetGroup deletion
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "DeleteCustomAssetGroup", action = "DeleteCustomAssetGroup")
	public String deleteCustomAssetGroup(@WebParam(name="reqObj" ) AssetGroupRespContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetGroupService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("AssetGroupId:"+reqObj.getAssetGroupId());
		
		//DF20170919 @Roopa getting decoded UserId
		CommonUtil utilObj = new CommonUtil();
		String UserID= utilObj.getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
		
		if(UserID == null){
			throw new CustomFault("Invalid LoginId");
		}

		//text field validation to avoid SQL Injection
		String isUserValid = utilObj.inputFieldValidation(reqObj.getAssetGroupDescription());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		//text field validation to avoid SQL Injection
		isUserValid = utilObj.inputFieldValidation(reqObj.getAssetGroupName());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		//text field validation to avoid SQL Injection
		isUserValid = utilObj.inputFieldValidation(reqObj.getAssetGroupTypeName());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		
		//DF20180925 ::: MA369757 :: Security checks for all input fields
		String serialNumberList="",isValidinput="";
		ListToStringConversion convert=new ListToStringConversion();
		if(reqObj.getSerialNumberList()!=null && reqObj.getSerialNumberList().size()>0)
		{
			serialNumberList=convert.getStringWithoutQuoteList(reqObj.getSerialNumberList()).toString();
		}
		if(reqObj.getSerialNumberList()!=null){
			isValidinput = utilObj.inputFieldValidation(serialNumberList);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getClientId()!=0){
			isValidinput = utilObj.inputFieldValidation(reqObj.getClientId()+"");
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}if(reqObj.getTenancyId()!=0){
			isValidinput = utilObj.inputFieldValidation(reqObj.getTenancyId()+"");
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
				
		String response = new AssetGroupImpl().deleteAssetGroup(reqObj);
		
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetGroupService~executionTime:"+(endTime-startTime)+"~"+UserID+"~"+response);
		return response;

	}

}
