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
import remote.wise.service.datacontract.AssetGroupTypeReqContract;
import remote.wise.service.datacontract.AssetGroupTypeRespContract;
import remote.wise.service.implementation.AssetGroupTypeImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/** Webservice class to handle Custom Machine Group Type
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "AssetGroupTypeService")
public class AssetGroupTypeService 
{
	
	/** This method sets the details of Custom Machine Group Type
	 * @param reqObj details of custom Machine group Type is specified through reqObj
	 * @return Returns the status message of new machine group type creation/update
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetAssetGroupType", action = "SetAssetGroupType")
	public String setAssetGroupType(@WebParam(name="reqObj" ) AssetGroupTypeRespContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetGroupTypeService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Tenancy Id:"+reqObj.getTenancyId()+",  "+"AssetGroupTypeId:"+reqObj.getAssetGroupTypeId()+",  " +
				""+"AssetGroupTypeName:"+reqObj.getAssetGroupTypeName()+",  "+"Description: "+reqObj.getAssetGroupTypeDescription()+",  " +
				"ClientId:"+reqObj.getClientId());
		String response = new AssetGroupTypeImpl().setAssetGroupType(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetGroupTypeService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;

	}



	/** This method returns the details of Custom Asset Group Type
	 * @param reqObj AssetGroupType/Tenancy is specified for which details are to be returned
	 * @return details of required AssetGroupType
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetAssetGroupType", action = "GetAssetGroupType")
	public List<AssetGroupTypeRespContract> getAssetGroupType(@WebParam(name="reqObj" ) AssetGroupTypeReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetGroupTypeService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Login Id:"+reqObj.getLoginId()+",  "+"TenancyIdList:"+reqObj.getTenancyIdList()+",  " +
				""+"AssetGroupTypeId:"+reqObj.getAssetGroupTypeId()+",  "+"IsOtherTenancy: "+reqObj.isOtherTenancy());
		
		//DF20181015 - KO369761 - Extracting CSRF Token from login name field.
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
			iLogger.info("AssetGroupTypeService ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
		
		List<AssetGroupTypeRespContract> response = new AssetGroupTypeImpl().getAssetGroupType(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Tenancy Id:"+response.get(i).getTenancyId()+",  "+"AssetGroupTypeId:"+response.get(i).getAssetGroupTypeId()+",  " +
					""+"AssetGroupTypeName:"+response.get(i).getAssetGroupTypeName()+",  "+"Description: "+response.get(i).getAssetGroupTypeDescription()+",  " +
					"ClientId:"+response.get(i).getClientId());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetGroupTypeService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;

	}


	/** This method deletes the Custom Asset group Type
	 * @param reqObj CustomAssetGroupTypeId that has to be deleted 
	 * @return  status of AssetGroupType deletion
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "DeleteAssetGroupType", action = "DeleteAssetGroupType")
	public String deleteAssetGroupType(@WebParam(name="reqObj" ) AssetGroupTypeReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetGroupTypeService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("AssetGroupTypeId:"+reqObj.getAssetGroupTypeId());
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
				reqObj.setLoginId(UserID);
				iLogger.info("Decoded userId::"+reqObj.getLoginId());
				
		String response = new AssetGroupTypeImpl().deleteAssetGroupType(reqObj);
		
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetGroupTypeService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}

}
