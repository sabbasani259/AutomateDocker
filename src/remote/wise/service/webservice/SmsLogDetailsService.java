package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import remote.wise.service.implementation.SmsLogDetailsImpl;
import remote.wise.util.CommonUtil;

@Path("/LogDetailsService")
public class SmsLogDetailsService {

	@GET
	@Path("getsmslogdetails")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> getlogdetails(@Context HttpHeaders httpHeaders, @QueryParam("vin")String vin,@QueryParam("startdate") String startdate,@QueryParam("enddate") String enddate,@QueryParam("loginId") String loginId){
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		List<HashMap<String,String>> result = null;
		SmsLogDetailsImpl implObj = new SmsLogDetailsImpl();
		try{
			infoLogger.info("=======Webservice input===== /LogDetailsService/getsmslogdetails : vin :"+vin+" startdate :"+startdate+" enddate :"+enddate);
			
			//DF20181025 :: CSRF validation
			CommonUtil util = new CommonUtil();
			String csrfToken = null;
//			if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
//			{
//				csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
//			}
//			infoLogger.info("SmsLogDetailsService ::  received csrftoken :: "+csrfToken);
//			boolean isValidCSRF=false;
//			if(csrfToken!=null){
//			isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
//			}
//			infoLogger.info("SmsLogDetailsService ::   csrftoken isValidCSRF :: "+isValidCSRF);
//			if(!isValidCSRF)
//			{
//				infoLogger.info("SmsLogDetailsService :: getsmslogdetails ::  Invalid request.");
//				throw new CustomFault("Invalid request.");
//			}
			
			//DF20180713: KO369761 - Security Check added for input text fields.
			String isValidinput=null;
			isValidinput = util.inputFieldValidation(vin);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
		if(!(vin.isEmpty()||startdate.isEmpty()||enddate.isEmpty()))
		{
			try{
				result = implObj.getSmslogdetails(vin,startdate,enddate);
				long endTime = System.currentTimeMillis();
				infoLogger.info("serviceName:SmsLogDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
				infoLogger.info("=======Webservice output===== /LogDetailsService/getsmslogdetails :"+result);
			}catch(Exception e){
				fLogger.error("Exception: "+e);
			}
			
		}
		else{
			throw new CustomFault("Invalid/Null inputs received :vin :"+vin+" startdate :"+startdate+" enddate :"+enddate);
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception in webservice : LogDetailsService/getsmslogdetails : "+e);

		}

		return result;
	}

}
