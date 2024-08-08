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
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.PseudoAccessImpl;

@Path("/PseudoAccessListService")
public class PseudoAccessListService {
	@GET
	@Path("/PseudoTenancyList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> getPseudoTenancyList(/*@Context HttpHeaders httpHeaders, @QueryParam("loginID") String loginId*/) throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		List<HashMap<String,String>> response = null;
		
		
		//Implementation method here
		response = new PseudoAccessImpl().getPseudoTenancyList();
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:PseudoAccessListService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}
	
}
