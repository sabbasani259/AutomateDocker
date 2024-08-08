package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAssetDetailsReqContract;
import remote.wise.service.datacontract.UserAssetDetailsRespContract;
import remote.wise.service.implementation.UserAssetDetailsImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;

/** Webservice to return the details of a machine
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "UserAssetDetailsService")
public class UserAssetDetailsService 
{	
	/** WebMethod to return the details of a given VIN
	 * Defect Id: 902 - Rajani Nagaraju -  DealerUser as first PrimarySMS contact.
	 * @param reqObj specifies the VIN
	 * @return Returns the details of the VIN
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetUserAssetDetails", action = "GetUserAssetDetails")
	public UserAssetDetailsRespContract getUserAssetDetails(@WebParam(name="reqObj" ) UserAssetDetailsReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UserAssetDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("serialNumber:"+reqObj.getSerialNumber()+",  "+
				"loginId:"+reqObj.getLoginId()+",  "+"userTenancyId:"+reqObj.getUserTenancyId()+",  "+"lifeHours:"+reqObj.getLifeHours());
		
		//DF20181015 - KO369761 - Extracting CSRF Token from getLoginId field.
		String csrfToken = null;
		boolean isValidCSRF = false;
		CommonUtil util = new CommonUtil();
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}

		//DF20181015 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("getUserAssetDetails ::  Invalid request.");
			throw new CustomFault("Invalid request.");

		}
		
		//DF20180925 ::: MA369757 :: Security checks for all input fields
		String isValidinput=null;
		ListToStringConversion convert=new ListToStringConversion();
		String AssetCustomGroupNameString="";
		if(reqObj.getSerialNumber()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getSerialNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getLifeHours()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getLifeHours());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		isValidinput = util.inputFieldValidation(reqObj.getUserTenancyId()+"");
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		CommonUtil utilObj = new CommonUtil();
		String UserID=utilObj.getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
		
		//DF20180806:KO369761 - Validating VIN hierarchy against login id/tenancy id
		String serialNum = utilObj.validateVIN(reqObj.getUserTenancyId(), reqObj.getSerialNumber());
		if(serialNum == null || serialNum.equalsIgnoreCase("FAILURE")){
			throw new CustomFault("Invalid VIN Number");
		}
				
		UserAssetDetailsRespContract response = new UserAssetDetailsImpl().getUserAssetDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("serialNumber:"+response.getSerialNumber()+",  "+"assetClassName:"+response.getAssetClassName() +",  " +
				"assetGroupName:"+response.getAssetGroupName()+",  "+"assetTypeName:"+response.getAssetTypeName()+",  " +
				"make:"+response.getMake()+",  "+"customerName:"+response.getCustomerName()+",  " +
				"customerPhoneNumber:"+response.getCustomerPhoneNumber()+",  "+"customerEmailId:"+response.getCustomerEmailId()+",  " +
				"dealerName:"+response.getDealerName()+",  "+"dealerPhoneNumber:"+response.getDealerPhoneNumber()+",  " +
				"dealerEmailId:"+response.getDealerEmailId()+",  "+"assetCustomGroupName:"+response.getAssetCustomGroupName()+",  " +
				"lifeHours:"+response.getLifeHours()+",  "+"DealerTenancyId:"+response.getDealerTenancyId()+",  " +
				"renewalDate:"+response.getRenewalDate()+",  "+"driverName:"+response.getDriverName()+",  " +
				"driverContactNumber:"+response.getDriverContactNumber()+", "+"assetTypeCode:"+response.getAssetTypeCode());

		//DF20180925 ::: MA369757 :: Security checks for all input fields
		/*if(response.getSerialNumber()!=null){
			isValidinput = util.inputFieldValidation(response.getSerialNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}*/
		//ramu b get the serial number field  and split it to get the serial number 
		if(response.getSerialNumber()!=null){
			String serialNumberAndExtendedwarantType = response.getSerialNumber();
			String[] split = serialNumberAndExtendedwarantType.split(Pattern.quote("|"));
			iLogger.info("split size "+split.length);
			isValidinput = util.inputFieldValidation(split[0]);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getAssetClassName()!=null){
			isValidinput = util.inputFieldValidation(response.getAssetClassName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getAssetGroupName()!=null){
			isValidinput = util.inputFieldValidation(response.getAssetGroupName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getAssetTypeName()!=null){
			isValidinput = util.inputFieldValidation(response.getAssetTypeName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		isValidinput = util.inputFieldValidation(response.getMake()+"");
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		if(response.getCustomerName()!=null){
			isValidinput = util.inputFieldValidation(response.getCustomerName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getCustomerPhoneNumber()!=null){
			isValidinput = util.inputFieldValidation(response.getCustomerPhoneNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getCustomerEmailId()!=null){
			isValidinput = util.inputFieldValidation(response.getCustomerEmailId());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getDealerName()!=null){
			isValidinput = util.inputFieldValidation(response.getDealerName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getDealerPhoneNumber()!=null){
			isValidinput = util.inputFieldValidation(response.getDealerPhoneNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getDealerEmailId()!=null){
			isValidinput = util.inputFieldValidation(response.getDealerEmailId());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getAssetCustomGroupName()!=null && response.getAssetCustomGroupName().size()>0)
		{
			AssetCustomGroupNameString=convert.getStringWithoutQuoteList(response.getAssetCustomGroupName()).toString();
		}
		if(response.getAssetCustomGroupName()!=null){
			isValidinput = util.inputFieldValidation(AssetCustomGroupNameString);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getLifeHours()!=null){
			isValidinput = util.inputFieldValidation(response.getLifeHours());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		isValidinput = util.inputFieldValidation(response.getDealerTenancyId()+"");
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		if(response.getRenewalDate()!=null){
			isValidinput = util.inputFieldValidation(response.getRenewalDate());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getDriverName()!=null){
			isValidinput = util.inputFieldValidation(response.getDriverName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(response.getDriverContactNumber()!=null){
			isValidinput = util.inputFieldValidation(response.getDriverContactNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserAssetDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;		
	}	
}
