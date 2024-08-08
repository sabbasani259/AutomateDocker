package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.GeoFenceDetailsPOJONew;
import remote.wise.service.implementation.GeofenceDetailsImplV4;
import remote.wise.service.implementation.GeofenceDetailsImplV5;
import remote.wise.util.CommonUtil;

@Path("/GeoFenceServiceV5")
public class GeoFenceServiceV5 {

	@GET
	@Path("getLandmarkDetailsForVINNew")
	@Produces({"application/json"})
	public GeoFenceDetailsPOJONew getLandmarkDetailsForVINNew(@QueryParam("LoginID") String LoginID, 
			@QueryParam("AccountCode") String AccountCode,@QueryParam("VIN") String VIN, @QueryParam("Source") String Source) throws CustomFault 
	{
	
		GeoFenceDetailsPOJONew response = new GeoFenceDetailsPOJONew();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		iLogger.info("GeoFenceServiceV4:getLandmarkDetailsForVINNew:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+AccountCode+";" +
				"VIN:"+VIN);
		
		if(VIN==null || VIN.trim().length()==0)
		{
			fLogger.fatal("GeoFenceServiceV4:getLandmarkDetailsForVINNew:Mandatory parameter VIN is NULL. Hence returning empty response");
			return response;
		}
		
		
		if(Source==null) 
			Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		
		if(Source.equalsIgnoreCase("WebApp"))
		{
			CommonUtil util = new CommonUtil();
			String csrfToken = null;
			boolean isValidCSRF = false;
			
			if (LoginID != null) 
			{
				if (LoginID.split("\\|").length > 1) 
				{
					csrfToken = LoginID.split("\\|")[1];
					LoginID = LoginID.split("\\|")[0];
				}
				
				if(csrfToken != null)
				{
					isValidCSRF = util.validateANTICSRFTOKEN(LoginID, csrfToken);
				}
				if (!isValidCSRF) 
				{
					iLogger.info("GeoFenceServiceV4:getLandmarkDetailsForVINNew:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
					throw new CustomFault("Invalid request.");
				}
				/*else
				{
					util.deleteANTICSRFTOKENS(LoginID, csrfToken, "one");
				}*/
				
				//Get decoded login ID
				LoginID=new CommonUtil().getUserId(LoginID);
				
			}

			
			//------------ XSS validation of input for Security Fixes
			String isValidinput = null;
			
			if(VIN!=null)
			{
				isValidinput = util.inputFieldValidation(VIN);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(AccountCode!=null)
			{
				isValidinput = util.inputFieldValidation(AccountCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		response = new GeofenceDetailsImplV5(). getLandmarkDetailsForVINNew(LoginID,AccountCode,VIN);
		
		
		if(Source.equalsIgnoreCase("WebApp"))
		{
			String isValidinput = null;
			CommonUtil util = new CommonUtil();
			
			if(response.getLandmarkID()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkID());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(response.getLandmarkCategoryID()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkCategoryID());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getLandmarkName()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getLandmarkCategoryName()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkCategoryName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getVIN()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getVIN());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getCreatedDate()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getCreatedDate());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getCustomerCode()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getCustomerCode());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getMobileNumber()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getMobileNumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		long endTime = System.currentTimeMillis();
		iLogger.info("GeoFenceServiceV4:getLandmarkDetailsForVINNew:LoginID:"+LoginID+"; VIN:"+VIN+";" +
				" WebService output:"+response+"; Webservice execution time in ms:"+(endTime-startTime));
		if(response.getLandmarkID()==null || (response.getGeofenceDetails() == null || response.getGeofenceDetails().isEmpty() || response.getGeofenceDetails().size() == 0) ){
			response=null;
		}			
		return response;
		
	}
	
}
