package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetGroupReqContract;
import remote.wise.service.datacontract.AssetGroupRespContract;
import remote.wise.service.datacontract.AssetGroupUsersReqContract;
import remote.wise.service.datacontract.AssetGroupUsersRespContract;
import remote.wise.service.implementation.AssetGroupImpl;
import remote.wise.service.implementation.AssetGroupUsersImpl;
import remote.wise.service.implementation.OAssetGroupUsersImpl;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;




/** WebService class to set and get the users associated to the assets
 * @author Deepthi Rao
 *
 */
@WebService(name = "AssetGroupUsers")
public class AssetGroupUsersService {


	/** This method fetches the users attached to a Asset Group or all the users of the tenancy
	 * @param reqObj Get the details of users attached to the asset group users by passing the same to this request Object
	 * @return Returns the users attached to the AssetGroup
	 * @throws CustomFault
	 */

	@WebMethod(operationName = "GetAssetGroupUsers", action = "GetAssetGroupUsers")
	public AssetGroupUsersRespContract getAssetGroupUsers(@WebParam(name="reqObj" )  AssetGroupUsersReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetGroupUsersService:","info");
		Logger iLogger = InfoLoggerClass.logger;

		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("LoginId :" +reqObj.getLoginId()+",	" +"Group Id: " +reqObj.getGroupId()+",	"+"Login Tenancy ID :" + reqObj.getLoginTenancyId() );
		
		//DF20181015 - KO369761 - Extracting CSRF Token from login field.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}

		//DF20181011 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("getAssetGroupUsers ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}

		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());

		AssetGroupUsersRespContract response_list =  new AssetGroupUsersImpl().getAssetGroupUsers(reqObj);

		iLogger.info("----- Webservice Output-----");
		iLogger.info("GroupId: " + response_list.getGroupId() + "Login Id:  " + response_list.getLoginId() + "Login TenancyId " + response_list.getLoginTenancyId() + "Contact ID:  " +response_list.getContactId());
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetGroupUsers~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response_list;
	}


	/** This method sets the users to the asset group users
	 * @param reqObj Get the Group Users for the Asset if group is specified otherwise, all the users of the tenancy
	 * @return Returns the status String as either SUCCESS/FAILURE for setting the Group users for the Asset.
	 * @throws CustomFault
	 */
	@WebMethod(operationName="SetAssetGroupUsers", action ="SetAssetGroupUsers")

	public String setAssetGroupUsers(@WebParam(name="reqObj" ) AssetGroupUsersRespContract userDetailsResponseContract ) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetGroupUsersService:","info");
		Logger iLogger = InfoLoggerClass.logger;

		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("----- Webservice Input-----");
		iLogger.info("AssetGroupId: " + userDetailsResponseContract.getGroupId() +" , "  + " Login Id : "+ userDetailsResponseContract.getLoginId() +" ,	"  + "Login TenancyId:" + userDetailsResponseContract.getLoginTenancyId());
		
		//DF20181015 - KO369761 - Extracting CSRF Token from login field.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(userDetailsResponseContract.getLoginId().split("\\|").length > 1){
			csrfToken=userDetailsResponseContract.getLoginId().split("\\|")[1];
			userDetailsResponseContract.setLoginId(userDetailsResponseContract.getLoginId().split("\\|")[0]);
		}

		//DF20181011 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(userDetailsResponseContract.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("setAssetGroupUsers ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}else{
			//delete the validated token
			util.deleteANTICSRFTOKENS(userDetailsResponseContract.getLoginId(),csrfToken,"one");
		}

		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(userDetailsResponseContract.getLoginId());
		userDetailsResponseContract.setLoginId(UserID);
		iLogger.info("Decoded userId::"+userDetailsResponseContract.getLoginId());

		String response = new AssetGroupUsersImpl().setAssetGroupUsers(userDetailsResponseContract);


		iLogger.info("----- Webservice Output-----");


		iLogger.info("Response Status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetGroupUsers~executionTime:"+(endTime-startTime)+"~"+UserID+"~"+response);
		return response;
	}


}
