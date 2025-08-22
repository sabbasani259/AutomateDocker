/**
 * 
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.RolledOffMachinesReqContract;
import remote.wise.service.datacontract.RolledOffMachinesRespContract;
import remote.wise.service.implementation.RolledOffMachinesImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

/**
 * @author kprabhu5
 *	//LLOPS-164 : Sai Divya : 20250821 : Soap to Rest
 */
@Path("RolledOffMachinesServiceRestService")
public class RolledOffMachinesServiceRestService {
		
	@Path( "getRolledOffMachines")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<RolledOffMachinesRespContract> getRolledOffMachines(RolledOffMachinesReqContract reqObj) {

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("RolledOffMachinesService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("<-----Webservice Input----->");
		iLogger.info("LoginId:"+reqObj.getLoginId()+" , TenancyIdList:"+reqObj.getTenancyIdList()+"");
		List<RolledOffMachinesRespContract> response = null;
		try {
		//DF20181015 - KO369761 - Extracting CSRF Token from login field.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
//		if(reqObj.getLoginId().split("\\|").length > 1){
//			csrfToken=reqObj.getLoginId().split("\\|")[1];
//			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
//
//			//DF20181011 - KO369761 - Validating the CSRF Token against login id.
//			if(csrfToken != null)
//				isValidCSRF=util.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
//			if(!isValidCSRF){
//				iLogger.info("getRolledOffMachines ::  Invalid request.");
//				throw new CustomFault("Invalid request.");
//			}
//		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId("prit548406");
		iLogger.info(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
		
		//check whether the Login Id is specified
		if(reqObj.getLoginId()==null)
		{
			throw new CustomFault("Login Id not specified");
		}

		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

		if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
			reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
		}

		//DF20180925 ::: MA369757 :: Security checks for all input fields	
		String isValidinput=null;
		if(reqObj.getVin()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getVin());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getToDate()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getVin());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		if(reqObj.getFromDate()!=null){
			isValidinput = util.inputFieldValidation(reqObj.getVin());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
				
		response = new RolledOffMachinesImpl().getRolledOffMachines(reqObj);
		iLogger.info("<-----Webservice Output----->");
		//NMT page load issue
		/*for(int i=0;i<response.size();i++){
			iLogger.info(i+"  ROW");
			iLogger.info("AssetGroupId:"+response.get(i).getAssetGroupId()+" , AssetTypeId:"+response.get(i).getAssetTypeId()+" , EngineName:"+response.get(i).getEngineName()+" , EngineTypeId:"+response.get(i).getEngineTypeId()+" , IMEINumber:"+response.get(i).getIMEINumber()+" , Latitude:"+response.get(i).getLatitude()+" , Longitude:"+response.get(i).getLongitude()+" , MachineName:"+response.get(i).getMachineName()+" , ModelName:"+response.get(i).getModelName()+" , ProfileName:"+response.get(i).getProfileName()+" , SerialNumber:"+response.get(i).getSerialNumber()+" , SimNumber:"+response.get(i).getSimNumber()+" ,RegistrationDate:"+response.get(i).getRegDate()+" " +
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
					"FuelLevel:"+response.get(i).getFuelLevel()+", CMHR: "+response.get(i).getEnginehours()+", LastReportedTime: "+response.get(i).getLastReportedTime());

			//DF20180925 ::: MA369757 :: Security checks for all input fields	
			if(response.get(i).getSerialNumber()!=null){
				response.get(i).setSerialNumber(response.get(i).getSerialNumber());
				isValidinput = util.inputFieldValidation(response.get(i).getSerialNumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getIMEINumber()!=null){
				response.get(i).setIMEINumber(response.get(i).getIMEINumber().replaceAll("[\\t\\n\\r]+",""));
				isValidinput = util.inputFieldValidation(response.get(i).getIMEINumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getSimNumber()!=null){
				response.get(i).setSimNumber(response.get(i).getSimNumber().replaceAll("[\\t\\n\\r]+",""));
				isValidinput = util.inputFieldValidation(response.get(i).getSimNumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getMachineName()!=null){
				isValidinput = util.inputFieldValidation(response.get(i).getMachineName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getProfileName()!=null){
				isValidinput = util.inputFieldValidation(response.get(i).getProfileName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getModelName()!=null){
				isValidinput = util.inputFieldValidation(response.get(i).getModelName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getEngineName()!=null){
				isValidinput = util.inputFieldValidation(response.get(i).getEngineName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getLatitude()!=null){
				isValidinput = util.inputFieldValidation(response.get(i).getLatitude());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getLongitude()!=null){
				isValidinput = util.inputFieldValidation(response.get(i).getLongitude());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getRegDate()!=null){
				isValidinput = util.inputFieldValidation(response.get(i).getRegDate());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getFuelLevel()!=null){
				isValidinput = util.inputFieldValidation(response.get(i).getFuelLevel());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getLastReportedTime()!=null){
				isValidinput = util.inputFieldValidation(response.get(i).getLastReportedTime());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.get(i).getEnginehours()!=null){
				isValidinput = util.inputFieldValidation(response.get(i).getEnginehours());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}*/
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:RolledOffMachinesService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			iLogger.info("Error "+e);
		}
		return response;
	}
}
