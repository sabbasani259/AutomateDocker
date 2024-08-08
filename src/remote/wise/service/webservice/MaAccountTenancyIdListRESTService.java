package remote.wise.service.webservice;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.MaAccountTenancyIdListServiceImpl;

@Path("/MaAccountTenancyIdListRESTService")
public class MaAccountTenancyIdListRESTService {

	@GET
	@Path("/getMaAccountTenancyIdList")
	@Produces(MediaType.TEXT_PLAIN)
	public List<Integer> getMaAccountTenancyIdList() {
		Logger iLogger = InfoLoggerClass.logger;
		List<Integer> response;
		
		
		//Implementation method here
		response = new MaAccountTenancyIdListServiceImpl().getMaAccountTenancyList();
		System.out.println("Resp is "+response);
		return response;
		
	}
}
