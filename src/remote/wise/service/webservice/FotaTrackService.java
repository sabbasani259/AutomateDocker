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
import remote.wise.service.implementation.FotaTrackimpl;
import remote.wise.service.implementation.SmsLogDetailsImpl;
import remote.wise.util.CommonUtil;

@Path("/trackFota")
public class FotaTrackService {
	@GET
	@Path("/getFotaTrackDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String,String> gettrackdetails(@Context HttpHeaders httpHeaders,@QueryParam("vin")String vin, @QueryParam("loginId")String loginId) throws CustomFault{
		//input can be a vin or date.
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		HashMap<String,String> result=null;
		FotaTrackimpl implObj = new FotaTrackimpl();
		long startTime = System.currentTimeMillis();
		String csrfToken = null;
		try{
			infoLogger.info("=======Webservice input===== /trackFota/getFotaTrackDetails : vin :"+vin);
			
			//DF20181015 Avinash Xavier : CSRF Token Validation ---Start---.
			if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
			{
				csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
			}
			
			if((!vin.isEmpty()) && vin!=null){

				//DF20180713: KO369761 - Security Check added for input text fields.
				CommonUtil util = new CommonUtil();
				
				boolean isValidCSRF = false;
				//DF20181015 Avinash Xavier : CSRF Token Validation ---Start---.
				if(csrfToken!=null && (loginId != null && !loginId.equalsIgnoreCase("null")) ){
					isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
				}
				infoLogger.info("FotaTrackService :: getFotaTrackDetails ::   csrftoken isValidCSRF :: "+isValidCSRF);
				if(!isValidCSRF)
				{
					infoLogger.info("FotaTrackService :: getFotaTrackDetails ::  Invalid request.");
					throw new CustomFault("Invalid request.");
				}

				//DF20181015 Avinash Xavier : CSRF Token Validation ---end---.
				
				String isValidinput=null;
				isValidinput = util.inputFieldValidation(vin);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				result = implObj.getFotatrackdetails(vin);
				
				isValidinput = util.inputFieldValidation(result.get("imei_number"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(result.get("ep_timeStamp"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.inputFieldValidation(result.get("device_version"));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			else{
				throw new CustomFault("/trackFota/getFotaTrackDetails Null input");
			}
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:FotaTrackService~executionTime:"+(endTime-startTime)+"~"+""+"~");
			infoLogger.info("=======Webservice output===== /trackFota/getFotaTrackDetails :"+result);
		}catch(Exception e){
			e.printStackTrace();
			e.getMessage();
			fLogger.error("Exception: "+e);
		}
		return result;

	}

}
