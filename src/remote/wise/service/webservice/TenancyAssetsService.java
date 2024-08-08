package remote.wise.service.webservice;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.TenancyAssetsReqContract;
import remote.wise.service.datacontract.TenancyAssetsRespContract;
import remote.wise.service.implementation.TenancyAssetsImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

/** Webservice that returns the List Machines for the specified tenancy
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "TenancyAssetsService")
public class TenancyAssetsService 
{
	/**Webservice method to return the VIN list for a given tenancy
	 * @param reqObj TenancyId list is specified through this reqObj
	 * @return Returns the List of VINs for the specified tenancy
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetTenancyAssets", action = "GetTenancyAssets")
	public TenancyAssetsRespContract getTenancyAssets(@WebParam(name="reqObj" ) TenancyAssetsReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("TenancyAssetsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+reqObj.getLoginId()+",  "+"tenancyIdList:"+reqObj.getTenancyIdList()+",   " +
				"childTenancyAssetsRequired:"+reqObj.isChildTenancyAssetsRequired()+", "+"serialNumber:"+reqObj.getSerialNumber());
		
		//DF20181015 - KO369761 - Extracting CSRF Token from login name field.
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}

		//DF20181015 - KO369761 - Validating the CSRF Token against login id.
		CommonUtil util = new CommonUtil();
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("getTenancyAssets ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		//DF20180925 ::: MA369757 :: Security checks for all input fields		
		String isValidinput=null;

		ListToStringConversion convert=new ListToStringConversion();
		String tenancyList="";
		if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0)
		{
			tenancyList=convert.getIntegerListString(reqObj.getTenancyIdList()).toString();
		}
		if(reqObj.getTenancyIdList()!=null){
			isValidinput = util.inputFieldValidation(tenancyList);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getSerialNumber()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getSerialNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());

		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code


		if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
			reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
		}
				
		TenancyAssetsRespContract response = new TenancyAssetsImpl().getTenancyAssets(reqObj);
		
		if(response.getSerialNumberList()!=null && response.getSerialNumberList().size()>0)
		{
			String serialNumberListString=convert.getStringWithoutQuoteList(response.getSerialNumberList()).toString();
			isValidinput = util.inputFieldValidation(serialNumberListString);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Serial Number List Size:"+response.getSerialNumberList().size());
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:TenancyAssetsService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response;
	}

}
