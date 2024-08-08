package remote.wise.service.webservice;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.HMRDetailsServiceImpl;
import remote.wise.util.CommonUtil;

@Path("/HMRDetailsService")
public class HMRDetailsService {

	@GET
	@Path("getHMRDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Object> getHMRDetails(@QueryParam("vinId") String vinId ) throws CustomFault
	{
		HashMap<String, Object> result= null;
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();

		iLogger.info("HMRDetailsService:getHMRDetails:WebService Input: "+vinId);
		CommonUtil utilObj = new CommonUtil();
		String isValidInput = utilObj.inputFieldValidation(vinId);
		if(!isValidInput.equalsIgnoreCase("SUCCESS")){
			throw new CustomFault(isValidInput);
		}
		
		result = new HMRDetailsServiceImpl().getHMRDetails(vinId);
		long endTime=System.currentTimeMillis();
		iLogger.info("HMRDetailsService:getHMRDetails:WebService Output : "+result);
		iLogger.info("serviceName:HMRDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return result;
	}
}
