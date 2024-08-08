package remote.wise.service.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.BreakfastReportImpl;
import remote.wise.service.implementation.SendBreakfastReport;
import remote.wise.util.CommonUtil;

@Path("/BreakfastReport")
public class BreakfastReport 
{
	@POST
	@Path("setPreference")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_JSON })
	public String setPreference(LinkedHashMap<String, Object> requestObj) throws CustomFault 
	{
        Logger iLogger = InfoLoggerClass.logger;		
		String status = "SUCCESS";
		
        long startTime = System.currentTimeMillis();
		
		String LoginID =null;
		if(requestObj.get("LoginID")!=null){
			LoginID=requestObj.get("LoginID").toString();
		}
		
		iLogger.info("BreakfastReport:setPreference:WebService Input:LoginID:"+LoginID+"; AccountCode:"+requestObj.get("AccountCode")+";" +
				"Preference:"+requestObj.get("Preference")+"; Source:"+requestObj.get("Source"));
		
		String accountCode=null, Source=null; 
		int Preference=0;
		
		if(requestObj.get("AccountCode")!=null){
			accountCode=requestObj.get("AccountCode").toString();
		}
		if(requestObj.get("Preference")!=null){
			Preference=(int)requestObj.get("Preference");
		}
		
		if(requestObj.get("Source")==null) 
			Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		else
			Source = requestObj.get("Source").toString();
		
		
		//****************************************** Security Checks for the call from WebApp***************************
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
			}

			if(csrfToken != null)
			{
				isValidCSRF = util.validateANTICSRFTOKEN(LoginID, csrfToken);
			}
			if (!isValidCSRF) 
			{
				iLogger.info("BreakfastReport:setPreference:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
				throw new CustomFault("Invalid request.");
			}
			/*else
			{
				util.deleteANTICSRFTOKENS(LoginID, csrfToken, "one");
			}*/
			
			//--------------------------------------- XSS validation of input for Security Fixes
			String isValidinput = null;
			
			if(requestObj.get("AccountCode")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("AccountCode").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("Preference")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Preference").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			//Get decoded login ID
			LoginID=new CommonUtil().getUserId(LoginID);
		}
		status = new BreakfastReportImpl().setBreakfastReportPref(LoginID, accountCode, Preference, Source);
		long endTime = System.currentTimeMillis();
		iLogger.info("BreakfastReport:setPreference:LoginID:"+LoginID+";" +
					"WebService output:"+status+"; Webservice execution time in ms:"+(endTime-startTime));
		

		return status;
		
	}
	
	
	//***************************************************************************************************************
	@GET
	@Path("getPreference")
	public int getPreference(@QueryParam("LoginID") String LoginID, @QueryParam("Source") String Source) throws CustomFault 
	{
		int preference=0;
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		iLogger.info("BreakfastReport:getPreference:WebServiceInput:LoginID:"+LoginID+"; Source:"+Source);
		
		if(LoginID==null || LoginID.trim().length()==0)
		{
			fLogger.fatal("BreakfastReport:getPreference:Mandatory parameter LoginID is NULL");
			return preference;
		}
		
		if(Source==null) 
			Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		
		//****************************************** Security Checks for the call from WebApp***************************
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
			}

			if(csrfToken != null)
			{
				isValidCSRF = util.validateANTICSRFTOKEN(LoginID, csrfToken);
			}
			if (!isValidCSRF) 
			{
				iLogger.info("BreakfastReport:getPreference:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
				throw new CustomFault("Invalid request.");
			}
			else
			{
				util.deleteANTICSRFTOKENS(LoginID, csrfToken, "one");
			}
			
			//Get decoded login ID
			LoginID=new CommonUtil().getUserId(LoginID);
			
			//--------------------------------------- XSS validation of input for Security Fixes
			String isValidinput = null;
			
			if(Source!=null)
			{
				isValidinput = util.inputFieldValidation(Source);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		
		preference = new BreakfastReportImpl().getBFReportPreference(LoginID, Source);
		
		
		
		long endTime = System.currentTimeMillis();
		iLogger.info("BreakfastReport:getPreference:LoginID:"+LoginID+
				" WebService output:"+preference+"; Webservice execution time in ms:"+(endTime-startTime));
		
		
		return preference;
	}
	
	
	//***************************************************************************************************************************
	@GET
	@Path("sendBreakfastReport")
	public String sendBreakfastReport(@QueryParam("UserID") String UserID)
	{
		String status="SUCCESS";
		Logger iLogger=InfoLoggerClass.logger;
		iLogger.info("BreakfastReport:sendBreakfastReport:WebService Input:UserID:"+UserID);
		new SendBreakfastReport(UserID);
		return status;
	}
	
	//***************************************************************************************************************************
	@GET
	@Path("getBreakfastReport")
	@Produces({"application/pdf"})
	public byte[] getBreakfastReport(@QueryParam("fileName") String fileName) throws IOException
	{
		//String path = "/user/JCBLiveLink/BreakfastReport/"+fileName+".pdf";
		/* File file = new File(path);  
        ResponseBuilder response = Response.ok((Object) file);  
        response.header("Content-Disposition","attachment; filename=\""+fileName+".pdf\""); 
        response.type(MediaType.APPLICATION_OCTET_STREAM_TYPE);
        return response.build(); */
        
		File file = new File(fileName);  
		InputStream in =  new FileInputStream(file);
        return IOUtils.toByteArray(in);
	}
}
