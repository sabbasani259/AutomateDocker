package remote.wise.service.webservice;

import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import java.util.LinkedHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.SiteNameAssociationForMACustomerReqContract;
import remote.wise.service.implementation.SiteNameAssociationImpl;
import remote.wise.util.CommonUtil;

@Path("/SiteNameAssociation")
public class SiteNameAssociationForMACustomerRestService {

	@POST
	@Path("/AddSiteMaster")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String AddSiteMaster(
			final @JsonProperty("reqObj") LinkedHashMap<String, Object> reqObj)
			throws JsonGenerationException, JsonMappingException, IOException,
			CustomFault {
		SiteNameAssociationForMACustomerReqContract request = new SiteNameAssociationForMACustomerReqContract();
		String response = "SUCCESS";
		String userId = null;
		String siteName = null;
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: " + startDate);
		long startTime = System.currentTimeMillis();
		if (reqObj.get("userId") != null) {
			userId = (String) reqObj.get("userId");
			request.setUserId(userId);
		}
		if (reqObj.get("siteName") != null) {
			siteName = (String) reqObj.get("siteName");
			request.setSiteName(siteName);
		}
		infoLogger.info(" WebServie input--- : "+"userId"+request.getUserId()+"siteName"+request.getSiteName());
		try{
		response = new SiteNameAssociationImpl().SiteNameAssociation(request);
		infoLogger.info(" WebServie output--- SiteNameAssociationForMACustomerRestService: AddSiteMaster: response " + response);
		long endTime = System.currentTimeMillis();
		infoLogger.info("Webservice Execution Time in ms:" + (endTime - startTime));
		}catch (Exception e) {
			fLogger.error("Error in calling SiteNameAssociationImpl for SiteNameAssociation():" + e.getMessage());
		}
		return response;
	}

	@GET
	@Path("SiteNameDisplayForLoggedInUser")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> SiteNameDisplayForLoggedInUser(@QueryParam("userId") String userId)		
		 throws CustomFault {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<String> response =null;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: " + startDate);
		long startTime = System.currentTimeMillis();
		CommonUtil util = new CommonUtil();
		String isValidUserId = null;			
		try {
			
			isValidUserId = util.inputFieldValidation(String.valueOf(userId));
			if (!isValidUserId.equalsIgnoreCase("SUCCESS")) {
				throw new CustomFault(isValidUserId);
				}		
			infoLogger.info(" WebServie input--- : "+"userId"+userId);
			SiteNameAssociationImpl implObj = new SiteNameAssociationImpl();
			response = implObj.displaySiteNameAssociatedWithUser(userId);
			infoLogger.info("Webservice Output -- SiteNameAssociationForMACustomerRestService: SiteNameDisplayForLoggedInUser: Response : " + response);
			long endTime = System.currentTimeMillis();
			infoLogger.info("Webservice Execution Time in ms:" + (endTime - startTime));
		} catch (Exception e) {
			fLogger.error("Error in calling SiteNameAssociationImpl for displaySiteNameAssociatedWithUser():" + e.getMessage());
		}
		return response;
	}

	@GET
	@Path("VinDisplayForLoggedInUser")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> VinDisplayForLoggedInUser(@QueryParam("userId") String userId)		
		 throws CustomFault {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<HashMap<String,String>> response =null;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: " + startDate);
		long startTime = System.currentTimeMillis();
		CommonUtil util = new CommonUtil();
		String isValidUserId = null;			
		try {
			
			isValidUserId = util.inputFieldValidation(String.valueOf(userId));
			if (!isValidUserId.equalsIgnoreCase("SUCCESS")) {
				throw new CustomFault(isValidUserId);
				}	
			infoLogger.info(" WebServie input--- : "+"userId"+userId);
			SiteNameAssociationImpl implObj = new SiteNameAssociationImpl();
			response = implObj.displayVinAssociatedWithUser(userId);
			infoLogger.info("Webservice Output -- SiteNameAssociationForMACustomerRestService: VinDisplayForLoggedInUser: Response : " + response);
			long endTime = System.currentTimeMillis();
			infoLogger.info("Webservice Execution Time in ms:" + (endTime - startTime));
		} catch (Exception e) {
			fLogger.error("Error in calling SiteNameAssociationImpl for displayVinAssociatedWithUser():" + e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("SiteVinMapping")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String SiteVinMapping(final @JsonProperty("reqObj") LinkedHashMap<String,Object> reqObj) throws CustomFault {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "SUCCESS";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: " + startDate);
		long startTime = System.currentTimeMillis();		
		String userId = null;
		String siteName= null;
		List<String> serialNumberList = null;
		try {				
			for(int i=0;i<reqObj.size();i++){	
				if(reqObj.get("userId")!=null)
					userId=(String) reqObj.get("userId");	
				if(reqObj.get("siteName")!=null)
					siteName=(String) reqObj.get("siteName");
				if(reqObj.get("serialNumber")!=null)
					serialNumberList=(List<String>) reqObj.get("serialNumber");	
			}
			
			infoLogger.info(" WebServie input--- : "+"userId"+userId +"siteName"+siteName+"serialNumber"+serialNumberList);
			SiteNameAssociationImpl implObj = new SiteNameAssociationImpl();
			response = implObj.editSiteVinMapping(userId, siteName, serialNumberList);
			infoLogger.info("Webservice Output -- SiteNameAssociationForMACustomerRestService: SiteVinMapping: Response : " + response);
						long endTime = System.currentTimeMillis();
			infoLogger.info("Webservice Execution Time in ms:" + (endTime - startTime));
		} catch (Exception e) {
			fLogger.error("Error in calling SiteNameAssociationImpl for editSiteVinMapping():" + e.getMessage());
			return "FAILURE";
		}
		return response;
	}

/*	@GET
	@Path("/NickNameAdnNoteAddition")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String NickNameAdnNoteAddition(@QueryParam("serialNumber") String serialNumber) throws Exception{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "SUCCESS";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: " + startDate);
		long startTime = System.currentTimeMillis();
		infoLogger.info(" WebServie input--- : "+"serialNumber"+serialNumber);
		try{
		SiteNameAssociationImpl associationImpl = new SiteNameAssociationImpl();
		response = associationImpl.moolDAUpdationForNickNameandNote(serialNumber);
		infoLogger.info("Webservice Output -- SiteNameAssociationForMACustomerRestService: NickNameAdnNoteAddition: Response : " + response);
		long endTime = System.currentTimeMillis();
		infoLogger.info("Webservice Execution Time in ms:" + (endTime - startTime));
		}catch (Exception e) {
			fLogger.info("Error in calling SiteNameAssociationImpl for moolDAUpdationForNickNameandNote() "+e.getMessage());
		}		
		return response;
		
	}*/

}
