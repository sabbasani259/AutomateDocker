package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.TitanMachineParameterImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;

@Path("/TitanMachineParameterRestService")
public class TitanMachineParameterRestService {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getEngineRunningModeParameter")
	public HashMap<String,HashMap<String,Double>> getEngineRunningModeParameter(@QueryParam("loginId") String loginId,@QueryParam("serialNumber") String serialNumber,@QueryParam("period") String period){
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		HashMap<String,HashMap<String,Double>> response = new HashMap<String,HashMap<String,Double>>();
		String csrfToken = null;
		boolean isValidCSRF=false;
		CommonUtil util = new CommonUtil();
		
		try{
			long startTime = System.currentTimeMillis();
			iLogger.info("Webservice input Encrypted loginId: "+loginId+" serialNumber: "+serialNumber+" period: "+period);
			String userId=new CommonUtil().getUserId(loginId);
			iLogger.info("Decoded userId::"+userId);
			/*String isValidinput = util.inputFieldValidation(userId.toString());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}*/
			String isValidinput = util.inputFieldValidation(serialNumber.toString());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(period.toString());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			response = new TitanMachineParameterImpl(). getFleetUtilizationDetails(loginId,serialNumber,period);
			long endTime = System.currentTimeMillis();
			iLogger.info("serviceName:TitanMachineParameterRestService~executionTime:"+(endTime-startTime)+"~"+userId+"~");
		}
		catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception:"+e.getMessage());
		}
		return response;
	}

	
}
